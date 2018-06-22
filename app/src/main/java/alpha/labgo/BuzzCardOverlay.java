package alpha.labgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class BuzzCardOverlay extends ImageView {

    private final Paint mPaint;
    private final Path mMask;
    private static final float CARD_RATIO = 1.59f;

    public BuzzCardOverlay(Context context) {
        super(context);
        mPaint = new Paint();
        mMask = new Path();
    }

    public BuzzCardOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mMask = new Path();
    }

    public BuzzCardOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mMask = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPolygon(canvas);
    }

    private void drawPolygon(Canvas canvas) {
        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();

        float canvasRatio = canvasHeight / canvasWidth;

        if (canvasRatio >= CARD_RATIO) {
            float cardWith = canvasWidth * 3 / 4;
            float cardHeight = cardWith * CARD_RATIO;
            float margin = (canvasHeight - cardHeight) / 2;

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

            float cardHeight = canvasHeight * 3 / 4;
            float cardWith = cardHeight / CARD_RATIO;

            float margin = (canvasWidth - cardWith) / 2;

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

        mPaint.setARGB(200, 0, 0, 0);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawPath(mMask, mPaint);
    }
}