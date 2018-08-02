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
        switch (fontCode) {
            case 'A': return fontFactory.getFont("Times Roman", Font.PLAIN, 12);
            case 'B': return fontFactory.getFont("Times Roman", Font.PLAIN, 15);
            case 'C': return fontFactory.getFont("Times Roman", Font.BOLD, 15);
            case 'D': return fontFactory.getFont("Times Roman", Font.BOLD, 18);
            case 'E': return fontFactory.getFont("Times Roman", Font.BOLD, 21);
            case 'F': return fontFactory.getFont("Times Roman", Font.ITALIC, 18);
            case 'G': return fontFactory.getFont("Helvetica", Font.PLAIN, 9);
            case 'H': return fontFactory.getFont("Helvetica", Font.PLAIN, 15);
            case 'I': return fontFactory.getFont("Helvetica", Font.PLAIN, 18);
            case 'J': return fontFactory.getFont("Helvetica", Font.BOLD, 18);
            case 'K': return fontFactory.getFont("Helvetica", Font.BOLD, 21);
            case 'L': return fontFactory.getFont("Helvetica", Font.ITALIC, 18);
            case 'M': return fontFactory.getFont("Presentation", Font.BOLD, 27);
            case 'N': return fontFactory.getFont("Letter gothic", Font.PLAIN, 14);
            case 'O': return fontFactory.getFont("Prestige Elite", Font.PLAIN, 10);
            case 'P': return fontFactory.getFont("Prestige Elite", Font.BOLD, 15);
            case 'Q': return fontFactory.getFont("Courier", Font.PLAIN, 15);
            case 'R': return fontFactory.getFont("Courier", Font.BOLD, 18);
            case 'S': return fontFactory.getFont("OCR-A", Font.PLAIN, 12);
            case 'T': return fontFactory.getFont("OCR-B", Font.PLAIN, 12);
            case 'q': return fontFactory.getFont("Gothic 725", Font.PLAIN, 6);

            default: throw new IllegalArgumentException("Unknown font code: "+fontCode);
        }
    }

    private String fontDesc(char fontCode) {
        switch (fontCode) {
            case 'A': return "Times Roman medium: 12point";
            case 'B': return "Times Roman medium: 15point";
            case 'C': return "Times Roman bold: 15point";
            case 'D': return "Times Roman bold: 18point";
            case 'E': return "Times Roman bold: 21point";
            case 'F': return "Times Roman italic: 18point";
            case 'G': return "Helvetica medium: 9point";
            case 'H': return "Helvetica medium: 15point";
            case 'I': return "Helvetica medium: 18point";
            case 'J': return "Helvetica bold: 18point";
            case 'K': return "Helvetica bold: 21point";
            case 'L': return "Helvetica italic: 18point";
            case 'M': return "Presentation bold: 27point";
            case 'N': return "Letter Gothic medium: 14.3point";
            case 'O': return "Prestige Elite medium: 10.5point";
            case 'P': return "Prestige Elite bold: 15point";
            case 'Q': return "Courier medium: 15point";
            case 'R': return "Courier bold: 18point";
            case 'S': return "OCR-A: 12point";
            case 'T': return "OCR-B: 12point";
            case 'q': return "Gothic 725 Black: 6point";

            default: throw new IllegalArgumentException("Unknown font code: "+fontCode);
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
