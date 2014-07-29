package org.cds06.speleograph.actions;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel on 29/07/2014.
 */
public class RecalibrateAction extends AbstractAction {

    private final Series series;

    public RecalibrateAction(Series series) {
        super(I18nSupport.translate("actions.recalibrate"));
        this.series = series;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null, "Test");

    }
}
