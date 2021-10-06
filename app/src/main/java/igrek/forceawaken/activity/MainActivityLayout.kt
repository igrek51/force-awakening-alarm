package igrek.forceawaken.activity

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.*
import igrek.forceawaken.R
import igrek.forceawaken.alarm.AlarmManagerService
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
import igrek.forceawaken.system.SoftKeyboardService
import igrek.forceawaken.system.WindowManagerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.Minutes
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
    softKeyboardService: LazyInject<SoftKeyboardService> = appFactory.softKeyboardService,
) {
    private val activity by LazyExtractor(activity)
    private val windowManagerService by LazyExtractor(windowManagerService)
    private val navigationMenuController by LazyExtractor(navigationMenuController)
    private val alarmManagerService by LazyExtractor(alarmManagerService)
    private val uiInfoService by LazyExtractor(uiInfoService)
    private val alarmsPersistenceService by LazyExtractor(alarmsPersistenceService)
    private val permissionService by LazyExtractor(permissionService)
    private val commonLayout by LazyExtractor(commonLayout)
    private val softKeyboardService by LazyExtractor(softKeyboardService)

    private val logger = LoggerFactory.logger

    private val random = Random()
    private var btnSet: Button? = null
    private var alarmTimeInput: TriggerTimeInput? = null
    private var alarmSlumberLengthInput: EditText? = null
    private var nowDateTime: TextView? = null
    private var earlyMarginInput: EditText? = null
    private var alarmRepeatsInput: EditText? = null
    private var alarmRepeatsIntervalInput: EditText? = null
    private var currentAlarmType: Int = 0

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
        alarmTimeInput = activity.findViewById(R.id.alarmTimeInput)
        alarmSlumberLengthInput = activity.findViewById(R.id.alarmSlumberLengthInput)
        earlyMarginInput = activity.findViewById(R.id.earlyMarginInput)
        alarmRepeatsInput = activity.findViewById(R.id.alarmRepeatsInput)
        alarmRepeatsIntervalInput = activity.findViewById(R.id.alarmRepeatsIntervalInput)
        nowDateTime = activity.findViewById(R.id.nowDateTime)
        btnSet?.setOnClickListener { _ ->
            try {
                val triggerTime: DateTime = buildFinalTriggerTime()
                setAlarmOnTime(triggerTime)
            } catch (t: Throwable) {
                UiErrorHandler().handleError(t)
            }
        }

        val alarmsConfig = alarmsPersistenceService.readAlarmsConfig()
        val alarmTriggers = alarmsConfig.alarmTriggers
        // check alarm triggers are still valid
        val inactive = alarmTriggers.filter { it.triggerTime.isBefore(DateTime.now()) }
        for (inactiveAlarmTrigger in inactive) {
            alarmsPersistenceService.removeAlarmTrigger(inactiveAlarmTrigger)
        }

        // refreshing current time
        val someHandler = Handler(activity.mainLooper)
        someHandler.postDelayed(object : Runnable {
            override fun run() {
                nowDateTime?.text = DateTime.now().toString("HH:mm:ss, yyyy-MM-dd")
                someHandler.postDelayed(this, 1000)
            }
        }, 10)

        val spinner: Spinner = activity.findViewById(R.id.spinnerAlarmType)
        ArrayAdapter.createFromResource(
            activity,
            R.array.alarm_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectAlarmType(position)
                logger.debug("selected: $position, $id")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        selectAlarmType(0)

        alarmTimeInput?.requestFocus()
        // show keyboard
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        Handler(Looper.getMainLooper()).postDelayed({
            softKeyboardService.showSoftKeyboard(alarmTimeInput)
        }, 200)

        permissionService.isMicrophonePermissionGranted
        permissionService.isStoragePermissionGranted
        logger.info(activity.javaClass.simpleName + " has been created")
    }

    private fun selectAlarmType(option: Int) {
        currentAlarmType = option
        when (option) {
            0, 1, 3 -> {
                alarmTimeInput?.visibility = View.VISIBLE
                alarmSlumberLengthInput?.visibility = View.GONE
            }
            2 -> {
                alarmTimeInput?.visibility = View.GONE
                alarmSlumberLengthInput?.visibility = View.VISIBLE
            }
        }
    }


    private fun buildFinalTriggerTime(): DateTime {
        var triggerTime = readTriggerTime()
        // subtract random minutes
        if (earlyMarginInput?.length() ?: 0 > 0) {
            val earlyMarginMin: Int = earlyMarginInput?.text.toString().toInt()
            if (earlyMarginMin > 0) {
                val newTriggerTime: DateTime =
                    triggerTime.minusMinutes(random.nextInt(earlyMarginMin + 1))
                if (newTriggerTime.isAfterNow) { // check validity
                    triggerTime = newTriggerTime
                }
            }
        }
        return triggerTime
    }

    private fun readTriggerTime(): DateTime {
        when (currentAlarmType) {
            0 -> { // ring at
                require(alarmTimeInput!!.isNotEmpty()) { "trigger time not given" }
                return alarmTimeInput!!.triggerTime
            }
            1 -> { // ring until
                require(alarmTimeInput!!.isNotEmpty()) { "trigger time not given" }
                val endTime = alarmTimeInput!!.triggerTime
                val ringingDurationS = (alarmRepeatsCount - 1) * alarmRepeatsInterval
                return endTime.minusSeconds(ringingDurationS)
            }
            2 -> { // ring in minutes
                require(
                    !alarmSlumberLengthInput?.text?.toString().isNullOrBlank()
                ) { "minutes not given" }
                val minutes = alarmSlumberLengthInput!!.text.toString().toInt()
                return DateTime.now().plusMinutes(minutes)
            }
            else -> throw RuntimeException("unexpected alarm type")
        }
    }

    private val alarmRepeatsCount: Int
        get() {
            val input: String? = alarmRepeatsInput?.text?.toString()
            return input.takeIf { !it.isNullOrBlank() }?.toInt()?.takeIf { it >= 1 } ?: 1
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
        val minutesTo = Minutes.minutesBetween(DateTime.now(), triggerTime).minutes
        val timeTo = when {
            minutesTo < 60 -> "$minutesTo minutes"
            minutesTo < 120 -> "1 hour"
            else -> "${minutesTo / 60} hours"
        }
        uiInfoService.showToast("$repeats Alarm will go off in $timeTo")
    }

}
