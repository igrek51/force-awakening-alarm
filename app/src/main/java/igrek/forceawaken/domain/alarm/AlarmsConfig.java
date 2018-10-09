package igrek.forceawaken.domain.alarm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AlarmsConfig implements Parcelable {
	
	private ArrayList<AlarmTrigger> alarmTriggers = new ArrayList<>();
	
	public AlarmsConfig() {
	}
	
	public ArrayList<AlarmTrigger> getAlarmTriggers() {
		return alarmTriggers;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(alarmTriggers);
	}
	
	private AlarmsConfig(Parcel in) {
		this.alarmTriggers = in.readArrayList(AlarmTrigger.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<AlarmsConfig> CREATOR = new Parcelable.Creator<AlarmsConfig>() {
		public AlarmsConfig createFromParcel(Parcel in) {
			return new AlarmsConfig(in);
		}
		
		public AlarmsConfig[] newArray(int size) {
			return new AlarmsConfig[size];
		}
	};
}
