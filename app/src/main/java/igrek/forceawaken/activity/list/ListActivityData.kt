package igrek.forceawaken.activity.list

import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.activity.ActivityResultDispatcher
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.system.SystemKeyDispatcher

class ListActivityData(
        listActivityLayout: LazyInject<ListActivityLayout> = appFactory.listActivityLayout,
        systemKeyDispatcher: LazyInject<SystemKeyDispatcher> = appFactory.systemKeyDispatcher,
        activityResultDispatcher: LazyInject<ActivityResultDispatcher> = appFactory.activityResultDispatcher,
) : AppCompatActivity() {
    val listActivityLayout by LazyExtractor(listActivityLayout)
    val systemKeyDispatcher by LazyExtractor(systemKeyDispatcher)
    val activityResultDispatcher by LazyExtractor(activityResultDispatcher)
}
