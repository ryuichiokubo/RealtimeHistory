package com.ryuichiokubo.android.realtimehistory.lib.calender;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DataDownloader {
	@SuppressWarnings("unused")
	private static final String TAG = "DataDownloader";

	private static final String EVENT_DATA_URL = "http://bakumatsu-ryuichiokubo.rhcloud.com";
	private static final String DOWNLOADED_FILE_NAME = "events";

	private static final DataDownloader instance = new DataDownloader();

	private DataDownloader() {
		// Singleton
	}

	static DataDownloader getInstance() {
		return instance;
	}

	void download(final Context context) {
		// TODO: reload todayData and UI when download is complete
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

	private static void writeData(Context context, InputStream in) throws IOException {
		// TODO: should validate downloaded data

		FileOutputStream out = context.openFileOutput(DOWNLOADED_FILE_NAME, Context.MODE_PRIVATE);

		byte[] buffer = new byte[1024];
		int len = in.read(buffer);
		while (len != -1) {
			out.write(buffer, 0, len);
			len = in.read(buffer);
		}

		in.close();
		out.close();
	}

	@Nullable // if file not found
	public FileInputStream getDownloadedFileInputStream(Context context) {
		FileInputStream in = null;
		try {
			in = context.openFileInput(DOWNLOADED_FILE_NAME);
		} catch (FileNotFoundException e) {
			// return null
		}

		return in;
	}
}
