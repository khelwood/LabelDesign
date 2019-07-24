package uk.ac.sanger.labeldesign.view.implementation;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * Dispenses fonts sized to be consistent with the on-screen label size and fonts with "05" magnification.
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
    
    public static String nominalFontName(char fontCode) {
        switch (fontCode) {
            case 'A': return "Times Roman medium";
            case 'B': return "Times Roman medium";
            case 'C': return "Times Roman bold";
            case 'D': return "Times Roman bold";
            case 'E': return "Times Roman bold";
            case 'F': return "Times Roman italic";
            case 'G': return "Helvetica medium";
            case 'H': return "Helvetica medium";
            case 'I': return "Helvetica medium";
            case 'J': return "Helvetica bold";
            case 'K': return "Helvetica bold";
            case 'L': return "Helvetica italic";
            case 'M': return "Presentation bold";
            case 'N': return "Letter Gothic medium";
            case 'O': return "Prestige Elite medium";
            case 'P': return "Prestige Elite bold";
            case 'Q': return "Courier medium";
            case 'R': return "Courier bold";
            case 'S': return "OCR-A";
            case 'T': return "OCR-B";
            case 'q': return "Gothic 725 Black";

            default: throw new IllegalArgumentException("Unknown font code: "+fontCode);
        }
    }

    public static float nominalFontSize(char fontCode) {
        switch (fontCode) {
            case 'A': return 12;
            case 'B': return 15;
            case 'C': return 15;
            case 'D': return 18;
            case 'E': return 21;
            case 'F': return 18;
            case 'G': return 9;
            case 'H': return 15;
            case 'I': return 18;
            case 'J': return 18;
            case 'K': return 21;
            case 'L': return 18;
            case 'M': return 27;
            case 'N': return 14.3f;
            case 'O': return 10.5f;
            case 'P': return 15;
            case 'Q': return 15;
            case 'R': return 18;
            case 'S': return 12;
            case 'T': return 12;
            case 'q': return 6;
            default: throw new IllegalArgumentException("Unknown font code: "+fontCode);
        }
    }

    public static boolean mono(char fontCode) {
        return (fontCode>='O' && fontCode<='T');
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
