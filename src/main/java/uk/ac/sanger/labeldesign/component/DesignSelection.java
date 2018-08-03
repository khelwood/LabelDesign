package uk.ac.sanger.labeldesign.component;

import uk.ac.sanger.labeldesign.model.DesignField;
import uk.ac.sanger.labeldesign.view.Draw;

import java.awt.*;
import java.util.*;

/**
 * @author dr6
 */
public class DesignSelection implements Iterable<DesignField> {
    private Set<DesignField> selected = Collections.emptySet();
    private Set<DesignField> selectedInRect = Collections.emptySet();

    private Rectangle rect;
    private Stroke dashStroke;

    public Set<DesignField> getSelected() {
        if (rect==null || selectedInRect.isEmpty()) {
            return selected;
        }
        if (selected.isEmpty()) {
            return selectedInRect;
        }
        Set<DesignField> combined = new HashSet<>(selected.size()+selectedInRect.size());
        combined.addAll(selected);
        combined.addAll(selectedInRect);
        return combined;
    }

    private Stroke getDashStroke() {
        if (dashStroke==null) {
            dashStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
                    new float[] { 5 }, 0);
        }
        return dashStroke;
    }

    public void setRect(Rectangle rect, Set<DesignField> fields) {
        this.rect = rect;
        this.selectedInRect = fields;
    }

    public void finishRect() {
        if (!this.selectedInRect.isEmpty()) {
            this.selected = getSelected();
        }
        this.rect = null;
        this.selectedInRect = Collections.emptySet();
    }

    public void draw(Graphics g, Map<?, Rectangle> fieldBounds) {
        Set<DesignField> sel = getSelected();
        if (sel.isEmpty() && rect==null) {
            return;
        }
        Color colour = Color.blue;
        try (Draw draw = new Draw(g)) {
            for (DesignField df : sel) {
                Rectangle bounds = fieldBounds.get(df);
                if (bounds!=null) {
                    draw.rect(bounds, null, colour);
                }
            }
            if (rect!=null) {
                draw.setStroke(getDashStroke());
                draw.rect(rect, null, colour);
            }
        }
    }

    public void clear() {
        selected = Collections.emptySet();
        selectedInRect = Collections.emptySet();
        rect = null;
    }

    public void add(DesignField df) {
        if (!(selected instanceof HashSet)) {
            selected = new HashSet<>(selected);
        }
        selected.add(df);
    }

    public boolean contains(DesignField df) {
        return (selected.contains(df) || selectedInRect.contains(df));
    }

    public void toggle(DesignField df) {
        if (!selected.add(df)) {
            selected.remove(df);
        }
    }

    public boolean isEmpty() {
        return (selected.isEmpty() && selectedInRect.isEmpty());
    }

    @Override
    public Iterator<DesignField> iterator() {
        return getSelected().iterator();
    }
}
