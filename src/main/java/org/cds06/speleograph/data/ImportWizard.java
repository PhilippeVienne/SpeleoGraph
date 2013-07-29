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

import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.utils.FormDialog;
import org.jetbrains.annotations.NonNls;

import java.io.File;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class ImportWizard extends FormDialog {

    private final File file;

    public ImportWizard(File file) {
        super();
        this.file = file;
        construct();
    }


    /**
     * This function add component to the main panel.
     * <p>You have to override this function to add and setup your dialog</p>
     */
    @Override
    protected void setup() {

    }

    @Override
    protected void validateForm() {

    }

    @NonNls
    private static final FormLayout FORM_LAYOUT = new FormLayout(
            "r:p,4dlu,p:grow:4dlu,p:grow",
            "p,4dlu,p"
    );

    @Override
    protected FormLayout getFormLayout() {
        return FORM_LAYOUT;
    }
}
