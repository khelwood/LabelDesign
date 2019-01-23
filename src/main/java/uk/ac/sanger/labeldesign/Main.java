package uk.ac.sanger.labeldesign;

import javax.swing.SwingUtilities;

/**
 * @author dr6
 */
public class Main {
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "LabelDesign");
        SwingUtilities.invokeLater(new DesignApp());
    }
}
