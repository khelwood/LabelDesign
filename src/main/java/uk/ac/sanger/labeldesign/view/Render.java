package uk.ac.sanger.labeldesign.view;

/**
 * A object to render a particular type of item
 * @param <T> the type of item that can be rendered
 */
public interface Render<T> {
    /**
     * Renders the given item with the given graphics context
     * @param draw the drawing context
     * @param item the item to render
     */
    void render(Draw draw, T item);
}
