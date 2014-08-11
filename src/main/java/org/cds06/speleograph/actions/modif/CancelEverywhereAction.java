package org.cds06.speleograph.actions.modif;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.utils.Modification;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel.
 */
public class CancelEverywhereAction extends AbstractAction {

    public CancelEverywhereAction() {
        String name;
        if (Modification.canCancel()) {
            name = I18nSupport.translate("menus.edit.cancelAll");
            setEnabled(true);
        } else {
            name = "Pas de modification à annuler";
            setEnabled(false);
        }
        putValue(NAME, name);
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
