package alpha.labgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class BuzzCardOverlay extends ImageView {

    private static final float CARD_RATIO = 1.59f;

    public BuzzCardOverlay(Context context) {
        super(context);
    }

    public BuzzCardOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BuzzCardOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOverlay(canvas);
    }

    private void drawOverlay(Canvas canvas) {
        Path mMask = new Path();
        Paint mOverlayPaint = new Paint();
        Paint mTextPaint = new Paint();

        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();
        float canvasRatio = canvasHeight / canvasWidth;

        float cardWith;
        float cardHeight;
        float margin;

        if (canvasRatio >= CARD_RATIO) {
            cardWith = canvasWidth * 3 / 4;
            cardHeight = cardWith * CARD_RATIO;
            margin = (canvasHeight - cardHeight) / 2;

            mMask.moveTo(0, 0);
            mMask.lineTo(canvasWidth, 0);
            mMask.lineTo(canvasWidth, canvasHeight);
            mMask.lineTo(0, canvasHeight);
            mMask.close();

            mMask.moveTo(canvasWidth/8, margin);
            mMask.lineTo(canvasWidth*7/8, margin);
            mMask.lineTo(canvasWidth*7/8, canvasHeight-margin);
            mMask.lineTo(canvasWidth/8, canvasHeight-margin);
            mMask.close();
        } else {
            cardHeight = canvasHeight * 3 / 4;
            cardWith = cardHeight / CARD_RATIO;
            margin = (canvasWidth - cardWith) / 2;

            mMask.moveTo(0, 0);
            mMask.lineTo(canvasWidth, 0);
            mMask.lineTo(canvasWidth, canvasHeight);
            mMask.lineTo(0, canvasHeight);
            mMask.close();

            mMask.moveTo(margin, canvasHeight/8);
            mMask.lineTo(canvasWidth-margin, canvasHeight/8);
            mMask.lineTo(canvasWidth-margin, canvasHeight*7/8);
            mMask.lineTo(margin,canvasHeight*7/8);
            mMask.close();
        }


        mMask.setFillType(Path.FillType.EVEN_ODD);

        mOverlayPaint.setARGB(200, 0, 0, 0);
        mOverlayPaint.setStyle(Paint.Style.FILL);

        // draw overlay
        canvas.drawPath(mMask, mOverlayPaint);

        // draw hint text
        mTextPaint.setARGB(255, 255, 255, 255);
        mTextPaint.setStyle(Paint.Style.FILL);
        float fontSize = canvasWidth/25; // in pixel
        mTextPaint.setTextSize(canvasWidth/20);
        canvas.save();
        float textX = (canvasHeight-cardHeight)/2 + 10;
        float textY = -((canvasWidth-cardWith)/2 - 1.2f*fontSize);
        canvas.rotate(90f);
        canvas.drawText(getResources().getString(R.string.camera_hint) , textX, textY, mTextPaint);
        canvas.restore();
    }
}