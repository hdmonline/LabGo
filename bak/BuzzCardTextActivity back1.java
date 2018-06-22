package alpha.labgo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuzzCardTextActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final int PERMISSION_REQUESTS = 2;
    private static final String TAG = "BuzzCardTextActivity";

    private int caller;

    private CameraPreview  mCameraPreview;
    private ImageView mOverlay;
    private FrameLayout mPreview;
    private ImageButton mShutterButton;
    private Button detectBtn;
    private ImageView mImageCaptured;
    private Bitmap imageBitmap;
    private TextView recognizeTxtView;

    private int mOrientation = Surface.ROTATION_90;
    private boolean mCameraRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzz_card_text);

        // check the caller
        caller = getIntent().getIntExtra("caller", 0);

        mPreview = findViewById(R.id.layout_preview);
        mImageCaptured = findViewById(R.id.image_captured);

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
                    detectTxt();
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

//        detectBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                detectTxt();
//            }
//        });
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
//                imageBitmap = bitmap;
                if (bitmap != null) {
                    bitmap = ImageUtils.getRotatedBitmap(bitmap, mOrientation);
                    imageBitmap = bitmap;
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

    private void detectTxt() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void processTxt(FirebaseVisionText text) {
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(BuzzCardTextActivity.this, "No Text :(", Toast.LENGTH_LONG).show();
            return;
        }
        for (FirebaseVisionText.Block block : text.getBlocks()) {
            String txt = block.getText();
            recognizeTxtView.setTextSize(24);
            recognizeTxtView.setText(txt);
        }
    }

}
