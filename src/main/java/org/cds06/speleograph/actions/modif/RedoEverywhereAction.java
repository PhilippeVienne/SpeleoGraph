package org.cds06.speleograph.actions.modif;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel.
 */
public class RedoEverywhereAction extends AbstractAction {
    public RedoEverywhereAction() {
        super(I18nSupport.translate("menus.edit.redoAll"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setEnabled(false);
        for (Series s : Series.getInstances()) {
            if (s.canRedo()) {
                s.redo();
                setEnabled(true);
            }
        }
    }
}
