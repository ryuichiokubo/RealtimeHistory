package com.ryuichiokubo.android.realtimehistory.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ryuichiokubo.android.realtimehistory.R;
import com.ryuichiokubo.android.realtimehistory.lib.BackgroundManager;
import com.ryuichiokubo.android.realtimehistory.lib.CurrentStatusManager;
import com.ryuichiokubo.android.realtimehistory.lib.DateConverter;
import com.ryuichiokubo.android.realtimehistory.lib.TimeConverter;

public class MainActivity extends AppCompatActivity {

	private View wholeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		wholeView = findViewById(R.id.main);
		setCurrentStatusBar();

		// XXX
		setFloatingActionButton();
	}

	private void setCurrentStatusBar() {
		// TODO set action to see more detail
		final Snackbar statusBar = Snackbar
				.make(wholeView, CurrentStatusManager.getStatus(this), Snackbar.LENGTH_INDEFINITE);
		statusBar.show();

		wholeView.setOnClickListener(new View.OnClickListener() {
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

		// XXX date and time should be changed based on time, not activity lifecycle
		setTime();
		setDate();
		setBackground();
	}

	private void setBackground() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.main);
		layout.setBackgroundResource(BackgroundManager.getBackground());
	}

	private void setDate() {
		// XXX
		TextView date = (TextView) findViewById(R.id.date);
		date.setText(DateConverter.getInstance().getNameInOldFormat());
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
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
	}
}
