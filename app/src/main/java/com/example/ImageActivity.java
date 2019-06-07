package com.example;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

public class ImageActivity extends AppCompatActivity {
    private Bitmap bitmapSave;
    private float xCoOrdinate, yCoOrdinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        final RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);

        final Button btnAddText = findViewById(R.id.btn_add_text);
        final ImageView iv = findViewById(R.id.iv);

        Button btnSaveImage = findViewById(R.id.btn_save_image);

        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ImageActivity.this);
                final EditText edittext = new EditText(ImageActivity.this);
                edittext.setTextSize(25);
                alert.setTitle("Canvas Demo");
                alert.setMessage("Add Text to Canvas");
                alert.setView(edittext);
                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = edittext.getText().toString();
                        View v = new CanvasWithText(getApplicationContext(), str);
                        Bitmap bitmap = Bitmap.createBitmap(500/*width*/, 500/*height*/, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        v.draw(canvas);
                        bitmap = bitmapSave;     //for saving "b" to the sdcard

                       /* ImageView iv = new ImageView(ImageActivity.this);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.BELOW, btnAddText.getId());
                        // Add layout parameters to ImageView
                        iv.setLayoutParams(lp);*/

                        iv.setOnTouchListener(onTouchListener);
                        iv.setImageBitmap(bitmap);
                        //relativeLayout.addView(iv);
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

                File output = new File(dir, "canvasdemo.jpg");
                OutputStream os;

                try {
                    os = new FileOutputStream(output);
                    bitmapSave.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();

                    final Handler handler = new Handler();
                    //this code will scan the image so that it will appear in your gallery when you open next time
                    MediaScannerConnection.scanFile(ImageActivity.this, new String[]{output.toString()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ImageActivity.this, "Your image has been saved in gallery at " + dir.getPath(), Toast.LENGTH_LONG).show();
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
                default:
                    return false;
            }
            return true;
        }
    };

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
            pText.setColor(Color.RED);
            pText.setTextSize(60);

            canvas.save();
            canvas.drawText(str, 250, 250, pText);
            canvas.restore();
        }
    }
}

