package org.cds06.speleograph.actions;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.data.Type;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Create the submenu allowing the user to set the type of a Serie
 * Created by Gabriel Augendre
 * Distributed on GPL v3
 */
public class SetTypeMenu extends JMenu {

    public SetTypeMenu(final Series series) {
        super(I18nSupport.translate("actions.setType"));
        final ButtonGroup types = new ButtonGroup();

        for (int i = 0; i < Type.getInstances().size(); i++) {
            // Adding a separator to separate default types from user types
            if (i == Type.DEFAULT_SIZE)
                this.addSeparator();

            final Type t = Type.getInstances().get(i);

            //Creating the menu item and the action linked to it
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(t.getName() + " (" + t.getUnit() + ")");
            item.addActionListener(new ActionListener() {
                @Override
                //We want the button to change the type of the serie and to tell everyone the type has changed
                public void actionPerformed(ActionEvent e) {
                    series.setType(t);
                    series.notifyListeners();
                }
            });

            //Set the proper item selected
            if (t.equals(series.getType()))
                item.setSelected(true);

            types.add(item);
            this.add(item);
        }
    }
}



















//            this.add(new AbstractAction() {
//                {
//                    putValue(NAME, t.getName() + " (" + t.getUnit() + ")");
//                }
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    serie.setType(t);
//                    serie.notifyListeners();
//                    JOptionPane.showMessageDialog(
//                            null,
//                            "Le type de la série " + serie.getName() + " a été défini à " + t.toString(),
//                            "Type correctement défini",
//                            JOptionPane.INFORMATION_MESSAGE
//                    );
//                }
//            });