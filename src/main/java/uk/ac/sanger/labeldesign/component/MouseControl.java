package uk.ac.sanger.labeldesign.component;

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

    private DesignPanel designPanel;
    private int lastX, lastY;
    private boolean inSpace;
    private boolean editMode;
    private boolean allowDragging;

    public MouseControl(DesignPanel designPanel) {
        this.designPanel = designPanel;
    }

    private Design getDesign() {
        return designPanel.getDesign();
    }

    private DesignField fieldAt(MouseEvent e) {
        return designPanel.getFieldAt(e.getX(), e.getY());
    }

    private static boolean editSelection(MouseEvent event) {
        return (event.getModifiers() & C_MASK)!=0;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (!isEnabled()) {
            return;
        }
        editMode = editSelection(event);
        DesignField field = fieldAt(event);
        allowDragging = false;
        if (editMode) {
            allowDragging = (field!=null && designPanel.toggleSelection(field));
        } else if (field!=null && designPanel.isSelected(field)) {
            allowDragging = true;
        } else {
            designPanel.deselect();
            if (field!=null) {
                designPanel.select(field);
                allowDragging = true;
            }
        }
        lastX = event.getX();
        lastY = event.getY();
        inSpace = (field==null);
        designPanel.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (!editMode) {
            designPanel.deselect();
            DesignField field = fieldAt(event);
            if (field!=null) {
                designPanel.select(field);
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
            designPanel.setSelectionRect(lastX, lastY, x, y);
        } else if (allowDragging) {
            designPanel.drag(x-lastX, y-lastY);
            lastX = x;
            lastY = y;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        designPanel.clearSelectionRect();
        allowDragging = false;
    }

    public boolean isEnabled() {
        return getDesign()!=null;
    }
}
