package igrek.forceawaken.activity

import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.*
import igrek.forceawaken.R
import igrek.forceawaken.alarm.AlarmManagerService
import igrek.forceawaken.alarm.AlarmTrigger
import igrek.forceawaken.alarm.AlarmsConfig
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.info.errorcheck.UiErrorHandler
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.layout.CommonLayout
import igrek.forceawaken.layout.input.TriggerTimeInput
import igrek.forceawaken.layout.navigation.NavigationMenuController
import igrek.forceawaken.persistence.AlarmsPersistenceService
import igrek.forceawaken.system.PermissionService
import igrek.forceawaken.system.WindowManagerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.util.*

class MainActivityLayout(
        activity: LazyInject<Activity> = appFactory.activity,
        windowManagerService: LazyInject<WindowManagerService> = appFactory.windowManagerService,
        navigationMenuController: LazyInject<NavigationMenuController> = appFactory.navigationMenuController,
        alarmManagerService: LazyInject<AlarmManagerService> = appFactory.alarmManagerService,
        uiInfoService: LazyInject<UiInfoService> = appFactory.uiInfoService,
        alarmsPersistenceService: LazyInject<AlarmsPersistenceService> = appFactory.alarmsPersistenceService,
        permissionService: LazyInject<PermissionService> = appFactory.permissionService,
        commonLayout: LazyInject<CommonLayout> = appFactory.commonLayout,
) {
    private val activity by LazyExtractor(activity)
    private val windowManagerService by LazyExtractor(windowManagerService)
    private val navigationMenuController by LazyExtractor(navigationMenuController)
    private val alarmManagerService by LazyExtractor(alarmManagerService)
    private val uiInfoService by LazyExtractor(uiInfoService)
    private val alarmsPersistenceService by LazyExtractor(alarmsPersistenceService)
    private val permissionService by LazyExtractor(permissionService)
    private val commonLayout by LazyExtractor(commonLayout)

    private val logger = LoggerFactory.logger

    private val random = Random()
    private var btnSet: Button? = null
    private var btnTestAlarm: Button? = null
    private var alarmTimeInput: TriggerTimeInput? = null
    private var nowDateTime: TextView? = null
    private var earlyMarginInput: EditText? = null
    private var alarmRepeatsInput: EditText? = null
    private var alarmRepeatsIntervalInput: EditText? = null
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
        activity.setContentView(R.layout.screen_setup)
        commonLayout.init()
        navigationMenuController.init()

        btnSet = activity.findViewById(R.id.btnSetAlarm)
        btnTestAlarm = activity.findViewById(R.id.btnTestAlarm)
        alarmTimeInput = activity.findViewById(R.id.alarmTimeInput)
        earlyMarginInput = activity.findViewById(R.id.earlyMarginInput)
        alarmRepeatsInput = activity.findViewById(R.id.alarmRepeatsInput)
        alarmRepeatsIntervalInput = activity.findViewById(R.id.alarmRepeatsIntervalInput)
        nowDateTime = activity.findViewById(R.id.nowDateTime)
        btnSet?.setOnClickListener { v: View? ->
            try {
                val triggerTime: DateTime = buildFinalTriggerTime()
                setAlarmOnTime(triggerTime)
            } catch (t: Throwable) {
                UiErrorHandler().handleError(t)
            }
        }
        btnTestAlarm!!.setOnClickListener { v: View? -> setAlarmOnTime(DateTime.now().plusSeconds(3)) }

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

        // refreshing current time
        val someHandler = Handler(activity.mainLooper)
        someHandler.postDelayed(object : Runnable {
            override fun run() {
                nowDateTime?.text = DateTime.now().toString("HH:mm:ss, yyyy-MM-dd")
                someHandler.postDelayed(this, 1000)
            }
        }, 10)
        alarmTimeInput?.requestFocus()
        // show keyboard
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        permissionService.isMicrophonePermissionGranted
        permissionService.isStoragePermissionGranted
        logger.info(activity.javaClass.simpleName + " has been created")
    }


    private fun buildFinalTriggerTime(): DateTime {
        var triggerTime: DateTime = alarmTimeInput!!.triggerTime
        // subtract random minutes
        if (earlyMarginInput?.length() ?: 0 > 0) {
            val earlyMarginMin: Int = earlyMarginInput?.text.toString().toInt()
            if (earlyMarginMin > 0) {
                val newTriggerTime: DateTime = triggerTime.minusMinutes(random.nextInt(earlyMarginMin + 1))
                if (newTriggerTime.isAfterNow) { // check validity
                    triggerTime = newTriggerTime
                }
            }
        }
        return triggerTime
    }

    private val alarmRepeatsCount: Int
        get() {
            val input: String = alarmRepeatsInput?.text.toString()
            return if (input.isEmpty()) 1 else input.toInt()
        }
    private val alarmRepeatsInterval: Int
        get() {
            val input: String = alarmRepeatsIntervalInput?.text.toString()
            return if (input.isEmpty()) 60 else input.toInt()
        }

    private fun setAlarmOnTime(triggerTime: DateTime) {
        // multiple alarms at once
        val repeats = alarmRepeatsCount
        val repeatsInterval = alarmRepeatsInterval
        for (r in 0 until repeats) {
            val triggerTime2: DateTime = triggerTime.plusSeconds(r * repeatsInterval)
            alarmManagerService.setAlarmOnTime(triggerTime2)
        }
        uiInfoService.showToast(repeats.toString() + " Alarm set on " + triggerTime.toString("yyyy-MM-dd"))
    }

}
