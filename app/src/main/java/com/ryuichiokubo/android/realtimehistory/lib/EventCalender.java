package com.ryuichiokubo.android.realtimehistory.lib;

import android.content.Context;
import android.util.Log;

import com.ryuichiokubo.android.realtimehistory.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.Locale;

public final class EventCalender {

	@SuppressWarnings("unused")
	private static final String TAG = "EventCalender";

	private static final EventCalender instance = new EventCalender();
	private EnumMap<DataType, String> todayData;

	private EventCalender() {}

	public static EventCalender getInstance() {
		return instance;
	}

	private enum DataType {
		DATE, OLD_YEAR, OLD_MONTH, OLD_DAY, EVENT, LINK
	}

	public void init(Context context) {
		try {
			readData(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeData(Context context, InputStream in) throws IOException {
		FileOutputStream out = context.openFileOutput("events", Context.MODE_PRIVATE);

		byte[] buffer = new byte[1024];
		int len = in.read(buffer);
		while (len != -1) {
			out.write(buffer, 0, len);
			len = in.read(buffer);
		}

		in.close();
		out.close();
	}

	private void readData(Context context) throws IOException, ParseException {
		// XXX (clean) check if downloaded file exists, if not use default file like below
		// XXX default data is corrupted for test
		boolean fileExists = true;
		FileInputStream in = null;
		try {
			in = context.openFileInput("events");
		} catch (FileNotFoundException e) {
			fileExists = false;
		}

		BufferedReader reader;
		Log.d(TAG, "@@@ readData fileExists=" + fileExists);
		if (fileExists) {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} else {
			reader = new BufferedReader(new InputStreamReader(
					context.getResources().openRawResource(R.raw.events), "UTF-8"));
		}

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

	private boolean isToday(EnumMap<DataType, String> oneDay) throws ParseException {
		// XXX data should hold parsed date... inner class? or it is fine because it is used only here
		String datePart = oneDay.get(DataType.DATE);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE);

		Calendar parsingDate = Calendar.getInstance();
		parsingDate.setTime(format.parse(datePart));

		Calendar today = Calendar.getInstance();
		int dayOfToday = today.get(Calendar.DAY_OF_MONTH);
		int monthOfToday = today.get(Calendar.MONTH);

		return dayOfToday == parsingDate.get(Calendar.DAY_OF_MONTH)
				&& monthOfToday == parsingDate.get(Calendar.MONTH);
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

	public String getOldYear() {
		if (todayData == null) {
			throw new IllegalStateException();
		}

		return todayData.get(DataType.OLD_YEAR);
	}

	public String getOldMonth() {
		if (todayData == null) {
			throw new IllegalStateException();
		}

		return todayData.get(DataType.OLD_MONTH);
	}

	public String getOldDay() {
		if (todayData == null) {
			throw new IllegalStateException();
		}

		return todayData.get(DataType.OLD_DAY);
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
}
