package com.brandon3055.draconicevolution.api.modules.items;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.tile.TileEnderChest;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.NoData;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import com.brandon3055.draconicevolution.init.ItemData;
import com.brandon3055.draconicevolution.integration.ModHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

public class EnderCollectionModuleItem extends ModuleItem<NoData> {

    public EnderCollectionModuleItem(Supplier<Module<?>> moduleSupplier) {
        super(moduleSupplier);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();

        if (ModHelper.ENDERSTORAGE) {
            return bindFrequency(stack, level, context);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult bindFrequency(ItemStack stack, Level level, UseOnContext context) {
        BlockEntity tile = level.getBlockEntity(context.getClickedPos());
        if (tile instanceof TileEnderChest chest && context.getPlayer() != null && context.getPlayer().isCrouching()) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            Frequency frequency = chest.getFrequency();
            if (EnderStorageConfig.anarchyMode && !(frequency.owner().isPresent() && frequency.owner().get().equals(context.getPlayer().getUUID()))) {
                frequency = frequency.withoutOwner();
            }

            CompoundTag tag = stack.getOrDefault(ItemData.MODULE_ENTITY_TAG, CustomData.EMPTY).copyTag();
            tag.put("frequency", writeFrequency(frequency, level.registryAccess()));
            stack.set(ItemData.MODULE_ENTITY_TAG, CustomData.of(tag));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private CompoundTag writeFrequency(Frequency frequency, HolderLookup.Provider provider) {
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putInt("left", frequency.left().getWoolMeta());
        tagCompound.putInt("middle", frequency.middle().getWoolMeta());
        tagCompound.putInt("right", frequency.right().getWoolMeta());
        frequency.owner().ifPresent(uuid -> tagCompound.putUUID("owner", uuid));
        frequency.ownerName().ifPresent(component -> tagCompound.putString("owner_name", Component.Serializer.toJson(component, provider)));
        return tagCompound;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.pass(stack);
        }

        CompoundTag tag = stack.getOrDefault(ItemData.MODULE_ENTITY_TAG, CustomData.EMPTY).copyTag();
        if (player.isCrouching() && ModHelper.ENDERSTORAGE && !tag.contains("frequency")) {
            tag.remove("frequency");
            stack.set(ItemData.MODULE_ENTITY_TAG, CustomData.of(tag));
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        CompoundTag tag = stack.getOrDefault(ItemData.MODULE_ENTITY_TAG, CustomData.EMPTY).copyTag();
        if (ModHelper.ENDERSTORAGE && tag.contains("frequency")) {
            addEnderStorageInfo(tag, tooltip);
        }

        tooltip.add(Component.translatable("module." + MODID + ".ender_storage.about").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("module." + MODID + ".ender_storage.about_compat").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("module." + MODID + ".ender_storage.about_compat2").withStyle(ChatFormatting.DARK_GRAY));
    }

    private void addEnderStorageInfo(CompoundTag tagIn, List<Component> tooltip) {
        CompoundTag tag = tagIn.getCompound("frequency");
        if (tag.isEmpty()) return;

        Frequency frequency = new Frequency(tag);
        if (frequency.hasOwner()) {
            Component name = frequency.ownerName().get();
            if (name instanceof MutableComponent mutable) {
                mutable.withStyle(ChatFormatting.DARK_GREEN);
            }
            tooltip.add(Component.translatable("module." + MODID + ".ender_storage.owner").withStyle(ChatFormatting.GRAY).append(": ").append(name));
        }
        Component freq = frequency.getTooltip();
        if (freq instanceof MutableComponent mutable) {
            mutable.withStyle(ChatFormatting.DARK_GREEN);
        }
        tooltip.add(Component.translatable("module." + MODID + ".ender_storage.frequency").withStyle(ChatFormatting.GRAY).append(": ").append(freq));
    }
}
