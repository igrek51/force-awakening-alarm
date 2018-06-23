package igrek.forceawaken.dagger.base;

import android.app.Activity;

import org.mockito.Mockito;

import igrek.forceawaken.MainApplication;
import igrek.forceawaken.dagger.FactoryModule;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.mock.LoggerMock;

public class TestModule extends FactoryModule {
	
	public TestModule(MainApplication application) {
		super(application);
	}
	
	@Override
	protected Logger provideLogger() {
		return new LoggerMock();
	}
	
	@Override
	protected Activity provideActivity() {
		return Mockito.mock(Activity.class);
	}
}
