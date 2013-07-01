package org.cds06.speleograph;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */

import au.com.bytecode.opencsv.CSVReader;
import org.cds06.speleograph.Data.Type;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
            dataSets.put(type, new DataSet(this, type));
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
        boolean headersRead=false;
        Type[] availableTypes = new Type[]{};
        int[][] columns = new int[][]{};
        int dateColumn = -1, timeColumn = -1;
        while ((line = csvReader.readNext()) != null) {
            if (line.length <= 1) {                            // Title Line
                if (line.length != 0) setTitle(line[0]);
                continue;
            }
            if (!headersRead) {                         // The first line is headers
                Headers headers= Headers.parseHeaderLine(line);
                availableTypes = headers.availableTypes;
                columns = headers.typeColumns;
                dateColumn = headers.dateColumns[0];
                timeColumn = headers.dateColumns[1];
                headersRead=(dateColumn!=-1&&timeColumn!=-1&&availableTypes.length>0);
                if(!headersRead) LoggerFactory.getLogger(getClass()).error("Error while parsing",line);
                continue;
            }                                         // An data line
            Date day;
            try {
                day = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(line[dateColumn] + " " + line[timeColumn]);
            } catch (ParseException e) {
                day = GregorianCalendar.getInstance().getTime();
            }
            for (int i = 0; i < availableTypes.length; i++) {
                if (line[columns[i][0]].length() > 0) {
                    if (columns[i][1] != -1) {
                        if (line[columns[i][1]].length() > 0) {
                            new Data(
                                    dataSets.get(availableTypes[i]),
                                    availableTypes[i],
                                    day,
                                    Double.valueOf(line[columns[i][0]].replace(',', '.')),
                                    Double.valueOf(line[columns[i][1]].replace(',', '.'))
                            );
                        }
                    } else {
                        if (line[columns[i][0]].length() > 0) {
                            new Data(
                                    dataSets.get(availableTypes[i]),
                                    availableTypes[i],
                                    day,
                                    Double.valueOf(line[columns[i][0]].replace(',', '.'))
                            );
                        }
                    }
                }
            }
        }
    }

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
        private static HashMap<Type, String[]> headerConditions = new HashMap<>();

        static {   // Write conditions for each type
            headerConditions.put(Type.PRESSURE, new String[]{"Pression"});
            headerConditions.put(Type.TEMPERATURE, new String[]{"Moy. : Température, °C"});
            headerConditions.put(
                    Type.TEMPERATURE_MIN_MAX,
                    new String[]{null, "Min. : Température, °C", "Max. : Température, °C"}
            );
            headerConditions.put(Type.WATER, new String[]{"Pluvio"});
        }

        /**
         * Condition for date column.
         */
        private static final String dateColumn = "Date";
        /**
         * Condition for hour column
         */
        private static final String timeColumn = "Heure";

        public enum HEADER_TYPE{
            AVAILABLE_TYPES,
            COLUMNS_FOR_AVAILABLE_TYPES,
            DATE_COLUMNS
        }

        /**
         * Parse all data into a header line.
         * @return Header Data stored in an HashMap by Header_Type
         */
        public static Headers parseHeaderLine(String[] line){
            Headers headers = new Headers();
            ArrayList<Type> availableTypes=new ArrayList<>();
            ArrayList<int[]> columns=new ArrayList<>();
            Type t;
            for(int i=0;i<headerConditions.size();i++){
                t= (Type) headerConditions.keySet().toArray()[i];
                String[] strings = (String[]) headerConditions.values().toArray()[i];
                for (int condition = 0, stringsLength = (strings).length; condition < stringsLength; condition++) {
                    String s = strings[condition];
                    if(s==null) continue;
                    s=s.toLowerCase();
                    if(condition==0){
                        for (int columnId = 0, lineLength = line.length; columnId < lineLength; columnId++) {
                            String l = line[columnId];
                            if (l.toLowerCase().contains(s)) {
                                availableTypes.add(t);
                                columns.add(new int[]{columnId,-1});
                                columnId=lineLength;
                            }
                        }
                    } else if(condition==1) {
                        int[] columnsFound=new int[]{-1,-1};
                        String condition2=strings[condition+1].toLowerCase();
                        for (
                                int columnId = 0, lineLength = line.length;
                                columnId < lineLength;
                                columnId++) {
                            String l = line[columnId].toLowerCase();
                            if (l.contains(s)) {
                                columnsFound[0]=columnId;
                            } else if(l.contains(condition2))
                                columnsFound[1]=columnId;
                            if(columnsFound[0]!=-1&&columnsFound[1]!=-1){
                                availableTypes.add(t);
                                columns.add(columnsFound);
                                columnId=lineLength;
                            }
                        }
                        condition=stringsLength;
                    }
                }
            }
            headers.availableTypes=availableTypes.toArray(new Type[availableTypes.size()]);
            headers.typeColumns=columns.toArray(new int[availableTypes.size()][2]);
            headers.dateColumns=new int[2];
            for (int i = 0, lineLength = line.length; i < lineLength; i++) {
                String l = line[i];
                if(l.contains(dateColumn)) headers.dateColumns[0]=i;
                if(l.contains(timeColumn)) headers.dateColumns[1]=i;
            }
            return headers;
        }

        public Type[] availableTypes;
        public int[][] typeColumns;
        public int[] dateColumns;

    }
}
