package uk.ac.sanger.labeldesign.component;

import uk.ac.sanger.labeldesign.model.Design;
import uk.ac.sanger.labeldesign.view.Draw;
import uk.ac.sanger.labeldesign.view.RenderFactory;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A panel that displays a {@link Design}.
 * @author dr6
 */
public class DesignPanel extends JPanel {
    private RenderFactory renderFactory;
    private Design design;
    private int x0, y0;

    public DesignPanel(RenderFactory renderFactory) {
        this.renderFactory = renderFactory;
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                DesignPanel.this.mousePressed(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                DesignPanel.this.mouseReleased(e);
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                DesignPanel.this.mouseDragged(e);
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        x0 = y0 = 0;
        if (design!=null) {
            int wi = design.getWidth();
            int hi = design.getHeight();
            int gw = getWidth();
            int gh = getHeight();
            x0 = (gw-wi) / 2 - design.getXMin();
            y0 = (gh-hi) / 2 - design.getYMin();
            g.translate(x0, y0);
            try (Draw draw = new Draw(g)) {
                renderFactory.getDesignRender().render(draw, design);
            }
        }
    }

    public void setDesign(Design design) {
        this.design = design;
        repaint();
    }

    public Design getDesign() {
        return this.design;
    }

    private void mousePressed(MouseEvent e) {
        // TODO
    }

    private void mouseReleased(MouseEvent e) {
        // TODO
    }

    private void mouseDragged(MouseEvent e) {

    }
}
