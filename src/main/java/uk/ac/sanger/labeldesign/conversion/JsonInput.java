package uk.ac.sanger.labeldesign.conversion;

import uk.ac.sanger.labeldesign.model.Design;

import javax.json.*;
import javax.json.JsonValue.ValueType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author dr6
 */
public abstract class JsonInput {
    private JsonReaderFactory jrf;

    public JsonInput(JsonReaderFactory jrf) {
        this.jrf = jrf;
    }

    public JsonInput() {
        this(Json.createReaderFactory(null));
    }

    public JsonValue readPath(Path path) throws IOException {
        try (JsonReader reader = jrf.createReader(Files.newBufferedReader(path))) {
            return reader.readValue();
        }
    }

    public abstract Design toDesign(JsonValue jsonValue) throws IOException;

    public Design readDesign(Path path) throws IOException {
        JsonValue jsonValue = readPath(path);
        return toDesign(jsonValue);
    }


    protected JsonValue valueFrom(JsonObject jo, String key) throws IOException {
        JsonValue value = jo.get(key);
        if (value==null) {
            throw exception("Missing key \""+key+"\"");
        }
        return value;
    }

    protected int intFrom(JsonObject jo, String key) throws IOException {
        JsonValue value = valueFrom(jo, key);
        if (value.getValueType()==ValueType.STRING) {
            try {
                return Integer.parseInt(((JsonString) value).getString());
            } catch (NumberFormatException e) {
                throw exception("Expected an integer for "+key+", but got a non-integer string.", e);
            }
        }
        if (value.getValueType()!=ValueType.NUMBER) {
            throw exception("Expected an integer from key "+key+", but got "+value.getValueType());
        }
        JsonNumber jnum = (JsonNumber) value;
        if (!jnum.isIntegral()) {
            throw exception("Expected an integer from key "+key+", but got "+jnum);
        }
        return jnum.intValue();
    }

    protected int intFrom(JsonArray jarr, int index) throws IOException {
        JsonValue value = jarr.get(index);
        if (value==null) {
            throw exception("Expected an integer at index "+index+", but got null.");
        }
        if (value.getValueType()==ValueType.STRING) {
            try {
                return Integer.parseInt(((JsonString) value).getString());
            } catch (NumberFormatException e) {
                throw exception("Expected an integer at index "+index+", but got a non-integer string.", e);
            }
        }
        if (value.getValueType()!=ValueType.NUMBER) {
            throw exception("Expected an integer at index "+index+", but got "+value.getValueType());
        }
        JsonNumber jnum = (JsonNumber) value;
        if (!jnum.isIntegral()) {
            throw exception("Expected an integer at index "+index+", but got "+jnum);
        }
        return jnum.intValue();
    }

    protected String stringFrom(JsonObject jo, String key) throws IOException {
        JsonValue value = valueFrom(jo, key);
        if (!(value instanceof JsonString)) {
            throw exception("Expected string for key "+key+" but got "+value.getValueType());
        }
        return ((JsonString) value).getString();
    }

    protected JsonObject objectFrom(JsonObject jo, String key) throws IOException {
        JsonValue value = valueFrom(jo, key);
        if (!(value instanceof JsonObject)) {
            throw exception("Expected JSON object from key "+key+" but got "+value.getValueType());
        }
        return value.asJsonObject();
    }

    protected JsonArray arrayFrom(JsonObject jo, String key) throws IOException {
        JsonValue value = valueFrom(jo, key);
        if (!(value instanceof JsonArray)) {
            throw exception("Expected JSON array from key "+key+" but got "+value.getValueType());
        }
        return value.asJsonArray();
    }

    protected int[] pointFrom(JsonObject jo, String key) throws IOException {
        JsonArray jarr = arrayFrom(jo, key);
        if (jarr.size()!=2) {
            throw exception("Expected JSON array of length 2 for "+key+", but got length "+jarr.size());
        }
        int[] points = new int[2];
        for (int i = 0; i < 2; ++i) {
            points[i] = intFrom(jarr, i);
        }
        return points;
    }

    protected char charFrom(JsonObject jo, String key) throws IOException {
        String string = stringFrom(jo, key);
        if (string.length()!=1) {
            throw exception("Expected string of length 1 for key "+key+", but got a string of length "+string.length());
        }
        return string.charAt(0);
    }

    protected IOException exception(String message) {
        return new IOException(message);
    }

    protected IOException exception(String message, Exception cause) {
        return new IOException(message, cause);
    }
}
