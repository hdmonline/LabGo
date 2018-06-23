package alpha.labgo.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

public class QrCodeOverlay extends android.support.v7.widget.AppCompatImageView {

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

        float cardWith = canvasWidth / 2;;
        float cardHeight = cardWith;
        float margin = (canvasHeight - cardHeight) / 2;

        int lineWidth = 10;
        float halfLineWidth = lineWidth / 2;

        // TODO: draw overlay
        mMaskPath.moveTo(0, 0);
        mMaskPath.lineTo(canvasWidth, 0);
        mMaskPath.lineTo(canvasWidth, canvasHeight);
        mMaskPath.lineTo(0, canvasHeight);
        mMaskPath.close();

        mMaskPath.moveTo(canvasWidth/4, margin);
        mMaskPath.lineTo(canvasWidth*3/4, margin);
        mMaskPath.lineTo(canvasWidth*3/4, canvasHeight-margin);
        mMaskPath.lineTo(canvasWidth/4, canvasHeight-margin);
        mMaskPath.close();

        // TODO: draw lines
        float lineLength = cardWith / 8;
        // --
        //
        mLinesPath.moveTo(canvasWidth/4-halfLineWidth, margin);
        mLinesPath.lineTo(canvasWidth/4-halfLineWidth+lineLength, margin);
        mLinesPath.close();
        // |
        //
        mLinesPath.moveTo(canvasWidth/4, margin-halfLineWidth);
        mLinesPath.lineTo(canvasWidth/4, margin-halfLineWidth+lineLength);
        mLinesPath.close();
        //          --
        //
        mLinesPath.moveTo(canvasWidth*3/4+halfLineWidth, margin);
        mLinesPath.lineTo(canvasWidth*3/4+halfLineWidth-lineLength, margin);
        mLinesPath.close();
        //            |
        //
        mLinesPath.moveTo(canvasWidth*3/4, margin-halfLineWidth);
        mLinesPath.lineTo(canvasWidth*3/4, margin-halfLineWidth+lineLength);
        mLinesPath.close();
        //
        // |
        mLinesPath.moveTo(canvasWidth/4, canvasHeight-margin+halfLineWidth);
        mLinesPath.lineTo(canvasWidth/4, canvasHeight-margin+halfLineWidth-lineLength);
        mLinesPath.close();
        //
        // __
        mLinesPath.moveTo(canvasWidth/4-halfLineWidth, canvasHeight-margin);
        mLinesPath.lineTo(canvasWidth/4-halfLineWidth+lineLength, canvasHeight-margin);
        mLinesPath.close();
        //
        //          __
        mLinesPath.moveTo(canvasWidth*3/4+halfLineWidth, canvasHeight-margin);
        mLinesPath.lineTo(canvasWidth*3/4+halfLineWidth-lineLength, canvasHeight-margin);
        mLinesPath.close();
        //
        //            |
        mLinesPath.moveTo(canvasWidth*3/4, canvasHeight-margin+halfLineWidth);
        mLinesPath.lineTo(canvasWidth*3/4, canvasHeight-margin+halfLineWidth-lineLength);
        mLinesPath.close();

        // draw overlay
        mMaskPath.setFillType(Path.FillType.EVEN_ODD);
        mOverlayPaint.setARGB(150, 0, 0, 0);
        mOverlayPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mMaskPath, mOverlayPaint);

        // draw lines
        mLinePaint.setARGB(255, 0, 255, 0);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(lineWidth);
        canvas.drawPath(mLinesPath, mLinePaint);
    }
}