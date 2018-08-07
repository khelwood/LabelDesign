package uk.ac.sanger.labeldesign.conversion;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public void write(JsonValue value, Path path) throws IOException {
        try (JsonWriter out = jwf.createWriter(Files.newBufferedWriter(path))) {
            out.write(value);
        }
    }
}
