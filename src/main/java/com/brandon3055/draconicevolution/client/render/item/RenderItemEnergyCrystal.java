package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.colour.Colour;
import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal;
import com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal.CrystalType;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileEnergyCrystal;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.brandon3055.draconicevolution.client.render.tile.RenderTileEnergyCrystal.COLOURS;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class RenderItemEnergyCrystal implements IItemRenderer {
    public static final RenderType crystalBaseType = RenderType.entitySolid(new ResourceLocation(DraconicEvolution.MODID, "textures/models/crystal_base.png"));

    private final CrystalType type;
    private final TechLevel techLevel;
    private final CCModel crystalFull;
    private final CCModel crystalHalf;
    private final CCModel crystalBase;

    public RenderItemEnergyCrystal(CrystalType type, TechLevel techLevel) {
        this.type = type;
        this.techLevel = techLevel;
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/crystal.obj")).quads().ignoreMtl().parse();
        crystalFull = CCModel.combine(map.values()).backfacedCopy();
        map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/crystal_half.obj")).quads().ignoreMtl().parse();
        crystalHalf = map.get("Crystal").backfacedCopy();
        crystalBase = map.get("Base").backfacedCopy();
    }

    //region Unused

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    //endregion

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        int tier = techLevel.index;
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        mat.translate(0.5, type == CrystalType.CRYSTAL_IO ? 0 : 0.5, 0.5);
        DEShaders.energyCrystalMipmap.glUniform1f(0);
        DEShaders.energyCrystalColour.glUniform3f(COLOURS[tier][0], COLOURS[tier][1], COLOURS[tier][2]);

        if (type == CrystalType.CRYSTAL_IO) {
            ccrs.bind(crystalBaseType, getter);
            crystalBase.render(ccrs, mat);
            mat.apply(new Rotation(TimeKeeper.getClientTick() / 400F, 0, 1, 0));
            ccrs.baseColour = Colour.packRGBA(r[tier], g[tier], b[tier], 1F);
            ccrs.bind(RenderTileEnergyCrystal.crystalType, getter);
            crystalHalf.render(ccrs, mat);
        } else {
            ccrs.baseColour = Colour.packRGBA(r[tier], g[tier], b[tier], 1F);
            mat.apply(new Rotation(TimeKeeper.getClientTick() / 400F, 0, 1, 0));
            ccrs.bind(RenderTileEnergyCrystal.crystalType, getter);
            crystalFull.render(ccrs, mat);
        }
    }

    @Override
    public @Nullable PerspectiveModelState getModelState() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    private static float[] r = {0.0F, 0.47F, 1.0F};
    private static float[] g = {0.2F, 0.0F, 0.4F};
    private static float[] b = {0.3F, 0.58F, 0.1F};

    //@formatter:off //This is not cursed at all! idk what your talking about!
    public static class ITEM_BASIC_IO_CRYSTAL extends RenderItemEnergyCrystal { public ITEM_BASIC_IO_CRYSTAL() { super(EnergyCrystal.CrystalType.CRYSTAL_IO, TechLevel.DRACONIUM);}}
    public static class ITEM_WYVERN_IO_CRYSTAL extends RenderItemEnergyCrystal { public ITEM_WYVERN_IO_CRYSTAL() { super(EnergyCrystal.CrystalType.CRYSTAL_IO, TechLevel.WYVERN);}}
    public static class ITEM_DRACONIC_IO_CRYSTAL extends RenderItemEnergyCrystal { public ITEM_DRACONIC_IO_CRYSTAL() { super(EnergyCrystal.CrystalType.CRYSTAL_IO, TechLevel.DRACONIC);}}
    public static class ITEM_BASIC_RELAY_CRYSTAL extends RenderItemEnergyCrystal { public ITEM_BASIC_RELAY_CRYSTAL() { super(EnergyCrystal.CrystalType.RELAY, TechLevel.DRACONIUM);}}
    public static class ITEM_WYVERN_RELAY_CRYSTAL extends RenderItemEnergyCrystal { public ITEM_WYVERN_RELAY_CRYSTAL() { super(EnergyCrystal.CrystalType.RELAY, TechLevel.WYVERN);}}
    public static class ITEM_DRACONIC_RELAY_CRYSTAL extends RenderItemEnergyCrystal { public ITEM_DRACONIC_RELAY_CRYSTAL() { super(EnergyCrystal.CrystalType.RELAY, TechLevel.DRACONIC);}}
    public static class ITEM_BASIC_WIRELESS_CRYSTAL extends RenderItemEnergyCrystal { public ITEM_BASIC_WIRELESS_CRYSTAL() { super(EnergyCrystal.CrystalType.WIRELESS, TechLevel.DRACONIUM);}}
    public static class ITEM_WYVERN_WIRELESS_CRYSTAL extends RenderItemEnergyCrystal { public ITEM_WYVERN_WIRELESS_CRYSTAL() { super(EnergyCrystal.CrystalType.WIRELESS, TechLevel.WYVERN);}}
    public static class ITEM_DRACONIC_WIRELESS_CRYSTAL extends RenderItemEnergyCrystal { public ITEM_DRACONIC_WIRELESS_CRYSTAL() { super(EnergyCrystal.CrystalType.WIRELESS, TechLevel.DRACONIC);}}
    //@formatter:on
}
