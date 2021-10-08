package igrek.forceawaken.sensors

import android.app.Activity
import android.content.Context
import android.os.Vibrator
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory

class VibratorService(
        activity: LazyInject<Activity> = appFactory.activity,
) {
    private val activity by LazyExtractor(activity)
    private val vibrator = activity.get().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun vibrate(millis: Long) {
        vibrator.vibrate(millis)
    }

}