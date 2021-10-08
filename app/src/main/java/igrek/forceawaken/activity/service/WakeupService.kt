package igrek.forceawaken.activity.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import igrek.forceawaken.activity.AwakenActivity
import igrek.forceawaken.info.logger.LoggerFactory

class WakeupService : IntentService("WakeupService") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            onAlarm(intent)
        } finally {
            getLock(this)?.takeIf { it.isHeld }?.release()
//        Handler(Looper.getMainLooper()).postDelayed({
//            wakeLock.release()
//        }, 10000)
        }
    }

    private fun onAlarm(intent: Intent?) {
        LoggerFactory.logger.debug("received WakeupService signal")

        val purpose = intent?.extras?.get("purpose")
        if (purpose != "wakeUp") {
            LoggerFactory.logger.warn("Received unknown intent - ignoring")
            return
        }

        val context = this
        val intent2 = Intent(context, AwakenActivity::class.java)
        intent2.action = Intent.ACTION_MAIN
        intent2.addCategory(Intent.CATEGORY_LAUNCHER)
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        context.startActivity(intent2)
    }

    companion object {
        private var lockStatic: PowerManager.WakeLock? = null

        fun acquireStaticLock(context: Context) {
//        wakeLock.acquire(3 * 60 * 1000L /*3 minutes*/)
            getLock(context)?.acquire()
        }

        @Synchronized
        private fun getLock(context: Context): PowerManager.WakeLock? {
            if (lockStatic == null) {
                val power = context.getSystemService(POWER_SERVICE) as PowerManager
                lockStatic = power.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, // PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                    "igrek.forceawaken.activity.service.WakeupService:Static",
                )
                lockStatic!!.setReferenceCounted(true)
            }
            return lockStatic
        }
    }

}