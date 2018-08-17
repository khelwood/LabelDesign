package uk.ac.sanger.labeldesign.component.dialog;

import uk.ac.sanger.labeldesign.model.*;
import uk.ac.sanger.labeldesign.model.BarcodeField.Type;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.*;

/**
 * A pane for editing the properties of a barcode field
 * @author dr6
 */
public class BarcodeFieldPropertiesPane extends PropertiesPane {
    private JTextField nameField;

    private JSpinner xField, yField;
    private JSpinner cellWidthField;
    private JSpinner moduleWidthField;
    private JSpinner heightField;
    private JSpinner checkDigitField;
    private JComboBox<Type> typeCodeField;
    private JComboBox<Rotation> rotationField;
    private JLabel headlineLabel;
    private List<Component> componentsFor1D;
    private List<Component> componentsFor2D;

    public BarcodeFieldPropertiesPane(Design design) {
        nameField = makeTextField("barcode");
        xField = makeSpinner((design.getXMin()+design.getXMax())/2, null, null, 10);
        yField = makeSpinner((design.getYMin()+design.getYMax())/2, null, null, 10);
        cellWidthField = makeSpinner(4, 1, null, 1);
        moduleWidthField = makeSpinner(1, 1, null, 1);
        heightField = makeSpinner(70, 0, null, 1);
        checkDigitField = makeSpinner(2, 0, 2, 1);
        typeCodeField = new JComboBox<>();
        Arrays.stream(Type.values()).forEach(typeCodeField::addItem);
        typeCodeField.addItemListener(e -> typeCodeChanged());
        typeCodeField.addItemListener(getFieldItemListener());
        rotationField = makeRotationCombo();

        add(setupComponents(), BorderLayout.CENTER);
        updateState();
    }

    private JPanel setupComponents() {
        componentsFor1D = new ArrayList<>();
        componentsFor2D = new ArrayList<>();
        headlineLabel = new JLabel();
        headlineLabel.setFont(headlineLabel.getFont().deriveFont(headlineLabel.getFont().getSize()*1.3f));
        cancelButton.setText("Delete");
        return layOutComponents();
    }

    private JPanel layOutComponents() {
        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(panelOf(headlineLabel));
        panel.add(panelOf("Type code:", typeCodeField));
        panel.add(panelFor1D("Check digit type:", checkDigitField));
        panel.add(panelOf("Name:", nameField));
        panel.add(panelOf("Barcode sizes are estimates."));
        panel.add(panelFor2D("Cell width:", cellWidthField));
        panel.add(panelFor1D("Module width:", moduleWidthField));
        panel.add(panelFor1D("Height:", heightField));
        panel.add(panelOf("X:", xField, "Y:", yField));
        panel.add(panelFor1D("PMB ignores the rotation field for 1D barcodes."));
        panel.add(panelOf("Rotation:", rotationField));

        return panel;
    }

    private JPanel panelFor2D(Object... items) {
        JPanel panel = panelOf(items);
        componentsFor2D.add(panel);
        return panel;
    }

    private JPanel panelFor1D(Object... items) {
        JPanel panel = panelOf(items);
        componentsFor1D.add(panel);
        return panel;
    }

    public void loadBarcodeField(BarcodeField bf) {
        headlineLabel.setText(bf==null ? "New barcode field" : "Barcode field properties");
        if (bf!=null) {
            nameField.setText(bf.getName());
            typeCodeField.setSelectedItem(bf.getType());
            xField.setValue(bf.getX());
            yField.setValue(bf.getY());
            rotationField.setSelectedItem(bf.getRotation());
            heightField.setValue(bf.getHeight());
            cellWidthField.setValue(bf.getCellWidth());
            moduleWidthField.setValue(bf.getModuleWidth());
            checkDigitField.setValue(bf.getCheckDigitType());
            typeCodeChanged();
        }
    }

    public void updateBarcodeField(BarcodeField bf) {
        bf.setName(nameField.getText().trim());
        bf.setCellWidth((int) cellWidthField.getValue());
        bf.setModuleWidth((int) moduleWidthField.getValue());
        bf.setHeight((int) heightField.getValue());
        Type barcodeType = (Type) typeCodeField.getSelectedItem();
        if (barcodeType!=null) {
            bf.setType(barcodeType);
        }
        bf.setPosition((Integer) xField.getValue(), (Integer) yField.getValue());
        Rotation rotation = (Rotation) rotationField.getSelectedItem();
        if (rotation!=null) {
            bf.setRotation(rotation);
        }
        bf.setCheckDigitType((int) checkDigitField.getValue());
    }

    @Override
    protected boolean valid() {
        if (!allHaveValues(nameField, xField, yField, rotationField)) {
            return false;
        }
        Type barcodeType = (Type) typeCodeField.getSelectedItem();
        if (barcodeType==null) {
            return false;
        }
        if (barcodeType.is2D()) {
            return allHaveValues(cellWidthField);
        } else {
            return allHaveValues(moduleWidthField, heightField, checkDigitField);
        }
    }

    private void typeCodeChanged() {
        Type barcodeType = (Type) typeCodeField.getSelectedItem();
        if (barcodeType==null) {
            return;
        }
        boolean twod = barcodeType.is2D();
        for (Component comp : componentsFor1D) {
            comp.setVisible(!twod);
        }
        for (Component comp : componentsFor2D) {
            comp.setVisible(twod);
        }
    }

    @Override
    protected void setXY(int x, int y) {
        xField.setValue(x);
        yField.setValue(y);
    }
}
