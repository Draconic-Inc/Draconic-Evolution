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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularBow extends BowItem implements IReaperItem, IModularEnergyItem {
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
    public @NotNull ModuleHostImpl instantiateHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, ModuleCfg.toolWidth(techLevel), ModuleCfg.toolHeight(techLevel), "bow", ModuleCfg.removeInvalidModules);
        host.addCategories(ModuleCategory.RANGED_WEAPON);
        return host;
    }

    @Override
    public @NotNull ModularOPStorage instantiateOPStorage(ItemStack stack, Supplier<ModuleHost> hostSupplier) {
        return new ModularOPStorage(hostSupplier, EquipCfg.getBaseToolEnergy(techLevel), EquipCfg.getBaseToolTransfer(techLevel));
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        addModularItemInformation(stack, context, tooltip, flagIn);
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return techLevel.index;
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity player, ItemStack stack, int count) {
        if (getUseDuration(stack, player) - count >= getChargeTicks(stack, player.registryAccess())) {
            AutoFireEntity entity = DECapabilities.getHost(stack).getEntitiesByType(ModuleTypes.AUTO_FIRE).map(e -> (AutoFireEntity) e).findAny().orElse(null);
            if (entity != null && entity.getAutoFireEnabled()) {
                // auto fire
                InteractionHand usingHand = player.getUsedItemHand();
                player.stopUsingItem();
                stack.releaseUsing(player.level(), player, 0);
                player.startUsingItem(usingHand);
            }
        }
    }

    private boolean hasInfinity(Level level, ItemStack stack) {
        Registry<Enchantment> reg = level.registryAccess().registry(Registries.ENCHANTMENT).orElse(null);
        if (reg != null) {
            Holder<Enchantment> holder = reg.getHolder(Enchantments.INFINITY).orElse(null);
            return holder != null && stack.getEnchantmentLevel(holder) > 0;
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bowStack = player.getItemInHand(hand);
        boolean infinity = hasInfinity(level, bowStack);
        boolean hasAmmo = infinity || !player.getProjectile(bowStack).isEmpty();

        InteractionResultHolder<ItemStack> ret = net.neoforged.neoforge.event.EventHooks.onArrowNock(bowStack, level, player, hand, hasAmmo);
        if (ret != null) return ret;

        if (EnergyUtils.getEnergyStored(bowStack) < calculateShotEnergy(bowStack, player.registryAccess())) {
            hasAmmo = false;
        }

        if (!player.hasInfiniteMaterials() && !hasAmmo) {
            return InteractionResultHolder.fail(bowStack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(bowStack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return;
        }

        boolean infinity = hasInfinity(level, stack);
        ItemStack ammoStack = player.getProjectile(stack);
        if (ammoStack.isEmpty()) {
            if (infinity) {
                ammoStack = new ItemStack(Items.ARROW);
            } else {
                return;
            }
        }

        int drawTime = this.getUseDuration(stack, entity) - timeLeft;
        drawTime = EventHooks.onArrowLoose(stack, level, player, drawTime, !ammoStack.isEmpty() || infinity);
        if (drawTime < 0) {
            return;
        }

        if (ammoStack.isEmpty() && !infinity) {
            return;
        }

        float powerForTime;
        boolean infiniteAmmo;
        try (ModuleHost host = DECapabilities.getHost(stack)) {
            ProjectileData projData = host.getModuleData(ModuleTypes.PROJ_MODIFIER, new ProjectileData(0, 0, 0, 0, 0));

            powerForTime = getPowerForTime(drawTime, stack, player.registryAccess()) * (projData.velocity() + 1);
            if (!(powerForTime >= 0.1D)) {
                return;
            }

            infiniteAmmo = infinity || player.getAbilities().instabuild || (ammoStack.getItem() instanceof ArrowItem && ((ArrowItem) ammoStack.getItem()).isInfinite(ammoStack, stack, player));

            if (!level.isClientSide) {
                ArrowItem arrowitem = (ArrowItem) (ammoStack.getItem() instanceof ArrowItem ? ammoStack.getItem() : Items.ARROW);
                AbstractArrow arrowEntity = customArrow(arrowitem.createArrow(level, ammoStack, player, stack), ammoStack, stack);
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

                long energyRequired = (long) (EquipCfg.bowBaseEnergy * arrowEntity.getBaseDamage() * powerForTime * 3);
                if (extractEnergy(player, stack, energyRequired) < energyRequired) {
                    return;
                }

                if (infiniteAmmo) {
                    arrowEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }

                level.addFreshEntity(arrowEntity);
            }
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

    @Override
    protected void shoot(ServerLevel level, LivingEntity entity, InteractionHand hand, ItemStack bow, List<ItemStack> ammoStacks, float power3, float f, boolean fullPower, @Nullable LivingEntity someEntity) {
        super.shoot(level, entity, hand, bow, ammoStacks, power3, f, fullPower, someEntity);
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack stack, ItemStack weaponStack) {
        if (arrow.getType() != EntityType.ARROW && arrow.getType() != EntityType.SPECTRAL_ARROW) {
            return arrow;
        }

        Entity owner = arrow.getOwner();
        if (!(owner instanceof LivingEntity)) { //Because it seems there is an edge case where owner may be null hear.
            return new DraconicArrowEntity(arrow.level(), arrow.getX(), arrow.getY(), arrow.getZ(), stack.copyWithCount(1), weaponStack);
        }
        DraconicArrowEntity newArrow = new DraconicArrowEntity(arrow.level(), (LivingEntity) arrow.getOwner(), stack.copyWithCount(1), weaponStack);
        if (arrow instanceof SpectralArrow) {
            newArrow.setSpectral(((SpectralArrow) arrow).duration);
        }
        return newArrow;
    }

    public static float calculateDamage(ItemStack stack, HolderLookup.Provider provider) {
        try (ModuleHost host = DECapabilities.getHost(stack)) {
            ProjectileData projData = host.getModuleData(ModuleTypes.PROJ_MODIFIER, new ProjectileData(0, 0, 0, 0, 0));
            float baseDamage = 2;
            baseDamage *= (1 + projData.damage());
            baseDamage *= (3 * (1 + projData.velocity()));
            return baseDamage;
        }
    }

    public static long calculateShotEnergy(ItemStack stack, HolderLookup.Provider provider) {
        float damage = calculateDamage(stack, provider);
        //TODO add some energy usage for other modules
        return (long) (damage * EquipCfg.bowBaseEnergy);
    }

    public static float getPowerForTime(int time, ItemStack stack, HolderLookup.Provider provider) {
        float fullChargeTime = getChargeTicks(stack, provider);
        float power = (float) time / fullChargeTime;
        power = ((power * power) + (power * 2.0F)) / 3.0F;
        if (power > 1.0F) {
            power = 1.0F;
        }
        return power;
    }

    public static int getChargeTicks(ItemStack stack, HolderLookup.Provider provider) {
        ModuleHost host = DECapabilities.getHost(stack);
        SpeedData data = host.getModuleData(ModuleTypes.SPEED);
        float speedModifier = data == null ? 0 : (float) data.speedMultiplier();
        speedModifier++;
        return (int) Math.ceil(20.0F / speedModifier);
    }

    @Override
    public void addModularItemInformation(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        IModularEnergyItem.super.addModularItemInformation(stack, context, tooltip, flagIn);
        if (context.level() != null && stack.getCapability(DECapabilities.Host.ITEM) != null) {
            tooltip.add(Component.translatable("tooltip.draconicevolution.bow.damage", Math.round(calculateDamage(stack, context.level().registryAccess()) * 10) / 10F).withStyle(ChatFormatting.DARK_GREEN));
            tooltip.add(Component.translatable("tooltip.draconicevolution.bow.energy_per_shot", Utils.addCommas(calculateShotEnergy(stack, context.level().registryAccess()))).withStyle(ChatFormatting.DARK_GREEN));
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
    public boolean canBeHurtBy(ItemStack stack, DamageSource source) {
        return source.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0 && entity.pickupDelay != 32767) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public boolean isEnchantable(ItemStack p_41456_) {
        return true;
    }
}
