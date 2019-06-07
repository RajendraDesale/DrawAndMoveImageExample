package com.example;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private DrawCanvas myDrawView;
    private Context mContext = MainActivity.this;
    private static final int REQUEST = 112;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDrawView = findViewById(R.id.draw);
        final Button button1 = findViewById(R.id.button1);
        resources = getResources();

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST);
            }

            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    File folder = new File(Environment.getExternalStorageDirectory().toString());
                    boolean success = false;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    }

                    System.out.println(success + "folder");
                    File file = new File(Environment.getExternalStorageDirectory().toString() + "/sample.png");
                    if (!file.exists()) {
                        try {
                            success = file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println(success + "file");
                    FileOutputStream ostream = null;
                    try {
                        ostream = new FileOutputStream(file);
                        System.out.println(ostream);
                        Bitmap well = myDrawView.getBitmap();
                        Bitmap save = Bitmap.createBitmap(320, 480, Config.ARGB_8888);
                        Paint paint = new Paint();
                        paint.setColor(Color.WHITE);
                        Canvas now = new Canvas(save);
                        now.drawRect(new Rect(0, 0, 320, 480), paint);
                        now.drawBitmap(well, new Rect(0, 0, well.getWidth(), well.getHeight()), new Rect(0, 0, 320, 480), null);

                        if (save == null) {
                            System.out.println("Null bitmap save");
                        }
                        save.compress(Bitmap.CompressFormat.PNG, 100, ostream);

                        final Dialog settingsDialog = new Dialog(MainActivity.this);
                        //settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout, null));
                        ImageView imageView = settingsDialog.findViewById(R.id.openBitmap);
                        Button buttonOk = settingsDialog.findViewById(R.id.cancelDialog);

                        buttonOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                settingsDialog.dismiss();
                            }
                        });
                        imageView.setImageBitmap(save);
                        settingsDialog.show();
                        Toast.makeText(getApplicationContext(), "Save Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        save.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        Intent in1 = new Intent(MainActivity.this, CanvasDemoActivity.class);
                        in1.putExtra("image",byteArray);
                        startActivity(in1);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Null error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}