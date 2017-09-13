package com.eyalpano.app.Driver;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import com.eyalpano.app.GetUserCallback;
import com.eyalpano.app.R;
import com.eyalpano.app.ServerHandlers.ServerRequests;
import com.eyalpano.app.UserData.User;
import com.eyalpano.app.UserData.UserLocalStore;

/**
 * A login screen that offers login via email/password.
 */
public class DriverLoginActivity extends ActionBarActivity {

    // UI references.
    private EditText mPhone;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    UserLocalStore userLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_driver_login);
        // Set up the login form.
        mPhone = (EditText) findViewById(R.id.tel);
        mPasswordView = (EditText) findViewById(R.id.password);
        userLocalStore = new UserLocalStore(this);

    }

    public void onClickLogin(View v){
        String telephone = mPhone.getText().toString();
        String pass = mPasswordView.getText().toString();
        User user = new User(telephone,pass,null);
        authenticate(user);
    }

    private void authenticate(User user) {

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMassage("Error telephone/password");
                } else {
                    userLocalStore.storeUserData(returnedUser);
                    userLocalStore.setUserLoggedIn(true);
                    startActivity(new Intent(".DriversMainActivity"));
                }
            }
        });
    }
    private void logUserIn(User user) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.checkUserInDBAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMassage("IsInDB");
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void showErrorMassage(String str){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DriverLoginActivity.this);
        dialogBuilder.setMessage(str);
        dialogBuilder.setPositiveButton("Ok",null);
        dialogBuilder.show();
    }

    public void onRegisterClick(View v){
        startActivity(new Intent(".DriverSignUpActivity"));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


}

