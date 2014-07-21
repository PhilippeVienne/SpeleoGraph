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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.utils.FormDialog;
import org.jetbrains.annotations.NonNls;
import org.jfree.chart.axis.NumberAxis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ValueAxisEditor extends FormDialog {

    private final NumberAxis axis;

    public ValueAxisEditor(NumberAxis axis) {
        super();
        this.axis = axis;
        construct();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setup() {
        PanelBuilder builder = new PanelBuilder((FormLayout) getPanel().getLayout(), getPanel());
        CellConstraints cc = new CellConstraints();

        {
            builder.add(new JLabel("Titre de l'axe :"), cc.rc(1, 1));
            final JTextField axisTitleField = new JTextField();
            axisTitleField.setText(axis.getLabel());
            builder.add(axisTitleField, cc.rc(1, 3));
            addListenerOnSuccess(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    axis.setLabel(axisTitleField.getText());
                }
            });
        }

        {
            builder.add(new JLabel("Valeur min. :"), cc.rc(2, 1));
            final JTextField field = new JTextField(Double.toString(axis.getLowerBound()));
            builder.add(field, cc.rc(2, 3));
            addListenerOnSuccess(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        axis.setLowerBound(Double.valueOf(field.getText()));
                    } catch (NumberFormatException e1) {
                        canClose = false;
                        JOptionPane.showMessageDialog(
                                ValueAxisEditor.this.getParent(),
                                "'" + field.getText() + "' n'est pas un nombre", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        {
            builder.add(new JLabel("Valeur max. :"), cc.rc(3, 1));
            final JTextField field = new JTextField(Double.toString(axis.getUpperBound()));
            builder.add(field, cc.rc(3, 3));
            addListenerOnSuccess(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        axis.setUpperBound(Double.valueOf(field.getText()));
                    } catch (NumberFormatException e1) {
                        canClose = false;
                        JOptionPane.showMessageDialog(
                                ValueAxisEditor.this.getParent(),
                                "'" + field.getText() + "' n'est pas un nombre", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        {
            builder.add(new JButton(new AbstractAction() {

                {
                    putValue(NAME, I18nSupport.translate("ok"));
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    validateForm();
                }
            }), cc.rcw(4, 1, 3));
        }

        /*{
            builder.add(new JButton(new AbstractAction() {

                {
                    putValue(NAME, I18nSupport.translate("cancel"));
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            }), cc.rcw(5, 1, 3));
        }*/

        builder.build();

        getPanel().setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        Dimension dim = getPanel().getPreferredSize();
        getPanel().setPreferredSize(new Dimension(dim.width + 100, dim.height + 50));

        addListenerOnSuccess(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (canClose) setVisible(false);
                canClose = true;
            }
        });
    }

    private boolean canClose = true;

    /**
     * {@inheritDoc}
     */
    protected void validateForm() {
        firePropertyChange(FORM_VALIDATED_PROPERTY, null, this);
    }

    @NonNls
    private static final FormLayout FORM_LAYOUT = new FormLayout("r:p,4dlu,p:grow", "p,p,p,p");

    @Override
    protected FormLayout getFormLayout() {
        return FORM_LAYOUT;
    }


}
