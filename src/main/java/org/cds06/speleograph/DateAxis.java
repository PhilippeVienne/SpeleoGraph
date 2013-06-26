package org.cds06.speleograph;

import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* This file is created by PhilippeGeek.
* Distributed on licence GNU GPL V3.
*/
public class DateAxis extends Axis<Date> {

    //
    // Minimal and Maximal Date properties for this axis
    //

    /**
     * Minimal date for this axis.
     * Default value: {@code new Date(System.currentTimeMillis())}
     */
    private final ObjectProperty<Date> minDateProperty=new ObjectPropertyBase<Date>(new Date(System.currentTimeMillis())) {
        @Override
        public Object getBean() {
            return DateAxis.this;
        }

        @Override
        public String getName() {
            return "Minimal Date shown on the graphic";
        }
    };

    /**
     * Property accessor for minimal date displayed on this axis.
     *
     * @return the property
     * @see #minDateProperty
     */
    public ObjectProperty<Date> minDateProperty(){
        return  minDateProperty;
    }

    /**
     * Get the minimal date displayed on the axis.
     * @return the date stored
     * @see #minDateProperty()
     */
    public Date getMinDate(){
        return minDateProperty().getValue();
    }

    /**
     * Set the minimal date to display on this axis.
     * @param minDate The new date to set
     * @see #minDateProperty()
     */
    public void setMinDate(Date minDate){
        minDateProperty().setValue(minDate);
    }

    /**
     * Maximal date for this axis.
     * Default value: {@code new Date(System.currentTimeMillis())}
     */
    private final ObjectProperty<Date> maxDateProperty=new ObjectPropertyBase<Date>(new Date(System.currentTimeMillis())) {
        @Override
        public Object getBean() {
            return DateAxis.this;
        }

        @Override
        public String getName() {
            return "Maximal Date shown on the graphic";
        }
    };

    /**
     * Property accessor for maximal date displayed on this axis.
     *
     * @return the property
     * @see #maxDateProperty
     */
    public ObjectProperty<Date> maxDateProperty(){
        return  maxDateProperty;
    }

    /**
     * Get the maximal date displayed on the axis.
     * @return the date stored
     * @see #maxDateProperty()
     */
    public Date getMaxDate(){
        return maxDateProperty().getValue();
    }

    /**
     * Set the maximal date to display on this axis.
     * @param maxDate The new date to set
     * @see #maxDateProperty()
     */
    public void setMaxDate(Date maxDate){
        maxDateProperty().setValue(maxDate);
    }

    /**
     * Actual length of this axis in pixels.
     * <i>No scale on the date axis</i>
     */
    private DoubleProperty length=new SimpleDoubleProperty(this,"Axis length",100);

    /**
     * Actual range shown.
     * This auto-refresh on dateMin or dateMax change.
     * @see #minDateProperty()
     * @see #maxDateProperty()
     */
    private ObjectProperty<DateRange> rangeProperty=new ObjectPropertyBase<DateRange>(new DateRange(getMinDate(),getMaxDate())) {
        @Override
        public Object getBean() {
            return DateAxis.this;
        }

        @Override
        public String getName() {
            return "Range Value";
        }
    };

    /**
     * Property for the actual range shown.
     * This auto-refresh on dateMin or dateMax change.
     * @see #minDateProperty()
     * @see #maxDateProperty()
     */
    public ObjectProperty<DateRange> rangeProperty(){
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

    private  ObjectProperty<ArrayList<Date>> tickedDates=new ObjectPropertyBase<ArrayList<Date>>() {
        @Override
        public Object getBean() {
            return DateAxis.this;
        }

        @Override
        public String getName() {
            return "Ticked dates list";
        }
    };

    {
        tickedDates.bindBidirectional(rangeProperty().getValue().plotsProperty());
        rangeProperty.addListener(new ChangeListener<DateRange>() {
            @Override
            public void changed(ObservableValue<? extends DateRange> observableValue, DateRange oldDateRange, DateRange newDateRange) {
                tickedDates.unbindBidirectional(oldDateRange.plotsProperty());
                tickedDates.bindBidirectional(newDateRange.plotsProperty());
            }
        });
    }

    @Override
    protected Object autoRange(double length) {
        return rangeProperty().getValue();
    }

    @Override
    protected void setRange(Object range, boolean b) {
        if(range instanceof DateRange)
            rangeProperty.setValue((DateRange) range);
        else
            throw new IllegalArgumentException("It's not a DateRange");
    }

    @Override
    protected Object getRange() {
        return rangeProperty.getValue();
    }

    @Override
    public double getZeroPosition() {
        return Double.NaN;
    }

    private DoubleBinding millisecondLength = new DoubleBinding() {

        {
            super.bind(length,rangeProperty,maxDateProperty,minDateProperty);
        }

        @Override
        protected double computeValue() {
            return (length.getValue()/rangeProperty.getValue().length());
        }
    };

    @Override
    public double getDisplayPosition(Date date) {
        return toNumericValue(date);
    }

    @Override
    public Date getValueForDisplay(double v) {
        return toRealValue(v);
    }

    @Override
    public boolean isValueOnAxis(Date date) {
        return (date.after(getMinDate()))&&(date.before(getMaxDate()));
    }

    @Override
    public double toNumericValue(Date date) {
        return (date.getTime()-getMinDate().getTime())*(millisecondLength.get());
    }

    @Override
    public Date toRealValue(double v) {
        return new Date(getMinDate().getTime()+Math.round(v*millisecondLength.get()));
    }

    @Override
    protected List<Date> calculateTickValues(double length, Object range) {
        this.length.setValue(length);
        for(Date d:tickedDates.get())
            System.out.println(d);
        return tickedDates.get();
    }

    @Override
    protected String getTickMarkLabel(Date date) {
        DateFormat dateFormat=new SimpleDateFormat();
        switch(rangeProperty().get().getRangeCategory()){

            default:
                dateFormat=new SimpleDateFormat("dd/MM/yy");
        }
        return dateFormat.format(date);
    }




}