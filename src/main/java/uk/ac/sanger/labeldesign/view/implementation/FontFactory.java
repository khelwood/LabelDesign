package uk.ac.sanger.labeldesign.view.implementation;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dr6
 */
public class FontFactory {
    private Map<String, Font> namedFontCache = new HashMap<>();
    private static final float FONT_SCALE = 1.75f;

    public Font getFont(String family, int style, float size) {
        int scaledSize = (int) (FONT_SCALE*size);
        Font font = namedFontCache.get(family);
        if (font == null) {
            font = bestFont(family, style, scaledSize);
            namedFontCache.put(family, font);
        }
        return new Font(font.getFontName(), style, scaledSize);
    }

    private Font bestFont(String family, int style, int size) {
        String[] alternatives = null;
        boolean mono = false;
        if (family.startsWith("Times")) {
            alternatives = new String[] { "Times Roman", "Times New Roman" };
            family = "Times";
        } else if (family.startsWith("Helvetica")) {
            alternatives = new String[] { "Helvetica", "Helvetica Neue" };
            family = "Helvetica";
        } else if (family.startsWith("Courier")) {
            alternatives = new String[] { "Courier", "Courier New" };
            family = "Courier";
            mono = true;
        } else if (family.startsWith("Prestige") || family.startsWith("OCR")) {
            mono = true;
        }
        if (alternatives!=null) {
            for (String name : alternatives) {
                Font font = new Font(name, style, size);
                if (font.getFamily().contains(family)) {
                    return font;
                }
            }
        }
        Font font = new Font(family, style, size);
        if (font.getFamily().contains(family)) {
            return font;
        }
        if (mono) {
            font = new Font(Font.MONOSPACED, style, size);
        }
        return font;
    }
}
