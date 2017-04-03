package com.example.mikkel.ssgps;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by M.hayes on 4/13/2016.
 */
public interface CloseListener {
    public void deRegisterListener();

    void onConnected(Bundle bundle);

    void onConnectionFailed(ConnectionResult connectionResult);

    void onConnectionSuspended(int i);
}
