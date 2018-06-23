package igrek.forceawaken.service.alarm;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;

public class VibratorService {
	
	private Activity activity;
	
	private Vibrator vibrator;
	
	public VibratorService(Activity activity) {
		this.activity = activity;
		vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	public void vibrate(long millis) {
		vibrator.vibrate(millis);
	}
	
}
