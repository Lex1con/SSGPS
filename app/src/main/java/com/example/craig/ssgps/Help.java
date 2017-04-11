package com.example.craig.ssgps;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Help extends Fragment {
    Globals g = Globals.getInstance();

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
                sendSMS(v);
            }
        });
        return v;
    }

    private void sendSMS(View v) {
        g.setDanger(true);
        Toast.makeText(getContext(), "Attempting to call sendSMS", Toast.LENGTH_SHORT).show();
        Log.d("HelpActivity","Attempting to call sendSMS");
        Bundle bundle = new Bundle();

        Intent i = new Intent(v.getContext(), SendSMSActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }

}
