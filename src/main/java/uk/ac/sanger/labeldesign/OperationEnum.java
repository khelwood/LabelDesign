package uk.ac.sanger.labeldesign;

import uk.ac.sanger.labeldesign.component.DesignAction;

import java.util.function.Consumer;

/**
 * @author dr6
 */
public enum OperationEnum implements DesignAction.Operation {
    NEW_DESIGN(DesignApp.MenuGroup.File, "New design", DesignApp::newDesign),
    LOAD_DESIGN(DesignApp.MenuGroup.File, "Open design", DesignApp::loadDesign),
    SAVE_DESIGN(DesignApp.MenuGroup.File, "Save design", DesignApp::saveDesign),
    SAVE_AS(DesignApp.MenuGroup.File, "Save design as", DesignApp::saveDesignAs),
    EXPORT_JSON(DesignApp.MenuGroup.File, "Export JSON", DesignApp::exportJson),

    SELECT_ALL(DesignApp.MenuGroup.Edit, "Select all", DesignApp::selectAll),
    SELECT_NONE(DesignApp.MenuGroup.Edit, "Select none", DesignApp::selectNone),
    DELETE_SELECTED(DesignApp.MenuGroup.Edit, "Delete selected", DesignApp::deleteSelected),
    ADD_STRING(DesignApp.MenuGroup.Edit, "Add string field", DesignApp::addStringField),
    ADD_BARCODE(DesignApp.MenuGroup.Edit, "Add barcode", DesignApp::addBarcodeField),
    EDIT_LABEL(DesignApp.MenuGroup.Edit, "Edit label properties", DesignApp::editLabel),;

    private final DesignApp.MenuGroup menuGroup;
    private final String string;
    private final Consumer<DesignApp> function;

    OperationEnum(DesignApp.MenuGroup menuGroup, String string, Consumer<DesignApp> function) {
        this.menuGroup = menuGroup;
        this.string = string;
        this.function = function;
    }

    @Override
    public String getActionName() {
        return this.string;
    }

    @Override
    public void perform(DesignApp app) {
        this.function.accept(app);
    }

    public DesignApp.MenuGroup getMenuGroup() {
        return this.menuGroup;
    }
}
