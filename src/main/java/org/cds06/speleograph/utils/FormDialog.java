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

import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.SpeleoGraphApp;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Dialog usable to display a Panel.
 */
public abstract class FormDialog extends JDialog {

    @NonNls
    protected static String DEFAULT_LAYOUT_COLUMNS = "l:p,4dlu,p:grow";
    protected static String DEFAULT_LAYOUT_LINES = "";
    protected static final String FORM_VALIDATED_PROPERTY = "formValidated"; // NON-NLS

    protected final JPanel contentPane = new JPanel();

    {
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public JPanel getPanel() {
        return contentPane;
    }

    public FormDialog() {
        super(SpeleoGraphApp.getInstance(), true);
        FormLayout layout;
        if (getLayout() != null) {
            layout = getFormLayout();
        } else {
            layout = new FormLayout(DEFAULT_LAYOUT_COLUMNS, DEFAULT_LAYOUT_LINES);
        }
        contentPane.setLayout(layout);
    }

    /**
     * This function construct the content pane.
     * <p>You should call this method in the constructor after assigned arguments.</p>
     * <p>This function will : <ul>
     * <li>Setup the content pane</li>
     * <li>Call the {@link #setup()} function to populate the pane.</li>
     * <li>Set a default size (from the content pane preferred size)</li>
     * <li>Center the frame on the screen</li>
     * </ul></p>
     */
    protected void construct() {
        setContentPane(contentPane);
        setup();
        Dimension d = contentPane.getPreferredSize();
        d.height = d.height + 20;
        setSize(d);
        setMinimumSize(d);
        centerOnScreen();
    }

    /**
     * Center the dialog on screen.
     */
    protected void centerOnScreen() {
        int x = SpeleoGraphApp.getInstance().getX() +
                (SpeleoGraphApp.getInstance().getWidth() / 2 - getWidth() / 2);
        int y = SpeleoGraphApp.getInstance().getY() +
                (SpeleoGraphApp.getInstance().getHeight() / 2 - getHeight() / 2);
        setLocation(x, y);
    }

    protected void addListenerOnSuccess(final ActionListener listener) {
        addPropertyChangeListener(FORM_VALIDATED_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                ActionEvent e = new ActionEvent(FormDialog.this, 0, FORM_VALIDATED_PROPERTY);
                listener.actionPerformed(e);
            }
        });
    }

    /**
     * This function add component to the main panel.
     * <p>You have to override this function to add and setup your dialog</p>
     */
    protected abstract void setup();

    protected abstract void validateForm();

    protected abstract FormLayout getFormLayout();

}
