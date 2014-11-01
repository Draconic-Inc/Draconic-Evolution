package com.brandon3055.draconicevolution.common.core.utills;

/**
 * Created by Brandon on 28/06/2014.
 */
public class GuiHelper {
	public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY){
		return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
	}
}
