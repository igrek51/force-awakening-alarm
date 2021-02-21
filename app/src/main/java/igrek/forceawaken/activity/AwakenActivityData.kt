package igrek.forceawaken.activity

import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.alarm.AlarmManagerService
import igrek.forceawaken.alarm.VibratorService
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.persistence.AlarmsPersistenceService
import igrek.forceawaken.ringtone.AlarmPlayerService
import igrek.forceawaken.ringtone.RingtoneManagerService
import igrek.forceawaken.system.PermissionService
import igrek.forceawaken.system.SystemKeyDispatcher
import igrek.forceawaken.task.AwakeTaskService
import igrek.forceawaken.volume.VolumeCalculatorService

/*
    Main Activity starter pack
    Workaround for reusing finished activities by Android
 */
class AwakenActivityData(
        awakenActivityLayout: LazyInject<AwakenActivityLayout> = appFactory.awakenActivityLayout,
        activityController: LazyInject<ActivityController> = appFactory.activityController,
        optionSelectDispatcher: LazyInject<OptionSelectDispatcher> = appFactory.optionSelectDispatcher,
        systemKeyDispatcher: LazyInject<SystemKeyDispatcher> = appFactory.systemKeyDispatcher,
        permissionService: LazyInject<PermissionService> = appFactory.permissionService,
        alarmPlayerService: LazyInject<AlarmPlayerService> = appFactory.alarmPlayerService,
        alarmManagerService: LazyInject<AlarmManagerService> = appFactory.alarmManagerService,
        vibratorService: LazyInject<VibratorService> = appFactory.vibratorService,
        ringtoneManagerService: LazyInject<RingtoneManagerService> = appFactory.ringtoneManagerService,
        uiInfoService: LazyInject<UiInfoService> = appFactory.uiInfoService,
        volumeCalculatorService: LazyInject<VolumeCalculatorService> = appFactory.volumeCalculatorService,
        awakeTaskService: LazyInject<AwakeTaskService> = appFactory.awakeTaskService,
        alarmsPersistenceService: LazyInject<AlarmsPersistenceService> = appFactory.alarmsPersistenceService,
) : AppCompatActivity() {
    val awakenActivityLayout by LazyExtractor(awakenActivityLayout)
    val activityController by LazyExtractor(activityController)
    val optionSelectDispatcher by LazyExtractor(optionSelectDispatcher)
    val systemKeyDispatcher by LazyExtractor(systemKeyDispatcher)
    val permissionService by LazyExtractor(permissionService)
    val alarmPlayer by LazyExtractor(alarmPlayerService)
    val alarmManagerService by LazyExtractor(alarmManagerService)
    val vibratorService by LazyExtractor(vibratorService)
    val ringtoneManager by LazyExtractor(ringtoneManagerService)
    val uiInfoService by LazyExtractor(uiInfoService)
    val volumeCalculatorService by LazyExtractor(volumeCalculatorService)
    val awakeTaskService by LazyExtractor(awakeTaskService)
    val alarmsPersistenceService by LazyExtractor(alarmsPersistenceService)
}
