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

package org.cds06.speleograph.data.fileio;

import au.com.bytecode.opencsv.CSVReader;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.graph.SetTypePanel;
import org.cds06.speleograph.utils.FormDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class ImportWizard {

    private final File file;
    private char separatorChar = ';';

    public ImportWizard(File file) {
        super();
        this.file = file;
    }

    private class SeparatorDialog extends FormDialog {

        private final JComboBox<String> separator = new JComboBox<>(new String[]{",", ";", "(tabulation)"}); // NON-NLS

        public SeparatorDialog() {
            super();
            construct();
        }

        /**
         * This function add component to the main panel.
         * <p>You have to override this function to add and setup your dialog</p>
         */
        @Override
        protected void setup() {
            PanelBuilder builder = new PanelBuilder((FormLayout) getPanel().getLayout(), getPanel());

            builder.addLabel("Séparateur :");
            builder.nextColumn(2);
            builder.add(separator);
            builder.nextLine();
            builder.add(new JButton(new AbstractAction() {
                {
                    putValue(NAME, "Lire le fichier");
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    validateForm();
                }
            }), "1,2,3,1");

        }

        @Override
        protected void validateForm() {
            switch ((String) separator.getSelectedItem()) {
                case ",":
                    separatorChar = ',';
                    break;
                case "(tabulation)": // NON-NLS
                    separatorChar = '\t';
                    break;
                case ";":
                default:
                    separatorChar = ';';
                    break;
            }
            openImportTable();
        }

        @Override
        protected FormLayout getFormLayout() {
            return new FormLayout("r:p,4dlu,p:grow", "p,p"); // NON-NLS
        }

    }

    private SeparatorDialog separatorDialog = new SeparatorDialog();

    public void openWizard() {
        separatorDialog.setVisible(true);
    }

    /**
     * The ten first lines of data read from the file.
     */
    private String[][] data = new String[50][];

    /**
     * All lines read from the file.
     */
    private List<String[]> lines;

    private ImportDialog importDialog;

    private void openImportTable() {
        try {
            CSVReader reader = new CSVReader(new FileReader(file), separatorChar, '"');
            lines = reader.readAll();
            List<String[]> subList = lines.subList(0, 50);
            int columns = 0;
            for (int i = 0; i < subList.size(); i++) {
                String[] s = subList.get(i);
                data[i] = s;
                columns = columns < s.length ? s.length : columns;
            }
            separatorDialog.setVisible(false);
            importDialog = new ImportDialog(data, columns);
            importDialog.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showInternalMessageDialog(separatorDialog,
                    "Impossible de lire le fichier " + file + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ImportDialog extends FormDialog {

        private JPanel leftPanel = new JPanel(new FormLayout("p:grow,p", "p:grow,p,p,p,p,p,p"));
        private JTable table = new JTable();

        private ArrayList<ColumnEditor> editors = new ArrayList<>();
        private ColumnEditor currentShownEditor = null;
        private JButton readButton;
        private Integer firstLineOfData = 0;
        private int columns;

        /**
         * @param lines The first lines to display.
         * @param columns The number of columns to display.
         */
        public ImportDialog(final String[][] lines, final int columns) {
            super();
            this.columns = columns;
            for (int i = 0; i < columns; i++) {
                editors.add(i, new ColumnEditor());
            }
            table = new JTable(new DefaultTableModel() {
                @Override
                public String getColumnName(int column) {
                    if (column == 0) {
                        return "Ligne";
                    }
                    column--;
                    return Integer.toString(column);
                }

                @Override
                public Object getValueAt(int row, int column) {
                    if (column == 0) {
                        return Integer.toString(row + 1);
                    }
                    column--;
                    if (row < lines.length && lines[row] != null)
                        if (column < lines[row].length)
                            return lines[row][column];
                    return "";
                }

                @Override
                public int getRowCount() {
                    return lines.length;
                }

                @Override
                public int getColumnCount() {
                    return columns + 1;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            final CellConstraints cc = new CellConstraints(5, 1);
            table.getTableHeader().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    final int columnIndex = table.getTableHeader().columnAtPoint(e.getPoint()) - 1;
                    if (columnIndex < 0 || columnIndex > columns) return;
                    if (e.getClickCount() != 2) return;
                    if (currentShownEditor != null) {
                        getPanel().remove(currentShownEditor);
                    }
                    currentShownEditor = editors.get(columnIndex);
                    getPanel().add(currentShownEditor, cc);
                    pack();
                }
            });
            construct();
//            centerOnScreen();
        }

        @Override
        protected void setup() {

            {   // Construct the left panel
                PanelBuilder builder = new PanelBuilder((FormLayout) leftPanel.getLayout(), leftPanel);
                builder.add(
                        new JLabel("<HTML><h1>Importer des données</h1><p>Vous pouvez soit désigner une ligne " +
                                "d'en-tête dans votre fichier et les séries seront lu selon les colonnes, soit " +
                                "cliquer sur les colonnes pour définir ce qu'elles contiennent.</p></HTML>"),
                        "1,1,2,1");
                builder.addLabel("Ligne d'en-tête :", "1,2,2,1");
                final JTextField headerLineField = new JTextField("1");
                builder.add(headerLineField, "1,3");
//                builder.add(new JButton(new AbstractAction() {
//
//                    {
//                        putValue(NAME, "Ok");
//                    }
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//
//                    }
//                }), "2,3");
                builder.addLabel("Première ligne des données :", "1,4,2,1");
                final JTextField dataLineField = new JTextField("2");
                builder.add(dataLineField, "1,5");
                final JButton ok = new JButton("Ok");
                ok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            firstLineOfData = Integer.valueOf(dataLineField.getText()) - 1;
                            if (readButton != null) {
                                readButton.setEnabled(true);
                            }
                            ok.setBackground(new Color(118, 238, 0));
                        } catch (NumberFormatException e1) {
                            JOptionPane.showMessageDialog(
                                    ImportDialog.this,
                                    dataLineField.getText() + " n'est pas un nombre !",
                                    "Erreur",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                });
                CellConstraints cc = new CellConstraints();
                cc.xyw(1,7,2);
                builder.add(ok, cc);
            }

            PanelBuilder builder = new PanelBuilder((FormLayout) getPanel().getLayout(), getPanel());

            builder.add(leftPanel);
            builder.nextColumn(2);
            builder.add(new JScrollPane(table));

            builder.nextLine(2);
            readButton = new JButton(new AbstractAction() {
                {
                    putValue(NAME, "Lancer la lecture");
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    validateForm();
                }
            });
            readButton.setEnabled(false);
            builder.add(readButton, "1,2,5,1");

        }

        @Override
        protected void validateForm() {
            if (firstLineOfData == null) {
                JOptionPane.showMessageDialog(this,
                        "On ne peut pas continuer sans savoir où les données commencent.",
                        "Ereur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            SpeleoFileReader.HeaderInformation headerInformation = new SpeleoFileReader.HeaderInformation();
            SpeleoFileReader.DateInformation dateInformation = new SpeleoFileReader.DateInformation();
            headerInformation.setDateInformation(dateInformation);
            for (ColumnEditor editor : editors) {
                if (editor.isIgnoring()) continue;
                if (editor.isDateColumn()) {
                    dateInformation.set(editors.indexOf(editor), editor.getDateFormat());
                    continue;
                }
                if (editor.isSeriesColumn()) {
                    if (editor.isMinMax()) {
                        Series s = new Series(file, editor.getType());
                        s.setMinMax(true);
                        headerInformation.set(s, editor.getLinkedColumns());
                    } else {
                        headerInformation.set(new Series(file, editor.getType()), editors.indexOf(editor));
                    }
                }
            }
            readFile(headerInformation, firstLineOfData);
            setVisible(false);
        }

        @Override
        protected FormLayout getFormLayout() {
            return new FormLayout("150dlu,3dlu,p:grow,3dlu,p", "top:p:grow,p"); // NON-NLS
        }

        private class ColumnEditor extends JPanel implements ItemListener {

            private static final String IGNORE = "Ignorer la colonne";
            private static final String DATE = "Date et/ou Heure";
            private static final String SERIES = "Série de données";

            private JComboBox<String> columnTypeComboBox = new JComboBox<>(new String[]{
                    IGNORE, DATE, SERIES
            });

            private JPanel datePanel = new JPanel(new FormLayout("p:grow", "p,p,p:grow"));
            private JPanel series = new JPanel(new FormLayout("p:grow", "p,p,p:grow"));
            private JPanel empty = new JPanel();
            private JTextField dateFormatField = new JTextField("dd/MM/yyyy HH:mm:ss");

            {
                PanelBuilder builder = new PanelBuilder(new FormLayout("p:grow", "p,p,p:grow"), datePanel);
                builder.addLabel("Format de la date :");
                builder.nextLine();
                builder.add(dateFormatField);
                builder.nextLine();
                builder.add(new JLabel("<HTML>" +
                        "d : Jour dans le mois (1 à 31)<br/>" +
                        "M : Mois dans l'année (1 à 12)<br/>" +
                        "y : Année<br/>" +
                        "H : Heure sur 24 heures<br/>" +
                        "m : minutes<br/>" +
                        "s : secondes" +
                        "</HTML>"));
            }

            private SetTypePanel typePropertyPanel = new SetTypePanel();

            {
                typePropertyPanel.setVisible(true);
                typePropertyPanel.setBorder(
                        BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Type"));
            }

            private JCheckBox isMinMax = new JCheckBox("A une valeur minimale et/ou maximale");

            private JSpinner minColumnSpinner = new JSpinner(new SpinnerNumberModel(0, 0, columns, 1));
            private JSpinner maxColumnSpinner = new JSpinner(new SpinnerNumberModel(0, 0, columns, 1));
            private JPanel minMaxPropertyPanel;

            {
                PanelBuilder builder = new PanelBuilder(new FormLayout("r:p,p:grow", "p,p"));
                builder.addLabel("Minimal :");
                builder.nextColumn();
                builder.add(minColumnSpinner);
                builder.nextLine();
                builder.addLabel("Maximum :");
                builder.nextColumn();
                builder.add(maxColumnSpinner);
                minMaxPropertyPanel = builder.build();
                minMaxPropertyPanel.setBorder(
                        BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Min/Max"));
            }

            {
                PanelBuilder builder = new PanelBuilder(new FormLayout("p:grow", "p,p,p:grow"), series);
                builder.add(typePropertyPanel);
                builder.nextLine();
                builder.add(isMinMax);
                builder.nextLine();
                builder.add(minMaxPropertyPanel);
                minMaxPropertyPanel.setVisible(false);
            }

            public ColumnEditor() {
                super(new BorderLayout());
                add(columnTypeComboBox, BorderLayout.NORTH);
                columnTypeComboBox.addItemListener(this);
                columnTypeComboBox.setSelectedItem(IGNORE);
                isMinMax.addItemListener(this);
            }

            public JPanel getEditPanel(String selectedType) {
                if (IGNORE.equals(selectedType)) return empty;
                if (DATE.equals(selectedType)) return datePanel;
                return series;
            }

            public boolean isIgnoring() {
                return columnTypeComboBox.getSelectedItem().equals(IGNORE);
            }

            public boolean isDateColumn() {
                return columnTypeComboBox.getSelectedItem().equals(DATE);
            }

            public boolean isSeriesColumn() {
                return columnTypeComboBox.getSelectedItem().equals(SERIES);
            }

            public String getDateFormat() {
                return dateFormatField.getText();
            }

            public Integer[] getLinkedColumns() {
                return new Integer[]{(Integer) minColumnSpinner.getValue(), (Integer) maxColumnSpinner.getValue()};
            }

            public org.cds06.speleograph.data.Type getType() {
                return org.cds06.speleograph.data.Type.getType(typePropertyPanel.getTypeName(), typePropertyPanel.getTypeUnit());
            }

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (!e.getSource().equals(isMinMax) &&
                        !e.getSource().equals(columnTypeComboBox)) return;
//                if (!e.getSource().equals((typeNameBox))) return;
                if (e.getSource().equals(isMinMax)) {
                    minMaxPropertyPanel.setVisible(isMinMax());
                }
                if (e.getSource().equals(columnTypeComboBox)) {
                    JPanel p = getEditPanel((String) columnTypeComboBox.getSelectedItem());
                    removeAll();
                    add(columnTypeComboBox, BorderLayout.NORTH);
                    add(p);
                    revalidate();
                    repaint();
                    pack();
                }
//                if (e.getSource().equals(typeNameBox)) {
//                    final String[] type =((String) typeNameBox.getSelectedItem()).split(" ");
//                    String name = "";
//                    if (!type[0].equals("Autre"))
//                        name = type[0];
//                    String unit = "";
//                    if (type.length > 1)
//                        unit = type[1].substring(1,type[1].length()-1);
//                    if (type.length > 2)
//                        unit = type[2].substring(1,type[2].length()-1);
//                    typeNameField.setText(name);
//                    typeUnitField.setText(unit);
//                }
            }

            public boolean isMinMax() {
                return isMinMax.isSelected();
            }
        }
    }

    private void readFile(
            SpeleoFileReader.HeaderInformation headerInformation,
            int lineStart) {
        for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
            String[] line = lines.get(i);
            if (i < lineStart) continue;
            try {
                headerInformation.read(line);
            } catch (Exception e) {
                System.err.println("Error while importing : " + e.getMessage()); // NON-NLS
                e.printStackTrace(System.err);
            }
        }
    }
}
