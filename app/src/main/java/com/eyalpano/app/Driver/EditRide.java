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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditRide extends Activity implements View.OnClickListener {

    private EditText fromDateEtxt;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatterdmy;
    private SimpleDateFormat dateFormatterymd;

    String idRide,date,time,waze,fxDate;
    private Spinner fromHourSpn;
    private Spinner fromWazeSpn;
    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ride);
        idRide = getIntent().getStringExtra("idRide");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        waze = getIntent().getStringExtra("waze");

        dateFormatterdmy = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        dateFormatterymd = new SimpleDateFormat("yyyyMMdd", Locale.US);

        //waze spinner
        java.util.ArrayList<String> wazeAL = new java.util.ArrayList<>();
        for(int i = 0 ; i< Common.WazeArray.length; i++){
            wazeAL.add(Common.WazeArray[i]);
        }
        fromWazeSpn = (Spinner) findViewById(R.id.EditRide_wazespinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, wazeAL);
        fromWazeSpn.setAdapter(adapter);
        fromWazeSpn.setSelection(getIndex(fromWazeSpn, time));

        //hour spinner
        java.util.ArrayList<String> hour = new java.util.ArrayList<>();
        for(int i = 7 ; i<= 22; i++){
            hour.add("" + i);
        }
        fromHourSpn = (Spinner) findViewById(R.id.EditRide_hourspinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, hour);
        fromHourSpn.setAdapter(adapter1);
        fromHourSpn.setVerticalScrollbarPosition(Integer.parseInt(time) - 7);
        fromHourSpn.setSelection(getIndex(fromHourSpn, time));

        updateFxdate(date);
        fromDateEtxt = (EditText) findViewById(R.id.EditRide_etxt_fromdate);
        fromDateEtxt.setText(fxDate);
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
    @Override
    public void onClick(View view) {
        if(view == fromDateEtxt) {
            fromDatePickerDialog.show();
        }
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

    public void onCancelClick(View view) {
        close();
    }

    public void onSaveRideClick(View view) {
        time = fromHourSpn.getSelectedItem().toString();
        int fromip = fromWazeSpn.getSelectedItemPosition() + 1;
        waze = ""+fromip;
        new updateRide().execute();
    }

    public void onDeleteRideClick(View view) {
        new DeleteeRide().execute();
    }

    public   class updateRide extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditRide.this);
            pDialog.setMessage("Updating Rides...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("idRide", idRide));
            dataToSend.add(new BasicNameValuePair("date", date));
            dataToSend.add(new BasicNameValuePair("time", time));
            dataToSend.add(new BasicNameValuePair("waze", waze));
            dataToSend.add(new BasicNameValuePair("action", "0"));

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

    public   class DeleteeRide extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditRide.this);
            pDialog.setMessage("Delete Ride...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("idRide", idRide));
            dataToSend.add(new BasicNameValuePair("action", "-1"));

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
