package com.mattleo.finance.ui.transactions.edit.presenters;

import android.view.View;
import android.widget.Button;

import com.mattleo.finance.R;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.Presenter;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

public class DateTimePresenter extends Presenter {
    private final Button dateButton;
    private final Button timeButton;

    public DateTimePresenter(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        dateButton = findView(activity, R.id.dateButton);
        timeButton = findView(activity, R.id.timeButton);

        dateButton.setOnClickListener(clickListener);
        dateButton.setOnLongClickListener(longClickListener);
        timeButton.setOnClickListener(clickListener);
        timeButton.setOnLongClickListener(longClickListener);
    }

    public void setDateTime(long date) {
        final DateTime dateTime = new DateTime(date);
        dateButton.setText(DateUtils.formatDateTime(dateButton.getContext(), dateTime, DateUtils.FORMAT_SHOW_DATE));
        timeButton.setText(DateUtils.formatDateTime(timeButton.getContext(), dateTime, DateUtils.FORMAT_SHOW_TIME));
    }

    public void isSetByUser(boolean isSetByUser) {
//        dateTimeImageView.setImageAlpha(isSetByUser ? 255 : 64);
    }
}
