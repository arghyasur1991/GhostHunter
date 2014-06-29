/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.game.ghosthunter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 *
 * @author sur
 */
public class CameraSurface implements SurfaceTexture.OnFrameAvailableListener {
    private SurfaceTexture mSurface;
    private int mTexture;
    private Camera mCamera;
    private static MainActivity mContext;
    
    public CameraSurface(Context context) {
        mContext = (MainActivity) context;
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mContext.requestRender();
    }
    
    public void start(int texture) {
        mTexture = texture;
        mSurface = new SurfaceTexture(mTexture);
        mSurface.setOnFrameAvailableListener(this);
        
        mCamera = getCameraInstance();

        try {
            mCamera.setPreviewTexture(mSurface);
            mCamera.startPreview();
        } catch (IOException ioe) {
            //Log.w("MainActivity", "CAM LAUNCH FAILED");
        }
    }
    
    public void updateSurface(float[] mSTMatrix) {
        mSurface.updateTexImage();
        mSurface.getTransformMatrix(mSTMatrix);
    }
    
    public long getTimeStamp() {
        return mSurface.getTimestamp();
    }
    /**
     * A safe way to get an instance of the Camera object.
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    
    public void release() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mSurface.release();
    }
}
