package igrek.forceawaken.service.ui;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;

public class WindowManagerService {
	
	@Inject
	Activity activity;
	
	public WindowManagerService() {
		DaggerIOC.getAppComponent().inject(this);
	}
	
	public void setFullscreen() {
		Window win = activity.getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		// Turn on the screen unless we are being launched from the AlarmAlert
		// subclass.
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
		// hide status bar
		win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
}
