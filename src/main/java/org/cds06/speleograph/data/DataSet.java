package org.cds06.speleograph.data;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class DataSet implements XYDataset, Series.SeriesChangeListener {

    private static HashMap<Type, DataSet> dataSets = new HashMap<>();

    public static DataSet[] getDataSetInstances() {
        return dataSets.values().toArray(new DataSet[dataSets.size()]);
    }

    public static DataSet getDataSet(Type type) {
        if (dataSets.containsKey(type)) return dataSets.get(type);
        dataSets.put(type, new DataSet());
        return getDataSet(type);
    }

    public static void pushSeries(Series... series) {
        for (Series s : series) {
            DataSet set = getDataSet(s.getType());
            s.addListener(set);
            set.series.add(s);
        }
    }

    private ArrayList<Series> series = new ArrayList<>();
    private ArrayList<Series> shownSeries = new ArrayList<>();

    private ArrayList<Series> getShownSeries() {
        return shownSeries;
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return The series count.
     */
    @Override
    public int getSeriesCount() {
        return getShownSeries().size();
    }

    /**
     * Returns the key for a series.
     * <p/>
     * If <code>series</code> is not within the specified range, the
     * implementing method should throw an {@link IndexOutOfBoundsException}
     * (preferred) or an {@link IllegalArgumentException}.
     *
     * @param series the series index (in the range <code>0</code> to
     *               <code>getSeriesCount() - 1</code>).
     * @return The series key.
     */
    @Override
    public Comparable getSeriesKey(int series) {
        if (series < getShownSeries().size()) {
            return getShownSeries().get(series);
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Returns the index of the series with the specified key, or -1 if there
     * is no such series in the dataset.
     *
     * @param seriesKey the series key (<code>null</code> permitted).
     * @return The index, or -1.
     */
    @Override
    public int indexOf(Comparable seriesKey) {
        final int seriesCount = getSeriesCount();
        for (int i = 0; i < seriesCount; i++) {
            final Series seriesHashCode = series.get(i);
            if (seriesHashCode == seriesKey) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the order of the domain (or X) values returned by the dataset.
     *
     * @return The order (never <code>null</code>).
     */
    @Override
    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }

    /**
     * Returns the number of items in a series.
     * <br><br>
     * It is recommended that classes that implement this method should throw
     * an <code>IllegalArgumentException</code> if the <code>series</code>
     * argument is outside the specified range.
     *
     * @param series the series index (in the range <code>0</code> to
     *               <code>getSeriesCount() - 1</code>).
     * @return The item count.
     */
    @Override
    public int getItemCount(int series) {
        if (series < getShownSeries().size()) {
            return getShownSeries().get(series).getItems().size();
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Returns the x-value for an item within a series.  The x-values may or
     * may not be returned in ascending order, that is up to the class
     * implementing the interface.
     *
     * @param series the series index (in the range <code>0</code> to
     *               <code>getSeriesCount() - 1</code>).
     * @param item   the item index (in the range <code>0</code> to
     *               <code>getItemCount(series)</code>).
     * @return The x-value (never <code>null</code>).
     */
    @Override
    public Number getX(int series, int item) {
        return getXValue(series, item);
    }

    /**
     * Returns the x-value for an item within a series.
     *
     * @param series the series index (in the range <code>0</code> to
     *               <code>getSeriesCount() - 1</code>).
     * @param item   the item index (in the range <code>0</code> to
     *               <code>getItemCount(series)</code>).
     * @return The x-value.
     */
    @Override
    public double getXValue(int series, int item) {
        if (series < getShownSeries().size()) {
            if (item < getShownSeries().get(series).getItems().size()) {
                return getShownSeries().get(series).getItems().get(item).getDate().getTime();
            }
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series the series index (in the range <code>0</code> to
     *               <code>getSeriesCount() - 1</code>).
     * @param item   the item index (in the range <code>0</code> to
     *               <code>getItemCount(series)</code>).
     * @return The y-value (possibly <code>null</code>).
     */
    @Override
    public Number getY(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Returns the y-value (as a double primitive) for an item within a series.
     *
     * @param series the series index (in the range <code>0</code> to
     *               <code>getSeriesCount() - 1</code>).
     * @param item   the item index (in the range <code>0</code> to
     *               <code>getItemCount(series)</code>).
     * @return The y-value.
     */
    @Override
    public double getYValue(int series, int item) {
        if (series < getShownSeries().size()) {
            if (item < getShownSeries().get(series).getItems().size()) {
                return getShownSeries().get(series).getItems().get(item).getValue();
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void onChange(Series changed, Series.PropertyName propertyName) {
        if (propertyName == Series.PropertyName.SHOWN)
            refreshShownList();
    }

    private Type type = null;

    public Type getType() {
        if (type != null) return type;
        for (Type type : dataSets.keySet()) {
            if (getDataSet(type).equals(this))
                return type;
        }
        return null;
    }

    private void refreshShownList() {
        ArrayList<Series> list = new ArrayList<>(series);
        for (Series s : list)
            if (!s.isShow()) list.remove(s);
        shownSeries = list;
    }

    public void refresh() {
        refreshShownList();
    }

    private ArrayList<DatasetChangeListener> listeners = new ArrayList<>();

    /**
     * Notify sub-listeners about a change in the Set.
     */
    protected void notifyChangeListeners() {
        DatasetChangeEvent event = new DatasetChangeEvent(this, this);
        for (DatasetChangeListener listener : listeners)
            listener.datasetChanged(event);
    }

    /**
     * Registers an object for notification of changes to the dataset.
     *
     * @param listener the object to register.
     */
    @Override
    public void addChangeListener(DatasetChangeListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    /**
     * Deregisters an object for notification of changes to the dataset.
     *
     * @param listener the object to deregister.
     */
    @Override
    public void removeChangeListener(DatasetChangeListener listener) {
        if (listeners.contains(listener)) listeners.remove(listener);
    }

    private DatasetGroup group = new DatasetGroup();

    /**
     * Returns the dataset group.
     *
     * @return The dataset group.
     */
    @Override
    public DatasetGroup getGroup() {
        return group;
    }

    /**
     * Sets the dataset group.
     *
     * @param group the dataset group.
     */
    @Override
    public void setGroup(DatasetGroup group) {
        if (group == null) throw new NullPointerException("Group can not be null");
        this.group = group;
    }
}
