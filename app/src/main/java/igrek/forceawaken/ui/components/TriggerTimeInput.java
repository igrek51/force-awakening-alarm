package igrek.forceawaken.ui.components;

import android.content.Context;
import android.util.AttributeSet;

import org.joda.time.DateTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import igrek.forceawaken.ui.input.TextAddedListener;

public class TriggerTimeInput extends android.support.v7.widget.AppCompatEditText {
	
	public TriggerTimeInput(Context context) {
		super(context);
	}
	
	public TriggerTimeInput(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public TriggerTimeInput(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	{
		initialize();
	}
	
	private void initialize() {
		addTextChangedListener(new TextAddedListener() {
			@Override
			protected void onTextAdded(String newValue) {
				validateAlarmTime();
			}
		});
	}
	
	private void validateAlarmTime() {
		String text = getText().toString();
		if (!text.contains(":")) {
			if (text.length() == 4) {
				text = text.substring(0, 2) + ":" + text.substring(2);
				setAlarmTimeInput(text);
			} else if (text.length() == 3) {
				if (!(text.startsWith("0") || text.startsWith("1") || text.startsWith("2"))) {
					text = text.substring(0, 1) + ":" + text.substring(1);
					setAlarmTimeInput(text);
				}
			}
		}
	}
	
	private void setAlarmTimeInput(String text) {
		setText(text);
		setSelection(text.length(), text.length());
	}
	
	public DateTime getTriggerTime() {
		String alarmTime = getText().toString();
		
		if (alarmTime.isEmpty())
			throw new IllegalArgumentException("no trigger time set");
		
		if (!alarmTime.contains(":"))
			alarmTime = alarmTime.substring(0, alarmTime.length() - 2) + ":" + alarmTime.substring(alarmTime
					.length() - 2);
		
		String timeRegex = "([01]?[0-9]|2[0-3]):([0-5][0-9])";
		Pattern pattern = Pattern.compile(timeRegex);
		Matcher matcher = pattern.matcher(alarmTime);
		if (!matcher.matches()) {
			throw new NumberFormatException("Invalid time: " + alarmTime);
		}
		int hours = Integer.parseInt(matcher.group(1));
		int mins = Integer.parseInt(matcher.group(2));
		// todays time or tomorrow
		DateTime now = DateTime.now();
		DateTime todayTriggerTime = now.withHourOfDay(hours)
				.withMinuteOfHour(mins)
				.withSecondOfMinute(0);
		DateTime tomorrowTriggerTime = todayTriggerTime.plusDays(1);
		return now.isBefore(todayTriggerTime) ? todayTriggerTime : tomorrowTriggerTime;
	}
	
}
