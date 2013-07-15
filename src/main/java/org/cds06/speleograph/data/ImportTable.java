package org.cds06.speleograph.data;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.Validate;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Table to managing file imports into SpeleoGraph.
 * <p>This table show to the user the first ten lines from the file. If only one column is detected we ask if the column
 * separator is not another thing than ';'. After on the each column header on the table, the user can choose what is
 * the data in the current column. In the end, this class call the {@link SpeleoFileReader} to end file reading and push
 * the series into the DataSets which are automatically bidden with the list and the graph.</p>
 */
public class ImportTable extends JTable{

    private static final char DEFAULT_SEPARATOR = ';';
    private final TableModel model = new AbstractTableModel() {
        @Override
        public int getRowCount() {
            return lines.length;
        }

        @Override
        public int getColumnCount() {
            return numberOfColumns;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex < lines[rowIndex].length) {
                return lines[rowIndex][columnIndex];
            } else {
                return null;
            }
        }

        @Override
        public String getColumnName(int column) {
            return Integer.toString(column);
        }
    };
    /**
     * Number of read columns in the file.
     */
    private int numberOfColumns = 1;
    /**
     * The file used as source of data.
     */
    private File sourceFile;

    /**
     * The first ten lines of data from the file.
     */
    private String[][] lines = new String[10][];

    private final SpeleoFileReader.HeaderInformation headerInformation = new SpeleoFileReader.HeaderInformation();
    private final SpeleoFileReader.DateInformation dateInformation = new SpeleoFileReader.DateInformation();

    public ImportTable(File sourceFile, char columnSeparator) throws IOException {
        super();
        Validate.notNull(sourceFile, "Can not use a null file.");
        Validate.notNull(columnSeparator, "Column separator should be set");
        headerInformation.setColumnSeparator(columnSeparator);
        this.sourceFile = sourceFile;
        CSVReader reader = new CSVReader(new FileReader(sourceFile), headerInformation.getColumnSeparator());
        for (int i = 0; i < 10; i++) {
            lines[i] = reader.readNext();
            if (lines[i] != null) {
                numberOfColumns = lines[i].length;
            }
        }
        if (numberOfColumns <= 1) {
            throw new OnlyOneDataColumn();
        }
        setModel(this.model);
        getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final JDialog menu = new JDialog((Dialog) ((JViewport) e.getComponent().getParent()).getRootPane().getParent());
                menu.setUndecorated(true);
                menu.setModal(true);
                final EditPanel editPanel = new EditPanel();
                menu.add(editPanel);
                menu.setLocation(e.getPoint());
                menu.setVisible(true);
            }
        });
    }

    public static void openImportWizardFor(Frame parentFrame, File file) throws IOException {
        JDialog frame = new JDialog(parentFrame, true);
        boolean stopWhile = false;
        char separator = DEFAULT_SEPARATOR;
        ImportTable table = null;
        while (!stopWhile) {
            try {
                table = new ImportTable(file, separator);
                stopWhile = true;
            } catch (OnlyOneDataColumn e) {
                Object r = JOptionPane.showInputDialog(parentFrame, "Le fichier n'admet probablement pas le séparateur habituel ';', quel est le séparateur utiliser ?", "Erreur", JOptionPane.ERROR_MESSAGE, null, new Object[]{';', ',', '.', ':', '\t', "Annuler la lecture"}, "Annuler la lecture");
                if (r instanceof Character) {
                    separator = (char) r;
                    stopWhile = false;
                } else {
                    return;
                }
            }
        }
        final JPanel jPanel = new JPanel(new BorderLayout(5, 5));
        jPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setContentPane(jPanel);
        frame.setMinimumSize(new Dimension(480, 270));
        frame.setMaximumSize(new Dimension(800, 450));
        frame.setVisible(true);
    }

    private static class OnlyOneDataColumn extends IllegalArgumentException {

        public OnlyOneDataColumn() {
            super("This file has only one column of data, probably an error");
        }

    }

    private class EditPanel extends JPanel {

        private final JComboBox<String> box = new JComboBox<>(new String[]{"Date ou/et heure", "Mesure d'un instrument", "Ignorer cette colonne"});

        public EditPanel() {
            super();
            setMinimumSize(new Dimension(150, 200));
            setBorder(new TitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), "Editer la colonne"));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(box);
        }

        public void startEditColumn(int index) {
            this.removeAll();
            if (headerInformation.hasSeriesForColumn(index)) {

            } else if (dateInformation.hasDateInformationForColumn(index)) {

            } else {
                add(box);
            }
        }

    }
}
