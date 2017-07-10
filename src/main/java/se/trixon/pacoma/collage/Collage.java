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
import com.google.gson.JsonSyntaxException;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Patrik Karlsson
 */
public class Collage {

    public static final String FILE_EXT = "collage";
    private static final int FILE_FORMAT_VERSION = 1;
    private static final Gson sGson = new Gson();
    private Color mBorderColor;
    private double mBorderThickness;
    private Date mDate;
    private transient boolean mDirty = false;
    private transient File mFile;
    private int mFileFormatVersion;
    private final ArrayList<File> mFileList = new ArrayList<>();
    private int mHeight = 2480;
    private String mName;
    private transient final HashSet<CollagePropertyChangeListener> mPropertyChangeListeners = new HashSet<>();
    private int mWidth = 3508;

    public static Collage open(File file) throws IOException, JsonSyntaxException {
        String json = FileUtils.readFileToString(file, Charset.defaultCharset());

        Collage collage = sGson.fromJson(json, Collage.class);
        collage.setFile(file);

        if (collage.mFileFormatVersion != FILE_FORMAT_VERSION) {
            //TODO Handle file format version change
        }

        System.out.println(json);

        return collage;
    }

    public Collage() {
        mBorderColor = Color.CYAN;
    }

    public void addFile(File file) {
        mFileList.add(file);
        setDirty(true);
    }

    public void addFiles(List<File> files) {
        mFileList.addAll(files);
        setDirty(true);
    }

    public void addPropertyChangeListener(CollagePropertyChangeListener propertyChangeListener) {
        mPropertyChangeListeners.add(propertyChangeListener);
    }

    public void clearFiles() {
        if (hasImages()) {
            mFileList.clear();
            setDirty(true);
        }
    }

    public Color getBorderColor() {
        return mBorderColor;
    }

    public double getBorderThickness() {
        return mBorderThickness;
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

    public boolean hasImages() {
        return !mFileList.isEmpty();
    }

    public boolean isDirty() {
        return mDirty;
    }

    public void removeFile(File file) {
        mFileList.remove(file);
        setDirty(true);
    }

    public void removePropertyChangeListener(CollagePropertyChangeListener propertyChangeListener) {
        mPropertyChangeListeners.remove(propertyChangeListener);
    }

    public void save(File file) throws IOException {
        mFile = file;
        setName(FilenameUtils.getBaseName(mFile.getAbsolutePath()));
        mFileFormatVersion = FILE_FORMAT_VERSION;
        mDate = new Date();
        FileUtils.writeStringToFile(mFile, sGson.toJson(this), Charset.defaultCharset());
        setDirty(false);
    }

    public void setBorderColor(Color borderColor) {
        Color oldValue = mBorderColor;
        if (oldValue != borderColor) {
            mBorderColor = borderColor;
            setDirtyOr(oldValue != borderColor);
        }
    }

    public void setBorderThickness(double borderThickness) {
        double oldValue = mBorderThickness;
        if (oldValue != borderThickness) {
            mBorderThickness = borderThickness;
            setDirtyOr(oldValue != borderThickness);
        }
    }

    public void setFile(File file) {
        mFile = file;
    }

    public void setHeight(int height) {
        int oldValue = mHeight;
        if (oldValue != height) {
            mHeight = height;
            setDirtyOr(oldValue != height);
        }
    }

    public void setName(String name) {
        mName = name;
    }

    public void setWidth(int width) {
        int oldValue = mWidth;
        if (oldValue != width) {
            mWidth = width;
            setDirtyOr(oldValue != width);
        }
    }

    private void notifyPropertyChangeListeners() {
        mPropertyChangeListeners.forEach((propertyChangeListener) -> {
            try {
                propertyChangeListener.onPropertyChanged();
            } catch (Exception e) {
                //
            }
        });
    }

    private void setDirty(boolean dirty) {
        mDirty = dirty;
        notifyPropertyChangeListeners();
    }

    private void setDirtyOr(boolean dirty) {
        mDirty = mDirty || dirty;

        if (dirty) {
            notifyPropertyChangeListeners();
        }
    }

    public interface CollagePropertyChangeListener {

        void onPropertyChanged();
    }
}
