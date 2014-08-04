package org.cds06.speleograph.actions.modif;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel.
 */
public class CancelEverywhereAction extends AbstractAction {

    public CancelEverywhereAction() {
        super(I18nSupport.translate("menus.edit.cancelAll"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setEnabled(false);
        for (Series s : Series.getInstances()) {
            if (s.canUndo()) {
                s.undo();
                setEnabled(true);
            }
        }
    }
}
