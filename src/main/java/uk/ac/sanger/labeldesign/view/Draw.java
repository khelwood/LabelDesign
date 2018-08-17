package uk.ac.sanger.labeldesign.view;

import java.awt.*;

/**
 * A drawing context.
 * We abstract away from Graphics in case we want to do some additional scaling/rotating/translating.
 * Also in order to make use of {@code AutoCloseable}.
 * @author dr6
 */
public class Draw implements AutoCloseable {
    private Graphics graphics;

    public Draw(Graphics graphics) {
        this.graphics = graphics.create();
    }

    @Override
    public void close() {
        this.graphics.dispose();
    }

    public Rectangle rect(int x, int y, int w, int h, Color inner, Color outer) {
        return rect(new Rectangle(x, y, w, h), inner, outer);
    }

    public Rectangle rect(Rectangle rect, Color inner, Color outer) {
        if (inner!=null) {
            graphics.setColor(inner);
            graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        if (outer!=null) {
            graphics.setColor(outer);
            graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
        }
        return rect;
    }

    public void setStroke(Stroke stroke) {
        ((Graphics2D) graphics).setStroke(stroke);
    }

    public Rectangle string(int x, int y, String string, Color colour, Font font, int spacing,
                            int xScale, int yScale) {
        if (string.isEmpty()) {
            return new Rectangle(x,y,0,0);
        }
        int x1 = x;
        graphics.setColor(colour);
        graphics.setFont(font);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        for (int i = 0; i < string.length(); ++i) {
            char ch = string.charAt(i);
            int wid = fontMetrics.charWidth(ch) * xScale;
            scaleString(graphics, String.valueOf(ch), x1, y, xScale, yScale);
            x1 += wid + spacing;
        }
        int y0 = y - fontMetrics.getMaxAscent()*yScale;
        int y1 = y + fontMetrics.getMaxDescent()*yScale;
        return new Rectangle(x, y0, x1-x-spacing, y1-y0);
    }

    private void scaleString(Graphics g, String string, int x, int y, int xScale, int yScale) {
        if (xScale<=1 && yScale<=1) {
            g.drawString(string, x, y);
        }
        Graphics2D g2 = (Graphics2D) (g.create());
        g2.translate(x, y);
        g2.scale(xScale, yScale);
        g2.drawString(string, 0, 0);
        g2.dispose();
    }

//    public Draw create() {
//        return new Draw(this.graphics);
//    }

    public void rotate(int x, int y, double angle) {
        ((Graphics2D) graphics).rotate(angle, x, y);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static Rectangle rotate(Rectangle rect, int x0, int y0, int rotation) {
        rotation &= 3;
        if (rotation==0) {
            return rect;
        }
        Rectangle newRect = new Rectangle();
        if (rotation==2) {
            newRect.width = rect.width;
            newRect.height = rect.height;
            newRect.x = x0 + x0 - rect.x - rect.width;
            newRect.y = y0 + y0 - rect.y - rect.height;
        } else {
            newRect.width = rect.height;
            newRect.height = rect.width;
            if (rotation==1) {
                newRect.x = x0 - (rect.y - y0 + rect.height);
                newRect.y = y0 + (rect.x - x0);
            } else {
                newRect.x = x0 + (rect.y - y0);
                newRect.y = y0 - (rect.x - x0 + rect.width);
            }
        }
        return newRect;
    }
//
//    private static void assertEquals(Object x, Object y) {
//        if (!x.equals(y)) {
//            throw new AssertionError("Expected "+y+"; got "+x);
//        }
//    }
//
//    private static Rectangle slowtate(Rectangle rect, int x, int y) {
//        int x0 = rect.x - x;
//        int y0 = rect.y - y;
//        int x1 = x0 + rect.width;
//        int y1 = y0 + rect.height;
//
//        int x0n = -y0;
//        int y0n = x0;
//
//        int x1n = -y1;
//        int y1n = x1;
//
//        return new Rectangle(x + Math.min(x0n, x1n), y + Math.min(y0n, y1n), Math.abs(x1n-x0n), Math.abs(y1n-y0n));
//    }
//
//    public static void main(String[] args) {
//        int x0 = 100, y0 = 200;
//        Rectangle rect = new Rectangle(113, 117, 200, 300);
//        Rectangle rect1 = rotate(rect, x0, y0, 1);
//        assertEquals(rect1, slowtate(rect, x0, y0));
//        Rectangle rect2 = rotate(rect, x0, y0, 2);
//        assertEquals(rect2, slowtate(rect1, x0, y0));
//        Rectangle rect3 = rotate(rect, x0, y0, 3);
//        assertEquals(rect3, slowtate(rect2, x0, y0));
//    }
}
