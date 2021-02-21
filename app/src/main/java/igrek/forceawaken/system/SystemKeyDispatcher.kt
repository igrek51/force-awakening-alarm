package igrek.forceawaken.system

import igrek.forceawaken.activity.ActivityController
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory

class SystemKeyDispatcher(
        activityController: LazyInject<ActivityController> = appFactory.activityController,
) {
    private val activityController by LazyExtractor(activityController)

    fun onKeyBack(): Boolean {
        activityController.quit()
        return true
    }

    fun onKeyMenu(): Boolean {
        return false
    }

    fun onVolumeUp(): Boolean {
        return false
    }

    fun onVolumeDown(): Boolean {
        return false
    }
}
