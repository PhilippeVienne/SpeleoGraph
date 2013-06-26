package org.cds06.speleograph;

import javafx.beans.binding.Binding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class DataSet extends ArrayList<Data> {

    public boolean observed=false;
    private File source;
    private Data.Type type;
    private DataSetReader reader;
    private SimpleBooleanProperty shown=new SimpleBooleanProperty(this,"shown",false);

    public String getName(){
        if(getReader().getDataOriginFile()==null) return this.toString();
        String name=getReader().getDataOriginFile().getName();
        name=name.substring(0,(name.indexOf(".",name.length()-5)!=-1?name.indexOf(".",name.length()-5):name.length()));
        if(getType()!=null) switch (getType()) {
            case PRESSURE:
                name += " - Pression";
                break;
            case WATER:
                name += " - Précipitations";
                break;
            case TEMPERATURE:
                name += " - Température";
            case TEMPERATURE_MIN_MAX:
                name += " - Température (Min/Max)";
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

    public SimpleBooleanProperty shownProperty(){
        return shown;
    }

    public void orderByDate(){
        for(int i=0;i<size()-1;i++){
            if(get(i).getDate().after(get(i+1).getDate())){
                Data d=get(i+1);
                set(i+1,get(i));
                set(i,d);
            }
        }
    }

    public DateRange getDateRange(){
        Date older= get(0).getDate(), newer= get(size()-1).getDate();
        for(Data d:this){
            if(d.getDate().before(older)) older=d.getDate();
            if(d.getDate().after(newer)) newer=d.getDate();
        }
        return new DateRange(older,newer);
    }

    public XYChart.Series<Date,Number> getSeriesForChart(){
        XYChart.Series<Date,Number> series=new XYChart.Series<>();
        orderByDate(); // Be sure to order by date
        series.setName(getName());
        for(Data data:this)
            series.getData().add(new XYChart.Data<Date, Number>(data.getDate(),data.getValue(),data));
        return series;
    }

}
