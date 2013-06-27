package org.cds06.speleograph;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */

import au.com.bytecode.opencsv.CSVReader;
import org.cds06.speleograph.Data.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Reader for SpeleoGraph files.
 * <p>A SpeleoGraph's file is a file which contains data about measure of water, pressure, temperature.</p>
 * <div><h3>SpeleoGraph's File format</h3>
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
 */
public class DataSetReader {

    private static final Logger log = LoggerFactory.getLogger(DataSetReader.class);

    /**
     * The title read from the first line of file.
     */
    private String title = null;
    /**
     * The file where we read the data
     */
    private File dataOriginFile = null;
    /**
     * Read data stored by Type
     */
    private HashMap<Type, DataSet> dataSets = new HashMap<>(Type.values().length);

    {   // Setup DataSets
        for (Type type : Type.values()) {
            dataSets.put(type, new DataSet());
            dataSets.get(type).setReader(this);
            dataSets.get(type).setType(type);
        }
    }

    /**
     * Create a new reader.
     * <p>The constructor setup the dataSets and start to read the file.</p>
     * <p>It's recommended to put you call in a new thread to not block when you have large file.</p>
     *
     * @param file The file to open and read
     * @throws IOException
     */
    public DataSetReader(File file) throws IOException {
        setDataOriginFile(file);
        read();
    }

    /**
     * Getter for the file title
     * <p>If the header of file define a title, it will be this. Otherwise, we use the title of file</p>
     *
     * @return The title or null if none title are available
     */
    public String getTitle() {
        return title == null ? dataOriginFile.getName() : title;
    }

    /**
     * Used to set the title of DataSet from outside params.
     *
     * @param title The title to give
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the read file
     *
     * @return A file or null
     */
    public File getDataOriginFile() {
        return dataOriginFile;
    }

    /**
     * Set the file we have to read.
     * <p>This function will in add setup the dataSet with this file.</p>
     *
     * @param dataOriginFile The file to read
     */
    public void setDataOriginFile(File dataOriginFile) {
        this.dataOriginFile = dataOriginFile;

    }

    /**
     * Parse and read the file.
     * <p>An huge function, which will :
     * <ul>
     * <li>Parse the CSV file</li>
     * <li>Detect and read the headers</li>
     * <li>Read each line as entry</li>
     * </ul></p>
     *
     * @throws IOException in case of the JVM can't read the file
     */
    private void read() throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(getDataOriginFile()), ';');
        String[] line;
        HeadersList headers = null;
        ArrayList<Type> availableTypes = new ArrayList<>(Type.values().length);
        HashMap<Type, int[]> columns = new HashMap<>(Type.values().length);
        int id = 0;
        while ((line = csvReader.readNext()) != null) {
            if (line.length <= 1) {                            // Title Line
                if (line.length != 0) setTitle(line[0]);
            } else if (headers == null) {                         // The first line is headers
                headers = new HeadersList(line.length);
                Collections.addAll(headers, line);
                availableTypes = headers.getAvailableTypes();
                for (Type type : availableTypes) {
                    if (headers.isMinMaxType(type)) {
                        columns.put(type, headers.getMinMaxValueColumnIdForType(type));
                    } else {
                        columns.put(type, new int[]{headers.getValueColumnIdForType(type)});
                    }
                }
            } else {                                        // An data line
                String date = line[headers.getDateColumnId()] + " " + line[headers.getTimeColumnId()];
                String format = "dd/MM/yyyy HH:mm:ss";
                Data data = null;
                for (Type t : availableTypes) {
                    data = new Data();
                    data.setDataType(t);
                    data.setDate(date, format);
                    if (headers.isMinMaxType(t)) {
                        if (line[columns.get(t)[0]].length() > 0 && line[columns.get(t)[1]].length() > 0) {
                            data.setMinValue(Double.valueOf(line[columns.get(t)[0]].replace(',', '.')));
                            data.setMaxValue(Double.valueOf(line[columns.get(t)[1]].replace(',', '.')));
                            dataSets.get(t).add(data);
                        }
                    } else {
                        if (line[columns.get(t)[0]].length() > 0) {
                            data.setValue(Double.valueOf(line[headers.getValueColumnIdForType(t)].replace(',', '.')));
                            dataSets.get(t).add(data);
                        }
                    }
                }
                if (data == null) {
                    log.error("Unable to read line " + id, line);
                }
                id++;
            }
        }
        orderDataSetsByDate();
    }

//    Currently not used
//    /**
//     * Get the read data for a specific type.
//     * @param type The type of the wanted data
//     * @return The DataSet which only contains a Type
//     */
//    public DataSet getDataFor(Type type){
//        return dataSets.get(type);
//    }

    /**
     * Get all sets of Data.
     *
     * @return The HashMap of non null DataSet ordered by Type
     */
    public HashMap<Type, DataSet> getDataSets() {
        HashMap<Type, DataSet> returned = new HashMap<>(dataSets.size());
        for (Type t : dataSets.keySet())
            if (dataSets.get(t).size() > 0) returned.put(t, dataSets.get(t));
        return returned;
    }

    /**
     * Force each set to be ordered by Date.
     */
    private void orderDataSetsByDate() {
        for (Type type : dataSets.keySet()) {
            dataSets.get(type).orderByDate();
        }
    }

    /**
     * Helper for read headers.
     * <p>It's Helper is designed to get which data is stored in the file and in which column.</p>
     */
    private static class HeadersList extends ArrayList<String> {

        /**
         * Define conditions to determine if a column contains a type of data.
         * <p>Conditions are an array of strings. On index 0, the condition for value column, on index 1 and 2
         * conditions for min and max column. An null entry define that the value or min, max is not available fo
         * this Type of data.</p>
         */
        private static HashMap<Type, String[]> headerConditions = new HashMap<>();

        static {   // Write conditions for each type
            headerConditions.put(Type.PRESSURE, new String[]{"Pression"});
            headerConditions.put(Type.TEMPERATURE, new String[]{"Moy. : Température, °C"});
            headerConditions.put(Type.TEMPERATURE_MIN_MAX, new String[]{null, "Min. : Température, °C", "Max. : Température, °C"});
            headerConditions.put(Type.WATER, new String[]{"Pluvio"});
        }

        /**
         * Condition for date column.
         */
        private final String dateColumn = "Date";
        /**
         * Condition for hour column
         */
        private final String timeColumn = "Heure";

        /**
         * Determine which Type of Data are available.
         *
         * @return The list of available data
         */
        public ArrayList<Type> getAvailableTypes() {
            ArrayList<Type> list = new ArrayList<>(headerConditions.size());
            for (Type type : headerConditions.keySet())
                if (hasType(type)) list.add(type);
            return list;
        }

        /**
         * Determine if the header has got a type of data.
         *
         * @param t The type to check
         * @return true if data is available
         */
        public boolean hasType(Type t) {
            if (!headerConditions.keySet().contains(t)) return false;
            if (headerConditions.get(t).length > 0) {
                if (isMinMaxType(t)) {
                    boolean hasGotMin = false, hasGotMax = false;
                    for (String c : this) {
                        if (c.contains(headerConditions.get(t)[1])) hasGotMin = true;
                        if (c.contains(headerConditions.get(t)[2])) hasGotMax = true;
                    }
                    return hasGotMax && hasGotMin;
                }
                for (String c : this)
                    if (c.contains(headerConditions.get(t)[0])) return true;
            }
            return false;
        }

        /**
         * Determine the column id for a type.
         *
         * @param t The type
         * @return The index of type or -1 if it's not found.
         * @throws IllegalStateException if t is not a type which has got a single value
         */
        public int getValueColumnIdForType(Type t) {
            if (isMinMaxType(t)) throw new IllegalStateException(t + " is not a single value type");
            if (!hasType(t)) return -1;
            for (int i = 0; i < this.size(); i++)
                if (this.get(i).contains(headerConditions.get(t)[0])) return i;
            return -1;
        }

        /**
         * Determine the columns ids for a type with min and max values.
         *
         * @param type The type
         * @return Indexes in an array or Array of -1 if it's not found.
         * @throws IllegalStateException if t is not a type which has got a single value
         */
        public int[] getMinMaxValueColumnIdForType(Type type) {
            if (!isMinMaxType(type)) throw new IllegalStateException(type + " is not a min/max value type");
            int[] ids = {-1, -1};
            if (!hasType(type)) return ids;
            for (int i = 0; i < this.size(); i++) {
                String c = this.get(i);
                if (c.contains(headerConditions.get(type)[1])) ids[0] = i;
                if (c.contains(headerConditions.get(type)[2])) ids[1] = i;
            }
            return ids;
        }

        /**
         * Determine if a Type has only a Minimal and Maximal values.
         *
         * @param type The type to check
         * @return true if type has only minimal and maximal columns.
         */
        public boolean isMinMaxType(Type type) {
            return (headerConditions.get(type).length > 0) && headerConditions.get(type)[0] == null;
        }

        /**
         * Determine the Date column id
         *
         * @return the id or -1 if it's not found
         */
        public int getDateColumnId() {
            for (int i = 0; i < this.size(); i++)
                if (this.get(i).contains(dateColumn)) return i;
            return -1;
        }

        /**
         * Determine the Time column id
         *
         * @return the id or -1 if it's not found
         */
        public int getTimeColumnId() {
            for (int i = 0; i < this.size(); i++)
                if (this.get(i).contains(timeColumn)) return i;
            return -1;
        }

        /**
         * Setup a constructor.
         *
         * @param i Number of header columns.
         * @see ArrayList
         */
        public HeadersList(int i) {
            super(i);
        }

    }
}
