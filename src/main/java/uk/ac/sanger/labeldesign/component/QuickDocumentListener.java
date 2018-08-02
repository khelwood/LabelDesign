package uk.ac.sanger.labeldesign.component;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface QuickDocumentListener extends DocumentListener {
    @Override
    default void insertUpdate(DocumentEvent e) {
        anyUpdate();
    }

    @Override
    default void removeUpdate(DocumentEvent e) {
        anyUpdate();
    }

    @Override
    default void changedUpdate(DocumentEvent e) {
        anyUpdate();
    }

    void anyUpdate();

    static QuickDocumentListener of(QuickDocumentListener qdl) {
        return qdl;
    }
}
