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

/**
 * Based on work by Adrien Vergé in https://github.com/adrienverge/PhotoCollage
 *
 * @author Patrik Karlsson
 */
public class Cell {

    private Photo mPhoto;
    private CellExtent mCellExtent;
    private int mHeight;
    private int mWidth;
    private Column[] mParents;
//    private double

    public Cell(Photo photo, Column... parents) {
        mPhoto = photo;
        mParents = parents;
//        mHeight=
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
