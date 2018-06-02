package igrek.forceawaken.dagger;

import android.app.Activity;

import igrek.forceawaken.dagger.test.DaggerTestComponent;
import igrek.forceawaken.dagger.test.TestComponent;
import igrek.forceawaken.dagger.test.TestModuleFactory;

public class DaggerIOC {
	
	private static AppFactoryComponent appComponent;
	
	private DaggerIOC() {
	}
	
	public static void init(Activity activity) {
		appComponent = DaggerAppFactoryComponent.builder()
				.appFactoryModule(new AppFactoryModule(activity))
				.build();
	}
	
	public static AppFactoryComponent getAppComponent() {
		return appComponent;
	}
	
	public static void initTest(Activity activity) {
		appComponent = DaggerTestComponent.builder()
				.appFactoryModule(TestModuleFactory.getTestModule(activity))
				.build();
	}
	
	public static TestComponent getTestComponent() {
		return (TestComponent) appComponent;
	}
}
