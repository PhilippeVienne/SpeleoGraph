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
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

import javax.swing.*;
import java.util.List;

/**
 * Model to retrieve series opened in the global.
 *
 * @author Philippe VIENNE
 * @since 1.0
 */
public class SpeleoSeriesListModel extends AbstractListModel<Series> implements DatasetChangeListener {

    private static final long serialVersionUID = 1L;

    private List<Series> sets = Series.getInstances();

    {
        Series.addListener(this);
    }

    @Override
    public int getSize() {
        return sets.size();
    }

    @Override
    public Series getElementAt(int index) {
        return sets.get(index);
    }

    /**
     * Receives notification of an dataset change event.
     *
     * @param event information about the event.
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        fireContentsChanged(this, 0, getSize() - 1);
    }
}
