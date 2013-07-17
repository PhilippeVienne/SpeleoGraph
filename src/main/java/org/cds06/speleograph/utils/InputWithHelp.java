package org.cds06.speleograph.utils;

import javax.swing.*;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class InputWithHelp extends JPanel {

    private JLabel help = new JLabel("?");

    public InputWithHelp(JComponent component, String helpText) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(component);
//        add(Box.createHorizontalStrut(5));
//        add(help);
        setHelpText(helpText);
        validate();
    }

    public void setHelpText(String helpText) {
        help.setToolTipText(helpText);
    }

}
