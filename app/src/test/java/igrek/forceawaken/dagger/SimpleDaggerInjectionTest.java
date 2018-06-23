package igrek.forceawaken.dagger;

import android.app.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import igrek.forceawaken.BuildConfig;
import igrek.forceawaken.MainApplication;
import igrek.forceawaken.dagger.base.DaggerTestComponent;
import igrek.forceawaken.dagger.base.TestComponent;
import igrek.forceawaken.dagger.base.TestModule;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = MainApplication.class)
public class SimpleDaggerInjectionTest {
	
	@Inject
	Application application;
	
	@Before
	public void setUp() {
		MainApplication application = (MainApplication) RuntimeEnvironment.application;
		
		TestComponent component = DaggerTestComponent.builder()
				.factoryModule(new TestModule(application))
				.build();
		
		DaggerIOC.setFactoryComponent(component);
		
		component.inject(this);
	}
	
	@Test
	public void testApplicationInjection() {
		System.out.println("injected application: " + application.toString());
	}
	
}
