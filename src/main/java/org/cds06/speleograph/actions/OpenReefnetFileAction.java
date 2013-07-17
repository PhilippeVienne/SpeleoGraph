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

import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action for opening a Reefnet File.
 */
public class OpenReefnetFileAction extends AbstractAction {

    /**
     * Logger for info and errors.
     */ @NonNls
    private static final Logger log = LoggerFactory.getLogger(OpenReefnetFileAction.class);

    /**
     * Parent component for dialog display.
     */
    private final JComponent parent;

    /**
     * Construct the import action.
     * @param component The parent component used to display dialogs.
     */
    public OpenReefnetFileAction(JComponent component) {
        super("Importer");
        parent = component;
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        log.info("Not implemented");
        parent.getDropTarget();
    }
}
