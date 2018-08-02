package uk.ac.sanger.labeldesign.view.implementation;

import uk.ac.sanger.labeldesign.model.*;
import uk.ac.sanger.labeldesign.view.*;

import java.awt.*;

/**
 * @author dr6
 */
public class DesignRender implements Render<Design> {
    @Override
    public Rectangle render(Draw draw, Design design) {
        return draw.rect(new Rectangle(design.getXMin(), design.getYMin(), design.getWidth(), design.getHeight()),
                Color.white, Color.darkGray);
    }
}
