package com.example.craig.ssgps;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Help extends Fragment {


    public Help() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_help, container, false); 
        ((Button)v.findViewById(R.id.help_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });
        return v;
    }

    private void sendSMS() {
        Toast.makeText(getContext(), "Should sednd sms herree", Toast.LENGTH_SHORT).show();
    }

}
