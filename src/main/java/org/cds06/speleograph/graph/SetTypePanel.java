package org.cds06.speleograph.graph;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Type;
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
    private final String PRESS = Type.PRESSURE.toString();
    private final String TEMP = Type.TEMPERATURE.toString();
    private final String TEMP_MIN_MAX = Type.TEMPERATURE_MIN_MAX.toString();
    private final String WATER = Type.WATER.toString();
    private final String WATER_HEIGHT = Type.WATER_HEIGHT.toString();
    private final String OTHER = I18nSupport.translate("actions.setType.other");

    private JTextField typeNameField = new JTextField();
    private JComboBox<String> typeBox = new JComboBox<>(new String[]{
            PRESS, TEMP, TEMP_MIN_MAX, WATER, WATER_HEIGHT, OTHER
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

        builder.addLabel(I18nSupport.translate("actions.setType.type") + " :");
        builder.nextColumn();
        builder.add(typeBox);
        typeBox.addItemListener(this);
        builder.nextLine();

        String name = "";
        String[] sTab = ((String) typeBox.getSelectedItem()).split(" ");
        for (int i = 0; i < sTab.length - 1; i++) {
            name += sTab[i];
        }
        builder.addLabel(I18nSupport.translate("actions.setType.name") + " :");
        builder.nextColumn();
        builder.add(typeNameField);
        typeNameField.setText(name);
        builder.nextLine();

        String unit = ((String) typeBox.getSelectedItem()).split(" ")[1];
        unit = unit.substring(1, unit.length()-1);
        builder.addLabel(I18nSupport.translate("actions.setType.unit") + " :");
        builder.nextColumn();
        builder.add(typeUnitField);
        typeUnitField.setText(unit);

        CellConstraints cc = new CellConstraints();
        cc.xyw(1,4,2);
        builder.addLabel("<HTML>" +
                I18nSupport.translate("actions.setType.message") + " :" +
                "<ul>" +
                "<li>" + Type.PRESSURE.getName() + "</li>" +
                "<li>" + Type.TEMPERATURE.getName() + " " + I18nSupport.translate("actions.setType.maybeMinMax") + "</li>" +
                "<li>" + Type.WATER.getName() + "</li>" +
                "<li>" + Type.WATER_HEIGHT.getName() + "</li>" +
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
        if (!type[0].equals(OTHER)) {
            for (int i = 0; i < type.length - 1; i++) {
                name += type[i] + " ";
            }
            name = name.substring(0, name.length()-1);
        }
        String unit = "";
        if (type.length > 1)
            unit = type[type.length -1].substring(1,type[type.length -1].length()-1);
        typeNameField.setText(name);
        typeUnitField.setText(unit);
    }
}
