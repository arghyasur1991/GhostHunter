/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.game.ghosthunter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import java.util.ArrayList;
import javax.microedition.khronos.opengles.GL10;

/**
 *
 * @author sur
 */
public class GLCameraRenderer implements GLSurfaceView.Renderer {
    ArrayList<Integer> mTextureList;
    MainActivity mDelegate;
    
    private Square mSquare;
    
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private final float[] mSTMatrix = new float[16];
    
    private final CameraSurface mCameraSurface;

    public GLCameraRenderer(MainActivity _delegate) {
        mDelegate = _delegate;
        mTextureList = new ArrayList<Integer>();
        mCameraSurface = new CameraSurface(mDelegate);
    }

    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
        
        createExternalTexture();
        //createExternalTexture();
        
        Shaders chromaKeyShader = new Shaders(this, Shaders.VertexShader.texture(), Shaders.FragmentShader.texture());
        chromaKeyShader.setTextureIndex(0);
        mSquare = new Square(chromaKeyShader);
        
        mCameraSurface.start(mTextureList.get(0));
    }

    synchronized public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        mCameraSurface.updateSurface(mSTMatrix);
        
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        mSquare.draw(mMVPMatrix, mSTMatrix);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {       //Prevent A Divide By Zero By
            height = 1;         //Making Height Equal One
        }
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }
    
    private void createTexture2D() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);

        // Retrieve our image from resources.
        int id = mDelegate.getResources().getIdentifier("drawable/ic_launcher", null, mDelegate.getPackageName());

        // Temporary create a bitmap
        Bitmap bmp = BitmapFactory.decodeResource(mDelegate.getResources(), id);

        // Bind texture to texturename
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mTextureList.size());
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        // We are done using the bitmap so we should recycle it.
        bmp.recycle();
        
        mTextureList.add(texture[0]);
    }

    private void createExternalTexture() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mTextureList.size());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        mTextureList.add(texture[0]);
    }

    public void release() {
        mCameraSurface.release();
    }
}
