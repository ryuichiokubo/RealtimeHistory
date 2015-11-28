package com.ryuichiokubo.android.realtimehistory.lib;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.ryuichiokubo.android.realtimehistory.R;

public class EventManager {
	public static String getEvent() {
		return EventCalender.getInstance().getEvent();
	}

	public static TextView getEventTitle(Context context) {
		// XXX where to put link?
		TextView textView = new TextView(context);
		textView.setText(Html.fromHtml("<a href=" + EventCalender.getInstance().getLink() + ">" + context.getString(R.string.news_title)+ "</a>"));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		return textView;
	}
}
