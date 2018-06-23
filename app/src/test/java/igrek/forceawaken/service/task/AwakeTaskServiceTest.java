package igrek.forceawaken.service.task;

import org.junit.Test;

import javax.inject.Inject;

import igrek.forceawaken.dagger.base.BaseDaggerTest;
import igrek.forceawaken.dagger.base.TestComponent;

public class AwakeTaskServiceTest extends BaseDaggerTest {
	
	@Inject
	AwakeTaskService awakeTaskService;
	
	@Override
	protected void injectThis(TestComponent component) {
		component.inject(this);
	}
	
	@Test
	public void test_getRandomTask() {
		logger.info("awakeTaskService: " + awakeTaskService);
		for (int i = 0; i < 10; i++) {
			logger.debug(awakeTaskService.getRandomTask());
		}
	}
}
