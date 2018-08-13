package uk.ac.sanger.labeldesign.model;

/**
 * @author dr6
 */
public class BarcodeField extends DesignField {
    private char barcodeType;
    private int height;
    private int cellWidth;
    private int checkDigitType;

    public char getBarcodeType() {
        return this.barcodeType;
    }

    public void setBarcodeType(char barcodeType) {
        this.barcodeType = barcodeType;
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

    public int getCheckDigitType() {
        return this.checkDigitType;
    }

    public void setCheckDigitType(int checkDigitType) {
        this.checkDigitType = checkDigitType;
    }
}
