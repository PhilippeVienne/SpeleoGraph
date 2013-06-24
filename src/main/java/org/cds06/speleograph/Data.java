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
 *
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

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String date, String format) throws InvalidFormatException {
        try {
            this.setDate(new SimpleDateFormat(format).parse(date));
        } catch (ParseException e) {
            throw new InvalidFormatException("The date "+date+" can not be parse with "+format);
        }
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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
         * Amount of water in mm/m².
         */
        WATER
    }

    private Type dataType = Type.TEMPERATURE;
    private Date date = Calendar.getInstance().getTime();
    private Double value = 0.0;

    @Override
    public String toString() {
        return dataType.toString()+" Information, taken on "+SimpleDateFormat.getDateInstance().format(date)+", value : "+value;
    }
}
