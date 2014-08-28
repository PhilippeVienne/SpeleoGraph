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

import javax.swing.*;
import java.awt.*;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class About extends JPanel {

    private static final long serialVersionUID = -7058001019495308674L;

    /**
     * AboutPane constructor
     *
     * @param app        - name of the app
     * @param version    - app version
     * @param author     - developper name
     * @param email      - contact email (can be null)
     * @param site       - website url (can be null)
     * @param disclaimer - quick disclaimer/description (can be null)
     * @param icon       - application icon (can be null)
     */
    public About(String app, String version, String author, String email,
                 String site, String disclaimer, ImageIcon icon, ImageIcon icon2) {
        super(new FlowLayout(FlowLayout.LEFT));

        JPanel a = new JPanel(new GridLayout(7, 1));

        a.add(new JLabel(app + (version != null ? " - version " + version : "")));
        a.add(new JLabel(""));
        a.add(new JLabel(I18nSupport.translate("authors")+ " : " + author));

        if (email != null && !email.equals(""))
            a.add(new JLabel(I18nSupport.translate("contact") + " : <" + email + ">"));

        if (site != null && !site.equals(""))
            a.add(new JLabel(site));

        a.add(new JLabel(""));

        if (disclaimer != null && !disclaimer.equals(""))
            a.add(new JLabel(disclaimer));

        this.add(new JLabel(icon));
        this.add(a);
        this.add(new JLabel(icon2));
    }
}
