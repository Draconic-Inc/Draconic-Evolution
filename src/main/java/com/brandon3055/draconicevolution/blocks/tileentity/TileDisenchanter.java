package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.inventory.DisenchanterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileDisenchanter extends TileBCore implements MenuProvider, IInteractTile {

    public TileItemStackHandler itemHandler = new TileItemStackHandler(this, 3)
            .setSlotValidator(0, stack -> stack.isEnchanted() && ModHelper.canRemoveEnchants(stack))
            .setSlotValidator(1, itemStack -> itemStack.is(Items.BOOK));

    public TileDisenchanter(BlockPos pos, BlockState state) {
        super(DEContent.TILE_DISENCHANTER.get(), pos, state);
        capManager.setManaged("inventory", Capabilities.ItemHandler.BLOCK, itemHandler).saveBoth();
    }

    public static void register(RegisterCapabilitiesEvent event) {
        capability(event, DEContent.TILE_DISENCHANTER, Capabilities.ItemHandler.BLOCK);
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int pid) {
        ItemStack input = itemHandler.getStackInSlot(0);
        ItemStack books = itemHandler.getStackInSlot(1);
        ItemStack output = itemHandler.getStackInSlot(2);

        if (input.isEmpty() || !input.isEnchanted() || books.isEmpty() || books.getCount() <= 0 || !output.isEmpty()) {
            return;
        }

        ItemEnchantments list = input.get(DataComponents.ENCHANTMENTS);
        if (list == null) {
            return;
        }

        ResourceKey<Enchantment> target = ResourceKey.create(Registries.ENCHANTMENT, data.readResourceLocation());
        for (Holder<Enchantment> enchantHolder : list.keySet()) {
            int lvl = list.getLevel(enchantHolder);
            if (!target.equals(enchantHolder.getKey())) {
                continue;
            }
            Enchantment enchantment = enchantHolder.value();
            int cost = getCostInLevels(enchantment, lvl);

            if (!client.getAbilities().instabuild && cost > client.experienceLevel) {
                client.sendSystemMessage(Component.translatable("disenchanter." + DraconicEvolution.MODID + ".not_enough_levels", cost).withStyle(ChatFormatting.RED));
                return;
            }

            if (!client.getAbilities().instabuild) {
                client.giveExperienceLevels(-cost);
            }

            books.shrink(1);
            if (books.getCount() <= 0) {
                itemHandler.setStackInSlot(1, ItemStack.EMPTY);
            }

            int repairCost = input.getOrDefault(DataComponents.REPAIR_COST, 0);
            if (repairCost > 0) {
                repairCost -= (int) (repairCost * (1D / list.size()));
                input.set(DataComponents.REPAIR_COST, Math.max(repairCost, 0));
            }

            ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantHolder, lvl));
            itemHandler.setStackInSlot(2, book);

            EnchantmentHelper.updateEnchantments(input, mutable -> mutable.removeIf(e -> e == enchantHolder));
            return;
        }
    }
    
    public int getCostInLevels(Enchantment e, int level) {
    	int max = e.getMaxLevel();
    	return (int)((20 - (max == 1 ? 0 : max == 2 ? 7 : 10)) * 0.35 * DEOldConfig.disenchnaterCostMultiplyer) * level;
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int currentWindowIndex, Inventory playerInventory, Player player) {
        return new DisenchanterMenu(currentWindowIndex, player.getInventory(), this);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            player.openMenu(this, worldPosition);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }
}
