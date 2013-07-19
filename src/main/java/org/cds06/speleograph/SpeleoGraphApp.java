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

import org.cds06.speleograph.actions.ImportAction;
import org.cds06.speleograph.actions.OpenAction;
import org.cds06.speleograph.data.FileReadingError;
import org.cds06.speleograph.data.HoboFileReader;
import org.cds06.speleograph.data.ReefnetFileReader;
import org.cds06.speleograph.data.SpeleoDataFileReader;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * SpeleoGraph Main Frame. This class is the enter point for a graphical utilisation and is the JFrame show. It create
 * all sub-elements for UI like graph, lists, menus ...
 *
 * @author Philippe VIENNE
 */
public class SpeleoGraphApp extends JFrame {

    /**
     * Instance of SpeleoGraph in the current JVM.
     */
    private static SpeleoGraphApp instance;

    /**
     * Logger for errors and info.
     */
    @SuppressWarnings("UnusedDeclaration")
    @NonNls
    private static Logger log = LoggerFactory.getLogger(SpeleoGraphApp.class);

    /**
     * Main panel for the application.
     */
    private final JPanel panel;
    /**
     * Main upper Toolbar.
     */
//    private final JToolBar toolBar;
    /**
     * The splitPane to divide space between Graph and Series' List.
     */
    private final JSplitPane splitPane;

    public SpeleoGraphApp() {
        super("SpeleoGraph"); // NON-NLS

        // Initialize Graphic elements
//        toolBar = new JToolBar();
        panel = new JPanel(new BorderLayout(2, 2));
        SpeleoSeriesListModel listModel = new SpeleoSeriesListModel();
        CheckBoxList list = new CheckBoxList(listModel);
        JScrollPane scrollPane = new JScrollPane(list);
        GraphPanel graphPanel = new GraphPanel(this);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true, graphPanel,scrollPane);

        // Setup the toolbar
//        panel.add(toolBar, BorderLayout.NORTH);
//        addToolBarButtons();

        // Configure and add the splitPane
        splitPane.setResizeWeight(1.0);
        panel.add(splitPane, BorderLayout.CENTER);

        // Configure the frame
        setContentPane(panel);
        setJMenuBar(createMenus());
        // Positioning
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.height = screenSize.height - 100;
        screenSize.width = screenSize.width - 100;
        setSize(screenSize);
        setLocation(50,50);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Setup buttons for the toolBar.
     */
//    private void addToolBarButtons() {
////        toolBar.add(new OpenAction(panel, SpeleoDataFileReader.class));
////        toolBar.add(new OpenAction(panel, ReefnetFileReader.class));
//    }

    public JSplitPane getSplitPane() {
        return splitPane;
    }

    /**
     * Create a JMenuBar for this application.
     */
    public JMenuBar createMenus(){
        final JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu(I18nSupport.translate("menus.file"));
        fileMenu.add(new OpenAction(panel, SpeleoDataFileReader.class));
        JMenu importMenu = new JMenu(I18nSupport.translate("menus.import"));
        importMenu.add(new OpenAction(panel, ReefnetFileReader.class));
        importMenu.add(new OpenAction(panel, HoboFileReader.class));
        importMenu.addSeparator();
        importMenu.add(new ImportAction(panel));
        fileMenu.add(importMenu);
        fileMenu.addSeparator();
        fileMenu.add(new AbstractAction() {

            {
                putValue(Action.NAME, I18nSupport.translate("actions.exit"));
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        panel,
                        I18nSupport.translate("actions.exit.confirm.message"),
                        I18nSupport.translate("actions.exit.confirm.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if(result==JOptionPane.OK_OPTION){
                    SpeleoGraphApp.this.dispose();
                }
            }
        });
        bar.add(fileMenu);

        return bar;
    }

    /**
     * Start the application using this function.
     * No arguments are currently read by the program.
     * This function try to use the Nimbus LaF or System if not found.
     * @param args Arguments sand to the JVM (not used)
     */
    @NonNls
    public static void main(String... args) {

        // Setup Look and Feels
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { // NON-NLS
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e2) {
                System.out.println("Leave default Java LookAndFeel"); // NON-NLS
            }
        }

        instance = new SpeleoGraphApp();

        // Start application
        instance.setVisible(true);
    }

    /**
     * Open a SpeleoGraph file.
     * @param file The file to open.
     */
    public static void openFile(File file) throws IOException, ParseException {
        try {
            SpeleoDataFileReader.getInstance().readFile(file);
        } catch (FileReadingError fileReadingError) {
            log.error("Error on file reading",fileReadingError);
        }
    }
}
