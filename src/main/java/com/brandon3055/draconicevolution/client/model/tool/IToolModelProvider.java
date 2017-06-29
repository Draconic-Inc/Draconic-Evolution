package com.brandon3055.draconicevolution.client.model.tool;

import com.brandon3055.brandonscore.lib.PairKV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by covers1624 on 29/06/2017.
 */
public interface IToolModelProvider {

    /**
     * Returns the models for an item, used with ToolModelBakery
     *
     * @param stack The stack.
     * @return Pair of 2D model -> OBJ ResourceLocation
     */
    @SideOnly (Side.CLIENT)
    PairKV<TextureAtlasSprite, ResourceLocation> getModels(ItemStack stack);

}
