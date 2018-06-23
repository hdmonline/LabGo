package alpha.labgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

public class QrCodeOverlay extends ImageView {

    private static final float CARD_RATIO = 1.59f;

    public QrCodeOverlay(Context context) {
        super(context);
    }

    public QrCodeOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QrCodeOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOverlay(canvas);
    }

    private void drawOverlay(Canvas canvas) {
        Path mMaskPath = new Path();
        Path mLinesPath = new Path();
        Paint mOverlayPaint = new Paint();
        Paint mLinePaint = new Paint();

        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();
        float canvasRatio = canvasHeight / canvasWidth;

        float cardWith;
        float cardHeight;
        float margin;

        // TODO: draw overlay

        // TODO: draw lines

        // draw overlay
        mMaskPath.setFillType(Path.FillType.EVEN_ODD);
        mOverlayPaint.setARGB(150, 0, 0, 0);
        mOverlayPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mMaskPath, mOverlayPaint);

        // draw lines
        mLinePaint.setARGB(255, 255, 255, 255);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(8);
        canvas.drawPath(mLinesPath, mLinePaint);
    }
}