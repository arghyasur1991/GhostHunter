package com.game.ghosthunter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class LightSensorModule extends SensorModule{
	
	public LightSensorModule(MainActivity mainActivity) {
            super(mainActivity, Sensor.TYPE_LIGHT);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
		//mActivity.setValue(mValues);
	}	
}
