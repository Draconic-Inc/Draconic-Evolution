package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.GuiCelestialManipulator;
import com.brandon3055.draconicevolution.client.gui.GuiGenerator;
import com.brandon3055.draconicevolution.client.gui.GuiGrinder;

import codechicken.lib.datagen.DynamicTextureProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Created by brandon3055 on 07/02/2024
 */
public class DynamicTextures extends DynamicTextureProvider {

    public DynamicTextures(DataGenerator gen, ExistingFileHelper fileHelper) {
        super(gen, fileHelper, DraconicEvolution.MODID);
    }

    @Override
    public void addTextures() {
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/generator"), GuiGenerator.GUI_WIDTH, GuiGenerator.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/generator"), GuiGenerator.GUI_WIDTH, GuiGenerator.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/grinder"), GuiGrinder.GUI_WIDTH, GuiGenerator.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/grinder"), GuiGrinder.GUI_WIDTH, GuiGenerator.GUI_HEIGHT, 4);
        
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/celestial_manipulator"), GuiCelestialManipulator.GUI_WIDTH, GuiCelestialManipulator.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/celestial_manipulator"), GuiCelestialManipulator.GUI_WIDTH, GuiCelestialManipulator.GUI_HEIGHT, 4);
    }
}
