package igrek.forceawaken.dagger;

import android.app.Activity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.info.UserInfoService;
import igrek.forceawaken.service.noise.NoiseDetectorService;

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
	
}
