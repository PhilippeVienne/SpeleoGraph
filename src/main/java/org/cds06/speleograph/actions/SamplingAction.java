/*
 * Copyright (c) 2013 Philippe VIENNE
 *
 * This file is a part of SpeleoGraph
 *
 * SpeleoGraph is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * SpeleoGraph is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with SpeleoGraph.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.cds06.speleograph.actions;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.utils.FormDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Action to edit the Series Color on Screen.
 */
public class SamplingAction extends AbstractAction {

    private final Series series;

    public SamplingAction(Series series) {
        super(I18nSupport.translate("actions.sample"));
        this.series = series;
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        PromptDialog dialog = new PromptDialog();
        dialog.setVisible(true);
    }

    private class PromptDialog extends FormDialog {

        public JTextField name = new JTextField(series.getName());
        public JComboBox<Integer> timeTypeFieldBox = new JComboBox<>(new Integer[]{1, 60, 60 * 60, 60 * 60 * 24});

        {
            timeTypeFieldBox.setRenderer(new Renderer());
        }

        private class Renderer extends JLabel implements ListCellRenderer<Integer> {

            @Override
            public Component getListCellRendererComponent(
                    JList<? extends Integer> list,
                    Integer value, int index, boolean isSelected,
                    boolean cellHasFocus) {

                if (isSelected) {
                    //setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }

                String text;
                switch (value) {
                    case 1:
                        text = I18nSupport.translate("actions.sample.second");
                        break;
                    case 60:
                        text = I18nSupport.translate("actions.sample.minute");
                        break;
                    case 3600:
                        text = I18nSupport.translate("actions.sample.hour");
                        break;
                    case 86400:
                        text = I18nSupport.translate("actions.sample.day");
                        break;
                    default:
                        text = "x" + Integer.toString(value) + " " + I18nSupport.translate("actions.sample.seconds");
                }
                setText(text);
                return this;
            }
        }

        private JSpinner spinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 3600, 0.1));

        private PromptDialog() {
            super();
            construct();
        }

        @Override
        protected void setup() {

            timeTypeFieldBox.setSelectedItem(86400);

            PanelBuilder builder = new PanelBuilder(formLayout, getPanel());

            builder.addLabel(I18nSupport.translate("actions.sample.serieName") + " :", "1,1,3,1");
            builder.add(name, "1,2,3,1");

            builder.addLabel(I18nSupport.translate("actions.sample.noSampling") + " :", "1,3,3,1");
            builder.add(spinner, "1,4");
            builder.add(timeTypeFieldBox, "3,4");

            builder.add(new JButton(new AbstractAction() {

                {
                    putValue(NAME, I18nSupport.translate("actions.sample.sample"));
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    validateForm();
                }
            }), "1,6,3,1");

            builder.build();
        }

        @Override
        protected void validateForm() {
            long time = (long) (((Double) spinner.getValue()) * ((Integer) timeTypeFieldBox.getSelectedItem()) * 1000);
            series.generateSampledSeries(time).setName(name.getText().isEmpty() ? series.getName() : name.getText());
            setVisible(false);
        }

        private final FormLayout formLayout = new FormLayout("p:grow,4dlu,p", "p,p:grow,p,p:grow,4dlu:grow,p");

        @Override
        protected FormLayout getFormLayout() {
            return formLayout;
        }
    }
}
