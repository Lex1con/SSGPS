package com.example.craig.ssgps;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class UpdateFragment extends DialogFragment{
    private SingleItem targetItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_update,null);
        ((EditText)v.findViewById(R.id.name_text)).setText(targetItem.getName());
        ((EditText)v.findViewById(R.id.phone_number)).setText(String.valueOf(targetItem.getNumber()));
        ((EditText)v.findViewById(R.id.priority_num)).setText(String.valueOf(targetItem.getPriority()));

        ((Button)v.findViewById(R.id.save_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText)v.findViewById(R.id.name_text)).getText().toString();
                String phone = ((EditText)v.findViewById(R.id.phone_number)).getText().toString();
                String priority = ((EditText)v.findViewById(R.id.priority_num)).getText().toString();

                updateDatabase(name,phone,priority);

                UpdateFragment.this.dismiss();
            }
        });
        return v;

    }

    private void updateDatabase(String name, String phone, String priority) {
        Toast.makeText(getActivity(), "I am updating "+targetItem.getName()+" with id of "+targetItem.getId()+" to values "+name+" "+phone+" "+priority, Toast.LENGTH_SHORT).show();

    }

    public void setTargetItem(SingleItem targetItem) {
        this.targetItem = targetItem;
    }
}
