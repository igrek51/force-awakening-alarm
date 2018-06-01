package igrek.forceawaken;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;

public class AwakenActivity extends AppCompatActivity {
	
	private Logger logger = LoggerFactory.getLogger();
	private Random random = new Random();
	private File currentRingtone;
	private ArrayAdapter<String> listAdapter;
	private MediaPlayer mediaPlayer;
	private MediaRecorder mediaRecorder;
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.awaken_main);
		
		final Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		// Turn on the screen unless we are being launched from the AlarmAlert
		// subclass.
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
		
		// hide status bar
		win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		TextView unrealTime = (TextView) findViewById(R.id.fakeTime);
		unrealTime.setText(getFakeCurrentTime());
		
		String[] wakeUpInfos = {"RISE AND SHINE, MOTHERFUCKER!!!", "Kill Zombie process!!!",};
		TextView wakeUpLabel = (TextView) findViewById(R.id.wakeUpLabel);
		wakeUpLabel.setText(wakeUpInfos[random.nextInt(wakeUpInfos.length)]);
		
		// measure surrounding loudness level
		logger.debug("Measuring surrounding noise level...");
		try {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.setOutputFile("/dev/null");
			mediaRecorder.prepare();
			mediaRecorder.start();
			mediaRecorder.getMaxAmplitude(); // initialize measurement
		} catch (IOException e) {
			logger.error(e);
		}
		
		
		new Handler().postDelayed(() -> {
			
			int amplitude = mediaRecorder.getMaxAmplitude();
			double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
			logger.info("Surrounding noise amplitude: " + amplitudeDb + " dB");
			mediaRecorder.stop();
			mediaRecorder.release();
			
			startAlarm(amplitudeDb);
			
		}, 1000);
		
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void startAlarm(double noiseLevel) {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
		
		try {
			currentRingtone = randomRingtone();
			logger.debug("Current Ringtone: " + getRingtoneName(currentRingtone));
			
			Uri ringUri = Uri.fromFile(currentRingtone);
			//			Uri ringUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hopes_quiet);
			
			final float volume = (float) calculateAlarmVolume(noiseLevel);
			logger.debug("Alarm volume level: " + volume);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(getApplicationContext(), ringUri);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mediaPlayer.setVolume(volume, volume);
			mediaPlayer.setLooping(true);
			
			AudioAttributes aa = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
					.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
					//					.setFlags(FLAG_AUDIBILITY_ENFORCED)
					.build();
			mediaPlayer.setAudioAttributes(aa);
			
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) {
			logger.error(e);
		}
		
		List<String> ringtoneNames = new ArrayList<>();
		for (File ringtone : getAllRingtones()) {
			ringtoneNames.add(getRingtoneName(ringtone));
		}
		Collections.shuffle(ringtoneNames); // that's the evilest thing i can imagine
		
		listAdapter = new ArrayAdapter<>(this, R.layout.list_item, ringtoneNames);
		
		ListView listView = (ListView) findViewById(R.id.ringtones_answer_list);
		listView.setAdapter(listAdapter);
		
		listView.setOnItemClickListener((adapter1, v, position, id) -> {
			String selected = (String) adapter1.getItemAtPosition(position);
			if (selected.equals(getRingtoneName(currentRingtone))) {
				stopAlarm();
				Toast.makeText(getApplicationContext(), "Congratulations! You have woken up.", Toast.LENGTH_LONG)
						.show();
			} else {
				wrongAnswer();
			}
		});
	}
	
	private double calculateAlarmVolume(double noiseLevel) {
		final double[] transformFactors = new double[]{ // noise dB -> alarm volume
				35.0, 0.2, // low limit
				80.0, 1.0, // high limit
		};
		if (noiseLevel <= transformFactors[0])
			return transformFactors[1];
		if (noiseLevel >= transformFactors[2])
			return transformFactors[3];
		double fraction = (noiseLevel - transformFactors[0]) / (transformFactors[2] - transformFactors[0]);
		return transformFactors[1] + fraction * (transformFactors[3] - transformFactors[1]);
	}
	
	
	private void wrongAnswer() {
		Toast.makeText(getApplicationContext(), "Wrong answer, you morron!", Toast.LENGTH_LONG)
				.show();
		Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(1000);
		listAdapter.clear();
		List<String> ringtoneNames = new ArrayList<>();
		for (File ringtone : getAllRingtones()) {
			ringtoneNames.add(getRingtoneName(ringtone));
		}
		Collections.shuffle(ringtoneNames);
		listAdapter.addAll(ringtoneNames);
		listAdapter.notifyDataSetChanged();
	}
	
	private String getFakeCurrentTime() {
		// 2 hours forward
		DateTime fakeTime = DateTime.now().plusMinutes(random.nextInt(2 * 60));
		return fakeTime.toString("HH:mm");
	}
	
	private String getRingtoneName(File ringtone) {
		return ringtone.getName().replaceAll("\\.mp3$", "");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopAlarm();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// disable back key
		if (mediaPlayer.isPlaying()) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MENU) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private void stopAlarm() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}
	
	private void ensureSoundIsOn() {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (am.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}
	
	private String getExternalStorageDirectory() {
		String mExternalDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
		// fucking samsung workaround
		if (android.os.Build.DEVICE.contains("samsung") || android.os.Build.MANUFACTURER.contains("samsung")) {
			File f = new File("/storage/extSdCard");
			if (f.exists() && f.isDirectory()) {
				mExternalDirectory = "/storage/extSdCard";
			} else {
				f = new File("/storage/external_sd");
				if (f.exists() && f.isDirectory()) {
					mExternalDirectory = "/storage/external_sd";
				}
			}
		}
		return mExternalDirectory;
	}
	
	private File randomRingtone() {
		List<File> ringtones = getAllRingtones();
		return ringtones.get(random.nextInt(ringtones.size()));
	}
	
	private List<File> getAllRingtones() {
		String ringtonesPath = getExternalStorageDirectory() + "/Android/data/igrek.forceawaken/ringtones";
		File ringtonesDir = new File(ringtonesPath);
		return Arrays.asList(ringtonesDir.listFiles());
	}
	
	
}