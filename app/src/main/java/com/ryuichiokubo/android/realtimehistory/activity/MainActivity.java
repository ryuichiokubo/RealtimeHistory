package com.ryuichiokubo.android.realtimehistory.activity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.ryuichiokubo.android.realtimehistory.lib.TimeConverter;
import com.ryuichiokubo.android.realtimehistory.lib.analytics.AnalyticsManager;
import com.ryuichiokubo.android.realtimehistory.lib.analytics.Event;
import com.ryuichiokubo.android.realtimehistory.lib.analytics.Screen;
import com.ryuichiokubo.android.realtimehistory.lib.calender.EventCalender;

import java.io.IOException;
import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static butterknife.ButterKnife.findById;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";

	@Bind(R.id.main) View mainView;

	private AlertDialog eventDialog;
	private Snackbar statusBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		try {
			EventCalender.getInstance().init(this);
		} catch (IOException e) {
			Log.e(TAG, "init failed with IOException. e=" + e);
		} catch (ParseException e) {
			Log.e(TAG, "init failed with ParseException. e=" + e);
		}

		eventDialog = setupEventDialog();
		statusBar = setupCurrentStatusBar();

		setFloatingActionButton();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// FIXME date and time should be changed based on time, not activity lifecycle
		setTime();
		setDate();

		setBackground();
		statusBar.setText(CurrentStatusManager.getStatus(this));

		AnalyticsManager.getInstance(this).tagScreen(Screen.MAIN);
	}

	@Override
	protected void onStop() {
		super.onStop();

		eventDialog.dismiss();
	}

	private AlertDialog setupEventDialog() {
		DialogInterface.OnClickListener linkOpenAction = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				openWebPage(EventCalender.getInstance().getParser().getLink());

				AnalyticsManager.getInstance(MainActivity.this).tagEvent(Event.CLICK, "EventLink");
			}

			private void openWebPage(String url) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				if (intent.resolveActivity(getPackageManager()) != null) {
					startActivity(intent);
				}
			}
		};

		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.news_title))
				.setMessage(EventCalender.getInstance().getParser().getEvent())
				.setNeutralButton(R.string.more, linkOpenAction)
				.create();

		return dialog;
	}

	private Snackbar setupCurrentStatusBar() {
		Snackbar snackbar = Snackbar
				.make(mainView, CurrentStatusManager.getStatus(this), Snackbar.LENGTH_INDEFINITE);

		snackbar.show();

		return snackbar;
	}

	@OnClick(R.id.main)
	void onBackgroundClick() {
		if (statusBar.isShown()) {
			statusBar.dismiss();
		} else {
			statusBar.show();
		}

		AnalyticsManager.getInstance(MainActivity.this).tagEvent(Event.CLICK, "Background");
	}

	private void setBackground() {
		RelativeLayout layout = findById(this, R.id.main);
		layout.setBackgroundResource(BackgroundManager.getBackground());
	}

	private void setDate() {
		TextView date = findById(this, R.id.date);
		date.setText(EventCalender.getInstance().getParser().getDate(getResources()));
	}

	private void setTime() {
		TextView time = findById(this, R.id.time);
		time.setText(TimeConverter.getNameInOldFormat());
	}

	private void setFloatingActionButton() {
		FloatingActionButton fab = findById(this, R.id.fab);

		// FIXME: this should happen (also) when reading data is done
		if (EventCalender.getInstance().getParser().isTodayEventDataSet()) {
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					eventDialog.show();

					AnalyticsManager.getInstance(MainActivity.this).tagEvent(Event.CLICK, "EventView");
				}
			});
		} else {
			fab.setVisibility(View.GONE);
		}
	}
}
