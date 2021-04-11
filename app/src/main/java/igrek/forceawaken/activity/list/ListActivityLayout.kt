package igrek.forceawaken.activity.list

import android.app.Activity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import igrek.forceawaken.R
import igrek.forceawaken.alarm.AlarmManagerService
import igrek.forceawaken.alarm.AlarmTrigger
import igrek.forceawaken.alarm.AlarmsConfig
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.layout.CommonLayout
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

    private var alramTriggerList: ListView? = null
    private var alramTriggerListAdapter: ArrayAdapter<AlarmTrigger>? = null

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

        var alarmsConfig = alarmsPersistenceService.readAlarmsConfig()
        var alarmTriggers = alarmsConfig.alarmTriggers

        // check alarm triggers are still valid
        val inactive = alarmTriggers.filter { it.triggerTime.isBefore(DateTime.now()) }
        for (inactiveAlarmTrigger in inactive) {
            alarmsConfig = alarmsPersistenceService.removeAlarmTrigger(inactiveAlarmTrigger)
        }
        alarmTriggers = alarmsConfig.alarmTriggers

        alramTriggerListAdapter = ArrayAdapter<AlarmTrigger>(activity, R.layout.list_item, alarmTriggers)
        alramTriggerList = activity.findViewById(R.id.alramTriggerList)
        alramTriggerList!!.adapter = alramTriggerListAdapter
        alramTriggerList!!.onItemClickListener = AdapterView.OnItemClickListener { adapter1: AdapterView<*>, v: View?, position: Int, id: Long ->
            val selected: AlarmTrigger = adapter1.getItemAtPosition(position) as AlarmTrigger
            selected.isActive = false
            alarmManagerService.cancelAlarm(selected.triggerTime, selected.pendingIntent)
            val alarmsConfig2: AlarmsConfig = alarmsPersistenceService.removeAlarmTrigger(selected)
            alramTriggerListAdapter?.clear()
            alramTriggerListAdapter?.addAll(alarmsConfig2.alarmTriggers)
            alramTriggerListAdapter?.notifyDataSetChanged()
        }

        logger.info(activity.javaClass.simpleName + " has been created")
    }

}
