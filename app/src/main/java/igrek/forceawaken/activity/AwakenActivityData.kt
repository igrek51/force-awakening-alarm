package igrek.forceawaken.activity

import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.alarm.AlarmManagerService
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.ringtone.AlarmPlayerService

/*
    Main Activity starter pack
    Workaround for reusing finished activities by Android
 */
class AwakenActivityData(
    private val _awakenActivityLayout: LazyInject<AwakenActivityLayout> = appFactory.awakenActivityLayout,
    private val _activityController: LazyInject<ActivityController> = appFactory.activityController,
    private val _alarmPlayerService: LazyInject<AlarmPlayerService> = appFactory.alarmPlayerService,
    private val _alarmManagerService: LazyInject<AlarmManagerService> = appFactory.alarmManagerService,
    private val _uiInfoService: LazyInject<UiInfoService> = appFactory.uiInfoService,
) : AppCompatActivity() {
    val awakenActivityLayout by LazyExtractor(_awakenActivityLayout)
    val activityController by LazyExtractor(_activityController)
    val alarmPlayer by LazyExtractor(_alarmPlayerService)
    val alarmManagerService by LazyExtractor(_alarmManagerService)
    val uiInfoService by LazyExtractor(_uiInfoService)

    fun inflate() {
        _awakenActivityLayout.get()
        _activityController.get()
        _alarmPlayerService.get()
        _alarmManagerService.get()
        _uiInfoService.get()
    }
}
