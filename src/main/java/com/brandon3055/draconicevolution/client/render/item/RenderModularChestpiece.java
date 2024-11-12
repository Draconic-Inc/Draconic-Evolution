package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.client.shader.ToolShader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 22/5/20.
 */
public class RenderModularChestpiece extends ToolRenderBase {

    private final ToolPart basePart;
    private final ToolPart materialPart;
    private final ToolPart gemPart;
    private final CoreGemPart coreGemPart;

    public RenderModularChestpiece(TechLevel techLevel) {
        super(techLevel, "chestpeice");
        Map<String, CCModel> model = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/equipment/chestpeice.obj")).ignoreMtl().parse();
        basePart = basePart(model.get("base_model").backfacedCopy());
        materialPart = materialPart(model.get("chevrons").backfacedCopy());
        gemPart = gemPart(model.get("power_crystals").backfacedCopy());
        coreGemPart = coreGemPart(model.get("crystal_core").backfacedCopy());
    }

    @Override
    public void renderTool(CCRenderState ccrs, ItemStack stack, ItemDisplayContext context, Matrix4 mat, MultiBufferSource buffers, boolean gui) {
        mat.translate(0.5, 1.05, 0.5);
        mat.rotate(MathHelper.torad * 180, Vector3.Z_POS);
        mat.scale(1.95);

        basePart.render(context, buffers, mat);
        materialPart.render(context, buffers, mat);

        int shieldColour = 0xFFFFFFFF;
        ModuleHost host = stack.getCapability(DECapabilities.Host.ITEM);
        if (!stack.isEmpty() && host != null) {
            ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
            if (shieldControl != null) {
                shieldColour = shieldControl.getShieldColour();
            }
        }
        gemPart.render(context, buffers, mat);
        coreGemPart.render(buffers, mat, shieldColour);
    }

    @Override
    public @Nullable PerspectiveModelState getModelState() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    protected CoreGemPart coreGemPart(CCModel model) {
        String levelName = techLevel.name().toLowerCase(Locale.ROOT);
        RenderType gemType = RenderType.create(MODID + ":core_gem", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(DEShaders.CHESTPIECE_GEM_SHADER::getShaderInstance))
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/item/equipment/shader_fallback_" + levelName + ".png"), false, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        );

        return new CoreGemPart(model, gemType, DEShaders.CHESTPIECE_GEM_SHADER);
    }

    private class CoreGemPart extends SimpleToolPart {
        private final ToolShader shader;

        public CoreGemPart(CCModel model, RenderType baseType, ToolShader shader) {
            super(model, baseType, shader);
            this.shader = shader;
        }

        @Override
        public void render(ItemDisplayContext transformType, MultiBufferSource buffers, Matrix4 mat, float pulse) {
            render(buffers, mat, 0xFFFFFFFF);
        }

        public void render(MultiBufferSource buffers, Matrix4 mat, int color) {
            buffers.getBuffer(vboType.get().withCallback(() -> {
                shader.getBaseColorUniform().glUniform4f(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);
                shader.getModelMatUniform().glUniformMatrix4f(mat);
            }));
        }
    }

    //@formatter:off //This is not cursed at all! idk what your talking about!
    public static class CHESTPIECE_WYVERN extends RenderModularChestpiece { public CHESTPIECE_WYVERN() {super(TechLevel.WYVERN);}}
    public static class CHESTPIECE_DRACONIC extends RenderModularChestpiece { public CHESTPIECE_DRACONIC() {super(TechLevel.DRACONIC);}}
    public static class CHESTPIECE_CHAOTIC extends RenderModularChestpiece { public CHESTPIECE_CHAOTIC() {super(TechLevel.CHAOTIC);}}
    //@formatter::on
}
