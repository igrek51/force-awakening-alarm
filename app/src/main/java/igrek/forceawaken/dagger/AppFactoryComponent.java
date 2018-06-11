package igrek.forceawaken.dagger;

import javax.inject.Singleton;

import dagger.Component;
import igrek.forceawaken.AwakenActivity;
import igrek.forceawaken.MainActivity;
import igrek.forceawaken.service.alarm.AlarmManagerService;
import igrek.forceawaken.service.alarm.VibratorService;
import igrek.forceawaken.service.ringtone.AlarmPlayerService;
import igrek.forceawaken.service.ringtone.RingtoneManagerService;
import igrek.forceawaken.service.sensors.AccelerometerService;
import igrek.forceawaken.service.time.AlarmTimeService;
import igrek.forceawaken.service.ui.WindowManagerService;
import igrek.forceawaken.service.ui.info.UserInfoService;
import igrek.forceawaken.service.volume.NoiseDetectorService;
import igrek.forceawaken.service.volume.VolumeCalculatorService;
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
	
	/* Services */
	
	void inject(UserInfoService there);
	
	void inject(NoiseDetectorService there);
	
	void inject(WindowManagerService there);
	
	void inject(AlarmPlayerService there);
	
	void inject(RingtoneManagerService there);
	
	void inject(VibratorService there);
	
	void inject(AlarmTimeService there);
	
	void inject(AlarmManagerService there);
	
	void inject(AccelerometerService there);
	
	void inject(VolumeCalculatorService there);
	
}
