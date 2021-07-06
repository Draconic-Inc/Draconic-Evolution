package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.lib.TechPropBuilder;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

import java.util.List;

import static com.brandon3055.draconicevolution.api.capability.DECapabilities.MODULE_HOST_CAPABILITY;
import static com.brandon3055.draconicevolution.init.ModuleCfg.*;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularBow extends BowItem implements IReaperItem, IModularItem {
    private final TechLevel techLevel;

    public ModularBow(TechPropBuilder props) {
        super(props.build().fireResistant());
        this.techLevel = props.techLevel;
    }

    //###### Modular Item Stuff ######

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, toolWidth(techLevel), toolHeight(techLevel), "bow", removeInvalidModules);
        host.addCategories(ModuleCategory.RANGED_WEAPON);
        return host;
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        return new ModularOPStorage(host, EquipCfg.getBaseToolEnergy(techLevel), EquipCfg.getBaseToolTransfer(techLevel));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return 0;
    }

    //###### Draw & Charge time stuff ######


    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        int drawTime = (this.getUseDuration(stack) - count) + 1;
        if (drawTime == getChargeTicks(stack) * 2 && player.level.isClientSide) {
            player.level.playLocalSound(player.getX(), player.getY(), player.getZ(), DESounds.bowSecondCharge, SoundCategory.PLAYERS, 1.0F, 1.F, false);
            DraconicEvolution.LOGGER.info("SoundA " + System.currentTimeMillis());
        }
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean flag = !player.getProjectile(stack).isEmpty();

        ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(stack, world, player, hand, flag);
        if (ret != null) return ret;

        if (!player.abilities.instabuild && !flag) {
            return ActionResult.fail(stack);
        } else {
            player.startUsingItem(hand);
            return ActionResult.consume(stack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            boolean noAmmoRequired = player.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack ammoStack = player.getProjectile(stack);

            int drawTime = this.getUseDuration(stack) - timeLeft;
            drawTime = ForgeEventFactory.onArrowLoose(stack, world, player, drawTime, !ammoStack.isEmpty() || noAmmoRequired);
            if (drawTime < 0) return;

            if (!ammoStack.isEmpty() || noAmmoRequired) {
                if (ammoStack.isEmpty()) {
                    ammoStack = new ItemStack(Items.ARROW);
                }

                float powerForTime = getPowerForTime(drawTime, stack);
                if (!((double)powerForTime < 0.1D)) {
                    boolean flag1 = player.abilities.instabuild || (ammoStack.getItem() instanceof ArrowItem && ((ArrowItem)ammoStack.getItem()).isInfinite(ammoStack, stack, player));
                    if (!world.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem)(ammoStack.getItem() instanceof ArrowItem ? ammoStack.getItem() : Items.ARROW);
                        AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(world, ammoStack, player);
                        abstractarrowentity = customArrow(abstractarrowentity);
                        abstractarrowentity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, powerForTime * 3.0F, 1.0F);
                        if (powerForTime == 1.0F) {
                            abstractarrowentity.setCritArrow(true);
                        }

                        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                        if (j > 0) {
                            abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + (double)j * 0.5D + 0.5D);
                        }

                        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                        if (k > 0) {
                            abstractarrowentity.setKnockback(k);
                        }

                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                            abstractarrowentity.setSecondsOnFire(100);
                        }

                        stack.hurtAndBreak(1, player, (p_220009_1_) -> {
                            p_220009_1_.broadcastBreakEvent(player.getUsedItemHand());
                        });
                        if (flag1 || player.abilities.instabuild && (ammoStack.getItem() == Items.SPECTRAL_ARROW || ammoStack.getItem() == Items.TIPPED_ARROW)) {
                            abstractarrowentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }

                        world.addFreshEntity(abstractarrowentity);
                    }

                    world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + powerForTime * 0.5F);
                    if (!flag1 && !player.abilities.instabuild) {
                        ammoStack.shrink(1);
                        if (ammoStack.isEmpty()) {
                            player.inventory.removeItem(ammoStack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public static float getPowerForTime(int time, ItemStack stack) {
        float fullChargeTime = getChargeTicks(stack);
        float power = (float)time / fullChargeTime;
        power = ((power * power) + (power * 2.0F)) / 3.0F;
        if (power > 1.0F) {
//            if (time >= fullChargeTime * 2) {
//                DraconicEvolution.LOGGER.info("SoundB " + System.currentTimeMillis());
//                return 1.05F;
//            }
            power = 1.0F;
        }
        return power;
    }

    public static int getChargeTicks(ItemStack stack) {
        ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        SpeedData data = host.getModuleData(ModuleTypes.SPEED);
        float speedModifier = data == null ? 0 : (float) data.getSpeedMultiplier();
        speedModifier++;
        return (int)Math.ceil(20.0F / speedModifier);
    }

}
