package igrek.forceawaken.service.volume;

import org.junit.Before;
import org.junit.Test;

import igrek.forceawaken.AwakenActivity;
import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.dagger.test.BaseDaggerTest;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class NoiseDetectorTest extends BaseDaggerTest {
	
	@Before
	public void setUp() {
		AwakenActivity activity = mock(AwakenActivity.class);
		// Dagger init test
		DaggerIOC.initTest(activity);
		DaggerIOC.getTestComponent().inject(this);
	}
	
	@Test
	public void testCalculateAlarmVolume() {
		// FIXME get service from IOC container
		VolumeCalculatorService vc = volumeCalculatorService;
		//		VolumeCalculatorService vc = new VolumeCalculatorService();
		assertThat(vc.calcVolumeByNoise(0.0)).isEqualTo(vc.noiseVolTransform[1]);
		assertThat(vc.calcVolumeByNoise(vc.noiseVolTransform[0])).isEqualTo(vc.noiseVolTransform[1]);
		assertThat(vc.calcVolumeByNoise(vc.noiseVolTransform[2])).isEqualTo(vc.noiseVolTransform[3]);
		double middle = (vc.noiseVolTransform[0] + vc.noiseVolTransform[2]) / 2;
		assertThat(vc.calcVolumeByNoise(middle)).isGreaterThan(vc.noiseVolTransform[1])
				.isLessThan(vc.noiseVolTransform[3]);
	}
	
}
