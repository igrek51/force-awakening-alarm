package igrek.forceawaken;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.base.Joiner;

import org.joda.time.DateTime;

import java.util.Random;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.domain.alarm.AlarmTrigger;
import igrek.forceawaken.domain.alarm.AlarmsConfig;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.alarm.AlarmManagerService;
import igrek.forceawaken.service.persistence.AlarmsPersistenceService;
import igrek.forceawaken.service.time.AlarmTimeService;
import igrek.forceawaken.service.ui.info.UserInfoService;
import igrek.forceawaken.ui.components.TriggerTimeInput;
import igrek.forceawaken.ui.errorcheck.UIErrorHandler;

public class MainActivity extends AppCompatActivity {
	
	private Logger logger = LoggerFactory.getLogger();
	private Random random = new Random();
	private Button btnSet, btnTestAlarm;
	private TriggerTimeInput alarmTimeInput;
	private EditText earlyMarginInput;
	private EditText alarmRepeatsInput;
	
	@Inject
	AlarmManagerService alarmManagerService;
	
	@Inject
	UserInfoService userInfoService;
	
	@Inject
	AlarmTimeService alarmTimeService;
	
	@Inject
	AlarmsPersistenceService alarmsPersistenceService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DaggerIOC.getFactoryComponent().inject(this); // inject to this
		
		// catch all uncaught exceptions
		// TODO catch also in another activities
		Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler((thread, th) -> {
			logger.errorUncaught(th);
			//pass further to OS
			defaultUEH.uncaughtException(thread, th);
		});
		
		setContentView(R.layout.activity_main);
		btnSet = (Button) findViewById(R.id.btnSetAlarm);
		btnTestAlarm = (Button) findViewById(R.id.btnTestAlarm);
		alarmTimeInput = (TriggerTimeInput) findViewById(R.id.alarmTimeInput);
		earlyMarginInput = (EditText) findViewById(R.id.earlyMarginInput);
		alarmRepeatsInput = (EditText) findViewById(R.id.alarmRepeatsInput);
		
		btnSet.setOnClickListener(v -> {
			try {
				DateTime triggerTime = buildFinalTriggerTime();
				setAlarmOnTime(triggerTime);
			} catch (Throwable t) {
				UIErrorHandler.showError(t);
			}
		});
		
		btnTestAlarm.setOnClickListener(v -> {
			setAlarmOnTime(DateTime.now().plusSeconds(3));
		});
		
		// TODO alarms set list
		AlarmsConfig alarmsConfig = alarmsPersistenceService.readAlarmsConfig();
		if (alarmsConfig != null) {
			logger.debug(Joiner.on(", ").join(alarmsConfig.getAlarmTriggers()));
		}
		alarmsConfig.getAlarmTriggers().add(new AlarmTrigger(DateTime.now()));
		alarmsPersistenceService.writeAlarmsConfig(alarmsConfig);
		
		alarmTimeInput.requestFocus();
		// show keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		logger.debug(this.getClass().getSimpleName() + " has been created");
	}
	
	private DateTime buildFinalTriggerTime() {
		DateTime triggerTime = alarmTimeInput.getTriggerTime();
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
		return triggerTime;
	}
	
	private int getAlarmRepeatsCount() {
		String input = alarmRepeatsInput.getText().toString();
		if (input.isEmpty())
			return 1;
		return Integer.parseInt(input);
	}
	
	private void setAlarmOnTime(DateTime triggerTime) {
		// multiple alarms at once
		int repeats = getAlarmRepeatsCount();
		for (int r = 0; r < repeats; r++) {
			DateTime triggerTime2 = triggerTime.plusSeconds(r * 40);
			alarmManagerService.setAlarmOnTime(triggerTime2, this);
			logger.debug("Alarm set at " + triggerTime2.toString("HH:mm:ss, yyyy-MM-dd"));
		}
		
		userInfoService.showToast(Integer.toString(repeats) + " Alarm set on " + triggerTime.toString("yyyy-MM-dd"));
	}
	
	
}