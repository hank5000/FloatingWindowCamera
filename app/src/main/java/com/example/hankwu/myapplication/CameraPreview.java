package com.example.hankwu.myapplication;


import java.io.IOException;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private long count = 0;
    private long startTime = -1;
    private int FPS = 0;

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }


    public CameraPreview(Context context) {
        super(context);
        mCamera = Camera.open(findBackFacingCamera());

        mHolder = getHolder();
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
//                Log.d("HANK", "previewFrame:"+(count++)+", byte array size:"+bytes.length);
                if(startTime==-1)
                    startTime = System.currentTimeMillis();

                FPS++;
                if((System.currentTimeMillis()-startTime)>=1000) {
                    Log.d("HANK","FPS:"+FPS);

                    Camera.Size size = camera.getParameters().getPreviewSize();
                    Log.d("HANK","size:"+size.width+"x"+size.height);
                    Log.d("HANK","format:"+camera.getParameters().getPreviewFormat());

                    FPS = 0;
                    startTime = System.currentTimeMillis();
                }

            }
        });

        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // create the surface and start camera preview
            if (mCamera != null) {
                Camera.Parameters parameters = mCamera.getParameters();

                Log.d("HANK","===== Supported Preview Size =======");
                for (Camera.Size s :mCamera.getParameters().getSupportedPreviewSizes()) {
                    Log.d("HANK",s.width+"x"+s.height);
                }
                Log.d("HANK","===== Supported Preview Size =======");
                
                parameters.setPreviewSize(640,480);
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        refreshCamera(mCamera);
    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // mCamera.release();

    }
}