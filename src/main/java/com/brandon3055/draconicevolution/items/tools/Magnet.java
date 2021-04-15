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
import net.minecraftforge.event.entity.player.PlayerXpEvent;


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
    public boolean isFoil(ItemStack stack) {
        return isEnabled(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entity, int itemSlot, boolean isSelected) {
        updateMagnet(stack, entity);
    }

    private void updateMagnet(ItemStack stack, Entity entity) {
        if (!entity.isShiftKeyDown() && isEnabled(stack) && entity instanceof PlayerEntity) {
            World world = entity.getCommandSenderWorld();
            List<ItemEntity> items;
            if (entity.tickCount % 10 == 0) {
                items = world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(entity.getX(), entity.getY(), entity.getZ(), entity.getX(), entity.getY(), entity.getZ()).inflate(range, range, range));
            } else {
                items = world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(entity.getX(), entity.getY(), entity.getZ(), entity.getX(), entity.getY(), entity.getZ()).inflate(5, 5, 5));
            }

            boolean flag = false;
            for (ItemEntity itemEntity : items) {
//                ItemStack item = itemEntity.getItem();

                //For now i think the dislocation inhibitor is a better solution that makes more sense to the user.
//                String name = item.getItem().getRegistryName().toString();
                if (!itemEntity.isAlive() /*|| (DEOldConfig.itemDislocatorBlacklistMap.containsKey(name) && (DEOldConfig.itemDislocatorBlacklistMap.get(name) == -1*//* || DEConfig.itemDislocatorBlacklistMap.get(name) == item.getItemDamage()*/) {
                    continue;
                }

                CompoundNBT itemTag = itemEntity.getPersistentData();
                if (itemTag != null && itemTag.contains("PreventRemoteMovement")) {
                    continue;
                }

                if (itemEntity.getThrower() != null && itemEntity.getThrower().equals(entity.getUUID()) && itemEntity.pickupDelay > 0) {
                    continue;
                }

                PlayerEntity closest = world.getNearestPlayer(itemEntity, 4);
                if (closest != null && closest != entity) {
                    continue;
                }

                BlockPos pos = itemEntity.blockPosition();
                boolean blocked = false;
                for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-5, -5, -5), pos.offset(5, 5, 5))) {
                    if (world.getBlockState(checkPos).getBlock() == DEContent.dislocation_inhibitor) {
                        blocked = true;
                        break;
                    }
                }

                if (blocked) {
                    continue;
                }

                if (entity.distanceToSqr(itemEntity) > 2 * 2) {
                    flag = true;
                }

                if (!world.isClientSide) {
                    if (itemEntity.pickupDelay > 0) {
                        itemEntity.pickupDelay = 0;
                    }
                    itemEntity.setDeltaMovement(0, 0, 0);
                    itemEntity.fallDistance = 0;
                    itemEntity.setPos(entity.getX() - 0.2 + (world.random.nextDouble() * 0.4), entity.getY() - 0.6, entity.getZ() - 0.2 + (world.random.nextDouble() * 0.4));
                }
            }

            //TODO When loot piles are a thing
//            List<EntityLootCore> cores = world.getEntitiesWithinAABB(EntityLootCore.class, new AxisAlignedBB(entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.getPosX(), entity.getPosY(), entity.getPosZ()).grow(range, range, range));
//            for (EntityLootCore core : cores) {
//                PlayerEntity closest = world.getClosestPlayer(core, 4);
//                if (closest != null && closest != entity) {
//                    continue;
//                }
//
//                flag = true;
//
//                if (!world.isRemote) {
//                    core.setPosition(entity.getPosX() - 0.2 + (world.rand.nextDouble() * 0.4), entity.getPosY() - 0.6, entity.getPosZ() - 0.2 + (world.rand.nextDouble() * 0.4));
//                }
//            }

            if (flag && DEConfig.itemDislocatorSound) {
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 1F + (random.nextFloat() * 0.1F));
            }

            List<ExperienceOrbEntity> xp = world.getEntitiesOfClass(ExperienceOrbEntity.class, new AxisAlignedBB(entity.getX(), entity.getY(), entity.getZ(), entity.getX(), entity.getY(), entity.getZ()).inflate(4, 4, 4));

            PlayerEntity player = (PlayerEntity) entity;

            for (ExperienceOrbEntity orb : xp) {
                if (!world.isClientSide && orb.isAlive()) {
                    if (orb.throwTime == 0) {
                        if (MinecraftForge.EVENT_BUS.post(new PlayerXpEvent.PickupXp(player, orb))) {
                            continue;
                        }
                        if (DEConfig.itemDislocatorSound) {
                            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.8F));
                        }
                        player.take(orb, 1);
                        player.giveExperiencePoints(orb.value);
                        orb.remove();
                    }
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            toggleEnabled(stack, player);
        }
        return super.use(world, player, hand);
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    @Override
    public void appendHoverText(ItemStack stack, World p_77624_2_, List list, ITooltipFlag p_77624_4_) {
//        list.add(StatCollector.translateToLocal("info.de.shiftRightClickToActivate.txt"));
//        int range = stack.getItemDamage() == 0 ? 8 : 32;
//        list.add(InfoHelper.HITC() + range + InfoHelper.ITC() + " " + StatCollector.translateToLocal("info.de.blockRange.txt"));
    }

    public static boolean isEnabled(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "IsActive", false);
    }

    public static void toggleEnabled(ItemStack stack, PlayerEntity player) {
        ItemNBTHelper.setBoolean(stack, "IsActive", !isEnabled(stack));
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, isEnabled(stack) ? 1F : 0.5F);
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
