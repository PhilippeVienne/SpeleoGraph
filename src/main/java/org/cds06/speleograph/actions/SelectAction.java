package org.cds06.speleograph.actions;

import org.cds06.speleograph.CheckBoxList;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Gabriel.
 */
public class SelectAction extends AbstractAction {
    private final int value;
    private final CheckBoxList list;
    private final JCheckBox applyToLastFile;

    /**
     * Instanciate a select action depending of the value put in.
     * @param value 1 to tick everything, 0 to untick everything, -1 to invert selection.
     * @param applyToLastFile A checkbox to determine whether the selection applies to last file or all series.
     */
    public SelectAction(int value, CheckBoxList list, JCheckBox applyToLastFile) {
        this.value = value;
        this.list = list;
        this.applyToLastFile = applyToLastFile;
        switch (value) {
            case 1:
                putValue(NAME, I18nSupport.translate("list.tickAll"));
                break;
            case 0:
                putValue(NAME, I18nSupport.translate("list.untickAll"));
                break;
            case -1:
                putValue(NAME, I18nSupport.translate("list.invert"));
                break;
            default:
                putValue(NAME, I18nSupport.translate("unknown"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (value) {
            case 1:
                for (int i = 0; i < list.getModel().getSize(); i++) {
                    Series s = list.getModel().getElementAt(i);
                    if (!applyToLastFile.isSelected())
                        s.setShow(true);
                    else if (s.getOrigin().equals(Series.getLastOpenedFile()))
                        s.setShow(true);
                }
                break;
            case 0:
                for (int i = 0; i < list.getModel().getSize(); i++) {
                    Series s = list.getModel().getElementAt(i);
                    if (!applyToLastFile.isSelected())
                        s.setShow(false);
                    else if (s.getOrigin().equals(Series.getLastOpenedFile()))
                        s.setShow(false);
                }
                break;
            case -1:
                for (int i = 0; i < list.getModel().getSize(); i++) {
                    Series s = list.getModel().getElementAt(i);
                    if (!applyToLastFile.isSelected())
                        s.setShow(!s.isShow());
                    else if (s.getOrigin().equals(Series.getLastOpenedFile()))
                        s.setShow(!s.isShow());
                }
                break;
            default:
                break;
        }
    }
}
