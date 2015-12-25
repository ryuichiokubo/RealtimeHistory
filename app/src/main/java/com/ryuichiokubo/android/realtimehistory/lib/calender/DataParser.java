package com.ryuichiokubo.android.realtimehistory.lib.calender;

import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.ryuichiokubo.android.realtimehistory.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.Locale;

public class DataParser {
	@SuppressWarnings("unused")
	private static final String TAG = "DataParser";

	private enum DataType {
		DATE, OLD_YEAR, OLD_MONTH, OLD_DAY, EVENT, LINK
	}

	@Nullable // if not initialised or failed to do so
	private EnumMap<DataType, String> todayData;

	private static final DataParser instance = new DataParser();

	private DataParser() {
		// Singleton
	}

	static DataParser getInstance() {
		return instance;
	}

	void parse(BufferedReader reader) throws IOException, ParseException {
		try {
			while (true) {
				String oneDayCsv = reader.readLine();
				if (oneDayCsv == null) {
					break;
				}

				EnumMap<DataType, String> oneDayData = parseOneDay(oneDayCsv);
				if (isToday(oneDayData)) {
					todayData = oneDayData;
					break;
				}
			}
		} finally {
			reader.close();
		}
	}

	public boolean isTodayEventDataSet() {
		return todayData != null
				&& !todayData.get(DataType.EVENT).isEmpty()
				&& !todayData.get(DataType.LINK).isEmpty();
	}

	private String getOldYear() {
		if (todayData == null) {
			throw new IllegalStateException();
		}

		return todayData.get(DataType.OLD_YEAR);
	}

	private String getOldMonth() {
		if (todayData == null) {
			throw new IllegalStateException();
		}

		return todayData.get(DataType.OLD_MONTH);
	}

	private String getOldDay() {
		if (todayData == null) {
			throw new IllegalStateException();
		}

		return todayData.get(DataType.OLD_DAY);
	}

	public String getDate(Resources resources) {
		return resources.getString(R.string.date, getOldYear(), getOldMonth(), getOldDay());
	}

	public String getEvent() {
		if (todayData == null) {
			throw new IllegalStateException();
		}

		return todayData.get(DataType.EVENT);
	}

	public String getLink() {
		if (todayData == null) {
			throw new IllegalStateException();
		}

		return todayData.get(DataType.LINK);
	}

	private boolean isToday(EnumMap<DataType, String> oneDayData) throws ParseException {
		Calendar dataDate = Calendar.getInstance();
		dataDate.setTime(
				new SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE)
						.parse(oneDayData.get(DataType.DATE)));

		Calendar today = Calendar.getInstance();

		return today.get(Calendar.DAY_OF_MONTH) == dataDate.get(Calendar.DAY_OF_MONTH)
				&& today.get(Calendar.MONTH) == dataDate.get(Calendar.MONTH);
	}

	private EnumMap<DataType, String> parseOneDay(String oneDayCsv) {
		String[] splits = oneDayCsv.split(",", -1);
		EnumMap<DataType, String> map = new EnumMap<>(DataType.class);

		map.put(DataType.DATE, splits[0]);
		map.put(DataType.OLD_YEAR, splits[1]);
		map.put(DataType.OLD_MONTH, splits[2]);
		map.put(DataType.OLD_DAY, splits[3]);
		map.put(DataType.EVENT, splits[4]);
		map.put(DataType.LINK, splits[5]);

		return map;
	}

}
