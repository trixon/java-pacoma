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

import java.awt.Rectangle;
import java.util.LinkedList;

/**
 * Represents a cell in a column
 *
 * Based on work by Adrien Vergé in https://github.com/adrienverge/PhotoCollage
 *
 * @author Patrik Karlsson
 */
public class Cell {

    /*
    Properties:
    <- x -><- w ->
    ---------------------- ^
    |      |             | |y
    |------|             | |
    |      |-------------| v
    |------| Cell |      | ^
    |      |      |      | |h
    ---------------------- v
     */
    private Extent mExtent;
    private int mHeight;
    private LinkedList<Column> mParents;
    private Photo mPhoto;

    public Cell(Photo photo, LinkedList<Column> parents) {
        mParents = parents;
        mPhoto = photo;
        mExtent = null;
        mHeight = (int) Math.round(getWidth() * getWantedRatio());
    }

    public Cell() {
    }

    Cell getOrigin() {
        return null;
    }

    void setParents(LinkedList<Column> parents) {
        mParents = parents;
    }

    /**
     *
     * @return the cell below this one
     */
    Cell getBottomNeighbor() {
        try {
            int indexThis = mParents.getFirst().getCells().indexOf(this);
            return mParents.getFirst().getCells().get(indexThis + 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the coordinates of the contained image
     *
     * These are computed in order not to loose space, so the content area will always be greater
     * than the cell itself. It is the space taken by the contained image if it wasn't cropped.
     *
     * @return
     */
    private Rectangle getContentCoords() {
        int x = 0, y = 0, w = 0, h = 0;

        if (getWantedRatio() < getRatio()) {
            //If the contained image is too thick to fit
            h = getHeight();
            w = (int) Math.round(getHeight() / getWantedRatio());
            y = getY();
            x = (int) Math.round(getX() - (w - getWidth()) / 2.0);
        } else if (getWantedRatio() > getRatio()) {
            //If the contained image is too tall to fit
            w = getWidth();
            h = (int) Math.round(getWidth() * getWantedRatio());
            x = getX();
            y = (int) Math.round(getY() - (h - getHeight()) / 2.0);
        }

        return new Rectangle(x, y, w, h);
    }

    Extent getExtent() {
        return mExtent;
    }

    private double getRatio() {
        return 1.0 * getHeight() / getWidth();
    }

    Cell getTopNeighbor() {
        try {
            int indexThis = mParents.getFirst().getCells().indexOf(this);
            return mParents.getFirst().getCells().get(indexThis - 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private double getWantedRatio() {
        return mPhoto.getRatio();
    }

    private int getWidth() {
        return mParents
                .stream()
                .mapToInt(Column::getWidth)
                .sum();
    }

    private int getX() {
        return mParents.getFirst().getX();
    }

    int getHeight() {
        return mHeight;
    }

    LinkedList<Column> getParents() {
        return mParents;
    }

    Photo getPhoto() {
        return mPhoto;
    }

    /**
     * Returns the cell's y coordinate
     *
     * It assumes that the cell is in a single column, so it is the previous cell's y + h.
     */
    int getY() {
        Cell prev = null;
        for (Cell cell : mParents.getFirst().getCells()) {
            if (cell == this) {
                if (prev != null) {
                    return prev.getY() + prev.getHeight();
                } else {
                    return 0;
                }
            }
            prev = cell;
        }

        throw new IllegalAccessError("Should never reach thhis point");
    }

    boolean isExtended() {
        return !(this instanceof Extent) && mExtent != null;
    }

    boolean isExtension() {
        return this instanceof Extent;
    }

    void scale(double alpha) {
        mHeight = (int) Math.round(mHeight * alpha);
    }

    void setExtent(Extent extent) {
        mExtent = extent;
    }

    void setPhoto(Photo photo) {
        mPhoto = photo;
    }
}
/*
class Cell(object):
    """Represents a cell in a column

    Properties:
    <- x -><- w ->
    ---------------------- ^
    |      |             | |y
    |------|             | |
    |      |-------------| v
    |------| Cell |      | ^
    |      |      |      | |h
    ---------------------- v

    """
    def __init__(self, parents, photo):
        self.parents = parents
        self.photo = photo
        self.extent = None
        self.h = self.w * self.wanted_ratio

    def __repr__(self):
        """Representation of the cell in ASCII art"""
        end = "]"
        if self.extent is not None:
            end = "--"
        return "[%d %d%s" % (self.w, self.h, end)

    @property
    def x(self):
        return self.parents[0].x

    @property
    def y(self):
        """Returns the cell's y coordinate

        It assumes that the cell is in a single column, so it is the previous
        cell's y + h.

        """
        prev = None
        for c in self.parents[0].cells:
            if self is c:
                if prev:
                    return prev.y + prev.h
                return 0
            prev = c

    @property
    def w(self):
        return sum(c.w for c in self.parents)

    @property
    def ratio(self):
        return self.h / self.w

    @property
    def wanted_ratio(self):
        return self.photo.ratio

    def scale(self, alpha):
        self.h *= alpha

    def is_extended(self):
        return hasattr(self, 'extent') and self.extent is not None

    def is_extension(self):
        return isinstance(self, CellExtent)

    def content_coords(self):
        """Returns the coordinates of the contained image

        These are computed in order not to loose space, so the content area
        will always be greater than the cell itself. It is the space taken by
        the contained image if it wasn't cropped.

        """
        # If the contained image is too thick to fit
        if self.wanted_ratio < self.ratio:
            h = self.h
            w = self.h / self.wanted_ratio
            y = self.y
            x = self.x - (w - self.w) / 2.0
        # If the contained image is too tall to fit
        elif self.wanted_ratio > self.ratio:
            w = self.w
            h = self.w * self.wanted_ratio
            x = self.x
            y = self.y - (h - self.h) / 2.0
        else:
            w = self.w
            h = self.h
            x = self.x
            y = self.y
        return x, y, w, h

    def top_neighbor(self):
        """Returns the cell above this one"""
        prev = None
        for c in self.parents[0].cells:
            if self is c:
                return prev
            prev = c

    def bottom_neighbor(self):
        """Returns the cell below this one"""
        prev = None
        for c in reversed(self.parents[0].cells):
            if self is c:
                return prev
            prev = c
*/
