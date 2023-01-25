package com.brandon3055.draconicevolution.api.modules.lib;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.tile.TileEnderChest;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.NoData;
import com.brandon3055.draconicevolution.integration.ModHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

public class EnderCollectionModuleItem extends ModuleItem<NoData> {
    public EnderCollectionModuleItem(Properties properties, Supplier<Module<NoData>> moduleSupplier) {
        super(properties, moduleSupplier);
    }

    public EnderCollectionModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();

        if (ModHelper.ENDERSTORAGE.isPresent()) {
            return bindFrequency(stack, level, context);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult bindFrequency(ItemStack stack, Level level, UseOnContext context) {
        BlockEntity tile = level.getBlockEntity(context.getClickedPos());
        if (tile instanceof TileEnderChest chest && context.getPlayer().isCrouching()) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            Frequency frequency = chest.getFrequency().copy();
            if (EnderStorageConfig.anarchyMode && !(frequency.owner != null && frequency.owner.equals(context.getPlayer().getUUID()))) {
                frequency.setOwner(null);
            }

            stack.getOrCreateTag().put("frequency", frequency.writeToNBT(new CompoundTag()));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.pass(stack);
        }

        if (player.isCrouching() && ModHelper.ENDERSTORAGE.isPresent() && stack.hasTag()) {
            stack.getOrCreateTag().remove("frequency");
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        return InteractionResultHolder.pass(stack);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (ModHelper.ENDERSTORAGE.isPresent() && stack.hasTag()) {
            addEnderStorageInfo(stack, tooltip);
        }

        tooltip.add(new TranslatableComponent("module." + MODID + ".ender_storage.about").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(new TranslatableComponent("module." + MODID + ".ender_storage.about_compat").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(new TranslatableComponent("module." + MODID + ".ender_storage.about_compat2").withStyle(ChatFormatting.DARK_GRAY));
    }

    private void addEnderStorageInfo(ItemStack stack, List<Component> tooltip) {
        CompoundTag tag = stack.getOrCreateTag().getCompound("frequency");
        if (tag.isEmpty()) return;

        Frequency frequency = new Frequency(tag);
        if (frequency.hasOwner()) {
            Component name = frequency.getOwnerName();
            if (name instanceof MutableComponent mutable) {
                mutable.withStyle(ChatFormatting.DARK_GREEN);
            }
            tooltip.add(new TranslatableComponent("module." + MODID + ".ender_storage.owner").withStyle(ChatFormatting.GRAY).append(": ").append(name));
        }
        Component freq = frequency.getTooltip();
        if (freq instanceof MutableComponent mutable) {
            mutable.withStyle(ChatFormatting.DARK_GREEN);
        }
        tooltip.add(new TranslatableComponent("module." + MODID + ".ender_storage.frequency").withStyle(ChatFormatting.GRAY).append(": ").append(freq));
    }
}
