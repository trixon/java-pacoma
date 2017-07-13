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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Based on work by Adrien Vergé in https://github.com/adrienverge/PhotoCollage
 *
 * @author Patrik Karlsson
 */
public class Page {

    private final ArrayList<Column> mColumns = new ArrayList<>();
    /*
    Properties:
    <-------- w -------->
    ---------------------- ^
    |                    | |
    |                    |
    |        Page        | h
    |                    |
    |                    | |
    ---------------------- v
     */
    private final double mTargetRatio;

    Page(int w, double targetRatio, int numOfCols) {
        mTargetRatio = targetRatio;

        int colW = (int) Math.round(1.0 * w / numOfCols);
        for (int i = 0; i < numOfCols; i++) {
            mColumns.add(new Column(this, colW));
        }
    }

    private void addCellMultiColumn(Column column1, Column column2, Photo photo) {
        LinkedList<Column> columnList = new LinkedList<>();
        columnList.add(column1);
        columnList.add(column2);
        Cell cell = new Cell(photo, columnList);
        Extent cellExtent = new Extent(cell);
        column1.getCells().add(cell);
        column2.getCells().add(cellExtent);
    }

    private void addCellSingleColumn(Column column, Photo photo) {
        LinkedList<Column> columnList = new LinkedList<>();
        columnList.add(column);
        column.getCells().add(new Cell(photo, columnList));
    }

    /**
     * Set all columns' heights to same value by shrinking them
     */
    private void adjustColumnHeights() {
        double targetHeight = getWidth() * mTargetRatio;
        mColumns.forEach((column) -> {
            column.adjustHeight((int) targetHeight);
        });
    }

    private Cell getCellAtPosition(int x, int y) {
        for (Column col : mColumns) {
            if (x >= col.getX() && x < col.getX() + col.getWidth()) {
                for (Cell cell : col.getCells()) {
                    if (y >= cell.getY() && y < cell.getY() + cell.getHeight()) {
                        if (cell.isExtension()) {
                            return ((Extent) cell).getOrigin();
                        } else {
                            return cell;
                        }
                    }
                }
            }
        }

        return null;
    }

    private int getHeight() {
        return mColumns
                .stream()
                .mapToInt(Column::getHeight)
                .max()
                .getAsInt();
    }

    /**
     *
     * @return the column with lowest height
     */
    private Column getNextFreeColumn() {
        int minHeight = mColumns
                .stream()
                .mapToInt(Column::getHeight)
                .min()
                .getAsInt();
        ArrayList<Column> candidates = new ArrayList<>();

        mColumns.stream().filter((column) -> (column.getHeight() == minHeight)).forEachOrdered((column) -> {
            candidates.add(column);
        });

        return candidates.get(new Random().nextInt(candidates.size()));
    }

    private int getNumOfCols() {
        return mColumns.size();
    }

    private double getRatio() {
        return 1.0 * getHeight() / getWidth();
    }

    private double getWidth() {
        return mColumns
                .stream()
                .mapToDouble(Column::getWidth)
                .sum();
    }

    /**
     * Remove holes created by extended cells
     */
    private void removeBottomHoles() {
        /*
        Example (case A):
        The bottom-right cell should be extended to fill the hole.
        ----------------------             ----------------------
        |      |      |      |             |      |      |      |
        |      |-------------|             |      |-------------|
        |------|             |             |------|             |
        |      |--------------             |      |--------------
        |      |      |  ^                 |      |  ^   |      |
        --------------- hole               -------- hole --------

        Example (case B):
        The bottom cell should be moved under the other extended cell.
        ----------------------             ----------------------
        |      |      |      |             |      |      |      |
        |------|-------------|             |-------------|------|
        |      |             |             |             |      |
        |---------------------             ---------------------|
        |             |   <-- hole      hole ->   |             |
        ---------------                           ---------------
         */
        for (Column col : mColumns) {
            Cell cell = col.getCells().getLast();
            if (cell == col.getCells().getFirst()) {
                continue;
            }

            // Case A
            // If cell is not extended, is below an extended cell and has no
            // neighbour under the latter, it should be extended.
            if (!cell.isExtended() && !cell.isExtension()) {
                //Case A1
                if (cell.getTopNeighbor().isExtended() && cell.getTopNeighbor().getExtent().getBottomNeighbor() == null) {
                    //Extend cell to right
                    Extent extent = new Extent(cell);
                    col.getRightNeighbor().getCells().add(extent);
                    LinkedList<Column> parents = new LinkedList<>();
                    parents.add(col);
                    parents.add(col.getRightNeighbor());
                    cell.setParents(parents);
                    //Case A2
                } else if (cell.getTopNeighbor().isExtension() && cell.getTopNeighbor().getOrigin().getBottomNeighbor() == null) {
                    //Extend cell to left
                    col.getCells().remove(cell);
                    col.getLeftNeighbor().getCells().add(cell);
                    Extent extent = new Extent(cell);
                    col.getCells().add(extent);
                    LinkedList<Column> parents = new LinkedList<>();
                    parents.add(col.getLeftNeighbor());
                    parents.add(col);
                    cell.setParents(parents);
                }

                //Case B
                //If cell is extended and one of the cells above is extended too,
                //the bottom cell should be placed right below the top one.
            } else if (cell.isExtended() && cell.getExtent().getBottomNeighbor() == null) {
                //Case B1
                if (cell.getExtent().getTopNeighbor().isExtended()
                        && cell.getExtent().getTopNeighbor().getExtent().getBottomNeighbor() == null) {
                    //Move cell to right
                    col.getCells().remove(cell);
                    col.getRightNeighbor().getCells().remove(cell.getExtent());
                    col.getRightNeighbor().getCells().add(cell);
                    col.getRightNeighbor().getRightNeighbor().getCells().add(cell.getExtent());
                    LinkedList<Column> parents = new LinkedList<>();
                    parents.add(col.getRightNeighbor());
                    parents.add(col.getRightNeighbor().getRightNeighbor());
                    cell.setParents(parents);
                    //Case B2
                } else if (cell.getTopNeighbor().isExtension()
                        && cell.getTopNeighbor().getOrigin().getBottomNeighbor() == null) {
                    //Move cell to left
                    col.getCells().remove(cell);
                    col.getRightNeighbor().getCells().remove(cell.getExtent());
                    col.getLeftNeighbor().getCells().add(cell);
                    col.getCells().add(cell.getExtent());
                    LinkedList<Column> parents = new LinkedList<>();
                    parents.add(col.getLeftNeighbor());
                    parents.add(col);
                    cell.setParents(parents);
                }
            }
        }
    }

    private void removeEmptyCols() {
        for (Iterator<Column> iterator = mColumns.iterator(); iterator.hasNext();) {
            Column column = iterator.next();
            if (column.getCells().isEmpty()) {
                iterator.remove();
            }
        }
    }

    private void scale(double alpha) {
        mColumns.forEach((column) -> {
            column.scale(alpha);
        });
    }

    private void scaleToFit(double maxWidth, Double maxHeight) {
        if (maxHeight == null || getWidth() * maxHeight > getHeight() * maxWidth) {
            scale(maxWidth / getWidth());
        } else {
            scale(maxHeight / getHeight());
        }
    }

    private void swapPhotos(Cell cell1, Cell cell2) {
        Photo photo2 = cell2.getPhoto();
        cell2.setPhoto(cell1.getPhoto());
        cell1.setPhoto(photo2);
    }

    /**
     * Add a new cell in the best computed place. If possible, and if it's worth, make a
     * "multiple-column" cell.
     *
     * @param photo
     */
    void addCell(Photo photo) {
        Column col = getNextFreeColumn();
        Column left = col.getLeftNeighbor();
        Column right = col.getRightNeighbor();

        if (2 * new Random().nextDouble() > photo.getRatio()) {
            if (left != null && Math.abs(col.getHeight() - left.getHeight()) < 0.5 * col.getWidth()) {
                addCellMultiColumn(left, col, photo);
                return;
            } else if (right != null && Math.abs(col.getHeight() - right.getHeight()) < 0.5 * col.getWidth()) {
                addCellMultiColumn(col, right, photo);
                return;
            }
        }

        addCellSingleColumn(col, photo);
    }

    void adjust() {
        removeEmptyCols();
        removeBottomHoles();
        adjustColumnHeights();
    }

    ArrayList<Column> getColumns() {
        return mColumns;
    }

}
/*
class Page(object):
    """Represents a whole page

    Properties:
    <-------- w -------->
    ---------------------- ^
    |                    | |
    |                    |
    |        Page        | h
    |                    |
    |                    | |
    ---------------------- v

    """
    def __init__(self, w, target_ratio, no_cols):
        self.target_ratio = target_ratio
        col_w = float(w)/no_cols
        self.cols = []
        for i in range(no_cols):
            self.cols.append(Column(self, col_w))

    def __repr__(self):
        """Representation of the page in ASCII art

        Returns something like:
        [62 52]    [125 134-- ------]    [62 87]
        [62 47]    [62 66]    [125 132-- [62 45]
        [62 46]    ------]    [62 49]    ------]
        [62 78]    ------]    [62 49]    [62 45]
        [125 102-- ------]    [62 49]    [62 65]
        [125 135--            [62 85]    [62 53]
        [125 91--             [125 89--  [62 64]
                                 ------]
        """
        lines = []
        n = 0
        end = False
        while not end:
            lines.append("")
            end = True
            for col in self.cols:
                cells = col.__repr__().split("\n")
                w = max(len(cell) for cell in cells)
                if col != self.cols[-1]:
                    w += 1
                cell = w * " "
                if n < len(cells):
                    cell = cells[n] + (w - len(cells[n])) * " "
                    if n < len(cells) - 1:
                        end = False
                lines[-1] += cell
            n += 1
        return "\n".join(lines)

    @property
    def no_cols(self):
        return len(self.cols)

    @property
    def w(self):
        return sum(c.w for c in self.cols)

    @property
    def h(self):
        return max(c.h for c in self.cols)

    @property
    def ratio(self):
        return self.h / self.w

    def scale(self, alpha):
        for c in self.cols:
            c.scale(alpha)

    def scale_to_fit(self, max_w, max_h=None):
        if max_h is None or self.w * max_h > self.h * max_w:
            self.scale(max_w / self.w)
        else:
            self.scale(max_h / self.h)

    def next_free_col(self):
        """Returns the column with lowest height"""
        minimum = min(c.h for c in self.cols)
        candidates = []
        for c in self.cols:
            if c.h == minimum:
                candidates.append(c)
        return random.choice(candidates)

    def add_cell_single_col(self, col, photo):
        col.cells.append(Cell((col,), photo))

    def add_cell_multi_col(self, col1, col2, photo):
        cell = Cell((col1, col2), photo)
        extent = CellExtent(cell)
        col1.cells.append(cell)
        col2.cells.append(extent)

    def add_cell(self, photo):
        """Add a new cell in the best computed place

        If possible, and if it's worth, make a "multiple-column" cell.

        """
        col = self.next_free_col()
        left = col.left_neighbor()
        right = col.right_neighbor()
        if 2 * random.random() > photo.ratio:
            if left and abs(col.h - left.h) < 0.5 * col.w:
                return self.add_cell_multi_col(left, col, photo)
            elif right and abs(col.h - right.h) < 0.5 * col.w:
                return self.add_cell_multi_col(col, right, photo)

        self.add_cell_single_col(col, photo)

    def remove_empty_cols(self):
        i = 0
        while i < len(self.cols):
            if len(self.cols[i].cells) == 0:
                self.cols.pop(i)
            else:
                i += 1

    def remove_bottom_holes(self):
        """Remove holes created by extended cells

        Example (case A):
        The bottom-right cell should be extended to fill the hole.
        ----------------------             ----------------------
        |      |      |      |             |      |      |      |
        |      |-------------|             |      |-------------|
        |------|             |             |------|             |
        |      |--------------             |      |--------------
        |      |      |  ^                 |      |  ^   |      |
        --------------- hole               -------- hole --------

        Example (case B):
        The bottom cell should be moved under the other extended cell.
        ----------------------             ----------------------
        |      |      |      |             |      |      |      |
        |------|-------------|             |-------------|------|
        |      |             |             |             |      |
        |---------------------             ---------------------|
        |             |   <-- hole      hole ->   |             |
        ---------------                           ---------------

        """
        for col in self.cols:
            cell = col.cells[-1]
            if cell == col.cells[0]:
                continue

            # Case A
            # If cell is not extended, is below an extended cell and has no
            # neighbour under the latter, it should be extended.
            if not cell.is_extended() and not cell.is_extension():
                # Case A1
                if cell.top_neighbor().is_extended() \
                        and cell.top_neighbor().extent \
                        .bottom_neighbor() is None:
                    # Extend cell to right
                    extent = CellExtent(cell)
                    col.right_neighbor().cells.append(extent)
                    cell.parents = (col, col.right_neighbor())
                # Case A2
                elif cell.top_neighbor().is_extension() \
                        and cell.top_neighbor().origin \
                        .bottom_neighbor() is None:
                    # Extend cell to left
                    col.cells.remove(cell)
                    col.left_neighbor().cells.append(cell)
                    extent = CellExtent(cell)
                    col.cells.append(extent)
                    cell.parents = (col.left_neighbor(), col)
            # Case B
            # If cell is extended and one of the cells above is extended too,
            # the bottom cell should be placed right below the top one.
            elif cell.is_extended() and cell.extent.bottom_neighbor() is None:
                # Case B1
                if cell.extent.top_neighbor().is_extended() \
                        and cell.extent.top_neighbor().extent \
                        .bottom_neighbor() is None:
                    # Move cell to right
                    col.cells.remove(cell)
                    col.right_neighbor().cells.remove(cell.extent)
                    col.right_neighbor().cells.append(cell)
                    col.right_neighbor().right_neighbor().cells \
                        .append(cell.extent)
                    cell.parents = (col.right_neighbor(),
                                    col.right_neighbor().right_neighbor())
                # Case B2
                elif cell.top_neighbor().is_extension() \
                        and cell.top_neighbor().origin \
                        .bottom_neighbor() is None:
                    # Move cell to left
                    col.cells.remove(cell)
                    col.right_neighbor().cells.remove(cell.extent)
                    col.left_neighbor().cells.append(cell)
                    col.cells.append(cell.extent)
                    cell.parents = (col.left_neighbor(), col)

    def adjust_cols_heights(self):
        """Set all columns' heights to same value by shrinking them"""
        target_h = self.w * self.target_ratio
        for c in self.cols:
            c.adjust_height(target_h)

    def adjust(self):
        self.remove_empty_cols()
        self.remove_bottom_holes()
        self.adjust_cols_heights()

    def get_cell_at_position(self, x, y):
        for col in self.cols:
            if x >= col.x and x < col.x + col.w:
                for cell in col.cells:
                    if y >= cell.y and y < cell.y + cell.h:
                        if cell.is_extension():
                            return cell.origin
                        return cell
        return None

    def swap_photos(self, cell1, cell2):
        cell1.photo, cell2.photo = cell2.photo, cell1.photo
 */
