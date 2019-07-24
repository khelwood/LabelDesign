package uk.ac.sanger.labeldesign.conversion;

import uk.ac.sanger.labeldesign.model.*;
import uk.ac.sanger.labeldesign.view.implementation.FontFactory;

import java.io.Closeable;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author dr6
 */
public class CabGenerator implements Closeable {
    private static final String
            SET_UNIT = "m m",
            JOB_START = "J",
            SET_HEAT = "H 100",
            SET_AMOUNT = "A 1",
            SET_ORIENTATION = "O R";

    private PrintWriter writer;

    public CabGenerator(PrintWriter writer) {
        this.writer = writer;
    }

    public void write(Design design, Map<String, String> values) {
        writeln(SET_UNIT);
        writeln(JOB_START);
        // writeln("j myjobid");
        writeln(SET_HEAT);
        writeDimensions(design);
        writeln(SET_ORIENTATION);
        for (BarcodeField bf : design.getBarcodeFields()) {
            write(bf, values.get(bf.getName()));
        }
        for (StringField sf : design.getStringFields()) {
            write(sf, values.get(sf.getName()));
        }
        writeln(SET_AMOUNT);
    }

    private void writeln(String string) {
        writer.println(string);
    }

    private void formatln(String format, Object... args) {
        writer.format(format, args);
        writer.println();
    }

    @Override
    public void close() {
        writer.close();
    }

    private void writeDimensions(Design design) {
        final String ptype = "l1"; // TODO -- correct ptype?
        formatln("S %s;%s,%s,%s,%s,%s", ptype,
                -design.getYMin()/10, -design.getXMin()/10,
                design.getHeight()/10, design.getHeight()/10+4, design.getWidth()/10);
    }

    private void write(BarcodeField bf, String value) {
        if (value==null) {
            return;
        }
        switch (bf.getType()) {
            case DATAMATRIX:
                writeDataMatrix(bf, value);
                break;
            case EAN13:
                writeEAN13(bf, value);
                break;
            case CODE128:
                writeCode128(bf, value);
        }
    }

    private void writeDataMatrix(BarcodeField bf, String value) {
        formatln("B %s,%s,%s,DATAMATRIX,%.1f;%s",
                bf.getX()/10, bf.getY()/10, bf.getRotation().degrees(),
                bf.getCellWidth()/9.0,
                value);
    }

    private void writeEAN13(BarcodeField bf, String value) {
        formatln("B %s,%s,%s,EAN13,%s,%.1f;%s",
                bf.getX()/10+3, bf.getY()/10, bf.getRotation().degrees(),
                bf.getHeight()/10, bf.getModuleWidth()/5.0,
                value);
    }

    private void writeCode128(BarcodeField bf, String value) {
        formatln("B %s,%s,%s,CODE128,%s,%.2f;%s",
                bf.getX()/10+3, bf.getY()/10, bf.getRotation().degrees(),
                bf.getHeight()/10, bf.getModuleWidth()/10.0,
                value);
    }

    private void write(StringField sf, String value) {
        final int font = (FontFactory.mono(sf.getFontCode()) ? 596 : 3); // Monospace 821 : Swiss 721
        String fontSize = "pt"+ (int) (FontFactory.nominalFontSize(sf.getFontCode())/2);
        formatln("T %s,%s,%s,%s,%s;%s",
                sf.getX()/10, sf.getY()/10, sf.getRotation().degrees(),
                font, fontSize,
                value);
    }

}
