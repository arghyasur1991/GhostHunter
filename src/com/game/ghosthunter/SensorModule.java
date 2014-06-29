package com.game.ghosthunter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class SensorModule implements SensorEventListener{

	private final SensorManager mSensorManager;
	protected final Sensor mSensor;
	protected float[] mValues;
	
	protected final MainActivity mActivity;
	
	public SensorModule(MainActivity mainActivity, int type) {
		mSensorManager = mainActivity.getSensorManager();
		mSensor = mSensorManager.getDefaultSensor(type);
		mValues = new float[3];
		mActivity = mainActivity;
		register();
	}
	
	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// we don't know what to write here.
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		mValues = event.values;
	}	
	
	public final void register() {
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	
	public final void unregister() {
		mSensorManager.unregisterListener(this);
	}
	
	public final float[] getValues() {
		return mValues;
	}
}
