package igrek.forceawaken.alarm

import android.os.Parcel
import android.os.Parcelable


data class AlarmsConfig(
    val alarmTriggers: MutableList<AlarmTrigger> = mutableListOf(),
    val repetitiveAlarms: MutableList<RepetitiveAlarm> = mutableListOf(),
) : Parcelable {

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<AlarmsConfig> {
            override fun createFromParcel(parcel: Parcel): AlarmsConfig {
                val alarmTriggers =
                    parcel.readArrayList(AlarmTrigger::class.java.classLoader) as List<AlarmTrigger>
                val repetitiveAlarms =
                    parcel.readArrayList(RepetitiveAlarm::class.java.classLoader) as List<RepetitiveAlarm>
                return AlarmsConfig(alarmTriggers.toMutableList(), repetitiveAlarms.toMutableList())
            }

            override fun newArray(size: Int) = arrayOfNulls<AlarmsConfig>(size)
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(alarmTriggers.toList())
        dest.writeList(repetitiveAlarms.toList())
    }

    override fun describeContents() = 0
}
