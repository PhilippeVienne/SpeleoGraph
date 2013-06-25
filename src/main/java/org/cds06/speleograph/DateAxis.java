package org.cds06.speleograph;

import javafx.scene.chart.Axis;

import java.util.Date;
import java.util.List;

/**
* This file is created by PhilippeGeek.
* Distributed on licence GNU GPL V3.
*/
class DateAxis extends Axis<Date> {

    @Override
    protected Object autoRange(double v) {
        return null;
    }

    @Override
    protected void setRange(Object o, boolean b) {

    }

    @Override
    protected Object getRange() {
        return null;
    }

    @Override
    public double getZeroPosition() {
        return 0;
    }

    @Override
    public double getDisplayPosition(Date date) {
        return 0;
    }

    @Override
    public Date getValueForDisplay(double v) {
        return null;
    }

    @Override
    public boolean isValueOnAxis(Date date) {
        return false;
    }

    @Override
    public double toNumericValue(Date date) {
        return 0;
    }

    @Override
    public Date toRealValue(double v) {
        return null;
    }

    @Override
    protected List<Date> calculateTickValues(double v, Object o) {
        return null;
    }

    @Override
    protected String getTickMarkLabel(Date date) {
        return null;
    }
}
