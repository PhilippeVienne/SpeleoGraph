package org.cds06.speleograph;

import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

import java.util.Date;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class SpeleoChart extends XYChart<Date, Number> {

    /**
     * Constructs a XYChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     * @param xAxis X Axis for this XY chart
     * @param yAxis Y Axis for this XY chart
     */
    public SpeleoChart(Axis<Date> dateAxis, Axis<Number> numberAxis) {
        super(dateAxis, numberAxis);
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

    }

    /**
     * Called when a data item has changed, ie its xValue, yValue or extraValue has changed.
     *
     * @param item The data item who was changed
     */
    @Override
    protected void dataItemChanged(Data<Date, Number> item) {

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

    }

    /**
     * Called to update and layout the plot children. This should include all work to updates nodes representing
     * the plot on top of the axis and grid lines etc. The origin is the top left of the plot area, the plot area with
     * can be got by getting the width of the x axis and its height from the height of the y axis.
     */
    @Override
    protected void layoutPlotChildren() {

    }
}
