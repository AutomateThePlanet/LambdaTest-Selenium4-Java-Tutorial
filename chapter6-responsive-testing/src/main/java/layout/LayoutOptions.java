/*
 * Copyright 2021 Automate The Planet Ltd.
 * Author: Anton Angelov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package layout;

public enum LayoutOptions {
    ABOVE("above"),
    RIGHT("right"),
    LEFT("left"),
    BELOW("below"),
    INSIDE("inside"),
    BOTTOM_INSIDE("bottom inside"),
    LEFT_INSIDE("left inside"),
    RIGHT_INSIDE("right inside"),
    HEIGHT("height"),
    WIDTH("width"),
    ALIGNED_HORIZONTALLY_TOP("aligned horizontally top"),
    ALIGNED_HORIZONTALLY_BOTTOM("aligned horizontally bottom"),
    ALIGNED_HORIZONTALLY_CENTERED("aligned horizontally centered"),
    ALIGNED_HORIZONTALLY_ALL("aligned horizontally all"),
    ALIGNED_VERTICALLY_LEFT("aligned vertically left"),
    ALIGNED_VERTICALLY_RIGHT("aligned vertically right"),
    ALIGNED_VERTICALLY_CENTERED("aligned vertically centered"),
    ALIGNED_VERTICALLY_ALL("aligned vertically all"),
    TOP_INSIDE("top inside");

    private final String name;

    LayoutOptions(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}
