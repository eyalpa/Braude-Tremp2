package com.eyalpano.app.Driver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.eyalpano.app.GetUserCallback;
import com.eyalpano.app.R;
import com.eyalpano.app.ServerHandlers.ServerRequests;
import com.eyalpano.app.UserData.User;
import com.eyalpano.app.UserData.UserLocalStore;


public class DriverSignUpActivity extends Activity {

    UserLocalStore userLocalStore;

    EditText name,phone,password,waze,comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_up);

        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);
        waze = (EditText) findViewById(R.id.waze);
        comments = (EditText) findViewById(R.id.Comments);
    }

    public void onRegisterClick(View v ){
        String name = this.name.getText().toString();
        String phone = this.phone.getText().toString();
        String password = this.password.getText().toString();
        String waze = this.waze.getText().toString();
        String comments = this.comments.getText().toString();

        User user = new User(0,phone,  password,  name, waze, comments);
        registerUser(user);
    }

    private void registerUser(User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                Intent loginIntent = new Intent(DriverSignUpActivity.this, DriversMainActivity.class);
                startActivity(loginIntent);
            }
        });
    }


    private void logUserIn(User user) {
        UserLocalStore.storeUserData(user);
    }

    private void showErrorMassage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DriverSignUpActivity.this);
        dialogBuilder.setMessage("User in the system");
        dialogBuilder.setPositiveButton("Ok",null);
        dialogBuilder.show();
    }

}
