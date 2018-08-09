package uk.ac.sanger.labeldesign;

import uk.ac.sanger.labeldesign.component.*;
import uk.ac.sanger.labeldesign.component.dialog.*;
import uk.ac.sanger.labeldesign.conversion.*;
import uk.ac.sanger.labeldesign.model.*;
import uk.ac.sanger.labeldesign.view.RenderFactory;
import uk.ac.sanger.labeldesign.view.implementation.RenderFactoryImp;

import javax.json.JsonValue;
import javax.swing.*;
import java.awt.FileDialog;
import java.io.FilenameFilter;
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
    private DesignField editingField;

    @Override
    public void run() {
        renderFactory = new RenderFactoryImp();
        createFrame();
        createActions();
        frame.setJMenuBar(createMenuBar());

        frame.setVisible(true);

        try {
            Design design = new DesignReader().readDesign(Paths.get(System.getProperty("user.home"),
                    "Desktop", "untitled"+DESIGN_EXTENSION));
            frame.setDesign(design);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFrame() {
        frame = new DesignFrame(renderFactory);
        frame.getDesignPanel().addMouseControl(new MouseControl(this));
        frame.setBounds(50, 50, 900, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public Design getDesign() {
        return frame.getDesign();
    }

    public DesignPanel getDesignPanel() {
        return frame.getDesignPanel();
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

        SELECT_ALL(MenuGroup.Edit, "Select all", DesignApp::selectAll),
        SELECT_NONE(MenuGroup.Edit, "Select none", DesignApp::selectNone),
        ADD_STRING(MenuGroup.Edit, "Add string field", DesignApp::addStringField),
        ADD_BARCODE(MenuGroup.Edit, "Add barcode", DesignApp::addBarcodeField),
        EDIT_LABEL(MenuGroup.Edit, "Edit label properties", DesignApp::editLabel),
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
        final DesignPropertiesPane ndop = new DesignPropertiesPane();
        ndop.loadDesign(null);
        ndop.setCloseAction(() -> {
            if (ndop.isOkPressed()) {
                Design design = new Design();
                ndop.updateDesign(design);
                setDesign(design);
                filePath = null;
            }
            frame.clearPropertiesView();
        });
        frame.setPropertiesView(ndop);
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
        StringFieldPropertiesPane propPane = new StringFieldPropertiesPane(design, renderFactory);
        propPane.loadStringField(null);
        frame.setPropertiesView(propPane);
        final StringField sf = new StringField();
        editingField = sf;
        propPane.updateStringField(sf);
        design.getStringFields().add(sf);
        repaintDesign();
        propPane.setChangeListener(e -> {
            propPane.updateStringField(sf);
            repaintDesign();
        });
        propPane.setCloseAction(() -> {
            if (propPane.isOkPressed()) {
                propPane.updateStringField(sf);
                repaintDesign();
            } else {
                design.getStringFields().remove(sf);
                repaintDesign();
            }
            frame.clearPropertiesView();
            editingField = null;
        });
    }

    public void fieldsDragged() {
        if (editingField!=null) {
            PropertiesPane propPane = frame.getPropertiesView();
            if (propPane!=null) {
                propPane.dragged(editingField);
            }
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

    private boolean write(JsonValue jsonValue, Path path, JsonOutput jcon) {
        try {
            jcon.write(jsonValue, path);
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
        return (string!=null && end!=null && string.length() >= end.length() &&
           string.regionMatches(true, string.length()-end.length(), end, 0, end.length()));
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

    private void selectAll() {
        frame.selectAll();
    }

    private void selectNone() {
        frame.deselect();
    }

    private void editLabel() {
        DesignPropertiesPane dp = new DesignPropertiesPane();
        dp.loadDesign(getDesign());
        frame.setPropertiesView(dp);
        dp.setCloseAction(() -> {
            if (dp.isOkPressed()) {
                Design design = getDesign();
                if (design!=null) {
                    dp.updateDesign(design);
                    frame.repaintDesign();
                }
            }
            frame.clearPropertiesView();
        });
        dp.setChangeListener(e -> {
            Design design = getDesign();
            if (design!=null) {
                dp.updateDesign(design);
                frame.repaintDesign();
                frame.setTitle(design.getName());
            }
        });
    }

    private void addBarcodeField() {
        // TODO
    }
}
