package com.ryuichiokubo.android.realtimehistory.lib;

public class DateConverter {
	private static final String TAG = "DateConverter";

	private static DateConverter instance = new DateConverter();

	public static DateConverter getInstance() {
		return instance;
	}

	private DateConverter() {
	}

	public String getNameInOldFormat() {
		EventCalender eventCalender = EventCalender.getInstance();
		return eventCalender.getOldYear() + "年"
				+ eventCalender.getOldMonth() + "月"
				+ eventCalender.getOldDay() + "日";
	}
}
