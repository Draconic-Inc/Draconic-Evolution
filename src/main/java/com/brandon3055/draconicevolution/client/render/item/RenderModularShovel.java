package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularShovel extends ToolRenderBase {

    private final ToolPart basePart;
    private final ToolPart materialPart;
    private final ToolPart gemPart;
    private final ToolPart tracePart;

    public RenderModularShovel(TechLevel techLevel) {
        super(techLevel, "shovel");
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/shovel.obj")).ignoreMtl().parse();

        basePart = basePart(CCModel.combine(Arrays.asList(model.get("handle"), model.get("gem_holder"))).backfacedCopy());
        materialPart = materialPart(model.get("blade").twoFacedCopy());
        gemPart = gemPart(model.get("gem").backfacedCopy());
        tracePart = tracePart(model.get("trace").backfacedCopy());
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, ItemDisplayContext context, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
        transform(mat, 0.27, 0.27, 0.5, gui ? 1.125 : 1.15);
        basePart.render(context, buffers, mat);
        materialPart.render(context, buffers, mat);
        tracePart.render(context, buffers, mat);
        gemPart.render(context, buffers, mat);
    }

    //@formatter:off //This is not cursed at all! idk what your talking about!
    public static class SHOVEL_WYVERN extends RenderModularShovel { public SHOVEL_WYVERN() {super(TechLevel.WYVERN);}}
    public static class SHOVEL_DRACONIC extends RenderModularShovel { public SHOVEL_DRACONIC() {super(TechLevel.DRACONIC);}}
    public static class SHOVEL_CHAOTIC extends RenderModularShovel { public SHOVEL_CHAOTIC() {super(TechLevel.CHAOTIC);}}
    //@formatter::on
}
