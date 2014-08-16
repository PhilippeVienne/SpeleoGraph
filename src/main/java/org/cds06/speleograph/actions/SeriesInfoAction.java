package org.cds06.speleograph.actions;

import com.jgoodies.forms.builder.ButtonBarBuilder;
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
        private final FormLayout layout = new FormLayout("p","p,p,p,p,p");
        private int clickCount = 0;

        public PromptDialog() {
            super();
            construct();
            setTitle(I18nSupport.translate("actions.info"));
            setResizable(false);
        }

        @Override
        protected void setup() {
            PanelBuilder builder = new PanelBuilder(layout, getPanel());

            builder.addLabel("<HTML><h3 style=\"margin-bottom:0;\">" + I18nSupport.translate("actions.info.series") + "</h3>" +
                    "<ul style=\"margin-top:0;margin-bottom:0;\"><li><em>" + series.getName() + "</em> - [" +
                    series.getSeriesMinValue() + " --> " + series.getSeriesMaxValue() +
                    "] " + series.getType().getUnit() + "</li></ul>" + "</HTML>");

            builder.nextLine();

            final String absoluteFile = "<HTML><h3 style=\"margin-bottom:0;\">" + I18nSupport.translate("actions.info.filename") + "</h3>" +
                    series.getOrigin().getAbsolutePath() + "</HTML>";
            final String file = "<HTML><h3 style=\"margin-bottom:0;\">" + I18nSupport.translate("actions.info.filename") + "</h3>" +
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
            builder.addLabel("<HTML><h3 style=\"margin-bottom:0;\">" + I18nSupport.translate("actions.info.dateRange") + "</h3>" +
                    series.getRange().toString() + "</HTML>");

            builder.nextLine();
            String seriesStringBlack = "<HTML><h3 style=\"margin-bottom:0;\">" +
                    I18nSupport.translate("actions.info.sameFileSeries") +
                    "</h3>" +
                    "<ul style=\"margin-top:0;\">";
            for (Series s : Series.getInstances()) {
                if (!s.equals(series)) {
                    if (s.getOrigin().equals(series.getOrigin())) {
                        seriesStringBlack += ("<li>" + s.toString(true) + "</li>");
                    }
                }
            }
            seriesStringBlack += "</ul></HTML>";
            final String black = seriesStringBlack;

            final JLabel seriesLabel = new JLabel(seriesStringBlack);
            seriesLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    clickCount++;
                    switch (clickCount) {
                        case 10:
                            seriesLabel.setText("<HTML><h3>Bon, ça ira maintenant ? C'est bon ?</h3></HTML>");
                            break;
                        case 15:
                            seriesLabel.setText("<HTML><h3>Ah ça suffit hein !</h3></HTML>");
                            break;
                        case 20:
                            seriesLabel.setText("<HTML><h3>J'irai me plaindre aux développeurs...</h3></HTML>");
                            break;
                        default:
                            seriesLabel.setText(black);
                            break;
                    }
                    pack();
                    centerOnScreen();
                }
            });
            builder.add(seriesLabel);


            JPanel buttonPanel= new JPanel();
            ButtonBarBuilder buttonBuilder = new ButtonBarBuilder(buttonPanel);
            buttonBuilder.addGlue();
            {
                buttonBuilder.addButton(new AbstractAction() {

                    {
                        putValue(NAME, I18nSupport.translate("ok"));
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        validateForm();
                    }
                });
            }

            buttonBuilder.build();
            buttonPanel.setVisible(true);
            builder.nextLine();
            builder.add(buttonPanel);
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
