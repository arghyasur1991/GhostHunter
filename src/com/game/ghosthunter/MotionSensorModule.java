package com.game.ghosthunter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import java.util.Date;

public class MotionSensorModule extends SensorModule{
    private static boolean calibrating = true;
    private static int calibration_counter = 0;
    private final int calibration_threshold = 1000;
    private static float[] prevSensorValues = {0, 0, 0};
    
    private final float alpha = 0.5f;
    private final float threshold = 0.05f;
    private static final int time_threshold = 1000 * 60 * 2;
    
    private static int accXZeroCount = 0;
    private static int accYZeroCount = 0;
    private static int accZZeroCount = 0;
    private static float[] offset = {0, 0, 0};
//	private static float[] linear_acceleration = {0, 0, 0};

    private static float[] appliedAcceleration = {0, 0, 0};
    private static float[] currentAcceleration = {0, 0, 0};
    private static float[] velocity = {0, 0, 0};
    private static float[] prevVelocity = {0, 0, 0};
    private static float[] distance = {0, 0, 0};
    private Date lastUpdate;

    public MotionSensorModule(MainActivity mainActivity) {
        super(mainActivity, Sensor.TYPE_LINEAR_ACCELERATION);
        
        lastUpdate = new Date(System.currentTimeMillis());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        super.onSensorChanged(event);
        applyFilter(currentAcceleration, mValues);
        if (!calibrating) {
//					currentAcceleration[0] = round(alpha * round(sensorData[9][0]) - offset[0] + (1 - alpha) * currentAcceleration[0]);
//					currentAcceleration[1] = round(alpha * round(sensorData[9][1]) - offset[1] + (1 - alpha) * currentAcceleration[1]);
//					currentAcceleration[2] = round(alpha * round(sensorData[9][2]) - offset[2] + (1 - alpha) * currentAcceleration[2]);
            currentAcceleration[0] -= offset[0];
            currentAcceleration[1] -= offset[1];
            currentAcceleration[2] -= offset[2];
            //currentAcceleration = sensorData[5];
            updateVelocity();
        } else {
            if (prevSensorValues[0] == currentAcceleration[0]
                    && prevSensorValues[1] == currentAcceleration[1]
                    && prevSensorValues[2] == currentAcceleration[2]) {
                calibration_counter++;
                if (calibration_counter == calibration_threshold) {
                    calibrating = false;
                    offset[0] = currentAcceleration[0];
                    offset[1] = currentAcceleration[1];
                    offset[2] = currentAcceleration[2];
                }
            } else {
                calibration_counter = 0;
            }
            prevSensorValues = currentAcceleration;
        }
        
        mActivity.setValue(distance);
    }	

    private float round(float a) {
        int round = 10;
        float b = (float) (Math.floor(Math.abs(a) * round)) / round;
        return b * Math.signum(a);
    }

    private float round(float a, int round) {
        float b = (float) (Math.floor(Math.abs(a) * round)) / round;
        return b * Math.signum(a);
    }

    private void updateVelocity() {
        // Calculate how long this acceleration has been applied.
        Date timeNow = new Date(System.currentTimeMillis());
        long timeDelta = timeNow.getTime() - lastUpdate.getTime();

//    	c = (int)timeDelta;
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

        if (currentAcceleration[0] == 0) {
            accXZeroCount++;
            if (accXZeroCount == 100) {
                velocity[0] = 0;
            }
        } else {
            accXZeroCount = 0;
        }
        if (currentAcceleration[1] == 0) {
            accYZeroCount++;
            if (accYZeroCount == 100) {
                velocity[1] = 0;
            }
        } else {
            accYZeroCount = 0;
        }
        if (currentAcceleration[2] == 0) {
            accZZeroCount++;
            if (accZZeroCount == 100) {
                velocity[2] = 0;
            }
        } else {
            accZZeroCount = 0;
        }

        updateDistance(timeDeltaInSec);
        //appliedAcceleration = currentAcceleration;
    }

    private void updateDistance(float timeDelta) {
        float scale = 100;
        distance[0] += round(scale * (prevVelocity[0] * timeDelta + 0.5f * currentAcceleration[0] * timeDelta * timeDelta));
        distance[1] += round(scale * (prevVelocity[1] * timeDelta + 0.5f * currentAcceleration[1] * timeDelta * timeDelta));
        distance[2] += round(scale * (prevVelocity[2] * timeDelta + 0.5f * currentAcceleration[2] * timeDelta * timeDelta));
        prevVelocity = velocity;
    }

    private void applyFilter(float[] value, float[] rawValue) {
        for (int i = 0; i < value.length; i++) {
            value[i] = round(alpha * round(rawValue[i]) + (1 - alpha) * value[i]);
        }
    }

    public static boolean isCalibrating() {
        return calibrating;
    }

    public static float[] getOffset() {
        return offset;
    }
}
