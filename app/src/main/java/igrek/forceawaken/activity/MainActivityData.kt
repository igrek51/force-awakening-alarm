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
        mainActivityLayout: LazyInject<MainActivityLayout> = appFactory.mainActivityLayout,
        activityController: LazyInject<ActivityController> = appFactory.activityController,
        optionSelectDispatcher: LazyInject<OptionSelectDispatcher> = appFactory.optionSelectDispatcher,
        systemKeyDispatcher: LazyInject<SystemKeyDispatcher> = appFactory.systemKeyDispatcher,
        permissionService: LazyInject<PermissionService> = appFactory.permissionService,
        activityResultDispatcher: LazyInject<ActivityResultDispatcher> = appFactory.activityResultDispatcher,
) : AppCompatActivity() {
    val mainActivityLayout by LazyExtractor(mainActivityLayout)
    val activityController by LazyExtractor(activityController)
    val optionSelectDispatcher by LazyExtractor(optionSelectDispatcher)
    val systemKeyDispatcher by LazyExtractor(systemKeyDispatcher)
    val permissionService by LazyExtractor(permissionService)
    val activityResultDispatcher by LazyExtractor(activityResultDispatcher)
}
