package igrek.forceawaken.dagger;

import android.app.Activity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.info.UserInfoService;
import igrek.forceawaken.service.noise.NoiseDetectorService;
import igrek.forceawaken.service.player.AlarmPlayerService;
import igrek.forceawaken.service.ringtone.RingtoneManagerService;
import igrek.forceawaken.service.ui.WindowManagerService;

/**
 * Module with providers. These classes can be injected
 */
@Module
public class AppFactoryModule {
	
	protected Activity activity;
	
	public AppFactoryModule(Activity activity) {
		this.activity = activity;
	}
	
	@Provides
	@Singleton
	protected Activity provideActivity() {
		return activity;
	}
	
	@Provides
	@Singleton
	protected Logger provideLogger() {
		return LoggerFactory.getLogger();
	}
	
	/* Services */
	
	@Provides
	@Singleton
	protected UserInfoService provideUserInfoService() {
		return new UserInfoService();
	}
	
	@Provides
	@Singleton
	protected NoiseDetectorService provideNoiseDetectorService() {
		return new NoiseDetectorService();
	}
	
	@Provides
	@Singleton
	protected WindowManagerService provideWindowManagerService() {
		return new WindowManagerService();
	}
	
	@Provides
	@Singleton
	protected AlarmPlayerService provideAlarmPlayerService() {
		return new AlarmPlayerService();
	}
	
	@Provides
	@Singleton
	protected RingtoneManagerService provideRingtoneManagerService() {
		return new RingtoneManagerService();
	}
	
}
