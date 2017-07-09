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
public class Column {

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
