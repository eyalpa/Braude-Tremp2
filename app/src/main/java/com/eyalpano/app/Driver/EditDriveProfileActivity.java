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
import com.eyalpano.app.UserData.Common;
import com.eyalpano.app.UserData.User;
import com.eyalpano.app.UserData.UserLocalStore;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class EditDriveProfileActivity extends ActionBarActivity {
    UserLocalStore userLocalStore;
    EditText name,phone,waze,comments;

    ProgressDialog pDialog;
    String tfullname;
    String twaze;
    String tcomments ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_edit_drive_profile);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);

        phone.setKeyListener(null);
        phone.setCursorVisible(false);
        phone.setPressed(false);
        phone.setFocusable(false);
        waze = (EditText) findViewById(R.id.waze);
        comments = (EditText) findViewById(R.id.Comments);
        userLocalStore = new UserLocalStore(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        name.setText(userLocalStore.getLoggedInUser().fullname);
        phone.setText(userLocalStore.getLoggedInUser().telephone);
        waze.setText(userLocalStore.getLoggedInUser().waze);
        comments.setText(userLocalStore.getLoggedInUser().comments);
    }

    public void onSaveNewProfileClicked(View v){
         tfullname = name.getText().toString();
         twaze = waze.getText().toString();
         tcomments = comments.getText().toString();
        new Update().execute();

    }

    class Update extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(EditDriveProfileActivity.this);
                pDialog.setMessage("Updating Profile...");
                pDialog.setCancelable(false);
                pDialog.show();
            }
            @Override
            protected Void doInBackground(Void... arg0) {
                ServiceHandler jsonParser = new ServiceHandler();
                ArrayList<NameValuePair> dataToSend = new ArrayList<>();
                int tidUsers = userLocalStore.getLoggedInUser().idUsers;
                String tpass = userLocalStore.getLoggedInUser().pass;
                String ttel = userLocalStore.getLoggedInUser().telephone;
                dataToSend.add(new BasicNameValuePair("idUsers",tidUsers+""));
                dataToSend.add(new BasicNameValuePair("fullname",tfullname ));
                dataToSend.add(new BasicNameValuePair("waze", twaze));
                dataToSend.add(new BasicNameValuePair("comments", tcomments));
                dataToSend.add(new BasicNameValuePair("action", "0"));
                User usr = new User(tidUsers, ttel, tpass, tfullname, twaze, tcomments);
                userLocalStore.clearUserData();
                userLocalStore.storeUserData(usr);
                userLocalStore.setUserLoggedIn(true);
                try {
                    String json = jsonParser.makeServiceCall(Common.SERVER_ADDRESS+"Users.php", ServiceHandler.POST,dataToSend);
                    Log.d("MyTagGoesHere",json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return null;
            }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.setMessage("Finish");
            if (pDialog.isShowing())
            {
                pDialog.dismiss();
                fetch();
            }

        }
    }

    private void fetch() {
        String str = "updated";
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

}
