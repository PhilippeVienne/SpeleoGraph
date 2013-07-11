package org.cds06.speleograph.data;

import org.jfree.chart.axis.NumberAxis;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class Type implements Comparable<Type> {

    public static final Type UNKNOWN = new Type(DataType.OTHER,"","Donnée");
    public static final Type PRESSURE = new Type(DataType.PRESSURE);
    public static final Type TEMPERATURE = new Type(DataType.TEMPERATURE);
    public static final Type TEMPERATURE_MIN_MAX = new Type(DataType.TEMPERATURE_MIN_MAX);
    public static final Type WATER = new Type(DataType.WATER);

    /**
     * All types are equal.
     *
     * @param o Type to compare with
     * @return Always 0.
     */
    @Override
    public int compareTo(Type o) {
        return this.equals(o) ? 0 : -1;
    }

    public static enum DataType {
        TEMPERATURE,
        TEMPERATURE_MIN_MAX,
        PRESSURE,
        WATER,
        OTHER
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NumberFormat getFormatter() {
        return formatter;
    }

    public void setFormatter(NumberFormat formatter) {
        this.formatter = formatter;
    }

    public NumberAxis getAxis() {
        if (axis == null) {
            axis = new NumberAxis(name + " (" + unit + ")");

        }
        return axis;
    }

    private DataType type;
    private List<DataSet> sets = new ArrayList<>();
    private String unit;
    private String name;
    private NumberAxis axis;
    private NumberFormat formatter = new NumberFormat() {

        @Override
        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
            String txt = Double.toString(number);
            txt += " ";
            txt += unit;
            return toAppendTo.append(txt);
        }

        @Override
        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
            String txt = Long.toString(number);
            txt += " ";
            txt += unit;
            return toAppendTo.append(txt);
        }

        @Override
        public Number parse(String source, ParsePosition parsePosition) {
            return Double.valueOf(source.split(" ", 2)[0]);
        }
    };

    public void registerToDataSet(DataSet set){
        sets.add(set);
    }

    public void unlinkFromDataSet(DataSet set){
        sets.remove(set);
    }

    public Type(DataType type) {
        setUpDefaults(type);
        this.type = type;
    }

    public Type(DataType type, String unit) {
        if (type != DataType.OTHER) setUpDefaults(type);
        this.type = type;
        if (unit != null) this.unit = unit;
    }

    public Type(DataType type, String unit, String name) {
        this(type, unit);
        this.name = name;
    }

    public Type(DataType type, String unit, String name, NumberFormat format) {
        this(type, unit, name);
        if (format != null) this.formatter = format;
    }

    private void setUpDefaults(DataType type) {
        if (type == null) throw new NullPointerException("Type can not be null");
        if (type == DataType.OTHER) throw new IllegalArgumentException("Type can not be OTHER with this constructor");
        switch (type) {
            case TEMPERATURE:
                this.name = "Température";
                this.unit = "°C";
                break;
            case TEMPERATURE_MIN_MAX:
                this.name = "Température (min/max)";
                this.unit = "°C";
                break;
            case WATER:
                this.name = "Précipitations";
                this.unit = "mm";
                break;
            case PRESSURE:
                this.name = "Pression";
                this.unit = "hPa";
                break;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

}
