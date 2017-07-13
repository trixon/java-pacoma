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

import java.util.LinkedList;

/**
 * Based on work by Adrien Vergé in https://github.com/adrienverge/PhotoCollage
 *
 * @author Patrik Karlsson
 */
public class Extent extends Cell {

    private final Cell mOrigin;

    Extent(Cell cell) {
        mOrigin = cell;
        mOrigin.setExtent(this);
    }

    @Override
    int getHeight() {
        return mOrigin.getHeight();
    }

    @Override
    Cell getOrigin() {
        return mOrigin;
    }

    @Override
    LinkedList<Column> getParents() {
        return (LinkedList<Column>) mOrigin.getParents().subList(1, 1);
    }

    @Override
    Photo getPhoto() {
        return mOrigin.getPhoto();
    }

    @Override
    int getY() {
        return mOrigin.getY();
    }

    @Override
    void scale(double alpha) {
        //nop
    }
}
/*
class CellExtent(Cell):
    def __init__(self, cell):
        self.origin = cell
        self.origin.extent = self

    def __repr__(self):
        """Representation of the cell in ASCII art"""
        return "------]"

    @property
    def parents(self):
        return (self.origin.parents[1],)

    @property
    def photo(self):
        return self.origin.photo

    @property
    def y(self):
        return self.origin.y

    @property
    def h(self):
        return self.origin.h

    def scale(self, alpha):
        pass
*/
