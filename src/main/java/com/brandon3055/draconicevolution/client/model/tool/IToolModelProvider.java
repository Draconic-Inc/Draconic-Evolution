package com.brandon3055.draconicevolution.client.model.tool;

import com.brandon3055.brandonscore.lib.Pair;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by covers1624 on 29/06/2017.
 */
@Deprecated
public interface IToolModelProvider {

    /**
     * Returns the models for an item, used with ToolModelBakery
     *
     * @param stack The stack.
     * @return Pair of 2D model -> OBJ ResourceLocation
     */
    @OnlyIn(Dist.CLIENT)
    Pair<TextureAtlasSprite, ResourceLocation> getModels(ItemStack stack);

}
