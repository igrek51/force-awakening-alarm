package igrek.forceawaken.activity.schedule

import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.activity.ActivityResultDispatcher
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.system.SystemKeyDispatcher

class ScheduleActivityData(
    private val _scheduleActivityLayout: LazyInject<ScheduleActivityLayout> = appFactory.scheduleActivityLayout,
    private val _systemKeyDispatcher: LazyInject<SystemKeyDispatcher> = appFactory.systemKeyDispatcher,
    private val _activityResultDispatcher: LazyInject<ActivityResultDispatcher> = appFactory.activityResultDispatcher,
) : AppCompatActivity() {
    val scheduleActivityLayout by LazyExtractor(_scheduleActivityLayout)
    val systemKeyDispatcher by LazyExtractor(_systemKeyDispatcher)
    val activityResultDispatcher by LazyExtractor(_activityResultDispatcher)

    fun inflate() {
        _scheduleActivityLayout.get()
        _systemKeyDispatcher.get()
        _activityResultDispatcher.get()
    }
}
