package org.cds06.speleograph;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
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

    // Not used and should never be reused
//    /**
//     * Series which can be shown.
//     * <p>Every series is associated to a boolean which determine if a Series is currently shown on graph area.</p>
//     */
//    private ObservableMap<XYChart.Series<Date, Number>, ObservableBooleanValue> chartSeries = FXCollections.observableHashMap();

    /**
     * List of opened dataSets.
     */
    @FXML
    public ListView<DataSet> openedList;

    /**
     * The actual chart area.
     */
    @FXML
    public SpeleoChart chart;
    private SimpleBooleanProperty rangeSetByUser = new SimpleBooleanProperty(this, "Range set by User", false);

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
            log.debug("Start reading file " + file.getName());
            DataSetReader reader = new DataSetReader(file);
            log.debug("End reading file " + file.getName());
            data.addAll(reader.getDataSets().values());
            log.debug("End add all sets" + file.getName());
            openedFiles.add(openedFile);
            log.debug("End do all things on file " + file.getName());
        } catch (Exception e) {
            log.error("Can not open file " + file.toString(), e);
        }

    }

    {   // Add listener to create the Chart Series when a new data is added.
        data.addListener(new ListChangeListener<DataSet>() {
            @Override
            public void onChanged(Change<? extends DataSet> change) {
                for (final DataSet set : change.getList()) {
                    // This listener should be into SpeleoChart
                    set.shownProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean before, Boolean after) {
                            if (before) {
                                // We have to hide the series
                                hide(set);
                            } else {
                                // We have to show the series
                                show(set);
                            }
                        }
                    });
                    //chartSeries.put(set.getSeriesForChart(), set.shownProperty());
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
        openedList.setCellFactory(new Callback<ListView<DataSet>, ListCell<DataSet>>() {
            private Callback<ListView<DataSet>, ListCell<DataSet>> checkBoxFactory = CheckBoxListCell.forListView(
                    new Callback<DataSet, ObservableValue<Boolean>>() {
                        @Override
                        public ObservableValue<Boolean> call(final DataSet dataSet) {
                            return dataSet.shownProperty();
                        }
                    },
                    new StringConverter<DataSet>() {
                        @Override
                        public String toString(DataSet dataSet) {
                            return dataSet.getName();
                        }

                        @Override
                        public DataSet fromString(String s) {
                            for (DataSet d : data)
                                if (d.getName().equals(s)) return d;
                            return null;
                        }
                    }
            );

            @Override
            public ListCell<DataSet> call(final ListView<DataSet> dataSetListView) {
                final ListCell<DataSet> cell = checkBoxFactory.call(dataSetListView);
                dataSetListView.setEditable(true);
                final MenuItem item = new MenuItem("Test");
                item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        cell.startEdit();
                        log.debug("Start editing");
                    }
                });
                cell.setContextMenu(ContextMenuBuilder.create().items(item).build());
                return cell;
            }
        });
        openedList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isSecondaryButtonDown()) {
                    log.debug("Choose a name ?");
                }
            }
        });
    }

    /**
     * Hide a DataSet on the chart.
     *
     * @param dataSet The dataSet to hide
     */
    private void hide(DataSet dataSet) {
        chart.dataMap.remove(dataSet);
    }

    /**
     * Show a DataSet on the chart.
     * <p>If the user has not defined a {@link DateRange} and the DataSet will be the onlyone on the graph the DateRange
     * is defined to be the minimal and maximal date from the DataSet</p>
     *
     * @param dataSet The dataSet to show
     */
    private void show(DataSet dataSet) {
        chart.dataMap.put(dataSet, null);
        if (chart.getData().size() <= 1 && !rangeSetByUser.getValue()) {
            chart.getXAxis().rangeProperty().set(dataSet.getDateRange());
        }
        chart.refresh();
    }

    /**
     * Set the range by the DataSet.
     * It used the smaller date and taller date in the current shown set to set the chart bounds.
     */
    public void autoRange() {
        if (chart.getData().size() <= 0) return;
        Date start = null;
        Date end = null;
        for (XYChart.Series<Date, Number> s : chart.getData())
            for (XYChart.Data<Date, Number> d : s.getData()) {
                if (start == null && end == null) {
                    start = d.XValueProperty().getValue();
                    end = d.XValueProperty().getValue();
                } else if (d.XValueProperty().getValue().before(start)) {
                    start = d.XValueProperty().getValue();
                } else if (d.XValueProperty().getValue().after(end)) {
                    end = d.XValueProperty().getValue();
                }
            }
        rangeSetByUser.setValue(false);
        chart.getXAxis().rangeProperty().setValue(new DateRange(start, end));
        chart.refresh();
    }

    /**
     * Prompt Window for a Date.
     * <p>This class is a stage used to show a small window prompt to enter a date.</p>
     */
    private class PopUpDatePrompt extends Stage {

        public Button button;
        public Text t = new Text();
        public DatePicker picker;
        public VBox box;
        public ObjectProperty<Date> selectedDate;

        public PopUpDatePrompt(Window parent) {

            box = new VBox(8.0);
            box.setPadding(new Insets(5));

            t.setText("");
            t.setVisible(false);
            t.setFill(Color.DARKRED);

            picker = new DatePicker();
            picker.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
            picker.setMaxWidth(Double.MAX_VALUE);
            selectedDate = picker.valueProperty();

            button = new Button("Valider");
            button.setMaxWidth(Double.MAX_VALUE);

            box.getChildren().addAll(picker, button);

            setTitle("Séléctionner une date");
            setScene(new Scene(box));

            initOwner(parent);
            initModality(Modality.WINDOW_MODAL);
            setX(parent.getX() + (parent.getWidth() / 2) - 50);
            setY(parent.getY() + (parent.getHeight() / 2) - 50);
        }

        /**
         * Show an error about the selected date.
         * @param why Reason of error (should be localized string)
         */
        public void error(String why) {
            log.error("Error: " + why);
        }
    }

    /**
     * Ask to the user to define the start date of chart.
     * @param actionEvent the action event is used to get the window to center the prompt box
     */
    public void defineStartDate(ActionEvent actionEvent) {
        Window w = null;
        if (actionEvent.getSource() instanceof Button) {
            Button button = (Button) actionEvent.getSource();
            w = button.getScene().getWindow();
        }
        final PopUpDatePrompt prompt = new PopUpDatePrompt(w);
        prompt.button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                final DateAxis xAxis = chart.getXAxis();
                rangeSetByUser.setValue(true);
                if (AppController.this.rangeSetByUser.getValue()) {
                    Date endDate = xAxis.rangeProperty().getValue().endProperty().getValue();
                    if (prompt.selectedDate.getValue().before(endDate)) {
                        xAxis.rangeProperty().setValue(new DateRange(prompt.selectedDate.getValue(), endDate));
                        chart.refresh();
                        prompt.close();
                    } else {
                        prompt.error("La date de début ne peut pas être après celle de fin");
                    }
                }
            }
        });
        prompt.show();
    }

    /**
     * Ask to the user to define the end date of chart.
     * @param actionEvent the action event is used to get the window to center the prompt box
     */
    public void defineStopDate(ActionEvent actionEvent) {
        Window w = null;
        if (actionEvent.getSource() instanceof Button) {
            Button button = (Button) actionEvent.getSource();
            w = button.getScene().getWindow();
        }
        final PopUpDatePrompt prompt = new PopUpDatePrompt(w);
        prompt.button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                final DateAxis xAxis = chart.getXAxis();
                rangeSetByUser.setValue(true);
                if (AppController.this.rangeSetByUser.getValue()) {
                    Date startDate = xAxis.rangeProperty().getValue().startProperty().getValue();
                    if (prompt.selectedDate.getValue().after(startDate)) {
                        xAxis.rangeProperty().setValue(new DateRange(startDate, prompt.selectedDate.getValue()));
                        chart.refresh();
                        prompt.close();
                    } else {
                        prompt.error("La date de fin ne peut pas être avant celle de début");
                    }
                }
            }
        });
        prompt.show();
    }
}
