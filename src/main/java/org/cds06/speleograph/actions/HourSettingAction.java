package org.cds06.speleograph.actions;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.SpeleoGraphApp;
import org.cds06.speleograph.data.Item;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.utils.FormDialog;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gabriel Augendre.
 * Allow the user to set an offset for the hour.
 */
public class HourSettingAction extends AbstractAction {
    private final Series series;

    /**
     * Logger for errors and info.
     */
    @SuppressWarnings("UnusedDeclaration")
    @NonNls
    private static Logger log = LoggerFactory.getLogger(SpeleoGraphApp.class);

    public HourSettingAction(Series series) {
        super();
        putValue(NAME, I18nSupport.translate("actions.timezone"));
        this.series = series;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PromptDialog dialog = new PromptDialog();
        dialog.setVisible(true);
    }

    private class PromptDialog extends FormDialog {
        private FormLayout layout = new FormLayout("p:grow,p","p,p,p");
        private JTextField offsetValue = new JTextField();
        private final String HOUR = I18nSupport.translate("actions.timezone.hours"),
                MINUTE = I18nSupport.translate("actions.timezone.minutes"),
                SECOND = I18nSupport.translate("actions.sample.second");
        private JComboBox<String> offsetUnit = new JComboBox<>(new String[] {
                HOUR, MINUTE, SECOND
        });

        public PromptDialog() {
            super();
            construct();
            setTitle(I18nSupport.translate("actions.timezone"));
        }

        @Override
        protected void setup() {
            PanelBuilder builder = new PanelBuilder(layout, getPanel());
            CellConstraints cc = new CellConstraints();
            cc.xyw(1,1,2);
            builder.addLabel(I18nSupport.translate("actions.timezone.offset"), cc);
            builder.nextLine();
            builder.add(offsetValue);
            builder.nextColumn();
            builder.add(offsetUnit);
            builder.nextLine();
            cc.xyw(1,3,2);
            builder.add(new JButton(new AbstractAction() {
                {
                    putValue(NAME,I18nSupport.translate("ok"));
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    validateForm();
                }
            }),cc);
        }

        @Override
        protected void validateForm() {
            List<Item> items = series.getItems();

            int modifier = 1000;
            if (offsetUnit.getSelectedItem().equals(HOUR))
                modifier *= 3600;
            else if (offsetUnit.getSelectedItem().equals(MINUTE))
                modifier *= 60;

            int value = 0;

            try {
                value = Integer.parseInt(offsetValue.getText());
            } catch (NumberFormatException e) {
                log.error(offsetValue.getText() + " " + I18nSupport.translate("notInt") + ".");
            }

            ArrayList<Item> newItems = new ArrayList<>(items.size());

            if (!series.isMinMax()) {
                for (Item i : items) {
                    newItems.add(new Item(series, new Date(i.getDate().getTime() + value * modifier), i.getValue()));
                }
            } else {
                for (Item i : items) {
                    newItems.add(new Item(series, new Date(i.getDate().getTime() + value * modifier), i.getLow(), i.getHigh()));
                }
            }
            series.setItems(newItems, (String) getValue(NAME));
            setVisible(false);
        }

        @Override
        protected FormLayout getFormLayout() {
            return layout;
        }
    }
}
