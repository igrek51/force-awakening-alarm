package igrek.forceawaken.service.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.joda.time.DateTime;

import igrek.forceawaken.activity.AlarmReceiver;
import igrek.forceawaken.domain.alarm.AlarmTrigger;
import igrek.forceawaken.service.persistence.AlarmsPersistenceService;

public class AlarmManagerService {
	
	private Activity activity;
	private AlarmsPersistenceService alarmsPersistenceService;
	private AlarmManager alarmManager;
	
	public AlarmManagerService(Activity activity, AlarmsPersistenceService alarmsPersistenceService) {
		this.activity = activity;
		this.alarmsPersistenceService = alarmsPersistenceService;
		alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void setAlarmOnTime(DateTime triggerTime, Context context) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		long millis = triggerTime.getMillis();
		int id = (int) millis; // unique to enable multiple alarms
		PendingIntent p1 = PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_ONE_SHOT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, p1);
		} else {
			alarmManager.set(AlarmManager.RTC_WAKEUP, millis, p1);
		}
		
		// save creation information in external place
		alarmsPersistenceService.addAlarmTrigger(new AlarmTrigger(triggerTime));
	}
	
	public void cancelAlarm(DateTime triggerTime, Context context) {
		// FIXME not working
		Intent intent = new Intent(context, AlarmReceiver.class);
		long millis = triggerTime.getMillis();
		int id = (int) millis; // unique to enable multiple alarms
		PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}
}
