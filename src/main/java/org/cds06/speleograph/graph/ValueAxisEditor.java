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
import org.cds06.speleograph.utils.FormDialog;
import org.jfree.chart.axis.ValueAxis;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ValueAxisEditor extends FormDialog {

    private final ValueAxis axis;

    public ValueAxisEditor(ValueAxis axis) {
        super();
        this.axis = axis;
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
            final JSpinner spinner = new JSpinner();
            spinner.setValue(axis.getRange().getLowerBound());
            builder.add(spinner, cc.rc(2, 3));
            addListenerOnSuccess(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    axis.setLowerBound((Double) spinner.getValue());
                }
            });
        }

        {
            builder.add(new JLabel("Valeur max. :"), cc.rc(3, 1));
            final JSpinner spinner = new JSpinner();
            spinner.setValue(axis.getRange().getUpperBound());
            builder.add(spinner, cc.rc(3, 3));
            addListenerOnSuccess(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    axis.setUpperBound((Double) spinner.getValue());
                }
            });
        }

        {

        }

        builder.build();
    }


}
