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

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.Validate;
import org.cds06.speleograph.utils.InputWithHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Table to managing file imports into SpeleoGraph.
 * <p>This table show to the user the first ten lines from the file. If only one column is detected we ask if the column
 * separator is not another thing than ';'. After on the each column header on the table, the user can choose what is
 * the data in the current column. In the end, this class call the {@link SpeleoFileReader} to end file reading and push
 * the series into the DataSets which are automatically bidden with the list and the graph.</p>
 */
public class ImportTable extends JPanel {

    private static final char DEFAULT_SEPARATOR = ';';
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("org.cds06.speleograph.Messages"); // NON-NLS
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

    private JTable table = new JTable();

    private EditPanel editPanel = new EditPanel();

    /**
     * Number of read columns in the file.
     */
    private int numberOfColumns = 1;
    /**
     * The file used as source of data.
     */
    private final File sourceFile;

    /**
     * The first ten lines of data from the file.
     */
    private String[][] lines = new String[10][];

    private final SpeleoFileReader.HeaderInformation headerInformation = new SpeleoFileReader.HeaderInformation();
    private final SpeleoFileReader.DateInformation dateInformation = new SpeleoFileReader.DateInformation();
    private static final Logger log = LoggerFactory.getLogger(ImportTable.class);

    public ImportTable(final File sourceFile, char columnSeparator) throws IOException {
        super();
        Validate.notNull(sourceFile, "Can not use a null file."); // NON-NLS
        Validate.notNull(columnSeparator, "Column separator should be set"); // NON-NLS
        setLayout(new BorderLayout());
        headerInformation.setDateInformation(dateInformation);
        headerInformation.setColumnSeparator(columnSeparator);
        this.sourceFile = sourceFile;
        CSVReader reader = new CSVReader(new FileReader(sourceFile), headerInformation.getColumnSeparator());
        headerInformation.setFirstLineOfData(1);
        for (int i = 0; i < 10; i++) {
            lines[i] = reader.readNext();
            if (lines[i] != null) {
                numberOfColumns = lines[i].length;
                if (numberOfColumns == 1) {
                    headerInformation.setFirstLineOfData(i + 2);
                }
            }
        }
        if (numberOfColumns <= 1) {
            throw new OnlyOneDataColumn();
        }
        table.setModel(model);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(editPanel, BorderLayout.EAST);
        JButton read = new JButton("Lire");
        read.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = (JDialog) SwingUtilities.windowForComponent(ImportTable.this);
                dialog.setVisible(false);
                try {
                    SpeleoFileReader.read(sourceFile, headerInformation);
                } catch (Exception e1) {
                    showError("Impossible de lire le fichier");
                    log.error("Read file error:", e1); // NON-NLS
                }
            }
        });
        add(read, BorderLayout.SOUTH);
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseClicked(final MouseEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int col = table.columnAtPoint(e.getPoint());
                        col = Integer.valueOf(table.getColumnName(col));
                        if (col == -1) return;
                        else if (editPanel.getEditedColumn() == col) return;
                        editPanel.setEditedColumn(col);
                        System.out.println("Edit: " + col); // NON-NLS
                    }
                });
            }
        });
    }

    public static void openImportWizardFor(JComponent parentFrame, File file) throws IOException {
        JOptionPane.showMessageDialog(parentFrame,
                "Cette fonctionnalité n'est pas disponible",
                "Erreur", JOptionPane.ERROR_MESSAGE);
//        JDialog frame = new JDialog((Frame) SwingUtilities.windowForComponent(parentFrame), true);
//        boolean stopWhile = false;
//        char separator = DEFAULT_SEPARATOR;
//        ImportTable table = null;
//        while (!stopWhile) {
//            try {
//                table = new ImportTable(file, separator);
//                stopWhile = true;
//            } catch (OnlyOneDataColumn e) {
//                Object r = JOptionPane.showInputDialog(parentFrame, resourceBundle.getString("import.error.separator.message"), "Erreur", JOptionPane.ERROR_MESSAGE, null, new Object[]{';', ',', '.', ':', '\t', "Annuler la lecture"}, "Annuler la lecture");
//                if (r instanceof Character) {
//                    separator = (char) r;
//                    stopWhile = false;
//                } else {
//                    return;
//                }
//            }
//        }
//        final JPanel jPanel = new JPanel(new BorderLayout(5, 5));
//        jPanel.add(table, BorderLayout.CENTER);
//        frame.setContentPane(table);
//        frame.setMinimumSize(new Dimension(480, 270));
//        frame.setPreferredSize(new Dimension(500, 380));
//        frame.setMaximumSize(new Dimension(800, 450));
//        frame.setLocationRelativeTo(parentFrame);
//        frame.setLocation(parentFrame.getWidth() / 2 - frame.getWidth() / 2, parentFrame.getHeight() / 2 - frame.getHeight() / 2);
//        frame.setVisible(true);
    }

    private static class OnlyOneDataColumn extends IllegalArgumentException {

        public OnlyOneDataColumn() {
            super("This file has only one column of data, probably an error");
        }

    }

    /**
     * Show an error on the user screen.
     *
     * @param message The message to display to the user
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private class EditPanel extends JPanel {


        private final String DATE_COLUMN = "Date ou/et heure";
        private final String MEASURE_COLUMN = "Mesure d'un instrument";
        private final String IGNORE_COLUMN = "Ignorer la colonne";
        private final Dimension inputMaxDimensions = new Dimension(Integer.MAX_VALUE, 25);

        private final JComboBox<String> box = new JComboBox<>(new String[]{DATE_COLUMN, MEASURE_COLUMN, IGNORE_COLUMN});
        private int editedColumn = -1;

        private JPanel editPanel = new JPanel(new CardLayout());
        private JPanel ignoreColumnPanel = new JPanel();
        private JPanel dateColumnPanel = new JPanel();
        private JPanel measureColumnPanel = new JPanel();

        {
            dateColumnPanel.setLayout(new BoxLayout(dateColumnPanel, BoxLayout.Y_AXIS));
            measureColumnPanel.setLayout(new BoxLayout(measureColumnPanel, BoxLayout.Y_AXIS));
            editPanel.setBackground(Color.BLUE);
            editPanel.add(ignoreColumnPanel, IGNORE_COLUMN);
            editPanel.add(dateColumnPanel, DATE_COLUMN);
            editPanel.add(measureColumnPanel, MEASURE_COLUMN);
        }

        private void showPane(String columnKey) {
            ((CardLayout) editPanel.getLayout()).show(editPanel, columnKey);
        }

        private JTextField dateFormatField = new JTextField();

        {
            dateColumnPanel.setLayout(new BoxLayout(dateColumnPanel, BoxLayout.Y_AXIS));
            dateColumnPanel.add(new JLabel("Entrer un format de date :"));
            dateColumnPanel.add(Box.createVerticalStrut(5));
            dateFormatField.setMaximumSize(inputMaxDimensions);
            dateColumnPanel.add(dateFormatField);
            dateColumnPanel.add(Box.createVerticalStrut(40));
        }

        private JComboBox<Object> typeSelection = new JComboBox<>(new Object[0]);
        //        private JTextField typeNameField = new JTextField();
//        private JTextField typeUnitField = new JTextField();
//        private JCheckBox isMinMaxSeries = new JCheckBox(resourceBundle.getString("input.hasMinMaxValues"));
        private JTextField maxColumnField = new JTextField();
        private JTextField minColumnField = new JTextField();

        private JPanel minMaxPromptPanel = new JPanel();

        {
            minMaxPromptPanel.setLayout(new BoxLayout(minMaxPromptPanel, BoxLayout.Y_AXIS));
            minMaxPromptPanel.add(new JLabel(resourceBundle.getString("input.minColumn")));
            minMaxPromptPanel.add(Box.createVerticalStrut(5));
            minMaxPromptPanel.add(minColumnField);
            minMaxPromptPanel.add(Box.createVerticalStrut(5));
            minMaxPromptPanel.add(new JLabel(resourceBundle.getString("input.maxColumn")));
            minMaxPromptPanel.add(Box.createVerticalStrut(5));
            minMaxPromptPanel.add(maxColumnField);
        }

        {
            typeSelection.setMaximumSize(inputMaxDimensions);
            measureColumnPanel.add(new InputWithHelp(typeSelection, "Quel est le type de donnée ?"));
            typeSelection.addItem("Selectionnez un type de donné");
            for (Type t : Type.getInstances()) typeSelection.addItem(t);
            typeSelection.setSelectedIndex(0);
//            typeSelection.addItemListener(new ItemListener() {
//                @Override
//                public void itemStateChanged(ItemEvent e) {
//                    if (e.getStateChange() == ItemEvent.SELECTED) {
//                        if (e.getItem() instanceof Type) {
////                            Type t = (Type) e.getItem();
////                            if (t.isHighLowType()) minMaxPromptPanel.setVisible(true);
//                        } else {
//                            minMaxPromptPanel.setVisible(false);
//                        }
//                    }
//                }
//            });
            measureColumnPanel.add(minMaxPromptPanel);
            minMaxPromptPanel.setVisible(false);
        }

        private JButton validateButton = new JButton(resourceBundle.getString("valid"));

        {
            validateButton.setMaximumSize(inputMaxDimensions);
            validateButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    validateDataFor(editedColumn);
                }
            });
        }


        public EditPanel() {
            super();
            setMinimumSize(new Dimension(150, 200));
            setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Editer la colonne"));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setVisible(true);
            box.setMaximumSize(inputMaxDimensions);
            box.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(final ItemEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (e.getStateChange() == ItemEvent.SELECTED)
                                switch ((String) box.getSelectedItem()) {
                                    case DATE_COLUMN:
                                        showPane(DATE_COLUMN);
                                        break;
                                    case MEASURE_COLUMN:
                                        showPane(MEASURE_COLUMN);
                                        break;
                                    case IGNORE_COLUMN:
                                    default:
                                        showPane(IGNORE_COLUMN);
                                }
                        }
                    });
                }
            });
            box.setSelectedItem(IGNORE_COLUMN);
            box.setEnabled(false);
            add(box);
            add(Box.createVerticalStrut(5));
            add(editPanel);
            add(Box.createVerticalGlue());
            add(validateButton);
            showPane(IGNORE_COLUMN);
            validate();
        }

        private void validateDataFor(int index) {
//            switch (box.getSelectedIndex()) {
//                case 0:
//                    String format = dateFormatField.getText();
//                    try {
//                        new SimpleDateFormat(format).format(Calendar.getInstance().getTime());
//                        dateInformation.set(index, format);
//                        if (headerInformation.hasSeriesForColumn(index)) {
//                            headerInformation.remove(index);
//                        }
//                    } catch (IllegalArgumentException e) {
//                        showError("Le format de la date n'est pas valable");
//                    }
//                    break;
//                case 1:
//                    if (typeSelection.getSelectedItem() instanceof Type) {
//                        Type t = (Type) typeSelection.getSelectedItem();
//                        if (t.isHighLowType()) {
//                            try {
//                                Validate.notBlank(minColumnField.getText(), "Pas de numéro pour la valeur minimal");
//                                Validate.notBlank(minColumnField.getText(), "Pas de numéro pour la valeur maximal");
//                                Integer[] columns = {Integer.parseInt(minColumnField.getText()), Integer.parseInt(maxColumnField.getText())};
//                                for (int col : columns) {
//                                    Validate.notNull(col, "Il manque un numéro de colonne");
//                                    Validate.inclusiveBetween(0, numberOfColumns, col, "Le numéro de colonne n'est pas valable");
//                                }
//                                Series series = new Series(sourceFile, t);
//                                headerInformation.set(series, columns);
//                            } catch (NumberFormatException e) {
//                                showError("Merci de bien saisir des nombres");
//                                log.error(null, e);
//                            } catch (IllegalArgumentException | NullPointerException e) {
//                                showError(e.getMessage());
//                                log.error("Runtime error:", e); // NON-NLS
//                            }
//                        } else {
//                            Series series = new Series(sourceFile, t);
//                            headerInformation.set(series, editedColumn);
//                        }
//                    } else {
//                        JOptionPane.showMessageDialog(this, "No support operation !"); //NON-NLS
//                    }
//                    break;
//                default:
//            }
        }

        public void startEditColumn(final int index) {
            if (!isVisible()) setVisible(true);
            if (!box.isEnabled()) box.setEnabled(true);
            Border border = getBorder();
            if (border instanceof TitledBorder) {
                ((TitledBorder) border).setTitle("Edition de la colonne " + index);
            }
            resetDataInput();
            if (dateInformation.hasDateInformationForColumn(index)) {
                showPane(DATE_COLUMN);
                box.setSelectedItem(DATE_COLUMN);
                String format = dateInformation.getForColumn(index);
                dateFormatField.setText(format);
            } else if (headerInformation.hasSeriesForColumn(index)) {
                showPane(MEASURE_COLUMN);
                try {
                    Series series = headerInformation.getSeriesForColumn(index);
                    typeSelection.setSelectedItem(series == null ? Type.UNKNOWN : series.getType());
                } catch (Exception e) {
                    log.error("Error when try to retrive existing type", e); // NON-NLS
                }
                box.setSelectedItem(MEASURE_COLUMN);
            } else {
                showPane(IGNORE_COLUMN);
                box.setSelectedItem(IGNORE_COLUMN);
            }
            invalidate();
            validate();
            repaint();
            setVisible(true);
        }

        private void resetDataInput() {
            dateFormatField.setText(null);
            typeSelection.setSelectedItem(Type.UNKNOWN);
        }

        private int getEditedColumn() {
            return editedColumn;
        }

        private void setEditedColumn(int editedColumn) {
            startEditColumn(editedColumn);
            this.editedColumn = editedColumn;
        }

    }
}
