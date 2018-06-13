package igrek.forceawaken.service.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.joda.time.DateTime;

import javax.inject.Inject;

import igrek.forceawaken.AlarmReceiver;
import igrek.forceawaken.dagger.DaggerIOC;

import static android.content.Context.ALARM_SERVICE;

public class AlarmManagerService {
	
	@Inject
	Activity activity;
	
	public AlarmManagerService() {
		DaggerIOC.getAppComponent().inject(this);
	}
	
	public void setAlarmOnTime(DateTime triggerTime, Context context) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		long millis = triggerTime.getMillis();
		int id = (int) millis; // unique to enable multiple alarms
		PendingIntent p1 = PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager a = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			a.setExact(AlarmManager.RTC_WAKEUP, millis, p1);
		} else {
			a.set(AlarmManager.RTC_WAKEUP, millis, p1);
		}
	}
}
