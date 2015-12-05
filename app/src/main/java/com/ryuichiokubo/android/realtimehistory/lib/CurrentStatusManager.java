package com.ryuichiokubo.android.realtimehistory.lib;

import android.content.Context;

import com.ryuichiokubo.android.realtimehistory.R;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CurrentStatusManager {
	private static final Map<Integer, Integer> HOUR_STATUS_MAP;

	static {
		Map<Integer, Integer> map = new HashMap<>();

		map.put(0, R.string.sleeping);
		map.put(1, R.string.sleeping);
		map.put(2, R.string.sleeping);
		map.put(3, R.string.sleeping);
		map.put(4, R.string.sleeping);
		map.put(5, R.string.sleeping);
		map.put(6, R.string.awake);
		map.put(7, R.string.breakfast);
		map.put(8, R.string.calligraphy);
		map.put(9, R.string.math);
		map.put(10, R.string.letter);
		map.put(11, R.string.geography);
		map.put(12, R.string.lunch);
		map.put(13, R.string.philosophy);
		map.put(14, R.string.history);
		map.put(15, R.string.shopping);
		map.put(16, R.string.kabuki);
		map.put(17, R.string.housework);
		map.put(18, R.string.dinner);
		map.put(19, R.string.bath);
		map.put(20, R.string.reading);
		map.put(21, R.string.sleeping);
		map.put(22, R.string.sleeping);
		map.put(23, R.string.sleeping);

		HOUR_STATUS_MAP = Collections.unmodifiableMap(map);
	}

	public static String getStatus(Context context) {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		return context.getString(HOUR_STATUS_MAP.get(hour));
	}
}
