package com.game.ghosthunter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class LightSensorModule implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mLightSensor;
	private float[] mValues;
	
	private MainActivity mActivity;
	
	public LightSensorModule(MainActivity mainActivity) {
		mSensorManager = mainActivity.getSensorManager();
		mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		mValues = new float[1];
		mActivity = mainActivity;
		register();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// we don't know what to write here.
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		mValues = event.values;
		mActivity.setValue(mValues);
	}	
	
	public void register() {
		mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	
	public void unregister() {
		mSensorManager.unregisterListener(this);
	}
	
	public float[] getValues() {
		return mValues;
	}
}
