package igrek.forceawaken.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import igrek.forceawaken.alarm.AlarmManagerService
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.SingletonInject
import igrek.forceawaken.persistence.AlarmsPersistenceService
import igrek.forceawaken.system.filesystem.InternalDataService

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, _intent: Intent) {
        LoggerFactory.logger.debug("received BootCompletedReceiver")

        refreshAlarms(context)
    }

    private fun refreshAlarms(context: Context) {
        val internalDataService = InternalDataService(
                context = SingletonInject { context },
        )
        val alarmsPersistenceService = AlarmsPersistenceService(
                internalDataService = SingletonInject { internalDataService },
        )
        val alarmManagerService = AlarmManagerService(
                context = SingletonInject { context },
                alarmsPersistenceService = SingletonInject { alarmsPersistenceService },
        )

        alarmManagerService.replenishAllRepetitiveAlarms()
        LoggerFactory.logger.debug("alarms refreshed")
    }
}
