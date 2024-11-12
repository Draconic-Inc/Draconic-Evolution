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

import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularHoe extends ToolRenderBase {

    private final ToolPart basePart;
    private final ToolPart materialPart;
    private final ToolPart gemPart;
    private final ToolPart tracePart;

    public RenderModularHoe(TechLevel techLevel) {
        super(techLevel, "hoe");
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/hoe.obj")).ignoreMtl().parse();
        basePart = basePart(model.get("handle").backfacedCopy());
        materialPart = materialPart(model.get("head").backfacedCopy());
        gemPart = gemPart(model.get("gem").backfacedCopy());
        tracePart = tracePart(model.get("trace").backfacedCopy());
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, ItemDisplayContext context, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
        transform(mat, 0.28, 0.28, 0.5, 1.25);
        basePart.render(context, buffers, mat);
        materialPart.render(context, buffers, mat);
        tracePart.render(context, buffers, mat);
        gemPart.render(context, buffers, mat);
    }

    //@formatter:off //This is not cursed at all! idk what your talking about!
    public static class HOE_WYVERN extends RenderModularHoe { public HOE_WYVERN() {super(TechLevel.WYVERN);}}
    public static class HOE_DRACONIC extends RenderModularHoe { public HOE_DRACONIC() {super(TechLevel.DRACONIC);}}
    public static class HOE_CHAOTIC extends RenderModularHoe { public HOE_CHAOTIC() {super(TechLevel.CHAOTIC);}}
    //@formatter::on
}
