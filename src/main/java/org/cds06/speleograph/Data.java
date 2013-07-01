/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cds06.speleograph;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represent a Data extracted from measure.
 *
 * @since 1.0
 * @author PhilippeGeek
 */
public class Data {

    /**
     * Describe the type of the Data.
     * It should never be null.
     */
    public Type dataType;

    /**
     * Measure date.
     * When has this data been measured ?
     */
    public Date date;

    /**
     * Value (without units) of the data.
     */
    public Double value;

    /**
     * Minimal value during the measure.
     */
    public Double minValue;

    /**
     * Maximal value during the measure.
     */
    public Double maxValue;

    /**
     * The dataSet which contains the measure.
     */
    public DataSet originalSet;

    public Data(DataSet originalSet, Type type, Date date, double value) {
        this.originalSet = originalSet;
        if (!originalSet.contains(this)) originalSet.add(this);
        this.dataType = type;
        this.date = date;
        this.value = value;
    }

    public Data(DataSet originalSet, Type dataType, Date date, Double maxValue, Double minValue) {
        this.originalSet = originalSet;
        if (!originalSet.contains(this)) originalSet.add(this);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.date = date;
        this.dataType = dataType;
    }

    /**
     * Computes a value between approximately 0 and 100 for a chart view.
     * @return The computed value
     */
    public Number getValueForAChart() {
        switch (dataType) {
            case PRESSURE:
                return (value - 700) / 50;
            default:
                return value;
        }
    }

    static public enum Type{
        /**
         * A pressure in hPa.
         */
        PRESSURE,
        /**
         * A temperature in °C.
         */
        TEMPERATURE,
        /**
         * Min, max of Temperature
         */
        TEMPERATURE_MIN_MAX,
        /**
         * Amount of water in mm/m².
         */
        WATER
    }

    @Override
    public String toString() {
        String desc = "Measure(";
        if (dataType != null) desc += "TYPE=" + dataType + ", ";
        if (date != null) desc += "DATE=" + SimpleDateFormat
                .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date) + ", ";
        if (value != null) desc += "VALUE=" + value + ", ";
        if (minValue != null) desc += "MIN_VALUE=" + minValue + ", ";
        if (maxValue != null) desc += "MAX_VALUE=" + maxValue + ", ";
        desc += ")";
        return desc;
    }
}
