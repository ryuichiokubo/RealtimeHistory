package com.ryuichiokubo.android.realtimehistory.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

	private AlertDialog dialog;
	private Snackbar statusBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		View view = findViewById(R.id.main);

		// XXX move to somewhere?
		EventCalender.getInstance().init(this);

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
