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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.utils.FormDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class ImportWizard {

    private final File file;
    private char separatorChar = ';';

    public ImportWizard(File file) {
        super();
        this.file = file;
    }

    private class SeparatorDialog extends FormDialog {

        private final JComboBox<String> separator = new JComboBox<>(new String[]{",", ";", "(tabulation)"}); // NON-NLS

        public SeparatorDialog() {
            super();
            construct();
        }

        /**
         * This function add component to the main panel.
         * <p>You have to override this function to add and setup your dialog</p>
         */
        @Override
        protected void setup() {
            PanelBuilder builder = new PanelBuilder((FormLayout) getPanel().getLayout(), getPanel());

            builder.addLabel("Séparateur :");
            builder.nextColumn(2);
            builder.add(separator);
            builder.nextLine();
            builder.add(new JButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    validateForm();
                }
            }), "2,2,3,1");

        }

        @Override
        protected void validateForm() {
            switch ((String) separator.getSelectedItem()) {
                case ",":
                    separatorChar = ',';
                    break;
                case "(tabulation)": // NON-NLS
                    separatorChar = ',';
                    break;
                case ";":
                default:
                    separatorChar = ',';
                    break;
            }
            openImportTable();
        }

        @Override
        protected FormLayout getFormLayout() {
            return new FormLayout("r:p,4dlu,p:grow", "p,p"); // NON-NLS
        }

    }

    public void openWizard() {
        SeparatorDialog dialog = new SeparatorDialog();
        dialog.setVisible(true);
    }

    private void openImportTable() {

    }
}
