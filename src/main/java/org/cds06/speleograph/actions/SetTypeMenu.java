package org.cds06.speleograph.actions;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.data.Type;
import org.cds06.speleograph.graph.SetTypePanel;
import org.cds06.speleograph.utils.FormDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Create the submenu allowing the user to set the type of a Serie
 * Created by Gabriel Augendre
 * Distributed on GPL v3
 */
public class SetTypeMenu extends JMenu {
    Series series;

    public SetTypeMenu(final Series series) {
        super(I18nSupport.translate("actions.setType"));
        this.series = series;
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

        this.addSeparator();

        this.add(new AbstractAction() {
            {
                putValue(NAME, I18nSupport.translate("actions.setType.newType"));
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                final PromptDialog dialog = new PromptDialog();
                dialog.setVisible(true);
            }
        });
    }

    private class PromptDialog extends FormDialog {
        private FormLayout layout = getFormLayout();
        final SetTypePanel setTypePanel = new SetTypePanel();

        public PromptDialog() {
            super();
            setTitle(I18nSupport.translate("actions.setType.changeType"));
            construct();
        }

        @Override
        protected void setup() {
            PanelBuilder builder = new PanelBuilder(layout, getPanel());
            builder.add(setTypePanel);
            builder.nextLine();
            builder.add(new JButton(new AbstractAction() {
                {
                    putValue(NAME, I18nSupport.translate("actions.setType.setFor")+ " " + series.getName());
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    validateForm();
                }
            }));
        }

        @Override
        protected void validateForm() {
            series.setType(org.cds06.speleograph.data.Type.getType(setTypePanel.getTypeName(), setTypePanel.getTypeUnit()));
            series.notifyListeners();
            setVisible(false);
        }

        @Override
        protected FormLayout getFormLayout() {
            return new FormLayout("p:grow", "p:grow,p");
        }
    }
}


