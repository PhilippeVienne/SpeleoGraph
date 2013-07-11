package org.cds06.speleograph;

import org.cds06.speleograph.data.DataSet;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.data.SpeleoFileReader;
import org.cds06.speleograph.utils.ObservableArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
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

    public SpeleoSeriesListModel listModel = new SpeleoSeriesListModel();

    private CheckBoxList list = new CheckBoxList(listModel);

    private JScrollPane scrollPane = new JScrollPane(list);

    {
        panel.add(scrollPane, BorderLayout.EAST);
    }

    private final JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null, null, null, true, true, false);
    private final XYPlot plot = chart.getXYPlot();
    private final ObservableArrayList<DataSet> dataSets = new ObservableArrayList<>();

    public SpeleoGraphApp() {
        super("SpeleoGraph");

        panel.add(new ChartPanel(chart), BorderLayout.CENTER);

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
                        org.cds06.speleograph.data.DataSet.pushSeries(SpeleoFileReader.readFile(file));

                        log.debug("End reading file " + file.getName());
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

    private class SpeleoSeriesListModel extends AbstractListModel<Series> {

        private static final long serialVersionUID = 1L;

        private ArrayList<Series> delegate = new ArrayList<>();

        @Override
        public int getSize() {
            return delegate.size();
        }

        @Override
        public Series getElementAt(int index) {
            return delegate.get(index);
        }

        public void add(Series e) {
            int index = delegate.size();
            delegate.add(e);
            fireIntervalAdded(this, index, index);
        }

    }
}
