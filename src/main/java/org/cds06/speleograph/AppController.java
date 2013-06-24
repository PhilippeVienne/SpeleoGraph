package org.cds06.speleograph;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AppController implements Initializable{
    private static final Logger log = LoggerFactory.getLogger(AppController.class);

    public ListView<String> openedList;

    public ObservableList<DataSetReader> data;

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

    }

    public void close() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DataSetReader test=new DataSetReader();
        test.setDataOriginFile(new File("C:\\Users\\PhilippeGeek\\Dropbox\\CDS06 Comm Scientifique\\Releves-Instruments\\Pluvio Villebruc\\2315774_9-pluvio.txt"));
        ObservableList<String> files = FXCollections.observableArrayList("Single", "Double", "Suite", "Family App");
        openedList.setItems(files);
        openedList.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>(){

            @Override
            public ObservableValue<Boolean> call(String s) {
                return new SimpleBooleanProperty(false);  //To change body of implemented methods use File | Settings | File Templates.
            }
        }));
    }
}
