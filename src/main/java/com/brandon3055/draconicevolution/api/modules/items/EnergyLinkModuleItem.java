package com.brandon3055.draconicevolution.api.modules.items;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.data.EnergyLinkData;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStructureBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

public class EnergyLinkModuleItem extends ModuleItem<EnergyLinkData> {
    public EnergyLinkModuleItem(Properties properties, Supplier<Module<EnergyLinkData>> moduleSupplier) {
        super(properties, moduleSupplier);
    }

    public EnergyLinkModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if (!(level instanceof ServerLevel serverLevel) || TimeKeeper.getServerTick() % 20 != 0) return;
        checkResetLink(stack, serverLevel);
    }

    public static void checkResetLink(ItemStack stack, ServerLevel level) {
        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getOrCreateTag();
        BlockPos pos = new BlockPos(tag.getInt("core_x"), tag.getInt("core_y"), tag.getInt("core_z"));
        UUID uuid = tag.getUUID("link_id");
        ResourceKey<Level> dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString("dim")));
        ServerLevel coreLevel = level.getServer().getLevel(dimension);

        boolean reset = coreLevel == null;
        if (!reset) {
            if (coreLevel.getBlockEntity(pos) instanceof TileEnergyCore core) {
                if (!uuid.equals(core.linkUUID.get())) {
                    reset = true;
                }
            } else {
                reset = true;
            }
        }

        if (reset) {
            stack.setTag(null);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.isClientSide || player == null || !player.isShiftKeyDown()) return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (blockEntity instanceof TileStructureBlock structureBlock) blockEntity = (BlockEntity) structureBlock.getController();
        if (blockEntity instanceof TileEnergyPylon pylon) blockEntity = pylon.getCore();
        if (blockEntity instanceof TileEnergyCoreStabilizer stabilizer) blockEntity = stabilizer.getCore();
        if (!(blockEntity instanceof TileEnergyCore core)) return InteractionResult.PASS;
        UUID linkId = core.linkUUID.get();

        if (!core.active.get() || linkId == null) {
            player.sendMessage(new TranslatableComponent("module." + MODID + ".energy_link.core_not_active"), Util.NIL_UUID);
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("dim", level.dimension().location().toString());
        BlockPos pos = core.getBlockPos();
        tag.putInt("core_x", pos.getX());
        tag.putInt("core_y", pos.getY());
        tag.putInt("core_z", pos.getZ());
        tag.putUUID("link_id", linkId);
        return InteractionResult.CONSUME;
    }
}
