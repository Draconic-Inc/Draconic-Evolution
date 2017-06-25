package com.brandon3055.draconicevolution.client.model;

import codechicken.lib.model.ItemQuadBakery;
import codechicken.lib.model.bakery.generation.IItemBakery;
import codechicken.lib.texture.TextureUtils;
import com.brandon3055.brandonscore.lib.Set3;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 21/06/2017.
 */
public class ToolModelBakery implements IItemBakery, ILoadingBakery, IResourceManagerReloadListener {

    private static Map<Item, ToolModelBakery> instanceMap = new HashMap<>();

    private final ResourceLocation tex2D;
    private final ResourceLocation objTex;
    private final ResourceLocation objLoc;

    private boolean objBakeFailed = false;
    private IBakedModel bakedToolModel;

    public ToolModelBakery(Set3<String, String, String> texLocs) {

        this.tex2D = new ResourceLocation(DraconicEvolution.MODID, texLocs.getA());
        this.objTex = new ResourceLocation(DraconicEvolution.MODID, texLocs.getB());
        this.objLoc = new ResourceLocation(DraconicEvolution.MODID, texLocs.getC());
    }


    @Override//HURR OBERIDE!!!
    public void load() {
        //LogHelper.dev("Load Tool Model: " + objLoc + " Tex: " + objTex);
//        Collection<CCModel> models = OBJParser.parseModels(objLoc).values();
//        CCModel simplifiedModel = ModelHelper.quadulate(CCModel.combine(models));//ModelHelper.simplifyModel(ModelHelper.quadulate(CCModel.combine(models)));

//        objModel = simplifiedModel.computeNormals().smoothNormals().backfacedCopy();
//        objModel = simplifiedModel.backfacedCopy().computeNormals();
    }


    @Nonnull
    @Override
    public List<BakedQuad> bakeItemQuads(@Nullable EnumFacing face, ItemStack stack) {

        List<BakedQuad> quads = new LinkedList<>();
        if (face == null) {
            if (DEConfig.disable3DModels || objBakeFailed) {
                quads.addAll(ItemQuadBakery.bakeItem(ImmutableList.of(TextureUtils.getTexture(tex2D))));
            } else {
                if (bakedToolModel == null) {
                    try {
                        bakedToolModel = OBJLoader.INSTANCE.loadModel(objLoc).bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, TextureUtils.bakedTextureGetter);
                    } catch (Exception e) {
                        LogHelper.errorError("Exception thrown whilst baking 3d OBJ model!", e);
                        objBakeFailed = true;
                    }
                }
                return bakedToolModel.getQuads(null, null, 0);
            }
        }

        return quads;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        bakedToolModel = null;
        objBakeFailed = false;
    }

    public static ToolModelBakery getBakery(Item item) {
        return instanceMap.get(item);
    }

    public static void createBakery(Item item, Set3<String, String, String> texLocs) {
        ToolModelBakery bakery = new ToolModelBakery(texLocs);
        DraconicEvolution.proxy.registerLoadingBakery(bakery);
        TextureUtils.registerReloadListener(bakery);
        instanceMap.put(item, bakery);
    }
}
