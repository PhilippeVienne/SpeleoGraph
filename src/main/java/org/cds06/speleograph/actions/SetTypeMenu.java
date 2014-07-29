package org.cds06.speleograph.actions;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.data.Type;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel on 29/07/2014.
 */
public class SetTypeMenu extends JMenu {
    private final Series serie;

    public SetTypeMenu(Series series) {
        super(I18nSupport.translate("actions.setType"));
        this.serie = series;

        for (final Type t : Type.getInstances()) {
            this.add(new AbstractAction() {
                {
                    putValue(NAME, t.getName() + " (" + t.getUnit() + ")");
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    serie.setType(t);
                    serie.notifyListeners();
                    JOptionPane.showMessageDialog(
                            null,
                            "Le type de la série " + serie.getName() + " a été défini à " + t.toString(),
                            "Type correctement défini",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            });
            if (t.compareTo(Type.WATER) == 0)
                this.addSeparator();
        }
    }
}
