package com.mattleo.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.data.db.Column;
import com.mattleo.finance.data.db.Tables;

public class ExchangeRate extends BaseModel {
    public static final Parcelable.Creator<ExchangeRate> CREATOR = new Parcelable.Creator<ExchangeRate>() {
        public ExchangeRate createFromParcel(Parcel in) {
            return new ExchangeRate(in);
        }

        public ExchangeRate[] newArray(int size) {
            return new ExchangeRate[size];
        }
    };

    private String fromCode;
    private String toCode;
    private double rate;

    public ExchangeRate() {
        super();
    }

    private ExchangeRate(Parcel parcel) {
        super(parcel);
        setFromCode(parcel.readString());
        setToCode(parcel.readString());
        setRate(parcel.readDouble());
    }

    public static ExchangeRate from(Cursor cursor) {
        final ExchangeRate exchangeRate = new ExchangeRate();
        if (cursor.getCount() > 0) {
            exchangeRate.updateFromCursor(cursor, null);
        }
        return exchangeRate;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(fromCode);
        dest.writeString(toCode);
        dest.writeDouble(rate);
    }

    @Override public ContentValues asContentValues() {
        final ContentValues values = super.asContentValues();
        values.put(Tables.ExchangeRates.CURRENCY_CODE_FROM.getName(), fromCode);
        values.put(Tables.ExchangeRates.CURRENCY_CODE_TO.getName(), toCode);
        values.put(Tables.ExchangeRates.RATE.getName(), rate);
        return values;
    }

    @Override public void prepareForContentValues() {
        if (Double.compare(rate, 0) <= 0) {
            setRate(1);
        }
    }

    @Override public void validateForContentValues() {
        Preconditions.lengthEquals(fromCode, 3, "Currency from code length must be 3.");
        Preconditions.lengthEquals(toCode, 3, "Currency to code length must be 3.");
        Preconditions.more(rate, 0, "Rate must be > 0.");
    }

    @Override public void updateFromCursor(Cursor cursor, String columnPrefixTable) {
        super.updateFromCursor(cursor, columnPrefixTable);
        int index;

        // Currency from
        index = cursor.getColumnIndex(Tables.ExchangeRates.CURRENCY_CODE_FROM.getName(columnPrefixTable));
        if (index >= 0) {
            setFromCode(cursor.getString(index));
        }

        // Currency to
        index = cursor.getColumnIndex(Tables.ExchangeRates.CURRENCY_CODE_TO.getName(columnPrefixTable));
        if (index >= 0) {
            setToCode(cursor.getString(index));
        }

        // Rate
        index = cursor.getColumnIndex(Tables.ExchangeRates.RATE.getName(columnPrefixTable));
        if (index >= 0) {
            setRate(cursor.getDouble(index));
        }
    }

    @Override protected Column getLocalIdColumn() {
        return Tables.ExchangeRates.LOCAL_ID;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRate)) return false;

        final ExchangeRate that = (ExchangeRate) o;

        if (fromCode.equals(that.fromCode) || fromCode.equals(that.toCode)) return true;
        //noinspection RedundantIfStatement
        if (toCode.equals(that.fromCode) || toCode.equals(that.toCode)) return true;

        return false;
    }

    @Override public int hashCode() {
        int result = fromCode.hashCode();
        result = 31 * result + toCode.hashCode();
        return result;
    }

    @Override public String toString() {
        return fromCode + ":" + toCode + "=" + rate;
    }

    public double getRate(String toCode) {
        if (toCode.equals(this.toCode)) {
            return rate;
        }

        if (toCode.equals(fromCode)) {
            return 1 / rate;
        }

        throw new IllegalArgumentException("ExchangeRate " + toString() + " cannot give rate to " + toCode);
    }

    public String getFromCode() {
        return fromCode;
    }

    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    public String getToCode() {
        return toCode;
    }

    public void setToCode(String toCode) {
        this.toCode = toCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
