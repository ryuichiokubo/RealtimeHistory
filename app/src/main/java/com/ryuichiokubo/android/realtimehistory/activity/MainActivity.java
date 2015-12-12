package com.ryuichiokubo.android.realtimehistory.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ryuichiokubo.android.realtimehistory.R;
import com.ryuichiokubo.android.realtimehistory.lib.BackgroundManager;
import com.ryuichiokubo.android.realtimehistory.lib.CurrentStatusManager;
import com.ryuichiokubo.android.realtimehistory.lib.DateConverter;
import com.ryuichiokubo.android.realtimehistory.lib.EventCalender;
import com.ryuichiokubo.android.realtimehistory.lib.EventManager;
import com.ryuichiokubo.android.realtimehistory.lib.TimeConverter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";
	private AlertDialog dialog;
	private Snackbar statusBar;

	private String EVENT_DATA_URL = "http://bakumatsu-ryuichiokubo.rhcloud.com";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		View view = findViewById(R.id.main);

		// XXX move to somewhere?
		EventCalender.getInstance().init(this);

		// XXX when reading local data is done, trigger event and it will start downloading data
		// XXX event driven architecture via mediator
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				ConnectivityManager connMgr = (ConnectivityManager)
						getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					URL url;
					try {
						url = new URL(EVENT_DATA_URL);
					} catch (MalformedURLException e) {
						e.printStackTrace();
						return;
					}

					HttpURLConnection conn;
					try {
						conn = (HttpURLConnection) url.openConnection();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}

					conn.setReadTimeout(10000 /* milliseconds */);
					conn.setConnectTimeout(15000 /* milliseconds */);
					try {
						conn.setRequestMethod("GET");
					} catch (ProtocolException e) {
						e.printStackTrace();
						return;
					}

					try {
						conn.connect();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}

					int response;
					try {
						response = conn.getResponseCode();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					Log.d(TAG, "The response is: " + response);
					if (response != 200) {
						return;
					}

					try {
						EventCalender.writeData(MainActivity.this, conn.getInputStream());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		setEventDialog();

		setCurrentStatusBar(view);
		setFloatingActionButton();
	}

	private void setEventDialog() {
		DialogInterface.OnClickListener linkOpenAction = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				openWebPage(EventCalender.getInstance().getLink());
			}

			private void openWebPage(String url) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				if (intent.resolveActivity(getPackageManager()) != null) {
					startActivity(intent);
				}
			}
		};

		dialog = new AlertDialog.Builder(this)
				.setTitle(EventManager.getEventTitle(this))
				.setMessage(EventManager.getEvent())
				.setNeutralButton(R.string.more, linkOpenAction)
				.create();
	}

	private void setCurrentStatusBar(View view) {
		statusBar = Snackbar
				.make(view, CurrentStatusManager.getStatus(this), Snackbar.LENGTH_INDEFINITE);

		statusBar.show();

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (statusBar.isShown()) {
					statusBar.dismiss();
				} else {
					statusBar.show();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// FIXME date and time should be changed based on time, not activity lifecycle
		setTime();
		setDate();

		setBackground();
		statusBar.setText(CurrentStatusManager.getStatus(this));
	}

	@Override
	protected void onStop() {
		super.onStop();

		dialog.dismiss();
	}

	private void setBackground() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.main);
		layout.setBackgroundResource(BackgroundManager.getBackground());
	}

	private void setDate() {
		// XXX
		TextView date = (TextView) findViewById(R.id.date);
		date.setText(DateConverter.getInstance().getNameInOldFormat(getResources()));
	}

	private void setTime() {
		TextView time = (TextView) findViewById(R.id.time);
		time.setText(TimeConverter.getInstance().getNameInOldFormat());
	}

	private void setFloatingActionButton() {
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.show();
			}
		});
	}
}
