package org.cds06.speleograph.data;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class SpeleoFileReader {

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public static Series[] readFile(File file) throws IOException, ParseException {
        CSVReader reader = new CSVReader(new FileReader(file), ';', '"');
        int lineToStart = 0;
        String[] line;
        while ((line = reader.readNext()) != null) {
            if (line.length > 1) {
                lineToStart++;
                break;
            }
            lineToStart++;
        }
        final HashMap<Type, Integer[]> map = readHeaders(line);
        return readFile(
                file, map.keySet().toArray(new Type[1]), map.values().toArray(new Integer[0][]), lineToStart,
                readDateHeaders(line));
    }

    public static Series[] readFile(
            final File file,
            final Type[] types,
            final Integer[][] columns,
            final int lineToStart,
            final DateInformation dateInformation
    ) throws IOException, ParseException {

        // Validates Data
        {
            Validate.notNull(dateInformation);
            Validate.notNull(lineToStart);
            Validate.notEmpty(types, "Types can not be empty");
            Validate.notEmpty(columns, "Columns can not be empty");
            Validate.isTrue(columns.length == types.length, "Columns length and type length are not the sames");
        }

        // Setup series
        final int numberOfSeries = types.length;
        final Series[] seriesArray = new Series[numberOfSeries];
        for (int i = 0; i < numberOfSeries; i++) {
            seriesArray[i] = new Series(file);
            seriesArray[i].setSet(DataSet.getDataSet(types[i]));
        }

        // Create a Reader
        CSVReader reader = new CSVReader(new FileReader(file), ';', '"');
        int lineNumber = 0;
        String[] line;

        // Read line by line
        while ((line = reader.readNext()) != null) {
            lineNumber++;
            if (lineNumber < (lineToStart + 1)) continue;
            Date date = dateInformation.parse(line);
            for (int i = 0, max = types.length; i < max; i++) {
                Integer[] columnIds = columns[i];
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
                seriesArray[i].getItems().add(item);
            }
        }

        // Return the extracted series
        return seriesArray;
    }

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
                new String[]{"Min. : Température, °C", "Max. : Température, °C"}
        );
        headerConditions.put(Type.WATER, new String[]{"Pluvio"});
    }

    private static HashMap<Type, Integer[]> readHeaders(String[] line) {
        HashMap<Type, Integer[]> typeAndColumns = new HashMap<>();
        for (int i = 0; i < line.length; i++) {
            String columnName = line[i];
            for (Type t : headerConditions.keySet()) {
                String[] conditions = headerConditions.get(t);
                for (int j = 0, conditionsLength = conditions.length; j < conditionsLength; j++) {
                    String s = conditions[j];
                    if (columnName.contains(s)) {
                        Integer[] values;
                        if (typeAndColumns.get(t) == null)
                            values = new Integer[headerConditions.get(t).length];
                        else values = typeAndColumns.get(t);
                        values[j] = i;
                        typeAndColumns.put(t, values);
                    }
                }
            }
        }
        return typeAndColumns;
    }

    private static DateInformation readDateHeaders(String[] line) {
        int[] columnsForDate = new int[2];
        for (int i = 0; i < line.length; i++) {
            if (line[i].contains("Date")) columnsForDate[0] = i;
            else if (line[i].contains("Heure")) columnsForDate[1] = i;
        }
        return new DateInformation(columnsForDate);
    }

    public static class DateInformation {

        private int[] columnsToJoin = new int[]{};
        private String dateFormat = "dd/MM/yyyy HH:mm:ss";
        private SimpleDateFormat format = new SimpleDateFormat(dateFormat);

        public DateInformation(int[] columnsToJoin) {
            this.columnsToJoin = columnsToJoin;
        }

        public DateInformation(int[] columnsToJoin, String dateFormat) {
            this.columnsToJoin = columnsToJoin;
            this.dateFormat = dateFormat;
            format = new SimpleDateFormat(this.dateFormat);
        }

        public Date parse(String[] line) {
            String dateInAString = "";
            for (int i = 0, columnsToJoinLength = columnsToJoin.length; i < columnsToJoinLength; i++) {
                int column = columnsToJoin[i];
                dateInAString += line[column];
                if (i != columnsToJoinLength - 1) dateInAString += " ";
            }
            try {
                return format.parse(dateInAString);
            } catch (ParseException e) {
                throw new IllegalStateException("Can not parse a date !", e);
            }
        }
    }

//    public static void main(String[] args) {
//        String demo = "C:\\Users\\PhilippeGeek\\Dropbox\\CDS06 Comm Scientifique\\Releves-Instruments\\Pluvio Villebruc\\2315774_9-tous.txt";
//        try {
//            long t0 = System.currentTimeMillis();
//            Series[] series = SpeleoFileReader.readFile(new File(demo));
//            System.out.println(System.currentTimeMillis() - t0);
//            for (int i1 = 0, seriesLength = series.length; i1 < seriesLength; i1++) {
//                Series s = series[i1];
//                if (s.getType().equals(Type.WATER))
//                    series[i1] = Sampling.sampling(s, 86400000);
//            }
//            DataSet.pushSeries(series);
//            DataSet[] sets = new DataSet[]{DataSet.getDataSet(Type.TEMPERATURE), DataSet.getDataSet(Type.WATER)};
//            JFreeChart chart = ChartFactory.createTimeSeriesChart("Demo SpeleoGraph", "Temps", null, sets[0], true, true, false);
//            final XYPlot plot = chart.getXYPlot();
//            for (int i = 0, setsLength = sets.length; i < setsLength; i++) {
//                DataSet set = sets[i];
//                set.refresh();
//                final NumberAxis axis = set.getType().getAxis();
//                plot.setDataset(i, set);
//                plot.setRangeAxis(i, axis);
//                plot.setRenderer(i, new XYLineAndShapeRenderer(true, false));
//                plot.setRangeAxis(i, axis);
//                plot.mapDatasetToRangeAxis(i, i);
//                plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);
//            }
//            ChartFrame frame = new ChartFrame("SpeleoGraph Demo", chart);
//            frame.setSize(600, 300);
//            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//            frame.setVisible(true);
////                    System.out.println(Arrays.toString(DataSet.getDataSetInstances()));
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//        }
//    }

}
