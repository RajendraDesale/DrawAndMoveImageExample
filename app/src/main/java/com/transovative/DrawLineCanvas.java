package com.transovative;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawLineCanvas extends View {
    public Bitmap mBitmap;
    public Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private int BitmapSize = 30;
    private int width, height;
    private Context context;
    private float downx = 0, downy = 0, upx = 0, upy = 0;

    public DrawLineCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void setDrawableCanvas(Bitmap bitmap){
        width = bitmap.getWidth() + BitmapSize * 2;
        height = bitmap.getHeight() + BitmapSize * 2;

        Bitmap bitmap2 = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.RGB_565
        );

        mCanvas = new Canvas(bitmap2);
        mCanvas.drawColor(Color.CYAN);
        mCanvas.drawBitmap(
                bitmap2,
                BitmapSize,
                BitmapSize,
                mPaint
        );
        //mCanvas.setBitmap(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.alarm_clock);
        //Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        // canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawColor(0x00AAAAAA);
        canvas.drawPath(mPath, mPaint);
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                mCanvas.drawLine(downx, downy, upx, upy, mPaint);
                //mCanvas.drawRect(downx, downy, upx, upy, mPaint);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bmp;
    }

    public void clear() {
        //mBitmap.eraseColor(Color.GREEN);
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
        System.gc();
        //mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }
}
