/*
 * Copyright (c) 2013 Philippe VIENNE
 *
 * This file is a part of SpeleoGraph
 *
 * SpeleoGraph is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * SpeleoGraph is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with SpeleoGraph.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.cds06.speleograph.data;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.general.DatasetGroup;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class Type extends DatasetGroup implements Comparable<Type>, Cloneable {

    private static final ArrayList<Type> instances = new ArrayList<>(5);

    public static ArrayList<Type> getInstances() {
        return instances;
    }

    /**
     * Get Type by name and unit.
     * Find a Type instance using its name and unit. If no type are found we return a new Type.
     *
     * @param name Name of this type (not null)
     * @param unit Unit of this type (not null)
     * @return Type instance which correspond to parameters.
     */
    public static Type getType(@NotNull String name, @NotNull String unit) {
        Validate.notBlank(name, "Type name can not be blank"); // NON-NLS
        Validate.notNull(unit, "Unit can not be null"); // NON-NLS
        for (Type type : instances) {
            if (type.name.equals(name) && type.unit.equals(unit)) {
                return type;
            }
        }
        return new Type(DataType.OTHER, unit, name);
    }

    public static final Type UNKNOWN = new Type(DataType.OTHER, "", "Donnée");
    public static final Type PRESSURE = new Type(DataType.PRESSURE);
    public static final Type TEMPERATURE = new Type(DataType.TEMPERATURE);
    public static final Type TEMPERATURE_MIN_MAX = new Type(DataType.TEMPERATURE_MIN_MAX);
    public static final Type WATER = new Type(DataType.WATER);

    public static final Type[] internalTypes = new Type[]{
            PRESSURE,
            TEMPERATURE,
            TEMPERATURE_MIN_MAX,
            WATER
    };

    private boolean highLow = false;

    public boolean isSteppedType() {
        return isSteppedType;
    }

    public void setSteppedType(boolean steppedType) {
        isSteppedType = steppedType;
    }

    private boolean isSteppedType = false;

    /**
     * All types are equal.
     *
     * @param o Type to compare with
     * @return Always 0.
     */
    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") Type o) {
        return this.equals(o) ? 0 : -1;
    }

    public Type asStepType() {
        try {
            final Type newType = (Type) clone();
            newType.setSteppedType(true);
            return newType;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return Type.UNKNOWN;
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

    public NumberAxis getAxis() {
        if (axis == null) {
            axis = new NumberAxis(name + " (" + unit + ")");

        }
        return axis;
    }

    private DataType type;
    private String unit = "";
    private String name = "Inconnu";
    private NumberAxis axis;
    private final NumberFormat formatter = new NumberFormat() {

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
            source = source.split(" ", 2)[0];
            return NumberFormat.getNumberInstance().parse(source, parsePosition);
        }
    };

    public boolean isHighLowType() {
        return highLow;
    }

    public void setHighLowType(boolean v) {
        highLow = v;
    }

    public Type(DataType type) {
        setUpDefaults(type);
        this.type = type;
        instances.add(this);
    }

    public Type(DataType type, String unit) {
        if (type != DataType.OTHER) setUpDefaults(type);
        this.type = type;
        if (unit != null) this.unit = unit;
        instances.add(this);
    }

    public Type(DataType type, String unit, String name) {
        this(type, unit);
        this.name = name;
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
                this.highLow = true;
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

    @Override
    public String toString() {
        return name + (unit.isEmpty() ? "" : " (" + unit + ")");
    }

    /**
     * Determine if two Type are equals.
     * Type are equals if :
     * <ul>
     * <li>They has got the same name</li>
     * <li>They has got the same units</li>
     * <li>Properties like isMinMax, isSampled are equals</li>
     * </ul>
     *
     * @return true if the type are equals.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) return false;
        Type typeToCompare = (Type) o;
        return
                getName().equals(typeToCompare.getName()) &&
                        name.equals(typeToCompare.name) &&
                        unit.equals(typeToCompare.unit) &&
                        isSteppedType == typeToCompare.isSteppedType &&
                        highLow == typeToCompare.highLow;
    }

    /**
     * Returns the identification string for this group.
     *
     * @return The identification string.
     */
    @Override
    public String getID() {
        return getName();
    }
}
