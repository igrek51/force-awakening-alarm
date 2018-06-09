package igrek.forceawaken.dagger;

import javax.inject.Singleton;

import dagger.Component;
import igrek.forceawaken.AwakenActivity;
import igrek.forceawaken.MainActivity;
import igrek.forceawaken.service.info.UserInfoService;
import igrek.forceawaken.service.noise.NoiseDetectorService;
import igrek.forceawaken.service.player.AlarmPlayerService;
import igrek.forceawaken.service.ui.WindowManagerService;
import igrek.forceawaken.ui.errorcheck.UIErrorHandler;

/**
 * Dagger will be injecting to those classes
 */
@Singleton
@Component(modules = {AppFactoryModule.class})
public interface AppFactoryComponent {
	
	void inject(MainActivity there);
	
	void inject(AwakenActivity there);
	
	void inject(UIErrorHandler there);
	
	void inject(UserInfoService there);
	
	void inject(NoiseDetectorService there);
	
	void inject(WindowManagerService there);
	
	void inject(AlarmPlayerService there);
	
}
