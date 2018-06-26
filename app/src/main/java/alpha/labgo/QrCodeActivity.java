package alpha.labgo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import alpha.labgo.database.RestUtils;

public class QrCodeActivity extends BaseActivity {
    private static final String TAG = "QrCodeActivity";
    private static final String CHECK_IN = "checkIn";
    private static final String CHECK_OUT = "checkOut";

    private SurfaceView mQrCodePreview;
    private ViewPager mViewPager;
    private String mGtid;
    private Context mContext;
    private BarcodeDetector mBarcodeDetector;
    private CameraSource mCameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        mGtid = getIntent().getStringExtra("gtid");

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
        mContext = QrCodeActivity.this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQrCodePreview = findViewById(R.id.qr_code_preview);

        mBarcodeDetector = new BarcodeDetector.Builder(mContext).build();
        mCameraSource = new CameraSource.Builder(mContext, mBarcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1280, 720)
                .build();

        mQrCodePreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    mCameraSource.start(mQrCodePreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        mBarcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() > 0) {
                    String code = qrCodes.valueAt(0).displayValue;

                    // check if the code is valid
                    if (!(code.equals(CHECK_IN) || code.equals(CHECK_OUT))) {
                        Log.w(TAG, "Wrong QR code!");
                        Toast.makeText(mContext, "Wrong QR code!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        String[] paramStrings = {mGtid, code};
                        new RestUtils.StudentCheckInOrOut(mContext).execute(paramStrings);
                        // stop detector and camera then move to dashboard.
                        mBarcodeDetector.release();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
