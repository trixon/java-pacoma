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

import java.awt.Color;
import java.util.prefs.Preferences;
import se.trixon.almond.util.GraphicsHelper;

/**
 *
 * @author Patrik Karlsson
 */
public class Options {

    public static final String KEY_BACKGROUND_COLOR = "backgroundColor";
    public static final String KEY_CUSTOM_BACKGROUND = "customBackground";
    private static final String DEFAULT_COLOR_BACKGROUND = "#006699";
    private static final boolean DEFAULT_CUSTOM_BACKGROUND = false;
    private final Preferences mPreferences = Preferences.userNodeForPackage(Options.class);

    public static Options getInstance() {
        return Holder.INSTANCE;
    }

    private Options() {
    }

    public Color getBackgroundColor() {
        return Color.decode(mPreferences.get(KEY_BACKGROUND_COLOR, DEFAULT_COLOR_BACKGROUND));
    }

    public Preferences getPreferences() {
        return mPreferences;
    }

    public boolean isCustomBackground() {
        return mPreferences.getBoolean(KEY_CUSTOM_BACKGROUND, DEFAULT_CUSTOM_BACKGROUND);
    }

    public void setBackgroundColor(Color color) {
        mPreferences.put(KEY_BACKGROUND_COLOR, GraphicsHelper.colorToString(color));
    }

    public void setCustomBackground(boolean value) {
        mPreferences.putBoolean(KEY_CUSTOM_BACKGROUND, value);
    }

    private static class Holder {

        private static final Options INSTANCE = new Options();
    }
}
