package com.brandon3055.draconicevolution.datagen;

import codechicken.lib.datagen.DynamicTextureProvider;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Created by brandon3055 on 07/02/2024
 */
public class DynamicTextures extends DynamicTextureProvider {

    public DynamicTextures(DataGenerator gen, ExistingFileHelper fileHelper) {
        super(gen, fileHelper, DraconicEvolution.MODID);
    }

    @Override
    public void addTextures() {
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/generator"), GeneratorGui.GUI_WIDTH, GeneratorGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/generator"), GeneratorGui.GUI_WIDTH, GeneratorGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/grinder"), GrinderGui.GUI_WIDTH, GeneratorGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/grinder"), GrinderGui.GUI_WIDTH, GeneratorGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/celestial_manipulator"), CelestialManipulatorGui.GUI_WIDTH, CelestialManipulatorGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/celestial_manipulator"), CelestialManipulatorGui.GUI_WIDTH, CelestialManipulatorGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/advanced_dislocator"), DislocatorGui.GUI_WIDTH, DislocatorGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/advanced_dislocator"), DislocatorGui.GUI_WIDTH, DislocatorGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/disenchanter"), DisenchanterGui.GUI_WIDTH, DisenchanterGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/disenchanter"), DisenchanterGui.GUI_WIDTH, DisenchanterGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/transfuser"), EnergyTransfuserGui.GUI_WIDTH, EnergyTransfuserGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/transfuser"), EnergyTransfuserGui.GUI_WIDTH, EnergyTransfuserGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/entity_detector"), EntityDetectorGui.GUI_WIDTH, EntityDetectorGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/entity_detector"), EntityDetectorGui.GUI_WIDTH, EntityDetectorGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/draconium_chest"), DraconiumChestGui.GUI_WIDTH, DraconiumChestGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/draconium_chest"), DraconiumChestGui.GUI_WIDTH, DraconiumChestGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/fusion_craft"), FusionCraftingCoreGui.GUI_WIDTH, FusionCraftingCoreGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/fusion_craft"), FusionCraftingCoreGui.GUI_WIDTH, FusionCraftingCoreGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/energy_core"), EnergyCoreGui.GUI_WIDTH, EnergyCoreGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/energy_core"), EnergyCoreGui.GUI_WIDTH, EnergyCoreGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/reactor"), ReactorGui.GUI_WIDTH, ReactorGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/reactor"), ReactorGui.GUI_WIDTH, ReactorGui.GUI_HEIGHT, 4);

        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/light/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/light/flow_gate"), FlowGateGui.GUI_WIDTH, FlowGateGui.GUI_HEIGHT, 4);
        addDynamicTexture(new ResourceLocation(BrandonsCore.MODID, "textures/gui/dark/background_dynamic"), new ResourceLocation(DraconicEvolution.MODID, "textures/gui/dark/flow_gate"), FlowGateGui.GUI_WIDTH, FlowGateGui.GUI_HEIGHT, 4);
    }
}
