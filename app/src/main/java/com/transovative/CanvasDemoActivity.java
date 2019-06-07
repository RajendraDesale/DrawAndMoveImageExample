package com.transovative;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CanvasDemoActivity extends AppCompatActivity {

    private Bitmap b;
    private DrawCanvas myDrawView;
    private DrawRectangleCanvas rectangleCanvas;
    private DrawLineCanvas myDrawLineView;
    private DrawCircleCanvas drawCircleCanvas;
    private float xCoOrdinate, yCoOrdinate;
    private float oldDist = 1f;
    private Matrix savedMatrix = new Matrix();
    private float[] lastEvent = null;
    private PointF mid = new PointF();
    private int mode = NONE;
    private Matrix matrix = new Matrix();
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private float d = 0f;
    private float upx = 0, upy = 0;
    private static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final Button btnAddText = findViewById(R.id.btn_add_text);
        final Button btnAddDraw = findViewById(R.id.btn_add_draw);
        Button btnSaveImage = findViewById(R.id.btn_save_image);
        myDrawView = findViewById(R.id.draw);
        final Button rectangle = findViewById(R.id.rectangle);
        final Button line = findViewById(R.id.line);
        final Button circle = findViewById(R.id.circle);

        //final ImageView iv = findViewById(R.id.iv);
        final RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(CanvasDemoActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CanvasDemoActivity.this, Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(CanvasDemoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(CanvasDemoActivity.this);
                dialog.setContentView(R.layout.activity_circledraw);
                dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                drawCircleCanvas = dialog.findViewById(R.id.drawCircle);
                Button clear = dialog.findViewById(R.id.btnCirClear);

                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drawCircleCanvas.clear();
                    }
                });

                Button dialogButton = dialog.findViewById(R.id.saveCircle);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap well = drawCircleCanvas.getBitmap();
                        View view = new CanvasWithImage(getApplicationContext(), well);
                        Bitmap bitmap = Bitmap.createBitmap(1000/*width*/, 1000/*height*/, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        view.draw(canvas);
                        b = bitmap;     //for saving "b" to the sdcard

                        //ImageView iv = new ImageView(CanvasDemoActivity.this);
                        TouchImageView touchImageView = new TouchImageView(CanvasDemoActivity.this);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.BELOW, btnAddDraw.getId());
                        touchImageView.setLayoutParams(lp);
                        relativeLayout.addView(touchImageView);

                        touchImageView.setImageBitmap(bitmap);
                        touchImageView.setOnTouchListener(onTouchListener);

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(CanvasDemoActivity.this);
                dialog.setContentView(R.layout.activity_linedraw);
                dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                myDrawLineView = dialog.findViewById(R.id.drawLine);
                Button clear = dialog.findViewById(R.id.btnLineClear);

                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDrawLineView.clear();
                    }
                });

                Button dialogButton = dialog.findViewById(R.id.saveLine);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap well = myDrawLineView.getBitmap();
                        View view = new CanvasWithImage(getApplicationContext(), well);
                        Bitmap bitmap = Bitmap.createBitmap(1000/*width*/, 1000/*height*/, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        view.draw(canvas);
                        b = bitmap;     //for saving "b" to the sdcard

                        //ImageView iv = new ImageView(CanvasDemoActivity.this);
                        TouchImageView touchImageView = new TouchImageView(CanvasDemoActivity.this);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.BELOW, btnAddDraw.getId());
                        touchImageView.setLayoutParams(lp);
                        relativeLayout.addView(touchImageView);

                        touchImageView.setImageBitmap(bitmap);
                        touchImageView.setOnTouchListener(onTouchListener);
                        dialog.dismiss();
                    }
                });

                dialog.show();

                /*Paint paint = new Paint();
                paint.setColor(Color.TRANSPARENT);
                paint.setStrokeWidth(5);
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);

                Bitmap bg = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
                Canvas canvasRect = new Canvas(bg);
                canvasRect.drawLine(10, 10, 390, 10, paint);

                View v = new CanvasWithImage(getApplicationContext(), bg);
                Bitmap bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                b = bitmap;     //for saving "b" to the sdcard

                ImageView iv = new ImageView(CanvasDemoActivity.this);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, btnAddDraw.getId());
                iv.setLayoutParams(lp);
                relativeLayout.addView(iv);

                iv.setImageBitmap(bitmap);
                iv.setOnTouchListener(onTouchListener);*/
            }
        });

        rectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(CanvasDemoActivity.this);
                dialog.setContentView(R.layout.activity_rectangledraw);
                dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                rectangleCanvas = dialog.findViewById(R.id.drawRectangle);
                Button clear = dialog.findViewById(R.id.btnRectClear);

                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rectangleCanvas.clear();
                    }
                });

                Button dialogButton = dialog.findViewById(R.id.saveRectangle);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap well = rectangleCanvas.getBitmap();
                        View view = new CanvasWithImage(getApplicationContext(), well);
                        Bitmap bitmap = Bitmap.createBitmap(1000/*width*/, 1000/*height*/, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        view.draw(canvas);
                        b = bitmap;     //for saving "b" to the sdcard

                        //ImageView iv = new ImageView(CanvasDemoActivity.this);
                        TouchImageView touchImageView = new TouchImageView(CanvasDemoActivity.this);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.BELOW, btnAddDraw.getId());
                        touchImageView.setLayoutParams(lp);
                        relativeLayout.addView(touchImageView);

                        touchImageView.setImageBitmap(bitmap);
                        touchImageView.setOnTouchListener(onTouchListener);
                        dialog.dismiss();
                    }
                });

                dialog.show();

                /*Paint paint = new Paint();
                paint.setColor(Color.TRANSPARENT);
                paint.setStrokeWidth(5);
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);

                Bitmap bg = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
                Canvas canvasRect = new Canvas(bg);
                canvasRect.drawRect(200, 200, 600, 600, paint);

                View v = new CanvasWithImage(getApplicationContext(), bg);
                Bitmap bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                b = bitmap;     //for saving "b" to the sdcard

                ImageView iv = new ImageView(CanvasDemoActivity.this);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, btnAddDraw.getId());
                iv.setLayoutParams(lp);
                relativeLayout.addView(iv);

                iv.setImageBitmap(bitmap);
                iv.setOnTouchListener(onTouchListener);*/
            }
        });

        btnAddDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(CanvasDemoActivity.this);
                dialog.setContentView(R.layout.activity_main);
                dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                myDrawView = dialog.findViewById(R.id.draw);

                Button dialogButton = dialog.findViewById(R.id.button1);
                Button clear = dialog.findViewById(R.id.btnDrawClear);

                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDrawView.clear();
                    }
                });

                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap well = myDrawView.getBitmap();
                        View view = new CanvasWithImage(getApplicationContext(), well);
                        Bitmap bitmap = Bitmap.createBitmap(1000/*width*/, 1000/*height*/, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        view.draw(canvas);
                        b = bitmap;     //for saving "b" to the sdcard

                        //ImageView iv = new ImageView(CanvasDemoActivity.this);
                        TouchImageView iv = new TouchImageView(CanvasDemoActivity.this);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.BELOW, btnAddDraw.getId());
                        iv.setLayoutParams(lp);
                        relativeLayout.addView(iv);

                        iv.setImageBitmap(bitmap);
                        iv.setOnTouchListener(onTouchListener);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(CanvasDemoActivity.this);
                final EditText edittext = new EditText(CanvasDemoActivity.this);
                alert.setTitle("Demo Canvas");
                alert.setMessage("Add Text");
                alert.setView(edittext);
                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = edittext.getText().toString();
                        View v = new CanvasWithText(getApplicationContext(), str);
                        Bitmap bitmap = Bitmap.createBitmap(1200/*width*/, 600/*height*/, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        v.draw(canvas);
                        b = bitmap;     //for saving "b" to the sdcard

                        //ImageView iv = new ImageView(CanvasDemoActivity.this);
                        TouchImageView iv = new TouchImageView(CanvasDemoActivity.this);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.BELOW, btnAddText.getId());
                        iv.setLayoutParams(lp);
                        relativeLayout.addView(iv);

                        iv.setImageBitmap(bitmap);
                        iv.setOnTouchListener(onTouchListener);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create directory if not exist
                final File dir = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name));
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File output = new File(dir, "canvas_demo.png");
                OutputStream os;

                try {
                    os = new FileOutputStream(output);
                    b.setHasAlpha(true);
                    b.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();

                    final Handler handler = new Handler();
                    //this code will scan the image so that it will appear in your gallery when you open next time
                    MediaScannerConnection.scanFile(CanvasDemoActivity.this, new String[]{output.toString()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CanvasDemoActivity.this, "Save Successfully at " + dir.getPath(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                    );
                } catch (FileNotFoundException fnfe) {
                    fnfe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        });
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    xCoOrdinate = view.getX() - event.getRawX();
                    yCoOrdinate = view.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    view.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    lastEvent = new float[4];
                    lastEvent[0] = event.getX(0);
                    lastEvent[1] = event.getX(1);
                    lastEvent[2] = event.getY(0);
                    lastEvent[3] = event.getY(1);
                    d = rotation(event);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    lastEvent = null;
                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float s = x * x + y * y;
        return (float) Math.sqrt(s);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    //your canvas as per your requirement
    public class CanvasWithText extends View {
        private Paint pBackground, pText;
        private String str;

        public CanvasWithText(Context context, String str) {
            super(context);
            this.str = str;
            pBackground = new Paint();
            pText = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            pBackground.setColor(Color.TRANSPARENT);
            canvas.drawRect(0, 0, 512, 512, pBackground);
            pText.setColor(Color.BLUE);
            pText.setTextSize(80);
            canvas.drawText(str, 100, 100, pText);
        }
    }

    //your canvas as per your requirement
    public class CanvasWithImage extends View {
        private Paint pBackground, pText;
        private Bitmap bitmap;

        public CanvasWithImage(Context context, Bitmap bitmap) {
            super(context);
            this.bitmap = bitmap;
            pBackground = new Paint();
            pText = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            pBackground.setColor(Color.TRANSPARENT);
            canvas.drawRect(0, 0, 512, 512, pBackground);
            pText.setColor(Color.BLUE);
            pText.setTextSize(80);

            canvas.drawBitmap(bitmap, 100, 100, pText);
        }
    }

    /**
     * @param bitmap  The source bitmap.
     * @param opacity a value between 0 (completely transparent) and 255 (completely
     *                opaque).
     * @return The opacity-adjusted bitmap. If the source bitmap is mutable it
     * will be adjusted and returned, otherwise a new bitmap is created.
     * Source : http://stackoverflow.com/questions/7392062/android-
     * semitransparent-bitmap-background-is-black/14858913#14858913
     */
    private Bitmap adjustOpacity(Bitmap bitmap, int opacity) {
        Bitmap mutableBitmap = bitmap.isMutable() ? bitmap : bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        int colour = (opacity & 0xFF) << 24;
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN);
        return mutableBitmap;
    }
}
