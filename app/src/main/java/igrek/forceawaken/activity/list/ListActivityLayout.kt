package igrek.forceawaken.activity.list

import android.app.Activity
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import igrek.forceawaken.R
import igrek.forceawaken.alarm.AlarmManagerService
import igrek.forceawaken.alarm.AlarmTrigger
import igrek.forceawaken.alarm.AlarmsConfig
import igrek.forceawaken.alarm.RepetitiveAlarm
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.layout.CommonLayout
import igrek.forceawaken.layout.contextmenu.ContextMenuBuilder
import igrek.forceawaken.layout.listview.AlarmTriggersListView
import igrek.forceawaken.layout.listview.RepetitiveAlarmListView
import igrek.forceawaken.layout.navigation.NavigationMenuController
import igrek.forceawaken.persistence.AlarmsPersistenceService
import igrek.forceawaken.system.WindowManagerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class ListActivityLayout(
        activity: LazyInject<Activity> = appFactory.activity,
        windowManagerService: LazyInject<WindowManagerService> = appFactory.windowManagerService,
        navigationMenuController: LazyInject<NavigationMenuController> = appFactory.navigationMenuController,
        alarmManagerService: LazyInject<AlarmManagerService> = appFactory.alarmManagerService,
        uiInfoService: LazyInject<UiInfoService> = appFactory.uiInfoService,
        alarmsPersistenceService: LazyInject<AlarmsPersistenceService> = appFactory.alarmsPersistenceService,
        commonLayout: LazyInject<CommonLayout> = appFactory.commonLayout,
) {
    private val activity by LazyExtractor(activity)
    private val windowManagerService by LazyExtractor(windowManagerService)
    private val navigationMenuController by LazyExtractor(navigationMenuController)
    private val alarmManagerService by LazyExtractor(alarmManagerService)
    private val uiInfoService by LazyExtractor(uiInfoService)
    private val alarmsPersistenceService by LazyExtractor(alarmsPersistenceService)
    private val commonLayout by LazyExtractor(commonLayout)

    private val logger = LoggerFactory.logger

    private var alramTriggerList: AlarmTriggersListView? = null
    private var repetitiveAlarmList: RepetitiveAlarmListView? = null

    fun init() {
        logger.info("Initializing application...")

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                windowManagerService.hideTaskbar()
                initLayout()
            }

            logger.info("MainActivity Layout has been initialized.")
        }
    }

    private fun initLayout() {
        activity.setContentView(R.layout.screen_list_alarms)
        commonLayout.init()
        navigationMenuController.init()

        alramTriggerList = activity.findViewById(R.id.alramTriggerList)
        alramTriggerList?.init(
            activity,
            onClick = this::onAlarmClicked,
            onLongClick = this::onAlarmClicked,
            onMore = this::onAlarmMoreMenu,
        )
        repetitiveAlarmList = activity.findViewById(R.id.repetitiveAlarmList)
        repetitiveAlarmList?.init(
            activity,
            onLongClick = this::onRepetitiveAlarmClicked,
            onMore = this::onRepetitiveAlarmMoreMenu,
        )
        updateAlarmsList()

        val nowDateTimeText = activity.findViewById<TextView>(R.id.nowDateTime)
        val someHandler = Handler(activity.mainLooper)
        someHandler.postDelayed(object : Runnable {
            override fun run() {
                nowDateTimeText?.text = DateTime.now().toString("HH:mm:ss, yyyy-MM-dd")
                someHandler.postDelayed(this, 1000)
            }
        }, 10)

        val refreshAlarmsButton = activity.findViewById<Button>(R.id.refreshAlarmsButton)
        refreshAlarmsButton?.setOnClickListener { _ ->
            updateAlarmsList()
        }
        val replenishAlarmsButton = activity.findViewById<Button>(R.id.replenishAlarmsButton)
        replenishAlarmsButton?.setOnClickListener { _ ->
            repelnishRepetitiveAlarms()
        }

        logger.info(activity.javaClass.simpleName + " has been created")
    }

    private fun repelnishRepetitiveAlarms() {
        alarmManagerService.replenishAllRepetitiveAlarms()
        updateAlarmsList()
    }

    private fun updateAlarmsList() {
        val alarmsConfig = getAlarmsConfig()
        alramTriggerList?.setItems(alarmsConfig.alarmTriggers)
        repetitiveAlarmList?.setItems(alarmsConfig.repetitiveAlarms)
    }

    private fun getAlarmsConfig(): AlarmsConfig {
        var alarmsConfig = alarmsPersistenceService.readAlarmsConfig()
        val alarmTriggers = alarmsConfig.alarmTriggers

        // check alarm triggers are still valid
        val inactive = alarmTriggers.filter { it.triggerTime.isBefore(DateTime.now()) }
        for (inactiveAlarmTrigger in inactive) {
            alarmsConfig = alarmsPersistenceService.removeAlarmTrigger(inactiveAlarmTrigger)
        }
        return alarmsConfig
    }

    private fun onAlarmClicked(alarmTrigger: AlarmTrigger) {
        removeAlarmTrigger(alarmTrigger)
    }

    private fun removeAlarmTrigger(alarmTrigger: AlarmTrigger) {
        alarmTrigger.isActive = false
        alarmManagerService.cancelAlarm(alarmTrigger)
        updateAlarmsList()
    }

    private fun onRepetitiveAlarmClicked(repetitiveAlarm: RepetitiveAlarm) {
        removeRepetitiveAlarm(repetitiveAlarm)
    }

    private fun removeRepetitiveAlarm(repetitiveAlarm: RepetitiveAlarm) {
        alarmsPersistenceService.removeRepetitiveAlarm(repetitiveAlarm)
        updateAlarmsList()
    }

    private fun resetRepetitiveAlarm(repetitiveAlarm: RepetitiveAlarm) {
        val configAlarm = alarmsPersistenceService.updateRepetitiveAlarm(repetitiveAlarm) {
            this.resetNextTriggerTime()
        }
        alarmManagerService.replenishOneRepetitiveAlarm(configAlarm)
        updateAlarmsList()
    }

    private fun postponeRepetitiveAlarm(repetitiveAlarm: RepetitiveAlarm) {
        val configAlarm = alarmsPersistenceService.updateRepetitiveAlarm(repetitiveAlarm) {
            this.startFromTime = getNextTriggerTime().plusSeconds(1)
            this.startFromTime = getNextTriggerTime()
        }
        alarmManagerService.replenishOneRepetitiveAlarm(configAlarm)
        updateAlarmsList()
    }

    private fun onAlarmMoreMenu(alarmTrigger: AlarmTrigger) {
        ContextMenuBuilder().showContextMenu(
            listOf(
                ContextMenuBuilder.Action(R.string.alarm_trigger_remove) {
                    removeAlarmTrigger(alarmTrigger)
                },
            )
        )
    }

    private fun onRepetitiveAlarmMoreMenu(repetitiveAlarm: RepetitiveAlarm) {
        ContextMenuBuilder().showContextMenu(
            listOf(
                ContextMenuBuilder.Action(R.string.alarm_trigger_remove) {
                    removeRepetitiveAlarm(repetitiveAlarm)
                },
                ContextMenuBuilder.Action(R.string.alarm_trigger_postpone) {
                    postponeRepetitiveAlarm(repetitiveAlarm)
                },
                ContextMenuBuilder.Action(R.string.alarm_repetitive_reset) {
                    resetRepetitiveAlarm(repetitiveAlarm)
                },
            )
        )
    }

}
