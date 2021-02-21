package igrek.forceawaken.alarm

import android.os.Parcel
import android.os.Parcelable


data class AlarmsConfig(
        val alarmTriggers: MutableList<AlarmTrigger> = mutableListOf(),
) : Parcelable {

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<AlarmsConfig> {
            override fun createFromParcel(parcel: Parcel): AlarmsConfig {
                val alarmTriggers = parcel.readArrayList(AlarmTrigger::class.java.classLoader) as List<AlarmTrigger>
                return AlarmsConfig(alarmTriggers.toMutableList())
            }

            override fun newArray(size: Int) = arrayOfNulls<AlarmsConfig>(size)
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(alarmTriggers.toList())
    }

    override fun describeContents() = 0
}
