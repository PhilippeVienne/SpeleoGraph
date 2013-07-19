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
import org.jfree.data.time.DateRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Represent a Series of Data.
 * A series is coherent set of Data.
 */
public class Series implements Comparable{

    /**
     * Logger for debug and errors in Series instances.
     */
    @SuppressWarnings("UnusedDeclaration")
    @NonNls
    private static final Logger log = LoggerFactory.getLogger(Series.class);

    /**
     * Create a new Series with programmatic.
     * @param name The name of the Series
     * @param type The type of the Series
     * @throws IOException if can not create a temp file for this series.
     */
    public Series(@NonNls @NotNull String name, @NotNull Type type) throws IOException {
        Validate.notBlank(name,"Name can not be null or blank"); //NON-NLS
        Validate.notNull(type,"Type can not be null"); // NON-NLS
        this.origin = File.createTempFile("FakeSpeleoGraphFile",".speleo");
        this.name = name;
        this.set = DataSet.getDataSet(type);
    }

    /**
     * Create a new Series opened from a file.
     * @param origin The file where this series has been read.
     */
    public Series(@NotNull File origin) {
        Validate.notNull(origin);
        this.origin = origin;
    }

    /**
     * Create a new Series opened from a file with a default Type.
     * @param origin The file where this series has been read.
     * @param type The type for this series
     */
    public Series(@NotNull File origin, @NotNull Type type) {
        this(origin);
        Validate.notNull(type,"Type can not be null");// NON-NLS
        this.set = DataSet.getDataSet(type);
        this.set.add(this);
    }

    /**
     * Flag to define if we must show this series on chart.
     */
    private boolean show = false;
    /**
     * The file where this series has been read.
     */
    private File origin = null;
    /**
     * Series items, children of series.
     */
    private ArrayList<Item> items = new ArrayList<>();
    /**
     * DataSet where this series is currently stored.
     */
    private DataSet set;
    /**
     * The name of the series.
     */
    private String name;
    /**
     * Axis linked to this series.
     * This axis replaces the Type's Axis only if it's not null.
     */
    private NumberAxis axis = null;

    /**
     * Count the number of items into this Series.
     * @return The number of items (assuming is 0 or more)
     */
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    /**
     * Get the file used to read the data.
     * @return The data origin's file.
     */
    public File getOrigin() {
        return origin;
    }

    /**
     * Compute the date range of the items in this set.
     * @return A date range which contains the lower and upper bounds of data.
     */
    public DateRange getRange() {
        int max = getItemCount();
        DateRange range;
        if (max == 0){
            Date now = Calendar.getInstance().getTime();
            return new DateRange(now, now);
        }
        Date minDate = new Date(Long.MAX_VALUE), maxDate = new Date(Long.MIN_VALUE);
        for (int i = 0; i < max; i++) {
            Item item = items.get(i);
            if (item.getStartDate().before(minDate)) minDate = item.getStartDate();
            if (item.getEndDate() != null) {
                if (item.getEndDate().after(maxDate)) maxDate = item.getEndDate();
            } else {
                if (item.getDate().after(maxDate)) maxDate = item.getDate();
            }
        }
        range = new DateRange(minDate, maxDate);
        return range;
    }

    /**
     * Getter for the series Type.
     * If this series is not attached to a DataSet, then we suppose that Type is {@link Type#UNKNOWN}
     * @return The type for this series
     */
    @NotNull
    public Type getType() {
        if(set == null) return Type.UNKNOWN;
        return set.getType();
    }

    /**
     * Getter for the series' items.
     * @return The pointer to the array list of items.
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Say if we should show this series on a graph.
     * @return true if we should show this series.
     */
    public boolean isShow() {
        return show;
    }

    /**
     * Set if we should show this series on a graph.
     * @param v true if we should show
     */
    public void setShow(boolean v) {
        show = v;
        notifyListeners(PropertyName.SHOWN);
    }

    /**
     * Getter for the axis to display for this series.
     * If the series does not define his own axis, this function will search the Type's Axis.
     *
     * @return A NumberAxis to display it in a chart (never null)
     * @throws IllegalStateException if we can not find an axis for this series.
     */
    public NumberAxis getAxis(){
        if(axis!=null) return axis;
        else if(set!=null) return getType().getAxis();
        else throw new IllegalStateException("Can not find an axis for series !"); //NON-NLS
    }

    /**
     * Setter for the axis.
     * If an axis is set to the series, then the Type axis would not be shown and the Chart will display this axis for
     * the series even if other shown series are using the Type's axis.
     *
     * @param axis The axis to set for this series.
     */
    public void setAxis(NumberAxis axis){
        if(axis == null){
            log.info("Setting a null axis to series "+getName());
        }
        this.axis = axis;
    }

    /**
     * Listeners for this series.
     */
    private ArrayList<SeriesChangeListener> listeners = new ArrayList<>();

    /**
     * Notify listeners about something changed into the series.
     * @param property The property which has changed.
     * @see PropertyName
     */
    protected void notifyListeners(PropertyName property) {
        for (SeriesChangeListener listener : listeners)
            listener.onChange(this, property);
    }

    /**
     * Add a listener on series' properties.
     * @param listener The listener will be called on events
     */
    public void addListener(SeriesChangeListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    /**
     * Remove a listener on series' properties.
     * @param listener The listener which will be removed.
     */
    public void removeListener(SeriesChangeListener listener) {
        if (listeners.contains(listener)) listeners.remove(listener);
    }

    /**
     * Getter for the current linked DataSet.
     * @return The linked set or null if there is not one.
     */
    public DataSet getDataSet() {
        return set;
    }

    /**
     * Link a {@link DataSet} to this Series.
     * @param set The dataSet to link.
     */
    public void setDataSet(DataSet set) {
        set.add(this);
        this.set = set;
    }

    /**
     * Getter for the human name of this series.
     * If the name is not set, it computes a name as "[Origin File Name] - [Name of the Type]"
     * @return The display name for this Series.
     */
    public String getName() {
        if(name == null)
            name = getOrigin().getName() + " - " + getType().getName();
        return name;
    }

    /**
     * Set an human name for this Series.
     * @param name The name to set (should not be null)
     */
    public void setName(String name) {
        Validate.notNull(name,"Series name set by human can not be null");//NON-NLS
        this.name = name;
        notifyListeners(PropertyName.NAME);
    }

    @Override
    public int compareTo(Object o) {
        return this.equals(o)?0:-1;
    }

    /**
     * Add an item to this series.
     * @param item The item to add.
     */
    public void add(Item item) {
        Validate.notNull(item);
        items.add(item);
    }

    /**
     * Define an interface for Listener with only one function as a callBack.
     */
    public interface SeriesChangeListener {
        public void onChange(Series theSeriesChanged, PropertyName propertyName);
    }

    /**
     * All properties which can be updated and passed to ChangeListener.
     */
    public enum PropertyName {
        NAME, SHOWN, COLOR
    }

    /**
     * Define the {@link Object#toString()} to be alias of {@link #getName()}.
     * @return The name of this series.
     * @see #getName()
     */
    @Override
    public String toString() {
        return getName();
    }

}
