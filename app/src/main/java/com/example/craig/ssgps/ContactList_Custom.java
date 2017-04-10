package com.example.craig.ssgps;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

public class ContactList_Custom extends BaseAdapter implements ListAdapter{
    private final Activity activity;
    private ArrayList<SingleItem> list = new ArrayList<>();
    private Context context;
    private String i;
    ContactDBHelper contactsDB;

    public ContactList_Custom(ArrayList<SingleItem> list, Context context, Activity activity) {
        this.list = list;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
//        return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_list, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position).getName());

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_contact);
        Button updateBtn = (Button)view.findViewById(R.id.upate_contact);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                removeFromDb(position);
                list.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateContact(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    private void updateContact(int position) {
        SingleItem item = (SingleItem) getItem(position);
        UpdateFragment updateFragment = new UpdateFragment();
        updateFragment.setTargetItem(item);
        updateFragment.show(activity.getFragmentManager(),"TAG");

    }

    private void removeFromDb(int position) {
        SingleItem item = (SingleItem) getItem(position);
        new ContactDBHelper(context).deleteData(item);
    }
}
