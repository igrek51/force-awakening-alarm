package igrek.forceawaken.alarm

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
        context: LazyInject<Context> = appFactory.context,
        alarmsPersistenceService: LazyInject<AlarmsPersistenceService> = appFactory.alarmsPersistenceService,
) {
    private val context by LazyExtractor(context)
    private val alarmsPersistenceService by LazyExtractor(alarmsPersistenceService)

    private val alarmManager =
            context.get().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val logger: Logger = LoggerFactory.logger
    private val random = Random()

    enum class ScheduleMethod {
        EXACT_IDLE_RTC_WAKEUP,
        SET_ALARM_CLOCK,
        ;
    }

    private val scheduleMethod = ScheduleMethod.SET_ALARM_CLOCK

    fun setAlarmOnTime(triggerTime: DateTime) {
        ensureAlarmIsOn(triggerTime)
        // save creation information in external place
        alarmsPersistenceService.addAlarmTrigger(AlarmTrigger(triggerTime, true, null))
    }

    private fun ensureAlarmIsOn(triggerTime: DateTime) {
        when (scheduleMethod) {
            ScheduleMethod.EXACT_IDLE_RTC_WAKEUP -> {
                val showIntent = Intent(context.applicationContext, AlarmReceiver::class.java)
                // intent.addCategory("android.intent.category.DEFAULT")
                val millis: Long = triggerTime.millis
                val id = millis.toInt() // unique to enable multiple alarms
                val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                        context.applicationContext,
                        id,
                        showIntent,
                        0, // PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent)
            }
            ScheduleMethod.SET_ALARM_CLOCK -> {
                val showIntent = Intent(context.applicationContext, AlarmReceiver::class.java)
                val millis: Long = triggerTime.millis
                val id = millis.toInt() // unique to enable multiple alarms
                val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                        context.applicationContext,
                        id,
                        showIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT,
                )
                val info: AlarmManager.AlarmClockInfo = AlarmManager.AlarmClockInfo(millis, pendingIntent)
                alarmManager.setAlarmClock(info, pendingIntent)
            }
        }
    }

    fun cancelAlarm(alarmTrigger: AlarmTrigger) {
        val triggerTime = alarmTrigger.triggerTime
        val pendingIntent = alarmTrigger.pendingIntent
        logger.debug("cancelling alarm: " + triggerTime.toString("HH:mm:ss, yyyy-MM-dd"))
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
//        intent.addCategory("android.intent.category.DEFAULT")
        val millis: Long = triggerTime.millis
        val id = millis.toInt() // unique to enable multiple alarms
        val p1: PendingIntent = PendingIntent.getBroadcast(
                context.applicationContext,
                id,
                intent,
                PendingIntent.FLAG_NO_CREATE,
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