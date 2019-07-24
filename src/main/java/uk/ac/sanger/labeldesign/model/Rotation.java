package uk.ac.sanger.labeldesign.model;

/**
 * Rotation by some multiple of 90&deg;.
 * These are the rotations supported for barcodes and string fields.
 * @author dr6
 */
public enum Rotation {
    NONE("Unrotated"),
    RIGHT("Rotated 90° anticlockwise"),
    INVERT("Rotated 180°"),
    LEFT("Rotated 270° anticlockwise"),;

    private final String desc;

    Rotation(String desc) {
        this.desc = desc;
    }

    /**
     * The integer representing this rotation in the printer language.
     * @return a number in the range 0 to 3
     */
    public int index() {
        return this.ordinal();
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.index(), this.desc);
    }

    /**
     * The angle of this rotation, anticlockwise in radians.
     * @return the {@link #index} of this rotation multiplied by <code>&pi;/2</code>.
     */
    public double angle() {
        return this.index() * Math.PI / 2;
    }

    /**
     * The angle of this rotation, clockwise in degrees. Note this is in the opposite direction to {@link #angle}.
     */
    public int degrees() {
        return (360 - 90*this.index()) % 360;
    }

    /**
     * Gets the rotation for the given index. Index should be between 0 and 3.
     * @param index the index of a rotation
     * @return the rotation for the given index
     * @exception IndexOutOfBoundsException if index is not between 0 and 3
     */
    public static Rotation fromIndex(int index) {
        if (index < 0 || index >= 4) {
            throw new IndexOutOfBoundsException("No rotation has index " + index);
        }
        return Rotation.values()[index];
    }
}
