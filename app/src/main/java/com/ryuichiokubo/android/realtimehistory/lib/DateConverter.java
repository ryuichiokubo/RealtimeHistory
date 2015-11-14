package com.ryuichiokubo.android.realtimehistory.lib;

import java.util.Calendar;

public class DateConverter {
	private static DateConverter instance = new DateConverter();

	public static DateConverter getInstance() {
		return instance;
	}

	private DateConverter() {
	}

	public String getNameInOldFormat() {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		// XXX tmp
		int tmpDay = 24 + (day - 14);
		return "嘉永７年９月" + tmpDay + "日";
	}
}
