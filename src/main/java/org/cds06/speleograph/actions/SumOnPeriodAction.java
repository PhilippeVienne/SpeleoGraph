package org.cds06.speleograph.actions;

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
 * Created by Gabriel on 02/08/2014.
 */
public class SumOnPeriodAction extends AbstractAction {

    private final Series series;

    /**
     * Logger for errors and info.
     */
    @SuppressWarnings("UnusedDeclaration")
    @NonNls
    private static Logger log = LoggerFactory.getLogger(SpeleoGraphApp.class);

    public SumOnPeriodAction(Series series) {
        super("Somme sur période");
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
            builder.addLabel(I18nSupport.translate("actions.correlate.selectRange"));
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
                PanelBuilder builder = new PanelBuilder(layout, getPanel());
                setTitle(I18nSupport.translate("confirm"));
                builder.addLabel("<HTML>"+"<center>" + "Somme" +" :<br>"+somme+"</center><HTML>");
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
