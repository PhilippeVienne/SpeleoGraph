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

package org.cds06.speleograph;

import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.data.Type;
import org.cds06.speleograph.graph.DrawStyle;
import org.cds06.speleograph.graph.DrawStyles;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
            @Override
            public void mouseClicked(MouseEvent e) {
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

        if (series.getType().equals(Type.WATER)) {
            JMenuItem samplingItem = new JMenuItem("Créer une série échantillonée");
            samplingItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        boolean hasANumber = false;
                        int duration = 60 * 60 * 24;
                        while (!hasANumber)
                            try {
                                String result = JOptionPane.showInputDialog(CheckBoxList.this, "Quel est la longueur de l'échantillonage (en secondes) ?", 60 * 60 * 24);
                                duration = Integer.parseInt(result);
                                hasANumber = true;
                            } catch (NumberFormatException e1) {
                                hasANumber = false;
                            }
                        boolean hasAName = false;
                        String name = "";
                        while (!hasAName)
                            try {
                                name = JOptionPane.showInputDialog(CheckBoxList.this, "Quel est le nom de la nouvelle série ?", series.getName());
                                hasAName = !"".equals(name);
                            } catch (Exception e1) {
                                hasAName = false;
                            }
                        series.generateSampledSeries(1000 * duration).setName(name);
                    } catch (Exception e1) {
                        LoggerFactory.getLogger(CheckBoxList.class).error("Erreur lors de l'échantillonage", e1);
                    }
                }
            });
            popupMenu.add(samplingItem);
        }

        {
            JMenuItem samplingItem = new JMenuItem("Supprimer la série");
            samplingItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(CheckBoxList.this, "Etes-vous sur de vouloir supprimer cette série", "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
                        series.delete();
                    }
                }
            });
            popupMenu.add(samplingItem);
        }

        {
            final JMenuItem up = new JMenuItem("Remonter dans la liste"),
                    down = new JMenuItem("Descendre dans la liste");
            ActionListener listener = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource().equals(up)) {
                        series.upSeriesInList();
                    } else {
                        series.downSeriesInList();
                    }
                }
            };
            up.addActionListener(listener);
            down.addActionListener(listener);
            popupMenu.addSeparator();
            if (series.isFirst()) {
                popupMenu.add(down);
            } else if (series.isLast()) {
                popupMenu.add(up);
            } else {
                popupMenu.add(up);
                popupMenu.add(down);
            }
            popupMenu.addSeparator();
        }

        {
            JMenuItem colorItem = new JMenuItem("Couleur de la série");
            colorItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    series.setColor(JColorChooser.showDialog(
                            CheckBoxList.this,
                            I18nSupport.translate("actions.selectColorForSeries"),
                            series.getColor()));
                }
            });
            popupMenu.add(colorItem);
        }

        {
            JMenu plotRenderer = new JMenu("Affichage de la série");
            final ButtonGroup modes = new ButtonGroup();
            java.util.List<DrawStyle> availableStyles;
            if (series.getType().isHighLowType()) {
                availableStyles = DrawStyles.getDrawableStylesForHighLow();
            } else {
                availableStyles = DrawStyles.getDrawableStyles();
            }
            for (final DrawStyle s : availableStyles) {
                final JRadioButtonMenuItem item = new JRadioButtonMenuItem(
                        DrawStyles.getHumanCheckboxText(s)
                );
                item.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        if (item.isSelected())
                            series.setStyle(s);
                    }
                });
                modes.add(item);
                if (s.equals(series.getStyle())) {
                    modes.setSelected(item.getModel(), true);
                }
                plotRenderer.add(item);
            }
            popupMenu.add(plotRenderer);
        }

        popupMenu.show(this, mouseEvent.getX(), mouseEvent.getY());
    }

    protected final class CellRenderer implements ListCellRenderer<Series> {

        private JCheckBox box = new JCheckBox();

        public Component getListCellRendererComponent(
                JList list, Series value, int index,
                boolean isSelected, boolean cellHasFocus) {
            box.setSelected(value.isShow());
            box.setText(value.getName());
//            box.setBackground(isSelected ?
//                    getSelectionBackground() : getBackground());
//            box.setForeground(isSelected ?
//                    getSelectionForeground() : getForeground());
            box.setEnabled(isEnabled());
            box.setFont(getFont());
            box.setFocusPainted(false);
            box.setBorderPainted(true);
//            box.setBorder(isSelected ?
//                    UIManager.getBorder(
//                            "List.focusCellHighlightBorder") : noFocusBorder);
            return box;
        }
    }
}
