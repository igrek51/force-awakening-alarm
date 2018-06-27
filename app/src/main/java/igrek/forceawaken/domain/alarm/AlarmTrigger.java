package igrek.forceawaken.domain.alarm;

import org.joda.time.DateTime;

import java.io.Serializable;

public class AlarmTrigger implements Serializable {
	
	static final long serialVersionUID = 2;
	
	private DateTime triggerTime;
	
	public AlarmTrigger(DateTime triggerTime) {
		this.triggerTime = triggerTime;
	}
	
	public DateTime getTriggerTime() {
		return triggerTime;
	}
	
	@Override
	public String toString() {
		return triggerTime.toString("HH:mm:ss, yyyy-MM-dd");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AlarmTrigger && triggerTime.equals(((AlarmTrigger) obj).triggerTime);
	}
}
