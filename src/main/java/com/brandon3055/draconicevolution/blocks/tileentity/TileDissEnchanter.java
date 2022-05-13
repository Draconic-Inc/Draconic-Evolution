package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.ModHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileDissEnchanter extends TileBCore {

    public TileItemStackHandler itemHandler = new TileItemStackHandler(3);

    public TileDissEnchanter(BlockPos pos, BlockState state) {
        super(DEContent.tile_disenchanter, pos, state);
        capManager.setManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth();
        itemHandler.setStackValidator(this::isItemValidForSlot);
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 0) {
            return stack.isEnchanted() && ModHelper.canRemoveEnchants(stack);
        }
        else if (index == 1) {
            return stack.getItem() == Items.BOOK;
        }
        else return false;
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

        int targetId = data.readInt();

        for (int i = 0; i < list.size(); i++) {
            CompoundTag compound = list.getCompound(i);
            int id = compound.getShort("id");
            int lvl = compound.getShort("lvl");
            Enchantment e = Enchantment.byId(id);

            if (e == null || id != targetId) {
                continue;
            }

            int cost = (int) ((((double) lvl / (double) e.getMaxLevel()) * 20) * DEOldConfig.disenchnaterCostMultiplyer);

            if (!client.getAbilities().instabuild && cost > client.experienceLevel) {
                client.sendMessage(new TranslatableComponent("chat.dissEnchanter.notEnoughLevels.msg", cost).withStyle(ChatFormatting.RED), Util.NIL_UUID);
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
}
