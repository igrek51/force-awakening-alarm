package igrek.forceawaken.activity.schedule

import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.activity.ActivityResultDispatcher
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.system.SystemKeyDispatcher

class ScheduleActivityData(
        scheduleActivityLayout: LazyInject<ScheduleActivityLayout> = appFactory.scheduleActivityLayout,
        systemKeyDispatcher: LazyInject<SystemKeyDispatcher> = appFactory.systemKeyDispatcher,
        activityResultDispatcher: LazyInject<ActivityResultDispatcher> = appFactory.activityResultDispatcher,
) : AppCompatActivity() {
    val scheduleActivityLayout by LazyExtractor(scheduleActivityLayout)
    val systemKeyDispatcher by LazyExtractor(systemKeyDispatcher)
    val activityResultDispatcher by LazyExtractor(activityResultDispatcher)
}
