package igrek.forceawaken.inject

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.activity.*
import igrek.forceawaken.alarm.AlarmManagerService
import igrek.forceawaken.alarm.VibratorService
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.info.UiResourceService
import igrek.forceawaken.info.logger.Logger
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.layout.CommonLayout
import igrek.forceawaken.layout.navigation.NavigationMenuController
import igrek.forceawaken.persistence.AlarmsPersistenceService
import igrek.forceawaken.ringtone.AlarmPlayerService
import igrek.forceawaken.ringtone.RingtoneManagerService
import igrek.forceawaken.sensors.AccelerometerService
import igrek.forceawaken.system.PermissionService
import igrek.forceawaken.system.SoftKeyboardService
import igrek.forceawaken.system.SystemKeyDispatcher
import igrek.forceawaken.system.WindowManagerService
import igrek.forceawaken.system.filesystem.ExternalCardService
import igrek.forceawaken.system.filesystem.InternalDataService
import igrek.forceawaken.task.AwakeTaskService
import igrek.forceawaken.time.AlarmTimeService
import igrek.forceawaken.volume.NoiseDetectorService
import igrek.forceawaken.volume.VolumeCalculatorService


class AppFactory(
        activity: AppCompatActivity,
) {
    val activity: LazyInject<Activity> = SingletonInject { activity }
    val appCompatActivity: LazyInject<AppCompatActivity> = SingletonInject { activity }

    val context: LazyInject<Context> = SingletonInject { activity.applicationContext }
    val logger: LazyInject<Logger> = PrototypeInject { LoggerFactory.logger }

    /* Services */
    val activityData = SingletonInject { MainActivityData() }
    val awakenActivityData = SingletonInject { AwakenActivityData() }
    val activityController = SingletonInject { ActivityController() }
    val mainActivityLayout = SingletonInject { MainActivityLayout() }
    val awakenActivityLayout = SingletonInject { AwakenActivityLayout() }
    val optionSelectDispatcher = SingletonInject { OptionSelectDispatcher() }
    val systemKeyDispatcher = SingletonInject { SystemKeyDispatcher() }
    val windowManagerService = SingletonInject { WindowManagerService() }
    val uiResourceService = SingletonInject { UiResourceService() }
    val uiInfoService = SingletonInject { UiInfoService() }

    val navigationMenuController = SingletonInject { NavigationMenuController() }
    val softKeyboardService = SingletonInject { SoftKeyboardService() }
    val noiseDetectorService = SingletonInject { NoiseDetectorService() }
    val alarmPlayerService = SingletonInject { AlarmPlayerService() }
    val ringtoneManagerService = SingletonInject { RingtoneManagerService() }
    val vibratorService = SingletonInject { VibratorService() }
    val alarmTimeService = SingletonInject { AlarmTimeService() }
    val alarmManagerService = SingletonInject { AlarmManagerService() }
    val accelerometerService = SingletonInject { AccelerometerService() }
    val volumeCalculatorService = SingletonInject { VolumeCalculatorService() }
    val awakeTaskService = SingletonInject { AwakeTaskService() }
    val externalCardService = SingletonInject { ExternalCardService() }
    val alarmsPersistenceService = SingletonInject { AlarmsPersistenceService() }
    val internalDataService = SingletonInject { InternalDataService() }
    val permissionService = SingletonInject { PermissionService() }
    val activityResultDispatcher = SingletonInject { ActivityResultDispatcher() }
    val commonLayout = SingletonInject { CommonLayout() }
}
