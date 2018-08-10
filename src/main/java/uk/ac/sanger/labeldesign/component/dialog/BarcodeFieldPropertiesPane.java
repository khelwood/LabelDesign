package uk.ac.sanger.labeldesign.component.dialog;

import uk.ac.sanger.labeldesign.model.*;
import uk.ac.sanger.labeldesign.view.RenderFactory;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * A pane for editing the properties of a barcode field
 * @author dr6
 */
public class BarcodeFieldPropertiesPane extends PropertiesPane {
    private JTextField nameField;

    private JSpinner xField, yField;
    private JSpinner cellWidthField;
    private JSpinner heightField;
    private JComboBox<String> typeCodeField;
    private JComboBox<String> rotationField;
    private JLabel headlineLabel;

    public BarcodeFieldPropertiesPane(Design design, RenderFactory renderFactory) {
        nameField = makeTextField();
        xField = makeSpinner((design.getXMin()+design.getXMax())/2, null, null, 10);
        yField = makeSpinner((design.getYMin()+design.getYMax())/2, null, null, 10);
        cellWidthField = makeSpinner(1, 1, null, 1);
        heightField = makeSpinner(1, 1, null, 1);
        typeCodeField = new JComboBox<>();
        typeCodeField.addItem("Q");
        rotationField = makeRotationCombo();

        add(layOutComponents(), BorderLayout.CENTER);
        updateState();
    }

    private JPanel layOutComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        headlineLabel = new JLabel();
        panel.add(panelOf(headlineLabel));
        panel.add(panelOf("Currently only barcode type Q is supported."));
        panel.add(panelOf("Type code:", typeCodeField));
        panel.add(panelOf("Name:", nameField));
        panel.add(panelOf("Barcode size estimated based on cell width."));
        panel.add(panelOf("Cell width:", cellWidthField));
        panel.add(panelOf("Height:", heightField));
        panel.add(panelOf("X:", xField, "Y:", yField));
        panel.add(panelOf("Rotation:", rotationField));

        cancelButton.setText("Delete");

        return panel;
    }

    public void loadBarcodeField(BarcodeField bf) {
        headlineLabel.setText(bf==null ? "New barcode field" : "Barcode field properties");
        if (bf!=null) {
            nameField.setText(bf.getName());
            comboSelect(typeCodeField, s -> s.charAt(0)==bf.getBarcodeType());
            xField.setValue(bf.getX());
            yField.setValue(bf.getY());
            setSelectedRotation(rotationField, bf.getRotation());
            heightField.setValue(bf.getHeight());
            cellWidthField.setValue(bf.getCellWidth());
        }
    }

    public void updateBarcodeField(BarcodeField bf) {
        bf.setName(nameField.getText().trim());
        bf.setCellWidth((int) cellWidthField.getValue());
        bf.setHeight((int) heightField.getValue());
        Character typeCode = getCharacterCode(typeCodeField);
        if (typeCode!=null) {
            bf.setBarcodeType(typeCode);
        }
        bf.setPosition((Integer) xField.getValue(), (Integer) yField.getValue());
        Integer rotation = getRotation(rotationField);
        if (rotation!=null) {
            bf.setRotation(rotation);
        }
    }

    @Override
    protected boolean valid() {
        return allHaveValues(nameField, typeCodeField, xField, yField, rotationField, cellWidthField, heightField);
    }

    @Override
    public void dragged(DesignField field) {
        if (field instanceof BarcodeField) {
            boolean listening = isChangeListening();
            setChangeListening(false);
            xField.setValue(field.getX());
            yField.setValue(field.getY());
            setChangeListening(listening);
        }
    }
}
