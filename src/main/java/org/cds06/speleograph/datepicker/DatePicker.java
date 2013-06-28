/*
 * DatePicker.java
 * Date of creation: 2012-03-22
 *
 * Copyright (c) 2009-2012 CompuGroup Medical Software GmbH,
 *
 * This software is the confidential and proprietary information of
 * CompuGroup Medical Software GmbH. You shall not disclose
 * such confidential information and shall use it only in
 * accordance with the terms of the license agreement you
 * entered into with CompuGroup Medical Software GmbH.
 */
package org.cds06.speleograph.datepicker;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ComboBoxBase;
import javafx.util.Callback;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A date picker control, which allows the user to pick a date from a calendar pop-up or to enter a date manually.
 * <p/>
 * <h3>Screenshots</h3>
 * <img src="doc-files/DatePicker.png" alt="DatePicker"/>
 *
 * @author Christian Schudt
 * @see CalendarView
 */
public final class DatePicker extends ComboBoxBase<Date> {

    private final ObjectProperty<Error> error = new SimpleObjectProperty<Error>(this, "error");

    private final ObjectProperty<Calendar> calendar = new SimpleObjectProperty<Calendar>(this, "calendar");

    private final ObjectProperty<Locale> locale = new SimpleObjectProperty<Locale>(this, "locale");

    private final ObjectProperty<DateFormat> dateFormat = new SimpleObjectProperty<DateFormat>(this, "dateFormat");

    private final ObjectProperty<Date> minDate = new SimpleObjectProperty<Date>(this, "minDate");

    private final ObjectProperty<Date> maxDate = new SimpleObjectProperty<Date>(this, "maxDate");

    private final ObjectProperty<Callback<CalendarView, DateCell>> dayCellFactory = new SimpleObjectProperty<>(this, "dayCellFactory");

    /**
     * Indicates whether the weeks numbers are shown.
     */
    private final BooleanProperty showWeeks = new SimpleBooleanProperty(this, "showWeeks", false);

    /**
     * Initializes the date picker with the default locale.
     */
    public DatePicker() {
        this(Locale.getDefault());
    }

    /**
     * Initializes the date picker with the given locale.
     *
     * @param locale The locale.
     */
    public DatePicker(final Locale locale) {

        this.locale.set(locale);
        this.calendar.set(Calendar.getInstance(locale));
        setEditable(true);
        getStyleClass().add("date-picker");

        setFocusTraversable(false);

        sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene scene, Scene scene1) {
                if (scene1 != null) {
                    scene1.getStylesheets().add(getClass().getResource("DatePicker.css").toExternalForm());
                }
            }
        });
    }

    /**
     * States whether the user input is invalid (is no valid date).
     *
     * @return The property.
     */
    public ObjectProperty<Error> errorProperty() {
        return error;
    }

    /**
     * The calendar defines how to
     *
     * @return The property.
     * @see CalendarView#calendarProperty()
     * @see #getCalendar()
     * @see #setCalendar(java.util.Calendar)
     */
    public ObjectProperty<Calendar> calendarProperty() {
        return calendar;
    }

    /**
     * Gets the calendar.
     *
     * @return The calendar.
     * @see #calendarProperty()
     */
    public Calendar getCalendar() {
        return calendar.get();
    }

    /**
     * Sets the calendar.
     *
     * @param calendar The calendar.
     * @see #calendarProperty()
     */
    public void setCalendar(Calendar calendar) {
        this.calendar.set(calendar);
    }

    /**
     * The locale defines several things:
     * <ul>
     * <li>The date format if no {@linkplain #dateFormatProperty() date format} is explicitly set.</li>
     * <li>The calendar which is used, if no {@linkplain #calendarProperty() calendar} is explicitly set.</li>
     * <li>The language for the {@link CalendarView}</li>
     * </ul>
     *
     * @return The property.
     * @see #getLocale()
     * @see #setLocale(java.util.Locale)
     * @see CalendarView#localeProperty()
     */
    public ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    /**
     * Gets the locale.
     *
     * @return The locale.
     * @see #localeProperty()
     */
    public Locale getLocale() {
        return locale.get();
    }

    /**
     * Sets the locale.
     *
     * @param locale The locale.
     * @see #localeProperty()
     */
    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    /**
     * The date format describes how the date is parsed and formatted.
     *
     * @return The date format property.
     */
    public ObjectProperty<DateFormat> dateFormatProperty() {
        return dateFormat;
    }

    /**
     * Gets the date format.
     *
     * @return The date format.
     * @see #dateFormatProperty()
     */
    public DateFormat getDateFormat() {
        return dateFormat.get();
    }

    /**
     * Sets the date format.
     *
     * @param dateFormat The date format.
     * @see #dateFormatProperty()
     */
    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat.set(dateFormat);
    }

    /**
     * The min date defines the lower boundary of the date.
     *
     * @return The min date.
     */
    public ObjectProperty<Date> minDateProperty() {
        return minDate;
    }

    /**
     * Gets the min date.
     *
     * @return The min date.
     * @see #minDateProperty()
     */
    public Date getMinDate() {
        return minDate.get();
    }

    /**
     * Sets the min date.
     *
     * @param minDate The min date.
     * @see #minDateProperty()
     */
    public void setMinDate(Date minDate) {
        this.minDate.set(minDate);
    }

    /**
     * The max date define the upper boundaries of the date.
     *
     * @return The max date.
     * @see #getMaxDate()
     * @see #setMaxDate(java.util.Date) s
     */
    public ObjectProperty<Date> maxDateProperty() {
        return maxDate;
    }

    /**
     * Gets the max date.
     *
     * @return The max date.
     * @see #maxDateProperty()
     */
    public Date getMaxDate() {
        return maxDate.get();
    }

    /**
     * Sets the max date.
     *
     * @param maxDate The max date.
     * @see #maxDateProperty()
     */
    public void setMaxDate(Date maxDate) {
        this.maxDate.set(maxDate);
    }

    /**
     * Indicates, whether the week numbers are shown.
     *
     * @return The property.
     * @see #getShowWeeks()
     * @see #setShowWeeks(boolean)
     */
    public BooleanProperty showWeeksProperty() {
        return showWeeks;
    }

    /**
     * Gets the value, whether weeks are shown.
     *
     * @return True, if weeks are shown, otherwise false.
     * @see #showWeeksProperty()
     */
    public boolean getShowWeeks() {
        return showWeeks.get();
    }

    /**
     * Sets the value, whether weeks are shown.
     *
     * @param showWeeks True, if weeks are shown, otherwise false.
     * @see #showWeeksProperty()
     */
    public void setShowWeeks(boolean showWeeks) {
        this.showWeeks.set(showWeeks);
    }

    /**
     * The cell factory for the days.
     *
     * @return The cell factory.
     * @see #getDayCellFactory()
     * @see #setDayCellFactory(javafx.util.Callback)
     */
    public ObjectProperty<Callback<CalendarView, DateCell>> dayCellFactoryProperty() {
        return dayCellFactory;
    }

    /**
     * Gets the cell factory.
     *
     * @return The cell factory.
     * @see #dayCellFactoryProperty()
     */
    public Callback<CalendarView, DateCell> getDayCellFactory() {
        return dayCellFactory.get();
    }

    /**
     * Sets the cell factory.
     *
     * @param dayCellFactory The cell factory.
     * @see #dayCellFactoryProperty()
     */
    public void setDayCellFactory(final Callback<CalendarView, DateCell> dayCellFactory) {
        this.dayCellFactory.set(dayCellFactory);
    }


    @Override
    protected String getUserAgentStylesheet() {
        return getClass().getResource("DatePicker.css").toExternalForm();
    }

    public enum Error {
        DATE_GREATER_THAN_MAX,
        DATE_LESS_THAN_MIN,
        UNPARSABLE
    }
}
