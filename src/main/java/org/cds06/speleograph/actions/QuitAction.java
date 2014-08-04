package org.cds06.speleograph.actions;

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.SpeleoGraphApp;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel.
 */
public class QuitAction extends AbstractAction {
    private final JPanel panel;
    private final SpeleoGraphApp app;

    public QuitAction(JPanel panel, SpeleoGraphApp app) {
        super(I18nSupport.translate("actions.exit"));
        this.panel = panel;
        this.app = app;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(
                panel,
                I18nSupport.translate("actions.exit.confirm.message"),
                I18nSupport.translate("actions.exit.confirm.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            app.dispose();
        }
    }
}
