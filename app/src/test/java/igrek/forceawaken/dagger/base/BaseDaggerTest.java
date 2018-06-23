package igrek.forceawaken.dagger.base;

import android.app.Activity;
import android.app.Application;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import igrek.forceawaken.BuildConfig;
import igrek.forceawaken.MainApplication;
import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.logger.Logger;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = MainApplication.class, manifest = "src/main/AndroidManifest.xml", packageName = "igrek.forceawaken")
public abstract class BaseDaggerTest {
	
	@Inject
	protected Application application;
	
	@Inject
	protected Activity activity;
	
	@Inject
	protected Logger logger;
	
	
	@Before
	public void setUp() {
		MainApplication application = (MainApplication) RuntimeEnvironment.application;
		
		TestComponent component = DaggerTestComponent.builder()
				.factoryModule(new TestModule(application))
				.build();
		
		DaggerIOC.setFactoryComponent(component);
		
		injectThis(component);
	}
	
	protected void injectThis(TestComponent component) {
		component.inject(this);
	}
}
