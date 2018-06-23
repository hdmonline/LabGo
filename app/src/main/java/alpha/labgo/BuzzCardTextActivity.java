package alpha.labgo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import alpha.labgo.BaseActivity;
import alpha.labgo.overlay.BuzzCardOverlay;

public class BuzzCardTextActivity extends BaseActivity implements View.OnTouchListener {


    private static final String TAG = "BuzzCardTextActivity";

    private int caller;

    private CameraPreview  mCameraPreview;
    private ImageView mOverlay;
    private FrameLayout mPreview;
    private ImageButton mShutterButton;
    private ImageView mImageCaptured;
    private BuzzCardOverlay mBuzzCardOverlay;
    private ImageButton mConfirmButton;

    private int mOrientation = Surface.ROTATION_90;
    private boolean mCameraRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buzz_card_text);

        // check the caller
        caller = getIntent().getIntExtra("caller", 0);

        mPreview = findViewById(R.id.layout_preview);
        mImageCaptured = findViewById(R.id.image_captured);
        mOverlay = findViewById(R.id.camera_overlay);

        if (allPermissionsGranted()) {
            createCameraPreview();
        } else {
            getRuntimePermissions();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraRequested) {
            CameraUtils.startPreview();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mCameraRequested) {
            CameraUtils.startPreview();
        }
    }

    /** Stops the camera. */
    @Override
    protected void onPause() {
        super.onPause();
        CameraUtils.stopPreview();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Resources res = getResources();
        switch (v.getId()) {
            case R.id.button_shutter:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mShutterButton.setBackground(res.getDrawable(R.drawable.ic_shutter_pressed));
                    takePicture();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mShutterButton.setBackground(res.getDrawable(R.drawable.ic_shutter));
                    return true;
                }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createCameraPreview() {
        mPreview = findViewById(R.id.layout_preview);
        mCameraPreview = new CameraPreview(this);
        mPreview.addView(mCameraPreview);
        mOrientation = CameraUtils.calculateCameraPreviewOrientation(BuzzCardTextActivity.this);
        //CameraUtils.setOrientation(mOrientation);
        mShutterButton = findViewById(R.id.button_shutter);
        mShutterButton.setOnTouchListener(this);
    }



    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "Permission granted!");
        mCameraRequested = true;
        if (allPermissionsGranted()) {
            createCameraPreview();
        }
    }


    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(int id){
        Camera c = null;
        try {
            c = Camera.open(id); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + ".jpg");

        return mediaFile;
    }

    private void takePicture() {
        CameraUtils.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                CameraUtils.startPreview();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (bitmap != null) {
                    bitmap = ImageUtils.getRotatedBitmap(bitmap, mOrientation);
                    String path = Environment.getExternalStorageDirectory() + "/DCIM/Camera/"
                            + System.currentTimeMillis() + ".jpg";
                    mImageCaptured.setImageBitmap(bitmap);
                    try {
                        FileOutputStream fout = new FileOutputStream(path);
                        BufferedOutputStream bos = new BufferedOutputStream(fout);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();
                        fout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                CameraUtils.startPreview();
            }
        });
    }
}
