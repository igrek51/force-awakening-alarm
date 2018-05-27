package igrek.forceawaken;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.joda.time.DateTime;

import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;

public class MainActivity extends AppCompatActivity {
	Button btnSet;
	EditText alarmSeconds;
	EditText alarmTime;
	Logger logger = LoggerFactory.getLogger();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnSet = (Button) findViewById(R.id.btnSetAlarm);
		alarmSeconds = (EditText) findViewById(R.id.alarmSeconds);
		alarmTime = (EditText) findViewById(R.id.alarmTime);
		
		btnSet.setOnClickListener(v -> {
			DateTime triggerTime = getTriggerTime();
			
			Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
			PendingIntent p1 = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
			AlarmManager a = (AlarmManager) getSystemService(ALARM_SERVICE);
			// FIXME one alarm only can be set at a time
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				a.setExact(AlarmManager.RTC_WAKEUP, triggerTime.getMillis(), p1);
			} else {
				a.set(AlarmManager.RTC_WAKEUP, triggerTime.getMillis(), p1);
			}
			//			alarm.setExact(AlarmManager.RTC_WAKEUP,10000,pintent);
			Toast.makeText(getApplicationContext(), "AlarmReceiver set at " + triggerTime.toString("HH:mm:ss, yyyy-MM-dd"), Toast.LENGTH_LONG)
					.show();
		});
		
		logger.debug("Application has been started");
	}
	
	private DateTime getTriggerTime() {
		if (alarmTime.getText().length() > 0) {
			int hours = Integer.parseInt(alarmTime.getText().toString().substring(0, 2));
			int mins = Integer.parseInt(alarmTime.getText().toString().substring(3));
			// todays time or tomorrow
			DateTime now = DateTime.now();
			DateTime todayTriggerTime = now.withHourOfDay(hours)
					.withMinuteOfHour(mins)
					.withSecondOfMinute(0);
			DateTime tomorrowTriggerTime = todayTriggerTime.plusDays(1);
			DateTime triggerTime = now.isBefore(todayTriggerTime) ? todayTriggerTime : tomorrowTriggerTime;
			return triggerTime;
		} else {
			int seconds = Integer.parseInt(alarmSeconds.getText().toString());
			return DateTime.now().plusSeconds(seconds);
		}
	}
	
}