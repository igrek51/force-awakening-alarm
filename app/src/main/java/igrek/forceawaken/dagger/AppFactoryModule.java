package igrek.forceawaken.dagger;

import android.app.Activity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.alarm.AlarmManagerService;
import igrek.forceawaken.service.alarm.VibratorService;
import igrek.forceawaken.service.ringtone.AlarmPlayerService;
import igrek.forceawaken.service.ringtone.RingtoneManagerService;
import igrek.forceawaken.service.sensors.AccelerometerService;
import igrek.forceawaken.service.task.AwakeTaskService;
import igrek.forceawaken.service.time.AlarmTimeService;
import igrek.forceawaken.service.ui.WindowManagerService;
import igrek.forceawaken.service.ui.info.UserInfoService;
import igrek.forceawaken.service.volume.NoiseDetectorService;
import igrek.forceawaken.service.volume.VolumeCalculatorService;

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
	
	@Provides
	@Singleton
	protected VibratorService provideVibratorService() {
		return new VibratorService();
	}
	
	@Provides
	@Singleton
	protected AlarmTimeService provideAlarmTimeService() {
		return new AlarmTimeService();
	}
	
	@Provides
	@Singleton
	protected AlarmManagerService provideAlarmManagerService() {
		return new AlarmManagerService();
	}
	
	@Provides
	@Singleton
	protected AccelerometerService provideAccelerometerService() {
		return new AccelerometerService();
	}
	
	@Provides
	@Singleton
	protected VolumeCalculatorService provideVolumeCalculatorService(AccelerometerService accelerometerService) {
		return new VolumeCalculatorService(accelerometerService);
	}
	
	@Provides
	@Singleton
	protected AwakeTaskService provideAwakeTaskService() {
		return new AwakeTaskService();
	}
	
}
