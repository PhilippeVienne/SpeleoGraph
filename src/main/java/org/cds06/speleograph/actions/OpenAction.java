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

import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.DataFileReader;
import org.cds06.speleograph.data.FileReadingError;
import org.cds06.speleograph.utils.AcceptedFileFilter;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class OpenAction extends AbstractAction {

    /**
     * Logger for info and errors.
     */ @NonNls
    private static final Logger log = LoggerFactory.getLogger(OpenAction.class);

    /**
     * FileFilter for ReefNet files.
     */
    private final AcceptedFileFilter fileFilter;

    /**
     * File chooser for this action.
     */
    private final JFileChooser chooser;

    /**
     * Parent component for dialog display.
     */
    private final JComponent parent;

    /**
     * Data reader.
     */
    private final DataFileReader reader;

    /**
     * Construct the import action.
     * @param component The parent component used to display dialogs.
     */
    public OpenAction(JComponent component, Class<? extends DataFileReader> reader) {
        super(I18nSupport.translate("actions.openFile"));
        try {
            this.reader = reader.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            log.info("Can not create action for reader "+reader.getName());
            throw new IllegalArgumentException(e);
        }
        parent = component;
        fileFilter = new AcceptedFileFilter();
        fileFilter.acceptAllCSVAndTxt=true;
        fileFilter.acceptReefnet = false;
        fileFilter.acceptHobo = false;
        fileFilter.acceptFolders = true;
        chooser = new JFileChooser();
        chooser.setFileFilter(fileFilter);
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int result=chooser.showOpenDialog(parent);
        File file;
        switch (result){
            case JFileChooser.APPROVE_OPTION:
                file = chooser.getSelectedFile();
                if(file.isDirectory()) return;
                break;
            case JFileChooser.CANCEL_OPTION:
            default:
                return;
        }
        try {
            reader.readFile(file);
        } catch (FileReadingError e1) {
            log.error("Error when try to read a SpeleoGraph File", e1);
        }
    }
}