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

import org.jfree.data.time.DateRange;

import java.util.ArrayList;
import java.util.Date;

/**
 * Sampling for data.
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class Sampling {

    public static Series sampling(Series series, long samplingTime) {
        return new Sampling(series, samplingTime).getSampledSeries();
    }

    /**
     * Length in milliseconds between two samples.
     */
    final private long length;
    final private Series originalSeries;
    final private Series newSeries;

    public Sampling(Series originalSeries, long sampleLength) {
        this.originalSeries = originalSeries;
        this.length = sampleLength;
        this.newSeries = new Series(originalSeries.getOrigin());
        Type t = this.originalSeries.getType().asStepType();
        newSeries.setDataSet(DataSet.getDataSet(t));
    }

    public Series getSampledSeries() {
        final int itemsCount = originalSeries.getItemCount();
        final ArrayList<Item> items = originalSeries.getItems(), newItems = newSeries.getItems();
        double bufferValue = 0D;
        DateRange range = originalSeries.getRange();
        long lastStartBuffer = range.getLowerMillis();
        newItems.add(new Item(new Date(lastStartBuffer), 0.0));
        for (int i = 1; i < itemsCount; i++) {
            final Item originalItem = items.get(i), previousOriginalItem = items.get(i - 1);
            if (lastStartBuffer + length <= originalItem.getDate().getTime()) {
                newItems.add(new Item(new Date(lastStartBuffer), bufferValue));
                newItems.add(new Item(new Date(lastStartBuffer + length), bufferValue));
                lastStartBuffer = lastStartBuffer + length;
                bufferValue = 0D;
            }
            bufferValue = bufferValue + (originalItem.getValue() - previousOriginalItem.getValue());
        }
        newItems.add(new Item(new Date(lastStartBuffer), bufferValue));
        newItems.add(new Item(new Date(range.getUpperMillis()), bufferValue));
        return newSeries;
    }

}
