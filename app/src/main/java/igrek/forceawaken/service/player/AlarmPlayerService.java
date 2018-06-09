package igrek.forceawaken.service.player;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;

public class AlarmPlayerService {
	
	@Inject
	Activity activity;
	
	private MediaPlayer mediaPlayer;
	
	public AlarmPlayerService() {
		DaggerIOC.getAppComponent().inject(this);
		mediaPlayer = new MediaPlayer();
	}
	
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}
	
	public void setVolume(double volume) {
		mediaPlayer.setVolume((float) volume, (float) volume);
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void playAlarm(Uri ringtoneUri, double volume) throws IOException {
		// set global alarm volume level to max
		AudioManager am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
		
		setVolume(volume);
		
		mediaPlayer.setDataSource(activity.getApplicationContext(), ringtoneUri);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		mediaPlayer.setLooping(true);
		
		AudioAttributes aa = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
				.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
				//.setFlags(FLAG_AUDIBILITY_ENFORCED)
				.build();
		mediaPlayer.setAudioAttributes(aa);
		
		mediaPlayer.prepare();
		mediaPlayer.start();
	}
	
	public void stopAlarm() {
		if (mediaPlayer.isPlaying())
			mediaPlayer.stop();
	}
	
	public void ensureSoundIsOn() {
		AudioManager am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		if (am.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}
}
