package uk.ac.sanger.labeldesign.view.implementation;

import uk.ac.sanger.labeldesign.model.StringField;
import uk.ac.sanger.labeldesign.view.*;

import java.awt.Color;
import java.awt.Rectangle;

/**
 * @author dr6
 */
public class StringRender implements Render<StringField> {
    private RenderFactory renderFactory;

    public StringRender(RenderFactory renderFactory) {
        this.renderFactory = renderFactory;
    }

    @Override
    public Rectangle render(Draw draw, StringField sf) {
        if (sf.getRotation()!=0) {
            draw.rotate(sf.getX(), sf.getY(), sf.getRotation()*Math.PI/2);
        }
        Rectangle rect = draw.string(sf.getX(), sf.getY(), sf.getDisplayText(),
                Color.black, renderFactory.getFont(sf.getFontCode()), sf.getSpacing(),
                sf.getHorizontalMagnification()/5, sf.getVerticalMagnification()/5);
        if (sf.getRotation()!=0) {
            rect = Draw.rotate(rect, sf.getX(), sf.getY(), sf.getRotation());
        }
        return rect;
    }
}
