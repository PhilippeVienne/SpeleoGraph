package org.cds06.speleograph.data;

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
        newSeries.setSet(DataSet.getDataSet(t));
    }

    public Series getSampledSeries() {
        final int itemsCount = originalSeries.getItemCount();
        final ArrayList<Item> items = originalSeries.getItems(), newItems = newSeries.getItems();
        double bufferValue = 0D;
        long lastStartBuffer = originalSeries.getRange().getLowerMillis();
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
        newItems.add(new Item(new Date(originalSeries.getRange().getUpperMillis()), bufferValue));
        return newSeries;
    }

}
