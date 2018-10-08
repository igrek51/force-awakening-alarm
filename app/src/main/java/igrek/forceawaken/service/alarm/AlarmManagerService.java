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
	
	public void setAlarmOnTime(DateTime triggerTime, Context context) {
		Intent intent = new Intent(activity.getApplicationContext(), AlarmReceiver.class);
		intent.addCategory("android.intent.category.DEFAULT");
		long millis = triggerTime.getMillis();
		int id = (int) millis; // unique to enable multiple alarms
		
		PendingIntent p1 = PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, p1);
		} else {
			alarmManager.set(AlarmManager.RTC_WAKEUP, millis, p1);
		}
		
		// save creation information in external place
		alarmsPersistenceService.addAlarmTrigger(new AlarmTrigger(triggerTime, true));
	}
	
	public void cancelAlarm(DateTime triggerTime, Context context) {
		logger.debug("cancelling alarm: " + triggerTime.toString("HH:mm:ss, yyyy-MM-dd"));
		Intent intent = new Intent(activity.getApplicationContext(), AlarmReceiver.class);
		intent.addCategory("android.intent.category.DEFAULT");
		long millis = triggerTime.getMillis();
		int id = (int) millis; // unique to enable multiple alarms
		PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}
	
	public boolean isAlarmActive(DateTime triggerTime, Context context) {
		// WTF? returning true even after cancelling
		Intent intent = new Intent(activity.getApplicationContext(), AlarmReceiver.class);
		intent.addCategory("android.intent.category.DEFAULT");
		long millis = triggerTime.getMillis();
		int id = (int) millis; // unique to enable multiple alarms
		return PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_NO_CREATE) != null;
	}
}
