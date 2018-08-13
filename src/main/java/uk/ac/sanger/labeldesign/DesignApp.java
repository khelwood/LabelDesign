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
import java.util.*;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * @author dr6
 */
public class DesignApp implements Runnable {
    enum MenuGroup {
        File, Edit;

        public String getMenuName() {
            return this.name();
        }
    }

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

        Design design = new Design();
        design.setBounds(0, 0, 500, 300);
        design.setName("New label");
        frame.setDesign(design);
        editLabel();
    }

    private void createFrame() {
        frame = new DesignFrame(renderFactory);
        frame.getDesignPanel().addMouseControl(new MouseControl(this));
        frame.setBounds(50, 50, 950, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public Action getAction(OperationEnum operation) {
        return actions.get(operation);
    }

    public Design getDesign() {
        return frame.getDesign();
    }

    public DesignSelection getDesignSelection() {
        return getDesignPanel().getDesignSelection();
    }

    public boolean toggleSelection(DesignField field) {
        boolean f = getDesignSelection().toggle(field);
        boopSelection(f);
        repaintDesign();
        return f;
    }

    public boolean isSelected(DesignField field) {
        return getDesignSelection().contains(field);
    }

    public void select(DesignField field) {
        getDesignSelection().add(field);
        boopSelection(true);
        repaintDesign();
    }

    public void setSelectionRect(int x0, int y0, int x1, int y1) {
        getDesignPanel().setSelectionRect(x0, y0, x1, y1);
    }

    public void clearSelectionRect() {
        if (getDesignSelection().finishRect()) {
            boopSelection(true);
        }
        repaintDesign();
    }

    public DesignField getFieldAt(int x, int y) {
        return getDesignPanel().getFieldAt(x, y);
    }

    private void boopSelection(boolean on) {
        DesignField df = on ? getDesignSelection().getSingleSelected() : null;
        if (df!=null) {
            openProperties(df);
        } else if (editingField!=null && !getDesignSelection().contains(editingField)) {
            closeProperties();
        }
    }

    public DesignPanel getDesignPanel() {
        return frame.getDesignPanel();
    }

    public void setDesign(Design design) {
        frame.setDesign(design);
    }

    private void createActions() {
        actions = new EnumMap<>(OperationEnum.class);
        for (OperationEnum op : OperationEnum.values()) {
            actions.put(op, new DesignAction(this, op));
        }

        getDesignPanel().addKeyControl(new KeyControl(this));
    }

    void newDesign() {
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
        Map<DesignApp.MenuGroup, Integer> menuGroupIndex = new EnumMap<>(MenuGroup.class);
        for (OperationEnum op : OperationEnum.values()) {
            MenuGroup menuGroup = op.getMenuGroup();
            if (menuGroup==null) {
                continue;
            }
            JMenu menu = menus.get(menuGroup);
            Integer curGroup = menuGroupIndex.get(menuGroup);
            if (curGroup==null) {
                menuGroupIndex.put(menuGroup, op.getGroupIndex());
            } else if (curGroup != op.getGroupIndex()) {
                menuGroupIndex.put(menuGroup, op.getGroupIndex());
                menu.addSeparator();
            }
            menu.add(actions.get(op));
        }
        return menuBar;
    }

    private void repaintDesign() {
        frame.repaintDesign();
    }

    void addStringField() {
        Design design = getDesign();
        if (design==null) {
            return;
        }
        StringFieldPropertiesPane propPane = new StringFieldPropertiesPane(design, renderFactory);
        propPane.loadStringField(null);
        final StringField sf = new StringField();
        propPane.updateStringField(sf);
        design.getStringFields().add(sf);
        repaintDesign();
        installPane(propPane, sf);
    }

    public void drag(int dx, int dy) {
        if (getDesignPanel().drag(dx, dy) && editingField!=null) {
            PropertiesPane propPane = frame.getPropertiesView();
            if (propPane!=null) {
                propPane.dragged(editingField);
            }
            repaintDesign();
        }
    }

    void saveDesign() {
        if (filePath!=null) {
            saveDesign(filePath);
        } else {
            saveDesignAs();
        }
    }

    void saveDesignAs() {
        Path path = requestFilePath(filePath, FileDialog.SAVE, DESIGN_EXTENSION);
        if (path==null) {
            return;
        }
        saveDesign(path);
    }

    void loadDesign() {
        Path path = requestFilePath(null, FileDialog.LOAD, DESIGN_EXTENSION);
        if (path==null) {
            return;
        }
        JsonInput jin = new DesignReader();
        Design design = load(path, jin);
        if (design!=null) {
            setDesign(design);
            this.filePath = path;
            editLabel();
        }
    }

    void importJson() {
        Path path = requestFilePath(null, FileDialog.LOAD, JSON_EXTENSION);
        if (path==null) {
            return;
        }
        JsonImport jin = new JsonImport();
        Design design = load(path, jin);
        if (design!=null) {
            setDesign(design);
            getDesignPanel().adjustDesignBounds();
            repaintDesign();
            Collection<String> warnings = jin.getWarnings();
            String message = "The label bounds are not part of the JSON import.";
            if (!warnings.isEmpty()) {
                message = assembleMessage("The JSON was imported with the following warnings:",
                        warnings, message);
            }
            JOptionPane.showMessageDialog(frame, message, "Imported", JOptionPane.INFORMATION_MESSAGE);
            editLabel();
        }
    }

    private static String assembleMessage(String before, Collection<String> items, String after) {
        StringBuilder sb = new StringBuilder("<html>");
        sb.append(escapeHtml4(before));
        sb.append("<ul>");
        for (String item : items) {
            sb.append("<li>");
            sb.append(escapeHtml4(item));
        }
        sb.append("</ul>");
        sb.append(escapeHtml4(after));
        sb.append("</html>");
        return sb.toString();
    }

    private Design load(Path path, JsonInput reader) {
        try {
            JsonValue value = reader.readPath(path);
            return reader.toDesign(value);
        } catch (Exception e) {
            e.printStackTrace();
            showError("File error", "An error occurred trying to read the file.", e);
            return null;
        }
    }

    private boolean write(JsonValue jsonValue, Path path, JsonOutput jcon) {
        try {
            jcon.write(jsonValue, path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError("File error", "An error occurred trying to write the file.", e);
            return false;
        }
    }

    private void showError(String title, String message, Exception exception) {
        if (exception!=null) {
            message = String.format("<html>%s<br>%s</html>", message, escapeHtml4(exception.getMessage()));
        }
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    void exportJson() {
        Design design = getDesign();
        if (design==null) {
            return;
        }
        Path path = requestFilePath(null, FileDialog.SAVE, JSON_EXTENSION);
        if (path==null) {
            return;
        }
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

    void selectAll() {
        frame.selectAll();
    }

    public void selectNone() {
        getDesignSelection().clear();
        boopSelection(false);
        repaintDesign();
    }

    public void deleteSelected() {
        Set<DesignField> toDelete = getDesignSelection().getSelected();
        if (toDelete.isEmpty()) {
            return;
        }
        Design design = getDesign();
        design.getBarcodeFields().removeIf(toDelete::contains);
        design.getStringFields().removeIf(toDelete::contains);
        selectNone();
    }

    void editLabel() {
        closeProperties();
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

    public void openProperties(DesignField field) {
        if (field==editingField) {
            return;
        }
        if (field instanceof StringField) {
            openProperties((StringField) field);
        } else if (field instanceof BarcodeField) {
            openProperties((BarcodeField) field);
        }
    }

    public void closeProperties() {
        editingField = null;
        frame.clearPropertiesView();
    }

    private void openProperties(final StringField field) {
        Design design = getDesign();
        if (design==null) {
            return;
        }
        StringFieldPropertiesPane propPane = new StringFieldPropertiesPane(design, renderFactory);
        propPane.loadStringField(field);
        installPane(propPane, field);
    }

    private void openProperties(final BarcodeField field) {
        Design design = getDesign();
        if (design==null) {
            return;
        }
        BarcodeFieldPropertiesPane propPane = new BarcodeFieldPropertiesPane(design);
        propPane.loadBarcodeField(field);
        installPane(propPane, field);
    }

    private void installPane(StringFieldPropertiesPane propPane, final StringField field) {
        this.editingField = field;
        frame.setPropertiesView(propPane);
        final Design design = getDesign();
        propPane.setChangeListener(e -> {
            propPane.updateStringField(field);
            repaintDesign();
        });
        propPane.setCloseAction(() -> {
            if (propPane.isCancelPressed()) {
                design.getStringFields().remove(field);
            } else {
                propPane.updateStringField(field);
            }
            repaintDesign();
            frame.clearPropertiesView();
            editingField = null;
        });
    }

    void addBarcodeField() {
        final Design design = getDesign();
        if (design==null) {
            return;
        }
        final BarcodeField bf = new BarcodeField();
        design.getBarcodeFields().add(bf);
        BarcodeFieldPropertiesPane propPane = new BarcodeFieldPropertiesPane(design);
        propPane.loadBarcodeField(null);
        propPane.updateBarcodeField(bf);
        repaintDesign();
        installPane(propPane, bf);
    }

    private void installPane(BarcodeFieldPropertiesPane propPane, final BarcodeField field) {
        this.editingField = field;
        frame.setPropertiesView(propPane);
        final Design design = getDesign();
        propPane.setChangeListener(e -> {
            propPane.updateBarcodeField(field);
            repaintDesign();
        });
        propPane.setCloseAction(() -> {
            if (propPane.isCancelPressed()) {
                design.getBarcodeFields().remove(field);
            } else {
                propPane.updateBarcodeField(field);
            }
            repaintDesign();
            frame.clearPropertiesView();
            editingField = null;
        });
    }
}
