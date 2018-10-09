package igrek.forceawaken.domain.alarm;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

public class AlarmTrigger implements Parcelable {
	
	private DateTime triggerTime;
	private boolean active;
	private PendingIntent pendingIntent;
	
	public AlarmTrigger(DateTime triggerTime, boolean active, PendingIntent pendingIntent) {
		this.triggerTime = triggerTime;
		this.active = active;
		this.pendingIntent = pendingIntent;
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
	
	public PendingIntent getPendingIntent() {
		return pendingIntent;
	}
	
	@Override
	public String toString() {
		return triggerTime.toString("HH:mm:ss, yyyy-MM-dd");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AlarmTrigger && triggerTime.equals(((AlarmTrigger) obj).triggerTime);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(triggerTime);
		dest.writeByte((byte) (active ? 1 : 0));
		dest.writeParcelable(pendingIntent, flags);
	}
	
	private AlarmTrigger(Parcel in) {
		this.triggerTime = (DateTime) in.readSerializable();
		this.active = in.readByte() != 0;
		this.pendingIntent = in.readParcelable(PendingIntent.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<AlarmTrigger> CREATOR = new Parcelable.Creator<AlarmTrigger>() {
		public AlarmTrigger createFromParcel(Parcel in) {
			return new AlarmTrigger(in);
		}
		
		public AlarmTrigger[] newArray(int size) {
			return new AlarmTrigger[size];
		}
	};
}
