package com.brandon3055.draconicevolution.client.gui.modwiki.guielements;

import net.minecraft.client.gui.Gui;

import java.util.LinkedList;

/**
 * Created by brandon3055 on 21/07/2016.
 */
public class ElementBase extends Gui {

    protected LinkedList<ElementBase> childElements = new LinkedList<ElementBase>();
    public int ySize;
    public int xSize;

    public ElementBase() {}

    public <T extends ElementBase> T addChiled(T element) {
        childElements.add(element);
        return element;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }


}
