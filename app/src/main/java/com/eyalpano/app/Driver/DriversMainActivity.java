package com.eyalpano.app.Driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.eyalpano.app.MainWindow.MainActivity;
import com.eyalpano.app.R;
import com.eyalpano.app.UserData.UserLocalStore;

public class DriversMainActivity extends ActionBarActivity {

    UserLocalStore userLocalStore;
    TextView wellcom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_drivers_main);
        userLocalStore = new UserLocalStore(this);
        wellcom = (TextView) findViewById(R.id.DriverMain_Wellcom);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate() ==false){
            startActivity(new Intent(".DriverLoginActivity"));
        }
        else wellcom.setText("Wellcom " + userLocalStore.getLoggedInUser().fullname);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        finish();
        super.onPause();
    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }


    public void onLogOutClick(View v){
        userLocalStore.clearUserData();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    public void onEditRideActivitylClick(View v){
        //if(userconnect!=null)
        startActivity(new Intent(".List_rides"));
        //change button
    }

    public void onEditDriverProfileActivityClick(View v){
        //if(userconnect!=null)
        startActivity(new Intent(".EditDriveProfileActivity"));
        //change button
    }
    public void onChangePasswordActivityClick(View v){
        //if(userconnect!=null)
        startActivity(new Intent(".Change_Password"));
        //change button
    }
}
