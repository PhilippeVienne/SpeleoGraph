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

import org.apache.commons.lang3.Validate;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.SpeleoGraphApp;
import org.cds06.speleograph.data.SpeleoFileWriter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class SaveAction extends AbstractAction {


    /**
     * File Chooser for save location.
     */
    private JFileChooser chooser = new JFileChooser();

    private JComponent parent = null;

    private SpeleoFileWriter writer = new SpeleoFileWriter();

    /**
     * Construct the import action.
     *
     * @param parent The parent component used to display dialogs.
     */
    public SaveAction(JComponent parent){
        super(I18nSupport.translate("actions.save"));
        Validate.notNull(parent);
        this.parent = parent;
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        chooser.setCurrentDirectory(SpeleoGraphApp.getWorkingDirectory());
        chooser.
        int result = chooser.showSaveDialog(parent);
        File file;
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                file = chooser.getSelectedFile();
                if (file.isDirectory()) return;
                break;
            case JFileChooser.CANCEL_OPTION:
            default:
                SpeleoGraphApp.setWorkingDirectory(chooser.getCurrentDirectory());
                return;
        }
        try {
            SpeleoGraphApp.setWorkingDirectory(file.getParentFile());
            writer.save(file);
        } catch (IOException e1) {
            //log.error("Error when try to read a SpeleoGraph File", e1);
        }
    }
}
