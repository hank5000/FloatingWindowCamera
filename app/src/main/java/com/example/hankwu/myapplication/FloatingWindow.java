package com.example.hankwu.myapplication;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by HankWu on 16/3/6.
 */
public class FloatingWindow extends Service {

    private WindowManager wm;
    private LinearLayout ll;
    private LayoutInflater li;
    private FloatingWindow floatingWindow = this;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    Button stop;
    boolean focus = false;
    boolean close = false;

    boolean incre = false;

    Handler handler = null;

    boolean cameraFront = false;
    private Camera mCamera;
    private CameraPreview mPreview;
    private CameraPreviewTextureView mPreview2;

    boolean bHide = false;

    LinearLayout.LayoutParams cameraPreviewParams = null;
    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler(Looper.getMainLooper());

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        ViewGroup.LayoutParams viewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.argb(66, 128, 128, 255));
        ll.setLayoutParams(llParam);

        final View titleBar = li.inflate(R.layout.barlayout,null);
        ll.addView(titleBar);
//        mPreview = new CameraPreview(this);
        mPreview2 = new CameraPreviewTextureView(this);
        ll.addView(mPreview2, 500,500);


        final WindowManager.LayoutParams param = new WindowManager.LayoutParams(width, height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        param.x = 0;
        param.y = 0;
        param.gravity = Gravity.CENTER;

        wm.addView(ll, param);

        ((Button)titleBar.findViewById(R.id.focus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!focus) {
                    param.flags = WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE;
                    ((Button) titleBar.findViewById(R.id.focus)).setText("UNFOCUS");
                    wm.updateViewLayout(ll, param);
                    focus = true;
                } else {
                    param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    ((Button) titleBar.findViewById(R.id.focus)).setText("FOCUS");
                    wm.updateViewLayout(ll, param);

                    focus = false;
                }
            }
        });

        ((Button) titleBar.findViewById(R.id.exit)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                wm.removeView(ll);
                stopSelf();
                close = true;

                mCamera.release();
            }
        });

        ((Button) titleBar.findViewById(R.id.hide)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!bHide)
                    ll.updateViewLayout(mPreview2, new LinearLayout.LayoutParams(0,0));
                else
                    ll.updateViewLayout(mPreview2, new LinearLayout.LayoutParams(500,500));

                bHide = !bHide;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!close) {

                    if(incre) {
                        param.alpha += 0.1;
                    } else {
                        param.alpha -= 0.1;
                    }

//                    Log.d("HANK", param.alpha+"");

                    if(param.alpha>=1) {
                        incre = false;
                    } else if(param.alpha<=0) {
                        incre = true;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(!close)
                                wm.updateViewLayout(ll, param);
                        }
                    });

                }
            }
        }).start();

        ll.setOnTouchListener(new View.OnTouchListener() {

            int x, y;
            float touchedX, touchedY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = param.x;
                        y = param.y;

                        touchedX = motionEvent.getRawX();
                        touchedY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        param.x = (int) (x + (motionEvent.getRawX() - touchedX));
                        param.y = (int) (y + (motionEvent.getRawY() - touchedY));
                        wm.updateViewLayout(ll, param);
                }

                return false;
            }

        });

    }

}
