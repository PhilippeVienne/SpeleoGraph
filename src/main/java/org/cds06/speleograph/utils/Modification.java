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

    private static ArrayList<Modification> redoList = new ArrayList<>(Series.MAX_UNDO_ITEMS);

    private static ArrayList<Modification> undoList = new ArrayList<>(Series.MAX_UNDO_ITEMS);

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

    public static Modification getLastModif() {
        return undoList.get(undoList.size()-1);
    }

    public static Modification getNextRedo() {
        return redoList.get(redoList.size()-1);
    }

    public static boolean canCancel() {
        return undoList.size() > 0;
    }

    public static boolean canRedo() {
        return redoList.size() > 0;
    }

    public Series getLinkedSeries() {
        return items.get(0).getSeries();
    }

    /**
     * Compares two modifications to say if they are similar.
     * We consider two modifications similar if they are done at the same time (approx., with 3 sec of uncertainty).
     * @param modif The modification to compare to.
     * @return true if the modifications are similar, false else.
     */
    public boolean isLike(Modification modif) {
        return Math.abs(modif.getDate().getTime() - this.getDate().getTime()) <= 3000 && modif.isApplyToAll() && this.isApplyToAll();
    }

    public static void addToRedoList(Modification m) {
        redoList.add(m);
    }

    public static void removeLastRedo() {
        redoList.remove(redoList.size()-1);
    }

    public static void clearRedoList() {
        redoList.clear();
    }

    public static void addToUndoList(Modification m) {
        if (undoList.size() > Series.MAX_UNDO_ITEMS * Series.getInstances().size())
            undoList.remove(0);
        undoList.add(m);
    }

    public static void removeLastUndo() {
        undoList.remove(undoList.size()-1);
    }
}
