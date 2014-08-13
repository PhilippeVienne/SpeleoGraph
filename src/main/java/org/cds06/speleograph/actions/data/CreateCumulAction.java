package org.cds06.speleograph.actions.data;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.SpeleoGraphApp;
import org.cds06.speleograph.data.Item;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.utils.DateSelector;
import org.cds06.speleograph.utils.FormDialog;
import org.jetbrains.annotations.NonNls;
import org.jfree.data.time.DateRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Created by Gabriel Augendre.
 * Allow the user to compute the sum of items value on a period.
 * Useful for water-like series to compute how much of water fell during a certain amount of time.
 */
public class CreateCumulAction extends AbstractAction {

    private final Series series;

    /**
     * Logger for errors and info.
     */
    @SuppressWarnings("UnusedDeclaration")
    @NonNls
    private static Logger log = LoggerFactory.getLogger(SpeleoGraphApp.class);

    public CreateCumulAction(Series series) {
        super("Création du cumul");
        this.series = series;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final PromptDialog dialog = new PromptDialog();
        final DateRange range = series.getRange();
        dialog.startDateSelector.setDate(range.getLowerDate());
        dialog.endDateSelector.setDate(range.getUpperDate());
        dialog.setVisible(true);
    }

    private class PromptDialog extends FormDialog {
        private final FormLayout layout = new FormLayout("p:grow", "p,4dlu,p,4dlu,p,4dlu,p,6dlu,p");
        private final DateSelector startDateSelector = new DateSelector();
        private final DateSelector endDateSelector = new DateSelector();

        private PromptDialog() {
            super();
            construct();
            setTitle("");
        }

        @Override
        protected void setup() {
            PanelBuilder builder = new PanelBuilder(layout, getPanel());
            this.setTitle(I18nSupport.translate("actions.sum.selectRange"));
            builder.addLabel(I18nSupport.translate("date.from") + " :");
            builder.nextLine(2);
            builder.add(startDateSelector);
            builder.nextLine(2);
            builder.addLabel(I18nSupport.translate("date.to") + " :");
            builder.nextLine(2);
            builder.add(endDateSelector);
            builder.nextLine(2);
            builder.add(new JButton(new AbstractAction() {

                {
                    putValue(NAME, I18nSupport.translate("ok"));
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    validateForm();
                }
            }));

            final Dimension dim = getPanel().getPreferredSize();
            getPanel().setPreferredSize(new Dimension(dim.width + 50, dim.height));
        }

        @Override
        protected void validateForm() {
            org.cds06.speleograph.data.Type type;
            final String unit = series.getType().getUnit();
            switch (unit.toLowerCase()) {
                case "mm":
                    type = org.cds06.speleograph.data.Type.WATER_CUMUL;
                    break;
                default:
                    type = org.cds06.speleograph.data.Type.getType(org.cds06.speleograph.data.Type.WATER_CUMUL.getName(), unit);
            }
            final Series newSeries = new Series(series.getOrigin(), type);

            ArrayList<Item> items = new ArrayList<>(series.getItemCount());

            for (int i = 0; i < series.getItemCount(); i++) {
                Item item = series.getItems().get(i);
                double value = item.getValue();
                if (i > 0)
                    value += items.get(i-1).getValue();
                items.add(new Item(newSeries, item.getDate(), value));
            }

            newSeries.setItems(items, (String) getValue(NAME));


            setVisible(false);
        }

        @Override
        protected FormLayout getFormLayout() {
            return layout;
        }


    }
}
