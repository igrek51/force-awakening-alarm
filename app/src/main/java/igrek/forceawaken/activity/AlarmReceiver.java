package igrek.forceawaken.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;

public class AlarmReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (pm != null) {
			
			PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "FORCEAWEKENINGALARM");
			wakeLock.acquire();
			
			Intent intent2 = new Intent(context, AwakenActivity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			context.startActivity(intent2);
			
			new Handler().post(() -> {
				if (wakeLock != null) {
					wakeLock.release();
				}
			});
		}
		
	}
}