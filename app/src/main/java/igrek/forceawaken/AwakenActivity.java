package igrek.forceawaken;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.domain.ringtone.Ringtone;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.noise.NoiseDetectorService;
import igrek.forceawaken.service.player.AlarmPlayerService;
import igrek.forceawaken.service.ringtone.RingtoneManagerService;
import igrek.forceawaken.service.ui.WindowManagerService;

public class AwakenActivity extends AppCompatActivity {
	
	private Logger logger = LoggerFactory.getLogger();
	private Random random = new Random();
	private Ringtone currentRingtone;
	private ArrayAdapter<Ringtone> ringtoneListAdapter;
	
	@Inject
	NoiseDetectorService noiseDetectorService;
	
	@Inject
	WindowManagerService windowManagerService;
	
	@Inject
	AlarmPlayerService alarmPlayer;
	
	@Inject
	RingtoneManagerService ringtoneManager;
	
	private final String[] wakeUpInfos = new String[]{
			"RISE AND SHINE, MOTHERFUCKER!!!", "Kill Zombie process!!!", "Wstawaj, Nie Pierdol!",
	};
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Dagger Container init
		DaggerIOC.init(this); // reinitialize with different activity
		DaggerIOC.getAppComponent().inject(this);
		
		setContentView(R.layout.awaken_main);
		
		windowManagerService.setFullscreen();
		
		TextView fakeTime = (TextView) findViewById(R.id.fakeTime);
		fakeTime.setText(getFakeCurrentTime());
		
		TextView wakeUpLabel = (TextView) findViewById(R.id.wakeUpLabel);
		wakeUpLabel.setText(wakeUpInfos[random.nextInt(wakeUpInfos.length)]);
		
		// measure surrounding loudness level
		noiseDetectorService.measureNoiseLevel(1000, (amplitudeDb) -> {
			startAlarm(amplitudeDb);
		});
		
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void startAlarm(double noiseLevel) {
		
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
					
					Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(500);
					
					new Handler().postDelayed(this, 1000);
					
				}
			}
		};
		new Handler().postDelayed(vibrationsBooster, 180000);
		
		try {
			currentRingtone = ringtoneManager.getRandomRingtone();
			logger.debug("Current Ringtone: " + currentRingtone.getName());
			
			double volume = noiseDetectorService.calculateAlarmVolume(noiseLevel);
			logger.debug("Alarm volume level: " + volume);
			
			//Uri ringUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hopes_quiet);
			alarmPlayer.playAlarm(currentRingtone.getUri(), volume);
		} catch (IOException e) {
			logger.error(e);
		}
		
		List<Ringtone> ringtones = ringtoneManager.getAllRingtones();
		Collections.shuffle(ringtones); // that's the evilest thing i can imagine
		
		ringtoneListAdapter = new ArrayAdapter<>(this, R.layout.list_item, ringtones);
		ListView listView = (ListView) findViewById(R.id.ringtones_answer_list);
		listView.setAdapter(ringtoneListAdapter);
		
		listView.setOnItemClickListener((adapter1, v, position, id) -> {
			Ringtone selected = (Ringtone) adapter1.getItemAtPosition(position);
			if (selected.getName().equals(currentRingtone.getName())) {
				alarmPlayer.stopAlarm();
				Toast.makeText(getApplicationContext(), "Congratulations! You have woken up.", Toast.LENGTH_LONG)
						.show();
			} else {
				wrongAnswer();
			}
		});
	}
	
	private void wrongAnswer() {
		Toast.makeText(getApplicationContext(), "Wrong answer, you morron!", Toast.LENGTH_LONG)
				.show();
		
		Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(1000);
		
		ringtoneListAdapter.clear();
		
		List<Ringtone> ringtones = ringtoneManager.getAllRingtones();
		Collections.shuffle(ringtones); // that's the evilest thing i can imagine
		ringtoneListAdapter.addAll(ringtones);
		ringtoneListAdapter.notifyDataSetChanged();
	}
	
	private String getFakeCurrentTime() {
		// 2 hours forward
		DateTime fakeTime = DateTime.now().plusMinutes(random.nextInt(2 * 60));
		return fakeTime.toString("HH:mm");
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		alarmPlayer.stopAlarm();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// disable back key
		if (alarmPlayer.isPlaying()) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MENU) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
}