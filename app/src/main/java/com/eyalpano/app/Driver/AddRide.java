package com.eyalpano.app.Driver;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.eyalpano.app.R;

import com.eyalpano.app.ServerHandlers.ServiceHandler;
import com.eyalpano.app.UserData.Common;
import com.eyalpano.app.UserData.UserLocalStore;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddRide extends Activity implements View.OnClickListener {

    private EditText fromDateEtxt;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatterdmy;
    private SimpleDateFormat dateFormatterymd;
    UserLocalStore userLocalStore;

    String idRide,date,time,waze,fxDate;
    private Spinner fromHourSpn;
    private Spinner fromWazeSpn;
    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride);

        userLocalStore = new UserLocalStore(this);

        dateFormatterdmy = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        dateFormatterymd = new SimpleDateFormat("yyyyMMdd", Locale.US);

        //waze spinner
        java.util.ArrayList<String> wazeAL = new java.util.ArrayList<>();
        for(int i = 0 ; i< Common.WazeArray.length; i++){
            wazeAL.add(Common.WazeArray[i]);
        }
        fromWazeSpn = (Spinner) findViewById(R.id.AddRide_wazespinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, wazeAL);
        fromWazeSpn.setAdapter(adapter);

        //hour spinner
        java.util.ArrayList<String> hour = new java.util.ArrayList<>();
        for(int i = 7 ; i<= 22; i++){
            hour.add("" + i+":00");
        }
        fromHourSpn = (Spinner) findViewById(R.id.AddRide_hourspinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, hour);
        fromHourSpn.setAdapter(adapter1);

        fromDateEtxt = (EditText) findViewById(R.id.AddRide_etxt_fromdate);
        fromDateEtxt.setInputType(InputType.TYPE_NULL);
        fromDateEtxt.requestFocus();
        setDateTimeField();
    }

    private void updateFxdate(String date1) {
        fxDate=date1.toString().substring(6,8)+ date1.toString().substring(4,6)+ date1.toString().substring(0,4);
    }

    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        fromDateEtxt.setText(dateFormatterdmy.format(newDate.getTime()));
                        date = dateFormatterymd.format(newDate.getTime());
                        updateFxdate(date);
                    }
                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.AddRide_etxt_fromdate:
                if(v == fromDateEtxt) {
                    fromDatePickerDialog.show();
                }
                break;
            case R.id.AddRide_close:
                close();
                break;
            case R.id.AddRide_addRide:
                if(fromDateEtxt != null) {
                    time = fromHourSpn.getSelectedItem().toString();
                    int fromip = fromWazeSpn.getSelectedItemPosition() + 1;
                    waze = "" + fromip;
                    new AddRideDB().execute();
                }
                break;
        }
    }


    public   class AddRideDB extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddRide.this);
            pDialog.setMessage("Add Ride...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();


            dataToSend.add(new BasicNameValuePair("idUsers",""+userLocalStore.getLoggedInUser().idUsers));
            dataToSend.add(new BasicNameValuePair("date", date));
            dataToSend.add(new BasicNameValuePair("time", time));
            dataToSend.add(new BasicNameValuePair("waze", waze));
            dataToSend.add(new BasicNameValuePair("action", "1"));

            String json = jsonParser.makeServiceCall(Common.SERVER_ADDRESS+"Ride.php", ServiceHandler.POST,dataToSend);

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
                close();
            }
        }
    }


    private void close() {
        setResult(RESULT_OK,this.getIntent());
        finish();
    }

}
