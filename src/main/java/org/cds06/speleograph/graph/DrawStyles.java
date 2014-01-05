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

package org.cds06.speleograph.graph;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class represent all supported Drawing styles
 */
public class DrawStyles {
    private static Map<Object, Object> styles = ArrayUtils.toMap(new Object[]{
            new Object[]{DrawStyle.AREA, "Dessiner en aire"},
            new Object[]{DrawStyle.HIGH_LOW, "Dessiner avec ded batons min/max"},
            new Object[]{DrawStyle.LINE, "Dessiner sous une ligne"},
            new Object[]{DrawStyle.AUTO, null}
    });

    public static List<DrawStyle> getDrawableStyles() {
        return Arrays.asList(DrawStyle.AUTO, DrawStyle.AREA, DrawStyle.LINE);
    }

    public static List<DrawStyle> getDrawableStylesForHighLow() {
        return Arrays.asList(DrawStyle.AUTO, DrawStyle.HIGH_LOW);
    }

    public static String getHumanCheckboxText(DrawStyle style) {
        if (null != styles.get(style)) return (String) styles.get(style);
        return "Automatique";
    }
}
