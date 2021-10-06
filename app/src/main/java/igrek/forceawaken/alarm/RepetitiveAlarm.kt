package igrek.forceawaken.alarm

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.DateTime
import org.joda.time.LocalTime


data class RepetitiveAlarm(
    var triggerTime: LocalTime,
    var daysOfWeek: MutableList<Int> = mutableListOf(1, 2, 3, 4, 5),
    var startFromTime: DateTime,
    var earlyMinutes: Int = 0,
    var snoozes: Int = 0,
    var snoozeInterval: Int,
) : Parcelable {

    override fun toString(): String {
        val triggerTimeStr = triggerTime.toString("HH:mm:ss")
        val startFromTimeStr = startFromTime.toString("HH:mm, yyyy-MM-dd")
        val daysOfWeekStr = daysOfWeek.joinToString(separator = ",")
        return "at $triggerTimeStr every $daysOfWeekStr, from $startFromTimeStr" +
                " (-${earlyMinutes}min, ${snoozes}x${snoozeInterval}s)"
//        startFromTime.dayOfWeek().get()
    }

    override fun equals(obj: Any?): Boolean {
        return obj is RepetitiveAlarm
                && triggerTime == obj.triggerTime
                && daysOfWeek == obj.daysOfWeek
                && earlyMinutes == obj.earlyMinutes
                && snoozes == obj.snoozes
                && snoozeInterval == obj.snoozeInterval
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<RepetitiveAlarm> {
            override fun createFromParcel(parcel: Parcel): RepetitiveAlarm {
                val triggerTime = parcel.readSerializable() as LocalTime
                val daysOfWeek = parcel.readArrayList(Int::class.java.classLoader) as List<Int>
                val startFromTime = parcel.readSerializable() as DateTime
                val earlyMarginMin = parcel.readInt()
                val snoozeRepeats = parcel.readInt()
                val snoozeRepeatsInterval = parcel.readInt()
                return RepetitiveAlarm(
                    triggerTime, daysOfWeek.toMutableList(), startFromTime,
                    earlyMarginMin, snoozeRepeats, snoozeRepeatsInterval
                )
            }

            override fun newArray(size: Int) = arrayOfNulls<RepetitiveAlarm>(size)
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(triggerTime)
        dest.writeList(daysOfWeek.toList())
        dest.writeSerializable(startFromTime)
        dest.writeInt(earlyMinutes)
        dest.writeInt(snoozes)
        dest.writeInt(snoozeInterval)
    }

    override fun describeContents() = 0
}