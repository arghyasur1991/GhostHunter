package com.game.ghosthunter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity{
    
    private GLCameraSurfaceView glSurfaceView;
    
    private SensorManager mSensorManager;
    private LightSensorModule mLightSensorModule;
    private GyroSensorModule mGyroSensorModule;
    
    private int width;
    private int height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLightSensorModule = new LightSensorModule(this);
        mGyroSensorModule = new GyroSensorModule(this);
        
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
        mLightSensorModule.unregister();
        super.onPause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	mLightSensorModule.register();
    }
    
    public void setValue(float[] value) {
    	 TextView tView = (TextView) findViewById(R.id.LightValue);
         tView.setText(String.format("%.2f %.2f, %.2f" , value[0] , value[1] , value[2]));
    }
    
    public SensorManager getSensorManager() {
    	return mSensorManager;
    }
}