package com.mattleo.finance.ui.reports.trends;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.mattleo.finance.R;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.AmountGrouper;
import com.mattleo.finance.ui.common.presenters.Presenter;
import com.mattleo.finance.utils.ThemeUtils;
import com.mattleo.finance.utils.interval.BaseInterval;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;

public abstract class TrendsChartPresenter extends Presenter {
    private final TrendsChartView trendsChartView;
    private final Formatter formatter;

    public TrendsChartPresenter(TrendsChartView trendsChartView, AmountFormatter amountFormatter) {
        this.trendsChartView = trendsChartView;
        this.formatter = new Formatter(amountFormatter);
    }

    public void setData(Cursor cursor, BaseInterval baseInterval) {
        final AmountGrouper.AmountCalculator[] amountCalculators = getTransactionValidators();
        final AmountGrouper amountGrouper = new AmountGrouper(baseInterval.getInterval(), baseInterval.getType());
        final Map<AmountGrouper.AmountCalculator, List<Long>> groups = amountGrouper.getGroups(cursor, amountCalculators);

        final List<Line> lines = new ArrayList<>();
        for (AmountGrouper.AmountCalculator amountCalculator : amountCalculators) {
            final Line line = getLine(groups.get(amountCalculator))
                    .setColor(ThemeUtils.getColor(trendsChartView.getContext(), R.attr.textColorNegative))
                    .setHasLabels(true)
                    .setHasLabelsOnlyForSelected(true);
            onLineCreated(amountCalculator, line);
            lines.add(line);
        }

        final LineChartData lineChartData = new LineChartData(lines);
        lineChartData.setAxisXBottom(getAxis(baseInterval));

        trendsChartView.setLineGraphData(lineChartData);
    }

    protected abstract AmountGrouper.AmountCalculator[] getTransactionValidators();

    protected abstract void onLineCreated(AmountGrouper.AmountCalculator amountCalculator, Line line);

    protected Context getContext() {
        return trendsChartView.getContext();
    }

    private Line getLine(List<Long> amounts) {
        final List<PointValue> points = new ArrayList<>();
        int index = 0;
        for (Long amount : amounts) {
            final PointValue value = new PointValue(index++, amount);
            points.add(value);
        }

        final int lineWidthDp = (int) (trendsChartView.getResources().getDimension(R.dimen.report_line_graph_width) / Resources.getSystem().getDisplayMetrics().density);
        return new Line(points)
                .setCubic(true)
                .setStrokeWidth(lineWidthDp)
                .setPointRadius(lineWidthDp)
                .setFormatter(formatter)
                .setHasPoints(false);
    }

    private Axis getAxis(BaseInterval baseInterval) {
        final List<AxisValue> values = new ArrayList<>();
        final Period period = BaseInterval.getSubPeriod(baseInterval.getType(), baseInterval.getLength());

        Interval interval = new Interval(baseInterval.getInterval().getStart(), period);
        int index = 0;
        while (interval.overlaps(baseInterval.getInterval())) {
            values.add(new AxisValue(index++, BaseInterval.getSubTypeShortestTitle(interval, baseInterval.getType()).toCharArray()));
            interval = new Interval(interval.getEnd(), period);
        }

        return new Axis(values).setHasLines(true).setHasSeparationLine(true);
    }

    private static class Formatter implements LineChartValueFormatter {
        private final AmountFormatter amountFormatter;

        public Formatter(AmountFormatter amountFormatter) {
            this.amountFormatter = amountFormatter;
        }

        @Override public int formatChartValue(char[] chars, PointValue pointValue) {
            final char[] fullText = amountFormatter.format((long) pointValue.getY()).toCharArray();
            final int size = Math.min(chars.length, fullText.length);
            System.arraycopy(fullText, 0, chars, chars.length - size, size);
            return size;
        }
    }
}
