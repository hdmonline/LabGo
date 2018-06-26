package alpha.labgo;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
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
import java.util.List;

import alpha.labgo.overlays.BuzzCardOverlay;

public class BuzzCardTextActivity extends BaseActivity implements View.OnTouchListener {

    private static final String TAG = "BuzzCardTextActivity";

    private int caller;

    private CameraPreview  mCameraPreview;
    private FrameLayout mPreview;
    private ImageView mImageCaptured;
    private ImageButton mShutterButton;
    private ImageButton mRetakeButton;
    private ImageButton mConfirmButton;
    //private TextView mDetectedText;
    private BuzzCardOverlay mOverlay;

    private int mPreviewOrientation = Surface.ROTATION_0;
    private int mPictureOrientation = Surface.ROTATION_90;
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
        mRetakeButton = findViewById(R.id.button_retake);
        mConfirmButton = findViewById(R.id.button_confirm);

        mRetakeButton.setVisibility(View.INVISIBLE);
        mConfirmButton.setVisibility(View.INVISIBLE);
        //mDetectedText = findViewById(R.id.detected_text);

        // rotate the text view.
//        mDetectedText.getViewTreeObserver()
//                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mDetectedText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                int height = mDetectedText.getHeight();
//                int width = mDetectedText.getWidth();
//                AnimationSet animText = new AnimationSet(true);
//                RotateAnimation rotate = new RotateAnimation(0, 90,
//                        Animation.RELATIVE_TO_SELF, 0.5f,
//                        Animation.RELATIVE_TO_SELF, 0.5f);
//                rotate.setDuration(0);
//                rotate.setRepeatCount(-1);
//                animText.addAnimation(rotate);
//                TranslateAnimation translate = new TranslateAnimation(
//                        Animation.ABSOLUTE, width/2-height,
//                        Animation.ABSOLUTE, 0);
//                animText.addAnimation(translate);
//                mDetectedText.startAnimation(animText);
//            }
//        });
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
        //CameraUtils.setPreviewDefaultSize(mCameraFrameLayout.getWidth(), mCameraFrameLayout.getHeight());
        CameraUtils.setOrientation(mPreviewOrientation);
        mCameraPreview = new CameraPreview(this);
        mPreview.addView(mCameraPreview);
        mPreviewOrientation = CameraUtils.calculateCameraPreviewOrientation(BuzzCardTextActivity.this);
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
                // TODO: decide if anything is needed for this action
            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                CameraUtils.startPreview();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (bitmap != null) {
                    bitmap = ImageUtils.getRotatedBitmap(bitmap, mPictureOrientation);
                    // detect Text from image
                    detectText(bitmap);
                    // save the image to storage.
                    //savePicture(bitmap);
                }
                CameraUtils.startPreview();
            }
        });
    }

    private void detectText(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processText(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String fail = "Please retake a picture";
                //mOverlay.drawTextOnTop(fail);
            }
        });
    }

    private void processText(FirebaseVisionText text) {
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(BuzzCardTextActivity.this, "No GTID found :( Please take a picture again.", Toast.LENGTH_LONG).show();
            return;
        }
        // remove all non-digit characters in each block and exam it.
        for (FirebaseVisionText.Block block : text.getBlocks()) {
            String detectedText = block.getText();
            String gtid = detectedText.replaceAll("\\D+", "");
            if (gtid.length() == 9) {
                String display = "GTID: " + gtid;
                Toast.makeText(BuzzCardTextActivity.this, display, Toast.LENGTH_LONG).show();
                //mOverlay.drawTextOnTop(display);

                Intent intent;
                switch (caller) {
                    case 2:
                        intent = new Intent(BuzzCardTextActivity.this, SignInActivity.class);
                        setResult(CommonStatusCodes.SUCCESS, intent);
                        intent.putExtra("gtid", gtid);
                        finish();
                        break;
                    default:
                        intent = new Intent(BuzzCardTextActivity.this, SignUpActivity.class);
                        intent.putExtra("gtid", gtid);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        }
    }

    private void savePicture(Bitmap bitmap) {
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
}
