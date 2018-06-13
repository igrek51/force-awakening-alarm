package igrek.forceawaken.service.task;

import org.junit.Before;
import org.junit.Test;

import igrek.forceawaken.AwakenActivity;
import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.dagger.test.BaseDaggerTest;

import static org.mockito.Mockito.mock;

public class AwakeTaskServiceTest extends BaseDaggerTest {
	
	@Before
	public void setUp() {
		AwakenActivity activity = mock(AwakenActivity.class);
		// Dagger init test
		DaggerIOC.initTest(activity);
		DaggerIOC.getTestComponent().inject(this);
	}
	
	@Test
	public void test_getRandomTask() {
		AwakeTaskService awakeTaskService = new AwakeTaskService();
		for (int i = 0; i < 10; i++) {
			logger.debug(awakeTaskService.getRandomTask());
		}
	}
}
