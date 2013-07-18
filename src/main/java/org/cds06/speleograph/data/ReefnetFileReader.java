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
import au.com.bytecode.opencsv.CSVWriter;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Open a CSV Reefnet file and transform it to a CSV for SpeleoGraph.
 * <div>
 * An Reefnet is a comma-separated CSV file with this column :
 * <ol>
 * <li>Index (not used here)</li>
 * <li>Device ID (not used here)</li>
 * <li>File ID (not used here)</li>
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

    @NonNls
    private static Logger log = LoggerFactory.getLogger(ReefnetFileReader.class);

    /**
     * Detect if a file is a ReefNet CSV format.
     * <p>Open the file as a csv, read the first line, we check that :
     * <ul>
     * <li>has got 13 elements</li>
     * <li>the second column contains a ReefNet Device ID starting with "RU-"</li>
     * </ul></p>
     *
     * @param file File to test
     * @return true if it's a ReefNet file
     */
    public static boolean isReefnetFile(File file) {
        try {
            ReefnetFileReader converter = new ReefnetFileReader(file);
            CSVReader csvReader = converter.getReader();
            String[] line = csvReader.readNext();
            return line.length == 13 && line[1].startsWith("SU-");
        } catch (IOException e) {
            log.error("Can not test if it's a ReefFile, continuing as if it's not one", e); // NON-NLS
        }
        return false;
    }

    /**
     * The reefnetFile.
     */
    private File reefnetFile;
    /**
     * The new CSV file with SpeleoGraph format.
     */
    private File csvTempFile;
    /**
     * CVS Parser for ReefNet file.
     *
     * @see #reefnetFile
     */
    private CSVReader reader;
    /**
     * CVS Writer for the new file.
     *
     * @see #csvTempFile
     */
    private CSVWriter writer;

    /**
     * Create a new converter for ReefNet file to a temporary file.
     *
     * @param file The file to convert to SpeleoGraph format
     * @throws IOException when can not read the file or create the temporary file
     */
    public ReefnetFileReader(File file) throws IOException {
        this(file, File.createTempFile("ReefnetToSpeleoGraph", ".cvs"));
    }

    /**
     * Create a new converter for ReefNet file to a File
     *
     * @param reefnetFile     The ReefNet file (CSV Data)
     * @param speleographFile The file to write with the new data (it will be erased)
     * @throws IOException when can not read ReefNet File or write the new File.
     */
    public ReefnetFileReader(File reefnetFile, File speleographFile) throws IOException {
        setReefnetFile(reefnetFile);
        setCsvTempFile(speleographFile);
    }

    /**
     * Convert the Reefnet file.
     * <p>
     * This function read all lines from Reefnet files. For each line it does the following :
     * <ul>
     * <li>Convert the temperature from Kelvin to Celsius</li>
     * <li>Calculate the measure's date with date and offset</li>
     * <li>Add all information to the new CSV File</li>
     * </ul>
     * </p>
     *
     * @return The converted file
     *
     * @throws IOException when can not read or write a piece of information.
     */
    public File convert() throws IOException {
        log.info("Start the conversion of " + getReefnetFile().getName());
        List<String[]> lines = getReader().readAll();
        getWriter().writeNext(new String[]{lines.get(0)[1]});
        getWriter().writeNext(new String[]{"Date", "Heure", "Pression", "Moy. : Température, °C"});
//        String[] line;
        for (String[] line : lines) {
            String temperature;
            {
                Integer tempEnt = Integer.parseInt(line[11]), tempDec = Integer.parseInt(line[12]);
                tempDec = tempDec - 15;
                if (tempDec < 0) {
                    tempDec = 100 + tempDec;
                    tempEnt = tempEnt - 274;
                } else {
                    tempEnt = tempEnt - 273;
                }
                temperature = tempEnt + "." + tempDec;
            }
            String pressure = line[10];
            int offset = Integer.parseInt(line[9]);
            Calendar moment = Calendar.getInstance();
            //noinspection MagicConstant
            moment.set(
                    Integer.parseInt(line[3]),
                    Integer.parseInt(line[4]) - 1,
                    Integer.parseInt(line[5]),
                    Integer.parseInt(line[6]),
                    Integer.parseInt(line[7]),
                    Integer.parseInt(line[8])
            );
            moment.add(Calendar.SECOND, offset); // Add the offset to the reference date
            String date = new SimpleDateFormat("dd/MM/yyyy").format(moment.getTime());
            String hour = new SimpleDateFormat("HH:mm:ss").format(moment.getTime());
            // Write all into the file
            getWriter().writeNext(new String[]{date, hour, pressure, temperature});
        }
        getWriter().close();
        log.info("Conversion of " + getReefnetFile().getName() + " is ended");
        return getCsvTempFile();
    }

    /**
     * Getter for ReefNet File
     *
     * @return The ReefNet file currently read by the program
     */
    public File getReefnetFile() {
        return reefnetFile;
    }

    /**
     * Set the ReefNet File to convert.
     * It will set the class variable and create the CSVReader for this file.
     *
     * @param reefnetFile The ReefNet file to read
     * @throws IOException when can not read the ReefNet File.
     */
    private void setReefnetFile(File reefnetFile) throws IOException {
        this.reefnetFile = reefnetFile;
        try {
            setReader(new CSVReader(new FileReader(getReefnetFile()), ','));
        } catch (FileNotFoundException e) {
            throw new IOException("Unable to read file " + reefnetFile.getName());
        }
    }

    /**
     * Getter for the new CSV file.
     *
     * @return The File object which represent the targeted file for conversion.
     */
    public File getCsvTempFile() {
        return csvTempFile;
    }

    /**
     * Set the destination file.
     * This file will be cleared and replaced with the converted data after call on #convert
     *
     * @param csvTempFile The destination file to write
     * @throws IOException when can not wright the new file.
     */
    private void setCsvTempFile(File csvTempFile) throws IOException {
        this.csvTempFile = csvTempFile;
        try {
            setWriter(new CSVWriter(new FileWriter(getCsvTempFile()), ';', (char) 0));
        } catch (IOException e) {
            throw new IOException("Unable to write file " + getCsvTempFile().getName(), e);
        }
    }

    private CSVReader getReader() {
        return reader;
    }

    private void setReader(CSVReader reader) {
        this.reader = reader;
    }

    private CSVWriter getWriter() {
        return writer;
    }

    private void setWriter(CSVWriter writer) {
        this.writer = writer;
    }

    @Override
    public void readFile(File file) throws FileReadingError {

    }

    /**
     * Get the name of file read by this class.
     *
     * @return The localized name of file.
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * Get the text for buttons or menus.
     *
     * @return The localized text.
     */
    @Override
    public String getButtonText() {
        return null;
    }
}
