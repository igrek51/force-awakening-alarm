package igrek.forceawaken.activity.list

import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.activity.ActivityResultDispatcher
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.system.SystemKeyDispatcher

class ListActivityData(
    private val _listActivityLayout: LazyInject<ListActivityLayout> = appFactory.listActivityLayout,
    private val _systemKeyDispatcher: LazyInject<SystemKeyDispatcher> = appFactory.systemKeyDispatcher,
    private val _activityResultDispatcher: LazyInject<ActivityResultDispatcher> = appFactory.activityResultDispatcher,
) : AppCompatActivity() {
    val listActivityLayout by LazyExtractor(_listActivityLayout)
    val systemKeyDispatcher by LazyExtractor(_systemKeyDispatcher)
    val activityResultDispatcher by LazyExtractor(_activityResultDispatcher)

    fun inflate() {
        _listActivityLayout.get()
        _systemKeyDispatcher.get()
        _activityResultDispatcher.get()
    }
}
