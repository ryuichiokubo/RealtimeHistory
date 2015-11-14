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
import com.ryuichiokubo.android.realtimehistory.lib.DateConverter;
import com.ryuichiokubo.android.realtimehistory.lib.TimeConverter;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setFloatingActionButton();
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
