package igrek.forceawaken.activity.settings

import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.R
import igrek.forceawaken.alarm.AlarmManagerService
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.layout.CommonLayout
import igrek.forceawaken.layout.navigation.NavigationMenuController
import igrek.forceawaken.settings.SettingsFragment
import igrek.forceawaken.system.WindowManagerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivityLayout(
    activity: LazyInject<AppCompatActivity> = appFactory.appCompatActivity,
    windowManagerService: LazyInject<WindowManagerService> = appFactory.windowManagerService,
    navigationMenuController: LazyInject<NavigationMenuController> = appFactory.navigationMenuController,
    alarmManagerService: LazyInject<AlarmManagerService> = appFactory.alarmManagerService,
    uiInfoService: LazyInject<UiInfoService> = appFactory.uiInfoService,
    commonLayout: LazyInject<CommonLayout> = appFactory.commonLayout,
) {
    private val activity by LazyExtractor(activity)
    private val windowManagerService by LazyExtractor(windowManagerService)
    private val navigationMenuController by LazyExtractor(navigationMenuController)
    private val alarmManagerService by LazyExtractor(alarmManagerService)
    private val uiInfoService by LazyExtractor(uiInfoService)
    private val commonLayout by LazyExtractor(commonLayout)

    private val logger = LoggerFactory.logger

    fun init() {
        logger.info("Initializing application...")

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                windowManagerService.hideTaskbar()
                initLayout()
            }

            logger.info("MainActivity Layout has been initialized.")
        }
    }

    private fun initLayout() {
        activity.setContentView(R.layout.screen_settings)

        commonLayout.init()
        navigationMenuController.init()

        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_content, SettingsFragment())
            .commit()

        logger.info(activity.javaClass.simpleName + " has been created")
    }

}
