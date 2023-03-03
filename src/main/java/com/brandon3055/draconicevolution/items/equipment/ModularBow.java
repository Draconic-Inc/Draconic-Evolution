package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.ProjectileData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.entities.AutoFireEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.entity.projectile.DraconicArrowEntity;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.brandon3055.draconicevolution.init.TechProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularBow extends BowItem implements IReaperItem, IModularItem {
    private final TechLevel techLevel;

    public ModularBow(TechProperties props) {
        super(props);
        this.techLevel = props.getTechLevel();
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, ModuleCfg.toolWidth(techLevel), ModuleCfg.toolHeight(techLevel), "bow", ModuleCfg.removeInvalidModules);
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return techLevel.index;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (getUseDuration(stack) - count >= getChargeTicks(stack)) {
            AutoFireEntity entity = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new).getEntitiesByType(ModuleTypes.AUTO_FIRE).map(e -> (AutoFireEntity) e).findAny().orElse(null);
            if (entity != null && entity.getAutoFireEnabled()) {
                // auto fire
                InteractionHand usingHand = player.getUsedItemHand();
                player.stopUsingItem();
                stack.releaseUsing(player.level, player, 0);
                player.startUsingItem(usingHand);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean hasAmmo = !player.getProjectile(stack).isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;

        InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onArrowNock(stack, world, player, hand, hasAmmo);
        if (ret != null) return ret;

        if (EnergyUtils.getEnergyStored(stack) < calculateShotEnergy(stack)) {
            hasAmmo = false;
        }

        if (!player.getAbilities().instabuild && !hasAmmo) {
            return InteractionResultHolder.fail(stack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            boolean noAmmoRequired = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack ammoStack = player.getProjectile(stack);

            int drawTime = this.getUseDuration(stack) - timeLeft;
            drawTime = ForgeEventFactory.onArrowLoose(stack, level, player, drawTime, !ammoStack.isEmpty() || noAmmoRequired);
            if (drawTime < 0) return;

            if (!ammoStack.isEmpty() || noAmmoRequired) {
                if (ammoStack.isEmpty()) {
                    ammoStack = new ItemStack(Items.ARROW);
                }

                ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
                ProjectileData projData = host.getModuleData(ModuleTypes.PROJ_MODIFIER, new ProjectileData(0, 0, 0, 0, 0));

                float powerForTime = getPowerForTime(drawTime, stack) * (projData.velocity() + 1);
                if (powerForTime >= 0.1D) {
                    boolean infiniteAmmo = player.getAbilities().instabuild || (ammoStack.getItem() instanceof ArrowItem && ((ArrowItem) ammoStack.getItem()).isInfinite(ammoStack, stack, player));

                    if (!level.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (ammoStack.getItem() instanceof ArrowItem ? ammoStack.getItem() : Items.ARROW);
                        AbstractArrow arrowEntity = customArrow(arrowitem.createArrow(level, ammoStack, player));
                        if (arrowEntity instanceof Arrow) {
                            ((Arrow) arrowEntity).setEffectsFromItem(ammoStack);
                        } else if (arrowEntity instanceof DraconicArrowEntity) {
                            ((DraconicArrowEntity) arrowEntity).setEffectsFromItem(ammoStack);
                        }
                        arrowEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, powerForTime * 3.0F, 1 - projData.accuracy());
                        if (arrowEntity instanceof DraconicArrowEntity) {
                            DraconicArrowEntity deArrow = (DraconicArrowEntity) arrowEntity;
                            deArrow.setTechLevel(techLevel);
                            deArrow.setPenetration(projData.penetration());
                            deArrow.setGravComp(projData.antiGrav());

                            if (host.getEntitiesByType(ModuleTypes.PROJ_ANTI_IMMUNE).findAny().isPresent()) {
                                deArrow.setProjectileImmuneOverride(true);
                            }
                        }

                        if (powerForTime == 1.0F) {
                            arrowEntity.setCritArrow(true);
                        }

                        arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() * (projData.damage() + 1));

                        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                        if (j > 0) {
                            arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (double) j * 0.5D + 0.5D);
                        }

                        long energyRequired = (long) (EquipCfg.bowBaseEnergy * arrowEntity.getBaseDamage() * powerForTime * 3);
                        if (extractEnergy(player, stack, energyRequired) < energyRequired) {
                            return;
                        }

                        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                        if (k > 0) {
                            arrowEntity.setKnockback(k);
                        }

                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                            arrowEntity.setSecondsOnFire(100);
                        }

                        if (infiniteAmmo /*|| (player.abilities.instabuild && ((ammoStack.getItem() == Items.SPECTRAL_ARROW) || (ammoStack.getItem() == Items.TIPPED_ARROW))) <Unreachable>*/) {
                            arrowEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        level.addFreshEntity(arrowEntity);
                    }

                    level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.random.nextFloat() * 0.4F + 1.2F) + powerForTime * 0.5F);
                    if (!infiniteAmmo && !player.getAbilities().instabuild) {
                        ammoStack.shrink(1);
                        if (ammoStack.isEmpty()) {
                            player.getInventory().removeItem(ammoStack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow) {
        if (arrow.getType() != EntityType.ARROW && arrow.getType() != EntityType.SPECTRAL_ARROW) {
            return arrow;
        }

        Entity owner = arrow.getOwner();
        if (!(owner instanceof LivingEntity)) { //Because it seems there is an edge case where owner may be null hear.
            return new DraconicArrowEntity(DEContent.draconicArrow, arrow.level);
        }
        DraconicArrowEntity newArrow = new DraconicArrowEntity(arrow.level, (LivingEntity) arrow.getOwner());
        if (arrow instanceof SpectralArrow) {
            newArrow.setSpectral(((SpectralArrow) arrow).duration);
        }
        return newArrow;
    }

    public static float calculateDamage(ItemStack stack) {
        ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        ProjectileData projData = host.getModuleData(ModuleTypes.PROJ_MODIFIER, new ProjectileData(0, 0, 0, 0, 0));

        float baseDamage = 2;
        baseDamage *= (1 + projData.damage());
        baseDamage *= (3 * (1 + projData.velocity()));
        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
        if (j > 0) {
            baseDamage += (double) j * 0.5D + 0.5D;
        }
        return baseDamage;
    }

    public static long calculateShotEnergy(ItemStack stack) {
        float damage = calculateDamage(stack);
        //TODO add some energy usage for other modules
        return (long) (damage * EquipCfg.bowBaseEnergy);
    }

    public static float getPowerForTime(int time, ItemStack stack) {
        float fullChargeTime = getChargeTicks(stack);
        float power = (float) time / fullChargeTime;
        power = ((power * power) + (power * 2.0F)) / 3.0F;
        if (power > 1.0F) {
            power = 1.0F;
        }
        return power;
    }

    public static int getChargeTicks(ItemStack stack) {
        ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        SpeedData data = host.getModuleData(ModuleTypes.SPEED);
        float speedModifier = data == null ? 0 : (float) data.speedMultiplier();
        speedModifier++;
        return (int) Math.ceil(20.0F / speedModifier);
    }

    @Override
    public void addModularItemInformation(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        IModularItem.super.addModularItemInformation(stack, worldIn, tooltip, flagIn);
        if (worldIn != null && stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).isPresent()) {
            tooltip.add(new TranslatableComponent("tooltip.draconicevolution.bow.damage", Math.round(calculateDamage(stack) * 10) / 10F).withStyle(ChatFormatting.DARK_GREEN));
            tooltip.add(new TranslatableComponent("tooltip.draconicevolution.bow.energy_per_shot", Utils.addCommas(calculateShotEnergy(stack))).withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return damageBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return damageBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return damageBarColour(stack);
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source == DamageSource.OUT_OF_WORLD;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public boolean isEnchantable(ItemStack p_41456_) {
        return true;
    }
}
