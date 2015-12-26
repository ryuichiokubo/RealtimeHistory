package com.ryuichiokubo.android.realtimehistory.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static butterknife.ButterKnife.findById;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";

	@Bind(R.id.main) View mainView;
	@Bind(R.id.chat_bubble) TextView chatBubble;

	private AlertDialog eventDialog;
	private Snackbar statusBar;

	private Observable<Boolean> tickerObservable;
	private Subscription tickerSubscription;

	private boolean isFullyVisible = false;

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

		tickerObservable = setupTickerObservable();
	}

	private Observable<Boolean> setupTickerObservable() {
		Observable<Boolean> intervalTicker = Observable.interval(10, TimeUnit.SECONDS).map(new Func1<Long, Boolean>() {
			@Override
			public Boolean call(Long aLong) {
				return true;
			}
		});

		Observable<Boolean> backgroundClickObservable = Observable.create(new Observable.OnSubscribe<Boolean>() {
			@Override
			public void call(final Subscriber<? super Boolean> subscriber) {
				findById(MainActivity.this, R.id.main).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						subscriber.onNext(true);
					}
				});

			}
		});

		return Observable.merge(intervalTicker, backgroundClickObservable);
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "@@@ onResume");
		super.onResume();

		isFullyVisible = true;

		// FIXME date and time should be changed based on time, not activity lifecycle
		setTime();
		setDate();

		setBackground();
		statusBar.setText(CurrentStatusManager.getStatus(this));

		subscribeToTicker();

		AnalyticsManager.getInstance(this).tagScreen(Screen.MAIN);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "@@@ onPause");
		super.onPause();

		isFullyVisible = false;

		unsubscribeFromTicker();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "@@@ onStop");
		super.onStop();

		eventDialog.dismiss();
	}

	private void subscribeToTicker() {
		if (tickerSubscription == null || tickerSubscription.isUnsubscribed()) {
			tickerSubscription = tickerObservable
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<Boolean>() {
						@Override
						public void call(Boolean bool) {
							if (chatBubble.getVisibility() != View.VISIBLE) {
								chatBubble.setVisibility(View.VISIBLE);

								new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
									@Override
									public void run() {
										chatBubble.setVisibility(View.INVISIBLE);
									}
								}, 5000); // XXX const (also other rx related timer)
							}
						}
					});
		}
	}

	private void unsubscribeFromTicker() {
		if (tickerSubscription != null) {
			tickerSubscription.unsubscribe();
		}
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

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				unsubscribeFromTicker();
			}
		});

		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (isFullyVisible) {
					subscribeToTicker();
				}
			}
		});

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
