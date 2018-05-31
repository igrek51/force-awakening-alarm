package igrek.forceawaken;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
	
	private MediaPlayer mMediaPlayer;
	private Random random = new Random();
	private Logger logger = LoggerFactory.getLogger();
	private File currentRingtone;
	private ArrayAdapter<String> listAdapter;
	
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
		
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		//		am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
		
		try {
			currentRingtone = randomRingtone();
			logger.debug("Current Ringtone: " + getRingtoneName(currentRingtone));
			
			Uri ringUri = Uri.fromFile(currentRingtone);
			//			Uri ringUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hopes_quiet);
			
			final float volume = 0.3f;
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(getApplicationContext(), ringUri);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mMediaPlayer.setVolume(volume, volume);
			mMediaPlayer.setLooping(true);
			
			AudioAttributes aa = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
					.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
					//					.setFlags(FLAG_AUDIBILITY_ENFORCED)
					.build();
			mMediaPlayer.setAudioAttributes(aa);
			
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
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
				stopRingtone();
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
		stopRingtone();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// disable back key
		if (mMediaPlayer.isPlaying()) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MENU) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private void stopRingtone() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
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