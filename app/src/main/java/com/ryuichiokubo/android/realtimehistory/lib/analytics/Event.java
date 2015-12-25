package com.ryuichiokubo.android.realtimehistory.lib.analytics;

public enum Event implements AnalyticsTag {
	CLICK("Click");

	private final String id;

	Event(String id) {
		this.id = id;
	}

	@Override
	public String getTagName() {
		return this.id;
	}
}
