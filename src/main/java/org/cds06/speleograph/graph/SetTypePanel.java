package org.cds06.speleograph.graph;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Describes the panel used to select the type of the serie.
 */
public class SetTypePanel extends JPanel implements ItemListener {
    private final String PRESS = org.cds06.speleograph.data.Type.PRESSURE.toString();
    private final String TEMP = org.cds06.speleograph.data.Type.TEMPERATURE.toString();
    private final String TEMP_MIN_MAX = org.cds06.speleograph.data.Type.TEMPERATURE_MIN_MAX.toString();
    private final String WATER = org.cds06.speleograph.data.Type.WATER.toString();
    private final String OTHER = "Autre";

    private JTextField typeNameField = new JTextField();
    private JComboBox<String> typeBox = new JComboBox<>(new String[]{
            PRESS, TEMP, TEMP_MIN_MAX, WATER, OTHER
    });
    private JTextField typeUnitField = new JTextField();

    /**
     * Logger for errors and info.
     */
    @SuppressWarnings("UnusedDeclaration")
    @NonNls
    private static Logger log = LoggerFactory.getLogger(SetTypePanel.class);

    public SetTypePanel() {
        PanelBuilder builder = new PanelBuilder(new FormLayout("r:p,p:grow", "p,p,p,p"), this);

        builder.addLabel("Type :");
        builder.nextColumn();
        builder.add(typeBox);
        typeBox.addItemListener(this);
        builder.nextLine();

        final String name = ((String) typeBox.getSelectedItem()).split(" ")[0];
        builder.addLabel("Nom :");
        builder.nextColumn();
        builder.add(typeNameField);
        typeNameField.setText(name);
        builder.nextLine();

        String unit = ((String) typeBox.getSelectedItem()).split(" ")[1];
        unit = unit.substring(1, unit.length()-1);
        builder.addLabel("Unité :");
        builder.nextColumn();
        builder.add(typeUnitField);
        typeUnitField.setText(unit);

        CellConstraints cc = new CellConstraints();
        cc.xyw(1,4,2);
        builder.addLabel("<HTML>" +
                "Le menu déroulant vous aide à indiquer à SpeleoGraph<br />le type correct de données.<br />" +
                "Pour des données correspondant aux types prédéfinis,<br />le nom doit correspondre à " +
                "ce que donne le menu<br />déroulant (par exemple \"Précipitations\" si vous avez<br />" +
                "des données de pluviométrie.<br />" +
                "Les types prédéfinis sont :" +
                "<ul>" +
                "<li>Pression</li>" +
                "<li>Température (éventuellement min/max,<br />voir case à cocher)</li>" +
                "<li>Précipitations</li>" +
                "</ul>" +
                "</HTML>", cc);

        builder.build();
    }

    public String getTypeName() {
        return typeNameField.getText();
    }

    public String getTypeUnit() {
        return typeUnitField.getText();
    }



    @Override
    public void itemStateChanged(ItemEvent e) {
        if (!e.getSource().equals(typeBox)) return;

        final String[] type =((String) typeBox.getSelectedItem()).split(" ");
        String name = "";
        if (!type[0].equals("Autre"))
            name = type[0];
        String unit = "";
        if (type.length > 1)
            unit = type[1].substring(1,type[1].length()-1);
        if (type.length > 2)
            unit = type[2].substring(1,type[2].length()-1);
        typeNameField.setText(name);
        typeUnitField.setText(unit);
    }
}
