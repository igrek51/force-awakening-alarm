package igrek.forceawaken.dagger.test;

import android.app.Activity;

import igrek.forceawaken.dagger.AppFactoryModule;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.mock.LoggerMock;

public class TestModuleFactory {
	
	public static AppFactoryModule getTestModule(Activity activity) {
		return new AppFactoryModule(activity) {
			
			@Override
			protected Logger provideLogger() {
				return new LoggerMock();
			}
			
		};
	}
	
}
