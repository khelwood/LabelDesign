package uk.ac.sanger.labeldesign.view;

import uk.ac.sanger.labeldesign.model.*;

import java.awt.Font;
import java.util.Map;
import java.util.stream.Stream;

public interface RenderFactory {
    Render<BarcodeField> getBarcodeRender();
    Render<Design> getDesignRender();
    Render<StringField> getStringRender();
    Font getFont(char fontCode);
    Stream<Map.Entry<Character, String>> fontDescs();
}
