package org.cds06.speleograph;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.util.Date;

/**
* This file is created by PhilippeGeek.
* Distributed on licence GNU GPL V3.
*/
class DateRange {

    public static enum Length{
        SECOND,
        MINUTE,
        HOUR,
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    private ObjectProperty<Date> start= new SimpleObjectProperty<>();
    private ObjectProperty<Date> end=new SimpleObjectProperty<>();

    public DateRange(Date start, Date end){
        startProperty().setValue(start);
        endProperty().setValue(end);
    }

    public ObjectProperty<Date> startProperty(){
        return start;
    }

    public ObjectProperty<Date> endProperty(){
        return end;
    }

    /**
     * The length from this Range
     * @return Length in seconds
     */
    public long length(){
        return Math.round(startProperty().getValue().getTime() - endProperty().getValue().getTime()/1000);
    }

    /**
     * Describe this Range by an interval.
     * @see DateRange.Length
     * @return The length who best describe the Range
     */
    public Length getRangeCategory(){
        if(length()>(60*60*24*365))
            return Length.YEAR;
        else if(length()>(60*60*24*31))
            return Length.MONTH;
        else if(length()>(60*60*24*7))
            return Length.WEEK;
        else if(length()>(60*60*24))
            return Length.DAY;
        else if(length()>(60*60))
            return Length.HOUR;
        else if(length()>(60))
            return Length.MINUTE;
        else
            return Length.SECOND;
    }

}
