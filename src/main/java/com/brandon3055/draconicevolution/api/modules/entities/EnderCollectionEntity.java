package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.NoData;
import com.brandon3055.draconicevolution.client.DEMiscSprites;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
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
            slots.add(new Slot(i, xPos, yPos, slotXSize, slotYSize));
        }
        return slots;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected Material getSlotOverlay() {
        return BCGuiSprites.get("slots/filter");
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
    public void renderModule(GuiElement<?> parent, MultiBufferSource getter, PoseStack poseStack, int x, int y, int width, int height, double mouseX, double mouseY, boolean renderStack, float partialTicks) {
        super.renderModule(parent, getter, poseStack, x, y, width, height, mouseX, mouseY, renderStack, partialTicks);
        if (frequencyTag.isEmpty() || !ModHelper.ENDERSTORAGE.isPresent()) {
            return;
        }

        float dist = (float) GuiHelper.distToRect(x, y, width, height, GuiHelper.getMouseX(), GuiHelper.getMouseY());
        float alpha = dist <= 10 ? (dist / 10F) : 1;
        if (alpha == 0) return;
        if (alpha != 1) {
            poseStack.translate(0, 0, 201);
        }

        double p = width / 16D;
        double w = p * 2;    //Indicator Width
        double h = p * 4;    //Indicator Height
        double mid = x + (width / 2D);
        double py = y + (height / 2D) - (h / 2);
        EnumColour[] colours = getColours();

        Material mat = DEMiscSprites.getMat("enderstorage", "buttons");
        VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(DEMiscSprites.GUI_TYPE), poseStack);
        double hp = 1/32D;
        for (int i = 0; i < 3; i++) {
            double px = mid - (w / 2D) - (p * 3) + (i * (p * 3));
            double u = (hp * 3) + ((hp*8) * (colours[i].getWoolMeta() % 4));
            //noinspection IntegerDivisionInFloatingPointContext
            double v = (hp * 2) +  ((hp*8) * (colours[i].getWoolMeta() / 4));
            GuiHelper.drawPartialSprite(builder, px, py, w, h, mat.sprite(), u, v, u + (hp * 2), v + (hp * 4), 1, 1, 1, alpha);
        }
        RenderUtils.endBatch(getter);
        if (alpha != 1) {
            poseStack.translate(0, 0, -201);
        }
    }
}
