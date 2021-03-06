package uk.ac.sanger.labeldesign.model;

import java.util.Objects;

/**
 * @author dr6
 */
public abstract class DesignField {

    private String name;
    private int x,y;
    private Rotation rotation = Rotation.NONE;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = Objects.requireNonNull(rotation, "rotation is null");
    }
}
