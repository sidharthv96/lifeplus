package com.getbase.floatingactionbutton.sample;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends Activity implements SensorEventListener {


    private float[] gravity,linear_acceleration,linear_acceleration_old;
    private boolean isFirstEvent;
    private long lastUpdate;
    SensorManager sensorManager;
    Context context;

    private TextView txtLat;
    private TextView txtLon,txtSpeed;
    private LocationManager locationManager;
    private String provider,devID;
    ImageView img;
    Button CAMERA;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private Uri fileUri; // file url to store image/video
    private Bitmap b_image=null;

    private Button   alert;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 27)
        {
            Log.e("ASD",String.valueOf(actualTime - lastUpdate));
            if (actualTime - lastUpdate < 1000) {
                return;
            }
            Log.e("RED", String.valueOf(accelationSquareRoot));

            lastUpdate = actualTime;
            alert(null);
            Toast.makeText(this, "Device Hit", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    Location lastKnown=null;
    public static Map<Integer,DataPacket> dataQ = new ConcurrentHashMap<Integer, DataPacket>();
    PostTask postTask = new PostTask();


    private class PostTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;

        protected Integer doInBackground(String... x) {

            while(true) {
                Iterator<Integer> iterator = dataQ.keySet().iterator();
                while(iterator.hasNext())
                    try {
                        //                       Toast.makeText(getApplicationContext(),"Next",Toast.LENGTH_SHORT).show();
//                        Log.i("RESP")
                        Integer dk=iterator.next();
                        DataPacket dp=dataQ.get(dk);
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost("http://54.68.166.147/oskad/insert.php");
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
                        nameValuePairs.add(new BasicNameValuePair("ID", devID));
                        nameValuePairs.add(new BasicNameValuePair("Loc", dp.lat+","+dp.lon));
//                        nameValuePairs.add(new BasicNameValuePair("Lon", dp.lon));
                        nameValuePairs.add(new BasicNameValuePair("TimeL", dp.dt));
                        nameValuePairs.add(new BasicNameValuePair("TimeS", dp.date));
                        nameValuePairs.add(new BasicNameValuePair("Speed", dp.speed));
                        nameValuePairs.add(new BasicNameValuePair("Image", dp.image));
                        nameValuePairs.add(new BasicNameValuePair("Type", dp.type));
                        nameValuePairs.add(new BasicNameValuePair("RepID", dp.repid));
                        nameValuePairs.add(new BasicNameValuePair("Status", dp.status));
                        nameValuePairs.add(new BasicNameValuePair("Severity", dp.severity));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = httpclient.execute(httppost);
                        Log.i("RESP", EntityUtils.toString(response.getEntity()));
                        Log.i("RESP", String.valueOf(response.getStatusLine().getStatusCode()));
                        Log.i("RESP", "BR" + String.valueOf(dataQ.size()));
                        if (response.getStatusLine().getStatusCode() == 200)
                            dataQ.remove(dk);
                        Log.i("RESP", String.valueOf(dataQ.size()));
                    } catch (Exception e) {
                        //e.printStackTrace();
                        Log.i("RESPE", e.toString());
                    }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void alert(View v){
        if(lastKnown!=null) {
            Location location = lastKnown;
            dataQ.put(dataQ.size() + 1, new DataPacket(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new Date().toString(), String.valueOf(new Date().getTime()), String.valueOf(location.getSpeed()),"Null","Self","Self","Self","Self"));
            Log.e("T", String.valueOf(location.getLatitude()));
        }
        else{
            Toast.makeText(this, "Error ! Location not detected !", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void alertother(View v){
        if(lastKnown!=null) {
            Location location = lastKnown;
            String type="",status="",repid="",severity="";
            dataQ.put(dataQ.size() + 1, new DataPacket(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new Date().toString(), String.valueOf(new Date().getTime()), String.valueOf(location.getSpeed()), "Null", type, status, repid, severity));
        }
    }


    @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

      sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
      lastUpdate = System.currentTimeMillis();
        devID = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        context=this;
        provider = locationManager.getBestProvider(criteria, false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    findViewById(R.id.pink_icon).setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "Clicked pink Floating Action Button", Toast.LENGTH_SHORT).show();
        }
    });

    FloatingActionButton button = (FloatingActionButton) findViewById(R.id.setter);
    button.setSize(FloatingActionButton.SIZE_MINI);
    button.setColorNormalResId(R.color.pink);
    button.setColorPressedResId(R.color.pink_pressed);
    button.setIcon(R.drawable.ic_fab_star);
    button.setStrokeVisible(false);

    final View actionB = findViewById(R.id.action_b);
    FloatingActionButton actionC = new FloatingActionButton(getBaseContext());
        actionC.setVisibility(View.GONE);

       // actionC.setTitle("Hide/Show Action above");
    actionC.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
      }
    });

    final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
    menuMultipleActions.addButton(actionC);

    final FloatingActionButton removeAction = (FloatingActionButton) findViewById(R.id.button_remove);
    removeAction.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        ((FloatingActionsMenu) findViewById(R.id.multiple_actions_down)).removeButton(removeAction);
      }
    });

    ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
    drawable.getPaint().setColor(getResources().getColor(R.color.white));
    ((FloatingActionButton) findViewById(R.id.setter_drawable)).setIconDrawable(drawable);

    final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
    actionA.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        //actionA.setTitle("Action A clicked");
          Intent intent;
          intent = new Intent(MainActivity.this,HelpMe.class);
            startActivity(intent);

      }
    });

    // Test that FAMs containing FABs with visibility GONE do not cause crashes
    findViewById(R.id.button_gone).setVisibility(View.GONE);

    final FloatingActionButton actionEnable = (FloatingActionButton) findViewById(R.id.action_enable);
    actionEnable.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        menuMultipleActions.setEnabled(!menuMultipleActions.isEnabled());
      }
    });
        if(postTask.getStatus()!= AsyncTask.Status.RUNNING)
            postTask.execute();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
  }

    public int getOptimumSpeed(Location l){
        return 75;
        //This should be made into a complex function which takes data from coudsourced projects
    }

    long lastOS;

    public void overspeed(long time){
        if(time-lastOS<5*60000){
            
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(),"Welcome Back",Toast.LENGTH_SHORT).show();

        locationManager.requestLocationUpdates(provider, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                TextView t=(TextView)findViewById(R.id.speed);
                if(location==null)
                {
                    Toast.makeText(getApplicationContext(),"GPS is not connected",Toast.LENGTH_SHORT).show();
                }
                else {
                    double speed=location.getSpeed();
                    t.setText(String.format("%.2f",(speed*18/5))+ "\nKm/H");
                    speed=(speed*18/5);
                    lastKnown = location;
                    if(speed>getOptimumSpeed(location)){
                        overspeed(location.getTime());
                    }

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

    }
}
