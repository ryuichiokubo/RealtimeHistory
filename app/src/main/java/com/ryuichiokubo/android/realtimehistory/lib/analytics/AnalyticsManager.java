package com.ryuichiokubo.android.realtimehistory.lib.analytics;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ryuichiokubo.android.realtimehistory.AnalyticsApplication;

public class AnalyticsManager {
	@SuppressWarnings("unused")
	private static final String TAG = "AnalyticsManager";

	private static Tracker tracker;
	private static final Object trackerLock = new Object();

	private static final AnalyticsManager instance = new AnalyticsManager();

	private AnalyticsManager() {
		// Singleton
	}

	public static AnalyticsManager getInstance(Activity activity) {
		synchronized (trackerLock) {
			if (tracker == null) {
				tracker = ((AnalyticsApplication) activity.getApplication()).getDefaultTracker();
			}
		}

		return instance;
	}

	public void tagScreen(Screen screen) {
		Log.i(TAG, "Analytics: Screen " + screen);

		tracker.setScreenName(screen.getTagName());
		tracker.send(new HitBuilders.ScreenViewBuilder()
				.build());
	}

	public void tagEvent(Event event, String detail) {
		Log.i(TAG, "Analytics: Event " + event + " " + detail);

		tracker.send(new HitBuilders.EventBuilder()
				.setCategory(event.getTagName())
				.setAction(detail)
				.build());
	}
}
