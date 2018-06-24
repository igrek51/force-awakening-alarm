package igrek.forceawaken.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import igrek.forceawaken.R;
import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.domain.ringtone.Ringtone;
import igrek.forceawaken.domain.task.AwakeTask;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.alarm.VibratorService;
import igrek.forceawaken.service.ringtone.AlarmPlayerService;
import igrek.forceawaken.service.ringtone.RingtoneManagerService;
import igrek.forceawaken.service.task.AwakeTaskService;
import igrek.forceawaken.service.time.AlarmTimeService;
import igrek.forceawaken.service.ui.WindowManagerService;
import igrek.forceawaken.service.ui.info.UserInfoService;
import igrek.forceawaken.service.volume.NoiseDetectorService;
import igrek.forceawaken.service.volume.VolumeCalculatorService;

public class AwakenActivity extends AppCompatActivity {
	
	private Logger logger = LoggerFactory.getLogger();
	private Random random = new Random();
	private Ringtone currentRingtone;
	private ArrayAdapter<Ringtone> ringtoneListAdapter;
	private AwakeTask awakeTask;
	
	@Inject
	NoiseDetectorService noiseDetectorService;
	@Inject
	WindowManagerService windowManagerService;
	@Inject
	AlarmPlayerService alarmPlayer;
	@Inject
	RingtoneManagerService ringtoneManager;
	@Inject
	VibratorService vibratorService;
	@Inject
	AlarmTimeService alarmTimeService;
	@Inject
	UserInfoService userInfoService;
	@Inject
	VolumeCalculatorService volumeCalculatorService;
	@Inject
	AwakeTaskService awakeTaskService;
	
	private final long ALARM_VIBRATION_PERIOD = 1000;
	private final double ALARM_VIBRATION_PWM = 0.5;
	
	private final String[] wakeUpInfos = new String[]{
			"RISE AND SHINE, MOTHERFUCKER!!!", "Kill Zombie process!!!", "Wstawaj, Nie Pierdol!",
	};
	
	private TextView fakeTimeLabel;
	private TextView wakeUpLabel;
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		logger.debug("AwakenActivity.onCreate");
		
		DaggerIOC.getFactoryComponent().inject(this);
		
		windowManagerService.setFullscreen();
		
		setContentView(R.layout.awaken_main);
		fakeTimeLabel = (TextView) findViewById(R.id.fakeTime);
		wakeUpLabel = (TextView) findViewById(R.id.wakeUpLabel);
		
		bootstrapAlarm();
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void bootstrapAlarm() {
		String fakeTimeStr = alarmTimeService.getFakeCurrentTime().toString("HH:mm");
		fakeTimeLabel.setText(fakeTimeStr);
		
		wakeUpLabel.setText(wakeUpInfos[random.nextInt(wakeUpInfos.length)]);
		// measure surrounding loudness level
		noiseDetectorService.measureNoiseLevel(1000, (amplitudeDb) -> {
			startAlarmPlaying(amplitudeDb);
		});
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		logger.debug("AwakenActivity.onNewIntent");
		if (alarmPlayer.isPlaying()) {
			logger.debug("Alarm already playing - cancelling the new one");
		} else {
			bootstrapAlarm();
		}
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void startAlarmPlaying(double noiseLevel) {
		// stop the previous alarm
		alarmPlayer.stopAlarm();
		
		new Handler().postDelayed(() -> {
			if (alarmPlayer.isPlaying()) {
				alarmPlayer.setVolume(1.0);
				logger.debug("Alarm is still playing - volume level boosted");
			}
		}, 90000);
		
		Runnable vibrationsBooster = new Runnable() {
			@Override
			public void run() {
				if (alarmPlayer.isPlaying()) {
					logger.debug("Alarm is still playing - turning on vibrations");
					vibratorService.vibrate((long) (ALARM_VIBRATION_PWM * ALARM_VIBRATION_PERIOD));
					new Handler().postDelayed(this, ALARM_VIBRATION_PERIOD);
				}
			}
		};
		new Handler().postDelayed(vibrationsBooster, 180000);
		
		try {
			currentRingtone = ringtoneManager.getRandomRingtone();
			logger.debug("Current Ringtone: " + currentRingtone.getName());
			
			double volume = volumeCalculatorService.calcFinalVolume(noiseLevel);
			logger.debug("Alarm volume level: " + volume);
			
			//Uri ringUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hopes_quiet);
			alarmPlayer.playAlarm(currentRingtone.getUri(), volume);
		} catch (IOException e) {
			logger.error(e);
		}
		
		List<Ringtone> ringtones = ringtoneManager.getAllRingtones();
		Collections.shuffle(ringtones); // that's the evilest thing i can imagine
		
		ringtoneListAdapter = new ArrayAdapter<>(this, R.layout.list_item, ringtones);
		ListView listView = findViewById(R.id.ringtones_answer_list);
		listView.setAdapter(ringtoneListAdapter);
		listView.setOnItemClickListener((adapter1, v, position, id) -> {
			Ringtone selected = (Ringtone) adapter1.getItemAtPosition(position);
			onRingtoneAnswer(selected);
		});
	}
	
	private void onRingtoneAnswer(Ringtone selected) {
		if (selected.equals(currentRingtone)) {
			correctAnswer();
		} else {
			wrongAnswer();
		}
	}
	
	private void correctAnswer() {
		alarmPlayer.stopAlarm();
		userInfoService.showToast("Congratulations! You have woken up.");
		
		if (awakeTask == null) { // only once
			awakeTask = awakeTaskService.getRandomTask();
			logger.debug("Random task: " + awakeTask.getClass().getSimpleName());
			awakeTask.run(this);
		}
	}
	
	private void wrongAnswer() {
		userInfoService.showToast("Wrong answer, you morron!");
		
		vibratorService.vibrate(1000);
		
		List<Ringtone> ringtones = ringtoneManager.getAllRingtones();
		Collections.shuffle(ringtones);
		ringtoneListAdapter.clear();
		ringtoneListAdapter.addAll(ringtones);
		ringtoneListAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		alarmPlayer.stopAlarm();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (alarmPlayer.isPlaying()) {
			// disable back key
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MENU) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}