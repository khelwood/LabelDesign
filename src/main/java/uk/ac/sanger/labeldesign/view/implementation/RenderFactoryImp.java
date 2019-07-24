package uk.ac.sanger.labeldesign.view.implementation;

import uk.ac.sanger.labeldesign.view.RenderFactory;

import java.awt.Font;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author dr6
 */
public class RenderFactoryImp implements RenderFactory {
    private DesignRender designRender = new DesignRender();
    private BarcodeRender barcodeRender = new BarcodeRender();
    private StringRender stringRender = new StringRender(this);

    private FontFactory fontFactory = new FontFactory();
    private Font[] fontCache = new Font[21];

    @Override
    public BarcodeRender getBarcodeRender() {
        return this.barcodeRender;
    }

    @Override
    public DesignRender getDesignRender() {
        return this.designRender;
    }

    @Override
    public StringRender getStringRender() {
        return this.stringRender;
    }

    private Font newFont(char fontCode) {
        String family = substituteFamily(fontCode);
        int style = style(fontCode);
        float size = FontFactory.nominalFontSize(fontCode);
        return fontFactory.getFont(family, style, size);
    }

    private String substituteFamily(char fontCode) {
        if (fontCode >= 'A' && fontCode <= 'F') {
            return "Times Roman";
        }
        if (fontCode >= 'G' && fontCode <= 'L') {
            return "Helvetica";
        }
        if (fontCode >= 'O' && fontCode <= 'P') {
            return "Prestige Elite";
        }
        if (fontCode >= 'Q' && fontCode <= 'R') {
            return "Courier";
        }
        switch (fontCode) {
            case 'M': return "Presentation";
            case 'N': return "Letter gothic";
            case 'S': return "OCR-A";
            case 'T': return "OCR-B";
            case 'q': return "Gothic 725";
            default: throw new IllegalArgumentException("Unknown font code: "+fontCode);
        }
    }

    private int style(char fontCode) {
        switch (fontCode) {
            case 'C': case 'D': case 'E': case 'J': case 'K': case 'M': case 'P': case 'R':
                return Font.BOLD;
            case 'F': case 'L':
                return Font.ITALIC;
            default:
                return Font.PLAIN;
        }
    }

    private String fontDesc(char fontCode) {
        float fontSize = FontFactory.nominalFontSize(fontCode);
        String name = FontFactory.nominalFontName(fontCode);
        if ((int) fontSize==fontSize) {
            return String.format("%s: %dpoint", name, (int) fontSize);
        } else {
            return String.format("%s: %.1fpoint", name, fontSize);
        }
    }

    @Override
    public Stream<Map.Entry<Character, String>> fontDescs() {
        return IntStream.concat(IntStream.range('A', 'T'+1), IntStream.of('q'))
                .mapToObj(ch -> (char) ch)
                .map(ch -> new AbstractMap.SimpleEntry<>(ch, fontDesc(ch)));
    }

    @Override
    public Font getFont(char fontCode) {
        int fontIndex;
        if (fontCode=='q') {
            fontIndex = 20;
        } else if (fontCode>='A' && fontCode<='T'){
            fontIndex = fontCode-'A';
        } else {
            throw new IllegalArgumentException("Unknown font code: "+fontCode);
        }
        Font font = fontCache[fontIndex];
        if (font==null) {
            font = newFont(fontCode);
            fontCache[fontIndex] = font;
        }
        return font;
    }

    public static void main(String[] args) {
        RenderFactoryImp rfi = new RenderFactoryImp();
        for (char ch = 'A'; ch <= 'T'; ch += 1) {
            Font font = rfi.getFont(ch);
            System.out.printf("%s : %s%n", ch, font);
        }
    }
}
