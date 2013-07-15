package org.cds06.speleograph.data;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.Validate;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
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
        final JTree tree = new JTree(new TreeModel() {

            /**
             * Returns the root of the tree.  Returns <code>null</code>
             * only if the tree has no nodes.
             *
             * @return the root of the tree
             */
            @Override
            public Object getRoot() {
                return null;
            }

            /**
             * Returns the child of <code>parent</code> at index <code>index</code>
             * in the parent's
             * child array.  <code>parent</code> must be a node previously obtained
             * from this data source. This should not return <code>null</code>
             * if <code>index</code>
             * is a valid index for <code>parent</code> (that is <code>index >= 0 &&
             * index < getChildCount(parent</code>)).
             *
             * @param parent a node in the tree, obtained from this data source
             * @return the child of <code>parent</code> at index <code>index</code>
             */
            @Override
            public Object getChild(Object parent, int index) {
                return null;
            }

            /**
             * Returns the number of children of <code>parent</code>.
             * Returns 0 if the node
             * is a leaf or if it has no children.  <code>parent</code> must be a node
             * previously obtained from this data source.
             *
             * @param parent a node in the tree, obtained from this data source
             * @return the number of children of the node <code>parent</code>
             */
            @Override
            public int getChildCount(Object parent) {
                return 0;
            }

            /**
             * Returns <code>true</code> if <code>node</code> is a leaf.
             * It is possible for this method to return <code>false</code>
             * even if <code>node</code> has no children.
             * A directory in a filesystem, for example,
             * may contain no files; the node representing
             * the directory is not a leaf, but it also has no children.
             *
             * @param node a node in the tree, obtained from this data source
             * @return true if <code>node</code> is a leaf
             */
            @Override
            public boolean isLeaf(Object node) {
                return false;
            }

            /**
             * Messaged when the user has altered the value for the item identified
             * by <code>path</code> to <code>newValue</code>.
             * If <code>newValue</code> signifies a truly new value
             * the model should post a <code>treeNodesChanged</code> event.
             *
             * @param path     path to the node that the user has altered
             * @param newValue the new value from the TreeCellEditor
             */
            @Override
            public void valueForPathChanged(TreePath path, Object newValue) {

            }

            /**
             * Returns the index of child in parent.  If either <code>parent</code>
             * or <code>child</code> is <code>null</code>, returns -1.
             * If either <code>parent</code> or <code>child</code> don't
             * belong to this tree model, returns -1.
             *
             * @param parent a node in the tree, obtained from this data source
             * @param child  the node we are interested in
             * @return the index of the child in the parent, or -1 if either
             * <code>child</code> or <code>parent</code> are <code>null</code>
             * or don't belong to this tree model
             */
            @Override
            public int getIndexOfChild(Object parent, Object child) {
                return 0;
            }

            /**
             * Adds a listener for the <code>TreeModelEvent</code>
             * posted after the tree changes.
             *
             * @param l the listener to add
             * @see #removeTreeModelListener
             */
            @Override
            public void addTreeModelListener(TreeModelListener l) {

            }

            /**
             * Removes a listener previously added with
             * <code>addTreeModelListener</code>.
             *
             * @param l the listener to remove
             * @see #addTreeModelListener
             */
            @Override
            public void removeTreeModelListener(TreeModelListener l) {

            }
        });
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
}
