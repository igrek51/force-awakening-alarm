package igrek.forceawaken.mock;

import android.app.Activity;

import igrek.forceawaken.logger.LogLevel;
import igrek.forceawaken.logger.Logger;

public class LoggerMock extends Logger {
	
	@Override
	public void fatal(Activity activity, String e) {
		log(e, LogLevel.FATAL, "[FATAL ERROR] ", 4);
	}
	
	@Override
	protected void printInfo(String msg) {
		System.out.println(msg);
	}
	
	@Override
	protected void printError(String msg) {
		System.err.println(msg);
	}
	
}
