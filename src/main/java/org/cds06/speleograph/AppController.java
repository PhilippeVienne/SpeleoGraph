package org.cds06.speleograph;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;
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

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private Label messageLabel;

    public void sayHello() {

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        StringBuilder builder = new StringBuilder();

        if (!StringUtils.isEmpty(firstName)) {
            builder.append(firstName);
        }

        if (!StringUtils.isEmpty(lastName)) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(lastName);
        }

        if (builder.length() > 0) {
            String name = builder.toString();
            log.debug("Saying hello to " + name);
            messageLabel.setText("Hello " + name);
        } else {
            log.debug("Neither first name nor last name was set, saying hello to anonymous person");
            messageLabel.setText("Hello mysterious person");
        }
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
            if(reader.getWater().size()>0){
                String dataSetName = reader.getDataOriginFile().getName() + " - Water Amount";
                dataSets.put(dataSetName,reader.getWater());
            }
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
