package uk.ac.sanger.labeldesign.component.dialog;

import uk.ac.sanger.labeldesign.component.QuickDocumentListener;
import uk.ac.sanger.labeldesign.model.DesignField;
import uk.ac.sanger.labeldesign.model.Rotation;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.function.Predicate;

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
        if (fieldDocListener==null) {
            fieldDocListener = QuickDocumentListener.of(this::updateState);
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
        return makeTextField(null);
    }

    protected JTextField makeTextField(String string) {
        JTextField tf = new JTextField(12);
        if (string!=null) {
            tf.setText(string);
        }
        tf.getDocument().addDocumentListener(getFieldDocListener());
        return tf;
    }

    protected JSpinner makeSpinner(Integer value, Integer min, Integer max, Integer step) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        return makeSpinner(model);
    }

    protected JSpinner makeSpinner(SpinnerModel model) {
        JSpinner spinner = new JSpinner(model);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setColumns(3);
        }
        spinner.addChangeListener(getFieldChangeListener());
        return spinner;
    }

    protected JComboBox<Rotation> makeRotationCombo() {
        JComboBox<Rotation> combo = new JComboBox<>();
        Arrays.stream(Rotation.values()).forEach(combo::addItem);
        combo.addItemListener(getFieldItemListener());
        return combo;
    }

    protected static <T> boolean comboSelect(JComboBox<T> combo, Predicate<? super T> predicate) {
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

    protected Character getCharacterCode(JComboBox<String> combo) {
        String s = (String) combo.getSelectedItem();
        if (s==null || s.isEmpty()) {
            return null;
        }
        return s.charAt(0);
    }

    protected static boolean allHaveValues(Component... components) {
        for (Component comp : components) {
            if (comp instanceof JComboBox) {
                if (((JComboBox) comp).getSelectedItem()==null) {
                    return false;
                }
            } else if (comp instanceof JTextField) {
                if (((JTextField) comp).getText().trim().isEmpty()) {
                    return false;
                }
            } else if (comp instanceof JSpinner) {
                if (((JSpinner) comp).getValue()==null) {
                    return false;
                }
            } else {
                throw new IllegalArgumentException("Unexpected component " + comp);
            }
        }
        return true;
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

    protected abstract void setXY(int x, int y);

    public void dragged(DesignField field) {
        boolean listening = isChangeListening();
        setChangeListening(false);
        setXY(field.getX(), field.getY());
        setChangeListening(listening);
    }

}
