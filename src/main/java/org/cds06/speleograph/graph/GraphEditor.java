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
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Create a Dialog to personalize a {@link org.jfree.chart.JFreeChart}.
 * <p>The user can edit axes, background colors, titles ...</p>
 */
public class GraphEditor extends JDialog {

    /**
     * Attached graph panel where put user choices.
     */
    private final GraphPanel graphPanel;

    /**
     * Main panel for this dialog
     */
    private final JPanel mainPanel = new JPanel(new BorderLayout());

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

        {
            final FormLayout layout = new FormLayout(
                    "right:max(40dlu;p), 4dlu, p:grow, 4dlu, p, 4dlu, p:grow, 4dlu, p", //NON-NLS
                    "p,4dlu,p,4dlu,p,4dlu,p,4dlu,p" //NON-NLS
            );


            PanelBuilder builder = new PanelBuilder(layout);

            builder.addLabel("Fond de l'image", "1,1");
            final JTextField colorLabel = new JTextField();
            colorLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            colorLabel.setBackground(Color.BLUE);
            colorLabel.setText("     ");
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
            builder.add(colorLabel, "3,1,5,1");
            builder.add(edit, "9,1");

            mainPanel.add(builder.build(), BorderLayout.CENTER);

            ButtonBarBuilder buttonBarBuilder = new ButtonBarBuilder();

            buttonBarBuilder.addGlue();
            buttonBarBuilder.addButton(new AbstractAction() {

                {
                    putValue(NAME, "Appliquer");
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    graphPanel.getChart().setBackgroundPaint(colorLabel.getBackground());
                    GraphEditor.this.setVisible(false);
                }
            });

            mainPanel.add(buttonBarBuilder.build(), BorderLayout.SOUTH);
        }

//        {
//            GridBagConstraints constraints = new GridBagConstraints();
//
//            JLabel label = new JLabel("Couleur du fond :");
//
//            final JTextField field = new JTextField();
//            field.setEnabled(false);
//            field.setBackground((Color) graphPanel.getChart().getBackgroundPaint());
//
//            final JButton select = new JButton("Selectionner");
//            select.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    Color c = JColorChooser.showDialog(GraphEditor.this,"Selectinnez la couleur",select.getBackground());
//                    if(c!=null){
//                        select.setBackground(c);
//                        graphPanel.getChart().setBackgroundPaint(c);
//                    }
//                }
//            });
//
//            CellConstraints cc = new CellConstraints();
//
//            mainPanel.add(label,cc.xy(1,1));
//            mainPanel.add(select,cc.xyw(3, 1, 2));
//        }

        setSize(300, 200);
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
