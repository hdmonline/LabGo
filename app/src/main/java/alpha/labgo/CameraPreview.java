package alpha.labgo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = CameraPreview.class.getSimpleName();

    private SurfaceHolder mSurfaceHolder;

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraUtils.openRearCamera(CameraUtils.DESIRED_PREVIEW_FPS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraUtils.startPreviewDisplay(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraUtils.releaseCamera();
    }
}