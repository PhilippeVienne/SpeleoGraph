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

package org.cds06.speleograph.graph;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.lang3.Validate;
import org.cds06.speleograph.GraphPanel;
import org.cds06.speleograph.I18nSupport;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.plot.XYPlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Create a Dialog to personalize a {@link org.jfree.chart.JFreeChart}.
 * <p>The user can edit axes, background colors, titles ...</p>
 */
public class    GraphEditor extends JDialog {

    /**
     * Attached graph panel where put user choices.
     */
    private final GraphPanel graphPanel;

    /**
     * Main panel for this dialog
     */
    private final JPanel mainPanel = new JPanel(new BorderLayout());


    /**
     * Logger for errors and info.
     */
    @SuppressWarnings("UnusedDeclaration")
    @NonNls
    private static Logger log = LoggerFactory.getLogger(GraphEditor.class);

    /**
     * Creates a modal dialog by specifying the attached {@link GraphPanel}.
     * <p>Please look at {@link javax.swing.JDialog#JDialog()} to see defaults params applied to this Dialog.</p>
     */
    public GraphEditor(GraphPanel panel) {
        super((Frame) SwingUtilities.windowForComponent(panel), true);
        Validate.notNull(panel);
        this.graphPanel = panel;
        setContentPane(mainPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setTitle(I18nSupport.translate("menus.graph.graphEditor"));

        {
            // This section use FormLayout which is an external layout for Java, consult the doc before edit the
            // following lines !
            final FormLayout layout = new FormLayout(
                    "right:max(40dlu;p), 4dlu, p:grow, 4dlu, p, 4dlu, p:grow, 4dlu, p", //NON-NLS
                    "p,4dlu,p,4dlu,p,4dlu,p,4dlu,p,4dlu,p" //NON-NLS
            );


            PanelBuilder builder = new PanelBuilder(layout);

            final JLabel colorLabel = new JLabel();
            final JTextField titleForGraph = new JTextField(graphPanel.getChart().getTitle() != null ? graphPanel.getChart().getTitle().getText() : "");
            final JLabel colorXYPlotLabel = new JLabel();
            final JLabel colorGridLabel = new JLabel();
            final JCheckBox showLegendCheckBox = new JCheckBox(
                    "Afficher la légende", graphPanel.getChart().getLegend().isVisible());

            {
                builder.addLabel("Titre :", "1,1");
                builder.add(titleForGraph, "3,1,7,1");
            }

            {
                builder.addLabel("Fond de l'image", "1,3");
                colorLabel.setBorder(BorderFactory.createLineBorder(
                        Color.BLACK
                ));
                colorLabel.setText(" ");
                colorLabel.setBackground((Color) graphPanel.getChart().getBackgroundPaint());
                colorLabel.setOpaque(true);
                colorLabel.setEnabled(false);
                final JButton edit = new JButton("Editer");
                edit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Color c = JColorChooser.showDialog(GraphEditor.this, "Selectionnez une couleur", colorLabel.getBackground());
                        if (c != null) {
                            colorLabel.setBackground(c);
                        }
                    }
                });
                builder.add(colorLabel, "3,3,5,1");
                builder.add(edit, "9,3");
            }

            final XYPlot xyPlot = graphPanel.getChart().getXYPlot();
            {
                builder.addLabel("Fond de la zone graphique", "1,5");
                colorXYPlotLabel.setText(" ");
                colorXYPlotLabel.setOpaque(true);
                colorXYPlotLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                colorXYPlotLabel.setBackground(
                        (Color) xyPlot.getBackgroundPaint()
                );
                colorXYPlotLabel.setEnabled(false);
                final JButton edit = new JButton("Editer");
                edit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Color c = JColorChooser.showDialog(GraphEditor.this, "Selectionnez une couleur",
                                colorXYPlotLabel.getBackground());
                        if (c != null) {
                            colorXYPlotLabel.setBackground(c);
                        }
                    }
                });
                builder.add(colorXYPlotLabel, "3,5,5,1");
                builder.add(edit, "9,5");
            }

            {
                builder.addLabel("Couleur de la grille", "1,7");
                colorGridLabel.setOpaque(true);
                colorGridLabel.setText(" ");
                colorGridLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                colorGridLabel.setBackground(
                        (Color) xyPlot.getRangeGridlinePaint()
                );
                colorGridLabel.setEnabled(false);
                final JButton edit = new JButton("Editer");
                edit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Color c = JColorChooser.showDialog(GraphEditor.this, "Selectionnez une couleur",
                                colorGridLabel.getBackground());
                        if (c != null) {
                            colorGridLabel.setBackground(c);
                        }
                    }
                });
                builder.add(colorGridLabel, "3,7,5,1");
                builder.add(edit, "9,7");
            }

            {
                builder.add(showLegendCheckBox, "1,9,9,1");
            }

            mainPanel.add(builder.build(), BorderLayout.CENTER);

            ButtonBarBuilder buttonBarBuilder = new ButtonBarBuilder();

            buttonBarBuilder.addGlue();

            buttonBarBuilder.addButton(new AbstractAction() {
                {
                    putValue(NAME, I18nSupport.translate("cancel"));
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    GraphEditor.this.setVisible(false);
                }
            });

            buttonBarBuilder.addButton(new AbstractAction() {

                {
                    putValue(NAME, I18nSupport.translate("ok"));
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (titleForGraph.getText().isEmpty())
                        graphPanel.getChart().setTitle((String) null);
                    else
                        graphPanel.getChart().setTitle(titleForGraph.getText());
                    {
                        Color c = colorGridLabel.getBackground();
                        xyPlot.setRangeGridlinePaint(c);
                        xyPlot.setRangeMinorGridlinePaint(c);
                        xyPlot.setDomainGridlinePaint(c);
                        xyPlot.setDomainMinorGridlinePaint(c);
                    }
                    graphPanel.getChart().setBackgroundPaint(colorLabel.getBackground());
                    xyPlot.setBackgroundPaint(colorXYPlotLabel.getBackground());
                    graphPanel.getChart().getLegend().setVisible(showLegendCheckBox.isSelected());
                    GraphEditor.this.setVisible(false);
                }
            });

            mainPanel.add(buttonBarBuilder.build(), BorderLayout.SOUTH);
        }

        setSize(300, 300);
    }

    /**
     * Getter for the linked {@link GraphPanel}
     *
     * @return A non-null instance of GraphPanel
     */
    @SuppressWarnings("UnusedDeclaration")
    @NotNull
    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}
