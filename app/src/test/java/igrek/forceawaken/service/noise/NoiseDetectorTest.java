package igrek.forceawaken.service.noise;

import org.junit.Before;
import org.junit.Test;

import igrek.forceawaken.MainActivity;
import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.dagger.test.BaseDaggerTest;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class NoiseDetectorTest extends BaseDaggerTest {
	
	@Before
	public void setUp() {
		MainActivity activity = mock(MainActivity.class);
		// Dagger init test
		DaggerIOC.initTest(activity);
		DaggerIOC.getTestComponent().inject(this);
	}
	
	@Test
	public void testCalculateAlarmVolume() {
		NoiseDetectorService nd = new NoiseDetectorService();
		assertThat(nd.calculateAlarmVolume(0.0)).isEqualTo(nd.noiseVolTransform[1]);
		assertThat(nd.calculateAlarmVolume(nd.noiseVolTransform[0])).isEqualTo(nd.noiseVolTransform[1]);
		assertThat(nd.calculateAlarmVolume(nd.noiseVolTransform[2])).isEqualTo(nd.noiseVolTransform[3]);
		double middle = (nd.noiseVolTransform[0] + nd.noiseVolTransform[2]) / 2;
		assertThat(nd.calculateAlarmVolume(middle)).isGreaterThan(nd.noiseVolTransform[1])
				.isLessThan(nd.noiseVolTransform[3]);
	}
	
}
