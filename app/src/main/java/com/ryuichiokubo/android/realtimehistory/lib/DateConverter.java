package com.ryuichiokubo.android.realtimehistory.lib;

import android.content.Context;
import android.util.Log;

import com.ryuichiokubo.android.realtimehistory.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
	private static final String TAG = "DateConverter";

	private static DateConverter instance = new DateConverter();

	public static DateConverter getInstance() {
		return instance;
	}

	private DateConverter() {
	}

	public String getNameInOldFormat(Context context) {
		// XXX parse data on start in somwhere else and store in java object
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(
					new InputStreamReader(context.getResources().openRawResource(R.raw.events), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// XXX
			e.printStackTrace();
		}

		String dataOfToday = "";

		if (reader != null) {
			try {
				while (true) {
					final String readChar;
					readChar = reader.readLine();
					if (readChar == null) {
						break;
					}

					Log.d(TAG, "@@@ getNameInOldFormat readChar=" + readChar);
					if (isToday((readChar))) {
						dataOfToday = parseOneDay(readChar);
						break;
					}
				}
			} catch (IOException e) {
				// XXX
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					// XXX
					e.printStackTrace();
				}
			}
		}

		Log.d(TAG, "@@@ getNameInOldFormat dataOfToday=" + dataOfToday);
		int indexOfFirstComma = dataOfToday.indexOf(",");
		int indexOfSecondComma = dataOfToday.indexOf(",", indexOfFirstComma + 1);
		int indexOfThirdComma = dataOfToday.indexOf(",", indexOfSecondComma + 1);
		int indexOfForthComma = dataOfToday.indexOf(",", indexOfThirdComma + 1);
		Log.d(TAG, "@@@ getNameInOldFormat indexOfFirstComma=" + indexOfFirstComma);
		Log.d(TAG, "@@@ getNameInOldFormat indexOfSecondComma=" + indexOfSecondComma);
		String year = dataOfToday.substring(indexOfFirstComma + 1, indexOfSecondComma);
		String month = dataOfToday.substring(indexOfSecondComma + 1, indexOfThirdComma);
		String day = dataOfToday.substring(indexOfThirdComma + 1, indexOfForthComma);
		return year + month + "月" + day + "日";
	}

	private boolean isToday(String readChar) {
		String datePart = readChar.substring(0, readChar.indexOf(","));
		Log.d(TAG, "@@@ isToday datePart=" + datePart);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE);

		Date date = null;
		try {
			date = format.parse(datePart);
		} catch (ParseException e) {
			// XXX
			e.printStackTrace();
		}

		Calendar today = Calendar.getInstance();
		int day = today.get(Calendar.DAY_OF_MONTH);
		int month = today.get(Calendar.MONTH);

		Calendar readDay = Calendar.getInstance();
		readDay.setTime(date);

		return day == readDay.get(Calendar.DAY_OF_MONTH) && month == readDay.get(Calendar.MONTH);
	}

	// @param CSV
	private String parseOneDay(final String oneDay) {
		return oneDay; // XXX
	}
}
