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

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Writer for SpeleoGraph's Files.
 */
public class SpeleoFileWriter {

    @NonNls
    private static final Logger log = LoggerFactory.getLogger(SpeleoFileWriter.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.###");
    private FileWriterWithEncoding writer;
    private Integer allocatedColumns = 0;

    /**
     * Save all SpeleoGraph State in a .speleo File.
     *
     * @param destination The destination file, if it don't end with ".speleo", the name is edited.
     * @return true On success
     * @throws java.io.IOException On read/write errors.
     */
    public boolean save(File destination) throws IOException {
        if (!destination.getName().endsWith(".speleo")) {// NON-NLS
            destination = new File(destination.getAbsolutePath() + ".speleo");
        }
        writer = new FileWriterWithEncoding(destination, "UTF-8"); //NON-NLS
        List<Series> series = Series.getInstances();
        Integer[][] columns = new Integer[series.size()][];
        allocatedColumns = 0;
        write(SpeleoFileReader.SPELEOGRAPH_FILE_HEADER);
        write("headers"); // NON-NLS
        writeHeaders(series, columns);
        write("data"); // NON-NLS
        writeSeries(series, columns);
        write("eof"); // NON-NLS
        return true;
    }

    private Integer writeHeaders(List<Series> series, Integer[][] columns) {
        write("date", "", Integer.toString(allocatedColumns), "d/M/y H:m:s"); // NON-NLS
        allocatedColumns++;
        for (Series s : series) {
            String[] seriesDescriptor = {
                    Integer.toString(allocatedColumns),
                    s.getType().getName(),
                    s.getType().getUnit()
            };
            if (s.getType().isHighLowType()) {
                seriesDescriptor = ArrayUtils.add(seriesDescriptor, "min-max:1"); // NON-NLS
                seriesDescriptor = ArrayUtils.add(seriesDescriptor,
                        "min:" + Integer.toString(allocatedColumns)); // NON-NLS
                seriesDescriptor = ArrayUtils.add(seriesDescriptor,
                        "max:" + Integer.toString(allocatedColumns + 1)); // NON-NLS
                columns[series.indexOf(s)] = new Integer[]{allocatedColumns, allocatedColumns + 1};
                allocatedColumns++;
                allocatedColumns++;
            } else {
                columns[series.indexOf(s)] = new Integer[]{allocatedColumns};
                allocatedColumns++;
            }
            if (s.isShow())
                seriesDescriptor = ArrayUtils.add(seriesDescriptor, "show:1");
            if (s.getColor() != null)
                seriesDescriptor = ArrayUtils.add(seriesDescriptor, "color:" + s.getColor().getRGB());
            if (s.getType().isSteppedType())
                seriesDescriptor = ArrayUtils.add(seriesDescriptor, "stepped:1"); // NON-NLS
            if (s.getStyle() != null) {
                seriesDescriptor = ArrayUtils.add(seriesDescriptor, "style:" + s.getStyle().toString()); // NON-NLS
            }
            write(seriesDescriptor);
        }
        return allocatedColumns;
    }

    private void writeSeries(List<Series> series, Integer[][] columns) {
        for (Series s : series) {
            int seriesId = series.indexOf(s);
            switch (columns[seriesId].length) {
                case 1:
                    writeColumn(columns[seriesId][0], s);
                    break;
                case 2:
                    writeMinMaxColumn(columns[seriesId], s);
                    break;
                default:

            }
        }
    }

    private void writeColumn(Integer integer, Series s) {
        int column = integer;
        for (Item i : s.getItems()) {
            String[] line = new String[allocatedColumns];
            line[0] = DATE_FORMAT.format(i.getDate());
            line[column] = DECIMAL_FORMAT.format(i.getValue());
            write(resetNotNullArray(line));
        }
    }

    private void writeMinMaxColumn(Integer[] column, Series s) {
        int minColumn = column[0], maxColumn = column[1];
        for (Item i : s.getItems()) {
            String[] line = new String[allocatedColumns];
            line[0] = DATE_FORMAT.format(i.getDate());
            line[minColumn] = DECIMAL_FORMAT.format(i.getLow());
            line[maxColumn] = DECIMAL_FORMAT.format(i.getHigh());
            write(resetNotNullArray(line));
        }
    }

    /**
     * Make sure that there is not null element in a String array.
     *
     * @param line The array which can contains null elements
     * @return A String array without null elements.
     */
    private static String[] resetNotNullArray(String[] line) {
        for (int i1 = 0; i1 < line.length; i1++) {
            if (line[i1] == null) line[i1] = "";
        }
        return line;
    }

    private void write(String... line) {
        final String lineToWrite = StringUtils.join(line, ';');
        try {
            writer.write(lineToWrite);
            writer.write("\n");
        } catch (IOException e) {
            log.error("Can not write line '" + lineToWrite + "'", e);
        }
    }

}
