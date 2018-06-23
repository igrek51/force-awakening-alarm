package igrek.forceawaken.domain.alarm;

import java.io.Serializable;
import java.util.ArrayList;

public class AlarmsConfig implements Serializable {
	
	private ArrayList<AlarmTrigger> alarmTriggers = new ArrayList<>();
	
	public AlarmsConfig() {
	}
	
	public ArrayList<AlarmTrigger> getAlarmTriggers() {
		return alarmTriggers;
	}
	
}
