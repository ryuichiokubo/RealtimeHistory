package com.ryuichiokubo.android.realtimehistory.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ryuichiokubo.android.realtimehistory.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class EventCalender {

	@SuppressWarnings("unused")
	private static final String TAG = "EventCalender";

	private String EVENT_DATA_URL = "http://bakumatsu-ryuichiokubo.rhcloud.com";

	private static final EventCalender instance = new EventCalender();
	private EnumMap<DataType, String> todayData;

	private EventCalender() {}

	public static EventCalender getInstance() {
		return instance;
	}

	private enum DataType {
		DATE, OLD_YEAR, OLD_MONTH, OLD_DAY, EVENT, LINK
	}

	public void init(Context context) throws IOException, ParseException {
		setTodayData(context);
		downloadData(context);
	}

	private void downloadData(final Context context) {
		// TODO: use event driven architecture via mediator
		// to start downloading when local data read is done
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		executorService.submit(new Runnable() {
			@Override
			public void run() {
				ConnectivityManager connMgr = (ConnectivityManager)
						context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

				if (networkInfo != null && networkInfo.isConnected()) {
					URL url;
					try {
						url = new URL(EVENT_DATA_URL);
					} catch (MalformedURLException e) {
						Log.e(TAG, "URL failed with MalformedURLException. e=" + e);
						return;
					}

					HttpURLConnection conn;
					try {
						conn = (HttpURLConnection) url.openConnection();
					} catch (IOException e) {
						Log.e(TAG, "openConnection failed with IOException. e=" + e);
						return;
					}

					conn.setReadTimeout(10000 /* milliseconds */);
					conn.setConnectTimeout(15000 /* milliseconds */);

					try {
						conn.setRequestMethod("GET");
					} catch (ProtocolException e) {
						Log.e(TAG, "setRequestMethod failed with ProtocolException. e=" + e);
						return;
					}

					try {
						conn.connect();
					} catch (IOException e) {
						Log.e(TAG, "connect failed with IOException. e=" + e);
						return;
					}

					int response;
					try {
						response = conn.getResponseCode();
					} catch (IOException e) {
						Log.e(TAG, "getResponseCode failed with IOException. e=" + e);
						return;
					}

					Log.d(TAG, "The response is: " + response);
					if (response != 200) {
						return;
					}

					try {
						writeData(context, conn.getInputStream());
					} catch (IOException e) {
						Log.e(TAG, "writeData failed with IOException. e=" + e);
					}
				}
			}
		});
	}

	public static void writeData(Context context, InputStream in) throws IOException {
		// TODO: should validate downloaded data

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

	// todayData field will be null if this call failed
	private void setTodayData(Context context) throws IOException, ParseException {
		BufferedReader reader = getDataReader(context);

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

	// Check if downloaded file exists, if not use default file
	private BufferedReader getDataReader(Context context) throws UnsupportedEncodingException {
		boolean fileExists = true;
		FileInputStream in = null;
		try {
			in = context.openFileInput("events");
		} catch (FileNotFoundException e) {
			fileExists = false;
		}

		BufferedReader reader;
		if (fileExists) {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} else {
			reader = new BufferedReader(new InputStreamReader(
					context.getResources().openRawResource(R.raw.events), "UTF-8"));
		}

		return reader;
	}

	private boolean isToday(EnumMap<DataType, String> oneDay) throws ParseException {
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

	public boolean isTodayDataSet() {
		return todayData != null;
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
