package org.cds06.speleograph;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
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

    private void process(DataSetReader dataSetReader) {

    }

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

    private ObservableList<DataSet> data = FXCollections.observableArrayList();

    {
        data.addListener(new ListChangeListener<DataSet>() {
            @Override
            public void onChanged(Change<? extends DataSet> change) {

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
                                ObservableValue<Boolean> booleanObservableValue=datas.shownProperty();
                                if(!datas.observed) {
                                    booleanObservableValue.addListener(new ChangeListener<Boolean>() {
                                        @Override
                                        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean before, Boolean after) {
                                            update(datas,after);
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
    }

    private void update(DataSet data, Boolean after) {

    }

}
