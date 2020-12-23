package com.brandon3055.draconicevolution.items.equipment;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.brandonscore.capability.MultiCapabilityProvider;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.BooleanFormatter;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter;
import com.brandon3055.draconicevolution.api.config.ConfigProperty.IntegerFormatter;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.config.IntegerProperty;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.AOEData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.entity.PersistentItemEntity;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.brandon3055.draconicevolution.api.capability.DECapabilities.*;

/**
 * Created by brandon3055 on 16/6/20
 */
public interface IModularItem extends IForgeItem {

    TechLevel getTechLevel();

    @Override
    default MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        MultiCapabilityProvider provider = new MultiCapabilityProvider();
        ModuleHostImpl host = createHost(stack);
        provider.addCapability(host, "module_host", MODULE_HOST_CAPABILITY, PROPERTY_PROVIDER_CAPABILITY);
        ModularOPStorage opStorage = createOPStorage(stack, host);
        if (opStorage != null) {
            provider.addCapability(opStorage, "energy", OP_STORAGE, CapabilityEnergy.ENERGY);
            host.addCategories(ModuleCategory.ENERGY);
        }

        if (this instanceof IModularMiningTool) {
            host.addCategories(ModuleCategory.MINING_TOOL);
            host.addPropertyBuilder(props -> {
                props.add(new DecimalProperty("mining_speed", 1).range(0, 1).setFormatter(DecimalFormatter.PERCENT_1));
                AOEData aoe = host.getModuleData(ModuleTypes.AOE);
                if (aoe != null) {
                    props.add(new IntegerProperty("mining_aoe", aoe.getAOE()).range(0, aoe.getAOE()).setFormatter(IntegerFormatter.AOE));
                    props.add(new BooleanProperty("aoe_safe", false).setFormatter(BooleanFormatter.ENABLED_DISABLED));
                }
            });
        }

        if (this instanceof IModularMelee) {
            host.addCategories(ModuleCategory.MELEE_WEAPON);
            host.addPropertyBuilder(props -> {
                AOEData aoe = host.getModuleData(ModuleTypes.AOE);
                if (aoe != null) {
                    props.add(new DecimalProperty("attack_aoe", aoe.getAOE() * 1.5).range(0, aoe.getAOE() * 1.5).setFormatter(DecimalFormatter.AOE_1));
                }
            });
        }

        initCapabilities(stack, host, provider);
        return provider;
    }

    default void initCapabilities(ItemStack stack, ModuleHostImpl host, MultiCapabilityProvider provider) {}

    ModuleHostImpl createHost(ItemStack stack);

    @Nullable
    ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host);

    @OnlyIn(Dist.CLIENT)
    default void addModularItemInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("[Modular Item]").mergeStyle(TextFormatting.BLUE));
        }
        EnergyUtils.addEnergyInfo(stack, tooltip);
    }

    @Override
    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        if (MODULE_HOST_CAPABILITY != null && stack.getCapability(MODULE_HOST_CAPABILITY).isPresent()) { //Because vanilla calls this before capabilities are registered.
            ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
            host.getAttributeModifiers(slot, stack, map);
        }
        return map;
    }

    default void handleTick(ItemStack stack, LivingEntity entity, @Nullable EquipmentSlotType slot) {
        ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        StackModuleContext context = new StackModuleContext(stack, entity, slot);
        host.handleTick(context);
    }

    /**
     * This is used to determine if a modular item is in a valid slot for its modules to operate.
     * //TODO this is not currently implemented by some modules such as the shield modules
     *
     * @param stack        The stack
     * @param slot         The equipment slot or null if this item is in the players general main inventory.
     * @param inBaubleSlot //TODO Bauble support
     * @return true if this stack is in a valid slot.
     */
    default boolean isEquipped(ItemStack stack, @Nullable EquipmentSlotType slot, boolean inBaubleSlot) {
        if (this instanceof IModularArmor) return slot != null && slot.getSlotType() == EquipmentSlotType.Group.ARMOR;
        return true;
    }

    default float getDestroySpeed(ItemStack stack, BlockState state) {
        ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        SpeedData data = host.getModuleData(ModuleTypes.SPEED);
        float moduleValue = data == null ? 0 : (float) data.getSpeedMultiplier();
        //The way vanilla handles efficiency is kinda dumb. So this is far from perfect but its kinda close... ish.
        float multiplier = MathHelper.map((moduleValue + 1F) * (moduleValue + 1F), 1F, 2F, 1F, 1.65F);
        float propVal = 1F;
        //Module host should always be a property provider because it needs to provide module properties.
        if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("mining_speed")) {
            propVal = (float) ((PropertyProvider) host).getDecimal("mining_speed").getValue();
            propVal *= propVal; //Make this exponential
        }

        float aoe = host.getModuleData(ModuleTypes.AOE, new AOEData(0)).getAOE();
        if (host instanceof PropertyProvider && ((PropertyProvider) host).hasInt("mining_aoe")) {
            aoe = ((PropertyProvider) host).getInt("mining_aoe").getValue();
        }

        if (getEnergyStored(stack) < EquipCfg.energyHarvest) {
            multiplier = 0;
        } else if (aoe > 0) {
            float userTarget = multiplier * propVal;
            multiplier = Math.min(userTarget, multiplier / (1 + (aoe * 10)));
        } else {
            multiplier *= propVal;
        }

        if (isToolEffective(stack, state) && (multiplier > 0 || propVal == 0)) {
            return getBaseEfficiency() * multiplier;
        } else {
            return propVal == 0 ? 0 : 1F;
        }
    }

    default boolean isToolEffective(ItemStack stack, BlockState state) {
        return getToolTypes(stack).stream().anyMatch(state::isToolEffective) || overrideEffectivity(state.getMaterial()) || effectiveBlockAdditions().contains(state.getBlock());
    }

    default float getBaseEfficiency() {
        return 1F;
    }

    /**
     * Returns a list of "additional blocks" that this tool is effective against. This probably isn't required but vanilla does it so why not!
     * This overrides the default {@link Block#isToolEffective(BlockState, ToolType)} check.
     */
    default Set<Block> effectiveBlockAdditions() {
        return Collections.emptySet();
    }

    /**
     * I use this for things like allowing the pickaxe to mine glass at full speed.
     * And the staff which can mine anything at full speed
     * This overrides the default {@link Block#isToolEffective(BlockState, ToolType)} check.
     */
    default boolean overrideEffectivity(Material material) {
        return false;
    }

    @Nullable
    @Override
    default CompoundNBT getShareTag(ItemStack stack) {
        return DECapabilities.writeToShareTag(stack, stack.getTag());
    }

    @Override
    default void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        stack.setTag(nbt);
        DECapabilities.readFromShareTag(stack, nbt);
    }

    default long getEnergyStored(ItemStack stack) {
        return EnergyUtils.getEnergyStored(stack);
    }

    default long extractEnergy(PlayerEntity player, ItemStack stack, long amount) {
        if (player != null && player.abilities.isCreativeMode) {
            return amount;
        }
        IOPStorage storage = EnergyUtils.getStorage(stack);
        if (storage instanceof IOPStorageModifiable) {
            return ((IOPStorageModifiable) storage).modifyEnergyStored(-amount);
        } else if (storage != null) {
            return storage.extractOP(amount, false);
        }
        return 0;
    }

    @Override
    default boolean showDurabilityBar(ItemStack stack) {
        long max = EnergyUtils.getMaxEnergyStored(stack);
        return max > 0 && EnergyUtils.getEnergyStored(stack) < max;
    }

    @Override
    default double getDurabilityForDisplay(ItemStack stack) {
        return 1D - ((double) EnergyUtils.getEnergyStored(stack) / EnergyUtils.getMaxEnergyStored(stack));
    }

    @Override
    default boolean hasCustomEntity(ItemStack stack) {
        return true; //Waiting for forge to not be broken
    }

    @Nullable
    @Override
    default Entity createEntity(World world, Entity location, ItemStack itemstack) {
//        return new PersistentItemEntity(world, location, itemstack);
        if (location instanceof ItemEntity) {
            ((ItemEntity) location).age = -32767; //extra 27 minute despawn delay
        }
        return null;
    }
}
