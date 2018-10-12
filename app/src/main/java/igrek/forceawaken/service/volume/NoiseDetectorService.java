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
		try {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.setOutputFile("/dev/null");
			mediaRecorder.prepare();
			mediaRecorder.start();
			mediaRecorder.getMaxAmplitude(); // initialize measurement
			
			new Handler().postDelayed(() -> {
				
				try {
					int amplitude = mediaRecorder.getMaxAmplitude();
					double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
					logger.info("Surrounding noise amplitude: " + amplitudeDb + " dB");
					mediaRecorder.stop();
					mediaRecorder.release();
					completeCallback.onComplete(amplitudeDb);
				} catch (Exception e) {
					logger.error(e);
					completeCallback.onComplete(0);
				}
				
			}, millis);
			
		} catch (IOException | IllegalStateException e) {
			logger.error(e);
			completeCallback.onComplete(0);
		}
	}
	
	public interface NoiseLevelMeasureCallback {
		void onComplete(double amplitudeDb);
	}
	
}
