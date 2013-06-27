package org.cds06.speleograph;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.Axis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DateAxis for JavaFX Charts.
 * <p>This class was written to create an axis of date, with a minimal and maximal date shown.</p>
 *
 * @author Philippe VIENNE
 * @see DateRange
 * @since 1.0
 */
public class DateAxis extends Axis<Date> {

    //
    // Minimal and Maximal Date properties for this axis
    //

    /**
     * Minimal date for this axis.
     * Default value: {@code new Date(System.currentTimeMillis())}
     */
    private final ObjectProperty<Date> minDateProperty = new SimpleObjectProperty<>(this, "max date", new Date(System.currentTimeMillis()));

    /**
     * Property accessor for minimal date displayed on this axis.
     *
     * @return the property
     * @see #minDateProperty
     */
    public ObjectProperty<Date> minDateProperty() {
        return minDateProperty;
    }

    /**
     * Get the minimal date displayed on the axis.
     *
     * @return the date stored
     * @see #minDateProperty()
     */
    public Date getMinDate() {
        return minDateProperty().getValue();
    }

    /**
     * Set the minimal date to display on this axis.
     *
     * @param minDate The new date to set
     * @see #minDateProperty()
     */
    public void setMinDate(Date minDate) {
        minDateProperty().setValue(minDate);
    }

    /**
     * Maximal date for this axis.
     * Default value: {@code new Date(System.currentTimeMillis())}
     */
    private final ObjectProperty<Date> maxDateProperty = new SimpleObjectProperty<>(this, "max date", new Date(System.currentTimeMillis() + 604800000L));

    /**
     * Property accessor for maximal date displayed on this axis.
     *
     * @return the property
     * @see #maxDateProperty
     */
    public ObjectProperty<Date> maxDateProperty() {
        return maxDateProperty;
    }

    /**
     * Get the maximal date displayed on the axis.
     *
     * @return the date stored
     * @see #maxDateProperty()
     */
    public Date getMaxDate() {
        return maxDateProperty().getValue();
    }

    /**
     * Set the maximal date to display on this axis.
     *
     * @param maxDate The new date to set
     * @see #maxDateProperty()
     */
    public void setMaxDate(Date maxDate) {
        maxDateProperty().setValue(maxDate);
    }

    /**
     * Actual length of this axis in pixels.
     * <i>No scale on the date axis</i>
     */
    private DoubleProperty length = new SimpleDoubleProperty(this, "Axis length", 100);

    /**
     * Actual range shown.
     * This auto-refresh on dateMin or dateMax change.
     *
     * @see #minDateProperty()
     * @see #maxDateProperty()
     */
    private ObjectProperty<DateRange> rangeProperty = new SimpleObjectProperty<>(this, "range", new DateRange(getMinDate(), getMaxDate()));

    /**
     * Property for the actual range shown.
     * This auto-refresh on dateMin or dateMax change.
     *
     * @see #minDateProperty()
     * @see #maxDateProperty()
     */
    public ObjectProperty<DateRange> rangeProperty() {
        return rangeProperty;
    }

    {   // Bind min and max dates with DateRange
        minDateProperty().bindBidirectional(rangeProperty().getValue().startProperty());
        maxDateProperty().bindBidirectional(rangeProperty().getValue().endProperty());
        rangeProperty.addListener(new ChangeListener<DateRange>() {
            @Override
            public void changed(ObservableValue<? extends DateRange> observableValue, DateRange oldDateRange, DateRange newDateRange) {
                minDateProperty().unbindBidirectional(oldDateRange.startProperty());
                maxDateProperty().unbindBidirectional(oldDateRange.endProperty());
                minDateProperty().bindBidirectional(newDateRange.startProperty());
                maxDateProperty().bindBidirectional(newDateRange.endProperty());
            }
        });
    }

    /**
     * Current ticked dates.
     * This is binned from {@link org.cds06.speleograph.DateRange#plotsProperty()}
     */
    private ObjectProperty<ArrayList<Date>> tickedDates = new SimpleObjectProperty<>(this, "ticked dates list");

    {   // Setup binding for data
        tickedDates.bindBidirectional(rangeProperty().getValue().plotsProperty());
        rangeProperty.addListener(new ChangeListener<DateRange>() {
            @Override
            public void changed(ObservableValue<? extends DateRange> observableValue, DateRange oldDateRange, DateRange newDateRange) {
                tickedDates.unbindBidirectional(oldDateRange.plotsProperty());
                tickedDates.bindBidirectional(newDateRange.plotsProperty());
            }
        });
    }

    /**
     * AutoRange for this axis.
     * <p>This function only return a value</p>
     *
     * @param length (unused but we have to implement it)
     * @return {@link #rangeProperty()}
     */
    @Override
    protected Object autoRange(double length) {
        return rangeProperty().getValue();
    }

    /**
     * Set the range for this axis
     *
     * @param range the nex range
     * @param b     an unknown and not used param
     * @deprecated Use {@link #setMinDate(java.util.Date)} et {@link #setMaxDate(java.util.Date)}
     */
    @Override
    @Deprecated
    protected void setRange(Object range, boolean b) {
        if (range instanceof DateRange)
            rangeProperty.setValue((DateRange) range);
        else
            throw new IllegalArgumentException("It's not a DateRange");
    }

    /**
     * @see #autoRange(double)
     * @see #rangeProperty
     */
    @Override
    protected Object getRange() {
        return rangeProperty.getValue();
    }

    /**
     * A date axis contains none 0 value.
     *
     * @return Not a Number value (NaN)
     */
    @Override
    public double getZeroPosition() {
        return Double.NaN;
    }

    /**
     * Compute the length of a millisecond on the axis.
     */
    private DoubleBinding millisecondLength = new DoubleBinding() {

        {
            super.bind(length, rangeProperty, maxDateProperty, minDateProperty);
        }

        @Override
        protected double computeValue() {
            return (length.getValue() / rangeProperty.getValue().length());
        }
    };

    /**
     * @see #toNumericValue(java.util.Date)
     */
    @Override
    public double getDisplayPosition(Date date) {
        return toNumericValue(date);
    }

    /**
     * @see #toRealValue(double)
     */
    @Override
    public Date getValueForDisplay(double v) {
        return toRealValue(v);
    }

    /**
     * Determine if a date is on the axis.
     *
     * @param date The date to test
     * @return true, if the date is in Axis Range
     */
    @Override
    public boolean isValueOnAxis(Date date) {
        return (date.after(getMinDate())) && (date.before(getMaxDate()));
    }

    /**
     * Turn a date into a numerical value on the axis.
     *
     * @param date the date to convert
     * @return An double which is the number of pixel from the start of axis
     */
    @Override
    public double toNumericValue(Date date) {
        return (date.getTime() - getMinDate().getTime()) * (millisecondLength.get());
    }

    /**
     * Convert an axis' position to a Date
     *
     * @param v the position on the axis
     * @return The date which correspond to the position
     */
    @Override
    public Date toRealValue(double v) {
        return new Date(getMinDate().getTime() + Math.round(v * millisecondLength.get()));
    }

    /**
     * Give the ticked date on this axis
     *
     * @param length the length of the axis on the screen
     * @param range  a range (here, this argument is ignored)
     * @return The list of date that you must tick
     * @see #tickedDates
     */
    @Override
    protected List<Date> calculateTickValues(double length, Object range) {
        this.length.setValue(length);
        return tickedDates.get();
    }

    /**
     * Determine the best label for a date.
     *
     * @param date The date.
     * @return The date in a string
     */
    @Override
    protected String getTickMarkLabel(Date date) {
        DateFormat dateFormat;
        switch (rangeProperty().get().getRangeCategory()) {
            case SECOND:
            case MINUTE:
                dateFormat = new SimpleDateFormat("mm:ss");
                break;
            case HOUR:
                dateFormat = new SimpleDateFormat("hh:mm");
                break;
            case DAY:
            case WEEK:
            case MONTH:
            case YEAR:
            default:
                dateFormat = new SimpleDateFormat("dd/MM/yy");
        }
        return dateFormat.format(date);
    }


}