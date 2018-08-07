package uk.ac.sanger.labeldesign.conversion;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.util.Collections;

/**
 * Tool to convert something to JSON.
 * @author dr6
 */
public abstract class JsonOutput {
    private JsonBuilderFactory jbf;
    private JsonWriterFactory jwf;

    public JsonOutput(JsonBuilderFactory jbf, JsonWriterFactory jwf) {
        this.jbf = jbf;
        this.jwf = jwf;
    }

    public JsonOutput() {
        this(Json.createBuilderFactory(null),
                Json.createWriterFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true)));
    }

    protected JsonBuilderFactory getBuilderFactory() {
        return this.jbf;
    }

    protected static String to2s(int n) {
        return String.format("%02d", n);
    }

    protected static String to4s(int n) {
        return String.format("%04d", n);
    }

    protected static String rep2(int n) {
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
        } catch (Exception e) {
            throw new JsonException("Error from writing JSON to string.", e);
        }
    }

}
