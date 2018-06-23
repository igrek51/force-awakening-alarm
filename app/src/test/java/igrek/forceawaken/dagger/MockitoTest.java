package igrek.forceawaken.dagger;

import org.junit.Before;
import org.junit.Test;

import igrek.forceawaken.MainApplication;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockitoTest {
	
	private MainApplication application;
	
	@Before
	public void setUp() {
		application = mock(MainApplication.class);
		
		when(application.toString()).thenReturn("dupa");
	}
	
	@Test
	public void testMocks() {
		System.out.println(application);
		assertThat(application.toString()).isEqualTo("dupa");
	}
	
}
