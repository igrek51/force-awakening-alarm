package igrek.forceawaken.service.ringtone;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;

public class VibratorService {
	
	@Inject
	Activity activity;
	
	private Vibrator vibrator;
	
	public VibratorService() {
		DaggerIOC.getAppComponent().inject(this);
		vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	public void vibrate(long millis) {
		vibrator.vibrate(millis);
	}
	
}
