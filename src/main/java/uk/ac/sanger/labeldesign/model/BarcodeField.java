package uk.ac.sanger.labeldesign.model;

import java.util.Objects;

/**
 * @author dr6
 */
public class BarcodeField extends DesignField {
    public enum Type {
        DATAMATRIX("Q: Data matrix", true), EAN13("5: EAN13", false);

        private final String desc;
        private final boolean twod;

        Type(String desc, boolean twod) {
            this.desc = desc;
            this.twod = twod;
        }

        public String getDesc() {
            return this.desc;
        }

        @Override
        public String toString() {
            return getDesc();
        }

        public char getCode() {
            return getDesc().charAt(0);
        }

        public boolean is2D() {
            return this.twod;
        }

        public static Type fromCode(char ch) {
            switch (ch) {
                case '5': return EAN13;
                case 'Q': return DATAMATRIX;
                default: throw new IllegalArgumentException("Invalid type code: "+ch);
            }
        }
    }

    private Type type = Type.DATAMATRIX;
    private int height = 70;
    private int cellWidth = 4;
    private int moduleWidth = 1;
    private int checkDigitType = 2;

    public char getTypeCode() {
        return getType().getCode();
    }

    public void setTypeCode(char barcodeTypeCode) {
        this.type = Type.fromCode(barcodeTypeCode);
    }

    public void setType(Type type) {
        this.type = Objects.requireNonNull(type, "Barcode type is null");
    }

    public Type getType() {
        return this.type;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCellWidth() {
        return this.cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getModuleWidth() {
        return this.moduleWidth;
    }

    public void setModuleWidth(int moduleWidth) {
        this.moduleWidth = moduleWidth;
    }

    public int getCheckDigitType() {
        return this.checkDigitType;
    }

    public void setCheckDigitType(int checkDigitType) {
        this.checkDigitType = checkDigitType;
    }

    public boolean is2D() {
        return this.type.is2D();
    }
    public boolean is1D() {
        return !this.type.is2D();
    }
}
