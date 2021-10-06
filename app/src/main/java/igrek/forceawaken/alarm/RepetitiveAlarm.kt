package igrek.forceawaken.alarm

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.DateTime
import org.joda.time.LocalTime


data class RepetitiveAlarm(
    var triggerTime: LocalTime,
    var daysOfWeek: MutableList<Int> = mutableListOf(1, 2, 3, 4, 5),
    var startFromTime: DateTime,
    var earlyMarginMin: Int = 0,
    var snoozeRepeats: Int = 0,
    var snoozeRepeatsInterval: Int,
) : Parcelable {

    override fun toString(): String {
        val triggerTimeStr = triggerTime.toString("HH:mm:ss")
        val startFromTimeStr = triggerTime.toString("HH:mm:ss, yyyy-MM-dd")
        val daysOfWeekStr = daysOfWeek.joinToString(separator = ",")
        return "at $triggerTimeStr every $daysOfWeekStr, from $startFromTimeStr" +
                " (-${earlyMarginMin}min, ${snoozeRepeats}x${snoozeRepeatsInterval}s)"
//        startFromTime.dayOfWeek().get()
    }

    override fun equals(obj: Any?): Boolean {
        return obj is RepetitiveAlarm
                && triggerTime == obj.triggerTime
                && daysOfWeek == obj.daysOfWeek
                && earlyMarginMin == obj.earlyMarginMin
                && snoozeRepeats == obj.snoozeRepeats
                && snoozeRepeatsInterval == obj.snoozeRepeatsInterval
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
        dest.writeInt(earlyMarginMin)
        dest.writeInt(snoozeRepeats)
        dest.writeInt(snoozeRepeatsInterval)
    }

    override fun describeContents() = 0
}