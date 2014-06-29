package com.game.ghosthunter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class GyroSensorModule extends SensorModule{
	
	public GyroSensorModule(MainActivity mainActivity) {
            super(mainActivity, Sensor.TYPE_GYROSCOPE);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
		//mActivity.setValue(mValues);
	}	
}
