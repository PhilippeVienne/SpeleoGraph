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
package org.cds06.speleograph;

import org.apache.commons.lang3.Validate;
import org.cds06.speleograph.data.DataSet;
import org.jetbrains.annotations.NonNls;
import org.jfree.chart.*;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for SpeleoGraph Charts.
 * <p>This class interact with {@link DataSet} to draw visible data into a {@link JFreeChart}.<br/>In add, this class
 * controls user interaction with the graph, like click on axes ...</p>
 * <p>This is the most important graphical class in SpeleoGraph.</p>
 *
 * @author Philippe VIENNE &lt;PhilippeGeek@gmail.com&gt;
 * @since 1.0
 */
@SuppressWarnings("FieldCanBeLocal")
public class GraphPanel extends JPanel implements DatasetChangeListener, ChartMouseListener {

    /**
     * Logger for debug and errors.
     */
    @NonNls
    private static final Logger log = LoggerFactory.getLogger(GraphPanel.class);

    /**
     * Instance of SpeleoGraph linked to this Graph.
     */
    private final SpeleoGraphApp application;
    /**
     * JFreeChart used by this instance.
     */
    private final JFreeChart chart;
    /**
     * Store the {@link XYPlot} linked to the current {@link #chart}.
     */
    private final XYPlot plot;
    /**
     * Used to save the dateAxis when no axis are shown.
     */
    private final DateAxis dateAxis;
    /**
     * The panel which encapsulate {@link #chart}. We use it to interact by {@link ChartMouseEvent} with the graph.
     */
    private final ChartPanel chartPanel;

    /**
     * Getter for the linked application instance
     * @return The instance
     */
    public SpeleoGraphApp getApplication(){return application;}

    /**
     * Construct a new GraphPanel for an application instance.
     * @param app The instance which should be linked with this Chart.
     */
    public GraphPanel(SpeleoGraphApp app){
        Validate.notNull(app);
        application=app;
        setLayout(new BorderLayout());
        chart = ChartFactory.createTimeSeriesChart(null,null,null,null,true,true,false);
        plot = chart.getXYPlot();
        dateAxis = (DateAxis) plot.getDomainAxis();
        chartPanel = new ChartPanel(chart,false,true,false,true,true);
        chartPanel.addChartMouseListener(this);
        setupEmptyChart();
        DataSet.addListener(this);
        add(chartPanel);
        log.info("GraphPanel is initialized");
    }

    /**
     * Configure the plot with no data.
     * We should hide axes and display a little message to explain that we have no data.
     */
    private void setupEmptyChart(){
        plot.setDomainAxis(null);
        plot.setRangeAxis(null);
        plot.setNoDataMessage(I18nSupport.translate("error.graphPanel.noData"));
    }

    /**
     * Method called when a DataSet has changed in the application.
     *
     * @param event information about the event.
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        for (int i = 0, max = plot.getDatasetCount(); i < max; i++) {
            plot.setDataset(i, null);
            plot.setRangeAxis(i, null);
            plot.setRenderer(i, null);
        }
        int axisIndex = 0;
        for (final DataSet set : DataSet.getInstances()) {
            if (set == null) continue;
            if (set.getSeriesCount() > 0) {
                boolean show = false;
                for (int j = 0; j < set.getSeriesCount(); j++) {
                    if (set.getItemCount(j) > 0) {
                        show = true;
                        break;
                    }
                }
                if (show) {
                    plot.setDomainAxis(dateAxis);
                    plot.setDataset(axisIndex, set);
                    plot.setRangeAxis(axisIndex, set.getValueAxis(), false);
                    plot.setRenderer(axisIndex, set.getRenderer(), false);
                    plot.mapDatasetToRangeAxis(axisIndex, axisIndex);
                    plot.setRangeAxisLocation(axisIndex, AxisLocation.BOTTOM_OR_LEFT);
                    plot.datasetChanged(new DatasetChangeEvent(this, set));
                    axisIndex++;
                }
            }
        }
        if (axisIndex == 0) {
            setupEmptyChart();
        }
    }

    /**
     * Callback method when click on a graph location.
     *
     * @param event information about the event.
     */
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        log.info("Received a mouse click");
    }

    /**
     * Callback method when a mouse move on the graph.
     *
     * @param event information about the event.
     */
    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        log.info("Received a mouse event");
    }
}
