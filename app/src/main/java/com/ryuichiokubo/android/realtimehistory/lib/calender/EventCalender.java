package com.ryuichiokubo.android.realtimehistory.lib.calender;

import android.content.Context;

import com.ryuichiokubo.android.realtimehistory.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public final class EventCalender {
	@SuppressWarnings("unused")
	private static final String TAG = "EventCalender";

	private static final EventCalender instance = new EventCalender();
	private static final DataParser parser = DataParser.getInstance();

	private EventCalender() {
		// Singleton
	}

	public static EventCalender getInstance() {
		// XXX call init here?
		return instance;
	}

	// TODO: ensure this is called
	public void prepareData(Context context) throws IOException, ParseException {
		DataParser.getInstance().parse(getDataReader(context));
	}

	public void downloadData(Context context) {
		DataDownloader.getInstance().download(context);
	}

	// Access data via parser
	public DataParser getParser() {
		return parser;
	}

	// Check if downloaded file exists, if not use default file
	private BufferedReader getDataReader(Context context) throws UnsupportedEncodingException {
		FileInputStream downloadedFileInputStream
				= DataDownloader.getInstance().getDownloadedFileInputStream(context);

		return downloadedFileInputStream != null
				? new BufferedReader(new InputStreamReader(downloadedFileInputStream, "UTF-8"))
				: new BufferedReader(new InputStreamReader(
					context.getResources().openRawResource(R.raw.events), "UTF-8"));
	}
}
