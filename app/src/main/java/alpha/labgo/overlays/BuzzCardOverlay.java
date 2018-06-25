package alpha.labgo.overlays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import alpha.labgo.R;

public class BuzzCardOverlay extends android.support.v7.widget.AppCompatImageView {

    private static final float CARD_RATIO = 1.59f;

    private Paint mTextPaint;
    private float textX;
    private float textYBottom;
    private float textYTop;
    private float fontSize;

    private float canvasWidth;
    private float canvasHeight;
    private float canvasRatio;

    private float cardWith;
    private float cardHeight;
    private float margin;

    private Canvas canvas;

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
        this.canvas = canvas;
        drawOverlay(canvas);
    }

    public void drawTextOnTop(String text) {
        canvas.rotate(90f);
        textYTop = -(canvasWidth/2 - 1.8f*fontSize);
        canvas.drawText(text, textX, textYTop, mTextPaint);
        canvas.restore();
    }

    private void drawOverlay(Canvas canvas) {
        Path mMaskPath = new Path();
        Path mLinesPath = new Path();
        Paint mOverlayPaint = new Paint();
        mTextPaint = new Paint();
        Paint mDashLinePaint = new Paint();

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        canvasRatio = canvasHeight / canvasWidth;

        if (canvasRatio >= CARD_RATIO) {
            // draw overlay
            cardWith = canvasWidth * 3 / 4;
            cardHeight = cardWith * CARD_RATIO;
            margin = (canvasHeight - cardHeight) / 2;

            mMaskPath.moveTo(0, 0);
            mMaskPath.lineTo(canvasWidth, 0);
            mMaskPath.lineTo(canvasWidth, canvasHeight);
            mMaskPath.lineTo(0, canvasHeight);
            mMaskPath.close();

            mMaskPath.moveTo(canvasWidth/8, margin);
            mMaskPath.lineTo(canvasWidth*7/8, margin);
            mMaskPath.lineTo(canvasWidth*7/8, canvasHeight-margin);
            mMaskPath.lineTo(canvasWidth/8, canvasHeight-margin);
            mMaskPath.close();

            // draw card frame and area guide
            mLinesPath.moveTo(canvasWidth/8, margin);
            mLinesPath.lineTo(canvasWidth*7/8, margin);
            mLinesPath.lineTo(canvasWidth*7/8, canvasHeight-margin);
            mLinesPath.lineTo(canvasWidth/8, canvasHeight-margin);
            mLinesPath.close();
            mLinesPath.moveTo(canvasWidth/8f+cardWith/20f, margin+cardHeight/10f);
            mLinesPath.lineTo(canvasWidth/8f+cardWith/8.5f, margin+cardHeight/10f);
            mLinesPath.lineTo(canvasWidth/8f+cardWith/8.5f, margin+cardHeight/2.45f);
            mLinesPath.lineTo(canvasWidth/8f+cardWith/20f, margin+cardHeight/2.45f);

            mLinesPath.close();
            mLinesPath.moveTo(canvasWidth/8f+cardWith/3.7f, margin+cardHeight/1.62f);
            mLinesPath.lineTo(canvasWidth/8f+cardWith/1.05f, margin+cardHeight/1.62f);
            mLinesPath.lineTo(canvasWidth/8f+cardWith/1.05f, margin+cardHeight/1.02f);
            mLinesPath.lineTo(canvasWidth/8f+cardWith/3.7f, margin+cardHeight/1.02f);


            mLinesPath.close();
        } else {
            // draw overlay
            cardHeight = canvasHeight * 3 / 4;
            cardWith = cardHeight / CARD_RATIO;
            margin = (canvasWidth - cardWith) / 2;

            mMaskPath.moveTo(0, 0);
            mMaskPath.lineTo(canvasWidth, 0);
            mMaskPath.lineTo(canvasWidth, canvasHeight);
            mMaskPath.lineTo(0, canvasHeight);
            mMaskPath.close();

            mMaskPath.moveTo(margin, canvasHeight/8);
            mMaskPath.lineTo(canvasWidth-margin, canvasHeight/8);
            mMaskPath.lineTo(canvasWidth-margin, canvasHeight*7/8);
            mMaskPath.lineTo(margin,canvasHeight*7/8);
            mMaskPath.close();

            // draw card frame and area guide
            mLinesPath.moveTo(margin, canvasHeight/8);
            mLinesPath.lineTo(canvasWidth-margin, canvasHeight/8);
            mLinesPath.lineTo(canvasWidth-margin, canvasHeight*7/8);
            mLinesPath.lineTo(margin,canvasHeight*7/8);
            mLinesPath.close();

            // draw card frame and area guide
            mLinesPath.moveTo(margin, canvasHeight/8);
            mLinesPath.lineTo(canvasWidth-margin, canvasHeight/8);
            mLinesPath.lineTo(canvasWidth-margin, canvasHeight*7/8);
            mLinesPath.lineTo(margin,canvasHeight*7/8);
            mLinesPath.close();
            mLinesPath.moveTo(margin+cardWith/20f, canvasHeight/8f+cardHeight/10f);
            mLinesPath.lineTo(margin+cardWith/8.5f, canvasHeight/8f+cardHeight/10f);
            mLinesPath.lineTo(margin+cardWith/8.5f, canvasHeight/8f+cardHeight/2.45f);
            mLinesPath.lineTo(margin+cardWith/20f, canvasHeight/8f+cardHeight/2.45f);
            mLinesPath.close();
            mLinesPath.moveTo(margin+cardWith/3.7f, canvasHeight/8f+cardHeight/1.62f);
            mLinesPath.lineTo(margin+cardWith/1.05f, canvasHeight/8f+cardHeight/1.62f);
            mLinesPath.lineTo(margin+cardWith/1.05f, canvasHeight/8f+cardHeight/1.02f);
            mLinesPath.lineTo(margin+cardWith/3.7f, canvasHeight/8f+cardHeight/1.02f);
            mLinesPath.close();
        }


        mMaskPath.setFillType(Path.FillType.EVEN_ODD);

        mOverlayPaint.setARGB(150, 0, 0, 0);
        mOverlayPaint.setStyle(Paint.Style.FILL);

        // draw overlay
        canvas.drawPath(mMaskPath, mOverlayPaint);

        // draw hint text
        mTextPaint.setARGB(255, 255, 255, 255);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        fontSize = canvasWidth/25; // in pixel
        mTextPaint.setTextSize(canvasWidth/20);
        canvas.save();
        textX = canvasHeight/2;
        textYBottom = -((canvasWidth-cardWith)/2 - 1.2f*fontSize);
        canvas.rotate(90f);
        canvas.drawText(getResources().getString(R.string.camera_hint) , textX, textYBottom, mTextPaint);
        canvas.restore();
        canvas.save();

        // draw dashline
        mDashLinePaint.setARGB(255, 255, 255, 255);
        mDashLinePaint.setStyle(Paint.Style.STROKE);
        mDashLinePaint.setStrokeWidth(8);
        mDashLinePaint.setPathEffect(new DashPathEffect(new float[] {20, 20}, 0));
        canvas.drawPath(mLinesPath, mDashLinePaint);
        canvas.save();
    }
}