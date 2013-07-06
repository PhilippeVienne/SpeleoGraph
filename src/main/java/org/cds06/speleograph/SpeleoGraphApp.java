package org.cds06.speleograph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class SpeleoGraphApp extends JFrame {

    private ArrayList<File> openedFiles = new ArrayList<>();
    private static Logger log = LoggerFactory.getLogger(SpeleoGraphApp.class);

    private JPanel panel = new JPanel(new BorderLayout(2, 2));
    private JToolBar toolBar = new JToolBar();

    {
        panel.add(toolBar, BorderLayout.NORTH);
        addToolBarButtons();
    }

    private void addToolBarButtons() {
        toolBar.add(new OpenSpeleoGraphFileAction());
    }

    public SpeleoDataListModel listModel = new SpeleoDataListModel();

    private CheckBoxList list = new CheckBoxList(listModel);

    private JScrollPane scrollPane = new JScrollPane(list);

    {
        panel.add(scrollPane, BorderLayout.EAST);
    }

    JFreeChart lineChartObject;

    public SpeleoGraphApp() {
        super("SpeleoGraph");
        /* Step - 1: Define the data for the line chart  */
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        line_chart_dataset.addValue(15, "schools", "1970");
        line_chart_dataset.addValue(30, "schools", "1980");
        line_chart_dataset.addValue(60, "schools", "1990");
        line_chart_dataset.addValue(120, "schools", "2000");
        line_chart_dataset.addValue(240, "schools", "2010");

                /* Step -2:Define the JFreeChart object to create line chart */
        lineChartObject = ChartFactory.createTimeSeriesChart(null, null, null, null, true, true, false);


        ChartPanel chartPanel = new ChartPanel(lineChartObject);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        setContentPane(chartPanel);

        panel.add(chartPanel, BorderLayout.CENTER);
        setContentPane(panel);
        setSize(600, 500);
    }

    public static void main(String... args) {
        new SpeleoGraphApp().setVisible(true);
    }

    ////////////////////////////////////////////////////////////////
    //////////          ACTIONS
    ////////////////////////////////////////////////////////////////

    private class OpenSpeleoGraphFileAction extends AbstractAction {

        public OpenSpeleoGraphFileAction() {
            super("Ouvrir un fichier");
        }

        private File debugFolder = new File("C:\\Users\\PhilippeGeek\\Dropbox\\CDS06 Comm Scientifique\\Releves-Instruments\\");

        /**
         * Invoked when an action occurs.
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JFileChooser fileChooser = new JFileChooser(debugFolder);
            fileChooser.setFileFilter(new SpeleoGraphFileFilter());
            int result = fileChooser.showOpenDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    File openedFile = file;

                    if (openedFiles.contains(file)) return;

                    if (ReefnetFileConverter.isReefnetFile(file)) { // We must convert it before using
                        try {
                            ReefnetFileConverter converter = new ReefnetFileConverter(file);
                            converter.convert();
                            file = converter.getCsvTempFile();
                        } catch (IOException e) {
                            log.error("Can not convert the ReefNet file, we stop the action.", e);
                            return;
                        }
                    }

                    try {
                        log.debug("Start reading file " + file.getName());
                        DataSetReader reader = new DataSetReader(file);
                        log.debug("End reading file " + file.getName());
                        for (DataSet set : reader.getDataSets().values()) {
                            listModel.add(set);
                        }
                        log.debug("End add all sets" + file.getName());
                        openedFiles.add(openedFile);
                        log.debug("End do all things on file " + file.getName());
                    } catch (Exception e) {
                        log.error("Can not open file " + file.toString(), e);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace(System.err);
                }
            }
        }

        private class SpeleoGraphFileFilter extends FileFilter {
            /**
             * Whether the given file is accepted by this filter.
             */
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".txt") || f.getName().endsWith(".csv") || f.isDirectory();
            }

            /**
             * The description of this filter. For example: "JPG and GIF Images"
             *
             * @see javax.swing.filechooser.FileView#getName
             */
            @Override
            public String getDescription() {
                return "SpeleoGraph File (.cvs,.txt)";
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    //////////          SUB-CLASS
    ////////////////////////////////////////////////////////////////

    private class SpeleoDataListModel extends AbstractListModel<DataSet> {

        private static final long serialVersionUID = 1L;

        private ArrayList<DataSet> delegate = new ArrayList<>();

        @Override
        public int getSize() {
            return delegate.size();
        }

        @Override
        public DataSet getElementAt(int index) {
            return delegate.get(index);
        }

        public void add(DataSet e) {
            int index = delegate.size();
            delegate.add(e);
            fireIntervalAdded(this, index, index);
        }

    }
}
