package igrek.forceawaken.service.time;

import org.joda.time.DateTime;

import java.util.Random;

public class AlarmTimeService {
	
	private Random random = new Random();
	
	public AlarmTimeService() {
	}
	
	public DateTime getFakeCurrentTime() {
		// 2 hours forward
		return DateTime.now().plusMinutes(random.nextInt(2 * 60));
	}
}
