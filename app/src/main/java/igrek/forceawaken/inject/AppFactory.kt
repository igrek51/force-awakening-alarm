package igrek.forceawaken.inject

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.activity.*
import igrek.forceawaken.activity.list.ListActivityData
import igrek.forceawaken.activity.list.ListActivityLayout
import igrek.forceawaken.activity.schedule.ScheduleActivityData
import igrek.forceawaken.activity.schedule.ScheduleActivityLayout
import igrek.forceawaken.activity.settings.SettingsActivityData
import igrek.forceawaken.activity.settings.SettingsActivityLayout
import igrek.forceawaken.alarm.AlarmManagerService
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
import igrek.forceawaken.sensors.VibratorService
import igrek.forceawaken.settings.preferences.PreferencesService
import igrek.forceawaken.settings.preferences.PreferencesState
import igrek.forceawaken.system.PermissionService
import igrek.forceawaken.system.SoftKeyboardService
import igrek.forceawaken.system.SystemKeyDispatcher
import igrek.forceawaken.system.WindowManagerService
import igrek.forceawaken.system.filesystem.ExternalCardService
import igrek.forceawaken.system.filesystem.InternalDataService
import igrek.forceawaken.task.AwakeTaskService
import igrek.forceawaken.time.AlarmTimeService
import igrek.forceawaken.userdata.LocalDbService
import igrek.forceawaken.userdata.UserDataDao
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
    val listActivityData = SingletonInject { ListActivityData() }
    val scheduleActivityData = SingletonInject { ScheduleActivityData() }
    val settingsActivityData = SingletonInject { SettingsActivityData() }
    val awakenActivityData = SingletonInject { AwakenActivityData() }
    val activityController = SingletonInject { ActivityController() }
    val mainActivityLayout = SingletonInject { MainActivityLayout() }
    val listActivityLayout = SingletonInject { ListActivityLayout() }
    val scheduleActivityLayout = SingletonInject { ScheduleActivityLayout() }
    val settingsActivityLayout = SingletonInject { SettingsActivityLayout() }
    val awakenActivityLayout = SingletonInject { AwakenActivityLayout() }
    val optionSelectDispatcher = SingletonInject { OptionSelectDispatcher() }
    val systemKeyDispatcher = SingletonInject { SystemKeyDispatcher() }
    val windowManagerService = SingletonInject { WindowManagerService() }
    val uiResourceService = SingletonInject { UiResourceService() }
    val uiInfoService = SingletonInject { UiInfoService() }
    val preferencesState = SingletonInject { PreferencesState() }
    val preferencesService = SingletonInject { PreferencesService() }
    val localDbService = SingletonInject { LocalDbService() }
    val userDataDao = SingletonInject { UserDataDao() }

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
