package uk.ac.sanger.labeldesign.component;

import uk.ac.sanger.labeldesign.model.Design;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author dr6
 */
public class MouseControl extends MouseAdapter {
    private DesignPanel designPanel;

    private Design getDesign() {
        return designPanel.getDesign();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        if (e.getClickCount()==2) {

        }
        // TODO
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        //TODO
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        //TODO
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        //TODO
    }

    public boolean isEnabled() {
        return getDesign()!=null;
    }
}
