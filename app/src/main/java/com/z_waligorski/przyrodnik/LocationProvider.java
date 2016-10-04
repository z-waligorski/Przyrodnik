package com.z_waligorski.przyrodnik;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class LocationProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final long REFRESHING_INTERVAL = 10000;
    private static final long FASTEST_REFRESHING_INTERVAL = 5000;

    public abstract interface LocationCallback{
        public void newLocation(Location location);
    }

    private Context context;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private GoogleApiClient client;

    public LocationProvider (Context context, LocationCallback callback) {
        locationCallback = callback;
        this.context = context;

        client = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // LocationRequest object establishes quality of location data that will be provided by FusedLocationApi
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(REFRESHING_INTERVAL)
                .setFastestInterval(FASTEST_REFRESHING_INTERVAL);
    }

    public void connect() {
        client.connect();
    }

    public void disconnect() {
        if(client.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            client.disconnect();
        }
    }

    // Called when client is connected to service
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(client);
        if(location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        } else {
            locationCallback.newLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    // Executed when new location is available
    @Override
    public void onLocationChanged(Location location) {
        locationCallback.newLocation(location);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(connectionResult.hasResolution() && context instanceof Activity) {
            try{
                Activity activity = (Activity) context;
                connectionResult.startResolutionForResult(activity, 9000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }
}
