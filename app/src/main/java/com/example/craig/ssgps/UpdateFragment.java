/*
Updates the user's emergency contact information when the update button is pressed.
 */
package com.example.craig.ssgps;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class UpdateFragment extends DialogFragment{
    private SingleItem targetItem;
    private String editPrio;
    private ContactDBHelper updateContact;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_update,null);
        ((EditText)v.findViewById(R.id.name_text)).setText(targetItem.getName());
        ((EditText)v.findViewById(R.id.phone_number)).setText(String.valueOf(targetItem.getNumber()));

        updateContact = new ContactDBHelper(getActivity());

        final Spinner m_spinner = (Spinner) v.findViewById(R.id.priority_Spinner);
        m_spinner.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.prio_int)));

        m_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editPrio = ((TextView)view).getText().toString();
//                Toast.makeText(SettingsActivity.this,((TextView)view).getText().toString()+"",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ((Button)v.findViewById(R.id.save_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText)v.findViewById(R.id.name_text)).getText().toString();
                String phone = ((EditText)v.findViewById(R.id.phone_number)).getText().toString();
                int id_N = targetItem.getId();
                String id = String.valueOf(id_N);

                updateDatabase(id,name,phone,editPrio);

                UpdateFragment.this.dismiss();
            }
        });
        return v;

    }

    private void updateDatabase(String id, String name, String phone, String priority) {
        boolean isInserted = updateContact.updateData(id,phone, name, editPrio);
        if(isInserted == true) {
            Toast.makeText(getActivity(), "Contact Updated", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Contact not Updated", Toast.LENGTH_LONG).show();
        }
        Intent i;
//        finish();
//        Toast.makeText(getActivity(), "I am updating "+targetItem.getName()+" with id of "+targetItem.getId()+" to values "+name+" "+phone+" "+priority, Toast.LENGTH_SHORT).show();

    }

    public void setTargetItem(SingleItem targetItem) {
        this.targetItem = targetItem;
    }
}
