package com.proshooters.tennisnetmagic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by David on 9/17/13.
 */
public class photoActivity extends Activity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int REQUEST_CODE = 1;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private Bitmap picBitmap;
    private Uri fileUri;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            TextView statusView = (TextView) findViewById(R.id.statusView);
            ImageView netImage = (ImageView) findViewById(R.id.netView);

            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent


                if (data == null) {

                    Bitmap picBitmap = BitmapFactory.decodeFile(fileUri.getPath());

                    netImage.setImageBitmap(Bitmap.createScaledBitmap(picBitmap,300,400,false));
                    statusView.setText("Got net image");

                } else {
                    // DEAD CODE FOR NOW, THIS IS ONLY IF WE GET A DATASTREAM, NOT A FILE
                    Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_SHORT).show();
                    statusView.setText("Image Saved");
                    // okay, we should now have the captured image

                    File picFile = new File(data.getData().toString());

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

    }


    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
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


}