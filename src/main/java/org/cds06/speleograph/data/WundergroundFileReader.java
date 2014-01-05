package org.cds06.speleograph.data;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
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
import java.util.Date;

/**
 * Reader for files from the website Wunderground.
 *
 * <p>Sorry but this format is not documented</p>
 *
 * @author Philippe VIENNE
 */
public class WundergroundFileReader implements DataFileReader{

    /**
     * Wunderground Date Format.
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-j H:m:s");

    /**
     * Logger for errors and information.
     */
    @NonNls
    private static final Logger log = LoggerFactory.getLogger(WundergroundFileReader.class);

    /**
     * List of CSV headers excepted into the file.
     */
    private static final String[] headers = new String[]{"Time","TemperatureC","DewpointC","PressurehPa",
            "WindDirection","WindDirectionDegrees","WindSpeedKMH","WindSpeedGustKMH","Humidity","HourlyPrecipMM",
            "Conditions","Clouds","dailyrainMM","SolarRadiationWatts/m^2","SoftwareType","DateUTC"};

    /**
     * Column index for pressure.
     */
    private static final int PRESSURE_COLUMN = 3;

    /**
     *
     */
    private static final int TIME_COLUMN = 0;

    /**
     *
     */
    private static final int TEMPERATURE_COLUMN = 1;

    /**
     *
     */
    private static final int WATER_COLUMN = 9; // TODO : OR 12 ??

    /**
     *
     * @param file The file to open
     * @throws FileReadingError
     */
    @Override
    public void readFile(File file) throws FileReadingError {
        log.debug("Start to read file "+file);
        try {
            CSVReader reader = new CSVReader(new FileReader(file));

            // Check if the file is correctly formatted
            String[] readNext = reader.readNext();
            for (int i = 0; i < readNext.length; i++) {
                String entry = readNext[i];
                if(!entry.equals(headers[i]))
                    throw new FileReadingError("File format is not valid !", FileReadingError.Part.HEAD);
            }

            Series temperature = new Series(file,Type.TEMPERATURE);
            Series pressure = new Series(file,Type.PRESSURE);
            Series water = new Series(file,Type.WATER);

            while ((readNext=reader.readNext())!=null){
                Date date = dateFormat.parse(readNext[TIME_COLUMN]);
                temperature.add(new Item(temperature,date,Double.valueOf(readNext[TEMPERATURE_COLUMN])));
                pressure.add(new Item(pressure,date,Double.valueOf(readNext[PRESSURE_COLUMN])));
                water.add(new Item(water,date,Double.valueOf(readNext[WATER_COLUMN])));
            }

        } catch (IOException e) {
            throw new FileReadingError("I/O Exception : Can not read this file !",FileReadingError.Part.HEAD,e);
        } catch (ParseException e) {
            throw new FileReadingError("I/O Exception : Can not read a time !",FileReadingError.Part.DATA,e);
        }
        log.debug("Ended to read file "+file);
    }

    @Override
    public String getName() {
        return "Wunderground";
    }

    @Override
    public String getButtonText() {
        return "Wunderground";
    }

    @NotNull
    @Override
    public IOFileFilter getFileFilter() {
        return FileFileFilter.FILE;
    }
}
