package org.cds06.speleograph.actions;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.SpeleoGraphApp;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.utils.FormDialog;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Gabriel Augendre.
 * Allow the user to set an offset for the hour.
 */
public class SeriesInfoAction extends AbstractAction {
    private final Series series;

    /**
     * Logger for errors and info.
     */
    @SuppressWarnings("UnusedDeclaration")
    @NonNls
    private static Logger log = LoggerFactory.getLogger(SpeleoGraphApp.class);

    public SeriesInfoAction(Series series) {
        super();
        putValue(NAME, I18nSupport.translate("actions.info"));
        this.series = series;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PromptDialog dialog = new PromptDialog();
        dialog.setVisible(true);
    }

    private class PromptDialog extends FormDialog {
        private FormLayout layout = new FormLayout("p:grow","p,p,p,p:grow,p");

        public PromptDialog() {
            super();
            construct();
            setTitle(I18nSupport.translate("actions.info"));
        }

        @Override
        protected void setup() {
            PanelBuilder builder = new PanelBuilder(layout, getPanel());

            String seriesString = "<HTML><ul>";
            for (Series s : Series.getInstances()) {
                if (s.getOrigin().equals(series.getOrigin())) {
                    seriesString += ("<li><em>" + s.getName() + "</em> - [" +
                            s.getSeriesMinValue() + " --> " + s.getSeriesMaxValue() +
                            "] " + s.getType().getUnit() + "</li>");
                }
            }
            seriesString += "</ul></HTML>";

            final String absoluteFile = "<HTML><strong>" + I18nSupport.translate("actions.info.filename") + " : </strong>" +
                    series.getOrigin().getAbsolutePath() + "</HTML>";
            final String file = "<HTML><strong>" + I18nSupport.translate("actions.info.filename") + " : </strong>" +
                    series.getOrigin().getName() + "</HTML>";
            final JLabel fileLabel = new JLabel(file);
            fileLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (fileLabel.getText().equals(absoluteFile)) {
                        fileLabel.setText(file);
                        pack();
                        centerOnScreen();
                    } else {
                        fileLabel.setText(absoluteFile);
                        pack();
                        centerOnScreen();
                    }
                }
            });
            builder.add(fileLabel);

            builder.nextLine();
            builder.addLabel("<HTML><strong>" + I18nSupport.translate("actions.info.dateRange") + " : </strong>" +
                    series.getRange().toString() + "</HTML>");

            builder.nextLine();
            builder.addLabel("<HTML><strong>" + I18nSupport.translate("actions.info.associatedSeries") + " :</strong></HTML>");
            builder.nextLine();
            builder.addLabel(seriesString);

            builder.nextLine();
            builder.add(new JButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    validateForm();
                }

                {
                    putValue(NAME, I18nSupport.translate("ok"));
                }
            }));
        }

        @Override
        protected void validateForm() {
            setVisible(false);
        }

        @Override
        protected FormLayout getFormLayout() {
            return layout;
        }

    }
}
