package igrek.forceawaken.logger;

public class LoggerFactory {
	
	public static final LogLevel CONSOLE_LEVEL = LogLevel.TRACE;
	public static final String LOG_TAG = "dupa";
	public static final boolean SHOW_EXCEPTIONS_TRACE = true;
	public static final LogLevel SHOW_TRACE_DETAILS_LEVEL = LogLevel.DEBUG;
	
	public static Logger getLogger() {
		return new Logger();
	}
	
}
