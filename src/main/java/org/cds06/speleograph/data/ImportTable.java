package org.cds06.speleograph.data;

import javax.swing.*;
import java.io.File;

/**
 * Table to managing file imports into SpeleoGraph.
 * <p>This table show to the user the first ten lines from the file. If only one column is detected we ask if the column
 * separator is not another thing than ';'. After on the each column header on the table, the user can choose what is
 * the data in the current column. In the end, this class call the {@link SpeleoFileReader} to end file reading and push
 * the series into the DataSets which are automatically bidden with the list and the graph.</p>
 */
public class ImportTable extends JTable{

    private int numberOfColumns;
    private File sourceFile;
    private Series[] series;
    private Integer[] columns;
    private SpeleoFileReader.DateInformation dateInformation;

}
