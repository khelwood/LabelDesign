package uk.ac.sanger.labeldesign.view.implementation;

import uk.ac.sanger.labeldesign.model.BarcodeField;
import uk.ac.sanger.labeldesign.view.Draw;
import uk.ac.sanger.labeldesign.view.Render;

import java.awt.Color;
import java.awt.Rectangle;

/**
 * @author dr6
 */
public class BarcodeRender implements Render<BarcodeField> {
    @Override
    public Rectangle render(Draw draw, BarcodeField bf) {
        if (bf.getRotation()!=0) {
            draw.rotate(bf.getX(), bf.getY(), bf.getRotation()*Math.PI/2);
        }
        Rectangle rect = renderRot0(draw, bf);
        if (bf.getRotation()!=0) {
            rect = Draw.rotate(rect, bf.getX(), bf.getY(), bf.getRotation());
        }
        return rect;
    }
    public Rectangle renderRot0(Draw draw, BarcodeField bf) {
        int size = 20 * bf.getCellWidth(); // approximate
        Rectangle rect = new Rectangle(bf.getX(), bf.getY(), size, size);
        int cw = size / 10;
        draw.rect(rect.x, rect.y, cw, size, Color.black, null);
        draw.rect(rect.x, rect.y+size-cw, size, cw, Color.black, null);
        for (int i = 1; i < 10; ++i) {
            for (int j = (i & 1); j < 10; j += 2) {
                draw.rect(rect.x + i * size / 10, rect.y + j * size / 10, cw, cw, Color.black, null);
            }
        }
        return rect;
    }
}
