package igrek.forceawaken.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.info.logger.Logger
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.AppContextFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import org.joda.time.DateTime

class AwakenActivity(
        awakenActivityData: LazyInject<AwakenActivityData> = appFactory.awakenActivityData,
) : AppCompatActivity() {
    private var activityData by LazyExtractor(awakenActivityData)

    private val logger: Logger = LoggerFactory.logger

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            logger.info("Creating Dependencies container...")
            AppContextFactory.createAppContext(this)
            recreateFields() // Workaround for reusing finished activities by Android
            super.onCreate(savedInstanceState)

            activityData.awakenActivityLayout.init()

        } catch (t: Throwable) {
            logger.fatal(t)
            throw t
        }
    }

    private fun recreateFields() {
        activityData = appFactory.awakenActivityData.get()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        activityData.activityController.onConfigurationChanged(newConfig)
    }


    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }, 100)
    }

    /**
     * @return is alarm playing or is intended to be playing
     */
    private val isAlarmActivating: Boolean
        get() {
            if (activityData.alarmPlayer.isPlaying) return true
            val activateAlarmTime = activityData.awakenActivityLayout.activateAlarmTime
            return activateAlarmTime?.isAfter(DateTime.now().minusSeconds(10)) ?: false
        }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (isAlarmActivating) {
            logger.info("Alarm already activated - postponing by 40 s")
            // postpone alarm - create new
            val triggerTime2: DateTime = DateTime.now().plusSeconds(40)
            activityData.alarmManagerService.setAlarmOnTime(triggerTime2)
        } else {
            logger.info("Recreating new activity...")
            val extras: Bundle? = intent.extras
            val restartIntent = Intent(this, AwakenActivity::class.java)
            if (extras != null) restartIntent.putExtras(extras)
            restartIntent.action = Intent.ACTION_MAIN
            restartIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(restartIntent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityData.alarmPlayer.stopAlarm()
        activityData.awakenActivityLayout.activateAlarmTime = null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (activityData.alarmPlayer.isPlaying) {
            // disable back key
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true
            } else if (keyCode == KeyEvent.KEYCODE_MENU) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}