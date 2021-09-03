package igrek.forceawaken.activity

import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.system.PermissionService
import igrek.forceawaken.system.SystemKeyDispatcher

/*
    Main Activity starter pack
    Workaround for reusing finished activities by Android
 */
class MainActivityData(
    private val _mainActivityLayout: LazyInject<MainActivityLayout> = appFactory.mainActivityLayout,
    private val _activityController: LazyInject<ActivityController> = appFactory.activityController,
    private val _optionSelectDispatcher: LazyInject<OptionSelectDispatcher> = appFactory.optionSelectDispatcher,
    private val _systemKeyDispatcher: LazyInject<SystemKeyDispatcher> = appFactory.systemKeyDispatcher,
    private val _permissionService: LazyInject<PermissionService> = appFactory.permissionService,
    private val _activityResultDispatcher: LazyInject<ActivityResultDispatcher> = appFactory.activityResultDispatcher,
) : AppCompatActivity() {
    val mainActivityLayout by LazyExtractor(_mainActivityLayout)
    val activityController by LazyExtractor(_activityController)
    val optionSelectDispatcher by LazyExtractor(_optionSelectDispatcher)
    val systemKeyDispatcher by LazyExtractor(_systemKeyDispatcher)
    val permissionService by LazyExtractor(_permissionService)
    val activityResultDispatcher by LazyExtractor(_activityResultDispatcher)

    fun inflate() {
        _mainActivityLayout.get()
        _activityController.get()
        _optionSelectDispatcher.get()
        _systemKeyDispatcher.get()
        _permissionService.get()
        _activityResultDispatcher.get()
    }
}
