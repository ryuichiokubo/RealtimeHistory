package com.ryuichiokubo.android.realtimehistory.lib.calender;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class DataDownloader {
	@SuppressWarnings("unused")
	private static final String TAG = "DataDownloader";

	private static final String EVENT_DATA_URL = "http://bakumatsu-ryuichiokubo.rhcloud.com";
	private static final String DOWNLOADED_FILE_NAME = "events";

	private final OkHttpClient httpClient = new OkHttpClient();

	private static final DataDownloader instance = new DataDownloader();

	private DataDownloader() {
		// Singleton
	}

	static DataDownloader getInstance() {
		return instance;
	}

	void download(final Context context) {
		if (!isConnected(context)) {
			return;
		}

		Request request = new Request.Builder().url(EVENT_DATA_URL).build();
		httpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Log.e(TAG, "HTTP request failed. e=" + e);
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (!response.isSuccessful()) {
					throw new IOException("Unexpected code " + response);
				}

				Log.i(TAG, "HTTP request success");

				saveDataInFile(context, response.body().byteStream());
			}
		});
	}

	private boolean isConnected(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnected();
	}

	private static void saveDataInFile(Context context, InputStream in) throws IOException {
		// TODO: should validate downloaded data (handle response as string)

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
