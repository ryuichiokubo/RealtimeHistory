package com.ryuichiokubo.android.realtimehistory.lib;

import android.support.annotation.DrawableRes;

import com.ryuichiokubo.android.realtimehistory.R;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class BackgroundManager {
	private static final Map<Integer, Integer> HOUR_BG_MAP;

	static {
		Map<Integer, Integer> map = new HashMap<>();

		map.put(0, R.drawable.edo_038);
		map.put(1, R.drawable.edo_038);
		map.put(2, R.drawable.edo_038);
		map.put(3, R.drawable.edo_038);
		map.put(4, R.drawable.edo_038);
		map.put(5, R.drawable.edo_008);
		map.put(6, R.drawable.edo_008);
		map.put(7, R.drawable.edo_008);
		map.put(8, R.drawable.edo_012);
		map.put(9, R.drawable.edo_012);
		map.put(10, R.drawable.edo_093);
		map.put(11, R.drawable.edo_093);
		map.put(12, R.drawable.edo_044);
		map.put(13, R.drawable.edo_044);
		map.put(14, R.drawable.edo_027);
		map.put(15, R.drawable.edo_027);
		map.put(16, R.drawable.edo_013);
		map.put(17, R.drawable.edo_013);
		map.put(18, R.drawable.edo_083);
		map.put(19, R.drawable.edo_083);
		map.put(20, R.drawable.edo_091);
		map.put(21, R.drawable.edo_091);
		map.put(22, R.drawable.edo_077);
		map.put(23, R.drawable.edo_077);

		HOUR_BG_MAP = Collections.unmodifiableMap(map);
	}

	@DrawableRes
	public static int getBackground() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		return HOUR_BG_MAP.get(hour);
	}
}
