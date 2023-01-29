package com.brandon3055.draconicevolution.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityPersistentItem;

/**
 * Created by brandon3055 on 1/10/2015.
 */
public class ChaosShard extends ItemDE {

    public ChaosShard() {
        this.setUnlocalizedName("chaosShard");
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        ModItems.register(this);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {}

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }
}
