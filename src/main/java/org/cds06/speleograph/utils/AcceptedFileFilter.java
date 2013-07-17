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

package org.cds06.speleograph.utils;

import org.cds06.speleograph.I18nSupport;
import org.jetbrains.annotations.NonNls;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * File filter for files which can be read by SpeleoGraph.
 *
 * @author Philippe VIENNE
 * @since 1.0
 */
public class AcceptedFileFilter extends FileFilter {
    /**
     * Whether the given file is accepted by this filter.
     * @return true if it's accepted
     */
    @Override
    @NonNls
    public boolean accept(File f) {
        return f.getName().endsWith(".txt") || f.getName().endsWith(".csv") || f.isDirectory();
    }

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     *
     * @see javax.swing.filechooser.FileView#getName
     */
    @Override
    public String getDescription() {
        return I18nSupport.translate("fileFilter.csvFormat");
    }
}
