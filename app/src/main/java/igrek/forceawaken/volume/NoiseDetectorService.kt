package igrek.forceawaken.volume

import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import igrek.forceawaken.info.logger.Logger
import igrek.forceawaken.info.logger.LoggerFactory
import java.io.IOException
import kotlin.math.abs
import kotlin.math.log10

class NoiseDetectorService() {

    private val logger: Logger = LoggerFactory.logger

    fun measureNoiseLevel(millis: Long, completeCallback: NoiseLevelMeasureCallback) {
        // measure surrounding loudness level
        try {
            val mediaRecorder = MediaRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder.setOutputFile("/dev/null")
            mediaRecorder.prepare()
            mediaRecorder.start()
            mediaRecorder.maxAmplitude // initialize measurement
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    val amplitude: Int = mediaRecorder.maxAmplitude
                    val amplitudeDb = 20 * log10(abs(amplitude).toDouble())
                    logger.info("Surrounding noise amplitude: $amplitudeDb dB")
                    mediaRecorder.stop()
                    mediaRecorder.release()
                    completeCallback.onComplete(amplitudeDb)
                } catch (e: Exception) {
                    logger.error(e)
                    completeCallback.onComplete(0.0)
                }
            }, millis)
        } catch (e: IOException) {
            logger.error(e)
            completeCallback.onComplete(0.0)
        } catch (e: IllegalStateException) {
            logger.error(e)
            completeCallback.onComplete(0.0)
        }
    }

    interface NoiseLevelMeasureCallback {
        fun onComplete(amplitudeDb: Double)
    }
}