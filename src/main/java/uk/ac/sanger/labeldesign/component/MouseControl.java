package uk.ac.sanger.labeldesign.component;

import uk.ac.sanger.labeldesign.DesignApp;
import uk.ac.sanger.labeldesign.model.Design;
import uk.ac.sanger.labeldesign.model.DesignField;

import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author dr6
 */
public class MouseControl extends MouseAdapter {
    private static final int C_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    private static boolean isEditEvent(MouseEvent event) {
        return (event.getModifiers() & C_MASK)!=0;
    }

    private DesignApp app;
    private int lastX, lastY;
    private boolean inSpace;
    private boolean editMode;
    private boolean allowDragging;

    public MouseControl(DesignApp app) {
        this.app = app;
    }

    private Design getDesign() {
        return app.getDesign();
    }

    private DesignField fieldAt(MouseEvent e) {
        return app.getFieldAt(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (!isEnabled()) {
            return;
        }
        editMode = isEditEvent(event);
        DesignField field = fieldAt(event);
        allowDragging = false;
        if (editMode) {
            allowDragging = (field!=null && app.toggleSelection(field));
        } else if (field!=null && app.isSelected(field)) {
            allowDragging = true;
        } else {
            app.selectNone();
            if (field!=null) {
                app.select(field);
                allowDragging = true;
            }
        }
        lastX = event.getX();
        lastY = event.getY();
        inSpace = (field==null);
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (!editMode) {
            app.selectNone();
            DesignField field = fieldAt(event);
            if (field!=null) {
                app.select(field);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (!isEnabled()) {
            return;
        }
        int x = event.getX();
        int y = event.getY();
        if (inSpace) {
            app.setSelectionRect(lastX, lastY, x, y);
        } else if (allowDragging) {
            app.drag(x-lastX, y-lastY);
            lastX = x;
            lastY = y;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        app.clearSelectionRect();
        allowDragging = false;
    }

    public boolean isEnabled() {
        return getDesign()!=null;
    }
}
