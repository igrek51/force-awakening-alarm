package igrek.forceawaken.logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

public class Logger {
	
	// package-private constructor
	Logger() {
	}
	
	public void error(String message) {
		log(message, LogLevel.ERROR, "[ERROR] ");
	}
	
	public void error(Throwable ex) {
		log(ex.getMessage(), LogLevel.ERROR, "[EXCEPTION - " + ex.getClass().getName() + "] ");
		printExceptionStackTrace(ex);
	}
	
	public void errorUncaught(Throwable ex) {
		log(ex.getMessage(), LogLevel.FATAL, "[UNCAUGHT EXCEPTION - " + ex.getClass()
				.getName() + "] ");
		printExceptionStackTrace(ex);
	}
	
	public void fatal(final Activity activity, String e) {
		log(e, LogLevel.FATAL, "[FATAL ERROR] ");
		if (activity == null) {
			error("FATAL ERROR: No activity");
			return;
		}
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);
		dlgAlert.setMessage(e);
		dlgAlert.setTitle("Critical error");
		dlgAlert.setPositiveButton("Close app", (dialog, which) -> activity.finish());
		dlgAlert.setCancelable(false);
		dlgAlert.create().show();
	}
	
	public void fatal(final Activity activity, Throwable ex) {
		String e = ex.getClass().getName() + " - " + ex.getMessage();
		printExceptionStackTrace(ex);
		fatal(activity, e);
	}
	
	public void warn(String message) {
		log(message, LogLevel.WARN, "[warn] ");
	}
	
	public void info(String message) {
		log(message, LogLevel.INFO, "");
	}
	
	public void debug(String message) {
		log(message, LogLevel.DEBUG, "[debug] ");
	}
	
	public void trace(String message) {
		log(message, LogLevel.TRACE, "[trace] ");
	}
	
	public void trace() {
		log("Quick Trace: " + System.currentTimeMillis(), LogLevel.DEBUG, "[trace] ");
	}
	
	private void log(String message, LogLevel level, String logPrefix) {
		if (level.moreOrEqualImportantThan(LoggerFactory.CONSOLE_LEVEL)) {
			
			String consoleMessage;
			if (level.lessOrEqualImportantThan(LoggerFactory.SHOW_TRACE_DETAILS_LEVEL)) {
				final int stackTraceIndex = 4; // depends on nested methods count
				
				StackTraceElement ste = Thread.currentThread().getStackTrace()[stackTraceIndex];
				
				String methodName = ste.getMethodName();
				String fileName = ste.getFileName();
				int lineNumber = ste.getLineNumber();
				
				consoleMessage = logPrefix + methodName + "(" + fileName + ":" + lineNumber + "): " + message;
			} else {
				consoleMessage = logPrefix + message;
			}
			
			if (level.moreOrEqualImportantThan(LogLevel.ERROR)) {
				printError(consoleMessage);
			} else {
				printInfo(consoleMessage);
			}
		}
	}
	
	private void printExceptionStackTrace(Throwable ex) {
		if (LoggerFactory.SHOW_EXCEPTIONS_TRACE) {
			printError(Log.getStackTraceString(ex));
		}
	}
	
	private void printInfo(String msg) {
		Log.i(LoggerFactory.LOG_TAG, msg);
	}
	
	private void printError(String msg) {
		Log.e(LoggerFactory.LOG_TAG, msg);
	}
	
}
