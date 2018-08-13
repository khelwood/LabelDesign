package uk.ac.sanger.labeldesign.conversion;

import uk.ac.sanger.labeldesign.model.*;

import javax.json.*;
import javax.json.JsonValue.ValueType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dr6
 */
public class DesignReader extends JsonInput {

    public StringField toStringField(JsonValue jsonValue) throws IOException {
        if (!(jsonValue instanceof JsonObject)) {
            throw exception("Expected JSON object representing string field, but got "+jsonValue.getValueType());
        }
        JsonObject jo = (JsonObject) jsonValue;
        StringField sf = new StringField();
        sf.setName(stringFrom(jo, "name"));
        sf.setDisplayText(stringFrom(jo, "text"));
        sf.setFontCode(charFrom(jo, "font"));
        sf.setSpacing(intFrom(jo, "spacing"));
        sf.setRotation(intFrom(jo, "rotation"));
        int[] position = pointFrom(jo, "position");
        int[] magnification = pointFrom(jo, "magnification");
        sf.setHorizontalMagnification(magnification[0]);
        sf.setVerticalMagnification(magnification[1]);
        sf.setPosition(position[0], position[1]);
        return sf;
    }

    public BarcodeField toBarcodeField(JsonValue jsonValue) throws IOException {
        if (!(jsonValue instanceof JsonObject)) {
            throw exception("Expected JSON object representing barcode field, but got "+jsonValue.getValueType());
        }
        JsonObject jo = (JsonObject) jsonValue;
        BarcodeField bf = new BarcodeField();
        bf.setName(stringFrom(jo, "name"));
        bf.setBarcodeType(charFrom(jo, "type"));
        bf.setCellWidth(intFrom(jo, "cellwidth"));
        bf.setRotation(intFrom(jo, "rotation"));
        int[] position = pointFrom(jo, "position");
        bf.setPosition(position[0], position[1]);
        return bf;
    }

    @Override
    public Design toDesign(JsonValue jsonValue) throws IOException {
        if (jsonValue.getValueType()==ValueType.ARRAY) {
            JsonArray arr = jsonValue.asJsonArray();
            if (arr.size()!=1) {
                throw exception("Expected one JSON object, but got an array of size "+arr.size());
            }
            jsonValue = jsonValue.asJsonArray().get(0);
            if (jsonValue.getValueType()!=ValueType.OBJECT) {
                throw exception("Expected a JSON object, but got an array containing "+jsonValue.getValueType());
            }
        } else if (jsonValue.getValueType()!=ValueType.OBJECT) {
            throw exception("Expected a JSON object, but got "+jsonValue.getValueType());
        }
        JsonObject jsonObject = jsonValue.asJsonObject();
        int version = intFrom(jsonObject, "version");
        if (version!=0) {
            throw exception("Unsupported version: "+version);
        }

        Design design = new Design();
        design.setName(stringFrom(jsonObject, "name"));
        design.setLabelTypeId(intFrom(jsonObject, "type"));
        JsonObject bounds = objectFrom(jsonObject, "bounds");
        int[] min = pointFrom(bounds, "min");
        int[] max = pointFrom(bounds, "max");
        design.setBounds(min[0], min[1], max[0], max[1]);

        JsonArray stringArray = arrayFrom(jsonObject, "strings");
        JsonArray barcodeArray = arrayFrom(jsonObject, "barcodes");

        List<StringField> stringFields = design.getStringFields();
        if (stringArray instanceof ArrayList) {
            ((ArrayList) stringArray).ensureCapacity(stringArray.size());
        }
        for (JsonValue value : stringArray) {
            stringFields.add(toStringField(value));
        }

        List<BarcodeField> barcodeFields = design.getBarcodeFields();
        if (barcodeFields instanceof ArrayList) {
            ((ArrayList) barcodeFields).ensureCapacity(barcodeArray.size());
        }
        for (JsonValue value : barcodeArray) {
            barcodeFields.add(toBarcodeField(value));
        }

        return design;
    }

}
