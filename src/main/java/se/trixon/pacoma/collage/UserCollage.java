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
