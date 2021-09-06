package igrek.forceawaken.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import igrek.forceawaken.MainApplication
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.system.WindowManagerService

class ActivityController(
        windowManagerService: LazyInject<WindowManagerService> = appFactory.windowManagerService,
        activity: LazyInject<Activity> = appFactory.activity,
) {
    private val windowManagerService by LazyExtractor(windowManagerService)
    private val activity by LazyExtractor(activity)

    private val logger = LoggerFactory.logger

    fun onConfigurationChanged(newConfig: Configuration) {
        // resize event
        val screenWidthDp = newConfig.screenWidthDp
        val screenHeightDp = newConfig.screenHeightDp
        val orientationName = getOrientationName(newConfig.orientation)
        logger.debug("Screen resized: " + screenWidthDp + "dp x " + screenHeightDp + "dp - " + orientationName)
    }

    private fun getOrientationName(orientation: Int): String {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return "landscape"
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return "portrait"
        }
        return orientation.toString()
    }

    fun quit() {
        windowManagerService.keepScreenOn(false)
        var currentActivity = activity
        if (activity.application is MainApplication) {
            val mainApplication = activity.application as MainApplication
            mainApplication.currentActivityListener.currentActivity?.let {
                currentActivity = it
                logger.debug("current activity set to ${currentActivity.javaClass.simpleName}")
            }
        }
        logger.debug("closing activity ${currentActivity.javaClass.simpleName}...")
        currentActivity.finish()
    }

    fun onStart() {
        logger.debug("starting activity...")
    }

    fun onStop() {
        logger.debug("stopping activity...")
    }

    fun onDestroy() {
        logger.info("activity has been destroyed")
    }

    fun minimize() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(startMain)
    }

}
