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

package org.cds06.speleograph.graph;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.utils.FormDialog;
import org.jetbrains.annotations.NonNls;
import org.jfree.chart.axis.NumberAxis;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ValueAxisEditor extends FormDialog {

    private final NumberAxis axis;
    private final Double oldHighValue;
    private final Double oldLowValue;

    private final JTextField maxField;
    private final JTextField maxModifier = new JTextField("0");
    private final JTextField lowField;
    private final JTextField minModifier = new JTextField("0");
    private final JSlider translateSlider;
    private final JSlider homotSlider;

    public ValueAxisEditor(NumberAxis axis) {
        super();
        this.axis = axis;
        this.oldLowValue = axis.getLowerBound();
        this.oldHighValue = axis.getUpperBound();
        this.maxField = new JTextField(Double.toString(axis.getUpperBound()));
        this.lowField = new JTextField(Double.toString(axis.getLowerBound()));
        int max = (int)(axis.getUpperBound()/5);
        translateSlider = new JSlider(Adjustable.VERTICAL, -max, max, 0);
        homotSlider = new JSlider(Adjustable.VERTICAL, -max, max, 0);

        construct();
        setTitle(I18nSupport.translate("graph.valueAxisEditor"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setup() {
        PanelBuilder builder = new PanelBuilder(getFormLayout(), getPanel());
        CellConstraints cc = new CellConstraints();

        Dimension d = maxModifier.getPreferredSize();
        maxModifier.setPreferredSize(new Dimension(d.width + 50, d.height));
        d = minModifier.getPreferredSize();
        minModifier.setPreferredSize(new Dimension(d.width + 50, d.height));

        {
            builder.add(new JLabel("Titre de l'axe :"));
            final JTextField axisTitleField = new JTextField();
            axisTitleField.setText(axis.getLabel());
            builder.nextColumn(2);
            builder.add(axisTitleField,cc.xyw(3,1,5));
            addListenerOnSuccess(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = axisTitleField.getText();
                    if (text != null && !text.equals("")) axis.setLabel(axisTitleField.getText());
                }
            });
        }

        {
            builder.nextLine(2);
            builder.add(new JLabel("Valeur min. :"));
            builder.nextColumn(2);
            builder.add(lowField);
            addListenerOnSuccess(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Double value = Double.valueOf(lowField.getText());
                        if (isApply) {
                            axis.setLowerBound(value);
                        } else if (isCancel && oldLowValue != null)
                            axis.setLowerBound(oldLowValue);
                    } catch (NumberFormatException e1) {
                        canClose = false;
                        JOptionPane.showMessageDialog(
                                ValueAxisEditor.this.getParent(),
                                "'" + lowField.getText() + "' n'est pas un nombre", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            builder.nextColumn(2);
            builder.add(new JLabel("+"));
            builder.nextColumn(2);
            builder.add(minModifier);
        }

        {
            builder.nextLine(2);
            builder.add(new JLabel("Valeur max. :"));
            builder.nextColumn(2);
            builder.add(maxField);
            addListenerOnSuccess(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Double value = Double.valueOf(maxField.getText());
                        if (isApply) {
                            axis.setUpperBound(value);
                        } else if (isCancel && oldHighValue != null)
                            axis.setUpperBound(oldHighValue);
                    } catch (NumberFormatException e1) {
                        canClose = false;
                        JOptionPane.showMessageDialog(
                                ValueAxisEditor.this.getParent(),
                                "'" + maxField.getText() + "' n'est pas un nombre", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            builder.nextColumn(2);
            builder.add(new JLabel("+"));
            builder.nextColumn(2);
            builder.add(maxModifier);
        }

        {
            builder.nextLine(2);
            builder.add(new JLabel("<HTML><strong>Séries associées à l'axe</strong></HTML>"), cc.xyw(1,7,3));
            String linkedSeries = "<html><ul>";
            for (Series series : Series.getInstances()) {
                if (series.getAxis().equals(axis))
                    linkedSeries += "<li>" + series.toString(true) + "</li>";
            }
            linkedSeries += "</ul></html>";

            builder.add(new JLabel(linkedSeries), cc.xyw(1,8,3));
        }

        JPanel buttonPanel= new JPanel();
        ButtonBarBuilder buttonBuilder = new ButtonBarBuilder(buttonPanel);
        buttonBuilder.addGlue();
        {
            buttonBuilder.addButton(new AbstractAction() {

                {
                    putValue(NAME, I18nSupport.translate("cancel"));
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    isCancel = true;
                    isApply = false;
                    canClose = true;
                    validateForm();
                }
            });
        }

        {
            buttonBuilder.addButton(new AbstractAction() {

                {
                    putValue(NAME, I18nSupport.translate("apply"));
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    isCancel = false;
                    isApply = true;
                    canClose = false;
                    validateForm();
                }
            });
        }

        {
            buttonBuilder.addButton(new AbstractAction() {

                {
                    putValue(NAME, I18nSupport.translate("ok"));
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    isCancel = false;
                    isApply = true;
                    canClose = true;
                    validateForm();
                }
            });
        }

        buttonBuilder.build();
        buttonPanel.setVisible(true);
        builder.add(buttonBuilder.getPanel(), cc.xyw(1,10,10));

        {
            translateSlider.setToolTipText("Translation des axes");
            translateSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int maxValue = -translateSlider.getValue();
                    maxModifier.setText(String.valueOf(maxValue));
                    int lowValue = -translateSlider.getValue();
                    minModifier.setText(String.valueOf(lowValue));
                }
            });
            builder.add(translateSlider, cc.xywh(9, 1, 1, 8));
        }
        {
            homotSlider.setToolTipText("Homothétie sur les axes");
            homotSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int maxValue = -homotSlider.getValue();
                    maxModifier.setText(String.valueOf(maxValue));
                    int lowValue = homotSlider.getValue();
                    minModifier.setText(String.valueOf(lowValue));
                }
            });
            builder.add(homotSlider, cc.xywh(10, 1, 1, 8));
        }

        builder.build();

        getPanel().setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        addListenerOnSuccess(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (canClose) setVisible(false);
            }
        });
    }

    private boolean canClose = false;
    private boolean isCancel = false;
    private boolean isApply = false;

    /**
     * {@inheritDoc}
     */
    protected void validateForm() {
        int maxValue = 0;
        int minValue = 0;
        try {
            maxValue = (int) (Double.parseDouble(maxField.getText()) + Double.parseDouble(maxModifier.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                ValueAxisEditor.this.getParent(),
                "'" + maxField.getText() + "'" + " ou '" + maxModifier.getText() + "' n'est pas un nombre", "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
        try {
            minValue = (int) (Double.parseDouble(lowField.getText()) + Double.parseDouble(minModifier.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                ValueAxisEditor.this.getParent(),
                "'" + lowField.getText() + "'" + " ou '" + minModifier.getText() + "' n'est pas un nombre", "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
        translateSlider.setValue(0);
        homotSlider.setValue(0);
        maxField.setText(String.valueOf(maxValue));
        lowField.setText(String.valueOf(minValue));

        firePropertyChange(FORM_VALIDATED_PROPERTY, null, this);
    }

    @NonNls
    private static final FormLayout FORM_LAYOUT = new FormLayout("r:p,4dlu,p:grow,2dlu,p,2dlu,p,4dlu,p,p", "p,2dlu,p,2dlu,p,2dlu,p,p,4dlu,p");

    @Override
    protected FormLayout getFormLayout() {
        return FORM_LAYOUT;
    }


}
