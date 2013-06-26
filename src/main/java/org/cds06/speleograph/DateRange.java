package org.cds06.speleograph;

import javafx.beans.binding.Binding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.Date;

/**
* This file is created by PhilippeGeek.
* Distributed on licence GNU GPL V3.
*/
class DateRange {

    private class UpdateListener<T> implements ChangeListener<T>{

        @Override
        public void changed(ObservableValue<? extends T> observableValue, T t, T t2) {
            DateRange.this.updatePlots();
        }
    }

    public static enum Length{
        SECOND,
        MINUTE,
        HOUR,
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    private IntegerProperty numberOfParts=new SimpleIntegerProperty(10);

    private ObjectProperty<Date> start= new SimpleObjectProperty<>();
    private ObjectProperty<Date> end=new SimpleObjectProperty<>();

    private ObjectProperty<ArrayList<Date>> plotsForTheRange = new ObjectPropertyBase<ArrayList<Date>>() {
        @Override
        public Object getBean() {
            return DateRange.this;
        }

        @Override
        public String getName() {
            return "Dates to divide the range in parts";
        }
    };

    {
        numberOfParts.addListener(new UpdateListener<>());
        start.addListener(new UpdateListener<>());
        end.addListener(new UpdateListener<>());
    }

    public DateRange(Date start, Date end){
        startProperty().setValue(start);
        endProperty().setValue(end);
        updatePlots();
    }

    private void updatePlots(){
        if(endProperty().getValue()!=null && startProperty().getValue()!=null)
            plotsForTheRange.setValue(divideInParts(numberOfParts.getValue()));
    }

    public ObjectProperty<Date> startProperty(){
        return start;
    }

    public ObjectProperty<Date> endProperty(){
        return end;
    }

    public ObjectProperty<ArrayList<Date>> plotsProperty(){
        return plotsForTheRange;
    }

    public IntegerProperty partsProperty(){
        return numberOfParts;
    }

    /**
     * The length from this Range
     * @return Length in seconds
     */
    public long length(){
        return lengthBinding.get();
    }

    /**
     * The length from this Range
     * @return Length in seconds
     */
    public LongBinding lengthBinding=new LongBinding() {

            {
                super.bind(start,end);
            }

            @Override
            protected long computeValue() {
                if(endProperty().getValue()!=null && startProperty().getValue()!=null)
                    return endProperty().getValue().getTime() - startProperty().getValue().getTime() ;
                return 0;
            }
        };

    /**
     * Describe this Range by an interval.
     * @see DateRange.Length
     * @return The length who best describe the Range
     */
    public Length getRangeCategory(){
        // NOTE: Too big values are calculated and written as long
        if(length()>31536000000L) // Length is more than a year
            return Length.YEAR;
        else if(length()>2678400000L) // Length is more than a month
            return Length.MONTH;
        else if(length()>(1000*60*60*24*7))
            return Length.WEEK;
        else if(length()>(1000*60*60*24))
            return Length.DAY;
        else if(length()>(1000*60*60))
            return Length.HOUR;
        else if(length()>(1000*60))
            return Length.MINUTE;
        else
            return Length.SECOND;
    }

    /**
     * Give a set of Date to divide the Range in parts.
     * @param parts the number of parts
     * @return The set of Date values to divide this Range
     */
    private ArrayList<Date> divideInParts(int parts){
        ArrayList<Date> plots=new ArrayList<>(parts+1);
        int partLength = Math.round(length()/parts);
        System.out.println("Part len : "+partLength);
        for(int i=0;i<parts;i++)
            plots.add(new Date(startProperty().getValue().getTime()+partLength*i));
        plots.add(end.getValue());
        return plots;
    }

}
