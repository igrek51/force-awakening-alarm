package igrek.forceawaken.dagger;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import igrek.forceawaken.MainApplication;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.alarm.AlarmManagerService;
import igrek.forceawaken.service.alarm.VibratorService;
import igrek.forceawaken.service.filesystem.ExternalCardService;
import igrek.forceawaken.service.filesystem.InternalDataService;
import igrek.forceawaken.service.persistence.AlarmsPersistenceService;
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
public class FactoryModule {
	
	private MainApplication application;
	
	public FactoryModule(MainApplication application) {
		this.application = application;
	}
	
	@Provides
	protected Application provideApplication() {
		return application;
	}
	
	@Provides
	protected Context provideContext() {
		return application.getApplicationContext();
	}
	
	@Provides
	protected Activity provideActivity() {
		return application.getCurrentActivity();
	}
	
	@Provides
	protected Logger provideLogger() {
		return LoggerFactory.getLogger();
	}
	
	/* Services */
	
	@Provides
	@Singleton
	protected UserInfoService provideUserInfoService(Logger logger, Activity activity) {
		return new UserInfoService(logger, activity);
	}
	
	@Provides
	@Singleton
	protected NoiseDetectorService provideNoiseDetectorService(Logger logger) {
		return new NoiseDetectorService(logger);
	}
	
	@Provides
	@Singleton
	protected WindowManagerService provideWindowManagerService(Activity activity) {
		return new WindowManagerService(activity);
	}
	
	@Provides
	@Singleton
	protected AlarmPlayerService provideAlarmPlayerService(Activity activity) {
		return new AlarmPlayerService(activity);
	}
	
	@Provides
	@Singleton
	protected RingtoneManagerService provideRingtoneManagerService(ExternalCardService externalCardService) {
		return new RingtoneManagerService(externalCardService);
	}
	
	@Provides
	@Singleton
	protected VibratorService provideVibratorService(Activity activity) {
		return new VibratorService(activity);
	}
	
	@Provides
	@Singleton
	protected AlarmTimeService provideAlarmTimeService() {
		return new AlarmTimeService();
	}
	
	@Provides
	@Singleton
	protected AlarmManagerService provideAlarmManagerService(Activity activity, AlarmsPersistenceService alarmsPersistenceService) {
		return new AlarmManagerService(activity, alarmsPersistenceService);
	}
	
	@Provides
	@Singleton
	protected AccelerometerService provideAccelerometerService(Activity activity) {
		return new AccelerometerService(activity);
	}
	
	@Provides
	@Singleton
	protected VolumeCalculatorService provideVolumeCalculatorService(AccelerometerService accelerometerService) {
		return new VolumeCalculatorService(accelerometerService);
	}
	
	@Provides
	@Singleton
	protected AwakeTaskService provideAwakeTaskService(Activity activity, Logger logger) {
		return new AwakeTaskService(activity, logger);
	}
	
	@Provides
	@Singleton
	protected ExternalCardService provideExternalCardService() {
		return new ExternalCardService();
	}
	
	@Provides
	@Singleton
	protected AlarmsPersistenceService provideAlarmsPersistenceService(InternalDataService internalDataService) {
		return new AlarmsPersistenceService(internalDataService);
	}
	
	@Provides
	@Singleton
	protected InternalDataService provideInternalDataService(Context context) {
		return new InternalDataService(context);
	}
	
}
