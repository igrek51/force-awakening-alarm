package igrek.forceawaken.service.volume;

import org.junit.Test;

import javax.inject.Inject;

import igrek.forceawaken.dagger.base.BaseDaggerTest;
import igrek.forceawaken.dagger.base.TestComponent;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class NoiseDetectorTest extends BaseDaggerTest {
	
	@Inject
	VolumeCalculatorService vc;
	
	@Override
	protected void daggerInject(TestComponent component) {
		component.inject(this);
	}
	
	@Test
	public void testCalculateAlarmVolume() {
		//		VolumeCalculatorService vc = new VolumeCalculatorService();
		assertThat(vc.calcVolumeByNoise(0.0)).isEqualTo(vc.noiseVolTransform[1]);
		assertThat(vc.calcVolumeByNoise(vc.noiseVolTransform[0])).isEqualTo(vc.noiseVolTransform[1]);
		assertThat(vc.calcVolumeByNoise(vc.noiseVolTransform[2])).isEqualTo(vc.noiseVolTransform[3]);
		double middle = (vc.noiseVolTransform[0] + vc.noiseVolTransform[2]) / 2;
		assertThat(vc.calcVolumeByNoise(middle)).isGreaterThan(vc.noiseVolTransform[1])
				.isLessThan(vc.noiseVolTransform[3]);
	}
	
}
