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

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an error which blocks FileReading.
 *
 * @author Philippe VIENNE
 * @since 1.0
 */
public class FileReadingError extends Exception{

    private Part part;

    public Part getPart() {
        return part;
    }

    /**
     * Represent the part where the problem occurs.
     */
    public enum Part{
        HEAD, BODY, DATA, VALUE
    }

    public FileReadingError(@Nls String message, @NotNull Part part){
        super(message);
        this.part = part;
    }

    public FileReadingError(@Nls String message, @NotNull Part part, @NotNull Throwable throwable){
        super(message,throwable);
        this.part = part;
    }

}
