package org.cds06.speleograph;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable{
    private static final Logger log = LoggerFactory.getLogger(AppController.class);

    public ListView<String> openedList;

    public ObservableMap<String,List<Data>> dataSets=FXCollections.observableHashMap();
    {
        dataSets.addListener(new MapChangeListener<String, List<Data>>() {
            @Override
            public void onChanged(Change<? extends String, ? extends List<Data>> change) {
                log.info("DataSets changed :");
                for(String m:change.getMap().keySet())
                    log.info("- "+m);
            }
        });
    }

    private void process(DataSetReader dataSetReader) {

    }

    public void open() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv,*.txt)", "*.csv", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);
        if(file==null) return;

        try {
            DataSetReader reader = new DataSetReader(file);
            //dataLoaded.add(reader.getWater());
//            if(reader.getWater().size()>0){
//                String dataSetName = reader.getDataOriginFile().getName() + " - Water Amount";
//                dataSets.put(dataSetName,reader.getWater());
//            }
        } catch (Exception e) {
            log.error("Can not open file "+file.toString(),e);
        }

    }

    private void update() {

    }

    public void close() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
