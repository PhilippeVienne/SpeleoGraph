package org.cds06.speleograph;

import org.cds06.speleograph.data.DataSet;
import org.cds06.speleograph.data.ImportTable;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.data.SpeleoFileReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
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
import java.util.Collection;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class SpeleoGraphApp extends JFrame implements DatasetChangeListener {

    //private ArrayList<File> openedFiles = new ArrayList<>();
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
    private final DateAxis dateAxis = (DateAxis) plot.getDomainAxis();

    {
        DataSet.addListener(this);
    }

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
                    plot.datasetChanged(new DatasetChangeEvent(SpeleoGraphApp.this, set));
                    axisIndex++;
                }
            }
        }
        if (axisIndex == 0) {
            plot.setDomainAxis(null);
        }
    }

    private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new ChartPanel(chart), scrollPane);

    {
        splitPane.setOneTouchExpandable(true);

//Provide minimum sizes for the two components in the split pane
//        Dimension minimumSize = new Dimension(100, 50);
//        splitPane.getLeftComponent().setMinimumSize(minimumSize);
//        splitPane.getRightComponent().setMinimumSize(minimumSize);
        splitPane.setDividerLocation(600);
    }

    public SpeleoGraphApp() {
        super("SpeleoGraph");

        plot.setNoDataMessage("Aucune Donnée");
        panel.add(splitPane, BorderLayout.CENTER);

        setContentPane(panel);
        setSize(800, 500);
        splitPane.setResizeWeight(1.0);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void openFile(SpeleoFileReader.HeaderInformation information, File file) {
        try {
            SpeleoFileReader.read(file, information);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(panel, "Impossible d'ouvrir le fichier", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String... args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { // NON-NLS
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e2) {
                System.out.println("Leave default Java LookAndFeel"); // NON-NLS
            }
        }

        new SpeleoGraphApp().setVisible(true);
    }

    ////////////////////////////////////////////////////////////////
    //////////          ACTIONS
    ////////////////////////////////////////////////////////////////

    private class OpenSpeleoGraphFileAction extends AbstractAction {

        public OpenSpeleoGraphFileAction() {
            super("Importer des données");
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
//                if (openedFiles.contains(file)) {
//                    if (JOptionPane.showConfirmDialog(
//                            SpeleoGraphApp.this,
//                            "Ce fichier a déjà été ouvert, êtes vous sur de vouloir le recharger ? (il peut y avoir" +
//                                    " des doublons)",
//                            "Question",
//                            JOptionPane.OK_CANCEL_OPTION,
//                            JOptionPane.QUESTION_MESSAGE
//                    ) != JOptionPane.OK_OPTION)
//                        return;
//                }
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
                    ImportTable.openImportWizardFor(SpeleoGraphApp.this, file);
                } catch (IOException e) {
                    LoggerFactory.getLogger(SpeleoGraphApp.class).error("Erreur lors de la lecture d'un fichier", e);
                }
//                try {
//                    SpeleoFileReader.readFile(file);
//                } catch (IOException | ParseException e) {
//                    JOptionPane.showInternalInputDialog(SpeleoGraphApp.this, "Impossible d'ouvrir le fichier", "Erreur", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }
//                log.debug("End reading file " + file.getName());
//                openedFiles.add(openedFile);
//                log.debug("End do all things on file " + file.getName());
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

    private class SpeleoSeriesListModel extends AbstractListModel<Series> implements DatasetChangeListener {

        private static final long serialVersionUID = 1L;

        private Collection<DataSet> sets = DataSet.getInstances();

        {
            DataSet.addListener(this);
        }

        public int indexOf(Series s) {
            int i = 0;
            for (DataSet set : sets) {
                for (Series ser : set.getSeries()) {
                    if (ser == s) return i;
                    i++;
                }
            }
            return 0;
        }

        @Override
        public int getSize() {
            int size = 0;
            for (DataSet set : sets) {
                size += set.getSeries().size();
            }
            return size;
        }

        @Override
        public Series getElementAt(int index) {
            int i = 0;
            for (DataSet set : sets) {
                for (Series s : set.getSeries()) {
                    if (index == i) return s;
                    i++;
                }
            }
            throw new IndexOutOfBoundsException("Can not find your index");
        }

        /**
         * Receives notification of an dataset change event.
         *
         * @param event information about the event.
         */
        @Override
        public void datasetChanged(DatasetChangeEvent event) {
            fireContentsChanged(this, 0, getSize() - 1);
        }
    }
}
