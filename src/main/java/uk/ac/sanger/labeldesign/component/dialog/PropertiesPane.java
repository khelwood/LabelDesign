package uk.ac.sanger.labeldesign.component.dialog;

import uk.ac.sanger.labeldesign.component.QuickDocumentListener;
import uk.ac.sanger.labeldesign.model.DesignField;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;

/**
 * @author dr6
 */
public abstract class PropertiesPane extends JPanel {
    protected JButton okButton, cancelButton;

    private DocumentListener fieldDocListener;
    private ChangeListener fieldChangeListener;
    private ItemListener fieldItemListener;

    private ChangeListener changeListener;
    private boolean changeListening = true;

    private boolean okPressed, cancelPressed;
    private Runnable closeAction;

    public PropertiesPane() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        okButton.addActionListener(this::close);
        cancelButton.addActionListener(this::close);

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    protected static JPanel panelOf(Object... objects) {
        JPanel line = new JPanel();
        for (Object obj : objects) {
            if (obj instanceof String) {
                obj = new JLabel((String) obj);
            }
            line.add((Component) obj);
        }
        return line;
    }

    protected DocumentListener getFieldDocListener() {
        if (fieldDocListener ==null) {
            fieldDocListener = (QuickDocumentListener) this::updateState;
        }
        return fieldDocListener;
    }

    protected ChangeListener getFieldChangeListener() {
        if (fieldChangeListener ==null) {
            fieldChangeListener = e -> this.updateState();
        }
        return fieldChangeListener;
    }

    protected ItemListener getFieldItemListener() {
        if (fieldItemListener==null) {
            fieldItemListener = e -> updateState();
        }
        return fieldItemListener;
    }

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    protected JTextField makeTextField() {
        JTextField tf = new JTextField(12);
        tf.getDocument().addDocumentListener(getFieldDocListener());
        return tf;
    }

    protected JSpinner makeSpinner(Integer value, Integer min, Integer max, Integer step) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        return makeSpinner(model);
    }

    protected JSpinner makeSpinner(SpinnerModel model) {
        JSpinner spinner = new JSpinner(model);
        spinner.addChangeListener(getFieldChangeListener());
        return spinner;
    }

    protected JComboBox<String> makeRotationCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("0: Normal");
        combo.addItem("1: Rotated 90° clockwise");
        combo.addItem("2: Rotated 180°");
        combo.addItem("3: Rotated 270° clockwise");
        combo.addItemListener(getFieldItemListener());
        return combo;
    }

    private void close(ActionEvent event) {
        this.okPressed = (event.getSource()==okButton);
        this.cancelPressed = (event.getSource()==cancelButton);
        closeAction.run();
    }

    public boolean isOkPressed() {
        return this.okPressed;
    }

    public boolean isCancelPressed() {
        return this.cancelPressed;
    }

    protected abstract boolean valid();

    public boolean isChangeListening() {
        return this.changeListening;
    }

    public void setChangeListening(boolean changeListening) {
        this.changeListening = changeListening;
    }

    protected void updateState() {
        if (changeListener!=null && isChangeListening()) {
            changeListener.stateChanged(new ChangeEvent(this));
        }
        okButton.setEnabled(valid());
    }

    public void setCloseAction(Runnable closeAction) {
        this.closeAction = closeAction;
    }

    public void dragged(DesignField field) {
        // do nothing
    }

}
