package uk.ac.sanger.labeldesign.component.dialog;

import uk.ac.sanger.labeldesign.component.QuickDocumentListener;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyChangeListener;

/**
 * @author dr6
 */
abstract class ComponentDialogPane extends JPanel {
    protected JButton okButton, cancelButton;

    private DocumentListener docListener;
    private ChangeListener changeListener;
    private PropertyChangeListener propChangeListener;

    private boolean okPressed;
    private Runnable closeAction;

    public ComponentDialogPane() {
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

    protected DocumentListener getDocumentListener() {
        if (docListener==null) {
            docListener = (QuickDocumentListener) this::updateState;
        }
        return docListener;
    }

    protected ChangeListener getChangeListener() {
        if (changeListener==null) {
            changeListener = e -> this.updateState();
        }
        return changeListener;
    }

    protected PropertyChangeListener getPropertyChangeListener() {
        if (propChangeListener==null) {
            propChangeListener = e -> updateState();
        }
        return propChangeListener;
    }

    protected JTextField makeTextField() {
        JTextField tf = new JTextField(12);
        tf.getDocument().addDocumentListener(getDocumentListener());
        return tf;
    }

    protected JSpinner makeSpinner(Integer value, Integer min, Integer max, Integer step) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        return makeSpinner(model);
    }

    protected JSpinner makeSpinner(SpinnerModel model) {
        JSpinner spinner = new JSpinner(model);
        spinner.addChangeListener(getChangeListener());
        return spinner;
    }

    protected JComboBox<String> makeRotationCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("0: Normal");
        combo.addItem("1: Rotated 90° clockwise");
        combo.addItem("2: Rotated 180°");
        combo.addItem("3: Rotated 270° clockwise");
        combo.addPropertyChangeListener(getPropertyChangeListener());
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
        okButton.setEnabled(valid());
    }

    public void setCloseAction(Runnable closeAction) {
        this.closeAction = closeAction;
    }

    public boolean showDialog(String title, Frame frame) {
        JDialog dialog = new JDialog(frame, title, true);
        dialog.setContentPane(this);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        this.setCloseAction(dialog::dispose);
        dialog.getRootPane().setDefaultButton(okButton);
        dialog.setVisible(true);
        return isOkPressed();
    }
}
