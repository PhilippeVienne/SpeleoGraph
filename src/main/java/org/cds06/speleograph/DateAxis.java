package org.cds06.speleograph;

import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;

import java.util.Date;
import java.util.List;

/**
* This file is created by PhilippeGeek.
* Distributed on licence GNU GPL V3.
*/
class DateAxis extends ValueAxis<Number> {


    @Override
    protected List<Number> calculateMinorTickMarks() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setRange(Object o, boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Object getRange() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected List<Number> calculateTickValues(double v, Object o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String getTickMarkLabel(Number date) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
