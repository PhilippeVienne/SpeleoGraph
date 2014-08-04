package org.cds06.speleograph.actions.modif;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel.
 */
public class ResetAllAction extends AbstractAction {
    public ResetAllAction() {
        super(I18nSupport.translate("menus.edit.resetSeries"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Series s : Series.getInstances()) {
            s.reset();
        }
    }
}
