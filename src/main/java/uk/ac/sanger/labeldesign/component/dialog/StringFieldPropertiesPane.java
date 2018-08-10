package uk.ac.sanger.labeldesign.component.dialog;

import uk.ac.sanger.labeldesign.model.*;
import uk.ac.sanger.labeldesign.view.RenderFactory;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.function.Predicate;

/**
 * @author dr6
 */
public class StringFieldPropertiesPane extends PropertiesPane {
    private JTextField nameField, stringField;

    private JSpinner xField, yField;
    private JSpinner spacingField;
    private JComboBox<String> fontCodeField;
    private JComboBox<String> rotationField;
    private JLabel headlineLabel;

    public StringFieldPropertiesPane(Design design, RenderFactory renderFactory) {
        nameField = makeTextField();
        stringField = makeTextField();
        stringField.setText("Placeholder string");
        stringField.selectAll();
        xField = makeSpinner((design.getXMin()+design.getXMax())/2, null, null, 10);
        yField = makeSpinner((design.getYMin()+design.getYMax())/2, null, null, 10);
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
        panel.add(panelOf(headlineLabel));
        panel.add(panelOf("All fonts are substitutes or approximations."));
        panel.add(panelOf("Font code:", fontCodeField));
        panel.add(panelOf("Name:", nameField));
        panel.add(panelOf("Spacing adjustment:", spacingField));
        panel.add(panelOf("Display string:", stringField));
        panel.add(panelOf("X:", xField, "Y:", yField));
        panel.add(panelOf("Rotation:", rotationField));

        cancelButton.setText("Delete");

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

    private static <T> boolean comboSelect(JComboBox<T> combo, Predicate<? super T> predicate) {
        int n = combo.getItemCount();
        for (int i = 0; i < n; ++i) {
            T item = combo.getItemAt(i);
            if (predicate.test(item)) {
                combo.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    private void setSelectedRotation(final int rotation) {
        comboSelect(rotationField, (String s) -> s.charAt(0)==rotation+'0');
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
            setSelectedRotation(sf.getRotation());
            setSelectedFont(sf.getFontCode());
        }
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
