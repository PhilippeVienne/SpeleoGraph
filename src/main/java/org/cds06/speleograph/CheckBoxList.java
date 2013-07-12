package org.cds06.speleograph;

import org.cds06.speleograph.data.Series;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class CheckBoxList extends JList<Series> {
    protected static Border noFocusBorder =
            new EmptyBorder(1, 1, 1, 1);

    private JPopupMenu popupMenu = new JPopupMenu();

    public CheckBoxList(ListModel<Series> model) {
        super(model);

        setCellRenderer(new CellRenderer());

        model.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                repaint();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                repaint();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());

                if (index != -1) {
                    Series value = getModel().getElementAt(index);
                    switch (e.getButton()) {
                        case MouseEvent.BUTTON1:
                            value.setShow(!value.isShow());
                            repaint();
                            break;
                        case MouseEvent.BUTTON3:
                            openPopupMenuFor(value, e);
                            break;
                    }
                }
            }
        }
        );

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void openPopupMenuFor(final Series series, MouseEvent mouseEvent) {
        popupMenu.removeAll();
        final JMenuItem renameItem = new JMenuItem("Renommer la série");
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                popupMenu.setVisible(false);
                String newName = "";
                while (newName.equals("")) {
                    newName = (String) JOptionPane.showInputDialog(
                            CheckBoxList.this,
                            "Entrez un nouveau nom pour la série",
                            null,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            null,
                            series.getName()
                    );
                }
                series.setName(newName);

            }
        });
        popupMenu.add(renameItem);

        popupMenu.show(this, mouseEvent.getX(), mouseEvent.getY());
    }

    protected final class CellRenderer implements ListCellRenderer<Series> {

        private JCheckBox box = new JCheckBox();

        public Component getListCellRendererComponent(
                JList list, Series value, int index,
                boolean isSelected, boolean cellHasFocus) {
            box.setSelected(value.isShow());
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
