package uk.ac.sanger.labeldesign;

import uk.ac.sanger.labeldesign.component.DesignAction;
import uk.ac.sanger.labeldesign.component.DesignFrame;
import uk.ac.sanger.labeldesign.component.dialog.DesignPropertiesDialogPane;
import uk.ac.sanger.labeldesign.component.dialog.StringFieldPropertiesDialogPane;
import uk.ac.sanger.labeldesign.conversion.*;
import uk.ac.sanger.labeldesign.model.Design;
import uk.ac.sanger.labeldesign.model.StringField;
import uk.ac.sanger.labeldesign.view.RenderFactory;
import uk.ac.sanger.labeldesign.view.implementation.RenderFactoryImp;

import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.swing.*;
import java.awt.FileDialog;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author dr6
 */
public class DesignApp implements Runnable {
    private static final String DESIGN_EXTENSION = ".lbld";
    private static final String JSON_EXTENSION = ".json";

    private RenderFactory renderFactory;
    private DesignFrame frame;
    private Map<OperationEnum, DesignAction> actions;
    private Path filePath;

    @Override
    public void run() {
        renderFactory = new RenderFactoryImp();
        createFrame();
        createActions();
        frame.setJMenuBar(createMenuBar());

        frame.setVisible(true);

        try {
            Design design = new DesignReader().readDesign(Paths.get("/Users/dr6/Desktop/untitled.lbld"));
            frame.setDesign(design);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        LOAD_DESIGN(MenuGroup.File, "Open design", DesignApp::loadDesign),
        SAVE_DESIGN(MenuGroup.File, "Save design", DesignApp::saveDesign),
        SAVE_AS(MenuGroup.File, "Save design as", DesignApp::saveDesignAs),
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

    private void saveDesign() {
        if (filePath!=null) {
            saveDesign(filePath);
        } else {
            saveDesignAs();
        }
    }

    private void saveDesignAs() {
        Path path = requestFilePath(filePath, FileDialog.SAVE, DESIGN_EXTENSION);
        if (path==null) {
            return;
        }
        saveDesign(path);
    }

    private void loadDesign() {
        Path path = requestFilePath(null, FileDialog.LOAD, DESIGN_EXTENSION);
        if (path==null) {
            return;
        }
        JsonInput jin = new DesignReader();
        Design design = load(path, jin);
        if (design!=null) {
            setDesign(design);
            this.filePath = path;
        }
    }

    private Design load(Path path, JsonInput reader) {
        try {
            JsonValue value = reader.readPath(path);
            return reader.toDesign(value);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "An error occurred trying to read the file.",
                    "File error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private boolean write(JsonValue json, Path path, JsonOutput jcon) {
        try (JsonWriter out = jcon.getWriter(Files.newBufferedWriter(path))) {
            out.write(json);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "An error occurred trying to write the file.",
                    "File error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void exportJson() {
        Design design = getDesign();
        if (design==null) {
            return;
        }
        Path path = requestFilePath(null, FileDialog.SAVE, JSON_EXTENSION);
        JsonExport jx = new JsonExport();
        JsonValue designJson = jx.toJson(design);
        write(designJson, path, jx);
    }

    private void saveDesign(Path path) {
        Design design = getDesign();
        if (design==null) {
            return;
        }
        DesignWriter dw = new DesignWriter();
        JsonValue designJson = dw.toJson(design);
        if (write(designJson, path, dw)) {
            this.filePath = path;
        }
    }

    private static boolean endsWithIgnoreCase(String string, String end) {
        if (string==null || end==null || string.length() < end.length()) {
            return false;
        }
        return string.regionMatches(true, string.length()-end.length(), end, 0, end.length());
    }

    private static FilenameFilter getFilter(final String extension) {
        return (dir, name) -> endsWithIgnoreCase(name, extension);
    }

    private Path requestFilePath(Path path, int mode, String extension) {
        FileDialog fd = new FileDialog(frame, mode==FileDialog.SAVE ? "Save design" : "Load design", mode);
        if (path!=null) {
            fd.setDirectory(filePath.getParent().toAbsolutePath().toString());
            fd.setFile(filePath.getFileName().toString());
        } else {
            fd.setFile("Label"+extension);
        }
        fd.setFilenameFilter(getFilter(extension));
        fd.setVisible(true);
        String filename = fd.getFile();
        if (filename==null) {
            return null;
        }
        if (!filename.endsWith(extension) && mode==FileDialog.SAVE) {
            filename += extension;
            path = Paths.get(fd.getDirectory(), filename);
            if (Files.exists(path)) {
                int confirm = JOptionPane.showConfirmDialog(frame,
                        String.format("The file %s already exists. Overwrite it?", path),
                        "Overwrite", JOptionPane.OK_CANCEL_OPTION);
                if (confirm!=JOptionPane.OK_OPTION) {
                    return null;
                }
            }
            return path;
        }
        return Paths.get(fd.getDirectory(), fd.getFile());
    }

    private void addBarcodeField() {
        // TODO
    }
}
