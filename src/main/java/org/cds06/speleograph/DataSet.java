package org.cds06.speleograph;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Date;

/**
 * Set of Data parsed by the SpeleoGraph.
 *
 * @author PhilippeGeek
 */
public class DataSet extends ArrayList<Data> {

    /**
     * Flag to determine if shown property is listened.
     *
     * @see #shown
     * @see AppController#initialize(java.net.URL, java.util.ResourceBundle) This function use this flag
     */
    public boolean observed = false;

    /**
     * Type of stored data.
     */
    private Data.Type type;

    /**
     * Reader used to read this DataSet
     */
    private DataSetReader reader;

    /**
     * Determine if the set is currently drawn on charts
     */
    private SimpleBooleanProperty shown = new SimpleBooleanProperty(this, "shown", false);

    /**
     * Name chosen by the user for this DataSet.
     */
    private SimpleStringProperty name = new SimpleStringProperty(this, "DataSet.name", null);

    /**
     * The binder to compute the DataSet's Name.
     */
    private final StringBinding nameBinder = new StringBinding() {

        {
            super.bind(name);
        }

        /**
         * Compute the name of DataSet.
         * <p>
         * If the name is user-set, then it returns this name. Otherwise, it computes the name from the Reader title and
         * the translated data type, for example "SU-75845 - Pressure".
         * </p>
         * @inheritDoc
         */
        @Override
        protected String computeValue() {
            if (name.isNull().getValue()) {
                if (getReader().getDataOriginFile() == null) return this.toString();
                String name = getReader().getTitle();
                if (getType() != null) switch (getType()) {
                    case PRESSURE:
                        name += " - Pression";
                        break;
                    case WATER:
                        name += " - Précipitations";
                        break;
                    case TEMPERATURE:
                        name += " - Température";
                        break;
                    case TEMPERATURE_MIN_MAX:
                        name += " - Température (Min/Max)";
                }
                return name;
            } else {
                return name.getValue();
            }
        }
    };

    /**
     * Construct a DataSet which depends on a Reader.
     *
     * @param reader The reader where the data has been taken from.
     * @param type   The type of data which will be stored in this set.
     * @throws IllegalArgumentException if reader or type are null.
     */
    public DataSet(DataSetReader reader, Data.Type type) {
        if (reader == null || type == null) throw new IllegalArgumentException("Reader and type can not be null");
        setReader(reader);
        setType(type);
        nameBinder.invalidate();
    }

    /**
     * Generate a name for the current set.
     *
     * @return The name for this set
     */
    public String getName() {
        return nameBinder.get();
    }

    /**
     * Determine Type from this set
     *
     * @return The Type, never null
     */
    public Data.Type getType() {
        return type;
    }

    /**
     * Set the Type for this set.
     * It will not be checked with sub-data
     *
     * @param type The type to set
     */
    public void setType(Data.Type type) {
        this.type = type;
    }

    /**
     * Accessor for the reader.
     * You can use it to guess which file was used to read this set
     *
     * @return The reader, never null
     */
    public DataSetReader getReader() {
        return reader;
    }

    /**
     * Set the reader for this set.
     * This function should never be called outside {@link DataSetReader}
     *
     * @param reader The reader used to read this set
     */
    public void setReader(DataSetReader reader) {
        this.reader = reader;
    }

    /**
     * Accessor for the shown property.
     * This property is used to define if the set is currently shown on the screen.
     *
     * @return The property.
     */
    public SimpleBooleanProperty shownProperty() {
        return shown;
    }

    /**
     * Accessor for the name property.
     * The name property can be used to set the name of dataSet.
     */
    public SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * Order all data by date.
     */
    public void orderByDate() {
        for (int i = 0; i < size() - 1; i++) {
            if (get(i).getDate().after(get(i + 1).getDate())) {
                Data d = get(i + 1);
                set(i + 1, get(i));
                set(i, d);
            }
        }
    }

    /**
     * Get the minimal and maximal date from data in this set.
     *
     * @return The range of this Set
     */
    public DateRange getDateRange() {
        Date older = get(0).getDate(), newer = get(size() - 1).getDate();
        for (Data d : this) {
            if (d.getDate().before(older)) older = d.getDate();
            if (d.getDate().after(newer)) newer = d.getDate();
        }
        return new DateRange(older, newer);
    }

    /**
     * Generate a chart series with this set of data.
     *
     * @return The big series.
     */
    public XYChart.Series<Date, Number> getSeriesForChart() {
        XYChart.Series<Date, Number> series = new XYChart.Series<>();
        orderByDate(); // Be sure to order by date
        series.setName(getName());
        for (Data data : this)
            series.getData().add(new XYChart.Data<Date, Number>(data.getDate(), data.getValue(), data));
        return series;
    }

}
