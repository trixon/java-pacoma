/*
 * Copyright 2017 Patrik Karlsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.pacoma.ui;

import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import se.trixon.almond.util.AlmondOptions;
import se.trixon.almond.util.AlmondOptionsPanel;
import se.trixon.almond.util.AlmondUI;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.PomInfo;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.almond.util.swing.dialogs.MenuModePanel;
import se.trixon.almond.util.swing.dialogs.SimpleDialog;
import se.trixon.almond.util.swing.dialogs.about.AboutModel;
import se.trixon.almond.util.swing.dialogs.about.AboutPanel;
import se.trixon.pacoma.Pacoma;
import se.trixon.pacoma.collage.Collage;

/**
 *
 * @author Patrik Karlsson
 */
public class MainFrame extends JFrame {

    private static final boolean IS_MAC = SystemUtils.IS_OS_MAC;

    private ActionManager mActionManager;
    private final AlmondUI mAlmondUI = AlmondUI.getInstance();
    private final ResourceBundle mBundle = SystemHelper.getBundle(Pacoma.class, "Bundle");
    private final ResourceBundle mBundleUI = SystemHelper.getBundle(MainFrame.class, "Bundle");
    private final AlmondOptions mAlmondOptions = AlmondOptions.getInstance();
    private static int sDocumentCounter = 0;
    private Collage mCollage = null;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();

        mAlmondUI.addWindowWatcher(this);
        mAlmondUI.initoptions();

        initActions();
        init();

        if (IS_MAC) {
            initMac();
        }

        initMenus();
    }

    public void setTile(Collage collage) {
        setTitle(String.format("%s — pacoma", collage.getName()));
    }

    private void addImages() {
        SimpleDialog.setParent(this);
        SimpleDialog.clearFilters();
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("Image file (*.jpg, *.png)", "jpg", "png");
        SimpleDialog.addFilter(fileNameExtensionFilter);
        SimpleDialog.setFilter(fileNameExtensionFilter);

        if (SimpleDialog.openFile(true)) {
            for (File file : SimpleDialog.getPaths()) {
                System.out.println(file.getAbsolutePath());
            }
        }
    }

    private void editCollage(Collage collage) {
        String title = "Edit properties";
        boolean existing = true;
        if (collage == null) {
            collage = new Collage();
            title = "Create a new collage";
            existing = false;
        }

        PropertiesPanel propertiesPanel = new PropertiesPanel(collage);
        SwingHelper.makeWindowResizable(propertiesPanel);

        int result = JOptionPane.showOptionDialog(this,
                propertiesPanel,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);

        if (result == JOptionPane.YES_OPTION) {
            propertiesPanel.store();
            mCollage = collage;

            if (!existing) {
                enableActions(true);
                mCollage.setName(String.format("%s %d", Dict.UNTITLED.toString(), ++sDocumentCounter));
                setTile(mCollage);
            }
        }
    }

    private void enableActions(boolean forceEnable) {
//        for (AlmondAction action : mAllActions) {
//            action.setEnabled(forceEnable || mBaseActions.contains(action));
//        }

    }

    private void init() {
        String fileName = String.format("/%s/pacoma-icon.png", getClass().getPackage().getName().replace(".", "/"));
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(fileName));
        setIconImage(imageIcon.getImage());

        initListeners();
    }

    private void initActions() {
        mActionManager = ActionManager.getInstance().init(getRootPane().getActionMap(), getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW));

        InputMap inputMap = mPopupMenu.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mPopupMenu.getActionMap();
        Action action = new AbstractAction("HideMenu") {

            @Override
            public void actionPerformed(ActionEvent e) {
                mPopupMenu.setVisible(false);
            }
        };

        String key = "HideMenu";
        actionMap.put(key, action);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        inputMap.put(keyStroke, key);

        //about
        PomInfo pomInfo = new PomInfo(Pacoma.class, "se.trixon", "pacoma");
        AboutModel aboutModel = new AboutModel(SystemHelper.getBundle(Pacoma.class, "about"), SystemHelper.getResourceAsImageIcon(MainFrame.class, "pacoma-icon.png"));
        aboutModel.setAppVersion(pomInfo.getVersion());
        AboutPanel aboutPanel = new AboutPanel(aboutModel);
        action = AboutPanel.getAction(MainFrame.this, aboutPanel);
        getRootPane().getActionMap().put(ActionManager.ABOUT, action);

        //File
        newButton.setAction(mActionManager.getAction(ActionManager.NEW));
        newMenuItem.setAction(mActionManager.getAction(ActionManager.NEW));

        openButton.setAction(mActionManager.getAction(ActionManager.OPEN));
        openMenuItem.setAction(mActionManager.getAction(ActionManager.OPEN));

        closeButton.setAction(mActionManager.getAction(ActionManager.CLOSE));
        closeMenuItem.setAction(mActionManager.getAction(ActionManager.CLOSE));

        saveButton.setAction(mActionManager.getAction(ActionManager.SAVE));
        saveMenuItem.setAction(mActionManager.getAction(ActionManager.SAVE));

        saveAsButton.setAction(mActionManager.getAction(ActionManager.SAVE_AS));
        saveAsMenuItem.setAction(mActionManager.getAction(ActionManager.SAVE_AS));

        propertiesMenuItem.setAction(mActionManager.getAction(ActionManager.PROPERTIES));
        propertiesButton.setAction(mActionManager.getAction(ActionManager.PROPERTIES));

        quitMenuItem.setAction(mActionManager.getAction(ActionManager.QUIT));

        //Tools
        optionsMenuItem.setAction(mActionManager.getAction(ActionManager.OPTIONS));

        //Help
        helpMenuItem.setAction(mActionManager.getAction(ActionManager.HELP));
        aboutMenuItem.setAction(mActionManager.getAction(ActionManager.ABOUT));

        //Toolbar
        addButton.setAction(mActionManager.getAction(ActionManager.ADD));

//        startButton.setAction(mActionManager.getAction(ActionManager.START));
//        cancelButton.setAction(mActionManager.getAction(ActionManager.CANCEL));
        menuButton.setAction(mActionManager.getAction(ActionManager.MENU));

        SwingHelper.clearTextButtons(menuButton);
    }

    private void initListeners() {
        mActionManager.addAppListener(new ActionManager.AppListener() {
            @Override
            public void onCancel(ActionEvent actionEvent) {
            }

            @Override
            public void onMenu(ActionEvent actionEvent) {
                if (actionEvent.getSource() != menuButton) {
                    menuButtonMousePressed(null);
                }
            }

            @Override
            public void onOptions(ActionEvent actionEvent) {
                showOptions();
            }

            @Override
            public void onQuit(ActionEvent actionEvent) {
                quit();
            }

            @Override
            public void onStart(ActionEvent actionEvent) {
            }
        });

        mActionManager.addProfileListener(new ActionManager.ProfileListener() {
            @Override
            public void onAdd(ActionEvent actionEvent) {
                addImages();
            }

            @Override
            public void onClose(ActionEvent actionEvent) {
                setTitle("pacoma");
                enableActions(false);
            }

            @Override
            public void onEdit(ActionEvent actionEvent) {
                editCollage(mCollage);
            }

            @Override
            public void onNew(ActionEvent actionEvent) {
                editCollage(null);
            }

            @Override
            public void onOpen(ActionEvent actionEvent) {
            }

            @Override
            public void onSave(ActionEvent actionEvent) {
                saveCollage(mCollage);
            }

            @Override
            public void onSaveAs(ActionEvent actionEvent) {
                saveCollage(mCollage);
            }
        });
    }

    private void initMac() {
        Application macApplication = Application.getApplication();
        macApplication.setAboutHandler((AppEvent.AboutEvent ae) -> {
            mActionManager.getAction(ActionManager.ABOUT).actionPerformed(null);
        });

        macApplication.setPreferencesHandler((AppEvent.PreferencesEvent pe) -> {
            mActionManager.getAction(ActionManager.OPTIONS).actionPerformed(null);
        });
    }

    private void initMenus() {
        if (mAlmondOptions.getMenuMode() == MenuModePanel.MenuMode.BUTTON) {
            mPopupMenu.add(newMenuItem);
            mPopupMenu.add(openMenuItem);
            mPopupMenu.add(closeMenuItem);
            mPopupMenu.add(new JSeparator());
            mPopupMenu.add(saveMenuItem);
            mPopupMenu.add(saveAsMenuItem);
            mPopupMenu.add(new JSeparator());
            mPopupMenu.add(propertiesMenuItem);
            mPopupMenu.add(new JSeparator());

            if (!IS_MAC) {
                mPopupMenu.add(optionsMenuItem);
                mPopupMenu.add(new JSeparator());
            }

            mPopupMenu.add(helpMenuItem);
            if (!IS_MAC) {
                mPopupMenu.add(aboutMenuItem);
            }

            if (!IS_MAC) {
                mPopupMenu.add(new JSeparator());
                mPopupMenu.add(quitMenuItem);
            }

        } else {
            setJMenuBar(menuBar);
            if (IS_MAC) {
                fileMenu.remove(quitMenuItem);
                toolsMenu.remove(optionsMenuItem);
                helpMenu.remove(aboutMenuItem);
            }

            fileMenu.setVisible(fileMenu.getMenuComponentCount() > 0 || !IS_MAC);
            toolsMenu.setVisible(toolsMenu.getMenuComponentCount() > 0 || !IS_MAC);
        }

        menuButton.setVisible(mAlmondOptions.getMenuMode() == MenuModePanel.MenuMode.BUTTON);
        SwingHelper.clearToolTipText(menuBar);
        SwingHelper.clearToolTipText(mPopupMenu);
    }

    private void saveCollage(Collage collage) {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Pacoma Collage (*.collage)", "collage");

        SimpleDialog.clearFilters();
        SimpleDialog.addFilter(filter);
        SimpleDialog.setFilter(filter);
        SimpleDialog.setParent(this);

        File destination = collage.getFile();
        if (destination == null) {
            SimpleDialog.setPath(FileUtils.getUserDirectory());
        } else {
            SimpleDialog.setPath(destination.getParentFile());
            SimpleDialog.setSelectedFile(new File(""));
        }

        if (SimpleDialog.saveFile(new String[]{"collage"})) {
            destination = SimpleDialog.getPath();
            collage.save(destination);
            collage.setName(FilenameUtils.getBaseName(destination.getAbsolutePath()));
            setTile(collage);
        }
    }

    private void showOptions() {
        OptionsPanel optionsPanel = new OptionsPanel();
        SwingHelper.makeWindowResizable(optionsPanel);

        Object[] options = new Object[]{AlmondOptionsPanel.getGlobalOptionsButton(optionsPanel), new JSeparator(), Dict.CANCEL, Dict.OK};
        int retval = JOptionPane.showOptionDialog(this,
                optionsPanel,
                Dict.OPTIONS.toString(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                Dict.OK);

        if (retval == Arrays.asList(options).indexOf(Dict.OK)) {
            optionsPanel.save();
        }
    }

    private void quit() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mPopupMenu = new javax.swing.JPopupMenu();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        closeMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        propertiesMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        quitMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        optionsMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();
        toolBar = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        saveButton = new javax.swing.JButton();
        saveAsButton = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        closeButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        propertiesButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        menuButton = new javax.swing.JButton();
        canvasPanel2 = new se.trixon.pacoma.ui.CanvasPanel();

        fileMenu.setText(Dict.FILE_MENU.toString());
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(closeMenuItem);
        fileMenu.add(jSeparator2);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(jSeparator1);
        fileMenu.add(propertiesMenuItem);
        fileMenu.add(jSeparator6);
        fileMenu.add(quitMenuItem);

        menuBar.add(fileMenu);

        toolsMenu.setText(Dict.TOOLS.toString());
        toolsMenu.add(optionsMenuItem);

        menuBar.add(toolsMenu);

        helpMenu.setText(Dict.HELP.toString());
        helpMenu.add(helpMenuItem);
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("se/trixon/pacoma/ui/Bundle"); // NOI18N
        setTitle(bundle.getString("MainFrame.title")); // NOI18N

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        newButton.setFocusable(false);
        toolBar.add(newButton);

        openButton.setFocusable(false);
        toolBar.add(openButton);
        toolBar.add(jSeparator4);

        saveButton.setFocusable(false);
        toolBar.add(saveButton);

        saveAsButton.setFocusable(false);
        toolBar.add(saveAsButton);
        toolBar.add(jSeparator5);

        closeButton.setFocusable(false);
        toolBar.add(closeButton);
        toolBar.add(jSeparator3);

        propertiesButton.setFocusable(false);
        toolBar.add(propertiesButton);

        addButton.setFocusable(false);
        toolBar.add(addButton);
        toolBar.add(filler1);

        menuButton.setFocusable(false);
        menuButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        menuButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                menuButtonMousePressed(evt);
            }
        });
        toolBar.add(menuButton);

        getContentPane().add(toolBar, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(canvasPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuButtonMousePressed
        if (evt == null || evt.getButton() == MouseEvent.BUTTON1) {
            if (mPopupMenu.isVisible()) {
                mPopupMenu.setVisible(false);
            } else {
                mPopupMenu.show(menuButton, menuButton.getWidth() - mPopupMenu.getWidth(), mPopupMenu.getHeight());

                int x = menuButton.getLocationOnScreen().x + menuButton.getWidth() - mPopupMenu.getWidth();
                int y = menuButton.getLocationOnScreen().y + menuButton.getHeight();

                mPopupMenu.setLocation(x, y);
            }
        }
    }//GEN-LAST:event_menuButtonMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton addButton;
    private se.trixon.pacoma.ui.CanvasPanel canvasPanel2;
    private javax.swing.JButton closeButton;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu mPopupMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton menuButton;
    private javax.swing.JButton newButton;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JButton openButton;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem optionsMenuItem;
    private javax.swing.JButton propertiesButton;
    private javax.swing.JMenuItem propertiesMenuItem;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JButton saveButton;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenu toolsMenu;
    // End of variables declaration//GEN-END:variables
}
