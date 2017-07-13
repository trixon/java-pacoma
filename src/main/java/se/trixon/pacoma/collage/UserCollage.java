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
import java.util.Collections;

/**
 * Based on work by Adrien Vergé in https://github.com/adrienverge/PhotoCollage
 *
 * @author Patrik Karlsson
 */
public class UserCollage {

    private Page mPage;
    private final ArrayList<Photo> mPhotos;

    public UserCollage(ArrayList photos) {
        mPhotos = photos;
    }

    public void makePage(Collage collage) {
        double ratio = 1.0 * collage.getHeight() / collage.getWidth();

        double avg_ratio = mPhotos
                .stream()
                .mapToDouble(Photo::getRatio)
                .average()
                .getAsDouble();

        /*
        # Virtual number of images: since ~ 1 image over 3 is in a multi-cell
        # (i.e. takes two columns), it takes the space of 4 images.
        # So it's equivalent to 1/3 * 4 + 2/3 = 2 times the number of images.
         */
        int virtualNumOfImages = 2 * mPhotos.size();
        int numOfCols = (int) Math.round(Math.sqrt(avg_ratio / ratio * virtualNumOfImages));

        mPage = new Page(1, ratio, numOfCols);
        Collections.shuffle(mPhotos);

        mPhotos.forEach((photo) -> {
            mPage.addCell(photo);
        });
        mPage.adjust();
    }

    //TODO Clone deep copy
}
/*
class UserCollage(object):
    """Represents a user-defined collage

    A UserCollage contains a list of photos (referenced by filenames) and a
    collage.Page object describing their layout in a final poster.

    """
    def __init__(self, photolist):
        self.photolist = photolist

    def make_page(self, opts):
        # Define the output image height / width ratio
        ratio = 1.0 * opts.out_h / opts.out_w

        # Compute a good number of columns. It depends on the ratio, the number
        # of images and the average ratio of these images. According to my
        # calculations, the number of column should be inversely proportional
        # to the square root of the output image ratio, and proportional to the
        # square root of the average input images ratio.
        avg_ratio = (sum(1.0 * photo.h / photo.w for photo in self.photolist) /
                     len(self.photolist))
        # Virtual number of images: since ~ 1 image over 3 is in a multi-cell
        # (i.e. takes two columns), it takes the space of 4 images.
        # So it's equivalent to 1/3 * 4 + 2/3 = 2 times the number of images.
        virtual_no_imgs = 2 * len(self.photolist)
        no_cols = int(round(math.sqrt(avg_ratio / ratio * virtual_no_imgs)))

        self.page = collage.Page(1.0, ratio, no_cols)
        random.shuffle(self.photolist)
        for photo in self.photolist:
            self.page.add_cell(photo)
        self.page.adjust()

    def duplicate(self):
        return UserCollage(copy.copy(self.photolist))
*/
