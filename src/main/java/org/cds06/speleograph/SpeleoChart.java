package org.cds06.speleograph;

import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Chart to draw a chart of SpeleoGraph Data.
 */
public class SpeleoChart extends LineChart<Date, Number> {

    private static Logger log = Logger.getLogger(SpeleoChart.class);

    final public class SpeleoDataMap extends SimpleMapProperty<DataSet, SpeleoChart.Series<Date, Number>> {

        public SpeleoDataMap() {
            super(SpeleoChart.this, "Data Map for the chart", FXCollections.<DataSet, Series<Date, Number>>observableHashMap());
        }

        @Override
        public Series<Date, Number> put(DataSet data, Series<Date, Number> s) {
            if (s == null) {
                return put(data, SpeleoChart.generateSeriesFor(data));
            }
            return super.put(data, s);
        }

    }

    public SpeleoDataMap dataMap = new SpeleoDataMap();

    {
        dataMap.addListener(new MapChangeListener<DataSet, Series<Date, Number>>() {
            @Override
            public void onChanged(Change<? extends DataSet, ? extends Series<Date, Number>> change) {
                if (change.wasAdded() && change.getValueAdded() != null) {
                    log.debug("DataSet '" + change.getKey().getName() + "' was added with a non-null Series");
                    if (!SpeleoChart.this.getData().contains(change.getValueAdded()))
                        SpeleoChart.this.getData().add(change.getValueAdded());
                } else if (change.wasRemoved()) {
                    log.debug("DataSet '" + change.getKey().getName() + "' was removed");
                    if (SpeleoChart.this.getData().contains(change.getValueRemoved()))
                        SpeleoChart.this.getData().remove(change.getValueRemoved());
                    updateLegend();
                }
            }
        });
    }

    /**
     * Constructs a XYChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     * @param dateAxis   X Axis for this XY chart
     * @param numberAxis Y Axis for this XY chart
     */
    public SpeleoChart(Axis<Date> dateAxis, Axis<Number> numberAxis) {
        super(dateAxis, numberAxis);
        dataProperty().setValue(FXCollections.<Series<Date, Number>>observableArrayList());
        dataProperty().getValue();
        setAnimated(false);
        dateAxis.setAnimated(false);
        numberAxis.setAnimated(false);
    }

    public SpeleoChart(Axis<Date> xAxis, Axis<Number> yAxis, ObservableList<Series<Date, Number>> data) {
        super(xAxis, yAxis, data);
    }

    public SpeleoChart() {
        super(new DateAxis(), new NumberAxis());
    }

    /**
     * Called when a data item has been added to a series. This is where implementations of XYChart can create/add new
     * nodes to getPlotChildren to represent this data item. They also may animate that data add with a fade in or
     * similar if animated = true.
     *
     * @param series    The series the data item was added to
     * @param itemIndex The index of the new item within the series
     * @param item      The new data item that was added
     */
    @Override
    protected void dataItemAdded(Series<Date, Number> series, int itemIndex, Data<Date, Number> item) {
        super.dataItemAdded(series, itemIndex, item);
    }

    /**
     * Called when a data item has been removed from data model but it is still visible on the chart. Its still visible
     * so that you can handle animation for removing it in this method. After you are done animating the data item you
     * must call removeDataItemFromDisplay() to remove the items node from being displayed on the chart.
     *
     * @param item   The item that has been removed from the series
     * @param series The series the item was removed from
     */
    @Override
    protected void dataItemRemoved(Data<Date, Number> item, Series<Date, Number> series) {
        super.dataItemRemoved(item, series);
    }

    /**
     * Called when a data item has changed, ie its xValue, yValue or extraValue has changed.
     *
     * @param item The data item who was changed
     */
    @Override
    protected void dataItemChanged(Data<Date, Number> item) {
        super.dataItemChanged(item);
    }

    /**
     * A series has been added to the charts data model. This is where implementations of XYChart can create/add new
     * nodes to getPlotChildren to represent this series. Also you have to handle adding any data items that are
     * already in the series. You may simply call dataItemAdded() for each one or provide some different animation for
     * a whole series being added.
     *
     * @param series      The series that has been added
     * @param seriesIndex The index of the new series
     */
    @Override
    protected void seriesAdded(Series<Date, Number> series, int seriesIndex) {
        super.seriesAdded(series, seriesIndex);
        log.debug("Oh, a new Data has been added :-)");
    }

    /**
     * A series has been removed from the data model but it is still visible on the chart. Its still visible
     * so that you can handle animation for removing it in this method. After you are done animating the data item you
     * must call removeSeriesFromDisplay() to remove the series from the display list.
     *
     * @param series The series that has been removed
     */
    @Override
    protected void seriesRemoved(Series<Date, Number> series) {
        super.seriesRemoved(series);
        log.debug("Oh no ! a Data has been removed :-(");
    }

    /**
     * Called to update and layout the plot children. This should include all work to updates nodes representing
     * the plot on top of the axis and grid lines etc. The origin is the top left of the plot area, the plot area with
     * can be got by getting the width of the x axis and its height from the height of the y axis.
     */
    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
    }

    @Override
    public DateAxis getXAxis() {
        return (DateAxis) super.getXAxis();
    }

    /**
     * Full call for refresh all chart.
     */
    public void refresh() {
        getXAxis().requestAxisLayout();
        getYAxis().requestAxisLayout();
        layoutPlotChildren();
        layoutChildren();
        setNeedsLayout(true);
        layout();
    }

    /**
     * Generate series for a DataSet.
     *
     * @param data The DataSet to use to generate the series
     * @return The series.
     */
    private static Series<Date, Number> generateSeriesFor(DataSet data) {
        Series<Date, Number> s = new Series<>();
        s.setName(data.getName());
        for (org.cds06.speleograph.Data d : data) {
            s.getData().add(new Data<>(d.date, d.getValueForAChart(), d));
        }
        return s;
    }
}
