/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cds06.speleograph;

import com.sun.media.sound.InvalidFormatException;

import java.beans.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Represent a Data extracted from measure.
 *
 * @since 1.0
 * @author PhilippeGeek
 */
public class Data {

    public Type getDataType() {
        return dataType;
    }

    public void setDataType(Type dataType) {
        this.dataType = dataType;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Setter for measure date.
     * You should prefer use the function #setDate(String,String)
     * @param date The date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Parse a date for this data.
     * @param date The string representing the date to parse and set
     * @param format The date format in the string
     * @see SimpleDateFormat#parse(String)
     * @throws InvalidFormatException On parsing error, see SimpleDateFormat for more details
     */
    public void setDate(String date, String format) throws InvalidFormatException {
        try {
            this.setDate(new SimpleDateFormat(format).parse(date));
        } catch (ParseException e) {
            throw new InvalidFormatException("The date "+date+" can not be parse with "+format);
        }
    }

    /**
     * Getter for #value field.
     * If there is a min and max values but no value, this function will return the average of both.
     * @return The current value of Data
     */
    public Double getValue() {
        if(value==Double.MIN_VALUE&&isMinAndMaxSat())
            return (getMinValue()+getMaxValue())/2;
        return value;
    }

    /**
     * Set the value of current data.
     * If it's a Temperature information and #isMinAndMaxSat is false, then min and max value are the value.
     * @param value the data value
     */
    public void setValue(Double value) {
        this.value = value;
        if(getDataType()==Type.TEMPERATURE&&!isMinAndMaxSat()){
            maxValue = value;
            minValue = value;
        }
    }

    /**
     * Set all values of current data.
     * It's a one call function to set value, min and max.
     * @param value the data value
     * @param maxValue maximal value on this Date
     * @param minValue minimal value on this Date
     * @see #setMinValue(Double)
     * @see #setMaxValue(Double)
     * @see #setValue(Double)
     */
    public void setValue(Double value,Double maxValue,Double minValue){
        setMaxValue(maxValue);
        setMinValue(minValue);
        setValue(value);
    }

    /**
     * Detect if there is a Minimal and a Maximal value.
     * There is a Minimal and Maximal value if one of theme is not equal to
     * {@code Double.MIN_VALUE}.
     * @return A boolean true if one of theme valid the condition
     */
    public boolean isMinAndMaxSat(){
        return ((getMinValue()!=Double.MIN_VALUE)||(getMaxValue()!=Double.MIN_VALUE));
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
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

    private Type dataType = Type.TEMPERATURE;
    private Date date = Calendar.getInstance().getTime();
    private Double value = Double.MIN_VALUE;
    private Double minValue = Double.MIN_VALUE;
    private Double maxValue = Double.MIN_VALUE;

    @Override
    public String toString() {
        String desc="Measure information :\n";
        desc+=" - Type: "+getDataType().toString()+"\n";
        desc+=" - Date: "+SimpleDateFormat.getDateTimeInstance().format(getDate())+"\n";
        switch(getDataType()){
            case TEMPERATURE_MIN_MAX:
                desc += " - Value is between "+getMinValue()+"°C and "+getMaxValue()+"°C";
                break;
            case WATER:
                desc += " - Cumulative "+getValue()+"mm/m²";
                break;
            case TEMPERATURE:
                desc += " - Value : "+getValue()+"°C";
                break;
            case PRESSURE:
                desc += " - Value : "+getValue()+"Pa";
                break;
            default:
                desc += " - Value : "+getValue();
        }
        desc+="\n";
        return desc;
    }
}
