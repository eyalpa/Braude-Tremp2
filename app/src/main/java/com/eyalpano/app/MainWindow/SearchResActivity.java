package com.eyalpano.app.MainWindow;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eyalpano.app.R;
import com.eyalpano.app.ServerHandlers.ServiceHandler;
import com.eyalpano.app.UserData.Common;
import com.eyalpano.app.swipeactionadapter.SwipeActionAdapter;
import com.eyalpano.app.swipeactionadapter.SwipeDirection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchResActivity extends ListActivity implements SwipeActionAdapter.SwipeActionListener {
    protected SwipeActionAdapter mAdapter;
    protected String[] content;
    ArrayAdapter<String> stringAdapter;
    static String day;
    static String time;
    static String waze;
    private ArrayList<String> ResArray;
    private String[] telArray;
    private String[] MoreArray;
    ProgressDialog pDialog;
    final Context context = this;
    TextView topDate;
    public static View searchWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_res);
        topDate = (TextView) findViewById(R.id.editDate);
        day = getIntent().getStringExtra("WheelDate");
        time = getIntent().getStringExtra("WheelHour");
        waze = getIntent().getStringExtra("WheelWaze");
        searchWindow = (View) findViewById(R.id.searchWindow);
        if(Integer.parseInt(waze)%2==0){
            searchWindow.setBackgroundResource(R.drawable.background_dark);
        }
        else{
            searchWindow.setBackgroundResource(R.drawable.background_light);
        }
        topDate.setText("\t\tSearch Day: " + day.toString().substring(6,8)+"-"+ day.toString().substring(4,6)+"-"+ day.toString().substring(0,4) );
        //fetch();
        new GetRes().execute();
    }


    public void fetch(){

        stringAdapter = new ArrayAdapter<>(this, R.layout.row_bg, R.id.text, ResArray);

        mAdapter = new SwipeActionAdapter(stringAdapter);
        mAdapter.setSwipeActionListener(this)
                .setDimBackgrounds(true)
                .setListView(getListView());
        setListAdapter(mAdapter);

        mAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.row_bg_right_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, final int position, long id){
    // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.user_ride_dialog);
        dialog.setTitle(ResArray.get(position));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(MoreArray[position]+"\n"+telArray[position]);

        Button CloseButton = (Button) dialog.findViewById(R.id.dialogButtonClose);
        // if button is clicked, close the custom dialog
        CloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button CallButton = (Button) dialog.findViewById(R.id.dialogButtonCall);
        // if button is clicked, close the custom dialog
        CallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = telArray[position];
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + tel));
                startActivity(callIntent);
            }
        });
        Button SMSButton = (Button) dialog.findViewById(R.id.dialogButtonSMS);
        // if button is clicked, close the custom dialog
        SMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = telArray[position];
                String message = "can i take tremp with you ?";
                Uri sms_uri = Uri.parse("smsto:" + phoneNo);
                Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                sms_intent.putExtra("sms_body", message);
                startActivity(sms_intent);
            }
        });
        Button WAButton = (Button) dialog.findViewById(R.id.dialogButtonWA);
        // if button is clicked, close the custom dialog
        WAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = "+972" +telArray[position].substring(1);
                String message = "can i take tremp with you ?";
                Uri uri = Uri.parse("smsto:" + phoneNo);
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.putExtra("sms_body", message);
                i.setPackage("com.whatsapp");
                startActivity(i);
            }
        });


        dialog.show();
    }

    @Override
    public boolean hasActions(int position, SwipeDirection direction){
        if(direction.isLeft()) return true;
        if(direction.isRight()) return true;
        return false;
    }

    @Override
    public boolean shouldDismiss(int position, SwipeDirection direction){
        return direction == SwipeDirection.DIRECTION_NORMAL_LEFT;
    }

    @Override
    public void onSwipe(int[] positionList, SwipeDirection[] directionList){
        for(int i=0;i<positionList.length;i++) {
            SwipeDirection direction = directionList[i];
            int position = positionList[i];
            String dir = "";

            switch (direction) {
                case DIRECTION_FAR_LEFT:
                    //dir = "Far left";
                case DIRECTION_NORMAL_LEFT:
                    //dir = "Left";
                    Toast.makeText(this, " SMS in Action " , Toast.LENGTH_SHORT).show();
                    String phoneNo = telArray[position];
                    String message = "can i take tremp with you ?";
                    Uri sms_uri = Uri.parse("smsto:" + phoneNo);
                    Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                    sms_intent.putExtra("sms_body", message);
                    startActivity(sms_intent);
                    break;
                case DIRECTION_FAR_RIGHT:
                    //dir = "Far right";
                case DIRECTION_NORMAL_RIGHT:
                    Toast.makeText(this, " Call in Action " , Toast.LENGTH_SHORT).show();
                    String tel = telArray[position];
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + tel));
                    startActivity(callIntent);
                    //dir = "Far right";
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("Test Dialog").setMessage("You swiped right").create().show();
//                    dir = "Right";
                    break;
            }
           // Toast.makeText(this, dir + " swipe Action triggered on " + mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            mAdapter.notifyDataSetChanged();
        }
    }




    public   class GetRes extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SearchResActivity.this);
            pDialog.setMessage("Fetching Rides...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("date", day));
            if(!(time.equalsIgnoreCase("6"))) dataToSend.add(new BasicNameValuePair("time", time));
            dataToSend.add(new BasicNameValuePair("waze", waze));
            dataToSend.add(new BasicNameValuePair("action", "2"));

            String json = jsonParser.makeServiceCall(Common.SERVER_ADDRESS+"Ride.php", ServiceHandler.POST,dataToSend);
            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray response = jsonObj.getJSONArray("data");
                        MoreArray = new String[response.length()];
                        telArray = new String[response.length()];
                        ResArray = new ArrayList<String>();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject catObj = (JSONObject) response.get(i);
                            String str = "" + catObj.getString("Rtime")+":00";
                            str += "\t " +catObj.getString("Ufullname");
                            String str1 = " Path : " +catObj.getString("Uwaze");
                            str1 += "\n Comments: " +catObj.getString("Ucomments");
                            String tel = catObj.getString("Utelephone");
                            telArray[i] = tel;
                            ResArray.add(str);
                            MoreArray[i] = str1;
                        }
                        //fetch();
                    }else{
                        telArray = new String[1];
                        ResArray = new ArrayList<>();
                        telArray[0] = " ";
                    }

                } catch (JSONException e) {
                    ResArray = new ArrayList<>();
                    telArray = new String[1];
                    telArray[0] = " ";
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


}
