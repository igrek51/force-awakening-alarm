package igrek.forceawaken.layout.input

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import org.joda.time.DateTime
import java.util.regex.Pattern

class TriggerTimeInput(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) : AppCompatEditText(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private fun initialize() {
        addTextChangedListener(object : TextAddedListener() {
            override fun onTextAdded(newValue: String) {
                validateAlarmTime()
            }
        })
    }

    private fun validateAlarmTime() {
        var text: String = text.toString()
        if (!text.contains(":")) {
            if (text.length == 4) {
                text = text.substring(0, 2) + ":" + text.substring(2)
                setAlarmTimeInput(text)
            } else if (text.length == 3) {
                if (!(text.startsWith("0") || text.startsWith("1") || text.startsWith("2"))) {
                    text = text.substring(0, 1) + ":" + text.substring(1)
                    setAlarmTimeInput(text)
                }
            }
        }
    }

    private fun setAlarmTimeInput(text: String) {
        setText(text)
        setSelection(text.length, text.length)
    }

    // todays time or tomorrow
    val triggerTime: DateTime
        get() {
            var alarmTime: String = text.toString()
            require(alarmTime.isNotEmpty()) { "no trigger time set" }
            if (!alarmTime.contains(":")) alarmTime = alarmTime.substring(0, alarmTime.length - 2) + ":" + alarmTime.substring(alarmTime
                    .length - 2)
            val timeRegex = "([01]?[0-9]|2[0-3]):([0-5][0-9])"
            val pattern = Pattern.compile(timeRegex)
            val matcher = pattern.matcher(alarmTime)
            if (!matcher.matches()) {
                throw NumberFormatException("Invalid time: $alarmTime")
            }
            val hours = matcher.group(1).toInt()
            val mins = matcher.group(2).toInt()
            // todays time or tomorrow
            val now: DateTime = DateTime.now()
            val todayTriggerTime: DateTime = now.withHourOfDay(hours)
                .withMinuteOfHour(mins)
                .withSecondOfMinute(0)
            val tomorrowTriggerTime: DateTime = todayTriggerTime.plusDays(1)
            return if (now.isBefore(todayTriggerTime)) todayTriggerTime else tomorrowTriggerTime
        }

    fun isNotEmpty(): Boolean = !text.isNullOrBlank()

    init {
        initialize()
    }
}