package org.cds06.speleograph.data.fileio;

import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Item;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.data.Type;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Reader for files from the website Wunderground.
 *
 * <p>Sorry but this format is not documented</p>
 *
 * @author Philippe VIENNE
 */
public class WundergroundFileReader implements DataFileReader {

    private static final I18nSupport resourceBundle = new I18nSupport();

    /**
     * Wunderground Date Format.
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d H:m:s");

    /**
     * Logger for errors and information.
     */
    @NonNls
    private static final Logger log = LoggerFactory.getLogger(WundergroundFileReader.class);

    /**
     * List of CSV headers excepted into the file.
     */
    @SuppressWarnings("UnusedDeclaration")
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
    @SuppressWarnings("UnusedDeclaration")
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
     * @throws org.cds06.speleograph.data.fileio.FileReadingError
     */
    @Override
    public void readFile(File file) throws FileReadingError {
        log.debug("Start to read file "+file);
        try {
            FileReader fileReader = new FileReader(file);

            BufferedReader reader = new BufferedReader(fileReader);


            ArrayList<String> data = new ArrayList<>();
            String line,buffer="";
            while ((line=reader.readLine())!=null){
                buffer+=StringUtils.normalizeSpace(line);
                if(StringUtils.countMatches(buffer,",")>=16){
                    data.add(buffer);
                    buffer="";
                }
            }

            Series temperature = new Series(file, Type.TEMPERATURE); // Temperature
            Series pressure = new Series(file,Type.PRESSURE); // Pressure
            Series water = new Series(file,Type.WATER); // Water

            for(String d:data){
                String[] lineSplit = StringUtils.splitPreserveAllTokens(d,',');
                try{
                    Date date = dateFormat.parse(lineSplit[15]);
                    temperature.add(new Item(temperature,date,Double.valueOf(lineSplit[TEMPERATURE_COLUMN])));
                    pressure.add(new Item(pressure,date,Double.valueOf(lineSplit[PRESSURE_COLUMN])));
                    water.add(new Item(water,date,Double.valueOf(lineSplit[WATER_COLUMN])));
                } catch(Exception e){
                    e.printStackTrace(System.err);
                }
            }

        } catch (IOException e) {
            throw new FileReadingError("I/O Exception : Can not read this file !",FileReadingError.Part.HEAD,e);
        }
        log.debug("Ended to read file "+file);
    }

    @Override
    public String getName() {
        return "Wunderground";
    }

    @Override
    public String getButtonText() {
        return resourceBundle.getString("actions.import.wunderground");
    }

    @NotNull
    @Override
    public IOFileFilter getFileFilter() {
        return FileFileFilter.FILE;
    }
}
