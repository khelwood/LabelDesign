package uk.ac.sanger.labeldesign.component;

import uk.ac.sanger.labeldesign.DesignApp;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

/**
 * @author dr6
 */
public class DesignAction extends AbstractAction {
    public interface Operation {
        String getActionName();
        void perform(DesignApp app);
    }

    private DesignApp app;
    private Operation operation;

    public DesignAction(DesignApp app, Operation operation) {
        super(operation.getActionName());
        this.app = app;
        this.operation = operation;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        operation.perform(app);
    }

    public void checkEnabled() {
        setEnabled(app.isOperationEnabled(this.operation));
    }
}
