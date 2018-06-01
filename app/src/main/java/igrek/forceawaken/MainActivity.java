package igrek.forceawaken;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;

public class MainActivity extends AppCompatActivity {
	
	Logger logger = LoggerFactory.getLogger();
	Button btnSet, btnTestAlarm;
	EditText alarmTimeInput;
	EditText earlyMarginInput;
	private Random random = new Random();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnSet = (Button) findViewById(R.id.btnSetAlarm);
		btnTestAlarm = (Button) findViewById(R.id.btnTestAlarm);
		alarmTimeInput = (EditText) findViewById(R.id.alarmTimeInput);
		earlyMarginInput = (EditText) findViewById(R.id.earlyMarginInput);
		
		btnSet.setOnClickListener(v -> {
			DateTime triggerTime = getTriggerTime();
			// subtract random minutes
			if (earlyMarginInput.length() > 0) {
				int earlyMarginMin = Integer.parseInt(earlyMarginInput.getText().toString());
				if (earlyMarginMin > 0) {
					DateTime newTriggerTime = triggerTime.minusMinutes(random.nextInt(earlyMarginMin + 1));
					if (newTriggerTime.isAfterNow()) { // check validity
						triggerTime = newTriggerTime;
					}
				}
			}
			setAlarmOnTime(triggerTime);
		});
		
		btnTestAlarm.setOnClickListener(v -> {
			DateTime triggerTime = DateTime.now().plusSeconds(3);
			setAlarmOnTime(triggerTime);
		});
		
		alarmTimeInput.addTextChangedListener(new TextWatcher() {
			int beforeLength;
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				beforeLength = s.length();
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > beforeLength) {
					validateAlarmTime();
				}
			}
		});
		
		
		logger.debug("Application has been started");
		
		alarmTimeInput.requestFocus();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
	}
	
	private void validateAlarmTime() {
		String text = alarmTimeInput.getText().toString();
		if (!text.contains(":")) {
			if (text.length() == 4) {
				text = text.substring(0, 2) + ":" + text.substring(2);
				setAlarmTimeInput(text);
			} else if (text.length() == 3) {
				if (!(text.startsWith("0") || text.startsWith("1") || text.startsWith("2"))) {
					text = text.substring(0, 1) + ":" + text.substring(1);
					setAlarmTimeInput(text);
				}
			}
		}
	}
	
	private void setAlarmTimeInput(String text) {
		alarmTimeInput.setText(text);
		alarmTimeInput.setSelection(text.length(), text.length());
	}
	
	private void setAlarmOnTime(DateTime triggerTime) {
		Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
		PendingIntent p1 = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
		AlarmManager a = (AlarmManager) getSystemService(ALARM_SERVICE);
		// FIXME one alarm only can be set at a time
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			a.setExact(AlarmManager.RTC_WAKEUP, triggerTime.getMillis(), p1);
		} else {
			a.set(AlarmManager.RTC_WAKEUP, triggerTime.getMillis(), p1);
		}
		// alarm.setExact(AlarmManager.RTC_WAKEUP,10000,pintent);
		Toast.makeText(getApplicationContext(), "Alarm set at " + triggerTime.toString("HH:mm:ss, yyyy-MM-dd"), Toast.LENGTH_LONG)
				.show();
	}
	
	private DateTime getTriggerTime() {
		String timeRegex = "([01]?[0-9]|2[0-3]):([0-5][0-9])";
		Pattern pattern = Pattern.compile(timeRegex);
		Matcher matcher = pattern.matcher(alarmTimeInput.getText().toString());
		if (!matcher.matches()) {
			throw new NumberFormatException("Invalid time: " + alarmTimeInput.getText().toString());
		}
		int hours = Integer.parseInt(matcher.group(1));
		int mins = Integer.parseInt(matcher.group(2));
		// todays time or tomorrow
		DateTime now = DateTime.now();
		DateTime todayTriggerTime = now.withHourOfDay(hours)
				.withMinuteOfHour(mins)
				.withSecondOfMinute(0);
		DateTime tomorrowTriggerTime = todayTriggerTime.plusDays(1);
		DateTime triggerTime = now.isBefore(todayTriggerTime) ? todayTriggerTime : tomorrowTriggerTime;
		return triggerTime;
	}
	
}