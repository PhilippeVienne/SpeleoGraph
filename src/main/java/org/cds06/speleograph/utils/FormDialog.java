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

import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.SpeleoGraphApp;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: PhilippeGeek
 * Date: 26/07/13
 * Time: 05:37
 * To change this template use File | Settings | File Templates.
 */
public class FormDialog extends JDialog{

    protected static final String LAYOUT_COLUMNS = "l:p,4dlu,p:grow";
    protected static final String LAYOUT_LINES = "";

    protected final JPanel contentPane = new JPanel();

    {
        contentPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        contentPane.setLayout(new FormLayout(LAYOUT_COLUMNS,LAYOUT_LINES));
    }

    public JPanel getPanel(){
        return contentPane;
    }

    public FormDialog(){
        super(SpeleoGraphApp.getInstance(),true);
        setContentPane(contentPane);
        setSize(400,300);
    }

    /**
     * Center the dialog on screen.
     */
    protected void centerOnScreen(){
        int x = SpeleoGraphApp.getInstance().getX() + (SpeleoGraphApp.getInstance().getWidth()/2-getWidth()/2);
        int y = SpeleoGraphApp.getInstance().getY() + (SpeleoGraphApp.getInstance().getHeight()/2-getHeight()/2);
        setLocation(x,y);
    }

}
