package uk.ac.sanger.labeldesign.component.dialog;

import uk.ac.sanger.labeldesign.model.Design;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.Arrays;

/**
 * @author dr6
 */
public class DesignPropertiesPane extends PropertiesPane {
    private JTextField nameField;
    private JSpinner[] boundaryFields;
    private JSpinner labelTypeField;
    private JLabel headlineLabel;

    public DesignPropertiesPane() {
        nameField = makeTextField();
        boundaryFields = new JSpinner[4];
        for (int i = 0; i < boundaryFields.length; ++i) {
            boundaryFields[i] = makeSpinner(1000, null, null, 10);
            boundaryFields[i].setPreferredSize(boundaryFields[i].getPreferredSize());
        }
        labelTypeField = makeSpinner(1, 1, null, 1);

        add(layOutComponents(), BorderLayout.CENTER);
        boundaryFields[0].setValue(0);
        boundaryFields[1].setValue(600);
        boundaryFields[2].setValue(0);
        boundaryFields[3].setValue(300);
        updateState();
    }

    private JPanel layOutComponents() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        headlineLabel = new JLabel("New design");
        headlineLabel.setFont(headlineLabel.getFont().deriveFont(headlineLabel.getFont().getSize()*1.3f));
        p.add(panelOf(headlineLabel));
        p.add(panelOf("Name:", nameField));
        p.add(panelOf("X min:", boundaryFields[0], "X max:", boundaryFields[1]));
        p.add(panelOf("Y min:", boundaryFields[2], "Y max:", boundaryFields[3]));
        p.add(panelOf("Label type:", labelTypeField));
        return p;
    }

    public String getNameInput() {
        String name = nameField.getText();
        if (name!=null) {
            name = name.trim();
            if (name.isEmpty()) {
                return null;
            }
        }
        return name;
    }

    @Override
    protected boolean valid() {
        return (getNameInput()!=null && Arrays.stream(boundaryFields).allMatch(f -> f.getValue()!=null)
                && labelTypeField!=null);
    }

    public void loadDesign(Design design) {
        headlineLabel.setText(design==null ? "New design" : "Edit design");
        cancelButton.setVisible(design==null);
        if (design!=null) {
            nameField.setText(design.getName());
            boundaryFields[0].setValue(design.getXMin());
            boundaryFields[1].setValue(design.getXMax());
            boundaryFields[2].setValue(design.getYMin());
            boundaryFields[3].setValue(design.getYMax());
            labelTypeField.setValue(design.getLabelTypeId());
        }
    }

    public void updateDesign(Design design) {
        String name = getNameInput();
        if (name!=null) {
            design.setName(name);
        }
        int x0 = (int) boundaryFields[0].getValue();
        int x1 = (int) boundaryFields[1].getValue();
        int y0 = (int) boundaryFields[2].getValue();
        int y1 = (int) boundaryFields[3].getValue();
        int labelType = (int) labelTypeField.getValue();
        design.setBounds(x0, y0, x1, y1);
        design.setLabelTypeId(labelType);
    }
}
