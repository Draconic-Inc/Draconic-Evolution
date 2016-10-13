package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 20/07/2016.
 */
public class ItemPersistent extends ItemBCore {

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }
}
