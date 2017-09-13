package com.eyalpano.app.Driver;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eyalpano.app.R;
import com.eyalpano.app.ServerHandlers.ServiceHandler;
import com.eyalpano.app.UserData.User;
import com.eyalpano.app.UserData.UserLocalStore;
import com.eyalpano.app.UserData.Common;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Change_Password extends ActionBarActivity {
    UserLocalStore userLocalStore;
    EditText Opass,Npass,CNpass;
    String pass,Npasstr;

    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_change__password);
        userLocalStore  = new UserLocalStore(this);
        Opass = (EditText) findViewById(R.id.Opass);
        Npass = (EditText) findViewById(R.id.Npass);
        CNpass = (EditText) findViewById(R.id.Cpass);
        pass = userLocalStore.getLoggedInUser().pass;
    }
    public void onSaveNewPassClicked(View v){
        if(Opass.getText().toString().equals(pass)){
            if(Npass.getText().toString().equals(CNpass.getText().toString())){
                Npasstr = Npass.getText().toString();
                new UpdatePass().execute();
            }
            else {
                Toast.makeText(this,"New Passowds Not Match" , Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this,"Old Password Not Match" , Toast.LENGTH_SHORT).show();
        }
    }

    public   class UpdatePass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Change_Password.this);
            pDialog.setMessage("Updating Pass");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("idUsers", ""+userLocalStore.getLoggedInUser().idUsers));
            dataToSend.add(new BasicNameValuePair("pass",Npasstr));
            dataToSend.add(new BasicNameValuePair("action", "3"));

            String json = jsonParser.makeServiceCall(Common.SERVER_ADDRESS+"Users.php", ServiceHandler.POST,dataToSend);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray response = jsonObj.getJSONArray("data");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                fetch();
            }
        }
    }

    private void fetch() {
        Toast.makeText(this,"Updated" , Toast.LENGTH_SHORT).show();
        User temp = userLocalStore.getLoggedInUser();
        temp.pass = Npasstr;
        userLocalStore.clearUserData();
        userLocalStore.storeUserData(temp);
        userLocalStore.setUserLoggedIn(true);
        finish();
    }
}
