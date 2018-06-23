package igrek.forceawaken.service;

import org.junit.Test;

import javax.inject.Inject;

import igrek.forceawaken.dagger.base.BaseDaggerTest;
import igrek.forceawaken.dagger.base.TestComponent;
import igrek.forceawaken.service.volume.VolumeCalculatorService;

public class ServiceInjectTest extends BaseDaggerTest {
	
	@Inject
	VolumeCalculatorService service;
	
	@Override
	protected void injectThis(TestComponent component) {
		component.inject(this);
	}
	
	@Test
	public void test_getRandomTask() {
		logger.info("VolumeCalculatorService: " + service);
	}
}
