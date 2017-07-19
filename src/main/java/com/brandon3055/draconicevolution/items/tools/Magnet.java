package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.entity.EntityLootCore;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by brandon3055 on 9/3/2016.
 */
public class Magnet extends ItemBCore {

    public Magnet() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }


    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (isInCreativeTab(tab)) {
            list.add(new ItemStack(this, 1, 0));
            list.add(new ItemStack(this, 1, 1));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return isEnabled(stack);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + (stack.getItemDamage() == 0 ? ".basic" : ".advanced");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean hotbar) {
        if (!entity.isSneaking() && entity.ticksExisted % 5 == 0 && isEnabled(stack) && entity instanceof EntityPlayer) {
            int range = stack.getItemDamage() == 0 ? 8 : 32;

            List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX, entity.posY, entity.posZ).grow(range, range, range));

            boolean flag = false;

            for (EntityItem itemEntity : items) {
                ItemStack item = itemEntity.getItem();

                String name = item.getItem().getRegistryName().toString();
                if (DEConfig.itemDislocatorBlacklistMap.containsKey(name) && (DEConfig.itemDislocatorBlacklistMap.get(name) == -1 || DEConfig.itemDislocatorBlacklistMap.get(name) == item.getItemDamage())) {
                    continue;
                }

                if (itemEntity.getThrower() != null && itemEntity.getThrower().equals(entity.getName()) && itemEntity.delayBeforeCanPickup > 0) {
                    continue;
                }

                EntityPlayer closest = world.getClosestPlayerToEntity(itemEntity, 4);
                if (closest != null && closest != entity) {
                    continue;
                }

                flag = true;

                if (!world.isRemote) {
                    if (itemEntity.delayBeforeCanPickup > 0) {
                        itemEntity.delayBeforeCanPickup = 0;
                    }
                    itemEntity.motionX = itemEntity.motionY = itemEntity.motionZ = 0;
                    itemEntity.setPosition(entity.posX - 0.2 + (world.rand.nextDouble() * 0.4), entity.posY - 0.6, entity.posZ - 0.2 + (world.rand.nextDouble() * 0.4));
                }
            }

            List<EntityLootCore> cores = world.getEntitiesWithinAABB(EntityLootCore.class, new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX, entity.posY, entity.posZ).grow(range, range, range));

            for (EntityLootCore core : cores) {
                EntityPlayer closest = world.getClosestPlayerToEntity(core, 4);
                if (closest != null && closest != entity) {
                    continue;
                }

                flag = true;

                if (!world.isRemote) {
                    core.setPosition(entity.posX - 0.2 + (world.rand.nextDouble() * 0.4), entity.posY - 0.6, entity.posZ - 0.2 + (world.rand.nextDouble() * 0.4));
                }
            }

            if (flag) {
                world.playSound((EntityPlayer) null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 2F));
            }


            List<EntityXPOrb> xp = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX, entity.posY, entity.posZ).grow(4, 4, 4));

            EntityPlayer player = (EntityPlayer) entity;

            for (EntityXPOrb orb : xp) {
                if (!world.isRemote) {
                    if (orb.delayBeforeCanPickup == 0) {
                        if (MinecraftForge.EVENT_BUS.post(new PlayerPickupXpEvent(player, orb))) {
                            continue;
                        }
                        world.playSound((EntityPlayer) null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
                        player.onItemPickup(orb, 1);
                        player.addExperience(orb.xpValue);
                        orb.setDead();
                    }
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            toggleEnabled(stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

//    @Override
//    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
//        if (player.isSneaking())
//            ItemNBTHelper.setBoolean(stack, "MagnetEnabled", !ItemNBTHelper.getBoolean(stack, "MagnetEnabled", false));
//        return stack;
//    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack stack, World p_77624_2_, List list, ITooltipFlag p_77624_4_) {
//        list.add(StatCollector.translateToLocal("info.de.shiftRightClickToActivate.txt"));
//        int range = stack.getItemDamage() == 0 ? 8 : 32;
//        list.add(InfoHelper.HITC() + range + InfoHelper.ITC() + " " + StatCollector.translateToLocal("info.de.blockRange.txt"));
    }

    public boolean isEnabled(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "IsActive", false);
    }

    public void toggleEnabled(ItemStack stack) {
        ItemNBTHelper.setBoolean(stack, "IsActive", !isEnabled(stack));
    }
}
