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

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Writer for SpeleoGraph's Files.
 */
public class SpeleoFileWriter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    /**
     * Save all SpeleoGraph State in a .speleo File.
     * @param destination The destination file, if it don't end with ".speleo", the name is edited.
     * @return true On success
     * @throws java.io.IOException On read/write errors.
     */
    public boolean save(File destination) throws IOException{
        if(destination.getName().endsWith(".speleo")){// NON-NLS
            destination = new File(destination.getAbsolutePath()+".speleo");
        }
        CSVWriter writer = new CSVWriter(new FileWriter(destination),';');
        List<Series> series = Series.getInstances();
        Integer[][] columns = new Integer[series.size()][];
        Integer allocatedColumns = 0;
        writer.writeNext(new String[]{SpeleoFileReader.SPELEOGRAPH_FILE_HEADER});
        writer.writeNext(new String[]{"headers"}); // NON-NLS
        writeHeaders(writer, series, columns, allocatedColumns);
        writer.writeNext(new String[]{"data"}); // NON-NLS
        writeSeries(writer, series, columns);
        writer.writeNext(new String[]{""});
        return true;
    }

    private Integer writeHeaders(CSVWriter writer, List<Series> series, Integer[][] columns, Integer allocatedColumns) {
        writer.writeNext(new String[]{"date","",Integer.toString(allocatedColumns), "d/M/y H:m:s"}); // NON-NLS
        allocatedColumns++;
        for(Series s:series){
            String[] seriesDescriptor = {
                    Integer.toString(allocatedColumns),
                    s.getType().getName(),
                    s.getType().getUnit()
            };
            if(s.getType().isHighLowType()){
                seriesDescriptor = ArrayUtils.add(seriesDescriptor, "min-max:1"); // NON-NLS
                seriesDescriptor = ArrayUtils.add(seriesDescriptor,
                        "min:"+Integer.toString(allocatedColumns)); // NON-NLS
                seriesDescriptor = ArrayUtils.add(seriesDescriptor,
                        "max:"+Integer.toString(allocatedColumns+1)); // NON-NLS
                columns[series.indexOf(s)] = new Integer[]{allocatedColumns,allocatedColumns+1};
                allocatedColumns++;
                allocatedColumns++;
            } else {
                columns[series.indexOf(s)] = new Integer[]{allocatedColumns};
                allocatedColumns++;
            }
            if(s.getType().isSteppedType())
                seriesDescriptor = ArrayUtils.add(seriesDescriptor,"stepped:1"); // NON-NLS
            writer.writeNext(seriesDescriptor);
        }
        return allocatedColumns;
    }

    private void writeSeries(CSVWriter writer, List<Series> series, Integer[][] columns) {
        for(Series s:series){
            int seriesId = series.indexOf(s);
            switch (columns[seriesId].length){
                case 1:
                    writeColumn(writer, series, columns[seriesId][0], s);
                    break;
                case 2:
                    writeMinMaxColumn(writer, series, columns[seriesId], s);
                    break;
                default:

            }
        }
    }

    private void writeColumn(CSVWriter writer, List<Series> series, Integer integer, Series s) {
        int column = integer;
        for(Item i:s.getItems()){
            String[] line = new String[series.size()+1];
            line[0] = DATE_FORMAT.format(i.getDate());
            line[column] = Double.toString(i.getValue());
            writer.writeNext(resetNotNullArray(line));
        }
    }

    private void writeMinMaxColumn(CSVWriter writer, List<Series> series, Integer[] column, Series s) {
        int minColumn = column[0], maxColumn = column[1];
        for(Item i:s.getItems()){
            String[] line = new String[series.size()+1];
            line[0] = DATE_FORMAT.format(i.getDate());
            line[minColumn] = Double.toString(i.getLow());
            line[maxColumn] = Double.toString(i.getHigh());
            writer.writeNext(resetNotNullArray(line));
        }
    }

    /**
     * Make sure that there is not null element in a String array.
     * @param line The array which can contains null elements
     * @return A String array without null elements.
     */
    private static String[] resetNotNullArray(String[] line) {
        for (int i1 = 0; i1 < line.length; i1++) {
            if (line[i1] == null) line[i1] = "";
        }
        return line;
    }

}
