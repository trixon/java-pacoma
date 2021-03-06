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
package se.trixon.pacoma;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.trixon.almond.util.AlmondUI;
import se.trixon.almond.util.Dict;
import se.trixon.pacoma.ui.MainFrame;

/**
 *
 * @author Patrik Karlsson
 */
public class Pacoma {

    private final AlmondUI mAlmondUI = AlmondUI.getInstance();
    private MainFrame mMainFrame = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Pacoma(args);

    }

    public Pacoma(String[] args) {
        mAlmondUI.installDarcula();
        mAlmondUI.initLookAndFeel();

        java.awt.EventQueue.invokeLater(() -> {
            mMainFrame = new MainFrame();
            mMainFrame.setVisible(true);
            if (args.length > 0) {
                File file = new File(args[0]);
                if (file.isFile()) {
                    try {
                        mMainFrame.open(file);
                    } catch (IOException ex) {
                        Logger.getLogger(Pacoma.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.err.println(String.format(Dict.Dialog.MESSAGE_FILE_NOT_FOUND.toString(), args[0]));
                }
            }
        });
    }
}
