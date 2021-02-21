package igrek.forceawaken.sensors

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory

class AccelerometerService(
        activity: LazyInject<Activity> = appFactory.activity,
) : SensorEventListener {
    private val activity by LazyExtractor(activity)

    private val sensorManager: SensorManager = activity.get().getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var xValue = 0f
    private var yValue = 0f
    private var zValue = 0f
    private var accelerometer: Sensor? = null
    override fun onSensorChanged(event: SensorEvent) {
        val mySensor: Sensor = event.sensor
        if (mySensor.type == Sensor.TYPE_ACCELEROMETER) {
            xValue = event.values.get(0)
            yValue = event.values.get(1)
            zValue = event.values.get(2)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    val isSpeakerRotatedDown: Boolean?
        get() = if (accelerometer == null) null else zValue > 0

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
}