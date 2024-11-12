package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.NoData;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 21/01/2023
 */
public class EnderCollectionEntity extends FilteredModuleEntity<NoData> {

    private CompoundTag frequencyTag = new CompoundTag();

    public EnderCollectionEntity(Module<NoData> module) {
        super(module, module.getProperties().getTechLevel() == TechLevel.DRACONIC ? 9 : 0);
        addEnabledProperty("ender_collection_mod", false);
    }

    @Override
    protected List<Slot> layoutSlots(int x, int y, int width, int height) {
        List<Slot> slots = new ArrayList<>();
        double slotXSize = width / 3D;
        double slotYSize = height / 3D;
        for (int i = 0; i < slotsCount; i++) {
            double xPos = x + ((i % 3) * slotXSize);
            //noinspection IntegerDivisionInFloatingPointContext
            double yPos = y + ((i / 3) * slotYSize);
            slots.add(new Slot(i, xPos, yPos, slotXSize));
        }
        return slots;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected Material getSlotOverlay() {
        return BCGuiTextures.get("slots/filter");
    }

    @Override
    protected void readExtraData(CompoundTag nbt) {
        super.readExtraData(nbt);
        frequencyTag = nbt.getCompound("frequency");
    }

    @Override
    protected CompoundTag writeExtraData(CompoundTag nbt) {
        nbt.put("frequency", frequencyTag);
        return super.writeExtraData(nbt);
    }

    public List<ItemStack> insertStacks(Player player, Collection<ItemStack> stacks, IOPStorage opStorage) {
        if (opStorage == null) return new ArrayList<>(stacks);
        Container container;
        if (ModHelper.ENDERSTORAGE.isPresent()) {
            container = getEnderStorage(player);
        } else {
            container = player.getEnderChestInventory();
        }

        Predicate<ItemStack> filter = null;
        if (!filterTags.isEmpty() || !filterStacks.isEmpty()) {
            filter = createFilterTest();
        }

        List<ItemStack> notInserted = new ArrayList<>();
        for (ItemStack stack : stacks) {
            long cost = (long) EquipCfg.enderModulePerItemEnergy * stack.getCount();
            if (opStorage.getOPStored() < cost || (filter != null && !filter.test(stack))) {
                notInserted.add(stack);
                continue;
            }
            opStorage.modifyEnergyStored(-cost);
            int remainder = InventoryUtils.insertItem(container, stack, false);
            if (remainder > 0) {
                stack.setCount(remainder);
                notInserted.add(stack);
            }
        }
        return notInserted;
    }

    /**
     * @return the remaining items that could not be inserted
     */
    public int insertStack(Player player, ItemStack stack, IOPStorage opStorage) {
        Container container;
        if (ModHelper.ENDERSTORAGE.isPresent()) {
            container = getEnderStorage(player);
        } else {
            container = player.getEnderChestInventory();
        }

        Predicate<ItemStack> filter = null;
        if (!filterTags.isEmpty() || !filterStacks.isEmpty()) {
            filter = createFilterTest();
        }

        long cost = (long) EquipCfg.enderModulePerItemEnergy * stack.getCount();
        if (opStorage.getOPStored() < cost || (filter != null && !filter.test(stack))) {
            return stack.getCount();
        }
        opStorage.modifyEnergyStored(-cost);
        return InventoryUtils.insertItem(container, stack, false);
    }

    private Container getEnderStorage(Player player) {
        if (frequencyTag.isEmpty()) {
            return player.getEnderChestInventory();
        }
        Frequency frequency = new Frequency(frequencyTag);
        return EnderStorageManager.instance(false).getStorage(frequency, EnderItemStorage.TYPE);
    }

    private EnumColour[] getColours() {
        Frequency frequency = new Frequency(frequencyTag);
        return new EnumColour[]{frequency.getLeft(), frequency.getMiddle(), frequency.getRight()};
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderModule(GuiElement<?> parent, GuiRender render, int x, int y, int width, int height, double mouseX, double mouseY, boolean renderStack, float partialTicks) {
        super.renderModule(parent, render, x, y, width, height, mouseX, mouseY, renderStack, partialTicks);
        if (frequencyTag.isEmpty() || !ModHelper.ENDERSTORAGE.isPresent()) {
            return;
        }

        float dist = (float) Utils.distToRect(x, y, width, height, mouseX, mouseY);
        float alpha = dist <= 10 ? (dist / 10F) : 1;
        if (alpha == 0) return;
        if (alpha != 1) {
            render.pose().translate(0, 0, 201);
        }

        float p = width / 16F;
        float w = p * 2;    //Indicator Width
        float h = p * 4;    //Indicator Height
        float mid = x + (width / 2F);
        float py = y + (height / 2F) - (h / 2);
        EnumColour[] colours = getColours();

        Material mat = DEGuiTextures.get("misc/es_buttons");
        float hp = 1/32F;
        for (int i = 0; i < 3; i++) {
            float px = mid - (w / 2F) - (p * 3) + (i * (p * 3));
            float u = (hp * 3) + ((hp*8) * (colours[i].getWoolMeta() % 4));
            //noinspection IntegerDivisionInFloatingPointContext
            float v = (hp * 2) +  ((hp*8) * (colours[i].getWoolMeta() / 4));
            render.partialSprite(mat.renderType(GuiRender::texColType), px, py, px + w, py + h, mat.sprite(), u, v, u + (hp * 2), v + (hp * 4), 1, 1, 1, alpha);
        }
        if (alpha != 1) {
            render.pose().translate(0, 0, -201);
        }
    }
}
