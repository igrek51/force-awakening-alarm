package igrek.forceawaken.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.PowerManager

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, "forceawaken:FORCEAWEKENINGALARM")
        wakeLock.acquire()
        val intent2 = Intent(context, AwakenActivity::class.java)
        intent2.action = Intent.ACTION_MAIN
        intent2.addCategory(Intent.CATEGORY_LAUNCHER)
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        context.startActivity(intent2)
        Handler().postDelayed({
            wakeLock.release()
        }, 1000)
    }
}
