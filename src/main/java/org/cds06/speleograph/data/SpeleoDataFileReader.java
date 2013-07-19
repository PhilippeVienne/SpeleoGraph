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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.cds06.speleograph.I18nSupport;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class SpeleoDataFileReader implements DataFileReader {

    @SuppressWarnings("UnusedDeclaration")
    @NonNls
    private static final Logger log = LoggerFactory.getLogger(SpeleoDataFileReader.class);

    /**
     * This exception is thrown when try to read a non-speleoGraph file.
     */
    @NonNls
    public static final FileReadingError NOT_SPELEO_FILE =
            new FileReadingError("This file is not a SpeleoGraph File", FileReadingError.Part.HEAD);

    /**
     * This string must match to the first line of any SpeleoGraph File.
     */
    @NonNls
    public static final String SPELEOGRAPH_FILE_HEADER = "SpeleoGraph File";

    private static final int READING_HEADERS = 0;
    private static final int FINDING_HEADERS = -1;
    private static final int READING_DATA = 1;
    private static final int CHECKING = 2;

    private static final String SERIES_WITH_INTERNAL_TYPE = "sgt"; //NON-NLS
    private static final String SERIES_WITH_USER_TYPE = "ut"; //NON-NLS
    private static DataFileReader instance = new SpeleoDataFileReader();

    public static DataFileReader getInstance() {
        return instance;
    }

    /**
     * Read a file with SpeleoGraph File Format.
     *
     * @param file The file to read
     * @throws FileReadingError On error while reading the file
     */
    @Override
    public void readFile(File file) throws FileReadingError {
        InputStreamReader streamReader;
        try {
            streamReader = new InputStreamReader(new FileInputStream(file), "UTF-8"); // NON-NLS
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            log.error("Can not access to file", e);
            throw new FileReadingError(
                    I18nSupport.translate("errors.canNotOpenFile", file.getName()), FileReadingError.Part.HEAD, e);
        }
        CSVReader reader = new CSVReader(streamReader, ';', '"');
        String[] line;
        try {
            line = reader.readNext();
        } catch (IOException e) {
            throw new FileReadingError(
                    I18nSupport.translate("errors.canNotReadFileOrEmpty"), FileReadingError.Part.HEAD, e);
        }
        int size, state = CHECKING;
        HeaderInformation headers = new HeaderInformation();
        DateInformation date = new DateInformation();
        headers.setDateInformation(date);
        while (line != null) {
            size = line.length;
            if (size == 0) {
                log.info("Empty line while reading file, just continue our walk.");
                continue;
            }
            String firstLineElement = line[0];
            switch (state) {
                case CHECKING:
                    if (!SPELEOGRAPH_FILE_HEADER.equals(firstLineElement)) throw NOT_SPELEO_FILE;
                    state = FINDING_HEADERS;
                    break;
                case FINDING_HEADERS:
                    if ("headers".equals(firstLineElement)) state = READING_HEADERS; // NON-NLS
                    break;
                case READING_HEADERS:
                    if ("data".equals(firstLineElement)) { // NON-NLS
                        state = READING_DATA;
                        break;
                    }
                    if ("date".equals(firstLineElement)) { // NON-NLS
                        readDateHeaderLine(date, line);
                    } else {
                        readSeriesHeaderLine(file, line, headers);
                        break;
                    }
                    break;
                case READING_DATA:
                    headers.read(line);
                    break;
                default:
                    log.info("State error in reading");
            }
            try {
                line = reader.readNext();
            } catch (IOException e) {
                log.debug("None next lines", e);
            }
        }
        log.info("File reading is ended");
    }

    @NonNls
    @Override
    public String getName() {
        return "SpeleoGraph";
    }

    @Override
    public String getButtonText() {
        return I18nSupport.translate("actions.openFile");
    }


    private static final AndFileFilter filter = new AndFileFilter();

    static {
        filter.addFileFilter(FileFileFilter.FILE);
        filter.addFileFilter(CanReadFileFilter.CAN_READ);
        filter.addFileFilter(CanWriteFileFilter.CAN_WRITE);
        filter.addFileFilter(EmptyFileFilter.NOT_EMPTY);
        filter.addFileFilter(new SuffixFileFilter(new String[]{".speleo", ".csv", ".txt"}, IOCase.INSENSITIVE));// NON-NLS
        filter.addFileFilter(new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                try {
                    return new Scanner(file).nextLine().equals(SPELEOGRAPH_FILE_HEADER);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public boolean accept(File dir, String name) {
                return accept(FileUtils.getFile(dir, name));
            }
        });
    }

    /**
     * Get the FileFilter to use.
     *
     * @return A file filter
     */
    @NotNull
    @Override
    public IOFileFilter getFileFilter() {
        return filter;
    }

    /**
     * Read a header line to add series in headers information.
     * <p>I must write to you the english doc for series lines</p>
     *
     * @param file    The file used to extract the data
     * @param line    The parsed line
     * @param headers The object which represent the headers
     */
    private static void readSeriesHeaderLine(File file, String[] line, HeaderInformation headers) {
        int size = line.length, column = Integer.parseInt(line[0]);
        if (size < 3) { // A series line must have a length gather than 2
            log.info("Invalid header : " + StringUtils.join(line, ' '));
            return;
        }
        String headerType = line[1];
        Series series = null;
        Type t = Type.UNKNOWN;
        switch (headerType) {
            case SERIES_WITH_INTERNAL_TYPE:
                String type = line[2];
                for (Type ty : Type.internalTypes)
                    if (ty.getType().toString().equals(type)) t = ty;
                series = new Series(file, t);
                break;
            case SERIES_WITH_USER_TYPE:
                if (size < 4) {
                    log.info("Invalid header : " + StringUtils.join(line, ' '));
                    break;
                }
                t = Type.getType(line[2], line[3]);
                series = new Series(file, t);
                break;
            default:
                log.info("Invalid header : " + StringUtils.join(line, ';'));
                throw new IllegalStateException("Invalid series entry");
        }
        Properties p = new Properties(line);

        // TODO : Add here code to setup series

        if (t.isHighLowType() || p.getBoolean("min-max")) { // NON-NLS
            Integer min = p.getNumber("min"), max = p.getNumber("max"); // NON-NLS
            if (min == null || max == null) return;
            if (headers.hasSeriesForColumn(min) && headers.hasSeriesForColumn(max)) {
                if (series != null)
                    series.delete();
            }
            t.setHighLowType(true);
            headers.set(series, min, max);
        } else {
            headers.set(series, column);
        }
    }

    /**
     * Read a header line as a date line.
     *
     * @param date The date information where add the date parsing information
     * @param line The parsed line from the CSV
     */
    private static void readDateHeaderLine(DateInformation date, String[] line) {
        int size = line.length;
        if (size < 4) {
            log.info("Invalid header : " + StringUtils.join(line, ' '));
            throw new IllegalStateException("Invalid date entry");
        }
        String time = line[1];
        if (!time.isEmpty())
            date.setTimeZone(TimeZone.getTimeZone(time));
        for (int i = 2; i < (size - 1); i = i + 2)
            date.set(Integer.parseInt(line[i]), line[i + 1]);
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
        final CSVReader reader = new CSVReader(new java.io.FileReader(file), headers.getColumnSeparator(), '"');
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
     * <p>Each column can be link to a java date format scheme. When we read an entry, we join all columns for date and
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
        private TimeZone timeZone;

        protected String computeDateFormat() {
            dateFormat = StringUtils.join(dateFormats, ' ');
            format = new SimpleDateFormat(dateFormat);
            if (timeZone != null)
                format.setTimeZone(timeZone);
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

        public void setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
        }
    }

    /**
     * Information about a file header.
     * <p>A file header is a pack of information which are which series we will read, which columns are liked to a
     * series, what are the date columns, how to parse the date ...</p>
     * <p>This class is designed to be used by two classes, the first is {@link ImportTable} which will populate this
     * class with user information, the second is {@link SpeleoDataFileReader} which will use it to parse the file fast.</p>
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
        @Nullable
        public Series getSeriesForColumn(int column) {
            int index = -1;
            for (int i = 0; i < columns.length; i++) {
                if (ArrayUtils.contains(columns, column)) {
                    index = i;
                    break;
                }
            }
            return index == -1 ? null : getSeries(index);
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
                    series[i].add(item);
                }
                return 0;
            } catch (ParseException e) {
                log.error("Can not read an entry", e);
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

    /**
     * Class used to parse and represent properties read from a SpeleoGraph File.
     *
     * @author Philippe VIENNE
     * @since 1.0
     */
    private static class Properties {

        /**
         * Store properties.
         */
        private HashMap<String, String> properties = new HashMap<>();

        /**
         * Create properties from an array.
         *
         * @param properties Each value in the array is a [key]:[value].
         */
        public Properties(String[] properties) {
            for (String p : properties) {
                final String[] strings = StringUtils.split(p, ":", 2);
                if (strings.length != 2) continue;
                this.properties.put(strings[0].toLowerCase(), strings[1]);
            }
        }

        /**
         * Get a value as a boolean.
         * "0", false, null are considered as false, all other values are true
         *
         * @param key The key to find
         * @return the value corresponding to the key as a boolean.
         */
        public boolean getBoolean(String key) {
            @NonNls String v = properties.get(key);
            return
                    v != null
                            &&
                            !(v.equals("0") || v.equals("false") || v.equals("non") || v.equals("N") || v.equals("F"));
        }

        /**
         * Get a value as an Integer
         *
         * @param key The key to find
         * @return the numerical value
         * @see Integer#parseInt(String)
         */
        public Integer getNumber(String key) {
            try {
                return Integer.parseInt(properties.get(key));
            } catch (Throwable throwable) {
                return null;
            }
        }

        /**
         * Get a value.
         *
         * @param key The key to find
         * @return the value in properties
         */
        public String get(String key) {
            return properties.get(key);
        }

    }
}
