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
import java.util.stream.Collectors;

/**
 * Represents a column in a page
 *
 * Based on work by Adrien Vergé in https://github.com/adrienverge/PhotoCollage
 *
 * @author Patrik Karlsson
 */
public class Column {

    /*
    Properties:
    <----- x ----><-- w ->
    ---------------------- ^
    |      |      |      | |
    |      |      |      |
    |      |      |Column| h
    |      |      |      |
    -------|      |      | |
    |      |------- v
    --------
     */
    private final LinkedList<Cell> mCells = new LinkedList<>();
    private final Page mParent;
    private final LinkedList<Column> mParentColumns;
    private int mWidth;

    Column(Page parent, int columnWidth) {
        mParent = parent;
        mParentColumns = mParent.getColumns();
        mWidth = columnWidth;
    }

    /**
     *
     * @return Representation of the column in ASCII art
     */
    @Override
    public String toString() {
        return mCells.stream()
                .map(Cell::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Set the column's height to a given value by resizing cells
     *
     * @param targetHeight
     */
    void adjustHeight(int targetHeight) {
        class Group {

            private final int y;
            private int h;
            private final LinkedList<Cell> cells = new LinkedList<>();

            public Group(int y) {
                this.y = y;
                h = 0;
            }

        }
        LinkedList<Group> groups = new LinkedList<>();
        groups.add(new Group(0));
        mCells.forEach((cell) -> {
            //While a cell extent is not reached, keep add cells to the group
            if (!cell.isExtension()) {
                groups.getLast().cells.add(cell);
            } else {
                //Close current group and create a new one
                groups.getLast().h = cell.getY() - groups.getLast().y;
                groups.add(new Group(cell.getY() + cell.getHeight()));
            }
        });
        groups.getLast().h = targetHeight - groups.getLast().y;

        //Adjust height for each group independently
        groups.stream().filter((group) -> !(group.cells.isEmpty())).forEachOrdered((group) -> {
            double alpha = group.h / group.cells
                    .stream()
                    .mapToInt(Cell::getHeight)
                    .sum();

            group.cells.forEach((cell) -> {
                cell.scale(alpha);
            });
        });
    }

    LinkedList<Cell> getCells() {
        return mCells;
    }

    /**
     * @return The column's total height
     *
     * This is not simply the sum of its cells heights, because there can be empty spaces between
     * cells.
     *
     */
    int getHeight() {
        if (mCells.isEmpty()) {
            return 0;
        } else {
            return mCells.getLast().getY() + mCells.getLast().getHeight();
        }
    }

    /**
     *
     * @return the column on the left of this one
     */
    Column getLeftNeighbor() {
        try {
            return mParentColumns.get(mParentColumns.indexOf(this) - 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     *
     * @return the column on the left of this one
     */
    Column getRightNeighbor() {
        try {
            return mParentColumns.get(mParentColumns.indexOf(this) + 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    int getWidth() {
        return mWidth;
    }

    int getX() {
        int x = 0;

        for (Column column : mParentColumns) {
            if (column == this) {
                break;
            } else {
                x += column.mWidth;
            }
        }

        return x;
    }

    void scale(double alpha) {
        mWidth *= alpha;
        mCells.forEach((Cell cell) -> {
            cell.scale(alpha);
        });
    }
}
/*
class Column(object):
    """Represents a column in a page

    Properties:
    <----- x ----><-- w ->
    ---------------------- ^
    |      |      |      | |
    |      |      |      |
    |      |      |Column| h
    |      |      |      |
    -------|      |      | |
           |      |------- v
           --------

    """
    def __init__(self, parent, w):
        self.parent = parent
        self.cells = []
        self.w = w

    def __repr__(self):
        """Representation of the column in ASCII art"""
        return "\n".join(c.__repr__() for c in self.cells)

    @property
    def h(self):
        """Returns the column's total height

        This is not simply the sum of its cells heights, because there can be
        empty spaces between cells.

        """
        if not self.cells:
            return 0
        return self.cells[-1].y + self.cells[-1].h

    @property
    def x(self):
        x = 0
        for c in self.parent.cols:
            if self is c:
                break
            x += c.w
        return x

    def scale(self, alpha):
        self.w *= alpha
        for c in self.cells:
            c.scale(alpha)

    def left_neighbor(self):
        """Returns the column on the left of this one"""
        prev = None
        for c in self.parent.cols:
            if self is c:
                return prev
            prev = c

    def right_neighbor(self):
        """Returns the column on the right of this one"""
        prev = None
        for c in reversed(self.parent.cols):
            if self is c:
                return prev
            prev = c

    def adjust_height(self, target_h):
        """Set the column's height to a given value by resizing cells"""
        # First, make groups of "movable" cells. Since cell extents are not
        # movable, these groups only contain pure cell objects. We only resize
        # those groups.
        class Group(object):
            def __init__(self, y):
                self.y = y
                self.h = 0
                self.cells = []

        groups = []
        groups.append(Group(0))
        for c in self.cells:
            # While a cell extent is not reached, keep add cells to the group
            if not c.is_extension():
                groups[-1].cells.append(c)
            else:
                # Close current group and create a new one
                groups[-1].h = c.y - groups[-1].y
                groups.append(Group(c.y + c.h))
        groups[-1].h = target_h - groups[-1].y

        # Adjust height for each group independently
        for group in groups:
            if not group.cells:
                continue
            alpha = group.h / sum(c.h for c in group.cells)
            for c in group.cells:
                c.h = c.h * alpha
 */
