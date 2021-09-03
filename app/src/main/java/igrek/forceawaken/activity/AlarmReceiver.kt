package igrek.forceawaken.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.PowerManager
import igrek.forceawaken.info.logger.LoggerFactory.logger

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        logger.debug("received AlarmReceiver signal")
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "forceawaken:FORCEAWAKENINGALARM"
        )
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
        val intent2 = Intent(context, AwakenActivity::class.java)
        intent2.action = Intent.ACTION_MAIN
        intent2.addCategory(Intent.CATEGORY_LAUNCHER)
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        context.startActivity(intent2)
        Handler().postDelayed({
            wakeLock.release()
        }, 10000)

    }
}
