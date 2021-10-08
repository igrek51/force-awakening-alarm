package igrek.forceawaken.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import igrek.forceawaken.activity.service.WakeupService
import igrek.forceawaken.info.logger.LoggerFactory.logger

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, _intent: Intent) {
        logger.debug("received AlarmReceiver signal")

        WakeupService.acquireStaticLock(context)

        val intent = Intent(context, WakeupService::class.java)
        intent.putExtra("purpose", "wakeUp");
        context.startService(intent)
    }
}
