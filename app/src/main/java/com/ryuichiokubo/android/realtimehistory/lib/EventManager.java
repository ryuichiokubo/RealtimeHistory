package com.ryuichiokubo.android.realtimehistory.lib;

import android.content.Context;

import com.ryuichiokubo.android.realtimehistory.R;

public class EventManager {
	public static String getEvent() {
		return EventCalender.getInstance().getEvent();
	}

	public static String getEventTitle(Context context) {
		return context.getString(R.string.news_title);
	}
}
