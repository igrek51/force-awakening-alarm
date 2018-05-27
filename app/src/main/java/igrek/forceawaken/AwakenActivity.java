package igrek.forceawaken;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;

public class AwakenActivity extends AppCompatActivity {
	
	Ringtone ringtone;
	MediaPlayer mMediaPlayer;
	Random random = new Random();
	Logger logger = LoggerFactory.getLogger();
	
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
	}
	
	private String getFakeCurrentTime() {
		// 2 hours forward
		DateTime fakeTime = DateTime.now().plusMinutes(random.nextInt(2 * 60));
		return fakeTime.toString("HH:mm");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
		
		try {
			
			File ringtone = randomRingtone();
			logger.debug("Ringtone: " + ringtone.getName().replaceAll("\\.mp3$", ""));
			
			Uri ringUri = Uri.fromFile(ringtone);
			
			//			Uri ringUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sweetwater);
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(getApplicationContext(), ringUri);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mMediaPlayer.setVolume(1, 1);
			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStop() {
		//FIXME turning screen of stops the alarm
		super.onStop();
		if (ringtone != null) {
			ringtone.stop();
		}
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
		String ringtonesPath = getExternalStorageDirectory() + "/Android/data/igrek.forceawaken/ringtones";
		File ringtonesDir = new File(ringtonesPath);
		List<File> ringtones = Arrays.asList(ringtonesDir.listFiles());
		return ringtones.get(random.nextInt(ringtones.size()));
	}
}