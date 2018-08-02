package uk.ac.sanger.labeldesign;

import uk.ac.sanger.labeldesign.model.*;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.util.Collections;

/**
 * Tool to convert a {@link Design} to JSON.
 * @author dr6
 */
public class JsonConversion {
    private JsonBuilderFactory jbf;
    private JsonWriterFactory jwf;

    public JsonConversion(JsonBuilderFactory jbf, JsonWriterFactory jwf) {
        this.jbf = jbf;
        this.jwf = jwf;
    }

    public JsonConversion() {
        this(Json.createBuilderFactory(null),
                Json.createWriterFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true)));
    }

    public JsonValue toJson(StringField sf) {
        return jbf.createObjectBuilder()
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
        return jbf.createObjectBuilder()
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

    private static String to2s(int n) {
        return String.format("%02d", n);
    }

    private static String to4s(int n) {
        return String.format("%04d", n);
    }

    private static String rep2(int n) {
        char ch = (char) ('0'+n);
        return new String(new char[] {ch, ch});
    }

    public JsonWriter getWriter(OutputStream out) {
        return jwf.createWriter(out);
    }
    public JsonWriter getWriter(Writer out) {
        return jwf.createWriter(out);
    }

    public String toString(JsonValue value) {
        try (StringWriter sw = new StringWriter()) {
            getWriter(sw).write(value);
            return sw.toString();
        } catch (IOException e) {
            throw new JsonException("Error from writing JSON to string.", e);
        }
    }

}
