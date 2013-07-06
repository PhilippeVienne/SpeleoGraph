package org.cds06.speleograph;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class CheckBoxList extends JList<DataSet> {
    protected static Border noFocusBorder =
            new EmptyBorder(1, 1, 1, 1);

    public CheckBoxList(ListModel<DataSet> model) {
        super(model);

        setCellRenderer(new CellRenderer());

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());

                if (index != -1) {
                    DataSet value = getModel().getElementAt(index);
                    value.shownProperty().set(!value.shownProperty().get());
                    repaint();
                }
            }
        }
        );

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    protected final class CellRenderer implements ListCellRenderer<DataSet> {

        private JCheckBox box = new JCheckBox();

        public Component getListCellRendererComponent(
                JList list, DataSet value, int index,
                boolean isSelected, boolean cellHasFocus) {
            box.setSelected(value.shownProperty().get());
            box.setText(value.getName());
            box.setBackground(isSelected ?
                    getSelectionBackground() : getBackground());
            box.setForeground(isSelected ?
                    getSelectionForeground() : getForeground());
            box.setEnabled(isEnabled());
            box.setFont(getFont());
            box.setFocusPainted(false);
            box.setBorderPainted(true);
            box.setBorder(isSelected ?
                    UIManager.getBorder(
                            "List.focusCellHighlightBorder") : noFocusBorder);
            return box;
        }
    }
}
