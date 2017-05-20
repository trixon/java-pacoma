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
package se.trixon.pacoma.collage;

import java.awt.Color;
import java.io.File;

/**
 *
 * @author Patrik Karlsson
 */
public class Collage {

    private double mBorderThickness;
    private Color mColor;
    private File mFile;
    private int mHeight = 2480;
    private String mName;
    private int mWidth = 3508;

    public static Collage open(File file) {
        return null;
    }

    public Collage() {
    }

    public double getBorderThickness() {
        return mBorderThickness;
    }

    public Color getColor() {
        return mColor;
    }

    public File getFile() {
        return mFile;
    }

    public int getHeight() {
        return mHeight;
    }

    public String getName() {
        return mName;
    }

    public int getWidth() {
        return mWidth;
    }

    public void save(File file) {

    }

    public void setBorderThickness(double borderThickness) {
        mBorderThickness = borderThickness;
    }

    public void setColor(Color color) {
        mColor = color;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setWidth(int width) {
        mWidth = width;
    }
}
