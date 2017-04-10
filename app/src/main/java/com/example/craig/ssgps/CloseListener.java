package com.example.craig.ssgps;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by Mikkel on 09/04/2017.
 */

public interface CloseListener {
    public void deRegisterListener();

    void onConnected(Bundle bundle);

    void onConnectionFailed(ConnectionResult connectionResult);

    void onConnectionSuspended(int i);
}

