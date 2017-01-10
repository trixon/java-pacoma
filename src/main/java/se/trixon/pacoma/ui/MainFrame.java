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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.SystemUtils;
import se.trixon.almond.util.AlmondAction;
import se.trixon.almond.util.AlmondOptions;
import se.trixon.almond.util.AlmondUI;
import se.trixon.almond.util.BundleHelper;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.icons.material.MaterialIcon;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.almond.util.swing.dialogs.Message;
import se.trixon.pacoma.Pacoma;

/**
 *
 * @author Patrik Karlsson
 */
public class MainFrame extends JFrame implements AlmondOptions.AlmondOptionsWatcher {

    private ActionManager mActionManager;
    private final AlmondUI mAlmondUI = AlmondUI.getInstance();
    private final ResourceBundle mBundle = BundleHelper.getBundle(Pacoma.class, "Bundle");
    private final ResourceBundle mBundleUI = BundleHelper.getBundle(MainFrame.class, "Bundle");
    private final LinkedList<AlmondAction> mBaseActions = new LinkedList<>();
    private final LinkedList<AlmondAction> mAllActions = new LinkedList<>();
    private final AlmondOptions mAlmondOptions = AlmondOptions.getInstance();

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        init();
    }

    @Override
    public void onAlmondOptions(AlmondOptions.AlmondOptionsEvent almondOptionsEvent) {
        switch (almondOptionsEvent) {
            case ICON_THEME:
                mAllActions.stream().forEach((almondAction) -> {
                    almondAction.updateIcon();
                });
                break;

            case LOOK_AND_FEEL:
                SwingUtilities.updateComponentTreeUI(this);
                SwingUtilities.updateComponentTreeUI(mPopupMenu);
                break;

            case MENU_ICONS:
                ActionMap actionMap = getRootPane().getActionMap();
                for (Object key : actionMap.allKeys()) {
                    Action action = actionMap.get(key);
                    Icon icon = null;
                    if (mAlmondOptions.isDisplayMenuIcons()) {
                        icon = (Icon) action.getValue(AlmondAction.ALMOND_SMALL_ICON_KEY);
                    }
                    action.putValue(Action.SMALL_ICON, icon);
                }
                break;

            default:
                throw new AssertionError();
        }
    }

    private void init() {
        String fileName = String.format("/%s/pacoma-icon.png", getClass().getPackage().getName().replace(".", "/"));
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(fileName));
        setIconImage(imageIcon.getImage());

        mAlmondUI.addOptionsWatcher(this);
        mAlmondUI.addWindowWatcher(this);
        mAlmondUI.initoptions();

        mActionManager = new ActionManager();
        mActionManager.initActions();

        mAlmondUI.addOptionsWatcher(this);
        mAlmondUI.addWindowWatcher(this);
        mAlmondUI.initoptions();

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

//        loadProfiles();
//        populateProfiles(null);
//        initListeners();
    }

    private void showOptions() {
        OptionsPanel optionsPanel = new OptionsPanel();
        SwingHelper.makeWindowResizable(optionsPanel);

        int retval = JOptionPane.showOptionDialog(this,
                optionsPanel,
                Dict.OPTIONS.toString(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);

        if (retval == JOptionPane.OK_OPTION) {
            optionsPanel.save();
        }
    }

    private void quit() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mPopupMenu = new javax.swing.JPopupMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        optionsMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        aboutMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        quitMenuItem = new javax.swing.JMenuItem();
        toolBar = new javax.swing.JToolBar();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        menuButton = new javax.swing.JButton();

        mPopupMenu.add(jSeparator1);
        mPopupMenu.add(optionsMenuItem);
        mPopupMenu.add(jSeparator2);
        mPopupMenu.add(aboutMenuItem);
        mPopupMenu.add(jSeparator6);
        mPopupMenu.add(quitMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("se/trixon/pacoma/ui/Bundle"); // NOI18N
        setTitle(bundle.getString("MainFrame.title")); // NOI18N

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
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
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu mPopupMenu;
    private javax.swing.JButton menuButton;
    private javax.swing.JMenuItem optionsMenuItem;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
    class ActionManager {

        static final String ABOUT = "about";
        static final String ADD = "add";
        static final String CLONE = "clone";
        static final String MENU = "menu";
        static final String OPTIONS = "options";
        static final String QUIT = "shutdownServerAndWindow";
        static final String REMOVE = "remove";
        static final String REMOVE_ALL = "remove_all";
        static final String RENAME = "rename";
        static final String START = "start";

        private ActionManager() {
            initActions();
        }

        Action getAction(String key) {
            return getRootPane().getActionMap().get(key);
        }

        private void initAction(AlmondAction action, String key, KeyStroke keyStroke, Enum iconEnum, boolean baseAction) {
            InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = getRootPane().getActionMap();

            action.putValue(Action.ACCELERATOR_KEY, keyStroke);
            action.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.NAME));
            action.putValue("hideActionText", true);
            action.setIconEnum(iconEnum);
            action.updateIcon();

            inputMap.put(keyStroke, key);
            actionMap.put(key, action);

            if (baseAction) {
                mBaseActions.add(action);
            }

            mAllActions.add(action);
        }

        private void initActions() {
            AlmondAction action;
            KeyStroke keyStroke;
            int commandMask = SystemHelper.getCommandMask();

            //menu
            int menuKey = KeyEvent.VK_M;
            keyStroke = KeyStroke.getKeyStroke(menuKey, commandMask);
            action = new AlmondAction(Dict.MENU.toString()) {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() != menuButton) {
                        menuButtonMousePressed(null);
                    }
                }
            };

            initAction(action, MENU, keyStroke, MaterialIcon._Navigation.MENU, true);
            menuButton.setAction(action);

            //options
            int optionsKey = SystemUtils.IS_OS_MAC ? KeyEvent.VK_COMMA : KeyEvent.VK_P;
            keyStroke = KeyStroke.getKeyStroke(optionsKey, commandMask);
            action = new AlmondAction(Dict.OPTIONS.toString()) {

                @Override
                public void actionPerformed(ActionEvent e) {
                    showOptions();
                }
            };

            initAction(action, OPTIONS, keyStroke, MaterialIcon._Action.SETTINGS, true);
            optionsMenuItem.setAction(action);

            //about
            keyStroke = null;
            action = new AlmondAction(Dict.ABOUT.toString()) {

                @Override
                public void actionPerformed(ActionEvent e) {
                    String versionInfo = String.format(mBundle.getString("version_info"), SystemHelper.getJarVersion(Pacoma.class));
                    Message.information(MainFrame.this, Dict.ABOUT.toString(), versionInfo);
                }
            };

            initAction(action, ABOUT, keyStroke, null, true);
            aboutMenuItem.setAction(action);

            //quit
            keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q, commandMask);
            action = new AlmondAction(Dict.QUIT.toString()) {

                @Override
                public void actionPerformed(ActionEvent e) {
                    quit();
                }
            };

            initAction(action, QUIT, keyStroke, MaterialIcon._Content.CLEAR, true);
            quitMenuItem.setAction(action);

            for (Component component : mPopupMenu.getComponents()) {
                if (component instanceof AbstractButton) {
                    ((AbstractButton) component).setToolTipText(null);
                }
            }

            for (Component component : toolBar.getComponents()) {
                if (component instanceof AbstractButton) {
                    ((AbstractButton) component).setText(null);
                }
            }
        }
    }
}
