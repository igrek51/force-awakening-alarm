package igrek.forceawaken.activity.schedule

import android.app.Activity
import android.widget.Button
import igrek.forceawaken.R
import igrek.forceawaken.alarm.AlarmManagerService
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.layout.CommonLayout
import igrek.forceawaken.layout.navigation.NavigationMenuController
import igrek.forceawaken.system.WindowManagerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class ScheduleActivityLayout(
        activity: LazyInject<Activity> = appFactory.activity,
        windowManagerService: LazyInject<WindowManagerService> = appFactory.windowManagerService,
        navigationMenuController: LazyInject<NavigationMenuController> = appFactory.navigationMenuController,
        alarmManagerService: LazyInject<AlarmManagerService> = appFactory.alarmManagerService,
        uiInfoService: LazyInject<UiInfoService> = appFactory.uiInfoService,
        commonLayout: LazyInject<CommonLayout> = appFactory.commonLayout,
) {
    private val activity by LazyExtractor(activity)
    private val windowManagerService by LazyExtractor(windowManagerService)
    private val navigationMenuController by LazyExtractor(navigationMenuController)
    private val alarmManagerService by LazyExtractor(alarmManagerService)
    private val uiInfoService by LazyExtractor(uiInfoService)
    private val commonLayout by LazyExtractor(commonLayout)

    private val logger = LoggerFactory.logger

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
        activity.setContentView(R.layout.screen_schedule)
        commonLayout.init()
        navigationMenuController.init()

        activity.findViewById<Button>(R.id.btnSetNap25)?.setOnClickListener { _ ->
            setAlarmOnTime(DateTime.now().plusMinutes(25))
        }

        activity.findViewById<Button>(R.id.btnTestAlarm)?.setOnClickListener { _ ->
            setAlarmOnTime(DateTime.now().plusSeconds(3), repeats = 3, repeatsInterval = 5)
        }

        activity.findViewById<Button>(R.id.btnTestAlarm1)?.setOnClickListener { _ ->
            setAlarmOnTime(DateTime.now().plusSeconds(3))
        }

        activity.findViewById<Button>(R.id.btnTestAlarm2)?.setOnClickListener { _ ->
            setAlarmOnTime(DateTime.now().plusMinutes(1))
        }

        logger.info(activity.javaClass.simpleName + " has been created")
    }

    private fun setAlarmOnTime(triggerTime: DateTime, repeats: Int = 1, repeatsInterval: Int = 0) {
        // multiple alarms at once
        for (r in 0 until repeats) {
            val triggerTime2: DateTime = triggerTime.plusSeconds(r * repeatsInterval)
            alarmManagerService.setAlarmOnTime(triggerTime2)
        }
        uiInfoService.showToast(repeats.toString() + " Alarm set on " + triggerTime.toString("yyyy-MM-dd"))
    }

}
