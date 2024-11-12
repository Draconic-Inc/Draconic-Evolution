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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
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

        ListTag list = input.getEnchantmentTags();
        if (list.isEmpty()) {
            return;
        }

        String targetId = data.readString();
        if (StringUtils.isEmpty(targetId)) {
        	return;
        }

        for (int i = 0; i < list.size(); i++) {
            CompoundTag compound = list.getCompound(i);
            String id = compound.getString("id");
            int lvl = compound.getShort("lvl");
            Enchantment e = getEnchantmentFromTag(compound);

            if (e == null || !id.equals(targetId)) {
                continue;
            }

            int cost = getCostInLevels(e, lvl);

            if (!client.getAbilities().instabuild && cost > client.experienceLevel) {
                client.sendSystemMessage(Component.translatable("disenchanter." + DraconicEvolution.MODID + ".not_enough_levels", cost).withStyle(ChatFormatting.RED));
                return;
            }

            if (!client.getAbilities().instabuild) {
                client.giveExperienceLevels(-cost);
            }

            CompoundTag stackCompound = input.getTag();
            if (stackCompound == null) {
                return;
            }

            books.shrink(1);
            if (books.getCount() <= 0) {
                itemHandler.setStackInSlot(1, ItemStack.EMPTY);
            }

            int repairCost = stackCompound.getInt("RepairCost");
            repairCost -= ((double) repairCost * (1D / list.size()));
            stackCompound.putInt("RepairCost", repairCost);

            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(book, new EnchantmentInstance(e, lvl));
            itemHandler.setStackInSlot(2, book);
            list.remove(i);

            if (list.size() <= 0) {
                stackCompound.remove("ench");
            }
            return;
        }
    }
    
    public int getCostInLevels(Enchantment e, int level) {
    	int max = e.getMaxLevel();
    	return (int)((20 - (max == 1 ? 0 : max == 2 ? 7 : 10)) * ((e.getRarity().ordinal() + 1) * 0.35D) * DEOldConfig.disenchnaterCostMultiplyer) * level;
    }
    
    @Nullable
    public Enchantment getEnchantmentFromTag(CompoundTag c) {
    	if (c != null && c.getString("id") != null) {
    		return BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(c.getString("id")));
    	}
    	return null;
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int currentWindowIndex, Inventory playerInventory, Player player) {
        return new DisenchanterMenu(currentWindowIndex, player.getInventory(), this);
    }

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            player.openMenu(this, worldPosition);
        }
        return true;
    }
}
