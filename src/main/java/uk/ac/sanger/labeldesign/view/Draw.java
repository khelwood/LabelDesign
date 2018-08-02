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

    public void rect(int x, int y, int wid, int hei, Color inner, Color outer) {
        if (inner!=null) {
            graphics.setColor(inner);
            graphics.fillRect(x, y, wid, hei);
        }
        if (outer!=null) {
            graphics.setColor(outer);
            graphics.drawRect(x, y, wid, hei);
        }
    }

    public void string(int x, int y, String string, Color colour, Font font, int spacing) {
        graphics.setColor(colour);
        graphics.setFont(font);
        for (int i = 0; i < string.length(); ++i) {
            char ch = string.charAt(i);
            int wid = graphics.getFontMetrics().charWidth(ch);
            graphics.drawString(String.valueOf(ch), x, y);
            x += wid + spacing;
        }
    }

    public Draw create() {
        return new Draw(this.graphics);
    }

    public void rotate(int x, int y, double angle) {
        ((Graphics2D) graphics).rotate(angle, x, y);
    }
}
