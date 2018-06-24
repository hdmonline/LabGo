package alpha.labgo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import alpha.labgo.database.RestUtils;

public class QrCodeActivity extends BaseActivity {
    private static final String TAG = "QrCodeActivity";

    private SurfaceView mQrCodePreview;
    private ViewPager mViewPager;
    private String gtid;
    private Context context;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        gtid = getIntent().getStringExtra("gtid");

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQrCodePreview = findViewById(R.id.qr_code_preview);

        barcodeDetector = new BarcodeDetector.Builder(this).build();
        cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 900)
                .build();

        mQrCodePreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(QrCodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(mQrCodePreview.getHolder());
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

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() > 0) {
                    String code = qrCodes.valueAt(0).displayValue;
                    //cameraSource.release();
                    //mViewPager = getActivity().findViewById(R.id.container);
                    //mViewPager.setCurrentItem(1);
                    // send check in/out request
                    String[] paramStrings = {gtid, code};
                    new RestUtils.StudentCheckInOrOut(getApplicationContext()).execute(paramStrings);
                    // stop detector and camera then move to dashboard.
                    barcodeDetector.release();
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
