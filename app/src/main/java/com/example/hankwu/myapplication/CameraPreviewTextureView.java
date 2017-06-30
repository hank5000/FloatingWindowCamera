package com.example.hankwu.myapplication;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.IOException;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

public class CameraPreviewTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private Camera mCamera;
    private long count = 0;
    private long startTime = -1;
    private int FPS = 0;
    private SurfaceTexture mSurfaceTexture;

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

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            mSurfaceTexture = surfaceTexture;
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
                mCamera.setPreviewTexture(surfaceTexture);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        refreshCamera(mCamera);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    int surfaceTextureUpdatedFPS = 0;
    long surfaceTextureStartTime = -1;
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        if(surfaceTextureStartTime==-1) surfaceTextureStartTime = System.currentTimeMillis();

        surfaceTextureUpdatedFPS++;
        if((System.currentTimeMillis()-surfaceTextureStartTime)>=1000) {
            surfaceTextureStartTime = System.currentTimeMillis();
            Log.d("HANK","SurfaceTextureUpdatedFPS:"+surfaceTextureUpdatedFPS);
            surfaceTextureUpdatedFPS = 0;
        }


    }

    public CameraPreviewTextureView(Context context) {
        super(context);
        mCamera = Camera.open(findBackFacingCamera());
        mSurfaceTexture = getSurfaceTexture();
        this.setSurfaceTextureListener(this);

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
//                    Log.d("HANK","size:"+size.width+"x"+size.height);
//                    Log.d("HANK","format:"+camera.getParameters().getPreviewFormat());

                    FPS = 0;
                    startTime = System.currentTimeMillis();
                }


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });



    }


    public void refreshCamera(Camera camera) {

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
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();

            Log.d("HANK","===== Supported Preview Size =======");
            for (Camera.Size s :mCamera.getParameters().getSupportedPreviewSizes()) {
                Log.d("HANK",s.width+"x"+s.height);
            }
            Log.d("HANK","===== Supported Preview Size =======");

            parameters.setPreviewSize(640,480);
            try {
                mCamera.setParameters(parameters);
                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
    }
}