package com.ryuichiokubo.android.realtimehistory.lib;

import android.content.res.Resources;

import com.ryuichiokubo.android.realtimehistory.R;

public class DateConverter {
	@SuppressWarnings("unused")
	private static final String TAG = "DateConverter";

	private static DateConverter instance = new DateConverter();

	public static DateConverter getInstance() {
		return instance;
	}

	private DateConverter() {
	}

	public String getNameInOldFormat(Resources resources) {
		EventCalender eventCalender = EventCalender.getInstance();
		return resources.getString(R.string.date, eventCalender.getOldYear(),
				eventCalender.getOldMonth(), eventCalender.getOldDay());
	}
}
