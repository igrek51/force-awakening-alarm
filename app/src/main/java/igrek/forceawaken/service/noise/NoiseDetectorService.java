package igrek.forceawaken.service.noise;

import android.media.MediaRecorder;
import android.os.Handler;

import java.io.IOException;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.logger.Logger;

public class NoiseDetectorService {
	
	@Inject
	Logger logger;
	
	private MediaRecorder mediaRecorder;
	
	final double[] noiseVolTransform = new double[]{ // noise dB -> alarm volume
			35.0, 0.3, // low limit
			70.0, 1.0, // high limit
	};
	
	public NoiseDetectorService() {
		DaggerIOC.getAppComponent().inject(this);
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
	
	public double calculateAlarmVolume(double noiseLevel) {
		if (noiseLevel <= noiseVolTransform[0])
			return noiseVolTransform[1];
		if (noiseLevel >= noiseVolTransform[2])
			return noiseVolTransform[3];
		double fraction = (noiseLevel - noiseVolTransform[0]) / (noiseVolTransform[2] - noiseVolTransform[0]);
		return noiseVolTransform[1] + fraction * (noiseVolTransform[3] - noiseVolTransform[1]);
	}
}
