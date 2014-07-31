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
import org.apache.commons.io.filefilter.*;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Open a CSV Reefnet file and transform it to a CSV for SpeleoGraph.
 * <div>
 * An Reefnet is a comma-separated CSV file with this column :
 * <ol>
 * <li>Index (not used here)</li>
 * <li>Device ID</li>
 * <li>ReefNet Series ID</li>
 * <li>Year</li>
 * <li>Month</li>
 * <li>Day</li>
 * <li>Hour</li>
 * <li>Minute</li>
 * <li>Second</li>
 * <li>Offset</li>
 * <li>Pressure</li>
 * <li>Temperature (Integer part)</li>
 * <li>Temperature (Decimal part)</li>
 * </ol>
 * This is the format when you export from Sensus Manager and excepted for this converter.
 * </div>
 *
 * @author Philippe VIENNE
 * @since 1.0
 */
public class ReefnetFileReader implements DataFileReader {

    /**
     * Logger for errors and information.
     */
    @NonNls
    private static final Logger log = LoggerFactory.getLogger(ReefnetFileReader.class);

    /**
     * When test if a CSV file is a ReefNet File, Maximal not ReefNet lines allowed before.
     */
    private static final int MAX_ALLOWED_HEADERS = 30;

    /**
     * Detect if a file is a ReefNet CSV format.
     * <p>Open the file as a csv, read the first line, we check that :
     * <ul>
     * <li>has got 12 or 13 elements</li>
     * <li>the second column contains a ReefNet Device ID starting with "RU-"</li>
     * </ul>
     * </p>
     * @param file File to test
     * @return true if it's a ReefNet file
     */
    public static boolean isReefnetFile(File file) {
        try {
            CSVReader csvReader = new CSVReader(new FileReader(file), ',');
            String[] line;
            for (int i = 0; i < MAX_ALLOWED_HEADERS && (line = csvReader.readNext()) != null; i++) {
                int size = line.length;
                if (11 < size && size < 14 && line[1].startsWith("SU-")) // NON-NLS
                    return true;
                if (size > 1)
                    return false;
            }
        } catch (IOException e) {
            log.error("Can not test if it's a ReefFile, continuing as if it's not one", e); // NON-NLS
        }
        return false;
    }

    /**
     * Date format used to parse date in ReefNet entries.
     * This variable must not be altered without editing {@link #readDate(String[], java.util.Calendar)}
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("y:M:d:H:m:s");

    /**
     * Read a ReefNet File.
     *
     * @param file The file to read.
     * @throws FileReadingError When an error occurs when read the file.
     * @see #readReefnetEntry(String[], Series, Series, String, java.util.Calendar)
     * @see #readDate(String[], java.util.Calendar)
     */
    @Override
    public void readFile(File file) throws FileReadingError {
        log.info("Start reading file: " + file);
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new FileReadingError(
                    I18nSupport.translate("errors.canNotOpenFile", file.getName()),
                    FileReadingError.Part.HEAD,
                    e
            );
        }
        CSVReader reader = new CSVReader(fileReader, ',');
        String[] line;
        try {
            line = reader.readNext();
        } catch (IOException e) {
            throw new FileReadingError(
                    I18nSupport.translate("errors.canNotReadFileOrEmpty"),
                    FileReadingError.Part.HEAD,
                    e
            );
        }
        Series pressureSeries = new Series(file, Type.PRESSURE),
                temperatureSeries = new Series(file, Type.TEMPERATURE);
        String seriesId = "";
        final Calendar calendar = Calendar.getInstance();
        while (line != null) {
            if (11 < line.length && line.length < 14) {
                seriesId = readReefnetEntry(line, pressureSeries, temperatureSeries, seriesId, calendar);
            } else {
                log.info("Not a Reefnet line: " + StringUtils.join(line, ',')); //NON-NLS
            }
            try {
                line = reader.readNext();
            } catch (IOException e) {
                line = null;
            }
        }
        log.info("Reefnet File (" + file.getName() + ") has been read."); //NON-NLS
    }

    /**
     * Read an entry from a Reefnet File.
     *
     * @param line              The line extracted from the file (length must be 12 or 13)
     * @param pressureSeries    The series where add pressure data
     * @param temperatureSeries The series where add temperature data
     * @param seriesId          The ReefNet's Series ID
     * @param calendar          The calendar which contains the start date of the current series.
     * @return The modified ReefNet's Series ID.
     * @throws FileReadingError When can not parse the date.
     * @see #readDate(String[], java.util.Calendar)
     * @see #readFile(java.io.File)
     */
    private String readReefnetEntry(
            String[] line, Series pressureSeries, Series temperatureSeries, String seriesId, Calendar calendar)
            throws FileReadingError {
        double temperature = 0;
        if (line.length == 12) {
            temperature = Double.parseDouble(line[11]) - 273.15;
        } else if (line.length == 13) {
            temperature = Double.parseDouble(line[11] + '.' + line[12]) - 273.15;
        }
        int pressure = Integer.parseInt(line[10]);
        if (!seriesId.equals(line[2])) {
            seriesId = readDate(line, calendar);
        }
        Calendar clone = (Calendar) calendar.clone();
        clone.add(Calendar.SECOND, Integer.parseInt(line[9]));
        temperatureSeries.add(new Item(temperatureSeries, clone.getTime(), temperature));
        pressureSeries.add(new Item(pressureSeries, clone.getTime(), pressure));
        return seriesId;
    }

    /**
     * Read a date from a Reefnet entry.
     *
     * @param line     The line where the date is stored
     * @param calendar The calendar to update with the read date
     * @return The new ReefNet series ID which comes with this date.
     * @throws FileReadingError
     * @see #readFile(java.io.File)
     * @see #readReefnetEntry(String[], Series, Series, String, java.util.Calendar)
     */
    private String readDate(String[] line, Calendar calendar) throws FileReadingError {
        String seriesId;
        seriesId = line[2];
        Date d;
        try {
            d = dateFormat.parse(StringUtils.join(Arrays.copyOfRange(line, 3, 9), ':'));
        } catch (ParseException e) {
            log.error("Can not parse a date", e);
            throw new FileReadingError(
                    I18nSupport.translate("errors.canNotReadDate"),
                    FileReadingError.Part.DATA,
                    e
            );
        }
        calendar.setTime(d);
        return seriesId;
    }

    /**
     * Get the name of file read by this class.
     *
     * @return The localized name of file.
     */
    @Override
    public String getName() {
        return "ReefNet File"; // NON-NLS
    }

    /**
     * Get the text for buttons or menus.
     *
     * @return The localized text.
     */
    @Override
    public String getButtonText() {
        return I18nSupport.translate("actions.openReefNetFile");
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


    private static final AndFileFilter filter = new AndFileFilter(
            FileFileFilter.FILE,
            new AndFileFilter(
                    new SuffixFileFilter(new String[]{".csv", ".txt"}), // NON-NLS
                    new AbstractFileFilter() {
                        /**
                         * Checks to see if the File should be accepted by this filter.
                         *
                         * @param dir  the directory File to check
                         * @param name the filename within the directory to check
                         * @return true if this file matches the test
                         */
                        @Override
                        public boolean accept(File dir, String name) {
                            return isReefnetFile(FileUtils.getFile(dir, name));
                        }
                    }));
}
