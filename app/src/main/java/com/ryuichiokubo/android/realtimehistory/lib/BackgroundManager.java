package com.ryuichiokubo.android.realtimehistory.lib;

import android.support.annotation.DrawableRes;

import com.ryuichiokubo.android.realtimehistory.R;

import java.util.Calendar;

public class BackgroundManager {
	private static final int IMAGE_DAY = R.drawable.day;
	private static final int IMAGE_NIGHT = R.drawable.night;

	@DrawableRes
	public static int getBackground() {
		if (isDaytime()) {
			return IMAGE_DAY;
		} else {
			return IMAGE_NIGHT;
		}
	}

	private static boolean isDaytime() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		return 6 <= hour && hour < 18;
	}
}
