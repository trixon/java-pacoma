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

import java.io.File;

/**
 * Based on work by Adrien Vergé in https://github.com/adrienverge/PhotoCollage
 *
 * @author Patrik Karlsson
 */
public class Photo {

    private final File mFile;
    private final int mHeight;
    private final int mOrientation;
    private final int mWidth;

    public Photo(File file, int width, int height, int orientation) {
        mHeight = height;
        mWidth = width;
        mOrientation = orientation;
        mFile = file;
    }

    public File getFile() {
        return mFile;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public double getRatio() {
        return 1.0 * mHeight / mWidth;
    }

    public int getWidth() {
        return mWidth;
    }
}
/*
class Photo(object):
    def __init__(self, filename, w, h, orientation=0):
        self.filename = filename
        self.w = w
        self.h = h
        self.orientation = orientation

    @property
    def ratio(self):
        return float(self.h) / float(self.w)
 */
