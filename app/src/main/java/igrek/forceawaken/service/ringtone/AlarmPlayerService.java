package igrek.forceawaken.service.ringtone;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;

public class AlarmPlayerService {
	
	private Activity activity;
	private MediaPlayer mediaPlayer;
	private AudioManager audioManager;
	private double volume;
	
	public AlarmPlayerService(Activity activity) {
		this.activity = activity;
		audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		mediaPlayer = new MediaPlayer();
	}
	
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}
	
	public void setVolume(double volume) {
		if (volume > 1.0)
			volume = 1.0;
		this.volume = volume;
		mediaPlayer.setVolume((float) volume, (float) volume);
	}
	
	public double getVolume() {
		return isPlaying() ? this.volume : 0;
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void playAlarm(Uri ringtoneUri, double volume) throws IOException {
		// set global alarm volume level to max
		audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
		
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
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer = new MediaPlayer();
		}
	}
	
	public void ensureSoundIsOn() {
		if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}
}
