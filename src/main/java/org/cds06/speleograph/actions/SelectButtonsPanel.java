package org.cds06.speleograph.actions;

import org.cds06.speleograph.CheckBoxList;
import org.cds06.speleograph.I18nSupport;
import org.jfree.ui.tabbedui.VerticalLayout;

import javax.swing.*;

/**
 * Created by Gabriel.
 */
public class SelectButtonsPanel extends JPanel {

    public SelectButtonsPanel(CheckBoxList list) {
        JCheckBox applyToNewFile = new JCheckBox(I18nSupport.translate("list.applyToLastFile", false));

        this.setLayout(new VerticalLayout());
        this.add(new JButton(new SelectAction(1, list, applyToNewFile)));
        this.add(new JButton(new SelectAction(0, list, applyToNewFile)));
        this.add(new JButton(new SelectAction(-1, list, applyToNewFile)));
        this.add(applyToNewFile);
    }
}
