package org.cds06.speleograph;

import org.cds06.speleograph.data.SpeleoFileReader;

import javax.swing.*;

/**
 * Wizard for file opening.
 */
public class OpenFileWizard {

    final private SpeleoFileReader reader = new SpeleoFileReader();
    final private ReefnetFileConverter reefnetFileConverter = new ReefnetFileConverter();
    final private JDialog dialog;
    final private JFileChooser fileChooser = new SpeleoFileChooser();

    public OpenFileWizard(JFrame parentFrame) {
        dialog = new JDialog(parentFrame);
    }

    private class SpeleoFileChooser extends JFileChooser {

        public SpeleoFileChooser() {
            super();
        }

    }
}
