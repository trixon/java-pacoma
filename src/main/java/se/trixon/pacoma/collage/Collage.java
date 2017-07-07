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

import com.google.gson.Gson;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Patrik Karlsson
 */
public class Collage {

    private static final int FILE_FORMAT_VERSION = 1;
    private static final Gson sGson = new Gson();
    private double mBorderThickness;
    private Color mColor;
    private Date mDate;
    private transient boolean mDirty = false;
    private transient File mFile;
    private int mFileFormatVersion;
    private final ArrayList<File> mFileList = new ArrayList<>();
    private int mHeight = 2480;
    private String mName;
    private transient CollagePropertyChangeListener mPropertyChangeListener;
    private int mWidth = 3508;

    public static Collage open(File file) throws IOException {
        String json = FileUtils.readFileToString(file, Charset.defaultCharset());

        Collage collage = sGson.fromJson(json, Collage.class);
        collage.setFile(file);

        if (collage.mFileFormatVersion != FILE_FORMAT_VERSION) {
            //TODO Handle file format version change
        }

        return collage;
    }

    public Collage() {
        mColor = Color.BLACK;
    }

    public void addFile(File file) {
        mFileList.add(file);
        setDirty(true);
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

    public ArrayList<File> getFileList() {
        return mFileList;
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

    public boolean isDirty() {
        return mDirty;
    }

    public void removeFile(File file) {
        mFileList.remove(file);
        setDirty(true);
    }

    public void save(File file) throws IOException {
        mFile = file;
        setName(FilenameUtils.getBaseName(mFile.getAbsolutePath()));
        mFileFormatVersion = FILE_FORMAT_VERSION;
        mDate = new Date();
        FileUtils.writeStringToFile(mFile, sGson.toJson(this), Charset.defaultCharset());
        setDirty(false);
    }

    public void setBorderThickness(double borderThickness) {
        setDirtyOr(mBorderThickness != borderThickness);
        mBorderThickness = borderThickness;
    }

    public void setColor(Color color) {
        setDirtyOr(mColor != color);
        mColor = color;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public void setHeight(int height) {
        setDirtyOr(mHeight != height);
        mHeight = height;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPropertyChangeListener(CollagePropertyChangeListener propertyChangeListener) {
        mPropertyChangeListener = propertyChangeListener;
    }

    public void setWidth(int width) {
        setDirtyOr(mWidth != width);
        mWidth = width;
    }

    private void setDirty(boolean dirty) {
        mDirty = dirty;

        mPropertyChangeListener.onPropertyChanged();
    }

    private void setDirtyOr(boolean dirty) {
        mDirty = mDirty || dirty;

        if (dirty) {
            mPropertyChangeListener.onPropertyChanged();
        }
    }

    public interface CollagePropertyChangeListener {

        void onPropertyChanged();
    }
}
