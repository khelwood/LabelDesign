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
    private final Color selectionFill = new Color(0x404040d0, true);

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

    public boolean finishRect() {
        boolean anything = !this.selectedInRect.isEmpty();
        if (anything) {
            this.selected = getSelected();
            this.selectedInRect = Collections.emptySet();
        }
        this.rect = null;
        return anything;
    }

    public void draw(Graphics g, Map<?, Rectangle> fieldBounds) {
        Set<DesignField> sel = getSelected();
        if (sel.isEmpty() && rect==null) {
            return;
        }
        try (Draw draw = new Draw(g)) {
            for (DesignField df : sel) {
                Rectangle bounds = fieldBounds.get(df);
                if (bounds!=null) {
                    draw.rect(bounds, selectionFill, null);
                }
            }
            if (rect!=null) {
                draw.setStroke(getDashStroke());
                draw.rect(rect, selectionFill, Color.blue);
            }
        }
    }

    public void clear() {
        selected = Collections.emptySet();
        selectedInRect = Collections.emptySet();
        rect = null;
    }

    public boolean add(DesignField df) {
        Objects.requireNonNull(df, "Cannot add null to selection");
        if (!(selected instanceof HashSet)) {
            selected = new HashSet<>(selected);
        }
        return selected.add(df);
    }

    public DesignField getSingleSelected() {
        return (selected.size()==1 ? selected.iterator().next() : null);
    }

    public void addAll(Collection<? extends DesignField> fields) {
        Objects.requireNonNull(fields, "Cannot add all (null) to selection");
        if (selected.isEmpty()) {
            selected = new HashSet<>(fields);
            return;
        }
        if (!(selected instanceof HashSet)) {
            selected = new HashSet<>(selected);
        }
        selected.addAll(fields);
    }

    public boolean contains(DesignField df) {
        return (selected.contains(df) || selectedInRect.contains(df));
    }

    public boolean toggle(DesignField df) {
        Objects.requireNonNull(df, "Cannot toggle null");
        if (add(df)) {
            return true;
        }
        selected.remove(df);
        return false;
    }

    public boolean isEmpty() {
        return (selected.isEmpty() && selectedInRect.isEmpty());
    }

    @Override
    public Iterator<DesignField> iterator() {
        return getSelected().iterator();
    }
}
