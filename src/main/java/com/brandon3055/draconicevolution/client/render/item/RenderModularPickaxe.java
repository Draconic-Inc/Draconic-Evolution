package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularPickaxe extends ToolRenderBase {

    private final ToolPart basePart;
    private final ToolPart materialPart;
    private final ToolPart gemPart;
    private final ToolPart tracePart;
    private final ToolPart bladePart;

    public RenderModularPickaxe(TechLevel techLevel) {
        super(techLevel, "pickaxe");
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/pickaxe.obj")).ignoreMtl().parse();
        basePart = basePart(model.get("handle").backfacedCopy());
        materialPart = materialPart(model.get("head").backfacedCopy());
        gemPart = gemPart(model.get("gem").backfacedCopy());
        tracePart = tracePart(model.get("handle_trace").backfacedCopy());
        bladePart = bladePart(model.get("blade").backfacedCopy());
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, TransformType transform, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
        transform(mat, 0.27, 0.27, 0.5, 1.125);

        basePart.render(transform, buffers, mat);
        materialPart.render(transform, buffers, mat);
        tracePart.render(transform, buffers, mat);
        gemPart.render(transform, buffers, mat);
        bladePart.render(transform, buffers, mat);
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }
}
