package igrek.forceawaken;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

public class AwakenActivity extends AppCompatActivity {
	
	Ringtone ringtone;
	MediaPlayer mMediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.awaken_main);
		
		final Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		// Turn on the screen unless we are being launched from the AlarmAlert
		// subclass.
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		// TUrn the sound ON !
		//		if (am.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
		//			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		
		//		am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_PLAY_SOUND);
		// someone could turned this off
		am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
		
		//		Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Uri ringUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sweetwater);
		//		ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringUri);
		//		ringtone.play();
		
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(getApplicationContext(), ringUri);
			
			//			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mMediaPlayer.setVolume(1, 1);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			//			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//		MediaPlayer mMediaPlayer = new MediaPlayer();
		//		mMediaPlayer.setDataSource(this, ringUri);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (ringtone != null) {
			ringtone.stop();
		}
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
	}
}