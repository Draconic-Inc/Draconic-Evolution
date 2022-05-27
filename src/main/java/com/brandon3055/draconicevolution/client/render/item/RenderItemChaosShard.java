package com.brandon3055.draconicevolution.client.render.item;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileChaosCrystal;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * Created by brandon3055 on 27/2/20.
 */
public class RenderItemChaosShard implements IItemRenderer {

    private CCModel shard;
    private Item item;


    public RenderItemChaosShard(Item item) {
        this.item = item;
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/item/chaos_shard.obj")).quads().ignoreMtl().parse();
        shard = CCModel.combine(map.values()).backfacedCopy();
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
    public void renderItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        Matrix4 mat = new Matrix4(mStack);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        mat.apply(new Scale(item == DEContent.chaos_shard ? 1 : item == DEContent.chaos_frag_large ? 0.75 : item == DEContent.chaos_frag_medium ? 0.5 : 0.25).at(new Vector3(0.5, 0.5, 0.5)));
        Player player = Minecraft.getInstance().player;
        DEShaders.chaosTime.glUniform1f((float) ClientUtils.getRenderTime());
        DEShaders.chaosYaw.glUniform1f((float) (player.getYRot() * MathHelper.torad));
        DEShaders.chaosPitch.glUniform1f((float) -(player.getXRot() * MathHelper.torad));
        ccrs.bind(RenderTileChaosCrystal.chaosType, getter);
        shard.render(ccrs, mat);
        ccrs.baseColour = 0xFFFFFFF0;
        ccrs.bind(RenderTileChaosCrystal.crystalType, getter);
        shard.render(ccrs, mat);
    }

    @Override
    public ModelState getModelTransform() {
        return TransformUtils.DEFAULT_ITEM;
    }
}
