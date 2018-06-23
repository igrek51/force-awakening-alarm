package igrek.forceawaken.dagger.base;

import javax.inject.Singleton;

import dagger.Component;
import igrek.forceawaken.dagger.FactoryComponent;
import igrek.forceawaken.dagger.FactoryModule;
import igrek.forceawaken.dagger.SimpleDaggerInjectionTest;
import igrek.forceawaken.service.task.AwakeTaskServiceTest;
import igrek.forceawaken.service.volume.NoiseDetectorTest;

@Singleton
@Component(modules = {FactoryModule.class})
public interface TestComponent extends FactoryComponent {
	
	// to use dagger injection in tests
	void inject(SimpleDaggerInjectionTest there);
	
	void inject(BaseDaggerTest there);
	
	void inject(AwakeTaskServiceTest there);
	
	void inject(NoiseDetectorTest there);
	
}