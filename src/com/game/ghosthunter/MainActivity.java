package com.game.ghosthunter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity{
    
    private GLCameraSurfaceView glSurfaceView;
    
    private int width;
    private int height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        glSurfaceView = new GLCameraSurfaceView(this);
        
        setContentView(R.layout.main);
        
        FrameLayout layout = (FrameLayout) findViewById(R.id.mainFrame);
        
        layout.addView(glSurfaceView);
        
        RelativeLayout newLayout = (RelativeLayout) findViewById(R.id.UILayout);
        
        layout.bringChildToFront(newLayout);
    }
    
    synchronized public void requestRender() {
        glSurfaceView.requestRender();
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    @Override
    public void onPause() {
        glSurfaceView.release();
        System.exit(0);
    }
}