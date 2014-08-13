package org.cds06.speleograph.actions.modif;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.utils.Modification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel.
 */
public class CancelLastModifAction extends AbstractAction {

    private static Logger log = LoggerFactory.getLogger(CancelLastModifAction.class);

    public CancelLastModifAction() {
        String name;
        if (Modification.canCancel()){
            Series series = Modification.getLastModif().getLinkedSeries();
            try {
                name = I18nSupport.translate("cancel") + " " + series.getItemsName();
                setEnabled(true);
            } catch (NullPointerException npe) {
                log.info("Unable to get series corresponding to last modif");
                name = "Erreur";
                setEnabled(false);
            }
        }
        else{
            name = "Pas de modification à annuler";
            setEnabled(false);
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
                    Modification m = s.getLastModif();
                    if (m != null && m.isLike(lastModif))
                        s.undo();
                }
            }
        }
    }
}
