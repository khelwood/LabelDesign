package uk.ac.sanger.labeldesign.component;

import uk.ac.sanger.labeldesign.DesignApp;
import uk.ac.sanger.labeldesign.OperationEnum;

import javax.swing.*;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import static java.awt.event.KeyEvent.*;

/**
 * @author dr6
 */
public class KeyControl {
    public static final int C_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    private DesignApp app;
    private List<Entry> entries;

    public KeyControl(DesignApp app) {
        this.app = app;
        loadEntries();
    }

    private void loadEntries() {
        entries = new ArrayList<>();
        add(OperationEnum.DELETE_SELECTED).key(VK_DELETE).key(VK_BACK_SPACE);
        add(OperationEnum.SAVE_DESIGN).key(VK_S, C_MASK);
        add(OperationEnum.SAVE_AS).key(VK_S, C_MASK|VK_SHIFT);
        add(OperationEnum.NEW_DESIGN).key(VK_N, C_MASK);
        add(OperationEnum.LOAD_DESIGN).key(VK_O, C_MASK);
        add(OperationEnum.SELECT_ALL).key(VK_A, C_MASK);
        add(OperationEnum.SELECT_NONE).key(VK_ESCAPE);
    }

    private Entry add(OperationEnum operation) {
        Entry entry = new Entry(operation);
        entries.add(entry);
        return entry;
    }

    public void register(JComponent comp) {
        InputMap inputMap = comp.getInputMap();
        ActionMap actionMap = comp.getActionMap();
        for (Entry entry : entries) {
            String string = entry.operation.getActionName();
            for (KeyStroke keyStroke : entry.keyStrokes) {
                inputMap.put(keyStroke, string);
            }
            actionMap.put(string, app.getAction(entry.operation));
        }
    }

    private static class Entry {
        OperationEnum operation;
        List<KeyStroke> keyStrokes;

        Entry(OperationEnum operation) {
            this.operation = operation;
            this.keyStrokes = new ArrayList<>();
        }

        Entry key(int key) {
            return key(KeyStroke.getKeyStroke(key, 0));
        }

        Entry key(KeyStroke keyStroke) {
            this.keyStrokes.add(keyStroke);
            return this;
        }

        Entry key(int key, int modifiers) {
            return key(KeyStroke.getKeyStroke(key, modifiers));
        }
    }
}
