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
        import static org.opencv.highgui.Highgui.imread;
        import static org.opencv.imgproc.Imgproc.HoughLines;
        import static org.opencv.imgproc.Imgproc.HoughLinesP;
        import static org.opencv.imgproc.Imgproc.cvtColor;

        import org.opencv.android.BaseLoaderCallback;
        import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
        import org.opencv.android.LoaderCallbackInterface;
        import org.opencv.android.OpenCVLoader;
        import org.opencv.android.Utils;
        import org.opencv.core.Core;
        import org.opencv.core.CvType;
        import org.opencv.core.Mat;
        import org.opencv.core.MatOfPoint;
        import org.opencv.core.Point;
        import org.opencv.core.Rect;
        import org.opencv.core.Scalar;
        import org.opencv.core.Size;
        import org.opencv.android.CameraBridgeViewBase;
        import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
        import org.opencv.imgproc.Imgproc;

/**
 * Created by David on 9/19/13.
 */
public class processImage {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int REQUEST_CODE = 1;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final int PROCESS_IMAGE_CODE = 300;

    private Mat imageContour;
    private Mat matImage;

    private Bitmap picBitmap;
    private Uri fileUri;

    public void CVTest(){

        System.out.println("Welcome to OpenCV " + Core.VERSION);
        Mat m = new Mat(5,10, CvType.CV_8UC1, new Scalar(0));
        System.out.println("OpenCV Mat: " + m);
        Mat mr1 = m.row(1);
        mr1.setTo(new Scalar(1));
        Mat mc5 = m.col(5);
        mc5.setTo(new Scalar(5));
        System.out.println("OpenCV Mat data:\n" + m.dump());

    }

    public void imageEdges(){

        Mat matImageBlur = new Mat();
        matImage = imread(MainActivity.imagefileURI.getPath());

        if (matImage.empty() == false) {

            // Optional pre-processing with Blur
            Imgproc.blur(matImage, matImageBlur, new Size(3,3));

            double thresh = 50;
            imageContour = new Mat();
            Mat imageContourGray = new Mat();

            Imgproc.Canny(matImageBlur, imageContour, thresh, thresh * 4);

            Bitmap picBitmapProcessed = Bitmap.createBitmap(imageContour.cols(),imageContour.rows(),Bitmap.Config.ARGB_8888);
            //Imgproc.cvtColor(imageContour, imageContour, Imgproc.COLOR_GRAY2RGBA,4);

            Utils.matToBitmap(imageContour,picBitmapProcessed,false);

            MainActivity.seeImage.setImageBitmap(Bitmap.createScaledBitmap(picBitmapProcessed,1200,1600,false));
            //MainActivity.seeImage.setImageBitmap(picBitmapProcessed);
        }
    }

    public void imageLines(){

            // Canny produces a gray image, so no need to convert
            //cvtColor(imageContour, imageContourGray, Imgproc.COLOR_RGB2GRAY,4);

            Mat lines = new Mat();
            int threshold = MainActivity.threshold;
            int minLineSize = MainActivity.minLineSize;
            int lineGap = 20;
            int lineDisplayWidth = 15;

        if (imageContour.empty() == false ) {

            HoughLinesP(imageContour, lines, 1, Math.PI/180, threshold, minLineSize, lineGap);

            for (int i = 0; i < lines.cols() ; i++){

                double[] vec = lines.get(0, i);
                double x1 = vec[0],
                        y1 = vec[1],
                        x2 = vec[2],
                        y2 = vec[3];
                Point start = new Point(x1,y1);
                Point end = new Point(x2, y2);
                Core.line(matImage, start, end, new Scalar(255,0,0), lineDisplayWidth);

            }

//            Bitmap picBitmapProcessed = Bitmap.createBitmap(imageContour.cols(),imageContour.rows(),Bitmap.Config.ARGB_8888);
            Bitmap picBitmapProcessed = Bitmap.createBitmap(matImage.cols(),matImage.rows(),Bitmap.Config.ARGB_8888);
            //Imgproc.cvtColor(imageContour, imageContour, Imgproc.COLOR_GRAY2RGBA,4);

            Utils.matToBitmap(matImage,picBitmapProcessed,false);

            MainActivity.seeImage.setImageBitmap(Bitmap.createScaledBitmap(picBitmapProcessed,1200,1600,false));
               //MainActivity.seeImage.setImageBitmap(picBitmapProcessed);

        } else {
            // Couldn't read image

        }
    }

    public void imageGray(){

        Mat picBitmapMat = null;
        Mat picBitmapMatGray = new Mat();

        picBitmapMat = imread(MainActivity.imagefileURI.getPath());
        if (picBitmapMat.empty() == false) {
        cvtColor(picBitmapMat,picBitmapMatGray, Imgproc.COLOR_RGB2GRAY);
//            blur(imageNetGray, imageNetGray, Size(3,3));

        int thresh = 100;
        Mat imageContour = null;
//            Canny(imageNetGray, imageContour, thresh, (int) thresh * 2, 3);

        Bitmap picBitmapProcessed = Bitmap.createBitmap(picBitmapMatGray.cols(),picBitmapMatGray.rows(),Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(picBitmapMatGray, picBitmapMatGray, Imgproc.COLOR_GRAY2RGBA,4);

        Utils.matToBitmap(picBitmapMatGray,picBitmapProcessed,false);

        MainActivity.seeImage.setImageBitmap(Bitmap.createScaledBitmap(picBitmapProcessed,300,400,false));

        } else {
            // Couldn't read image

        }
    }


}
