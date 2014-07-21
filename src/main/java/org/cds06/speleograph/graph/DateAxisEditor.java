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
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.SpeleoGraphApp;
import org.cds06.speleograph.utils.DateSelector;
import org.jfree.chart.axis.DateAxis;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Create a panel to edit a Date axis.
 *
 * @author Philippe VIENNE
 * @since 1.0
 */
public class DateAxisEditor extends JDialog {

    /**
     * The modified axis.
     */
    private DateAxis axis;


    final private JComboBox<HumanSimpleDateFormat> dateSelector = new JComboBox<>(new HumanSimpleDateFormat[]{
            new HumanSimpleDateFormat("MM/yyyy"),
            new HumanSimpleDateFormat("dd/MM/yyyy"),
            new HumanSimpleDateFormat("dd MMM yy"),
            new HumanSimpleDateFormat("dd MMM"),
            new HumanSimpleDateFormat("dd/MM"),
            new HumanSimpleDateFormat("dd MMM, HH:mm"),
            new HumanSimpleDateFormat("dd, HH:mm"),
            new HumanSimpleDateFormat("HH:mm"),
            new HumanSimpleDateFormat("HH:mm:ss"),
    });

    final private DateSelector minDate = new DateSelector();
    final private DateSelector maxDate = new DateSelector();


    public DateAxisEditor(DateAxis dateAxis) {
        super(SpeleoGraphApp.getInstance(), true);
        this.axis = dateAxis;
        this.setTitle(I18nSupport.translate("graph.dateAxisEditor"));
        JPanel panel = new JPanel();
        panel.setLayout(new FormLayout(
                "r:p,4dlu,p:grow,4dlu",
                "p:grow,p,4dlu:grow,p,4dlu:grow,p,4dlu:grow,p,p:grow"
        ));
        CellConstraints cc = new CellConstraints();
        panel.add(new JLabel("Format :"), cc.xy(1, 2));
        panel.add(dateSelector, cc.xy(3, 2));
        panel.add(new JLabel("Date début :"), cc.xy(1, 4));
        panel.add(minDate, cc.xy(3, 4));
        panel.add(new JLabel("Date fin :"), cc.xy(1, 6));
        panel.add(maxDate, cc.xy(3, 6));

        ButtonBarBuilder barBuilder = new ButtonBarBuilder();
        barBuilder.addGlue();

        //Cancel button
        barBuilder.addButton(new AbstractAction() {

            {
                putValue(NAME, I18nSupport.translate("cancel"));
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        //Ok button
        barBuilder.addButton(new AbstractAction() {

            {
                putValue(NAME, I18nSupport.translate("ok"));
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                axis.setDateFormatOverride((DateFormat) dateSelector.getSelectedItem());
                axis.setMinimumDate(minDate.getDate());
                axis.setMaximumDate(maxDate.getDate());
                setVisible(false);
            }
        });

        panel.add(barBuilder.build(), cc.xyw(1, 8, 3));
        panel.add(new JLabel(""), cc.xyw(1, 9, 3)); //Used to set a good looking OK button

        minDate.setDate(dateAxis.getMinimumDate());
        maxDate.setDate(dateAxis.getMaximumDate());
        if (dateAxis.getDateFormatOverride() != null && dateAxis.getDateFormatOverride() instanceof HumanSimpleDateFormat) {
            dateSelector.setSelectedItem(dateAxis.getDateFormatOverride());
        }

        setContentPane(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        setSize(panel.getPreferredSize().width + 100, panel.getPreferredSize().height + 100);

    }

    /**
     * A user understandable Date Format.
     * This SimpleDateFormat simply overrides the {@link #toString()} method to display the current system date
     * formatted with the current date format.
     */
    private class HumanSimpleDateFormat extends SimpleDateFormat {

        private HumanSimpleDateFormat(String pattern) {
            super(pattern);
        }

        public String toString() {
            return format(Calendar.getInstance().getTime());
        }

    }
}
