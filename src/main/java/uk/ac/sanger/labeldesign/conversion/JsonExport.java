package uk.ac.sanger.labeldesign.conversion;

import uk.ac.sanger.labeldesign.model.*;

import javax.json.*;

/**
 * @author dr6
 */
public class JsonExport extends JsonConversion {
    public JsonValue toJson(StringField sf) {
        return getBuilderFactory().createObjectBuilder()
                .add("horizontal_magnification", to2s(sf.getMagnification()))
                .add("vertical_magnification", to2s(sf.getMagnification()))
                .add("font", String.valueOf(sf.getFontCode()))
                .add("space_adjustment", to2s(sf.getSpacing()))
                .add("rotational_angles", rep2(sf.getRotation()))
                .add("x_origin", to4s(sf.getX()))
                .add("y_origin", to4s(sf.getY()))
                .add("field_name", sf.getName())
                .build();
    }

    public JsonValue toJson(BarcodeField bf) {
        return getBuilderFactory().createObjectBuilder()
                .add("barcode_type", String.valueOf(bf.getBarcodeType()))
                .add("height", to4s(bf.getHeight()))
                .add("one_cell_width", to2s(bf.getCellWidth()))
                .add("one_module_width", to2s(bf.getCellWidth()))
                .add("rotational_angle", String.valueOf(bf.getRotation()))
                .add("x_origin", to4s(bf.getX()))
                .add("y_origin", to4s(bf.getY()))
                .add("field_name", bf.getName())
                .build();
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

        JsonObject labelAttributes = jbf.createObjectBuilder()
                .add("name", "label")
                .add("bitmaps_attributes", sfBuilder.build())
                .add("barcodes_attributes", bfBuilder.build())
                .build();

        JsonObject attributes = jbf.createObjectBuilder()
                .add("name", design.getName())
                .add("label_type_id", design.getLabelTypeId())
                .add("labels_attributes", jbf.createArrayBuilder()
                        .add(labelAttributes)
                        .build()
                )
                .build();
        return jbf.createObjectBuilder()
                .add("data", jbf.createObjectBuilder()
                        .add("type", "label_templates")
                        .add("attributes", attributes)
                )
                .build();
    }
}
