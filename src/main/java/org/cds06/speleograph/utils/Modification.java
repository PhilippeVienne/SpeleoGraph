package org.cds06.speleograph.utils;

import org.cds06.speleograph.data.Item;
import org.cds06.speleograph.data.Series;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Gabriel Augendre.
 * This class is designed to save modifications made to series by giving it a name,
 * a date and the list of the series items before the modification occurs.
 */
public class Modification {

    private static ArrayList<Modification> allModifs;

    /**
     * A name for the modification.
     */
    private final String name;

    /**
     * The date when the modification was made.
     */
    private final Date date;

    /**
     * The item list to be saved (items before modification).
     */
    private final ArrayList<Item> items;

    /**
     * Intended to say of the modification applies to every series or just one.
     */
    private boolean applyToAll = false;

    /**
     * Create a modification saving the series items before the modification was made.
     * @param name A name for the modification.
     * @param date The date when the modification was made.
     * @param items The item list to be saved.
     */
    public Modification(String name, Date date, ArrayList<Item> items) {
        this.name = name;
        this.date = date;
        this.items = items;
    }

    /**
     * Create a modification saving the series items before the modification was made.
     * @param name A name for the modification.
     * @param date The date when the modification was made.
     * @param items The item list to be saved.
     * @param applyToAll Intended to say of the modification applies to every series or just one.
     */
    public Modification(String name, Date date, ArrayList<Item> items, boolean applyToAll) {
        this.name = name;
        this.date = date;
        this.items = items;
        this.applyToAll = applyToAll;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public boolean isApplyToAll() {
        return applyToAll;
    }

    public Modification getLastModif() {
        Modification modif = Series.getInstances().get(0).getLastModif();
        Date d = Series.getInstances().get(0).getLastModif().getDate();
        for (Series s : Series.getInstances()) {
            if (s.getLastModif().getDate().after(d)) {
                d = s.getLastModif().getDate();
                modif = s.getLastModif();
            }
        }
        return modif;
    }
}
