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
        String name;
        setEnabled(true);
        if (Modification.canCancel()){
            name = I18nSupport.translate("cancel") + " " + Modification.getLastModif().getLinkedSeries().getItemsName();
//            setEnabled(true);
        }
        else{
            name = "Pas de modification à annuler";
//            setEnabled(false);
        }

        putValue(NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Modification.canCancel()) {
            Modification lastModif = Modification.getLastModif(); //On récupère la dernière modification.
            if (!lastModif.isApplyToAll()) lastModif.getLinkedSeries().undo(); //Si elle n'était pas appliquée à toutes les séries, on l'annule sur la série concernée.
            else { //Sinon on l'annule partout où la même modification a été effectuée.
                for (Series s : Series.getInstances()) {
                    if (s.getLastModif() != null && s.getLastModif().isLike(lastModif))
                        s.undo();
                }
            }
        }
    }
}
