package com.ryuichiokubo.android.realtimehistory.lib.analytics;

public enum Screen implements AnalyticsTag {
	MAIN("Main");

	private final String id;

	Screen(String id) {
		this.id = id;
	}

	@Override
	public String getTagName() {
		return this.id;
	}
}
