package uk.ac.sanger.labeldesign.component;

import uk.ac.sanger.labeldesign.component.dialog.PropertiesPane;
import uk.ac.sanger.labeldesign.model.Design;
import uk.ac.sanger.labeldesign.view.RenderFactory;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author dr6
 */
public class DesignFrame extends JFrame {
    private DesignPanel designPanel;
    private JScrollPane propertiesScrollPane;
    private JPanel buttonPanel;

    public DesignFrame(RenderFactory renderFactory) {
        designPanel = new DesignPanel(renderFactory);

        JScrollPane designScrollPane = new JScrollPane(designPanel);
        designPanel.setRequestFocusEnabled(true);
        JPanel centrePanel = new JPanel(new BorderLayout());

        buttonPanel = new JPanel();
        centrePanel.add(designScrollPane, BorderLayout.CENTER);
        centrePanel.add(buttonPanel, BorderLayout.SOUTH);

        propertiesScrollPane = new JScrollPane(new JPanel());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centrePanel, propertiesScrollPane);
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(1);

        setContentPane(splitPane);
        designPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                designPanel.repaint();
            }
        });
    }

    public JButton addActionButton(Action action) {
        JButton button = new JButton(action);
        buttonPanel.add(button);
        button.setRequestFocusEnabled(false);
        return button;
    }

    public Design getDesign() {
        return this.designPanel.getDesign();
    }

    public DesignPanel getDesignPanel() {
        return this.designPanel;
    }

    public void setPropertiesView(Component component) {
        propertiesScrollPane.setViewportView(component);
    }

    public void clearPropertiesView() {
        propertiesScrollPane.setViewportView(new JPanel());
    }

    public void setDesign(Design design) {
        this.designPanel.setDesign(design);
        if (design!=null && design.getName()!=null) {
            setTitle(design.getName());
        }
    }

    public void selectAll() {
        designPanel.selectAll();
    }

    public void repaintDesign() {
        designPanel.repaint();
    }

    public PropertiesPane getPropertiesView() {
        Component component = propertiesScrollPane.getViewport().getView();
        if (component instanceof PropertiesPane) {
            return (PropertiesPane) component;
        }
        return null;
    }
}
