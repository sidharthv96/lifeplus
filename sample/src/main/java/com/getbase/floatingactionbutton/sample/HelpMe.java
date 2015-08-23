package com.getbase.floatingactionbutton.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.ByteArrayOutputStream;

public class HelpMe extends Activity {

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


    public void alert(View v){
        if(lastKnown!=null) {
            Location location = lastKnown;
            MainActivity.dataQ.put(MainActivity.dataQ.size() + 1, new DataPacket(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new Date().toString(), String.valueOf(new Date().getTime()), String.valueOf(location.getSpeed()),"Null","Self","Self","Self","Self"));
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
            MainActivity.dataQ.put(MainActivity.dataQ.size() + 1, new DataPacket(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new Date().toString(), String.valueOf(new Date().getTime()), String.valueOf(location.getSpeed()),BitMapToString(b_image),type,status,repid,severity));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_me);
        devID = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        context=this;
        provider = locationManager.getBestProvider(criteria, false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        img = (ImageView) findViewById(R.id.imageView);
        if(img!=null)
        img.setImageResource(R.drawable.camera);

        //CAMERA = (Button) findViewById(R.id.camer);
        alert = (Button) findViewById(R.id.angry_btn);

        /**
         * Capture image button click event
         */
        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                Toast.makeText(getApplicationContext(),"This feature is under construction",Toast.LENGTH_SHORT).show();
                //captureImage();
            }
        });
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alert(null);

            }
        });



        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }
    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public String BitMapToString(Bitmap bitmap){
        if (bitmap==null)
            return "Null";
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled recording
            Toast.makeText(getApplicationContext(),
                    "User cancelled video recording", Toast.LENGTH_SHORT)
                    .show();
        }
    }




    private void previewCapturedImage() {
        try {
            // hide video preview


            img.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            b_image=bitmap;
            img.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }




    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }


    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    Location lastKnown=null;

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(),"Welcome Back",Toast.LENGTH_SHORT).show();

        locationManager.requestLocationUpdates(provider, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastKnown=location;
//                Toast.makeText(getApplicationContext(),"Post",Toast.LENGTH_SHORT).show();
//                MainActivity.dataQ.put(MainActivity.dataQ.size() + 1, new DataPacket(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new Date().toString(), String.valueOf(new Date().getTime()), String.valueOf(location.getSpeed())));
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

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help_me, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}

