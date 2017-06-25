package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.draconicevolution.integration.ModHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileDissEnchanter extends TileInventoryBase {

    public TileDissEnchanter() {
        setInventorySize(3);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 0) {
            return stack.isItemEnchanted() && ModHelper.canRemoveEnchants(stack);
        }
        else if (index == 1) {
            return stack.getItem() == Items.BOOK;
        }
        else return false;
    }

    @Override
    public void receivePacketFromClient(MCDataInput data, EntityPlayerMP client, int pid) {
        ItemStack input = getStackInSlot(0);
        ItemStack books = getStackInSlot(1);
        ItemStack output = getStackInSlot(2);

        if (input.isEmpty() || !input.isItemEnchanted() || books.isEmpty() || books.getCount() <= 0 || !output.isEmpty()) {
            return;
        }

        NBTTagList list = input.getEnchantmentTagList();
        if (list == null) {
            return;
        }

        int targetId = data.readInt();

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            int id = compound.getShort("id");
            int lvl = compound.getShort("lvl");
            Enchantment e = Enchantment.getEnchantmentByID(id);

            if (e == null || id != targetId) {
                continue;
            }

            int cost = (int) (((double) lvl / (double) e.getMaxLevel()) * 20);

            if (!client.capabilities.isCreativeMode && cost > client.experienceLevel) {
                client.sendMessage(new TextComponentTranslation("chat.dissEnchanter.notEnoughLevels.msg", cost).setStyle(new Style().setColor(TextFormatting.RED)));
                return;
            }

            if (!client.capabilities.isCreativeMode) {
                client.removeExperienceLevel(cost);
            }

            NBTTagCompound stackCompound = input.getTagCompound();
            if (stackCompound == null) {
                return;
            }

            books.shrink(1);
            if (books.getCount() <= 0) {
                setInventorySlotContents(1, ItemStack.EMPTY);
            }

            int repairCost = stackCompound.getInteger("RepairCost");
            repairCost -= ((double) repairCost * (1D / list.tagCount()));
            stackCompound.setInteger("RepairCost", repairCost);

            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            Items.ENCHANTED_BOOK.addEnchantment(book, new EnchantmentData(e, lvl));
            setInventorySlotContents(2, book);
            list.removeTag(i);

            if (list.tagCount() <= 0) {
                stackCompound.removeTag("ench");
            }

            return;
        }
    }
}
