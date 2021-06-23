package com.brandon3055.draconicevolution.items;

import com.brandon3055.draconicevolution.entity.projectile.DEArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Werechang on 16/6/21
 */

public class DEArrowItem extends ArrowItem {

    private boolean isEnergyArrow;

    public DEArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public DEArrowEntity createArrow(World world, ItemStack stack, LivingEntity entity) {
        DEArrowEntity arrowEntity = new DEArrowEntity(world, entity, isEnergyArrow);
        arrowEntity.setEffectsFromItem(stack);
        return arrowEntity;
    }

    public void setEnergyArrow(boolean isEnergyArrow) {
        this.isEnergyArrow = isEnergyArrow;
    }
}
