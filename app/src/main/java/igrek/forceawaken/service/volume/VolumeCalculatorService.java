package igrek.forceawaken.service.volume;

import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.sensors.AccelerometerService;

public class VolumeCalculatorService {
	
	private Logger logger = LoggerFactory.getLogger();
	
	final double[] noiseVolTransform = new double[]{ // noise dB -> alarm volume
			35.0, 0.4, // low limit
			70.0, 0.8, // high limit
	};
	
	private final double speakerDownCompensation = 1.2;
	
	private final double globalVolume = 0.15;
	
	private AccelerometerService accelerometerService;
	
	public VolumeCalculatorService(AccelerometerService accelerometerService) {
		this.accelerometerService = accelerometerService;
	}
	
	public double calcVolumeByNoise(double noiseLevel) {
		if (noiseLevel <= noiseVolTransform[0])
			return noiseVolTransform[1];
		if (noiseLevel >= noiseVolTransform[2])
			return noiseVolTransform[3];
		double fraction = (noiseLevel - noiseVolTransform[0]) / (noiseVolTransform[2] - noiseVolTransform[0]);
		return noiseVolTransform[1] + fraction * (noiseVolTransform[3] - noiseVolTransform[1]);
	}
	
	public double calcFinalVolume(double noiseLevel) {
		double vol1 = calcVolumeByNoise(noiseLevel);
		boolean speakerDown = accelerometerService.isSpeakerRotatedDown();
		if (speakerDown) { // speaker rotated down - increase volume
			logger.debug("Speaker is rotated down - boosting volume level");
			vol1 *= speakerDownCompensation;
			if (vol1 > 1.0) // cut off
				vol1 = 1.0;
		}
		return vol1 * globalVolume;
	}
}
