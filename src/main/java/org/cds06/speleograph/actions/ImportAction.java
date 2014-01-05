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

package org.cds06.speleograph.actions;

import org.cds06.speleograph.SpeleoGraphApp;
import org.cds06.speleograph.data.ImportWizard;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action used to describe a file import.
 *
 * @author Philippe VIENNE
 * @since 1.0
 */
public class ImportAction extends AbstractAction {

    /**
     * Parent component for dialog display.
     */
    private final JComponent parent;

    /**
     * Construct the import action.
     *
     * @param component The parent component used to display dialogs.
     */
    public ImportAction(JComponent component) {
        super("Importer");
        parent = component;
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int i = JOptionPane.showConfirmDialog(parent,
                "Cette fonctionnalité est très instable et peut amener à des erreur sur la lecture des graphiques.\n" +
                        "Ne continuez que si vous êtes sur de ce que vous faîtes.",
                "Attention", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (i != JOptionPane.OK_OPTION) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(SpeleoGraphApp.getWorkingDirectory());
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            SpeleoGraphApp.setWorkingDirectory(fileChooser.getSelectedFile().getParentFile());
            new ImportWizard(fileChooser.getSelectedFile()).openWizard();
        }
    }
}
