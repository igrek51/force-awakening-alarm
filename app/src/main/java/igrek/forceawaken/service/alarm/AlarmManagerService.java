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
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.persistence.AlarmsPersistenceService;

public class AlarmManagerService {
	
	private Activity activity;
	private AlarmsPersistenceService alarmsPersistenceService;
	private AlarmManager alarmManager;
	private Logger logger = LoggerFactory.getLogger();
	
	public AlarmManagerService(Activity activity, AlarmsPersistenceService alarmsPersistenceService) {
		this.activity = activity;
		this.alarmsPersistenceService = alarmsPersistenceService;
		alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void setAlarmOnTime(DateTime triggerTime) {
		Intent intent = new Intent(activity.getApplicationContext(), AlarmReceiver.class);
		intent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra("message", "Hello world!");
		long millis = triggerTime.getMillis();
		int id = (int) millis; // unique to enable multiple alarms
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_ONE_SHOT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
		} else {
			alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
		}
		
		// save creation information in external place
		alarmsPersistenceService.addAlarmTrigger(new AlarmTrigger(triggerTime, true, null));
	}
	
	public void cancelAlarm(DateTime triggerTime, PendingIntent pendingIntent) {
		logger.debug("cancelling alarm: " + triggerTime.toString("HH:mm:ss, yyyy-MM-dd"));
		Intent intent = new Intent(activity.getApplicationContext(), AlarmReceiver.class);
		intent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra("message", "Hello world!");
		long millis = triggerTime.getMillis();
		int id = (int) millis; // unique to enable multiple alarms
		
		PendingIntent p1 = PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, 0);
		alarmManager.cancel(p1);
		
		if (pendingIntent != null) {
			alarmManager.cancel(pendingIntent);
		}
	}
	
	public boolean isAlarmActive(DateTime triggerTime) {
		// WTF? returning true even after cancelling
		Intent intent = new Intent(activity.getApplicationContext(), AlarmReceiver.class);
		intent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra("message", "Hello world!");
		long millis = triggerTime.getMillis();
		int id = (int) millis; // unique to enable multiple alarms
		return PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_NO_CREATE) != null;
	}
}
