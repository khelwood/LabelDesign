package uk.ac.sanger.labeldesign.conversion;

import uk.ac.sanger.labeldesign.model.*;

import javax.json.*;

/**
 * @author dr6
 */
public class DesignWriter extends JsonOutput {
    public JsonValue toJson(StringField sf) {
        return getBuilderFactory().createObjectBuilder()
                .add("name", sf.getName())
                .add("text", sf.getDisplayText())
                .add("font", String.valueOf(sf.getFontCode()))
                .add("spacing", sf.getSpacing())
                .add("rotation", sf.getRotation().index())
                .add("magnification", position(sf.getHorizontalMagnification(), sf.getVerticalMagnification()))
                .add("position", position(sf))
                .build();
    }

    public JsonValue toJson(BarcodeField bf) {
        JsonObjectBuilder builder =  getBuilderFactory().createObjectBuilder()
                .add("name", bf.getName())
                .add("type", String.valueOf(bf.getTypeCode()))
                .add("rotation", bf.getRotation().index())
                .add("position", position(bf));

        if (bf.is2D()) {
            builder.add("cellWidth", bf.getCellWidth());
        } else {
            builder.add("height", bf.getHeight());
            builder.add("moduleWidth", bf.getModuleWidth());
            builder.add("checkDigitType", bf.getCheckDigitType());
        }

        return builder.build();
    }

    private JsonArray position(DesignField sf) {
        return position(sf.getX(), sf.getY());
    }
    private JsonArray position(int x, int y) {
        return getBuilderFactory().createArrayBuilder().add(x).add(y).build();
    }

    public JsonValue toJson(Design design) {
        JsonBuilderFactory jbf = getBuilderFactory();
        JsonArrayBuilder sfBuilder = jbf.createArrayBuilder();
        for (StringField sf : design.getStringFields()) {
            sfBuilder.add(toJson(sf));
        }
        JsonArrayBuilder bfBuilder = jbf.createArrayBuilder();
        for (BarcodeField bf : design.getBarcodeFields()) {
            bfBuilder.add(toJson(bf));
        }
        JsonValue bounds = jbf.createObjectBuilder()
                .add("min", position(design.getXMin(), design.getYMin()))
                .add("max", position(design.getXMax(), design.getYMax()))
                .build();
        return jbf.createObjectBuilder()
                .add("version", 0)
                .add("name", design.getName())
                .add("type", design.getLabelTypeId())
                .add("bounds", bounds)
                .add("strings", sfBuilder.build())
                .add("barcodes", bfBuilder.build())
                .build();
    }
}
