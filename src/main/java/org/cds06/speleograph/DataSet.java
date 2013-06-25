package org.cds06.speleograph;

import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.util.ArrayList;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class DataSet extends ArrayList<Data> {

    private File source;
    private Data.Type type;
    private DataSetReader reader;
    private SimpleBooleanProperty shown=new SimpleBooleanProperty(this,"shown",false);

    public String getName(){
        String name=getSource().getName();
        name=name.substring(0,name.indexOf(".",name.length()-5));
        if(getType()!=null) switch (getType()) {
            case PRESSURE:
                name += " - Pression";
                break;
            case WATER:
                name += " - Précipitations";
                break;
            case TEMPERATURE:
                name += " - Température";
        }
        return name;
    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public Data.Type getType() {
        return type;
    }

    public void setType(Data.Type type) {
        this.type = type;
    }

    public DataSetReader getReader() {
        return reader;
    }

    public void setReader(DataSetReader reader) {
        this.reader = reader;
    }

    public boolean isShown() {
        return shownProperty().getValue();
    }

    public void setShown(boolean shown) {
        shownProperty().setValue(shown);
    }

    public SimpleBooleanProperty shownProperty(){
        return shown;
    }

}
