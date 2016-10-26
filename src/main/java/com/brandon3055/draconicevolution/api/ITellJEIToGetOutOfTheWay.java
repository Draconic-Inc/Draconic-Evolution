package com.brandon3055.draconicevolution.api;

import java.awt.Rectangle;
import java.util.List;

/**
 * Created by brandon3055 on 26/10/2016.
 * A simple interface that can be implemented on any GUI container to move items in the JEI item panel out of the way of extra gui elements that extend past the defined gui size.
 */
public interface ITellJEIToGetOutOfTheWay {

    /**
     * This returns a list of rectangles where JEI should not render items.
     * x/y is the location of the top left of the rectangle.
     */
    List<Rectangle> getGuiExtraAreas();
}
