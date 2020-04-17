package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.entity.EntityLootCore;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;

import java.util.List;

/**
 * Created by brandon3055 on 9/3/2016.
 */
//@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class Magnet extends ItemBCore /*implements IBauble*/ {

    private final int range;

    public Magnet(Properties properties, int range) {
        super(properties);
        this.range = range;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return isEnabled(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entity, int itemSlot, boolean isSelected) {
        updateMagnet(stack, entity);
    }

    private void updateMagnet(ItemStack stack, Entity entity) {
        if (!entity.isSneaking() && entity.ticksExisted % 10 == 0 && isEnabled(stack) && entity instanceof PlayerEntity) {
            World world = entity.getEntityWorld();

            List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX, entity.posY, entity.posZ).grow(range, range, range));

            boolean flag = false;

            for (ItemEntity itemEntity : items) {
                ItemStack item = itemEntity.getItem();

                String name = item.getItem().getRegistryName().toString();
                if (!itemEntity.isAlive() || (DEConfig.itemDislocatorBlacklistMap.containsKey(name) && (DEConfig.itemDislocatorBlacklistMap.get(name) == -1/* || DEConfig.itemDislocatorBlacklistMap.get(name) == item.getItemDamage()*/))) {
                    continue;
                }

                CompoundNBT itemTag = itemEntity.getPersistentData();
                if (itemTag != null && itemTag.contains("PreventRemoteMovement")) {
                    continue;
                }

                if (itemEntity.getThrowerId() != null && itemEntity.getThrowerId().equals(entity.getUniqueID()) && itemEntity.pickupDelay > 0) {
                    continue;
                }

                PlayerEntity closest = world.getClosestPlayer(itemEntity, 4);
                if (closest != null && closest != entity) {
                    continue;
                }

                BlockPos pos = new BlockPos(itemEntity);
                boolean blocked = false;
                for (BlockPos checkPos : BlockPos.getAllInBoxMutable(pos.add(-5, -5, -5), pos.add(5, 5, 5))) {
                    if (world.getBlockState(checkPos).getBlock() == DEContent.dislocation_inhibitor) {
                        blocked = true;
                        break;
                    }
                }

                if (blocked) {
                    continue;
                }

                flag = true;

                if (!world.isRemote) {
                    if (itemEntity.pickupDelay > 0) {
                        itemEntity.pickupDelay = 0;
                    }
                    itemEntity.setMotion(0, 0, 0);
                    itemEntity.setPosition(entity.posX - 0.2 + (world.rand.nextDouble() * 0.4), entity.posY - 0.6, entity.posZ - 0.2 + (world.rand.nextDouble() * 0.4));
                }
            }

            List<EntityLootCore> cores = world.getEntitiesWithinAABB(EntityLootCore.class, new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX, entity.posY, entity.posZ).grow(range, range, range));
            for (EntityLootCore core : cores) {
                PlayerEntity closest = world.getClosestPlayer(core, 4);
                if (closest != null && closest != entity) {
                    continue;
                }

                flag = true;

                if (!world.isRemote) {
                    core.setPosition(entity.posX - 0.2 + (world.rand.nextDouble() * 0.4), entity.posY - 0.6, entity.posZ - 0.2 + (world.rand.nextDouble() * 0.4));
                }
            }

            if (flag && !DEConfig.disableDislocatorSound) {
                world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 2F));
            }

            List<ExperienceOrbEntity> xp = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX, entity.posY, entity.posZ).grow(4, 4, 4));

            PlayerEntity player = (PlayerEntity) entity;

            for (ExperienceOrbEntity orb : xp) {
                if (!world.isRemote && orb.isAlive()) {
                    if (orb.delayBeforeCanPickup == 0) {
                        if (MinecraftForge.EVENT_BUS.post(new PlayerPickupXpEvent(player, orb))) {
                            continue;
                        }
                        if (!DEConfig.disableDislocatorSound) {
                            world.playSound((PlayerEntity) null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
                        }
                        player.onItemPickup(orb, 1);
                        player.giveExperiencePoints(orb.xpValue);
                        orb.remove();
                    }
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            toggleEnabled(stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack stack, World p_77624_2_, List list, ITooltipFlag p_77624_4_) {
//        list.add(StatCollector.translateToLocal("info.de.shiftRightClickToActivate.txt"));
//        int range = stack.getItemDamage() == 0 ? 8 : 32;
//        list.add(InfoHelper.HITC() + range + InfoHelper.ITC() + " " + StatCollector.translateToLocal("info.de.blockRange.txt"));
    }

    public static boolean isEnabled(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "IsActive", false);
    }

    public static void toggleEnabled(ItemStack stack) {
        ItemNBTHelper.setBoolean(stack, "IsActive", !isEnabled(stack));
    }

//    @Optional.Method(modid = "baubles")
//    @Override
//    public BaubleType getBaubleType(ItemStack itemstack) {
//        return BaubleType.TRINKET;
//    }
//
//    @Override
//    @Optional.Method(modid = "baubles")
//    public void onWornTick(ItemStack itemstack, LivingEntity player) {
//        if (!(player instanceof PlayerEntity)) return;
//        updateMagnet(itemstack, player);
//    }
}
