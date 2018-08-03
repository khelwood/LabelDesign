package uk.ac.sanger.labeldesign.component;

import uk.ac.sanger.labeldesign.model.*;
import uk.ac.sanger.labeldesign.view.Draw;
import uk.ac.sanger.labeldesign.view.RenderFactory;

import javax.swing.JPanel;
import java.awt.*;
import java.util.*;

/**
 * A panel that displays a {@link Design}.
 * @author dr6
 */
public class DesignPanel extends JPanel {
    private RenderFactory renderFactory;
    private Design design;
    private Map<DesignField, Rectangle> fieldBounds = Collections.emptyMap();
    private DesignSelection selection = new DesignSelection();
    private int x0, y0;

    public DesignPanel(RenderFactory renderFactory) {
        this.renderFactory = renderFactory;
        MouseControl mouseControl = new MouseControl(this);
        addMouseListener(mouseControl);
        addMouseMotionListener(mouseControl);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        x0 = y0 = 0;
        fieldBounds = new HashMap<>();
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
            for (StringField sf : design.getStringFields()) {
                try (Draw draw = new Draw(g)) {
                    Rectangle bounds = renderFactory.getStringRender().render(draw, sf);
                    fieldBounds.put(sf, bounds);
                }
            }
            for (BarcodeField bf : design.getBarcodeFields()) {
                try (Draw draw = new Draw(g)) {
                    Rectangle bounds = renderFactory.getBarcodeRender().render(draw, bf);
                    fieldBounds.put(bf, bounds);
                }
            }

            selection.draw(g, fieldBounds);
        }
    }

    public void setSelectionRect(int x0, int y0, int x1, int y1) {
        x0 -= this.x0;
        y0 -= this.y0;
        x1 -= this.x0;
        y1 -= this.y0;
        Rectangle rect = new Rectangle(Math.min(x0, x1), Math.min(y0, y1),
                Math.abs(x1-x0), Math.abs(y1-y0));

        Set<DesignField> inRect = new HashSet<>();
        for (Map.Entry<DesignField, Rectangle> entry : fieldBounds.entrySet()) {
            if (intersects(rect, entry.getValue())) {
                inRect.add(entry.getKey());
            }
        }

        selection.setRect(new Rectangle(Math.min(x0, x1), Math.min(y0, y1),
                Math.abs(x1-x0), Math.abs(y1-y0)), inRect);
        repaint();
    }

    public void clearSelectionRect() {
        selection.finishRect();
        repaint();
    }

    public void setDesign(Design design) {
        this.design = design;
        repaint();
    }

    public Design getDesign() {
        return this.design;
    }

    private static int area(Rectangle rect) {
        return rect.width*rect.width + rect.height*rect.height;
    }

    private static boolean intersects(Rectangle a, Rectangle b) {
        return (a.x <= b.x + b.width && b.x <= a.x + a.width
                && a.y <= b.y + b.height && b.y <= a.y + a.height);
    }

    public DesignField getFieldAt(int x, int y) {
        x -= x0;
        y -= y0;
        Map.Entry<DesignField, Rectangle> best = null;
        for (Map.Entry<DesignField, Rectangle> entry : fieldBounds.entrySet()) {
            if (entry.getValue().contains(x,y)) {
                if (best==null || area(entry.getValue()) < area(best.getValue())) {
                    best = entry;
                }
            }
        }
        return (best==null ? null : best.getKey());
    }

    public void deselect() {
        selection.clear();
        repaint();
    }

    public void select(DesignField df) {
        selection.add(df);
        repaint();
    }

    public void toggleSelection(DesignField df) {
        selection.toggle(df);
    }

    public boolean isSelected(DesignField df) {
        return selection.contains(df);
    }

    public void drag(int dx, int dy) {
        if (selection.isEmpty()) {
            return;
        }
        for (DesignField df : selection) {
            df.translate(dx, dy);
        }
        repaint();
    }
}
