package com.game.ghosthunter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import java.util.Date;

public class MotionSensorModule extends SensorModule{    
    private final float alpha = 0.35f;
    
    private static float[] currentAcceleration = {0, 0, 0};
    private static float[] velocity = {0, 0, 0};
    private static float[] prevVelocity = {0, 0, 0};
    private static float[] distance = {0, 0, 0};
    private static float[] gravity = {0, 0, 0};
    private Date lastUpdate;

    public MotionSensorModule(MainActivity mainActivity) {
        super(mainActivity, Sensor.TYPE_ACCELEROMETER);
        
        lastUpdate = new Date(System.currentTimeMillis());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        super.onSensorChanged(event);
        applyFilter(gravity, mValues);
        currentAcceleration[0] = mValues[0] - gravity[0];
        currentAcceleration[1] = mValues[1] - gravity[1];
        currentAcceleration[2] = mValues[2] - gravity[2];
        updateVelocity();
        mActivity.setValue(velocity);
    }	

    private float round(float a) {
        int round = 10;
        float b = (float) (Math.floor(Math.abs(a) * round)) / round;
        return b * Math.signum(a);
    }

    private void updateVelocity() {
        // Calculate how long this acceleration has been applied.
        Date timeNow = new Date(System.currentTimeMillis());
        long timeDelta = timeNow.getTime() - lastUpdate.getTime();

        lastUpdate.setTime(timeNow.getTime());

        // Calculate the change in velocity at the 
        // current acceleration since the last update. 
        float[] deltaVelocity = {0, 0, 0};
        float timeDeltaInSec = (float) (timeDelta) / 1000;
        deltaVelocity[0] = round(timeDeltaInSec * currentAcceleration[0]);
        deltaVelocity[1] = round(timeDeltaInSec * currentAcceleration[1]);
        deltaVelocity[2] = round(timeDeltaInSec * currentAcceleration[2]);

        // Add the velocity change to the current velocity.
        velocity[0] += deltaVelocity[0];
        velocity[1] += deltaVelocity[1];
        velocity[2] += deltaVelocity[2];

        updateDistance(timeDeltaInSec);
    }

    private void updateDistance(float timeDelta) {
        float scale = 100;
        //distance[0] += round(scale * (prevVelocity[0] * timeDelta + 0.5f * currentAcceleration[0] * timeDelta * timeDelta));
        //distance[1] += round(scale * (prevVelocity[1] * timeDelta + 0.5f * currentAcceleration[1] * timeDelta * timeDelta));
        //distance[2] += round(scale * (prevVelocity[2] * timeDelta + 0.5f * currentAcceleration[2] * timeDelta * timeDelta));
        distance[0] += round(scale * velocity[0] * timeDelta);
        distance[1] += round(scale * velocity[1] * timeDelta);
        distance[2] += round(scale * velocity[2] * timeDelta);
        prevVelocity = velocity;
    }

    private void applyFilter(float[] value, float[] rawValue) {
        for (int i = 0; i < value.length; i++) {
            value[i] = round(alpha * round(rawValue[i]) + (1 - alpha) * value[i]);
        }
    }
}
