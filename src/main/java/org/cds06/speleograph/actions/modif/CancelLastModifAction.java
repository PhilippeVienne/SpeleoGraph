package org.cds06.speleograph.actions.modif;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.utils.Modification;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel.
 */
public class CancelLastModifAction extends AbstractAction {

    public CancelLastModifAction() {
        putValue(NAME, I18nSupport.translate("cancel"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setEnabled(false);
        if (Modification.canCancel()) {
            Modification lastModif = Modification.getLastModif();
            if (!lastModif.isApplyToAll()) lastModif.getLinkedSeries().undo();
            else {
                for (Series s : Series.getInstances()) {
                    if (s.getLastModif().isLike(lastModif))
                        s.undo();
                }
            }
            setEnabled(true);
            putValue(NAME, I18nSupport.translate("cancel") + " " + lastModif.getName());
        }
    }
}
