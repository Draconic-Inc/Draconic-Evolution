package com.brandon3055.draconicevolution.items.equipment;

import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.MultiCapabilityProvider;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.data.JumpData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.api.modules.lib.StackTickContext;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
            provider.addCapability(opStorage, "energy", OP_STORAGE);
            host.addCategories(ModuleCategory.ENERGY);
        }

        if (this instanceof IModularMiningTool) {
            host.addCategories(ModuleCategory.MINING_TOOL);
            host.addPropertyBuilder(props -> {
                props.add(new DecimalProperty("mining_speed", 1).range(0, 1).setFormatter(ConfigProperty.DecimalFormatter.PERCENT_1));
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
            tooltip.add(new TranslationTextComponent("[Modular Item]").applyTextStyle(TextFormatting.BLUE));
        }
        EnergyUtils.addEnergyInfo(stack, tooltip);
    }

    @Override
    default Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = HashMultimap.create();
        if (MODULE_HOST_CAPABILITY != null && stack.getCapability(MODULE_HOST_CAPABILITY).isPresent()) { //Because vanilla calls this before capabilities are registered.
            ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
            host.getAttributeModifiers(slot, stack, map);
        }
        return map;
    }

    default void handleInventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        StackTickContext context = new StackTickContext(host, stack, entity, itemSlot, isSelected);
        host.handleTick(context);
    }

    default float getDestroySpeed(ItemStack stack, BlockState state) {
        ModuleHost host = stack.getCapability(MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        SpeedData data = host.getModuleData(ModuleTypes.SPEED);
        float moduleValue = data == null ? 0 : (float) data.getSpeedMultiplier();
        //The way vanilla handles efficiency is kinda dumb. So this is far from perfect but its kinda close... ish.
        float multiplier = MathHelper.map((moduleValue + 1F) * (moduleValue + 1F), 1F, 2F, 1F, 1.65F);
        //Module host should always be a property provider because it needs to provide module properties.
        if (host instanceof PropertyProvider && ((PropertyProvider) host).hasProperty("mining_speed")) {
            multiplier *= ((DecimalProperty) ((PropertyProvider) host).getProperty("mining_speed")).getValue();
        }
        if (getToolTypes(stack).stream().anyMatch(state::isToolEffective) || overrideEffectivity(state.getMaterial()) || effectiveBlockAdditions().contains(state.getBlock())) {
            return getBaseEfficiency() * multiplier;
        }
        IOPStorage opStorage = EnergyUtils.getStorage(stack);
        if (opStorage != null && opStorage.getOPStored() < EquipCfg.)

        return multiplier < 1 ? 1.0F * multiplier : 1.0F;
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

}
