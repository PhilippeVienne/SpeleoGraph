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
        PromptDialog dialog = new PromptDialog();
        DateRange range = series.getRange();
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
            builder.addLabel(I18nSupport.translate("actions.sum.selectRange"));
            builder.nextLine(2);
            builder.add(startDateSelector);
            builder.nextLine(2);
            builder.addLabel(I18nSupport.translate("actions.limit.label2"));
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
        }

        @Override
        protected void validateForm() {
            double somme = 0;
            ArrayList<Item> items = series.extractSubSerie(startDateSelector.getDate(), endDateSelector.getDate());
            for (Item item : items)
                somme += item.getValue();

            InfoDialog iD = new InfoDialog(somme);
            iD.setVisible(true);
            setVisible(false);
        }

        @Override
        protected FormLayout getFormLayout() {
            return layout;
        }



        private class InfoDialog extends FormDialog {

            private FormLayout layout = getFormLayout();
            double somme;

            public InfoDialog(double somme) {
                super();
                this.somme = somme;
                construct();
            }
            @Override
            protected void setup() {
                final String[] splited = ((Double) Math.abs(somme)).toString().split("\\.");
                final String str = splited[0] + "," + splited[1].substring(0,4);

                PanelBuilder builder = new PanelBuilder(layout, getPanel());
                setTitle(I18nSupport.translate("confirm"));
                builder.addLabel("<HTML>"+"<center>" + "Somme" +" :<br>"+ str +"</center><HTML>");
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
            }

            @Override
            protected void validateForm() {
                setVisible(false);
            }

            @Override
            protected FormLayout getFormLayout() {
                return new FormLayout("p:grow", "p:grow,6dlu,p");
            }
        }
    }
}
