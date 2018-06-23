package igrek.forceawaken.service.volume;

import android.media.MediaRecorder;
import android.os.Handler;

import java.io.IOException;

import igrek.forceawaken.logger.Logger;

public class NoiseDetectorService {
	
	private Logger logger;
	private MediaRecorder mediaRecorder;
	
	public NoiseDetectorService(Logger logger) {
		this.logger = logger;
	}
	
	public void measureNoiseLevel(long millis, NoiseLevelMeasureCallback completeCallback) {
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
			logger.debug("Surrounding noise amplitude: " + amplitudeDb + " dB");
			mediaRecorder.stop();
			mediaRecorder.release();
			
			completeCallback.onComplete(amplitudeDb);
			
		}, millis);
	}
	
	public interface NoiseLevelMeasureCallback {
		void onComplete(double amplitudeDb);
	}
	
}
