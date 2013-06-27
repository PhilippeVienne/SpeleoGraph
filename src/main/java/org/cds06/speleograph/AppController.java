package org.cds06.speleograph;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.cds06.speleograph.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(AppController.class);

    /**
     * List of opened files.
     * <p>This variable is used to know which files are already opened and parsed to not reopen them.</p>
     */
    private ArrayList<File> openedFiles = new ArrayList<>();

    /**
     * Current DataSets opened in this application.
     * <p>When a file is parsed, all DataSet read from it come here.</p>
     */
    private ObservableList<DataSet> data = FXCollections.observableArrayList();

    /**
     * Series which can be shown.
     * <p>Every series is associated to a boolean which determine if a Series is currently shown on graph area.</p>
     */
    private ObservableMap<XYChart.Series<Date, Number>, ObservableBooleanValue> chartSeries = FXCollections.observableHashMap();

    /**
     * List of opened dataSets.
     */
    @FXML
    public ListView<DataSet> openedList;

    /**
     * The actual chart area.
     */
    @FXML
    public LineChart<Date, Number> chart;

    /**
     * Open a new File in the Application.
     * <p>This function is the entry point if a button want to open a new file</p>
     * <p>
     * In this function, we do the following :
     * <ul>
     * <li>Ask to the user a .csv or .txt file to open</li>
     * <li>Check if it's not already opened</li>
     * <li>Convert it if it's a Reefnet File</li>
     * <li>Read the file</li>
     * <li>Add the read data to the {@link #data}</li>
     * <li>Add the file to the {@link #openedList}</li>
     * </ul>
     * </p>
     */
    @FXML
    public void open() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv,*.txt)", "*.csv", "*.txt"));

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

    {   // Add listener to create the Chart Series when a new data is added.
        data.addListener(new ListChangeListener<DataSet>() {
            @Override
            public void onChanged(Change<? extends DataSet> change) {
                for (DataSet set : change.getList()) {
                    chartSeries.put(set.getSeriesForChart(), set.shownProperty());
                }
            }
        });
    }

//    Not used :
//    /**
//     * Close the application.
//     */
//    @FXML
//    public void close() {
//        System.exit(0); // A little much violent
//    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        openedList.setItems(data);
        openedList.setCellFactory(
                CheckBoxListCell.forListView(
                        new Callback<DataSet, ObservableValue<Boolean>>() {
                            @Override
                            public ObservableValue<Boolean> call(final DataSet datas) {
                                final BooleanProperty booleanObservableValue = datas.shownProperty();
                                if (!datas.observed) {
                                    booleanObservableValue.addListener(new ChangeListener<Boolean>() {
                                        @Override
                                        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean before, Boolean after) {
                                            if (before) {
                                                // We have to hide the series
                                                hide((DataSet) booleanObservableValue.getBean());
                                            } else {
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
                                for (DataSet d : data)
                                    if (d.getName().equals(s)) return d;
                                return null;
                            }
                        }
                ));
    }

    /**
     * Hide a DataSet on the chart.
     *
     * @param dataSet The dataSet to hide
     */
    private void hide(DataSet dataSet) {
        XYChart.Series<Date, Number> linkedSeries = null;
        for (XYChart.Series<Date, Number> series : chartSeries.keySet()) {
            if (chartSeries.get(series) == dataSet.shownProperty())
                linkedSeries = series;
        }
        chart.getData().remove(linkedSeries);
    }

    /**
     * Show a DataSet on the chart.
     * <p>If the user has not defined a {@link DateRange} and the DataSet will be the onlyone on the graph the DateRange
     * is defined to be the minimal and maximal date from the DataSet</p>
     *
     * @param dataSet The dataSet to show
     */
    private void show(DataSet dataSet) {
        XYChart.Series<Date, Number> linkedSeries = null;
        for (XYChart.Series<Date, Number> series : chartSeries.keySet()) {
            if (chartSeries.get(series) == dataSet.shownProperty()) {
                linkedSeries = series;
            }
        }
        if (linkedSeries == null) return;
        if (chart.getData().size() <= 0) {
            ((DateAxis) chart.getXAxis()).setMinDate(dataSet.getDateRange().startProperty().get());
            ((DateAxis) chart.getXAxis()).setMaxDate(dataSet.getDateRange().endProperty().get());
        }
        chart.getData().add(linkedSeries);
        chart.layout();
    }

    /**
     * Open a popup to let the user define a start point for DateAxis.
     */
    @FXML
    public void setStart(ActionEvent event) {
        final Stage stage = new Stage();

        VBox parent = new VBox();
        parent.setSpacing(5);
        parent.setPadding(new Insets(5));

        final DatePicker datePicker = new DatePicker();
        datePicker.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
        datePicker.setShowWeeks(false);
        parent.getChildren().add(datePicker);

        final Button button = new Button("Valider");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LoggerFactory.getLogger(AppController.class).debug("Set start date: " + datePicker.getValue());
                ((DateAxis) chart.getXAxis()).setMinDate(datePicker.getValue());
                chart.layout();
                stage.close();
            }
        });
        parent.getChildren().add(button);

        Scene scene = new Scene(parent);

        stage.setScene(scene);
        stage.setTitle("Date selection");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());
        stage.show();
    }

    /**
     * Open a popup to let the user define a start point for DateAxis.
     */
    @FXML
    public void setEnd() {

    }
}
