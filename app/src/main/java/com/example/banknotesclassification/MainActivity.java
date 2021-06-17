package com.example.banknotesclassification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.nnapi.NnApiDelegate;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private static Bitmap bitmap;
    private Classifier classifier;
    private MediaPlayer seribu, duaRibu, limaRibu, sepuluhRibu, duapuluhRibu, limapuluhRibu,seratusRibu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        seribu = MediaPlayer.create(myContext, R.raw.seribu);
        duaRibu = MediaPlayer.create(myContext, R.raw.dua_ribu);
        limaRibu = MediaPlayer.create(myContext, R.raw.lima_ribu);
        sepuluhRibu = MediaPlayer.create(myContext, R.raw.sepuluh_ribu);
        duapuluhRibu = MediaPlayer.create(myContext, R.raw.duapuluh_ribu);
        limapuluhRibu = MediaPlayer.create(myContext, R.raw.limapuluh_ribu);
        seratusRibu = MediaPlayer.create(myContext, R.raw.seratus_ribu);

        cameraPreview = (LinearLayout) findViewById(R.id.cameraPreview);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        cameraPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });

        onResume();
        mCamera.startPreview();

        try {
            classifier = Classifier.create(this, Classifier.Device.CPU, 1);
        } catch (IOException | IllegalArgumentException e) {
            Log.e(e.toString(), "Failed to create classifier.");
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onResume() {

        super.onResume();
        if(mCamera == null) {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
            Log.d("nu", "null");
        }else {
            Log.d("nu","no null");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                processImage(bitmap);
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

    protected void processImage(Bitmap rgbFrameBitmap) {
            final long startTime = SystemClock.uptimeMillis();
            final List<Classifier.Recognition> results =
                    classifier.recognizeImage(rgbFrameBitmap, 90);
            Log.v("Detect: %s", results.toString());
            showResults(results);
    }

    private void showResults(List<Classifier.Recognition> results) {
        Classifier.Recognition recognition = results.get(0);
        if (recognition != null) {
            if (recognition.getTitle() != null && recognition.getConfidence() != null && recognition.getConfidence() > 0.9) {
//                Toast.makeText(this, recognition.getTitle(), Toast.LENGTH_LONG).show();

                switch (recognition.getTitle()) {
                    case "1000":
                        seribu.start();
                        break;
                    case "2000":
                        duaRibu.start();
                        break;
                    case "5000":
                        limaRibu.start();
                        break;
                    case "10000":
                        sepuluhRibu.start();
                        break;
                    case "20000":
                        duapuluhRibu.start();
                        break;
                    case "50000":
                        limapuluhRibu.start();
                        break;
                    case "100000":
                        seratusRibu.start();
                        break;
                }
            } else {
                Toast.makeText(this, "Uang tidak terdeteksi", Toast.LENGTH_LONG).show();
            }
        }
    }
}