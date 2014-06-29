/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.game.ghosthunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 *
 * @author sur
 */
public class GLCameraSurfaceView extends GLSurfaceView {

    GLCameraRenderer renderer;

    public GLCameraSurfaceView(Context context) {
        super(context);
        
        setZOrderMediaOverlay(true);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        
        getHolder().setFormat(PixelFormat.RGBA_8888);
        
        renderer = new GLCameraRenderer((MainActivity) context);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public GLCameraRenderer getRenderer() {
        return renderer;
    }
    
    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (renderer != null) {
                    // Ensure we call switchMode() on the OpenGL thread.
                    // queueEvent() is a method of GLSurfaceView that will do this for us.
                    
                    final Point p = new Point();
                    p.x = (int)event.getX();
                    p.y = (int)event.getY();
                    
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            
                        }
                    });

                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }
    */
    
    public void release() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.release();
            }
        });
    }
}
