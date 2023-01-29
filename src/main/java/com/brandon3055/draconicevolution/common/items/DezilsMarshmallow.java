package com.brandon3055.draconicevolution.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by Brandon on 4/15/2015.
 */
public class DezilsMarshmallow extends ItemFood {

    public DezilsMarshmallow() {
        super(100, 100F, true);
        this.setUnlocalizedName("draconicevolution:dezilsMarshmallow");
        this.setAlwaysEdible();
        GameRegistry.registerItem(this, "dezilsMarshmallow");
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    @Override
    protected void onFoodEaten(ItemStack p_77849_1_, World p_77849_2_, EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 20 * 60 * 10, 5));
        player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 20 * 60 * 10, 5));
        player.addPotionEffect(new PotionEffect(Potion.jump.id, 20 * 60 * 10, 5));
        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 20 * 60 * 10, 5));
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 20 * 60 * 10, 4));
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, 20 * 60 * 10, 4));
        player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 20 * 60 * 10, 0));
        super.onFoodEaten(p_77849_1_, p_77849_2_, player);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "dezilsMarshmallow");
    }
}
