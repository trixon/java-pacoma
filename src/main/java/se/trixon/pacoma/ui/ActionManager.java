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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import se.trixon.almond.util.AlmondAction;
import se.trixon.almond.util.AlmondActionManager;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.icons.material.MaterialIcon;
import se.trixon.almond.util.swing.dialogs.MenuModePanel;

/**
 *
 * @author Patrik Karlsson
 */
public class ActionManager extends AlmondActionManager {

    private final HashSet<AppListener> mAppListeners = new HashSet<>();
    private final HashSet<ProfileListener> mProfileListeners = new HashSet<>();

    public static ActionManager getInstance() {
        return Holder.INSTANCE;
    }

    private ActionManager() {
    }

    public void addAppListener(AppListener appListener) {
        mAppListeners.add(appListener);
    }

    public void addProfileListener(ProfileListener profileListener) {
        mProfileListeners.add(profileListener);
    }

    public ActionManager init(ActionMap actionMap, InputMap inputMap) {
        mActionMap = actionMap;
        mInputMap = inputMap;
        AlmondAction action;
        KeyStroke keyStroke;
        int commandMask = SystemHelper.getCommandMask();

        initHelpAction("https://trixon.se/projects/pacoma/documentation/");

        if (mAlmondOptions.getMenuMode() == MenuModePanel.MenuMode.BUTTON) {
            //menu
            int menuKey = KeyEvent.VK_M;
            keyStroke = KeyStroke.getKeyStroke(menuKey, commandMask);
            action = new AlmondAction(Dict.MENU.toString()) {

                @Override
                public void actionPerformed(ActionEvent e) {
                    mAppListeners.forEach((appActionListener) -> {
                        try {
                            appActionListener.onMenu(e);
                        } catch (Exception exception) {
                        }
                    });
                }
            };

            initAction(action, MENU, keyStroke, MaterialIcon._Navigation.MENU, true);
        }

        //options
        keyStroke = KeyStroke.getKeyStroke(getOptionsKey(), commandMask);
        keyStroke = IS_MAC ? null : keyStroke;
        action = new AlmondAction(Dict.OPTIONS.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mAppListeners.forEach((appActionListener) -> {
                    try {
                        appActionListener.onOptions(e);
                    } catch (Exception exception) {
                    }
                });
            }

        };

        initAction(action, OPTIONS, keyStroke, MaterialIcon._Action.SETTINGS, true);

        //start
        keyStroke = null;
        action = new AlmondAction(Dict.START.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mAppListeners.forEach((appActionListener) -> {
                    try {
                        appActionListener.onStart(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, START, keyStroke, MaterialIcon._Av.PLAY_ARROW, false);

        //cancel
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        action = new AlmondAction(Dict.CANCEL.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mAppListeners.forEach((appActionListener) -> {
                    try {
                        appActionListener.onCancel(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, CANCEL, keyStroke, MaterialIcon._Content.CLEAR, false);

        //add
        keyStroke = null;
        action = new AlmondAction(Dict.ADD.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onSave(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, ADD, keyStroke, MaterialIcon._Content.ADD, true);

        //clone
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, commandMask + InputEvent.SHIFT_DOWN_MASK);
        action = new AlmondAction(Dict.CLONE.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onOpen(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, CLONE, keyStroke, MaterialIcon._Content.CONTENT_COPY, false);

        //edit
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_E, commandMask + InputEvent.SHIFT_DOWN_MASK);
        action = new AlmondAction(Dict.EDIT.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onEdit(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, RENAME, keyStroke, MaterialIcon._Editor.MODE_EDIT, false);

        //remove
        keyStroke = null;
        action = new AlmondAction(Dict.REMOVE.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onSaveAs(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, REMOVE, keyStroke, MaterialIcon._Content.REMOVE, false);

        //remove all
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_DOWN_MASK);
        action = new AlmondAction(Dict.REMOVE_ALL.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onNew(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, REMOVE_ALL, keyStroke, MaterialIcon._Content.CLEAR, false);

        //quit
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q, commandMask);
        action = new AlmondAction(Dict.QUIT.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mAppListeners.forEach((appActionListener) -> {
                    try {
                        appActionListener.onQuit(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, QUIT, keyStroke, MaterialIcon._Content.CLEAR, true);

        //new document
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, commandMask);
        action = new AlmondAction(Dict.NEW.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onNew(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, NEW, keyStroke, MaterialIcon._Content.CREATE, true);

        //open document
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, commandMask);
        action = new AlmondAction(Dict.OPEN.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onOpen(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, OPEN, keyStroke, MaterialIcon._File.FOLDER_OPEN, true);

        //save document
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, commandMask);
        action = new AlmondAction(Dict.SAVE.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onSave(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, SAVE, keyStroke, MaterialIcon._Content.SAVE, false);

        //save document as
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, commandMask + InputEvent.SHIFT_MASK);
        action = new AlmondAction(Dict.SAVE_AS.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onSaveAs(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, SAVE_AS, keyStroke, MaterialIcon._Content.SAVE, false);

        //properties
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_MASK);
        action = new AlmondAction(Dict.PROPERTIES.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onEdit(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, PROPERTIES, keyStroke, MaterialIcon._Action.DESCRIPTION, false);

        //close
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, commandMask);
        action = new AlmondAction(Dict.CLOSE.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onClose(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, CLOSE, keyStroke, MaterialIcon._Navigation.CLOSE, false);

        //add images
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_A, commandMask);
        action = new AlmondAction(Dict.ADD.toString()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                mProfileListeners.forEach((profileListener) -> {
                    try {
                        profileListener.onAdd(e);
                    } catch (Exception exception) {
                    }
                });
            }
        };

        initAction(action, ADD, keyStroke, MaterialIcon._Image.ADD_A_PHOTO, false);

        return this;
    }

    public interface AppListener {

        void onCancel(ActionEvent actionEvent);

        void onMenu(ActionEvent actionEvent);

        void onOptions(ActionEvent actionEvent);

        void onQuit(ActionEvent actionEvent);

        void onStart(ActionEvent actionEvent);
    }

    public interface ProfileListener {

        void onAdd(ActionEvent actionEvent);

        void onClose(ActionEvent actionEvent);

        void onEdit(ActionEvent actionEvent);

        void onNew(ActionEvent actionEvent);

        void onOpen(ActionEvent actionEvent);

        void onSave(ActionEvent actionEvent);

        void onSaveAs(ActionEvent actionEvent);

    }

    private static class Holder {

        private static final ActionManager INSTANCE = new ActionManager();
    }
}
