package uk.ac.sanger.labeldesign.component.dialog;

import uk.ac.sanger.labeldesign.component.QuickDocumentListener;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ItemListener;

/**
 * @author dr6
 */
abstract class PropertiesPane extends JPanel {
    protected JButton okButton, cancelButton;

    private DocumentListener fieldDocListener;
    private ChangeListener fieldChangeListener;
    private ItemListener fieldItemListener;

    private ChangeListener changeListener;

    private boolean okPressed;
    private Runnable closeAction;

    public PropertiesPane() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        okButton.addActionListener(e -> close(true));
        cancelButton.addActionListener(e -> close(false));

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

    private void close(boolean okPressed) {
        this.okPressed = okPressed;
        closeAction.run();
    }

    public boolean isOkPressed() {
        return this.okPressed;
    }

    protected abstract boolean valid();

    protected void updateState() {
        if (changeListener!=null) {
            changeListener.stateChanged(new ChangeEvent(this));
        }
        okButton.setEnabled(valid());
    }

    public void setCloseAction(Runnable closeAction) {
        this.closeAction = closeAction;
    }

}
