package igrek.forceawaken.alarm

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import igrek.forceawaken.activity.AlarmReceiver
import igrek.forceawaken.info.logger.Logger
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.persistence.AlarmsPersistenceService
import org.joda.time.DateTime

class AlarmManagerService(
        activity: LazyInject<Activity> = appFactory.activity,
        alarmsPersistenceService: LazyInject<AlarmsPersistenceService> = appFactory.alarmsPersistenceService,
) {
    private val activity by LazyExtractor(activity)
    private val alarmsPersistenceService by LazyExtractor(alarmsPersistenceService)

    private val alarmManager = activity.get().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val logger: Logger = LoggerFactory.logger

    fun setAlarmOnTime(triggerTime: DateTime) {
        val intent = Intent(activity.applicationContext, AlarmReceiver::class.java)
        intent.addCategory("android.intent.category.DEFAULT")
        val millis: Long = triggerTime.millis
        val id = millis.toInt() // unique to enable multiple alarms
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(activity.applicationContext, id, intent, PendingIntent.FLAG_ONE_SHOT)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent)

        // save creation information in external place
        alarmsPersistenceService.addAlarmTrigger(AlarmTrigger(triggerTime, true, null))
    }

    fun cancelAlarm(triggerTime: DateTime, pendingIntent: PendingIntent?) {
        logger.debug("cancelling alarm: " + triggerTime.toString("HH:mm:ss, yyyy-MM-dd"))
        val intent = Intent(activity.applicationContext, AlarmReceiver::class.java)
        intent.addCategory("android.intent.category.DEFAULT")
        val millis: Long = triggerTime.getMillis()
        val id = millis.toInt() // unique to enable multiple alarms
        val p1: PendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_ONE_SHOT)
        alarmManager.cancel(p1)
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun isAlarmActive(triggerTime: DateTime): Boolean {
        // WTF? returning true even after cancelling
        val intent = Intent(activity.getApplicationContext(), AlarmReceiver::class.java)
        intent.addCategory("android.intent.category.DEFAULT")
        val millis: Long = triggerTime.getMillis()
        val id = millis.toInt() // unique to enable multiple alarms
        return PendingIntent.getBroadcast(
            activity.getApplicationContext(),
            id,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null
    }

    fun nextAlarm(): Long? {
        return alarmManager.nextAlarmClock?.triggerTime
    }

}