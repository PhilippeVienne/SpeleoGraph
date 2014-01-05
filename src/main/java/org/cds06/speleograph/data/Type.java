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

    public static final Type UNKNOWN = Type.getType("Data", null);
    public static final Type PRESSURE = Type.getType("Pression", "hPa");
    public static final Type TEMPERATURE = Type.getType("Température", "°C");
    public static final Type TEMPERATURE_MIN_MAX = Type.getType("Température (min/max)", "°C");
    public static final Type WATER = Type.getType("Précipitations", "mm");

    /**
     * Get Type by name and unit.
     * Find a Type instance using its name and unit. If no type are found we return a new Type.
     *
     * @param name Name of this type (not blank)
     * @param unit Unit of this type
     * @return Type instance which correspond to parameters.
     */
    public static Type getType(@NotNull @NonNls String name, @NonNls String unit) {
        Validate.notBlank(name, "Type name can not be blank"); // NON-NLS
        for (Type type : instances) {
            if (type.name.equals(name) &&
                    type.unit.equals(unit)) {
                return type;
            }
        }
        return new Type(unit, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") Type o) {
        return this.equals(o) ? 0 : -1;
    }

    public void setAxis(NumberAxis axis) {
        this.axis = axis;
    }

    public String getUnit() {
        return unit;
    }

    public String getName() {
        return name;
    }

    public NumberAxis getAxis() {
        if (axis == null) {
            axis = new NumberAxis(name + " (" + unit + ")");
        }
        return axis;
    }

    private String unit = "";
    private String name = "Inconnu";
    private NumberAxis axis;

    public Type(String unit) {
        if (unit != null) this.unit = unit;
        instances.add(this);
    }

    public Type(String unit, String name) {
        this(unit);
        this.name = name;
    }

    @Override
    public String toString() {
        return name + (unit == null || unit.isEmpty() ? "" : " (" + unit + ")");
    }

    /**
     * Determine if two Type are equals.
     * Type are equals if :
     * <ul>
     * <li>They has got the same name</li>
     * <li>They has got the same units</li>
     * </ul>
     *
     * @return true if the type are equals.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) return false;
        Type typeToCompare = (Type) o;
        return name.equals(typeToCompare.name) && unit.equals(typeToCompare.unit);
    }

    /**
     * Returns the identification string for this group.
     *
     * @return The identification string.
     */
    @Override
    public String getID() {
        return toString();
    }
}
