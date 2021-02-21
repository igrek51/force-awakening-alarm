package igrek.forceawaken.ringtone

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory

class AlarmPlayerService(
        activity: LazyInject<Activity> = appFactory.activity,
) {
    private val activity by LazyExtractor(activity)

    private var mediaPlayer = MediaPlayer()
    private val audioManager = activity.get().getSystemService(Context.AUDIO_SERVICE) as AudioManager

    var volume = 0.0
        get() = if (isPlaying) field else 0.0
        set(volume) {
            var volume = volume
            if (volume > 1.0) volume = 1.0
            field = volume
            mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
        }

    var alarmId: Long = 0

    val isPlaying: Boolean
        get() = mediaPlayer.isPlaying


    fun playAlarm(ringtoneUri: Uri, volume: Double) {
        // set global alarm volume level to max
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0)
        this.volume = volume
        mediaPlayer.setDataSource(activity.getApplicationContext(), ringtoneUri)
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM)
        mediaPlayer.setLooping(true)
        val aa: AudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) //.setFlags(FLAG_AUDIBILITY_ENFORCED)
                .build()
        mediaPlayer.setAudioAttributes(aa)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun stopAlarm() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop()
            mediaPlayer = MediaPlayer()
        }
    }

    fun ensureSoundIsOn() {
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
    }
}