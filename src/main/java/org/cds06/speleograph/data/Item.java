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

import java.util.Calendar;
import java.util.Date;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class Item implements Comparable<Item> {

    private Series series = null;
    private Date date = Calendar.getInstance().getTime();
    private double value = Double.NaN;
    private double low = Double.NaN;
    private double high = Double.NaN;

    public Item(Series s, Date date, double value) {
        this.value = value;
        this.date = date;
        series = s;
    }

    public Item(Series s, Date date, double low, double high) {
        this.date = date;
        this.low = low;
        this.high = high;
        series = s;
        if (!s.isMinMax()) s.setMinMax(true);
    }

    public double getValue() {
        return value;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public Date getDate() {
        return date;
    }

    public Series getSeries() {
        return series;
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") Item o) {
        if (o == null) throw new NullPointerException("Value compared is null");
        return getValue()<o.getValue() ? -1 : (getValue()==o.getValue() ? 0 : 1);
    }
}
