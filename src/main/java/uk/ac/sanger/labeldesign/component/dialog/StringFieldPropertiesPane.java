package uk.ac.sanger.labeldesign.component.dialog;

import uk.ac.sanger.labeldesign.model.*;
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
    private JSpinner horizontalMagnificationField, verticalMagnificationField;
    private JLabel headlineLabel;

    public StringFieldPropertiesPane(Design design, RenderFactory renderFactory) {
        nameField = makeTextField();
        stringField = makeTextField();
        stringField.setText("Placeholder string");
        stringField.selectAll();
        xField = makeSpinner((design.getXMin()+design.getXMax())/2, null, null, 10);
        yField = makeSpinner((design.getYMin()+design.getYMax())/2, null, null, 10);

        horizontalMagnificationField = makeSpinner(5, 5, 95, 5);
        verticalMagnificationField = makeSpinner(5, 5, 95, 5);

        spacingField = makeSpinner(0, 0, null, 1);
        fontCodeField = makeFontCodeCombo(renderFactory);
        rotationField = makeRotationCombo();

        add(layOutComponents(), BorderLayout.CENTER);
        updateState();
    }

    private JPanel layOutComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        headlineLabel = new JLabel();
        headlineLabel.setFont(headlineLabel.getFont().deriveFont(headlineLabel.getFont().getSize()*1.3f));
        panel.add(panelOf(headlineLabel));
        panel.add(panelOf("All fonts displayed are substitutes or approximations."));
        panel.add(panelOf("Font code:", fontCodeField));
        panel.add(panelOf("Name:", nameField));
        panel.add(panelOf("Spacing adjustment:", spacingField));
        panel.add(panelOf("Display string:", stringField));
        panel.add(panelOf("Magnification: X:", horizontalMagnificationField,
                "Y:", verticalMagnificationField));
        panel.add(panelOf("Position: X:", xField, "Y:", yField));
        panel.add(panelOf("Rotation:", rotationField));

        cancelButton.setText("Delete");

        return panel;
    }

    private Character getFontCode() {
        return getCharacterCode(fontCodeField);
    }

    private void setSelectedFont(final char fontCode) {
        comboSelect(fontCodeField, (String s) -> s.charAt(0)==fontCode);
    }

    public void loadStringField(StringField sf) {
        headlineLabel.setText(sf==null ? "New string field" : "String field properties");
        if (sf!=null) {
            nameField.setText(sf.getName());
            stringField.setText(sf.getDisplayText());
            xField.setValue(sf.getX());
            yField.setValue(sf.getY());
            spacingField.setValue(sf.getSpacing());
            horizontalMagnificationField.setValue(sf.getHorizontalMagnification());
            verticalMagnificationField.setValue(sf.getVerticalMagnification());
            setSelectedRotation(rotationField, sf.getRotation());
            setSelectedFont(sf.getFontCode());
        }
    }

    public void updateStringField(StringField sf) {
        sf.setName(nameField.getText().trim());
        sf.setDisplayText(stringField.getText().trim());
        sf.setPosition((Integer) xField.getValue(), (Integer) yField.getValue());
        Character fontCode = getFontCode();
        Integer rotation = getRotation(rotationField);
        if (fontCode!=null) {
            sf.setFontCode(fontCode);
        }
        if (rotation!=null) {
            sf.setRotation(rotation);
        }
        Integer horMag = (Integer) horizontalMagnificationField.getValue();
        Integer verMag = (Integer) verticalMagnificationField.getValue();
        sf.setHorizontalMagnification(horMag);
        sf.setVerticalMagnification(verMag);
        sf.setSpacing((Integer) spacingField.getValue());
    }

    @Override
    protected boolean valid() {
        return allHaveValues(nameField, stringField, xField, yField, fontCodeField, rotationField, spacingField);
    }

    private JComboBox<String> makeFontCodeCombo(RenderFactory renderFactory) {
        JComboBox<String> combo = new JComboBox<>();
        renderFactory.fontDescs()
                .forEach(e -> combo.addItem(e.getKey()+": "+e.getValue()));
        combo.addItemListener(getFieldItemListener());
        return combo;
    }

    @Override
    public void dragged(DesignField field) {
        if (field instanceof StringField) {
            boolean listening = isChangeListening();
            setChangeListening(false);
            xField.setValue(field.getX());
            yField.setValue(field.getY());
            setChangeListening(listening);
        }
    }
}
