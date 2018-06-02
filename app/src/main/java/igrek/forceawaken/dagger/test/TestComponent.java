package igrek.forceawaken.dagger.test;

import javax.inject.Singleton;

import dagger.Component;
import igrek.forceawaken.dagger.AppFactoryComponent;
import igrek.forceawaken.dagger.AppFactoryModule;

@Singleton
@Component(modules = {AppFactoryModule.class})
public interface TestComponent extends AppFactoryComponent {
	
	// to use dagger injection in tests
	void inject(BaseDaggerTest there);
	
}