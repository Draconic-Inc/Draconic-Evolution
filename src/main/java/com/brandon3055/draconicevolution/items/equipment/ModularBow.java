package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.lib.TechPropBuilder;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.AOEData;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.data.ProjectileData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.entity.projectile.DraconicArrowEntity;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
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
import net.minecraft.util.text.TranslationTextComponent;
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
        host.addPropertyBuilder(props -> props.add(new BooleanProperty("auto_fire", false).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED)));
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
    public void onUseTick(World world, LivingEntity player, ItemStack stack, int count) {
        // count: from 72000 (start) over 71980 (max tension) to negative
        if (getUseDuration(stack) - count >= getChargeTicks(stack)) {
            ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
            if (host instanceof PropertyProvider && ((PropertyProvider) host).hasBool("auto_fire")) {
                if (((PropertyProvider) host).getBool("auto_fire").getValue()) {
                    // auto fire
                    player.stopUsingItem();
                    stack.releaseUsing(world, player, 0);
                }
            }
        }
//        int drawTime = (this.getUseDuration(stack) - count) + 1;
//        if (drawTime == getChargeTicks(stack) * 2 && player.level.isClientSide) {
//            player.level.playLocalSound(player.getX(), player.getY(), player.getZ(), DESounds.bowSecondCharge, SoundCategory.PLAYERS, 1.0F, 1.F, false);
//        }
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean hasAmmo = !player.getProjectile(stack).isEmpty();

        ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(stack, world, player, hand, hasAmmo);
        if (ret != null) return ret;

        if (EnergyUtils.getEnergyStored(stack) < calculateShotEnergy(stack)) {
            hasAmmo = false;
        }

        if (!player.abilities.instabuild && !hasAmmo) {
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

                ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
                ProjectileData projData = host.getModuleData(ModuleTypes.PROJ_MODIFIER, new ProjectileData(0, 0, 0, 0, 0));

                float powerForTime = getPowerForTime(drawTime, stack) * (projData.getVelocity() + 1);
                if (powerForTime >= 0.1D) {
                    boolean infiniteAmmo = player.abilities.instabuild || (ammoStack.getItem() instanceof ArrowItem && ((ArrowItem)ammoStack.getItem()).isInfinite(ammoStack, stack, player));

                    if (!world.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem)(ammoStack.getItem() instanceof ArrowItem ? ammoStack.getItem() : Items.ARROW);
                        DraconicArrowEntity arrowEntity = customArrow(arrowitem.createArrow(world, ammoStack, player));
                        arrowEntity.setEffectsFromItem(ammoStack);
                        arrowEntity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, powerForTime * 3.0F, 1 - projData.getAccuracy());
                        arrowEntity.setTechLevel(techLevel);
                        arrowEntity.setPenetration(projData.getPenetration());
                        arrowEntity.setGravComp(projData.getAntiGrav());

                        if (powerForTime == 1.0F) {
                            arrowEntity.setCritArrow(true);
                        }

                        arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() * (projData.getDamage() + 1));

                        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                        if (j > 0) {
                            arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (double)j * 0.5D + 0.5D);
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
                            arrowEntity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }

                        world.addFreshEntity(arrowEntity);
                    }

                    world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + powerForTime * 0.5F);
                    if (!infiniteAmmo && !player.abilities.instabuild) {
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

    @Override
    public DraconicArrowEntity customArrow(AbstractArrowEntity arrow) {
        DraconicArrowEntity newArrow = new DraconicArrowEntity(arrow.level, (LivingEntity) arrow.getOwner());
        if (arrow instanceof SpectralArrowEntity) {
            newArrow.setSpectral(((SpectralArrowEntity) arrow).duration);
        }
        return newArrow;
    }

    public static float calculateDamage(ItemStack stack) {
        ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        ProjectileData projData = host.getModuleData(ModuleTypes.PROJ_MODIFIER, new ProjectileData(0, 0, 0, 0, 0));

        float baseDamage = 2;
        baseDamage *= (1 + projData.getDamage());
        baseDamage *= (3 * (1 + projData.getVelocity()));
        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
        if (j > 0) {
            baseDamage += (double)j * 0.5D + 0.5D;
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
        float power = (float)time / fullChargeTime;
        power = ((power * power) + (power * 2.0F)) / 3.0F;
        if (power > 1.0F) {
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

    @Override
    public void addModularItemInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IModularItem.super.addModularItemInformation(stack, worldIn, tooltip, flagIn);
        if (worldIn != null){
            tooltip.add(new TranslationTextComponent("tooltip.draconicevolution.bow.damage", Math.round(calculateDamage(stack) * 10) / 10F).withStyle(TextFormatting.DARK_GREEN));
            tooltip.add(new TranslationTextComponent("tooltip.draconicevolution.bow.energy_per_shot", Utils.addCommas(calculateShotEnergy(stack))).withStyle(TextFormatting.DARK_GREEN));
        }
    }
}
