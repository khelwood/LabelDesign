package uk.ac.sanger.labeldesign.view.implementation;

import uk.ac.sanger.labeldesign.model.*;
import uk.ac.sanger.labeldesign.view.*;

import java.awt.Color;
import java.awt.Graphics;

/**
 * @author dr6
 */
public class DesignRender implements Render<Design> {
    private RenderFactory renderFactory;

    public DesignRender(RenderFactory renderFactory) {
        this.renderFactory = renderFactory;
    }

    @Override
    public void render(Draw draw, Design design) {
        draw.rect(design.getXMin(), design.getYMin(), design.getWidth(), design.getHeight(), Color.white, Color.darkGray);
        for (StringField sf : design.getStringFields()) {
            try (Draw newD = draw.create()) {
                renderFactory.getStringRender().render(newD, sf);
            }
        }
        for (BarcodeField bf : design.getBarcodeFields()) {
            try (Draw newD = draw.create()) {
                renderFactory.getBarcodeRender().render(newD, bf);
            }
        }
    }
}
