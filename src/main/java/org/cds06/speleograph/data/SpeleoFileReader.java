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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class SpeleoFileReader {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(SpeleoFileReader.class);

    /**
     * This exception is thrown when try to read a non-speleoGraph file.
     */
    @NonNls
    public static final IOException NOT_SPELEO_FILE = new IOException("This file is not a SpeleoGraph File");

    /**
     * This string must match to the first line of any SpeleoGraph File.
     */
    @NonNls
    public static final String SPELEOGRAPH_FILE_HEADER = "SpeleoGraph File";

    /**
     * Read a file with SpeleoGraph File Format.
     *
     * @param file The file to read
     * @throws IOException    On error while reading the file
     * @throws ParseException On date or time parse error
     */
    public static void readFile(File file) throws IOException, ParseException {
        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), ';', '"'); //NON-NLS
        String[] line;
        boolean hasCheckFile = false, hasReadHeader = false;
        while ((line=reader.readNext())!=null){
            if(!hasCheckFile){
                if(line.length==0 || !line[0].equals(SPELEOGRAPH_FILE_HEADER)) throw NOT_SPELEO_FILE;
                hasCheckFile = true;
            } else if(!hasReadHeader&&hasCheckFile){ // Read headers
                if(line.length==0) continue;
                if(line[0].equals("date")){

                } else {
                    int column = Integer.parseInt(line[0]);
                    String type = line[1];
                    if("sgt".equals(line[1])){

                    } else if("ut".equals(line[1])){

                    } else {

                    }
                }
            } else { // Read Data

            }
        }

    }

    /**
     * Read a file into Series.
     * <p>Series are stored into the {@link HeaderInformation}. This function will call it line by line to push the data
     * into series. In case of error, we simply continue to the next value.</p>
     *
     * @param headers This object contains all data usefull
     * @param file    The file which we will read
     * @throws FileNotFoundException If file does not exists.
     * @throws IOException           If an error occurs when read line in the file, to get more information about this exception
     *                               see {@link au.com.bytecode.opencsv.CSVReader#readNext()}.
     */
    public static void read(File file, HeaderInformation headers) throws IOException {
        Validate.notNull(file);
        Validate.notNull(headers);
        final CSVReader reader = new CSVReader(new FileReader(file), headers.getColumnSeparator(), '"');
        int lineId = -1;
        String[] line;
        while ((line = reader.readNext()) != null) {
            lineId++;
            if (lineId < headers.getFirstLineOfData()) continue;
            headers.read(line);
        }
    }

    /**
     * Store date-column link information for a file.
     * <p>Each column can be link to a java date format sheme. When we read an entry, we join all columns for date and
     * all date formats and ask to Java to parse it. If we got an error on parse, just return the actual date.</p>
     */
    public static class DateInformation {

        /**
         * Columns joined to create the date to parse.
         */
        private int[] columns = new int[0];
        /**
         * The date format for a column with the same array index.
         * <p>Ex.: dateFormats[i] is the format for columns[i]</p>
         */
        private String[] dateFormats = new String[0];
        /**
         * This is the final computed value from {@link DateInformation#dateFormats}.
         */
        private String dateFormat = "dd/MM/yyyy HH:mm:ss";

        private SimpleDateFormat format = new SimpleDateFormat(dateFormat);

        protected String computeDateFormat() {
            dateFormat = StringUtils.join(dateFormats, ' ');
            format = new SimpleDateFormat(dateFormat);
            return dateFormat;
        }

        public int set(int column, String format) {
            Validate.notNull(column);
            Validate.notNull(format);
            Validate.notEmpty(format);
            Validate.isTrue(column >= 0, "Column index should be positive");
            {   // Check if format is a valid date format
                try {
                    new SimpleDateFormat(format).format(Calendar.getInstance().getTime());
                } catch (IllegalArgumentException e) {
                    return -1;
                }
            }
            int index = ArrayUtils.indexOf(columns, column);
            boolean isAdding = false;
            if (index == ArrayUtils.INDEX_NOT_FOUND) {
                index = columns.length;
                columns = Arrays.copyOf(columns, index + 1);
                dateFormats = Arrays.copyOf(dateFormats, index + 1);
                isAdding = true;
            }
            columns[index] = column;
            dateFormats[index] = format;
            computeDateFormat();
            return isAdding ? 1 : 0;
        }

        public Date parse(String[] line) {
            String[] toJoin = new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                toJoin[i] = line[columns[i]];
            }
            String date = StringUtils.join(toJoin, ' ');
            try {
                return format.parse(date);
            } catch (ParseException e) {
                throw new IllegalStateException("Can not parse a date !", e);
            }
        }

        /**
         * Determine if a column is already linked to a date format.
         *
         * @param index The column index
         * @return True if a date format exist.
         */
        public boolean hasDateInformationForColumn(int index) {
            return ArrayUtils.contains(columns, index);
        }

        /**
         * Get the current format for a column
         *
         * @param column The column to find
         * @return The format or null if this column has no date format.
         */
        public String getForColumn(int column) {
            for (int i = 0; i < columns.length; i++)
                if (columns[i] == column)
                    return dateFormats[i];
            return null;
        }

        /**
         * Delete an entry.
         *
         * @param index The index of column to delete
         */
        public void remove(int index) {
            dateFormats = ArrayUtils.removeElement(dateFormats, getForColumn(index));
            columns = ArrayUtils.removeElement(columns, index);
        }
    }

    /**
     * Information about a file header.
     * <p>A file header is a pack of information which are which series we will read, which columns are liked to a
     * series, what are the date columns, how to parse the date ...</p>
     * <p>This class is designed to be used by two classes, the first is {@link ImportTable} which will populate this
     * class with user information, the second is {@link SpeleoFileReader} which will use it to parse the file fast.</p>
     *
     * @author Philippe VIENNE
     * @see java.io.Serializable This class is serializable to be saved in case we have to reuse it.
     * @since 1.0
     */
    public static class HeaderInformation implements Serializable {

        /**
         * Serial Version UID.
         * Don't forget to change it if you change the serialization methods.
         */
        static final long serialVersionUID = 1L;

        /**
         * Number format is used to parse numbers in columns.
         *
         * @todo Can improve HeaderInformation to allow number reading on multiple columns ?
         */
        private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();

        /**
         * Number of read Series.
         * This value is computed to go faster while iterating series or columns. It must be a computed value and never
         * be visible for elements outside this class.
         */
        protected int numberOfSeriesToParse;

        /**
         * Series array, it contains all series to read mapped to an id (the index).
         */
        protected Series[] series = new Series[0];

        /**
         * Columns array, contains the columns id to read for a Series.
         * <p>Each entry in this array is designed as the following rules :
         * <ul>
         * <li>{@code [int]}: is a single column data and series admit only a value per item</li>
         * <li>{@code [int, int]}: each item has a minimal value (first index) and maximal value(second index)</li>
         * <li>{@code [int, int, int]}: 0 is a value, 1 a minimal value and 2 a maximal value</li>
         * </ul>
         * each entry in this array which has null length or length gather than 3 will be ignored.
         */
        protected Integer[][] columns = new Integer[0][];

        /**
         * The line in the file where we will start to read data.
         */
        protected int firstLineOfData;

        /**
         * Information about date columns.
         * <p>A column used for a Date element should not be used for a data.</p>
         */
        protected DateInformation dateInformation;

        /**
         * Separator for each column in this file.
         */
        private char columnSeparator = ';';

        /**
         * Get the value of the line in the file where we will start to read data.
         *
         * @return An integer if not it will be a RuntimeError.
         */
        public int getFirstLineOfData() {
            return firstLineOfData;
        }

        /**
         * Set the value of the line in the file where we will start to read data.
         *
         * @param firstLineOfData A non-negative integer
         * @throws IllegalArgumentException if the argument is not an integer or a negative integer.
         */
        public void setFirstLineOfData(int firstLineOfData) {
            Validate.notNull(firstLineOfData, "The argument should not be null.");
            Validate.isTrue(firstLineOfData >= 0, "The argument should be positive");
            this.firstLineOfData = firstLineOfData;
        }

        /**
         * Set the date information for the current file.
         *
         * @param dateInformation A non null object which represent the date information.
         */
        public void setDateInformation(DateInformation dateInformation) {
            Validate.notNull(dateInformation);
            this.dateInformation = dateInformation;
        }

        /**
         * Determine if a column is already linked to a series.
         *
         * @return true if a Series exist for this column.
         */
        public boolean hasSeriesForColumn(int index) {
            for (Integer[] cols : columns) if (ArrayUtils.contains(cols, index)) return true;
            return false;
        }

        /**
         * Get a series for an index.
         *
         * @param index The series index
         */
        public Series getSeries(int index) {
            Validate.validIndex(series, index, "The index should correspond to a series entry");
            return series[index];
        }

        /**
         * Get a series for a column.
         *
         * @param column The column index
         */
        public Series getSeriesForColumn(int column) {
            int index = -1;
            for (int i = 0; i < columns.length; i++) {
                if (ArrayUtils.contains(columns, column)) {
                    index = i;
                    break;
                }
            }
            return index == -1 ? new Series(null) : getSeries(index);
        }

        /**
         * Get column information for a series.
         *
         * @param series The series
         * @return The column information or null if series is not found in this header.
         */
        public Integer[] getColumnInformation(Series series) {
            final int index = ArrayUtils.indexOf(this.series, series);
            if (index == ArrayUtils.INDEX_NOT_FOUND) return null;
            Validate.validIndex(columns, index, "No column data for the index %d", index);
            return columns[index];
        }

        /**
         * Set data information for a Series and Columns.
         *
         * @param series The series to add (should be not null)
         * @param column The column information with this format: <ul>
         *               <li>{@code [int]}: is a single column data and series admit only a value per item</li>
         *               <li>{@code [int, int]}: each item has a minimal value (first index) and maximal value(second index)</li>
         *               <li>{@code [int, int, int]}: 0 is a value, 1 a minimal value and 2 a maximal value</li>
         *               </ul>
         * @return 0 if it creates a new entry, 1 if it update one, -1 in case of error.
         */
        public int set(Series series, Integer... column) {
            Validate.notNull(series);
            Validate.notEmpty(column);
            Validate.isTrue(column.length < 4, "Column array should have a length between 1 and 3 (see documentation)");
            int index = ArrayUtils.indexOf(this.series, series);
            boolean isCreated = false;
            if (index == ArrayUtils.INDEX_NOT_FOUND) {
                // Must remove other attached series to columns
                for (int col : column) {
                    if (hasSeriesForColumn(col)) {
                        remove(col);
                    }
                }
                index = this.series.length;
                this.series = Arrays.copyOf(this.series, index + 1);
                this.columns = Arrays.copyOf(this.columns, index + 1);
                this.numberOfSeriesToParse = this.series.length;
                isCreated = true;
            }
            this.series[index] = series;
            this.columns[index] = column;
            return isCreated ? 0 : 1;
        }

        /**
         * Read a line of data.
         *
         * @param line The array of columns. Should not be null.
         * @return 0 if parse is full correct, 1 otherwise.
         */
        public int read(String[] line) {
            try {
                final Date date = dateInformation.parse(line);
                for (int i = 0; i < numberOfSeriesToParse; i++) {
                    final Integer[] columnIds = columns[i];
                    Item item;
                    if (columnIds.length == 1) {
                        if ("".equals(line[columnIds[0]])) continue;
                        item = new Item(date, numberFormat.parse(line[columnIds[0]]).doubleValue());
                    } else if (columnIds.length == 2) {
                        if ("".equals(line[columnIds[0]])) continue;
                        if ("".equals(line[columnIds[1]])) continue;
                        item = new Item(date, numberFormat.parse(line[columnIds[0]]).doubleValue(), numberFormat.parse(line[columnIds[1]]).doubleValue());
                    } else {
                        continue;
                    }
                    series[i].getItems().add(item);
                }
                return 0;
            } catch (ParseException e) {
                LoggerFactory.getLogger(SpeleoFileReader.class).error("Can not read an entry", e);
                return 1;
            }
        }

        public char getColumnSeparator() {
            return columnSeparator;
        }

        public void setColumnSeparator(char columnSeparator) {
            this.columnSeparator = columnSeparator;
        }

        public void remove(int index) {
            for (int i = 0; i < columns.length; i++) {
                for (int c : columns[i]) {
                    if (c == index) {
                        columns = ArrayUtils.remove(columns, i);
                        series = ArrayUtils.remove(series, i);
                    }
                }
            }
        }
    }
}
