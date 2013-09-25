package com.proshooters.tennisnetmagic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_PICTURES;
import static org.opencv.highgui.Highgui.imread;
import static org.opencv.imgproc.Imgproc.cvtColor;


public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final int CONVERT_TO_GRAY = 6;
    public static Uri imagefileURI;
    public static ZoomableImageView seeImage;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int REQUEST_CODE = 1;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final int PROCESS_IMAGE_CODE = 300;

    private Uri fileUri;
    private boolean firstrun = true;
    private processImage pImage;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    System.out.println("OpenCV LOADED CORRECTLY ***************");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView statusView = (TextView) findViewById(R.id.statusView);
        ZoomableImageView netImage = (ZoomableImageView) findViewById(R.id.netView);
//        netImage.setMaxZoom(4f);
//        netImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        seeImage = netImage;

        Button buttonPhoto = (Button) findViewById(R.id.photoButton);
        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // create Intent to take a picture and return control to the calling application
                android.content.Intent intent = new android.content.Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // Let the user know the file path
                Context context = getApplicationContext();
                CharSequence text = "Image: ".concat(fileUri.getPath());

                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);


            }
        });

        final Button buttonProcess = (Button) findViewById(R.id.processButton);
        buttonProcess.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            if (pImage == null) pImage = new processImage();

            if (firstrun == true) {
                pImage.imageEdges();
                buttonProcess.setText(R.string.findlines);
                firstrun = false;
            } else {

                pImage.imageLines();
                buttonProcess.setText(R.string.findedges);
                File tobeDeleted = new File(fileUri.getPath());
                tobeDeleted.delete();
                firstrun = true;

            }
        }


        });
    }

    // TOTALLY BROKEN BECAUSE CALIBRATION USES JAVA CAMERA VIEW WHICH WONT LOAD
    public void doCalibration(View view) {
        Intent intent = new Intent(this, CameraCalibrationActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        File picFile = new File(fileUri.getPath());
        TextView statusView = (TextView) findViewById(R.id.statusView);
        ZoomableImageView netImage = (ZoomableImageView) findViewById(R.id.netView);
        Button procImage = (Button) findViewById(R.id.processButton);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent

                if (data == null) {

                    Bitmap picBitmap = BitmapFactory.decodeFile(fileUri.getPath());

                    // netImage.setScaleType(ImageView.ScaleType.CENTER);

                    //netImage.setAdjustViewBounds(true);
                    netImage.setImageBitmap(Bitmap.createScaledBitmap(picBitmap, 1200, 1600, false));
                    //netImage.setMaxZoom(4f);

                    statusView.setText("Got net image");
                    procImage.setEnabled(true);

                } else {
                    // DEAD CODE FOR NOW, THIS IS ONLY IF WE GET A DATASTREAM, NOT A FILE
                    Toast.makeText(this, "Image saved to:\n" +
                            data.getData(), Toast.LENGTH_SHORT).show();
                    statusView.setText("Image Saved");
                    // okay, we should now have the captured image

                    Bitmap picBitmap = BitmapFactory.decodeFile(picFile.getAbsolutePath());
                    netImage.setImageBitmap(picBitmap);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
                statusView.setText("Image capture cancelled.");

            } else {
                // Image capture failed, advise user
                statusView.setText("Image capture failed.");
            }
        }
       // finish();

    }


    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        MainActivity.imagefileURI = Uri.fromFile((getOutputMediaFile(type)));
        return Uri.fromFile((getOutputMediaFile(type)));
    }

    /** Create a File for saving an image or video */
    private static java.io.File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        java.io.File pictureDir = new java.io.File(String.valueOf(android.os.Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));

        java.io.File mediaStorageDir = new java.io.File(pictureDir, "TennisMagic");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("TennisMagic", "failed to create directory");
                // return null;
                mediaStorageDir = pictureDir;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }


    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }
}
