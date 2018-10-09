package igrek.forceawaken;

import android.app.Activity;
import android.app.Application;

import igrek.forceawaken.activity.CurrentActivityListener;
import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;

public class MainApplication extends Application {
	
	private Logger logger = LoggerFactory.getLogger();
	private CurrentActivityListener currentActivityListener = new CurrentActivityListener();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		registerActivityLifecycleCallbacks(currentActivityListener);
		
		try {
			// Dagger Container init
			DaggerIOC.init(this);
		} catch (Throwable t) {
			logger.fatal(currentActivityListener.getCurrentActivity(), t);
		}
		
		// catch all uncaught exceptions
		Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler((thread, th) -> {
			logger.fatal(currentActivityListener.getCurrentActivity(), th);
			//pass further to OS
			defaultUEH.uncaughtException(thread, th);
		});
		
		logger.info("Application has been started");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		unregisterActivityLifecycleCallbacks(currentActivityListener);
	}
	
	public Activity getCurrentActivity() {
		return currentActivityListener.getCurrentActivity();
	}
}
