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

package org.cds06.speleograph.utils;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.util.Calendar;
import java.util.Date;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class DateSelector extends JPanel {

    private final JDateChooser dateChooser = new JDateChooser();
    private final JComboBox<Integer> hourChooser = new JComboBox<>(new Integer[]{
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23
    });
    private final JComboBox<Integer> minuteChooser = new JComboBox<>(new Integer[]{
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,
            39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59
    });

    public DateSelector() {
        setLayout(new FormLayout("p:grow(0.6),4dlu,p:grow(0.2),2dlu,l:6dlu,4dlu,p:grow(0.2),2dlu,l:7dlu", "p")); // NON-NLS

        CellConstraints cc = new CellConstraints();
        add(dateChooser, cc.rc(1, 1));
        add(hourChooser, cc.rc(1, 3));
        add(new JLabel("h"), cc.rc(1, 5)); //NON-NLS
        add(minuteChooser, cc.rc(1, 7));
        add(new JLabel("m"), cc.rc(1, 9)); //NON-NLS

        revalidate();
    }

    public void setDate(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        dateChooser.setDate(c.getTime());
        c.setTime(date);
        hourChooser.setSelectedItem(c.get(Calendar.HOUR));
        minuteChooser.setSelectedItem(c.get(Calendar.MINUTE));
    }

    public Date getDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(dateChooser.getDate());
        c.add(Calendar.HOUR, (Integer) hourChooser.getSelectedItem());
        c.add(Calendar.MINUTE, (Integer) minuteChooser.getSelectedItem());
        return c.getTime();
    }

}
