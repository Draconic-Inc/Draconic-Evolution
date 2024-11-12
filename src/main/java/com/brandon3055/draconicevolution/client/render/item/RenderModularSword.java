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
public class RenderModularSword extends ToolRenderBase {

    private final ToolPart basePart;
    private final ToolPart materialPart;
    private final ToolPart gemPart;
    private final ToolPart tracePart;
    private final ToolPart bladePart;

    public RenderModularSword(TechLevel techLevel) {
        super(techLevel, "sword");
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/sword.obj")).ignoreMtl().parse();
        basePart = basePart(CCModel.combine(Arrays.asList(model.get("handle"), model.get("handle_bauble"), model.get("hilt"))).backfacedCopy());
        materialPart = materialPart(model.get("blade_core").backfacedCopy());
        gemPart = gemPart(model.get("blade_gem").backfacedCopy());
        tracePart = tracePart(CCModel.combine(Arrays.asList(model.get("trace_top"), model.get("trace_bottom"))).backfacedCopy());
        bladePart = bladePart(model.get("blade_edge").backfacedCopy());
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, ItemDisplayContext context, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
        transform(mat, 0.29, 0.29, 0.5, gui ? 0.875 : 1.125);

        basePart.render(context, buffers, mat);
        materialPart.render(context, buffers, mat);
        tracePart.render(context, buffers, mat);
        gemPart.render(context, buffers, mat);
        bladePart.render(context, buffers, mat);
    }

    //@formatter:off //This is not cursed at all! idk what your talking about!
    public static class SWORD_WYVERN extends RenderModularSword { public SWORD_WYVERN() {super(TechLevel.WYVERN);}}
    public static class SWORD_DRACONIC extends RenderModularSword { public SWORD_DRACONIC() {super(TechLevel.DRACONIC);}}
    public static class SWORD_CHAOTIC extends RenderModularSword { public SWORD_CHAOTIC() {super(TechLevel.CHAOTIC);}}
    //@formatter::on
}
