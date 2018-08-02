package uk.ac.sanger.labeldesign.view;

import java.awt.Rectangle;

/**
 * A object to render a particular type of item
 * @param <T> the type of item that can be rendered
 */
public interface Render<T> {
    /**
     * Renders the given item with the given graphics context
     * @param draw the drawing context
     * @param item the item to render
     * @return the bounds of the object drawn, if appropriate
     */
    Rectangle render(Draw draw, T item);
}
