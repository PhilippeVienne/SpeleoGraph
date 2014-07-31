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
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.cds06.speleograph.I18nSupport;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Reader for Hobo files.
 * <p>A Hobo's file is a file which contains data about measure of water, pressure, temperature.</p>
 * <div><h3>Hobo's File format</h3>
 * <p>This is a csv file separated by comas. The minimal columns are the first and the second, after you choose what
 * you want to add as data. Names are in french, translated it's Pressure, Temperature (in °C), Amount of water (mm),
 * Temperature minimal (in °C), Temperature maximal (in °C)</p>
 * <p>On read entry, if one of column contains no data (empty string), it's assumed as there is no measure on
 * this date</p>
 * <p>Stop talking, this is a full example: </p>
 * <pre>"Titre de tracé : 2315774"
 * "Date";"Heure, GMT+02:00";"Pluvio, mm";"Max. : Température, °C";"Min. : Température, °C";"Moy. : Température, °C"
 * 30/09/2012;00:00:00;;26,292;16,427;
 * 30/09/2012;10:30:00;;;;21,282
 * 30/09/2012;10:37:12;0,00;;;
 * 30/09/2012;11:00:00;;;;22,525
 * 30/09/2012;11:05:35;0,25;;;
 * 30/09/2012;11:07:12;;;;
 * 30/09/2012;11:30:00;;;;23,292
 * 30/09/2012;11:37:12;;;;
 * 30/09/2012;12:00:00;;;;23,581
 * 30/09/2012;12:07:12;;;;
 * 30/09/2012;12:30:00;;;;26,292
 * 30/09/2012;12:37:12;;;;
 * 30/09/2012;13:00:00;;;;25,319
 * 30/09/2012;13:07:12;;;;
 * 30/09/2012;13:30:00;;;;25,513
 * 30/09/2012;13:37:12;;;;
 * </pre>
 * </div>
 *
 * @author PhilippeGeek
 * @since 1.0
 */
public class HoboFileReader implements DataFileReader {

    @NonNls
    private static final Logger log = LoggerFactory.getLogger(HoboFileReader.class);

    /**
     * Hobo Date Format.
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/y H:m:s");

    @Override
    public void readFile(File file) throws FileReadingError {
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new FileReadingError(
                    I18nSupport.translate("error.canNotOpenFile", file.getName()),
                    FileReadingError.Part.HEAD,
                    e
            );
        }
        CSVReader csvReader = new CSVReader(fileReader, ';');
        String[] line;
        try {
            line = csvReader.readNext();
        } catch (IOException e) {
            throw new FileReadingError(
                    I18nSupport.translate("error.canNotReadFileOrEmpty"),
                    FileReadingError.Part.HEAD,
                    e
            );
        }
        Headers headers = null;
        Series[] availableSeries = new Series[]{};
        int[][] columns = new int[][]{};
        int dateColumn = -1, timeColumn = -1;
        while (line != null) {
            if (line.length <= 1) { // Title Line (just skip it)
                log.info("Head line", line);
            } else if (headers == null) { // The first line is headers
                headers = Headers.parseHeaderLine(file, line);
                availableSeries = headers.availableSeries;
                columns = headers.typeColumns;
                dateColumn = headers.dateColumns[0];
                timeColumn = headers.dateColumns[1];
                if (!(dateColumn != -1 && timeColumn != -1 && availableSeries.length > 0)) {
                    headers = null;
                    log.error("Error while parsing", line);
                }
            } else {

                // Now, this is a data line
                Date day;
                try {
                    day = dateFormat.parse(line[dateColumn] + " " + line[timeColumn]);
                } catch (ParseException e) {
                    day = Calendar.getInstance().getTime();
                }
                for (int i = 0; i < availableSeries.length; i++) {
                    if (line[columns[i][0]].length() > 0) {
                        Item item = null;
                        switch (columns[i].length) {
                            case 1:
                                item = new Item(
                                        availableSeries[i],
                                        day,
                                        Double.valueOf(line[columns[i][0]].replace(',', '.'))
                                );
                                break;
                            case 2:
                                if (!(line[columns[i][1]].length() > 0)) break;
                                item = new Item(
                                        availableSeries[i],
                                        day,
                                        Double.valueOf(line[columns[i][0]].replace(',', '.')),
                                        Double.valueOf(line[columns[i][1]].replace(',', '.'))
                                );
                                break;
                            default:
                                log.error("Strange things happened");
                        }
                        if (item != null) {
                            availableSeries[i].add(item);
                        }
                    }
                }
            }
            try {
                line = csvReader.readNext();
            } catch (IOException e) {
                line = null;
            }
        }
    }

    /**
     * Get the name of file read by this class.
     *
     * @return The localized name of file.
     */
    @Override
    public String getName() {
        return "Hobo File"; // NON-NLS
    }

    /**
     * Get the text for buttons or menus.
     *
     * @return The localized text.
     */
    @Override
    public String getButtonText() {
        return I18nSupport.translate("actions.openHoboFile");
    }

    private static final AndFileFilter filter = new AndFileFilter();

    static {
        filter.addFileFilter(FileFileFilter.FILE);
        filter.addFileFilter(CanReadFileFilter.CAN_READ);
        filter.addFileFilter(CanWriteFileFilter.CAN_WRITE);
        filter.addFileFilter(EmptyFileFilter.NOT_EMPTY);
        filter.addFileFilter(new SuffixFileFilter(new String[]{".csv", ".txt"}, IOCase.INSENSITIVE));// NON-NLS
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
     * Helper for read headers.
     * <p>It's Helper is designed to get which data is stored in the file and in which column.</p>
     */
    private static class Headers {

        /**
         * Define conditions to determine if a column contains a type of data.
         * <p>Conditions are an array of strings. On index 0, the condition for value column, on index 1 and 2
         * conditions for min and max column. An null entry define that the value or min, max is not available fo
         * this Type of data.</p>
         */
        @NonNls
        private static HashMap<Type, String[]> headerConditions = new HashMap<>();

        static {   // Write conditions for each type
            headerConditions.put(Type.TEMPERATURE, new String[]{"Moy. : Température, °C"});
            headerConditions.put(
                    Type.TEMPERATURE_MIN_MAX,
                    new String[]{"Min. : Température, °C", "Max. : Température, °C"}
            );
            headerConditions.put(Type.WATER, new String[]{"Pluvio, mm"});
        }

        /**
         * Condition for date column.
         */
        private static final String dateColumn = "Date"; // NON-NLS
        /**
         * Condition for hour column
         */
        private static final String timeColumn = "Heure"; // NON-NLS

        /**
         * Parse all data into a header line.
         *
         * @return Header Data in an object
         */
        public static Headers parseHeaderLine(File f, String[] line) {
            Headers headers = new Headers();
            ArrayList<Series> availableSeries = new ArrayList<>();
            ArrayList<int[]> columns = new ArrayList<>();
            for (Type t : headerConditions.keySet()) {
                findHeader(f, line, t, availableSeries, columns);
            }
            headers.availableSeries = availableSeries.toArray(new Series[availableSeries.size()]);
            headers.typeColumns = columns.toArray(new int[availableSeries.size()][2]);
            headers.dateColumns = new int[]{-1, -1};
            findDate(line, headers);
            return headers;
        }

        private static void findDate(String[] line, Headers headers) {
            for (int i = 0, lineLength = line.length; i < lineLength; i++) {
                String l = line[i];
                if (l.contains(dateColumn)) headers.dateColumns[0] = i;
                if (l.contains(timeColumn)) headers.dateColumns[1] = i;
            }
        }

        private static void findHeader(File file, String[] line, Type type, ArrayList<Series> availableSeries, ArrayList<int[]> columns) {
            String[] conditions = headerConditions.get(type);
            switch (conditions.length) {
                case 1:
                    int col = ArrayUtils.indexOf(line, conditions[0]);
                    if (col == ArrayUtils.INDEX_NOT_FOUND) break;
                    columns.add(new int[]{col});
                    availableSeries.add(new Series(file, type));
                    break;
                case 2:
                    int min = ArrayUtils.indexOf(line, conditions[0]);
                    int max = ArrayUtils.indexOf(line, conditions[1]);
                    if (min == ArrayUtils.INDEX_NOT_FOUND || max == ArrayUtils.INDEX_NOT_FOUND) break;
                    columns.add(new int[]{min, max});
                    availableSeries.add(new Series(file, type));
                    break;
                default:
                    log.error("Unknown conditions: " + StringUtils.join(conditions, ','));
            }
        }

        public Series[] availableSeries;
        public int[][] typeColumns;
        public int[] dateColumns;

    }
}
