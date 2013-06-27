package org.cds06.speleograph;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Range between two dates.
 * This class is a set of function to symbolize a Date Range.
 *
 * @author PhilippeGeek
 * @since 1.0
 */
public class DateRange {

    /**
     * Length of a date range
     */
    public static enum Length {
        SECOND,
        MINUTE,
        HOUR,
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    /**
     * Number of queried parts for the plots division.
     */
    private IntegerProperty numberOfParts = new SimpleIntegerProperty(10);

    /**
     * The start date from the range.
     */
    private ObjectProperty<Date> start = new SimpleObjectProperty<>();
    /**
     * The end date of this range.
     */
    private ObjectProperty<Date> end = new SimpleObjectProperty<>();

    /**
     * Plots computed for this range.
     */
    private ObjectProperty<ArrayList<Date>> plotsForTheRange = new SimpleObjectProperty<>(this, "plots");

    {
        ObjectBinding<ArrayList<Date>> objectBinding = Bindings.createObjectBinding(new Callable<ArrayList<Date>>() {
            @Override
            public ArrayList<Date> call() throws Exception {
                if (endProperty().getValue() != null && startProperty().getValue() != null)
                    return divideInParts(numberOfParts.getValue());
                return new ArrayList<>(0);
            }
        }, start, end, numberOfParts);
        plotsForTheRange.bind(objectBinding);
    }

    /**
     * Construct a new Date Range.
     *
     * @param start The start date for this range
     * @param end   The end date for this range
     */
    public DateRange(Date start, Date end) {
        startProperty().setValue(start);
        endProperty().setValue(end);
        updatePlots();
    }

    /**
     * Recompute the plots when
     */
    private void updatePlots() {
//        if(endProperty().getValue()!=null && startProperty().getValue()!=null)
//            plotsForTheRange.setValue(divideInParts(numberOfParts.getValue()));
    }

    public ObjectProperty<Date> startProperty() {
        return start;
    }

    public ObjectProperty<Date> endProperty() {
        return end;
    }

    public ObjectProperty<ArrayList<Date>> plotsProperty() {
        return plotsForTheRange;
    }

//    public IntegerProperty partsProperty(){
//        return numberOfParts;
//    }

    /**
     * The length from this Range
     *
     * @return Length in seconds
     */
    public long length() {
        return lengthBinding.get();
    }

    /**
     * The length from this Range.
     */
    public LongBinding lengthBinding = new LongBinding() {

        {
            super.bind(start, end);
        }

        @Override
        protected long computeValue() {
            if (endProperty().getValue() != null && startProperty().getValue() != null)
                return endProperty().getValue().getTime() - startProperty().getValue().getTime();
            return 0;
        }
    };

    /**
     * Describe this Range by an interval.
     *
     * @return The length who best describe the Range
     * @see DateRange.Length
     */
    public Length getRangeCategory() {
        // NOTE: Too big values are calculated and written as long
        if (length() > 31536000000L) // Length is more than a year
            return Length.YEAR;
        else if (length() > 2678400000L) // Length is more than a month
            return Length.MONTH;
        else if (length() > (1000 * 60 * 60 * 24 * 7))
            return Length.WEEK;
        else if (length() > (1000 * 60 * 60 * 24))
            return Length.DAY;
        else if (length() > (1000 * 60 * 60))
            return Length.HOUR;
        else if (length() > (1000 * 60))
            return Length.MINUTE;
        else
            return Length.SECOND;
    }

    /**
     * Give a set of Date to divide the Range in parts.
     *
     * @param parts the number of parts
     * @return The set of Date values to divide this Range
     */
    private ArrayList<Date> divideInParts(int parts) {
        ArrayList<Date> plots = new ArrayList<>(parts + 1);
        long partLength = length() / parts;
        for (int i = 0; i < parts; i++)
            plots.add(new Date(startProperty().getValue().getTime() + partLength * i));
        plots.add(end.getValue());
        return plots;
    }

}
