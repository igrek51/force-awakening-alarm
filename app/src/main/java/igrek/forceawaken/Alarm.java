package igrek.forceawaken;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.Vibrator;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 500 milliseconds
		//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
		//			v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
		//		}else{
		//deprecated in API 26
		v.vibrate(500);
		//		}
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TRAININGCOUNTDOWN");
		wl.acquire();
		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		//		context.startActivity(i);
		Toast.makeText(context, "Wake up", Toast.LENGTH_LONG).show();
		
		wl.release();
	}
}