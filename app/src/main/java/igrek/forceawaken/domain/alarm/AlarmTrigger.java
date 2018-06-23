package igrek.forceawaken.domain.alarm;

import org.joda.time.DateTime;

import java.io.Serializable;

public class AlarmTrigger implements Serializable {
	
	private DateTime triggerTime;
	
	public AlarmTrigger(DateTime triggerTime) {
		this.triggerTime = triggerTime;
	}
	
	public DateTime getTriggerTime() {
		return triggerTime;
	}
}
