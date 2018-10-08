package igrek.forceawaken.domain.alarm;

import org.joda.time.DateTime;

import java.io.Serializable;

public class AlarmTrigger implements Serializable {
	
	static final long serialVersionUID = 2;
	
	private DateTime triggerTime;
	private boolean active;
	
	public AlarmTrigger(DateTime triggerTime, boolean active) {
		this.triggerTime = triggerTime;
		this.active = active;
	}
	
	public DateTime getTriggerTime() {
		return triggerTime;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
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
