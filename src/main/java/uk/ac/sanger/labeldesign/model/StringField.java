package uk.ac.sanger.labeldesign.model;

/**
 * @author dr6
 */
public class StringField extends DesignField {
    private char fontCode = 'A';
    private String displayText = "Some text here";
    private int spacing = 0;
    private int horizontalMagnification = 5;
    private int verticalMagnification = 5;

    public char getFontCode() {
        return this.fontCode;
    }

    public void setFontCode(char fontCode) {
        this.fontCode = fontCode;
    }

    public String getDisplayText() {
        return this.displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public int getHorizontalMagnification() {
        return this.horizontalMagnification;
    }

    public void setHorizontalMagnification(int horizontalMagnification) {
        this.horizontalMagnification = validateMagnification(horizontalMagnification);
    }

    public int getVerticalMagnification() {
        return this.verticalMagnification;
    }

    public void setVerticalMagnification(int verticalMagnification) {
        this.verticalMagnification = validateMagnification(verticalMagnification);
    }

    public static int validateMagnification(int magnification) {
        if (magnification < 5 || magnification > 95) {
            throw new IllegalArgumentException(String.format("Magnification %s out of range (05 to 95)", magnification));
        }
        if (magnification%5!=0) {
            throw new IllegalArgumentException(String.format("Invalid magnification %s (should be multiple of 5)",
                    magnification));
        }
        return magnification;
    }

    public int getSpacing() {
        return this.spacing;
    }
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
}
