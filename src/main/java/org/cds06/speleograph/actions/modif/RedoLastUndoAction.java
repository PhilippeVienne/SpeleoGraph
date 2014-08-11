package org.cds06.speleograph.actions.modif;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.utils.Modification;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel.
 */
public class RedoLastUndoAction extends AbstractAction {

    public RedoLastUndoAction() {
        String name;
        setEnabled(true);
        if (Modification.canRedo()){
            name = I18nSupport.translate("menus.edit.redo") + " " + Modification.getNextRedo().getName();
//            setEnabled(true);
        }
        else{
            name = "Pas de modification à refaire";
//            setEnabled(false);
        }

        putValue(NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Modification.canRedo()) {
            Modification modif = Modification.getNextRedo();
            if (!modif.isApplyToAll()) modif.getLinkedSeries().redo();
            else {
                for (Series s : Series.getInstances()) {
                    if (s.getLastModif() != null && s.getLastModif().isLike(modif))
                        s.redo();
                }
            }
        }
    }
}
