package com.brandon3055.draconicevolution.client.gui.modwiki;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 18/09/2016.
 */
public class StylePreset {

    public static final List<StylePreset> PRESETS = new LinkedList<>();

    private final int NAV_WINDOW;
    private final int CONTENT_WINDOW;
    private final int MENU_BAR;

    private final int NAV_TEXT;
    private final int TEXT_2;
    private final String name;


    public StylePreset(String name, int nav_window, int content_window, int menu_bar, int nav_text, int text_2) {
        NAV_WINDOW = nav_window;
        CONTENT_WINDOW = content_window;
        MENU_BAR = menu_bar;
        NAV_TEXT = nav_text;
        TEXT_2 = text_2;
        this.name = name;
    }

    public static void addPreset(StylePreset preset) {
        PRESETS.add(preset);
    }

    public void apply() {
        WikiConfig.NAV_WINDOW = this.NAV_WINDOW;
        WikiConfig.CONTENT_WINDOW = this.CONTENT_WINDOW;
        WikiConfig.MENU_BAR = this.MENU_BAR;
        WikiConfig.NAV_TEXT = this.NAV_TEXT;
        WikiConfig.TEXT_COLOUR = this.TEXT_2;
        WikiConfig.save();
    }

    @SideOnly(Side.CLIENT)
    public String getName() {
        return I18n.format("modwiki.style.preset." + name);
    }

    static {
        addPreset(new StylePreset("intellij", 0xFF3c3f41, 0xFF3c3f41, 0xFF3c3f41, 0xD0D0D0, 0xC0C0C0));
        addPreset(new StylePreset("black&white", 0xFF080808, 0xFF080808, 0xFF080808, 0xFFFFFF, 0xFFFFFF));
        addPreset(new StylePreset("white&black", 0xFFC0C0C0, 0xFFC0C0C0, 0xFFC0C0C0, 0x000000, 0x000000));
        addPreset(new StylePreset("purple", 0xFF2A006D, 0xFF2A006D, 0xFF2A006D, 0xFFFFFF, 0xFFFFFF));
        addPreset(new StylePreset("draconic", 0xFF781B00, 0xFF781B00, 0xFF781B00, 0x3900B3, 0x3900B3));
        addPreset(new StylePreset("transparent", 0x80781B00, 0x80781B00, 0x80781B00, 0xFFFFFF, 0xFFFFFF));
    }
}
