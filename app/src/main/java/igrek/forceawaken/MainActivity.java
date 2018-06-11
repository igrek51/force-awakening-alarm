package igrek.forceawaken;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.joda.time.DateTime;

import java.util.Random;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.alarm.AlarmManagerService;
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
	
	@Inject
	AlarmManagerService alarmManagerService;
	
	@Inject
	UserInfoService userInfoService;
	
	@Inject
	AlarmTimeService alarmTimeService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Dagger Container init
		DaggerIOC.init(this); // reinitialize with different activity
		DaggerIOC.getAppComponent().inject(this); // inject to this
		
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
		
		alarmTimeInput.requestFocus();
		// show keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		logger.debug("Application has been started");
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
	
	private void setAlarmOnTime(DateTime triggerTime) {
		alarmManagerService.setAlarmOnTime(triggerTime, this);
		userInfoService.showToast("Alarm set on " + triggerTime.toString("yyyy-MM-dd"));
		logger.debug("Alarm set at " + triggerTime.toString("HH:mm:ss, yyyy-MM-dd"));
	}
	
	
}