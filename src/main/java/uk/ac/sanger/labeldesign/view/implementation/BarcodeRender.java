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
        Rectangle rect;
        if (bf.is2D()) {
            rect = render2D(draw, bf);
        } else {
            rect = render1D(draw, bf);
        }
        if (bf.getRotation()!=0) {
            rect = Draw.rotate(rect, bf.getX(), bf.getY(), bf.getRotation());
        }
        return rect;
    }

    private Rectangle render2D(Draw draw, BarcodeField bf) {
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

    private Rectangle render1D(Draw draw, BarcodeField bf) {
        int moduleWidth = bf.getModuleWidth()*10;
        int width = moduleWidth*12;
        int height = bf.getHeight();
        Rectangle rect = new Rectangle(bf.getX(), bf.getY(), width, height);
        int lineWidth = moduleWidth/5;
        for (int x = 0, i = 0; x < width; x += lineWidth, ++i) {
            if (i%3==0 || i%7==0) {
                draw.rect(rect.x + x, rect.y, lineWidth, height, Color.black, null);
            }
        }
        return rect;
    }
}
