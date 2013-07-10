package org.cds06.speleograph.data;

import org.jfree.data.time.DateRange;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class Series implements Comparable {

    private static Date now() {
        return Calendar.getInstance().getTime();
    }

    public Series(File origin, Type type) {
        this.type = type;
        this.origin = origin;
    }

    private boolean show = true; // = false;
    private File origin = null;
    private ArrayList<Item> items = new ArrayList<>();
    private Type type;
    private DateRange range;

    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public File getOrigin() {
        return origin;
    }

    public DateRange getRange() {
        if (range == null) computeRange();
        return range;
    }

    private void computeRange() {
        int max = getItemCount();
        if (max == 0 && range == null) range = new DateRange(now(), now());
        Date minDate = new Date(Long.MAX_VALUE), maxDate = new Date(Long.MIN_VALUE);
        for (int i = 0; i < max; i++) {
            Item item = items.get(i);
            if (item.getStartDate().before(minDate)) minDate = item.getStartDate();
            if (item.getEndDate() != null) {
                if (item.getEndDate().after(maxDate)) maxDate = item.getEndDate();
            } else {
                if (item.getDate().after(maxDate)) maxDate = item.getDate();
            }
        }
        range = new DateRange(minDate, maxDate);
    }

    public Type getType() {
        return type;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean v) {
        show = v;
    }

    @Override
    public int compareTo(Object o) {
        return this.equals(o) ? 0 : -1;
    }

    public interface SeriesChangeListener {
        public void onChange(Series theSeriesChanged, PropertyName propertyName);
    }

    public enum PropertyName {
        SHOWN,
        TYPE
    }

}
