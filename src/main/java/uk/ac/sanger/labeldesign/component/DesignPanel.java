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
    private boolean settingBounds;

    public DesignPanel(RenderFactory renderFactory) {
        this.renderFactory = renderFactory;
    }

    public void addMouseControl(MouseControl mouseControl) {
        addMouseListener(mouseControl);
        addMouseMotionListener(mouseControl);
    }

    public void addKeyControl(KeyControl keyControl) {
        keyControl.register(this);
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
            x0 = (gw - wi) / 2 - design.getXMin();
            y0 = (gh - hi) / 2 - design.getYMin();
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

            if (this.settingBounds) {
                if (fieldBounds.isEmpty()) {
                    design.setBounds(0, 0, 100, 100);
                } else {
                    Rectangle bounds = boundsUnion(fieldBounds.values());
                    int margin = 20;
                    design.setBounds(bounds.x - margin, bounds.y - margin,
                            bounds.x + bounds.width + margin, bounds.y + bounds.height + margin);
                }
                repaint();
            }
        }
        this.settingBounds = false;
    }

    private static Rectangle boundsUnion(Collection<? extends Rectangle> rects) {
        Iterator<? extends Rectangle> iter = rects.iterator();
        Rectangle rect = iter.next();
        int x0 = rect.x;
        int y0 = rect.y;
        int x1 = x0 + rect.width;
        int y1 = y0 + rect.height;
        while (iter.hasNext()) {
            rect = iter.next();
            x0 = Math.min(x0, rect.x);
            y0 = Math.min(y0, rect.y);
            x1 = Math.max(x1, rect.x + rect.width);
            y1 = Math.max(y1, rect.y + rect.height);
        }
        return new Rectangle(x0, y0, x1-x0, y1-y0);
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

    public void adjustDesignBounds() {
        this.settingBounds = true;
        repaint();
    }

    public void setDesign(Design design) {
        this.design = design;
        repaint();
    }

    public Design getDesign() {
        return this.design;
    }

    public DesignSelection getDesignSelection() {
        return this.selection;
    }

    private static int area(Rectangle rect) {
        int w = rect.width;
        int h = rect.height;
        return (w*w + h*h);
    }

    private static boolean intersects(Rectangle a, Rectangle b) {
        return (a.x <= b.x + b.width && b.x <= a.x + a.width
                && a.y <= b.y + b.height && b.y <= a.y + a.height);
    }

    public DesignField getFieldAt(int x, int y) {
        x -= x0;
        y -= y0;
        int bestArea = 0;
        Map.Entry<DesignField, Rectangle> best = null;
        for (Map.Entry<DesignField, Rectangle> entry : fieldBounds.entrySet()) {
            if (entry.getValue().contains(x,y)) {
                int area = area(entry.getValue());
                if (best==null || area < bestArea) {
                    best = entry;
                    bestArea = area;
                }
            }
        }
        return (best==null ? null : best.getKey());
    }

    public void selectAll() {
        selection.clear();
        if (design==null) {
            return;
        }
        selection.addAll(design.getStringFields());
        selection.addAll(design.getBarcodeFields());
        repaint();
    }

    public boolean drag(int dx, int dy) {
        if (selection.isEmpty()) {
            return false;
        }
        for (DesignField df : selection) {
            df.translate(dx, dy);
        }
        repaint();
        return true;
    }

    @Override
    public Dimension getPreferredSize() {
        if (design==null) {
            return super.getPreferredSize();
        }
        return new Dimension(design.getWidth(), design.getHeight());
    }
}
