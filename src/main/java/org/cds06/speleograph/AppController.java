package org.cds06.speleograph;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AppController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(AppController.class);

    public ListView<DataSet> openedList;
    public LineChart<Date,Number> chart;


    private ObservableList<XYChart.Series<Date, Number>> chartList=FXCollections.observableArrayList();

    private Property<ObservableList<XYChart.Series<Date, Number>>> shownLists=new ObjectPropertyBase<ObservableList<XYChart.Series<Date, Number>>>(chartList) {
        @Override
        public Object getBean() {
            return AppController.this;
        }

        @Override
        public String getName() {
            return "Shown Lists";
        }
    };

    public void open() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv,*.txt)", "*.csv", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ReefNet CSV files (*.csv,*.txt)", "*.csv", "*.txt"));

        // For tests only
        fileChooser.setInitialDirectory(new File("C:\\Users\\PhilippeGeek\\Dropbox\\CDS06 Comm Scientifique\\Releves-Instruments\\Pluvio Villebruc"));

        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;
        if (openedFiles.contains(file)) return; // We not reopen an opened file

        File openedFile = file;

        if (ReefnetFileConverter.isReefnetFile(file)) { // We must convert it before using
            try {
                ReefnetFileConverter converter = new ReefnetFileConverter(file);
                converter.convert();
                file = converter.getCsvTempFile();
            } catch (IOException e) {
                log.error("Can not convert the ReefNet file, we stop the action.", e);
                return;
            }
        }

        try {
            DataSetReader reader = new DataSetReader(file);
            data.addAll(reader.getDataSets().values());
            openedFiles.add(openedFile);
        } catch (Exception e) {
            log.error("Can not open file " + file.toString(), e);
        }

    }

    private ArrayList<File> openedFiles = new ArrayList<>();

    private ObservableMap<XYChart.Series<Date,Number>,ObservableBooleanValue> chartSeries=FXCollections.observableHashMap();

    private ObservableList<DataSet> data = FXCollections.observableArrayList();

    {
        data.addListener(new ListChangeListener<DataSet>() {
            @Override
            public void onChanged(Change<? extends DataSet> change) {
               for(DataSet set:change.getList()){
                    chartSeries.put(set.getSeriesForChart(),set.shownProperty());
               }
            }
        });
    }

    public void close() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        openedList.setItems(data);
        openedList.setCellFactory(
                CheckBoxListCell.forListView(
                        new Callback<DataSet, ObservableValue<Boolean>>() {
                            @Override
                            public ObservableValue<Boolean> call(final DataSet datas) {
                                final BooleanProperty booleanObservableValue=datas.shownProperty();
                                if(!datas.observed) {
                                    booleanObservableValue.addListener(new ChangeListener<Boolean>() {
                                        @Override
                                        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean before, Boolean after) {
                                            if(before){
                                                // We have to hide the series
                                                hide((DataSet)booleanObservableValue.getBean());
                                            }else{
                                                // We have to show the series
                                                show((DataSet) booleanObservableValue.getBean());
                                            }
                                        }
                                    });
                                    datas.observed = true;
                                }
                                return booleanObservableValue;
                            }
                        },
                        new StringConverter<DataSet>() {
                            @Override
                            public String toString(DataSet datas) {
                                return datas.getName();
                            }

                            @Override
                            public DataSet fromString(String s) {
                                for(DataSet d:data)
                                    if(d.getName().equals(s)) return d;
                                return null;
                            }
                        }
                ));
        chart.setAnimated(false);
    }

    private void hide(DataSet dataSet) {
        XYChart.Series<Date,Number> linkedSeries = null;
        for(XYChart.Series series:chartSeries.keySet()){
            if(chartSeries.get(series)==dataSet.shownProperty())
                linkedSeries=series;
        }
        chart.getData().remove(linkedSeries);
    }

    private void show(DataSet dataSet) {
        XYChart.Series<Date,Number> linkedSeries = null;
        for(XYChart.Series series:chartSeries.keySet()){
            if(chartSeries.get(series)==dataSet.shownProperty()) {
                linkedSeries = series;
            }
        }
        if(linkedSeries==null) return;
        if(chart.getData().size()<=0) {
            ((DateAxis) chart.getXAxis()).setMinDate(dataSet.getDateRange().startProperty().get());
            ((DateAxis) chart.getXAxis()).setMaxDate(dataSet.getDateRange().endProperty().get());
        }
        chart.getData().add(linkedSeries);
        chart.layout();
    }

}
