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
import java.util.*

class AlarmManagerService(
    activity: LazyInject<Activity> = appFactory.activity,
    alarmsPersistenceService: LazyInject<AlarmsPersistenceService> = appFactory.alarmsPersistenceService,
) {
    private val activity by LazyExtractor(activity)
    private val alarmsPersistenceService by LazyExtractor(alarmsPersistenceService)

    private val alarmManager =
        activity.get().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val logger: Logger = LoggerFactory.logger
    private val random = Random()

    fun setAlarmOnTime(triggerTime: DateTime) {
        ensureAlarmIsOn(triggerTime)
        // save creation information in external place
        alarmsPersistenceService.addAlarmTrigger(AlarmTrigger(triggerTime, true, null))
    }

    private fun ensureAlarmIsOn(triggerTime: DateTime) {
        val intent = Intent(activity.applicationContext, AlarmReceiver::class.java)
        // intent.addCategory("android.intent.category.DEFAULT")
        val millis: Long = triggerTime.millis
        val id = millis.toInt() // unique to enable multiple alarms
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            activity.applicationContext,
            id,
            intent,
            0, // PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent)
    }

    fun cancelAlarm(alarmTrigger: AlarmTrigger) {
        val triggerTime = alarmTrigger.triggerTime
        val pendingIntent = alarmTrigger.pendingIntent
        logger.debug("cancelling alarm: " + triggerTime.toString("HH:mm:ss, yyyy-MM-dd"))
        val intent = Intent(activity.applicationContext, AlarmReceiver::class.java)
//        intent.addCategory("android.intent.category.DEFAULT")
        val millis: Long = triggerTime.millis
        val id = millis.toInt() // unique to enable multiple alarms
        val p1: PendingIntent = PendingIntent.getBroadcast(
            activity.applicationContext,
            id,
            intent,
            0, // PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(p1)
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
        alarmsPersistenceService.removeAlarmTrigger(alarmTrigger)
    }

    fun setSingleAlarmSnoozed(
        _triggerTime: DateTime,
        snoozes: Int,
        snoozeInterval: Int,
        earlyMinutes: Int
    ) {
        var triggerTime = _triggerTime
        // subtract random minutes
        if (earlyMinutes > 0) {
            val newTriggerTime: DateTime =
                triggerTime.minusMinutes(random.nextInt(earlyMinutes + 1))
            if (newTriggerTime.isAfterNow) { // check validity
                triggerTime = newTriggerTime
            }
        }
        for (r in 0 until snoozes) {
            val triggerTime2: DateTime = triggerTime.plusSeconds(r * snoozeInterval)
            setAlarmOnTime(triggerTime2)
        }
    }

    fun replenishAllRepetitiveAlarms() {
        val alarmsConfig: AlarmsConfig = alarmsPersistenceService.readAlarmsConfig()
        alarmsConfig.alarmTriggers.forEach {
            if (it.triggerTime.isAfterNow) {
                ensureAlarmIsOn(it.triggerTime)
            }
        }
        alarmsConfig.repetitiveAlarms.forEach {
            replenishRepetitiveAlarmWithConfig(it, alarmsConfig)
        }
    }

    fun replenishOneRepetitiveAlarm(repetitiveAlarm: RepetitiveAlarm) {
        val alarmsConfig: AlarmsConfig = alarmsPersistenceService.readAlarmsConfig()
        replenishRepetitiveAlarmWithConfig(repetitiveAlarm, alarmsConfig)
    }

    private fun replenishRepetitiveAlarmWithConfig(
        repetitiveAlarm: RepetitiveAlarm,
        alarmsConfig: AlarmsConfig
    ) {
        val alarmTriggers: MutableList<AlarmTrigger> = alarmsConfig.alarmTriggers
        if (!isAlarmActive(repetitiveAlarm, alarmTriggers)) {
            val nextTriggerTime = repetitiveAlarm.getNextTriggerTime()
            setSingleAlarmSnoozed(
                nextTriggerTime, repetitiveAlarm.snoozes,
                repetitiveAlarm.snoozeInterval, repetitiveAlarm.earlyMinutes
            )
            logger.info("Repetitive alarm ($repetitiveAlarm) rescheduled at $nextTriggerTime")
        }
    }

    private fun isAlarmActive(
        repetitiveAlarm: RepetitiveAlarm,
        alarmTriggers: MutableList<AlarmTrigger>
    ): Boolean {
        val snoozes = (repetitiveAlarm.snoozes - 1).lowCap(0)
        val baseTime = repetitiveAlarm.getNextTriggerTime()
        val minTriggerTime = baseTime.minusMinutes(repetitiveAlarm.earlyMinutes)
        val maxTriggerTime = baseTime.plusSeconds(repetitiveAlarm.snoozeInterval * snoozes)
        return alarmTriggers.any {
            it.triggerTime.isBetween(minTriggerTime, maxTriggerTime) && it.isActive
        }
    }

    fun nextAlarm(): Long? {
        return alarmManager.nextAlarmClock?.triggerTime
    }

}

fun DateTime.isBeforeOrEqual(d2: DateTime): Boolean {
    return this.isBefore(d2) || this == d2
}

fun DateTime.isAfterOrEqual(d2: DateTime): Boolean {
    return this.isAfter(d2) || this == d2
}

fun DateTime.isBetween(d1: DateTime, d2: DateTime): Boolean {
    return this.isAfterOrEqual(d1) && this.isBeforeOrEqual(d2)
}

fun Int.lowCap(lowLimit: Int): Int {
    if (this < lowLimit)
        return lowLimit
    return this
}