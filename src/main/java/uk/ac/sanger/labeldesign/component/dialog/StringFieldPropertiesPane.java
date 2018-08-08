package uk.ac.sanger.labeldesign.component.dialog;

import uk.ac.sanger.labeldesign.model.Design;
import uk.ac.sanger.labeldesign.model.StringField;
import uk.ac.sanger.labeldesign.view.RenderFactory;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * @author dr6
 */
public class StringFieldPropertiesPane extends PropertiesPane {
    private JTextField nameField, stringField;

    private JSpinner xField, yField;
    private JSpinner spacingField;
    private JComboBox<String> fontCodeField;
    private JComboBox<String> rotationField;

    public StringFieldPropertiesPane(Design design, RenderFactory renderFactory) {
        int wi = design.getWidth();
        int hi = design.getHeight();
        nameField = makeTextField();
        stringField = makeTextField();
        stringField.setText("Placeholder string");
        stringField.selectAll();
        xField = makeSpinner(wi/2, 0, wi, 10);
        yField = makeSpinner(hi/2, 0, hi, 10);
        spacingField = makeSpinner(0, 0, null, 1);
        fontCodeField = makeFontCodeCombo(renderFactory);
        rotationField = makeRotationCombo();

        add(layOutComponents(), BorderLayout.CENTER);
        updateState();
    }

    private JPanel layOutComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(panelOf("New string field"));
        panel.add(panelOf("Font code:", fontCodeField));
        panel.add(panelOf("Name:", nameField));
        panel.add(panelOf("Spacing adjustment:", spacingField));
        panel.add(panelOf("Display string:", stringField));
        panel.add(panelOf("X:", xField, "Y:", yField));
        panel.add(panelOf("Rotation:", rotationField));
        return panel;
    }

    private Character getFontCode() {
        String item = (String) fontCodeField.getSelectedItem();
        if (item==null) {
            return null;
        }
        return item.charAt(0);
    }

    private Integer getRotation() {
        String item = (String) rotationField.getSelectedItem();
        if (item==null) {
            return null;
        }
        return item.charAt(0)-'0';
    }

    public StringField makeStringField() {
        StringField sf = new StringField();
        updateStringField(sf);
        return sf;
    }

    public void updateStringField(StringField sf) {
        sf.setName(nameField.getText().trim());
        sf.setDisplayText(stringField.getText().trim());
        sf.setPosition((Integer) xField.getValue(), (Integer) yField.getValue());
        Character fontCode = getFontCode();
        Integer rotation = getRotation();
        if (fontCode!=null) {
            sf.setFontCode(fontCode);
        }
        if (rotation!=null) {
            sf.setRotation(rotation);
        }
        sf.setSpacing((Integer) spacingField.getValue());
    }

    @Override
    protected boolean valid() {
        return !(nameField.getText().trim().isEmpty() || stringField.getText().trim().isEmpty()
                || xField.getValue()==null || yField.getValue()==null
                || getFontCode()==null || getRotation()==null || spacingField.getValue()==null);
    }

    private JComboBox<String> makeFontCodeCombo(RenderFactory renderFactory) {
        JComboBox<String> combo = new JComboBox<>();
        renderFactory.fontDescs()
                .forEach(e -> combo.addItem(e.getKey()+": "+e.getValue()));
        combo.addPropertyChangeListener(getPropertyChangeListener());
        return combo;
    }

}
