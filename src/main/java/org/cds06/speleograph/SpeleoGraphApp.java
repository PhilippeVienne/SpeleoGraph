package org.cds06.speleograph;

import org.cds06.speleograph.data.DataSet;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.data.SpeleoFileReader;
import org.cds06.speleograph.utils.AbstractChangeArrayListObserver;
import org.cds06.speleograph.utils.ObservableArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
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

    {
        dataSets.registerObserver(new AbstractChangeArrayListObserver<DataSet>() {

            @Override
            public void onAdd(DataSet element) {
                super.onAdd(element);
                element.addChangeListener(new DatasetChangeListener() {
                    @Override
                    public void datasetChanged(DatasetChangeEvent event) {
                        if (event.getSource() instanceof Series) {
                            listModel.notifyElementsChanged((Series) event.getSource());
                        } else {
                            refreshChart();
                        }
                    }
                });
            }

            @Override
            public void onChange(ChangeType type) {
                refreshChart();
            }
        });
    }

    public void refreshChart() {
        for (int i = 0, max = plot.getDatasetCount(); i < max; i++) {
            plot.setDataset(i, null);
            plot.setRangeAxis(i, null);
            plot.setRenderer(i, null);
        }
        for (int i = 0, max = dataSets.size(); i < max; i++) {
            final DataSet set = dataSets.get(i);
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
                    plot.setDataset(i, set);
                    plot.setRangeAxis(i, set.getValueAxis(), false);
                    plot.setRenderer(i, set.getRenderer(), false);
                    plot.mapDatasetToRangeAxis(i, i);
                    plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);
                    plot.datasetChanged(new DatasetChangeEvent(SpeleoGraphApp.this, set));
                }
            }
        }
    }

    public SpeleoGraphApp() {
        super("SpeleoGraph");

        plot.setNoDataMessage("Aucune Donnée");
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
                log.debug("Start reading file " + file.getName());
                try {
                    listModel.add(SpeleoFileReader.readFile(file));
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                for (DataSet set : DataSet.getDataSetInstances()) {
                    if (!dataSets.contains(set)) {
                        dataSets.add(set);
                    }
                }
                log.debug("End reading file " + file.getName());
                openedFiles.add(openedFile);
                log.debug("End do all things on file " + file.getName());
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

        public void notifyElementsChanged(Series s) {
            final int i = delegate.indexOf(s);
            fireContentsChanged(s, i, i);
        }

        @Override
        public int getSize() {
            return delegate.size();
        }

        @Override
        public Series getElementAt(int index) {
            return delegate.get(index);
        }

        public void add(Series... e) {
            int index = delegate.size();
            for (int i = delegate.size(), max = i + e.length; i < max; i++) {
                delegate.add(i, e[i]);
            }
            fireIntervalAdded(this, index, index + e.length - 1);
        }

    }
}
