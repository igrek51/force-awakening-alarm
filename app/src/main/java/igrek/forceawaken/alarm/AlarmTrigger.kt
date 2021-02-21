package igrek.forceawaken.alarm

import android.app.PendingIntent
import android.os.Parcel
import android.os.Parcelable
import org.joda.time.DateTime


data class AlarmTrigger(
        var triggerTime: DateTime,
        var isActive: Boolean,
        var pendingIntent: PendingIntent?,
) : Parcelable {

    override fun toString(): String {
        return triggerTime.toString("HH:mm:ss, yyyy-MM-dd")
    }

    override fun equals(obj: Any?): Boolean {
        return obj is AlarmTrigger && triggerTime == obj.triggerTime
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<AlarmTrigger> {
            override fun createFromParcel(parcel: Parcel): AlarmTrigger {
                val triggerTime = parcel.readSerializable() as DateTime
                val isActive = parcel.readByte().toInt() != 0
                val pendingIntent = parcel.readParcelable(PendingIntent::class.java.classLoader) as PendingIntent?
                return AlarmTrigger(triggerTime, isActive, pendingIntent)
            }

            override fun newArray(size: Int) = arrayOfNulls<AlarmTrigger>(size)
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(triggerTime)
        dest.writeByte((if (isActive) 1 else 0).toByte())
        dest.writeParcelable(pendingIntent, flags)
    }

    override fun describeContents() = 0
}