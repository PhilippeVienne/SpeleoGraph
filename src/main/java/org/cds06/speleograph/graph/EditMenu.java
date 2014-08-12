package org.cds06.speleograph.graph;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.actions.modif.CancelLastModifAction;
import org.cds06.speleograph.actions.modif.RedoLastUndoAction;
import org.cds06.speleograph.actions.modif.ResetAllAction;
import org.cds06.speleograph.data.Series;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

import javax.swing.*;

/**
 * Created by Gabriel Augendre.
 * This class is used by the main class to define the edit menu.
 * This is not done in the main class because of the Listener put on this menu that caused bigger latency in the main class.
 */
public class EditMenu extends JMenu implements DatasetChangeListener {

    public EditMenu() {
        super(I18nSupport.translate("menus.edit"));
        createMenu();
        Series.addListener(this);
    }

    public void createMenu() {
        this.removeAll();
        this.add(new CancelLastModifAction());
        this.add(new RedoLastUndoAction());
//        this.addSeparator();
//        this.add(new CancelEverywhereAction());
//        this.add(new RedoEverywhereAction());
        this.addSeparator();
        this.add(new ResetAllAction());
    }

    @Override
    public void datasetChanged(DatasetChangeEvent datasetChangeEvent) {
        createMenu();
    }
}
