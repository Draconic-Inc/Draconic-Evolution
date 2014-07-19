package com.brandon3055.draconicevolution.common.core.utills;

/**
 * Created by Brandon on 28/06/2014.
 */
public class GuiHelper {
	public static boolean isInRect(int x1, int y1, int x2, int y2, int x, int y){
		return ((x >= x1 && x <= x2) && (y >= y1 && y <= y2));
	}
}
