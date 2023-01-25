package com.brandon3055.draconicevolution.client;

import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;
import com.brandon3055.draconicevolution.integration.ModHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;

import java.util.function.Supplier;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 14/12/20
 * This all goes through BCSprites which means these are also registered to the BC GUI Texture sheet.
 * This means i can use these sprites anywhere i the BCSprites can be used without having to worry about texture sheets.
 */
public class DEGuiSprites {

    public static ResourceLocation LOCATION_SPRITE_ATLAS = BCGuiSprites.ATLAS_LOCATION;
    public static final RenderType GUI_TEX_TYPE = BCGuiSprites.GUI_TYPE;

    public static void initialize(ColorHandlerEvent.Block event) {
        //Gui Transfuser
        for (TileEnergyTransfuser.ItemIOMode mode : TileEnergyTransfuser.ItemIOMode.values()) {
            BCGuiSprites.register(MODID, mode.getSpriteName());
        }
        BCGuiSprites.register(MODID, "transfuser/balanced_charge");
        BCGuiSprites.register(MODID, "transfuser/sequential_charge");

        BCGuiSprites.register(MODID, "dislocator/slot");
        BCGuiSprites.register(MODID, "dislocator/slot_selected");
        BCGuiSprites.register(MODID, "dislocator/locked");
        BCGuiSprites.register(MODID, "dislocator/unlocked");
        BCGuiSprites.register(MODID, "dislocator/delete");
        BCGuiSprites.register(MODID, "dislocator/add_top");
        BCGuiSprites.register(MODID, "dislocator/add_bottom");

        // Gui Draconium chest
        BCGuiSprites.register(MODID, "chest/fire_over");
        BCGuiSprites.register(MODID, "chest/fire_base");
        BCGuiSprites.register(MODID, "chest/feed_off");
        BCGuiSprites.register(MODID, "chest/feed_all");
        BCGuiSprites.register(MODID, "chest/feed_filter");
        BCGuiSprites.register(MODID, "chest/feed_filter_sticky");
        
        // Celestial Manipulator
        BCGuiSprites.register(MODID, "celestial_manipulator/clear");
        BCGuiSprites.register(MODID, "celestial_manipulator/rain");
        BCGuiSprites.register(MODID, "celestial_manipulator/storm");
        BCGuiSprites.register(MODID, "celestial_manipulator/sunrise");
        BCGuiSprites.register(MODID, "celestial_manipulator/noon");
        BCGuiSprites.register(MODID, "celestial_manipulator/sunset");
        BCGuiSprites.register(MODID, "celestial_manipulator/moonrise");
        BCGuiSprites.register(MODID, "celestial_manipulator/midnight");
        BCGuiSprites.register(MODID, "celestial_manipulator/moonset");

        // Hud
        BCGuiSprites.register(MODID, "hud/ryg_bar");
        BCGuiSprites.register(MODID, "hud/shield_icon");
        BCGuiSprites.register(MODID, "hud/undying");

        // Reactor
        BCGuiSprites.register(MODID, "reactor/background");
        BCGuiSprites.register(MODID, "reactor/pointer");

        // Particle
        for (int i = 0; i < 8; i++) {
            BCGuiSprites.register(MODID, "effect/glitter_" + i);
        }
    }

    public static Material getThemed(String location) {
        return BCGuiSprites.getThemed(MODID, location);
    }

    public static Material get(String location) {
        return BCGuiSprites.get(MODID, location);
    }

    public static TextureAtlasSprite getSprite(String location) {
        return get(location).sprite();
    }

    public static Supplier<Material> themedGetter(String location) {
        return BCGuiSprites.themedGetter(MODID, location);
    }

    public static Supplier<Material> getter(String location) {
        return BCGuiSprites.getter(MODID, location);
    }
}
