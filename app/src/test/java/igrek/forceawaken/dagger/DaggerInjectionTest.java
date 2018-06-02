package igrek.forceawaken.dagger;

import org.junit.Before;
import org.junit.Test;

import igrek.forceawaken.MainActivity;
import igrek.forceawaken.dagger.test.BaseDaggerTest;

import static org.mockito.Mockito.mock;

public class DaggerInjectionTest extends BaseDaggerTest {
	
	@Before
	public void setUp() {
		MainActivity activity = mock(MainActivity.class);
		// Dagger init test
		DaggerIOC.initTest(activity);
		DaggerIOC.getTestComponent().inject(this);
	}
	
	@Test
	public void testInjections() {
		System.out.println("injected activity: " + activity.toString());
	}
	
}
