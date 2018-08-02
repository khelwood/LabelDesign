package uk.ac.sanger.labeldesign.model;

/**
 * @author dr6
 */
public class StringField extends DesignField {
    private char fontCode = 'A';
    private String displayText = "Some text here";
    private int spacing = 0;

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

    public int getMagnification() {
        return 5;
    }

    public int getSpacing() {
        return this.spacing;
    }
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
}
