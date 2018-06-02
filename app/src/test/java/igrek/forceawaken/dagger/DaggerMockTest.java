package igrek.forceawaken.dagger;

import org.junit.Before;
import org.junit.Test;

import igrek.forceawaken.MainActivity;
import igrek.forceawaken.dagger.test.BaseDaggerTest;

import static org.mockito.Mockito.mock;

public class DaggerMockTest extends BaseDaggerTest {
	
	@Before
	public void setUp() {
		MainActivity activity = mock(MainActivity.class);
		// Dagger init test
		DaggerIOC.initTest(activity);
		DaggerIOC.getTestComponent().inject(this);
	}
	
	@Test
	public void testMocks() {
		logger.info("dupa");
	}
	
}
