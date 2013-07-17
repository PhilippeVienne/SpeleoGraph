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

import org.cds06.speleograph.data.DataSet;
import org.cds06.speleograph.data.Series;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

import javax.swing.*;
import java.util.Collection;

/**
 * Model to retrieve series opened in the global {@link org.cds06.speleograph.data.DataSet}.
 *
 * @author Philippe VIENNE
 * @since 1.0
 */
public class SpeleoSeriesListModel extends AbstractListModel<Series> implements DatasetChangeListener {

    private static final long serialVersionUID = 1L;

    private Collection<DataSet> sets = DataSet.getInstances();

    {
        DataSet.addListener(this);
    }

    public int indexOf(Series s) {
        int i = 0;
        for (DataSet set : sets) {
            for (Series ser : set.getSeries()) {
                if (ser == s) return i;
                i++;
            }
        }
        return 0;
    }

    @Override
    public int getSize() {
        int size = 0;
        for (DataSet set : sets) {
            size += set.getSeries().size();
        }
        return size;
    }

    @Override
    public Series getElementAt(int index) {
        int i = 0;
        for (DataSet set : sets) {
            for (Series s : set.getSeries()) {
                if (index == i) return s;
                i++;
            }
        }
        throw new IndexOutOfBoundsException("Can not find your index");
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
