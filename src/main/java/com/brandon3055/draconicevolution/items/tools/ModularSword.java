package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.brandonscore.lib.TechItemProps;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.itemconfig_dep.ItemConfigFieldRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularSword extends ModularToolBase implements IReaperItem {

    public ModularSword(TechItemProps props) {
        super(props);
        DraconicEvolution.proxy.registerToolRenderer(this);
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return 0;
    }
}
