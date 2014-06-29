package com.game.ghosthunter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class MotionSensorModule extends SensorModule{
	
	public MotionSensorModule(MainActivity mainActivity) {
            super(mainActivity, Sensor.TYPE_ACCELEROMETER);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
	}	
}
