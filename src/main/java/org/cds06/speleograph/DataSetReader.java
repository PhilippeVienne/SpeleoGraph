package org.cds06.speleograph;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */

import au.com.bytecode.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Design a DataSet model.
 * A Data set contains information about something. The data is stored as date belongs to one data.
 * An DataSet come from a CSV file
 */
public class DataSetReader {

    private static final Logger log = LoggerFactory.getLogger(DataSetReader.class);

    private String title=null;
    private File dataOriginFile=null;
    private ArrayList<Data> readData=new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public File getDataOriginFile() {
        return dataOriginFile;
    }

    public void setDataOriginFile(File dataOriginFile) {
        this.dataOriginFile = dataOriginFile;
    }

    protected void read() throws IOException {
        CSVReader csvReader=new CSVReader(new FileReader(getDataOriginFile()),';');
        String[] line;
        HeadersList headers=null;
        int id=0;
        while((line=csvReader.readNext())!=null){
            if(line.length<=1) {                            // Title Line
                if(line.length!=0) setTitle(line[0]);
            }
            else if(headers==null){                         // The first line is headers
                headers=new HeadersList(line.length);
                Collections.addAll(headers, line);
            } else {                                        // An data line
                String date = line[headers.dateColumnId()] + " " + line[headers.hourColumnId()],
                        format = "dd/MM/yyyy HH:mm:ss";
                if(headers.containsPluvio()){
                    Data data=new Data();
                    data.setDataType(Data.Type.WATER);
                    data.setDate(date,format);
                    data.setValue(Double.valueOf(line[headers.pluvioColumnId()].replace(',', '.')));
                    readData.add(data);
                } else {
                    log.error("Unable to read line "+id,line);
                }
                id++;
            }
        }
    }

    private class HeadersList extends ArrayList<String> {

        public boolean containsPluvio(){
            for(String s:this)
                if(s.contains("Pluvio")) return true;
            return false;
        }

        public int pluvioColumnId(){
            if(!containsPluvio()) return -1;
            for(int i=0;i<this.size();i++)
                if(this.get(i).contains("Pluvio")) return i;
            return -1;
        }

        public int dateColumnId(){
            for(int i=0;i<this.size();i++)
                if(this.get(i).contains("Date")) return i;
            return -1;
        }

        public int hourColumnId(){
            for(int i=0;i<this.size();i++)
                if(this.get(i).contains("Heure")) return i;
            return -1;
        }

        public HeadersList(int i){
            super(i);
        }

    }

    static public void main(String[] args){
        DataSetReader reader=new DataSetReader();
        reader.setDataOriginFile(new File("C:\\Users\\PhilippeGeek\\Dropbox\\CDS06 Comm Scientifique\\Releves-Instruments\\Pluvio Villebruc\\2315774_9-pluvio.txt"));
        try {
            reader.read();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
