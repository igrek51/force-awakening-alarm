package igrek.forceawaken.dagger;

import javax.inject.Singleton;

import dagger.Component;
import igrek.forceawaken.MainActivity;
import igrek.forceawaken.service.info.UserInfoService;
import igrek.forceawaken.ui.errorcheck.UIErrorHandler;

/**
 * Dagger will be injecting to those classes
 */
@Singleton
@Component(modules = {AppFactoryModule.class})
public interface AppFactoryComponent {
	
	void inject(MainActivity there);
	
	void inject(UIErrorHandler there);
	
	void inject(UserInfoService there);
	
}
