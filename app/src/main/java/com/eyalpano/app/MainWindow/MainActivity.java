package com.eyalpano.app.MainWindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.eyalpano.app.R;
import com.eyalpano.app.UserData.Common;
import com.eyalpano.app.UserData.UserLocalStore;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kankan.wheel.widget.DayWheelAdapter;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

public  class MainActivity extends Activity{
    public static WheelView Wheelday;
    public static WheelView Wheelhour;
    public static WheelView Wheelwaze;
    public static ArrayList<Date> days;

    public static int Go_Back = 0;
    public static Button go;
    public static Button back;
    public static Button settings;
    public static Button search;
    public static View mainWindow;
    UserLocalStore userLocalStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Array for the am/pm marker column
        mainWindow = (View) findViewById(R.id.mainWindow);
        go = (Button) findViewById(R.id.go);
        back = (Button) findViewById(R.id.back);
        settings = (Button) findViewById(R.id.settings);
        search = (Button) findViewById(R.id.search);


        //With a custom method I get the next following 10 days from now
       //ArrayList<Date> days = DateUtils.getNextNumberOfDays(new Date(), 10);
        days = new ArrayList<Date>();
        Date Today = new Date();
        days.add(Today);
        for(int i = 1 ; i < 10 ; i++){
            days.add(new Date());
        }
        for(int i = 1 ; i < 10 ; i++){
            days.get(i).setTime(days.get(i).getTime() + i*1000*60*60*24);
        }

        //Configure Days Column
        Wheelday = (WheelView) findViewById(R.id.day);
        Wheelday.setViewAdapter(new DayWheelAdapter(this, days));

        //Configure Hours Column
        Wheelhour = (WheelView) findViewById(R.id.hour);
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(this, 6, 22,"%02d:00");
        hourAdapter.setItemResource(R.layout.wheel_item_time);
        hourAdapter.setItemTextResource(R.id.time_item);
        Wheelhour.setViewAdapter(hourAdapter);

        Wheelwaze = (WheelView) findViewById(R.id.ampm);
        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(this, Common.WazeArray_city);
        ampmAdapter.setItemResource(R.layout.wheel_item_time);
        ampmAdapter.setItemTextResource(R.id.time_item);
        Wheelwaze.setViewAdapter(ampmAdapter);

    }

    // Wheel scrolled flag
    private boolean wheelScrolled = false;

    // Wheel scrolled listener
    OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
            // Do something here
        }
        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            // Do something here
        }
    };

    // Wheel changed listener
    private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (!wheelScrolled) {
                // Do something here
            }
        }
    };

    /**
     * Initializes wheel
     * @param id the wheel widget Id
     */
    private void initWheel(int id) {
        WheelView wheel = getWheel(id);
        wheel.setViewAdapter(new SlotMachineAdapter(this));
        wheel.setCurrentItem((int)(Math.random() * 10));

        wheel.addChangingListener(changedListener);
        wheel.addScrollingListener(scrolledListener);
        wheel.setCyclic(true);
        wheel.setEnabled(true);
        wheel.setVertical(false);
        wheel.setInterpolator(new AnticipateOvershootInterpolator());
    }

    /**
     * Returns wheel by Id
     * @param id the wheel Id
     * @return the wheel with passed Id
     */
    private WheelView getWheel(int id) {
        return (WheelView) findViewById(id);
    }

    /**
     * Mixes wheel
     * @param id the wheel id
     */
    private void mixWheel(int id) {
        WheelView wheel = getWheel(id);
        wheel.scroll(-350 + (int) (Math.random() * 50), 2000);
    }


    /**
     * Slot machine adapter
     */
    private class SlotMachineAdapter extends AbstractWheelAdapter {
        // Image size
        final int IMAGE_WIDTH = 230;
        final int IMAGE_HEIGHT = 230;

        // Slot machine symbols
        private final int items[] = new int[] {
                android.R.drawable.star_big_on,
                android.R.drawable.stat_sys_warning,
                android.R.drawable.radiobutton_on_background,
                android.R.drawable.ic_delete
        };

        // Cached images
        private List<SoftReference<Bitmap>> images;

        // Layout inflater
        private Context context;

        /**
         * Constructor
         */
        public SlotMachineAdapter(Context context) {
            this.context = context;
            images = new ArrayList<SoftReference<Bitmap>>(items.length);
            for (int id : items) {
                images.add(new SoftReference<Bitmap>(loadImage(id)));
            }
        }

        /**
         * Loads image from resources
         */
        private Bitmap loadImage(int id) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
            bitmap.recycle();
            return scaled;
        }

        @Override
        public int getItemsCount() {
            return items.length;
        }

        // Layout params for image view
        final LayoutParams params = new LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            ImageView img;
            if (cachedView != null) {
                img = (ImageView) cachedView;
            } else {
                img = new ImageView(context);
            }
            img.setLayoutParams(params);
            SoftReference<Bitmap> bitmapRef = images.get(index);
            Bitmap bitmap = bitmapRef.get();
            if (bitmap == null) {
                bitmap = loadImage(items[index]);
                images.set(index, new SoftReference<Bitmap>(bitmap));
            }
            img.setImageBitmap(bitmap);

            return img;
        }
    }
    public Date addDays(Date d, int days)
    {
        long secondsToAdd = days;
        secondsToAdd *= (1000*60*60*24);
        d.setTime(d.getTime() + secondsToAdd);
        return d;
    }

    public void onNowClicked(View v){
        Date Now = new Date();

        if(Now.getHours()>7 || Now.getHours()<22){
            Wheelhour.setCurrentItem(Now.getHours() - 7, true);
        }
        Wheelday.setCurrentItem(0, true);
        //To-Do --> set location
    }
//    public void onSearchDayClicked(View v){
//        Intent SearchIntent = new Intent(".SearchResActivity");
//        Date SendDate = days.get(Wheelday.getCurrentItem());
//        int hour = Wheelhour.getCurrentItem() - 7;
//        int waze = Wheelwaze.getCurrentItem();
//        SearchIntent.putExtra("isSearchClicked",true);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // Set your date format
//        String currentData = sdf.format(SendDate);
//        SearchIntent.putExtra("WheelDate",currentData);
//        SearchIntent.putExtra("WheelHour","0");
//        SearchIntent.putExtra("WheelWaze",""+waze+1);
//        startActivity(SearchIntent);
//    }
    public void onMainClick(View v) {
        switch (v.getId()){
            case R.id.back:
                if (Go_Back == 0){
                    Go_Back = 1;
                    go.setBackgroundResource(R.drawable.go_dark);
                    back.setBackgroundResource(R.drawable.back_dark);
                    mainWindow.setBackgroundResource(R.drawable.background_dark);
                    settings.setBackgroundResource(R.drawable.settings_dark);
                    search.setBackgroundResource(R.drawable.search_dark);
                }
                break;
            case R.id.go:
                if (Go_Back == 1){
                    Go_Back = 0;
                    go.setBackgroundResource(R.drawable.go_light);
                    back.setBackgroundResource(R.drawable.back_light);
                    mainWindow.setBackgroundResource(R.drawable.background_light);
                    settings.setBackgroundResource(R.drawable.settings_light);
                    search.setBackgroundResource(R.drawable.search_light);
                }
                break;
            case R.id.search:
                Intent SearchIntent = new Intent(".SearchResActivity");
                Date SendDate = days.get(Wheelday.getCurrentItem());
                //int hour = Wheelhour.getCurrentItem() - 7;
                int waze = Wheelwaze.getCurrentItem();
                SearchIntent.putExtra("isSearchClicked",true);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // Set your date format
                String currentData = sdf.format(SendDate);
                SearchIntent.putExtra("WheelDate",currentData);
                Integer time2send =  Wheelhour.getCurrentItem()+6;
                SearchIntent.putExtra("WheelHour",""+time2send);
                SearchIntent.putExtra("WheelWaze",""+(waze*2+1+Go_Back));
                startActivity(SearchIntent);
                break;
            case R.id.settings:
                startActivity(new Intent(".DriversMainActivity"));
                break;
        }
    }

    }
