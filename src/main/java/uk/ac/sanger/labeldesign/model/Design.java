package uk.ac.sanger.labeldesign.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the design of a label
 * @author dr6
 */
public class Design {
    private String name;
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;
    private int labelTypeId;
    private List<StringField> stringFields = new ArrayList<>();
    private List<BarcodeField> barcodeFields = new ArrayList<>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXMin() {
        return this.xMin;
    }

    public int getXMax() {
        return this.xMax;
    }

    public int getYMin() {
        return this.yMin;
    }

    public int getYMax() {
        return this.yMax;
    }

    public void setBounds(int xMin, int yMin, int xMax, int yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    public int getWidth() {
        return getXMax()-getXMin();
    }

    public int getHeight() {
        return getYMax()-getYMin();
    }

    public int getLabelTypeId() {
        return this.labelTypeId;
    }

    public void setLabelTypeId(int labelTypeId) {
        this.labelTypeId = labelTypeId;
    }

    public List<StringField> getStringFields() {
        return this.stringFields;
    }

    public List<BarcodeField> getBarcodeFields() {
        return this.barcodeFields;
    }
}
