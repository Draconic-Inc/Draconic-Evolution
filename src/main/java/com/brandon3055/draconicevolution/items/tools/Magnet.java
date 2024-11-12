package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

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
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entity, int itemSlot, boolean isSelected) {
        updateMagnet(stack, entity);
    }

    private void updateMagnet(ItemStack stack, Entity entity) {
        if (!entity.isShiftKeyDown() && isEnabled(stack) && entity instanceof Player player) {
            Level world = entity.getCommandSenderWorld();
            List<ItemEntity> items;
            if (entity.tickCount % 10 == 0) {
                items = world.getEntitiesOfClass(ItemEntity.class, new AABB(entity.getX(), entity.getY(), entity.getZ(), entity.getX(), entity.getY(), entity.getZ()).inflate(range, range, range));
            } else {
                items = world.getEntitiesOfClass(ItemEntity.class, new AABB(entity.getX(), entity.getY(), entity.getZ(), entity.getX(), entity.getY(), entity.getZ()).inflate(5, 5, 5));
            }

            boolean flag = false;
            for (ItemEntity itemEntity : items) {
//                ItemStack item = itemEntity.getItem();

                //For now i think the dislocation inhibitor is a better solution that makes more sense to the user.
//                String name = item.getItem().getRegistryName().toString();
                if (!itemEntity.isAlive() /*|| (DEOldConfig.itemDislocatorBlacklistMap.containsKey(name) && (DEOldConfig.itemDislocatorBlacklistMap.get(name) == -1*//* || DEConfig.itemDislocatorBlacklistMap.get(name) == item.getItemDamage()*/) {
                    continue;
                }

                CompoundTag itemTag = itemEntity.getPersistentData();
                if (itemTag != null && itemTag.contains("PreventRemoteMovement")) {
                    continue;
                }

                if (itemEntity.getOwner() != null && itemEntity.getOwner().equals(entity.getUUID()) && itemEntity.pickupDelay > 0) {
                    continue;
                }

                Player closest = world.getNearestPlayer(itemEntity, 4);
                if (closest != null && closest != entity) {
                    continue;
                }

                BlockPos pos = itemEntity.blockPosition();
                boolean blocked = false;
                for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-5, -5, -5), pos.offset(5, 5, 5))) {
                    if (world.getBlockState(checkPos).getBlock() == DEContent.DISLOCATION_INHIBITOR.get()) {
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
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 1F + (world.random.nextFloat() * 0.1F));
            }

            List<ExperienceOrb> xp = world.getEntitiesOfClass(ExperienceOrb.class, new AABB(entity.getX(), entity.getY(), entity.getZ(), entity.getX(), entity.getY(), entity.getZ()).inflate(4, 4, 4));

            for (ExperienceOrb orb : xp) {
                if (!world.isClientSide && orb.isAlive()) {
                    PlayerXpEvent.PickupXp event = NeoForge.EVENT_BUS.post(new PlayerXpEvent.PickupXp(player, orb));
                    if (event.isCanceled()) {
                        continue;
                    }
                    if (DEConfig.itemDislocatorSound) {
                        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.8F));
                    }
                    player.take(orb, 1);
                    player.giveExperiencePoints(orb.value);
                    orb.discard();
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            toggleEnabled(stack, player);
        }
        return super.use(world, player, hand);
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    @Override
    public void appendHoverText(ItemStack stack, Level p_77624_2_, List list, TooltipFlag p_77624_4_) {
//        list.add(StatCollector.translateToLocal("info.de.shiftRightClickToActivate.txt"));
//        int range = stack.getItemDamage() == 0 ? 8 : 32;
//        list.add(InfoHelper.HITC() + range + InfoHelper.ITC() + " " + StatCollector.translateToLocal("info.de.blockRange.txt"));
    }

    public static boolean isEnabled(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "IsActive", false);
    }

    public static void toggleEnabled(ItemStack stack, Player player) {
        ItemNBTHelper.setBoolean(stack, "IsActive", !isEnabled(stack));
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, isEnabled(stack) ? 1F : 0.5F);
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
