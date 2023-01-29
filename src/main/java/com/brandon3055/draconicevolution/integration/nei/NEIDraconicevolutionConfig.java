package com.brandon3055.draconicevolution.integration.nei;

import net.minecraft.item.ItemStack;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

import com.brandon3055.draconicevolution.client.gui.GUIDraconiumChest;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

/**
 * Created by Brandon on 30/10/2014.
 */
public class NEIDraconicevolutionConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {

        API.registerGuiOverlay(GUIDraconiumChest.class, "crafting", new CraftingChestStackPositioner());
        API.registerGuiOverlayHandler(GUIDraconiumChest.class, new CraftingChestOverlayHandler(), "crafting");
        API.hideItem(new ItemStack(ModBlocks.placedItem));
        API.hideItem(new ItemStack(ModBlocks.invisibleMultiblock));
        API.hideItem(new ItemStack(ModBlocks.safetyFlame));
        API.hideItem(new ItemStack(ModItems.tclogo));
        API.hideItem(new ItemStack(ModItems.creativeStructureSpawner));
        API.hideItem(new ItemStack(ModItems.dezilsMarshmallow));
        API.hideItem(new ItemStack(ModItems.creativeStructureSpawner, 1, 1));
        API.hideItem(new ItemStack(ModItems.sunFocus));
        API.hideItem(new ItemStack(ModBlocks.portal));
        API.hideItem(new ItemStack(ModBlocks.chaosShardAtmos));
        API.hideItem(new ItemStack(ModBlocks.chaosCrystal));
        API.registerNEIGuiHandler(new DENEIGuiHandler());

        LogHelper.info("Added NEI integration");
    }

    @Override
    public String getName() {
        return "DraconicEvolution-NEIConfig";
    }

    @Override
    public String getVersion() {
        return References.VERSION;
    }
}
