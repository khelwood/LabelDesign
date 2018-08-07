package uk.ac.sanger.labeldesign.component;

import uk.ac.sanger.labeldesign.model.Design;
import uk.ac.sanger.labeldesign.view.RenderFactory;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author dr6
 */
public class DesignFrame extends JFrame {
    private DesignPanel designPanel;

    public DesignFrame(RenderFactory renderFactory) {
        designPanel = new DesignPanel(renderFactory);
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(designPanel, BorderLayout.CENTER);

        designPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                designPanel.repaint();
            }
        });
    }

    public Design getDesign() {
        return this.designPanel.getDesign();
    }

    public void setDesign(Design design) {
        this.designPanel.setDesign(design);
        if (design!=null && design.getName()!=null) {
            setTitle(design.getName());
        }
    }

    public void repaintDesign() {
        designPanel.repaint();
    }
}
