package igrek.forceawaken.domain.alarm;

import org.joda.time.DateTime;

public class AlarmTrigger {
	
	private DateTime triggerTime;
	
	public AlarmTrigger(DateTime triggerTime) {
		this.triggerTime = triggerTime;
	}
	
	public DateTime getTriggerTime() {
		return triggerTime;
	}
}
