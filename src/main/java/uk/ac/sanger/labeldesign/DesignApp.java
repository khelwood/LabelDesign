package uk.ac.sanger.labeldesign;

import uk.ac.sanger.labeldesign.component.DesignAction;
import uk.ac.sanger.labeldesign.component.DesignFrame;
import uk.ac.sanger.labeldesign.component.dialog.DesignPropertiesDialogPane;
import uk.ac.sanger.labeldesign.component.dialog.StringFieldPropertiesDialogPane;
import uk.ac.sanger.labeldesign.model.Design;
import uk.ac.sanger.labeldesign.model.StringField;
import uk.ac.sanger.labeldesign.view.RenderFactory;
import uk.ac.sanger.labeldesign.view.implementation.RenderFactoryImp;

import javax.json.JsonValue;
import javax.swing.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author dr6
 */
public class DesignApp implements Runnable {
    private RenderFactory renderFactory;
    private DesignFrame frame;
    private Map<OperationEnum, DesignAction> actions;

    @Override
    public void run() {
        renderFactory = new RenderFactoryImp();
        createFrame();
        createActions();
        frame.setJMenuBar(createMenuBar());

        frame.setVisible(true);
        Design design = new Design();
        design.setName("New design");
        design.setBounds(0, 600, 0, 300);
        StringField sf = new StringField();
        sf.setName("field1");
        sf.setPosition(100, 100);
        sf.setDisplayText("String field 1");
        sf.setFontCode('H');
        design.getStringFields().add(sf);
        sf = new StringField();
        sf.setName("field2");
        sf.setPosition(200, 200);
        sf.setDisplayText("String field 2");
        sf.setFontCode('C');
        design.getStringFields().add(sf);
        frame.setDesign(design);
    }

    private void createFrame() {
        frame = new DesignFrame(renderFactory);
        frame.setBounds(50, 50, 700, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public Design getDesign() {
        return frame.getDesign();
    }

    public void setDesign(Design design) {
        frame.setDesign(design);
    }

    private enum MenuGroup {
        File, Edit;

        public String getMenuName() {
            return this.name();
        }
    }

    private enum OperationEnum implements DesignAction.Operation {
        NEW_DESIGN(MenuGroup.File, "New design", DesignApp::newDesign),
        EXPORT_JSON(MenuGroup.File, "Export JSON", DesignApp::exportJson),

        ADD_STRING(MenuGroup.Edit, "Add string field", DesignApp::addStringField),
        ADD_BARCODE(MenuGroup.Edit, "Add barcode", DesignApp::addBarcodeField),
        ;

        private final MenuGroup menuGroup;
        private final String string;
        private final Consumer<DesignApp> function;

        OperationEnum(MenuGroup menuGroup, String string, Consumer<DesignApp> function) {
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

        public MenuGroup getMenuGroup() {
            return this.menuGroup;
        }
    }

    private void createActions() {
        actions = new EnumMap<>(OperationEnum.class);
        for (OperationEnum op : OperationEnum.values()) {
            actions.put(op, new DesignAction(this, op));
        }
    }

    private void newDesign() {
        DesignPropertiesDialogPane ndop = new DesignPropertiesDialogPane();
        if (ndop.showDialog("New design", frame)) {
            Design design = ndop.getNewDesign();
            frame.setTitle(design.getName());
            setDesign(design);
        }
    }

    private void exportJson() {
        Design design = getDesign();
        if (design!=null) {
            JsonConversion jcon = new JsonConversion();
            JsonValue value = jcon.toJson(design);
            jcon.getWriter(System.out).write(value);
        }
    }


    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        Map<DesignApp.MenuGroup, JMenu> menus = new EnumMap<>(DesignApp.MenuGroup.class);
        for (DesignApp.MenuGroup mg : DesignApp.MenuGroup.values()) {
            JMenu menu = new JMenu(mg.getMenuName());
            menus.put(mg, menu);
            menuBar.add(menu);
        }
        for (DesignApp.OperationEnum op : DesignApp.OperationEnum.values()) {
            if (op.getMenuGroup()!=null) {
                menus.get(op.getMenuGroup()).add(actions.get(op));
            }
        }
        return menuBar;
    }

    private void repaintDesign() {
        frame.repaintDesign();
    }

    private void addStringField() {
        Design design = getDesign();
        if (design==null) {
            return;
        }
        StringFieldPropertiesDialogPane dp = new StringFieldPropertiesDialogPane(design, renderFactory);
        if (dp.showDialog("New string field", frame)) {
            StringField sf = dp.makeStringField();
            design.getStringFields().add(sf);
            repaintDesign();
        }
    }

    private void addBarcodeField() {

    }
}
