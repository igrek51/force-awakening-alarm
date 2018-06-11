package igrek.forceawaken.service.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.logger.Logger;

public class AccelerometerService implements SensorEventListener {
	
	@Inject
	Activity activity;
	
	private float xValue, yValue, zValue;
	private Sensor accelerometer;
	
	@Inject
	Logger logger;
	
	public AccelerometerService() {
		DaggerIOC.getAppComponent().inject(this);
		logger.info("acxc stgatrted");
		SensorManager sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager == null)
			logger.warn("no sensor manager found");
		else {
			accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (accelerometer != null) {
				sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			}
		}
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor mySensor = event.sensor;
		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			xValue = event.values[0];
			yValue = event.values[1];
			zValue = event.values[2];
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	
	}
	
	public Boolean isDeviceRotatedUp() {
		if (accelerometer == null)
			return null;
		return zValue > 0;
	}
}
