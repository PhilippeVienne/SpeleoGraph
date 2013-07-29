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
import org.jetbrains.annotations.NonNls;
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
     * @param name    Name of this type (not null)
     * @param unit    Unit of this type (not null)
     * @param stepped Is a type with stepped data on a range
     * @param minMax  Is a min/max type
     * @return Type instance which correspond to parameters.
     */
    public static Type getType(@NotNull @NonNls String name, @NotNull @NonNls String unit,
                               boolean stepped, boolean minMax) {
        Validate.notBlank(name, "Type name can not be blank"); // NON-NLS
        Validate.notNull(unit, "Unit can not be null"); // NON-NLS
        for (Type type : instances) {
            if (type.name.equals(name) &&
                    type.unit.equals(unit) &&
                    type.isHighLowType() == minMax &&
                    type.isSteppedType() == stepped) {
                return type;
            }
        }
        final Type t = new Type(DataType.OTHER, unit, name);
        t.setSteppedType(stepped);
        t.setHighLowType(minMax);
        return t;
    }

    public static final Type UNKNOWN = Type.getType("Data", "", false, false);
    public static final Type PRESSURE = Type.getType("Pression", "hPa", false, false);
    public static final Type TEMPERATURE = Type.getType("Température", "°C", false, false);
    public static final Type TEMPERATURE_MIN_MAX = Type.getType("Température (min/max)", "°C", false, true);
    public static final Type WATER = Type.getType("Précipitations", "mm", false, false);

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

    public void setAxis(NumberAxis axis) {
        this.axis = axis;
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

    public Type(DataType type, String unit) {
        this.type = type;
        if (unit != null) this.unit = unit;
        instances.add(this);
    }

    public Type(DataType type, String unit, String name) {
        this(type, unit);
        this.name = name;
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
