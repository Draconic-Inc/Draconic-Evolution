package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.lib.TechPropBuilder;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.ArrowDamageData;
import com.brandon3055.draconicevolution.api.modules.data.ArrowSpeedData;
import com.brandon3055.draconicevolution.api.modules.data.DrawSpeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.entity.projectile.DEArrowEntity;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.items.DEArrowItem;
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

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, toolWidth(techLevel), toolHeight(techLevel), "bow", removeInvalidModules);
        host.addCategories(ModuleCategory.BOW);
        host.addPropertyBuilder(props -> {
            props.add(new BooleanProperty("auto_fire", false).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
            props.add(new BooleanProperty("energy_arrow", false).setFormatter(ConfigProperty.BooleanFormatter.ACTIVE_INACTIVE));
            ArrowSpeedData asd = host.getModuleData(ModuleTypes.ARROW_SPEED);
            if (asd != null) {
                props.add(new DecimalProperty("arrow_speed", asd.getArrowSpeed()).min(0).max(asd.getArrowSpeed()).setFormatter(ConfigProperty.DecimalFormatter.PLUS_PERCENT_0));
            }
            ArrowDamageData add = host.getModuleData(ModuleTypes.ARROW_DAMAGE);
            if (add != null) {
                props.add(new DecimalProperty("arrow_damage", add.getArrowDamage()).range(2, add.getArrowDamage()).setFormatter(ConfigProperty.DecimalFormatter.RAW_0));
            }
            DrawSpeedData dsd = host.getModuleData(ModuleTypes.DRAW_SPEED);
            if (dsd != null) {
                props.add(new DecimalProperty("draw_speed", dsd.getDrawTimeReduction()).min(0).max(dsd.getDrawTimeReduction()).setFormatter(ConfigProperty.DecimalFormatter.PLUS_PERCENT_0));
            }
        });
        return host;
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {
        ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);

        if (entity instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)entity;
            boolean flag = playerentity.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack itemstack = playerentity.getProjectile(stack);

            boolean isEnergyArrow = false;
            if (host instanceof PropertyProvider && ((PropertyProvider) host).hasBool("energy_arrow")) {
                isEnergyArrow = ((PropertyProvider) host).getBool("energy_arrow").getValue();
            }

            if (!isEnergyArrow && (getEnergyStored(stack) - calculateEnergyCost(stack, true) >= 0) && itemstack.isEmpty()) {
                isEnergyArrow = true;
            }

            int charge = this.getUseDuration(stack) - timeLeft;
            charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, playerentity, charge, !itemstack.isEmpty() || flag || isEnergyArrow);
            if (charge < 0) return;

            if (isEnergyArrow || !itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                    isEnergyArrow = true;
                }

                float drawArrowSpeedModifier = Math.min((float)charge/getDrawTicks(stack), 1);
                if (!((double)drawArrowSpeedModifier < 0.1D)) {
                    boolean flag1 = playerentity.abilities.instabuild || (itemstack.getItem() instanceof ArrowItem && ((ArrowItem)itemstack.getItem()).isInfinite(itemstack, stack, playerentity)) || isEnergyArrow;

                    double speed = 1;
                    if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("arrow_speed")) {
                        speed += ((PropertyProvider) host).getDecimal("arrow_speed").getValue();
                    }
                    float velocity = (float) (speed * drawArrowSpeedModifier);

                    if (!world.isClientSide) {
                        DEArrowItem deArrowItem = new DEArrowItem(new Properties());
                        deArrowItem.setEnergyArrow(isEnergyArrow);
                        DEArrowEntity deArrowEntity = deArrowItem.createArrow(world, itemstack, playerentity);
                        deArrowEntity.shootFromRotation(playerentity, playerentity.xRot, playerentity.yRot, 0.0F, velocity * 3.0F, 1.0F);

                        if (drawArrowSpeedModifier >= 1.0F) {
                            deArrowEntity.setCritArrow(true);
                        }

                        int extraDamage = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                        if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("arrow_damage")) {
                            double moduleDamage = ((PropertyProvider) host).getDecimal("arrow_damage").getValue();
                            deArrowEntity.setBaseDamage(moduleDamage);
                        }
                        if (extraDamage > 0) {
                            deArrowEntity.setBaseDamage(deArrowEntity.getBaseDamage() + (double)extraDamage * 0.5D + 0.5D);
                        }

                        int knockback = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                        if (knockback > 0) {
                            deArrowEntity.setKnockback(knockback);
                        }

                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                            deArrowEntity.setSecondsOnFire(100);
                        }

                        stack.hurtAndBreak(1, playerentity, (p_220009_1_) -> p_220009_1_.broadcastBreakEvent(playerentity.getUsedItemHand()));
                        if (flag1 || playerentity.abilities.instabuild && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                            deArrowEntity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }
                        extractEnergy(playerentity, stack, calculateEnergyCost(stack, isEnergyArrow));
                        world.addFreshEntity(deArrowEntity);
                    }

                    world.playSound(null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
                    if (!flag1 && !playerentity.abilities.instabuild) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            playerentity.inventory.removeItem(itemstack);
                        }
                    }

                    playerentity.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public int getDrawTicks(ItemStack stack) {
        ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        double drawTimeReduction = 0;
        if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("draw_speed")) {
            drawTimeReduction = (float) ((PropertyProvider) host).getDecimal("draw_speed").getValue();
        }
        return (int) (20d / (1 + drawTimeReduction));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemstack = playerEntity.getItemInHand(hand);
        // TODO Werechang (Cookieso): Warn when no ammo
        ModuleHost host = itemstack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        boolean isEnergyArrow = false;
        if (host instanceof PropertyProvider && ((PropertyProvider) host).hasBool("energy_arrow")) {
            isEnergyArrow = ((PropertyProvider) host).getBool("energy_arrow").getValue();
        }
        boolean flag = !playerEntity.getProjectile(itemstack).isEmpty();

        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, world, playerEntity, hand, flag);
        if (ret != null) return ret;

        if (!playerEntity.abilities.instabuild && !flag && !isEnergyArrow) {
            if (getEnergyStored(itemstack) < calculateEnergyCost(itemstack, true)) {
                return ActionResult.fail(itemstack);
            }
        }
        playerEntity.startUsingItem(hand);
        return ActionResult.consume(itemstack);
    }

    @Override
    public void onUseTick(World world, LivingEntity player,ItemStack bow, int count) {
        // count: from 72000 (start) over 71980 (max tension) to negative
        if (getUseDuration(bow) - count >= getDrawTicks(bow)) {
            ModuleHost host = bow.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
            if (host instanceof PropertyProvider && ((PropertyProvider) host).hasBool("auto_fire")) {
                if (((PropertyProvider) host).getBool("auto_fire").getValue()) {
                    // auto fire
                    player.stopUsingItem();
                    bow.releaseUsing(world, player, 0);
                }
            }
        }
    }

    private long calculateEnergyCost(ItemStack bow, boolean isEnergyArrow) {
        long sum = 1000;

        ModuleHost host = bow.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);

        double arrowSpeed = 1;
        if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("arrow_speed")) {
            arrowSpeed += ((PropertyProvider) host).getDecimal("arrow_speed").getValue();
        }

        double arrowDamage = 1;
        if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("arrow_damage")) {
            arrowDamage = ((PropertyProvider) host).getDecimal("arrow_damage").getValue();
        }
        sum += Math.round(arrowSpeed * 10) * 100;
        sum += arrowDamage * 100;
        sum += isEnergyArrow ? 4000 : 1;
        return sum;
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
        tooltip.add(new StringTextComponent("This item requires energy modules to function").withStyle(TextFormatting.RED));
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return techLevel.index+1;
    }
}
