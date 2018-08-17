package uk.ac.sanger.labeldesign.conversion;

import uk.ac.sanger.labeldesign.model.*;

import javax.json.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Function;

/**
 * @author dr6
 */
public class JsonImport extends JsonInput {
    private Collection<String> warnings = new LinkedHashSet<>();

    @Override
    public Design toDesign(JsonValue jsonValue) throws IOException {
        Objects.requireNonNull(jsonValue, "Cannot JSON Import null");
        if (!(jsonValue instanceof JsonObject)) {
            throw exception("Expected a JSON object, but got "+jsonValue.getValueType());
        }
        Design design = new Design();
        JsonObject attributes = objectFrom(objectFrom(jsonValue.asJsonObject(), "data"), "attributes");
        design.setName(stringFrom(attributes, "name"));
        design.setLabelTypeId(intFrom(attributes, "label_type_id"));
        JsonArray labelsArr = arrayFrom(attributes, "labels_attributes");
        if (labelsArr.size()!=1) {
            throw exception("Expected labels_attributes to be an array of length 1, but it is of length "
                    +labelsArr.size());
        }
        JsonObject label = labelsArr.getJsonObject(0);
        String labelName = stringFrom(label, "name");
        if (!labelName.equals("label")) {
            warnings.add("This application expects the labels in labels_attributes to have the name \"label\"," +
                    "but this one has name \""+labelName+"\".");
        }
        JsonArray textFields = arrayFrom(label, "bitmaps_attributes");
        JsonArray barcodeFields = arrayFrom(label, "barcodes_attributes");
        List<StringField> sfs = design.getStringFields();
        try {
            if (sfs instanceof ArrayList) {
                ((ArrayList<?>) sfs).ensureCapacity(textFields.size());
            }
            textFields.stream().map(Unchecker.wrap(this::toStringField)).forEach(sfs::add);
            List<BarcodeField> bfs = design.getBarcodeFields();
            if (bfs instanceof ArrayList) {
                ((ArrayList<?>) bfs).ensureCapacity(barcodeFields.size());
            }
            barcodeFields.stream().map(Unchecker.wrap(this::toBarcodeField)).forEach(bfs::add);
        } catch (UncheckedIOException ue) {
            throw ue.getCause();
        }

        return design;
    }

    public Collection<String> getWarnings() {
        return this.warnings;
    }

    private StringField toStringField(JsonValue value) throws IOException {
        if (!(value instanceof JsonObject)) {
            throw exception("Expected a JSON object representing a string field, but got "+value.getValueType());
        }
        JsonObject jo = value.asJsonObject();
        StringField sf = new StringField();
        sf.setHorizontalMagnification(getMagnification(jo, "horizontal_magnification"));
        sf.setVerticalMagnification(getMagnification(jo, "vertical_magnification"));
        sf.setFontCode(charFrom(jo, "font"));
        sf.setSpacing(intFrom(jo, "space_adjustment"));
        sf.setPosition(intFrom(jo, "x_origin"), intFrom(jo, "y_origin"));
        sf.setRotation(rotationsFrom(jo));
        sf.setName(stringFrom(jo, "field_name"));
        sf.setDisplayText(sf.getName());
        Set<String> extraKeys = new HashSet<>(jo.keySet());
        for (String key : new String[] { "horizontal_magnification", "vertical_magnification",
                "font", "space_adjustment", "x_origin", "y_origin", "rotational_angles",
                "field_name" }) {
            extraKeys.remove(key);
        }
        if (!extraKeys.isEmpty()) {
            warnings.add("Ignoring extra fields in string field: "+extraKeys);
        }
        return sf;
    }

    private Rotation rotationsFrom(JsonObject jo) throws IOException {
        JsonValue jv = jo.get("rotational_angles");
        if (jv==null || jv.getValueType()==JsonValue.ValueType.NULL) {
            return Rotation.NONE;
        }
        if (jv.getValueType()==JsonValue.ValueType.NUMBER) {
            int n = ((JsonNumber) jv).intValue();
            if (n>10 && n<40 && n%11==0) {
                n /= 11;
            }
            if (n<0 || n>=4) {
                throw exception("rotational_angles should be a string, but it is the invalid number "+n);
            }
            warnings.add("rotational_angles should be a string, but it is given as a number: "+jv);
            return Rotation.fromIndex(n);
        }
        if (!(jv instanceof JsonString)) {
            throw exception("Expected rotational_angles to be a string, but it is "+jv.getValueType());
        }
        String value = ((JsonString) jv).getString();
        if (value.length()!=2) {
            throw exception("Expected rotational_angles to be a 2-digit string, but found \""+value+"\".");
        }
        int r = value.charAt(0)-'0';
        if (r<0 || r>=4) {
            throw exception("Valid characters in rotational_angles are 0 to 3, but found \""+value+"\".");
        }
        if (value.charAt(1)!=value.charAt(0)) {
            throw exception("This application currently only supports rotational_angles where the character rotation "+
                    "matches the string rotation. E.g. \"00\", \"11\". Found: \""+value+"\".");
        }
        return Rotation.fromIndex(r);
    }

    private Rotation rotationFrom(JsonObject jo, boolean twod) throws IOException {
        JsonValue jv = jo.get("rotational_angle");
        if (jv==null || jv.getValueType()==JsonValue.ValueType.NULL) {
            // PMB defaults to rotation 1 for 2D barcodes
            return twod ? Rotation.RIGHT : Rotation.NONE;
        }
        if (jv.getValueType()==JsonValue.ValueType.NUMBER) {
            int n = ((JsonNumber) jv).intValue();
            if (n<0 || n>=4) {
                throw exception("rotational_angle should be a string, but it is the invalid number "+n);
            }
            warnings.add("rotational_angle should be a string, but it is given as a number: "+jv);
            return Rotation.fromIndex(n);
        }
        if (!(jv instanceof JsonString)) {
            throw exception("Expected rotational_angle to be a string, but it is "+jv.getValueType());
        }
        String value = ((JsonString) jv).getString();
        if (value.length()!=1) {
            throw exception("Expected rotational_angle to be a 1-digit string, but found \""+value+"\".");
        }
        int r = value.charAt(0)-'0';
        if (r<0 || r>=4) {
            throw exception("Expected rotational_angle to be in the range \"0\" to \"3\", but found \""+value+"\".");
        }
        return Rotation.fromIndex(r);
    }

    private int getMagnification(JsonObject jo, String key) throws IOException {
        String value = stringFrom(jo, key);
        int n = 0;
        if (value.length()==1 || value.length()==2) {
            try {
                n = Integer.parseInt(value);
                if (n<=0 || n>=100) {
                    n = -1;
                } else if (value.length()==1) {
                    n *= 10;
                }
            } catch (NumberFormatException e) {
                n = -1;
            }
        }
        if (n <= 0) {
            throw exception(String.format("Expected a 1- or 2-digit positive number for key \"%s\", but found \"%s\".",
                    key, value));
        }
        if (n%5!=0) {
            throw exception(String.format("2-digit value for \"%s\" must be a multiple of 5, but found \"%s\".",
                    key, value));
        }
        return n;
    }

    private BarcodeField toBarcodeField(JsonValue value) throws IOException {
        if (!(value instanceof JsonObject)) {
            throw exception("Expected a JSON object representing a barcode field, but got "+value.getValueType());
        }
        JsonObject jo = value.asJsonObject();
        Set<String> otherKeys = new HashSet<>(jo.keySet());
        BarcodeField bf = new BarcodeField();
        bf.setTypeCode(charFrom(jo, "barcode_type"));
        otherKeys.remove("barcode_type");
        boolean twod = bf.is2D();
        bf.setRotation(rotationFrom(jo, twod));
        otherKeys.remove("rotational_angle");
        bf.setPosition(intFrom(jo, "x_origin"), intFrom(jo, "y_origin"));
        otherKeys.remove("x_origin");
        otherKeys.remove("y_origin");
        bf.setName(stringFrom(jo, "field_name"));
        otherKeys.remove("field_name");
        if (twod) {
            bf.setCellWidth(intFrom(jo, "one_cell_width"));
            otherKeys.remove("one_cell_width");
        } else {
            bf.setHeight(intFrom(jo, "height"));
            bf.setModuleWidth(intFrom(jo, "one_module_width"));
            bf.setCheckDigitType(intFrom(jo, "type_of_check_digit"));
            otherKeys.remove("height");
            otherKeys.remove("one_module_width");
            otherKeys.remove("type_of_check_digit");
        }
        if (!otherKeys.isEmpty()) {
            warnings.add("Ignoring extra fields for barcode of type "+bf.getTypeCode()+": "+otherKeys);
        }
        return bf;
    }

    @FunctionalInterface
    private interface Unchecker<U,T> {
        T apply(U arg) throws IOException;

        default T unchecked(U arg) throws UncheckedIOException {
            try {
                return this.apply(arg);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        static <U,T> Function<U,T> wrap(Unchecker<U,T> function) {
            return function::unchecked;
        }
    }

}
