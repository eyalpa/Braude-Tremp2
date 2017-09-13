package com.eyalpano.app.Driver;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.eyalpano.app.R;
import com.eyalpano.app.ServerHandlers.ServiceHandler;
import com.eyalpano.app.UserData.Common;
import com.eyalpano.app.UserData.UserLocalStore;
import com.eyalpano.app.swipeactionadapter.SwipeActionAdapter;
import com.eyalpano.app.swipeactionadapter.SwipeDirection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class List_rides extends ListActivity implements SwipeActionAdapter.SwipeActionListener {
        protected SwipeActionAdapter mAdapter;
        protected String[] content;
        ArrayAdapter<String> stringAdapter;
        private ArrayList<String> idRide;
        private ArrayList<String> date;
        private ArrayList<String> time;
        private ArrayList<String> waze;
        private ArrayList<String> full;
        private int pos;
        private String id;
        ProgressDialog pDialog;
        UserLocalStore userLocalStore;

        final Context context = this;
        TextView topDate;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_list_rides);
            userLocalStore = new UserLocalStore(this);
            new GetRes().execute();
        }


        public void fetch(){
            //setContentView(R.layout.activity_search_res);
            stringAdapter = new ArrayAdapter<>(this, R.layout.row_bg, R.id.text,full);

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
            Intent EditRideIntent = new Intent(".EditRide");
            EditRideIntent.putExtra("idRide", idRide.get(position));
            EditRideIntent.putExtra("date", date.get(position));
            EditRideIntent.putExtra("time",time.get(position) );
            EditRideIntent.putExtra("waze", waze.get(position));

            startActivityForResult(EditRideIntent, 12);
            //startActivity(EditRideIntent);
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            new GetRes().execute();
    }
    public void onAddRideActivityClick(View v){
        //if(userconnect!=null)
        Intent AddRideIntent = new Intent(".AddRide");
        startActivityForResult(AddRideIntent, 12);
        //change button
    }

    @Override
    public boolean hasActions(int position, SwipeDirection direction) {
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
                    case DIRECTION_NORMAL_LEFT:
                        break;
                    case DIRECTION_FAR_RIGHT:
                    case DIRECTION_NORMAL_RIGHT:
                        pos = position;
                        id = idRide.get(position);
                       idRide.remove(position);
                        date.remove(position);
                         time.remove(position);
                         waze.remove(position);
                         full.remove(position);
                        new DeleteeRide().execute();
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
                pDialog = new ProgressDialog(List_rides.this);
                pDialog.setMessage("Fetching Rides...");
                pDialog.setCancelable(false);
                pDialog.show();
            }
            @Override
            protected Void doInBackground(Void... arg0) {
                ServiceHandler jsonParser = new ServiceHandler();
                ArrayList<NameValuePair> dataToSend = new ArrayList<>();
                dataToSend.add(new BasicNameValuePair("idUsers", "" + userLocalStore.getLoggedInUser().idUsers));

                Date SendDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // Set your date format
                String currentData = sdf.format(SendDate);
                dataToSend.add(new BasicNameValuePair("date",currentData));
                dataToSend.add(new BasicNameValuePair("action","3"));

                String json = jsonParser.makeServiceCall(Common.SERVER_ADDRESS+"Ride.php", ServiceHandler.POST,dataToSend);
                Log.e("Response: ", "> " + json);

                if (json != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(json);
                        if (jsonObj != null) {
                            JSONArray response = jsonObj.getJSONArray("data");
                            idRide = new ArrayList<String>();
                            date = new ArrayList<String>();
                            time = new ArrayList<String>();
                            waze = new ArrayList<String>();
                            full = new ArrayList<String>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject catObj = (JSONObject) response.get(i);
                                idRide.add(catObj.getString("idRide"));
                                date.add(catObj.getString("date"));
                                time.add(catObj.getString("time"));
                                waze.add(catObj.getString("waze"));
                                String day = catObj.getString("date");
                                String Datetxt = day.toString().substring(6,8)+"-"+ day.toString().substring(4,6)+"-"+ day.toString().substring(0,4);
                                full.add(Datetxt + "   \t   " + catObj.getString("time") + "    \t   " + Common.WazeArray[Integer.parseInt(catObj.getString("waze"))-1] );
                            }
                            //fetch();
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
    public   class DeleteeRide extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(List_rides.this);
            pDialog.setMessage("Delete Ride...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("idRide", id));
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
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    }


