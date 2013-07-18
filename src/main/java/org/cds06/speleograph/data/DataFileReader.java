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

package org.cds06.speleograph.data;

import java.io.File;

/**
 * Describe a class able to read a file to SpeleoGraph and load data into DataSets.
 */
public interface DataFileReader {

    public void readFile(File file) throws FileReadingError;

    /**
     * Get the name of file read by this class.
     * @return The localized name of file.
     */
    public String getName();

    /**
     * Get the text for buttons or menus.
     * @return The localized text.
     */
    public String getButtonText();

}
