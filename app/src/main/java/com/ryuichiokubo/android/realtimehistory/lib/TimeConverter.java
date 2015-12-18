package com.ryuichiokubo.android.realtimehistory.lib;

import java.util.Calendar;

public final class TimeConverter {

	private enum OldFormat {
		// Start with rat at 23:00 and change in 2 hours

		RAT("子", 23, 0), OX("丑", 1, 2), TIGER("寅", 3, 4), RABBIT("卯", 5, 6),
		DRAGON("辰", 7, 8), SNAKE("巳", 9, 10), HORSE("午", 11, 12), GOAT("未", 13, 14),
		MONKEY("申", 15, 16), ROOSTER("酉", 17, 18), DOG("戌", 19, 20), PIG("亥", 21, 22);

		private final String name;
		private final String nameSuffix = "の刻";
		private final int hour1;
		private final int hour2;

		OldFormat(String name, int hour1, int hour2) {
			this.name = name + nameSuffix;
			this.hour1 = hour1;
			this.hour2 = hour2;
		}
	}

	private TimeConverter() {
	}

	public static String getNameInOldFormat() {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);

		for (OldFormat oldFormat: OldFormat.values()) {
			if (hour == oldFormat.hour1 || hour == oldFormat.hour2) {
				return oldFormat.name;
			}
		}

		return "";
	}
}
