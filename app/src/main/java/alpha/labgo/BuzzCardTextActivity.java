package alpha.labgo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuzzCardTextActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUESTS = 2;
    private static final String TAG = "BuzzCardTextActivity";

    private int caller;

    private CameraPreview  mCameraPreview;
    private ImageView mOverlay;
    private FrameLayout mPreview;
    private ImageButton mShutterButton;

    private int mOrientation;
    private boolean mCameraRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzz_card_text);

        // check the caller
        caller = getIntent().getIntExtra("caller", 0);

        mPreview = findViewById(R.id.layout_preview);

        if (allPermissionsGranted()) {
            createCameraPreview();
        } else {
            getRuntimePermissions();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_shutter:
                takePicture();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createCameraPreview() {
        mPreview = findViewById(R.id.layout_preview);
        mCameraPreview = new CameraPreview(this);
        mPreview.addView(mCameraPreview);
        mOrientation = Surface.ROTATION_90;
        mShutterButton = findViewById(R.id.button_shutter);
        mShutterButton.setOnClickListener(this);
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                        .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String [] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context,String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
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
