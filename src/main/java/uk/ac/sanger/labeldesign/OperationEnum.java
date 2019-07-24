package uk.ac.sanger.labeldesign;

import uk.ac.sanger.labeldesign.DesignApp.MenuGroup;
import uk.ac.sanger.labeldesign.component.DesignAction;

import java.util.function.Consumer;

/**
 * @author dr6
 */
public enum OperationEnum implements DesignAction.Operation {
    NEW_DESIGN(MenuGroup.File, "New design", DesignApp::newDesign),
    LOAD_DESIGN(MenuGroup.File, "Open design", DesignApp::loadDesign),
    SAVE_DESIGN(MenuGroup.File, "Save design", DesignApp::saveDesign, 1),
    SAVE_AS(MenuGroup.File, "Save design as", DesignApp::saveDesignAs, 1),
    IMPORT_JSON(MenuGroup.File, "Import JSON", DesignApp::importJson, 2),
    EXPORT_JSON(MenuGroup.File, "Export JSON", DesignApp::exportJson, 2),
    EXPORT_CAB(MenuGroup.File, "Export CAB", DesignApp::exportCab, 3),

    SELECT_ALL(MenuGroup.Edit, "Select all", DesignApp::selectAll),
    SELECT_NONE(MenuGroup.Edit, "Select none", DesignApp::selectNone),
    DELETE_SELECTED(MenuGroup.Edit, "Delete selected", DesignApp::deleteSelected),
    EDIT_LABEL(MenuGroup.Edit, "Edit label properties", DesignApp::editLabel, 1),
    ADD_STRING(MenuGroup.Edit, "Add string field", DesignApp::addStringField, 2),
    ADD_BARCODE(MenuGroup.Edit, "Add barcode", DesignApp::addBarcodeField, 2),

    ;

    private final MenuGroup menuGroup;
    private final String actionName;
    private final Consumer<DesignApp> function;
    private final int groupIndex;

    OperationEnum(MenuGroup menuGroup, String actionName, Consumer<DesignApp> function, int groupIndex) {
        this.menuGroup = menuGroup;
        this.actionName = actionName;
        this.function = function;
        this.groupIndex = groupIndex;
    }

    OperationEnum(MenuGroup menuGroup, String actionName, Consumer<DesignApp> function) {
        this(menuGroup, actionName, function, 0);
    }

    @Override
    public String getActionName() {
        return this.actionName;
    }

    @Override
    public void perform(DesignApp app) {
        this.function.accept(app);
    }

    public MenuGroup getMenuGroup() {
        return this.menuGroup;
    }

    public int getGroupIndex() {
        return this.groupIndex;
    }
}
