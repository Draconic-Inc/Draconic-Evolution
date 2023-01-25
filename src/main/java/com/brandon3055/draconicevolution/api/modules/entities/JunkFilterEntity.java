package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiPopUpDialogBase;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiScrollElement;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiSlideControl;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.*;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.NoData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.render.RenderTypes;
import com.brandon3055.draconicevolution.init.ClientInit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 21/01/2023
 */
public class JunkFilterEntity extends ModuleEntity<NoData> {

    private static final int SLOTS = 9;

    private BooleanProperty filterEnabled;
    private IntObjectMap<ItemStack> filterStacks = new IntObjectHashMap<>();
    private IntObjectMap<TagKey<Item>> filterTags = new IntObjectHashMap<>();

    public JunkFilterEntity(Module<NoData> module) {
        super(module);
        addProperty(filterEnabled = new BooleanProperty("junk_filter_mod.enabled", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
        filterEnabled.setDisplayName(this::getName);
        filterEnabled.setToolTip(this::getName);
    }

    private Component getName() {
        MutableComponent component = new TranslatableComponent("item_prop." + MODID + ".junk_filter_mod.enabled");
        boolean first = true;
        for (int i = 0; i < SLOTS; i++) {
            MutableComponent append = null;
            if (filterTags.containsKey(i)) {
                append = new TextComponent(filterTags.get(i).location().toString()).withStyle(ChatFormatting.GRAY);
            } else if (filterStacks.containsKey(i)) {
                append = new TextComponent(filterStacks.get(i).getHoverName().getString()).withStyle(ChatFormatting.GRAY);
            }
            if (append != null) {
                if (first) {
                    component.append(": ");
                    first = false;
                } else {
                    component.append(", ");
                }
                component.append(append);
            }
        }
        return component;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderModule(GuiElement<?> parent, MultiBufferSource getter, PoseStack poseStack, int x, int y, int width, int height, double mouseX, double mouseY, boolean renderStack, float partialTicks) {
        float dist = (float) GuiHelper.distToRect(x, y, width, height, GuiHelper.getMouseX(), GuiHelper.getMouseY());
        float alpha = dist <= 10 ? (dist / 10F) : 1;
        poseStack.pushPose();

        //Draw slots and stacks
        if (alpha < 1) {
            Material slot = BCGuiSprites.getThemed("slot");
            Material trash = BCGuiSprites.get("slots/trash");
            VertexConsumer buffer = new TransformingVertexConsumer(getter.getBuffer(BCGuiSprites.GUI_TYPE), poseStack);

            double slotSize = width / 3D;
            double itemOffset = slotSize / 16;
            //Draw Slots
            for (int i = 0; i < SLOTS; i++) {
                double xPos = x + ((i % 3) * slotSize);
                //noinspection IntegerDivisionInFloatingPointContext
                double yPos = y + ((i / 3) * slotSize);
                GuiHelper.drawSprite(buffer, xPos, yPos, slotSize, slotSize, slot.sprite());
                if (filterStacks.containsKey(i)) continue;
                GuiHelper.drawSprite(buffer, xPos, yPos, slotSize, slotSize, trash.sprite());
            }
            RenderUtils.endBatch(getter);
            poseStack.translate(0, 0, 100);

            //Draw Items
            for (int i = 0; i < SLOTS; i++) {
                double xPos = x + ((i % 3) * slotSize);
                //noinspection IntegerDivisionInFloatingPointContext
                double yPos = y + ((i / 3) * slotSize);
                ItemStack stack = filterStacks.getOrDefault(i, ItemStack.EMPTY);
                if (filterTags.containsKey(i)) {
                    ITagManager<Item> tags = ForgeRegistries.ITEMS.tags();
                    if (tags != null) {
                        List<Item> matchingItems = tags.getTag(filterTags.get(i)).stream().toList();
                        stack = new ItemStack(matchingItems.get((TimeKeeper.getClientTick() / 10) % matchingItems.size()));
                    }
                }

                double itemX = xPos + itemOffset;
                double itemY = yPos + itemOffset;
                double itemSize = slotSize - (itemOffset * 2);
                if (!stack.isEmpty()) {
                    GuiHelper.renderGuiStack(stack, poseStack, itemX, itemY, itemSize, itemSize);
                }
                if (GuiHelper.isInRect(xPos, yPos, slotSize, slotSize, mouseX, mouseY)) {
                    poseStack.translate(0, 0, 100);
                    GuiHelper.drawRect(getter, poseStack, itemX, itemY, itemSize, itemSize, 0x80ffffff);
                    poseStack.translate(0, 0, -100);
                }
            }
            RenderUtils.endBatch(getter);
            poseStack.translate(0, 0, 100);
        } else if (renderStack) {
            poseStack.translate(0, 0, 200);
        }

        //Draw module texture
        if (alpha > 0) {
            int bgColour = (getModuleColour(module) & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
            GuiHelper.drawRect(getter, poseStack, x, y, width, height, bgColour);
            GuiHelper.drawBorderedRect(getter, poseStack, x, y, width, height, 1, bgColour, GuiHelper.mixColours(bgColour, 0x20202000, true));

            TextureAtlasSprite sprite = ClientInit.moduleSpriteUploader.getSprite(module);
            float ar = (float) sprite.getWidth() / (float) sprite.getHeight();
            float iar = (float) sprite.getHeight() / (float) sprite.getWidth();

            VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(RenderTypes.MODULE_TYPE), poseStack);
            if (iar * width <= height) { //Fit Width
                double h = width * iar;
                GuiHelper.drawSprite(builder, x, y + (height / 2D) - (h / 2D), width, h, sprite, 1F, 1F, 1F, alpha);
            } else { //Fit height
                double w = height * ar;
                GuiHelper.drawSprite(builder, x + (width / 2D) - (w / 2D), y, w, height, sprite, 1F, 1F, 1F, alpha);
            }
        }

        poseStack.popPose();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean clientModuleClicked(GuiElement<?> parent, Player player, int x, int y, int width, int height, double mouseX, double mouseY, int button) {
        int slotX = MathHelper.clip((int) (((mouseX - x) / (double) width) * 3), 0, 2);
        int slotY = MathHelper.clip((int) (((mouseY - y) / (double) height) * 3), 0, 2);
        int index = slotX + (slotY * 3);
        ItemStack carrying = player.containerMenu.getCarried();

        //Clear Filter
        if (button == 1 && Screen.hasShiftDown()) {
            filterStacks.remove(index);
            filterTags.remove(index);
            sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
            return true;
        } else if (button == 1) {
            displayTagDialog(parent, index);
            return true;
        }

        if (carrying.isEmpty()) {
            return false;
        }

        //Set item filter
        ItemStack newFilter = carrying.copy();
        newFilter.setCount(1);
        filterStacks.put(index, newFilter);
        filterTags.remove(index);
        sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void displayTagDialog(GuiElement<?> parent, int index) {
        GuiTexture bg = GuiTexture.newDynamicTexture(148, 150, () -> BCGuiSprites.getThemed("background_dynamic"));
        GuiPopUpDialogBase<?> dialog = new GuiPopUpDialogBase<>(parent);
        dialog.setPosAndSize(bg);
        dialog.setDragBar(20);
        dialog.addChild(bg);

        GuiLabel heading = bg.addChild(new GuiLabel(new TranslatableComponent("module." + MODID + ".junk_filter.filter_by_tag")))
                .setRelPos(bg, 0, 5)
                .setSize(bg.xSize(), 9)
                .setTextColGetter(GuiToolkit.Palette.BG::text)
                .setShadowStateSupplier(() -> BCConfig.darkMode);

        GuiTextField textField = bg.addChild(new GuiTextField())
                .setPos(bg.xPos() + 5, heading.maxYPos() + 2)
                .setMaxXPos(bg.maxXPos() - 5, true)
                .setYSize(12)
                .setTextColor(GuiToolkit.Palette.Ctrl::text)
                .setShadow(false)
                .addBackground(GuiToolkit.Palette.Ctrl::fill, hovering -> GuiToolkit.Palette.Ctrl.accentLight(false))
                .setSuggestion(I18n.get("module." + MODID + ".junk_filter.filter_example"));

        GuiLabel matchingLabel = bg.addChild(new GuiLabel(I18n.get("module." + MODID + ".junk_filter.matching")))
                .setPos(bg.xPos(), textField.maxYPos() + 6)
                .setSize(bg.xSize(), 9)
                .setTextColGetter(GuiToolkit.Palette.BG::text)
                .setShadowStateSupplier(() -> BCConfig.darkMode);

        GuiElement<?> matchContainer = bg.addChild(new GuiBorderedRect())
                .setPos(textField.xPos(), matchingLabel.maxYPos() + 2)
                .setMaxPos(textField.maxXPos(), bg.maxYPos() - 5, true)
                .setColours(GuiToolkit.Palette.Slot.fill(), GuiToolkit.Palette.Slot.accentDark(), GuiToolkit.Palette.Slot.accentLight());

        GuiSlideControl scrollBar = new GuiSlideControl(GuiSlideControl.SliderRotation.VERTICAL)
                .setPos(matchContainer.maxXPos() - 11, matchContainer.yPos() + 1)
                .setMaxPos(matchContainer.maxXPos() - 1, matchContainer.maxYPos() - 1, true)
                .setBackgroundElement(GuiTexture.newDynamicTexture(BCGuiSprites.themedGetter("button_disabled")))
                .setSliderElement(GuiTexture.newDynamicTexture(BCGuiSprites.themedGetter("button_borderless")))
                .onReload(GuiSlideControl::updateElements)
                .setEnabledCallback(() -> true);

        GuiScrollElement scrollElement = matchContainer.addChild(new GuiScrollElement())
                .setRelPos(matchContainer, 1, 1)
                .setListMode(GuiScrollElement.ListMode.VERT_LOCK_POS_WIDTH)
                .setMaxPos(matchContainer.maxXPos() - 11, matchContainer.maxYPos() - 2, true)
                .setVerticalScrollBar(scrollBar)
                .setStandardScrollBehavior();

        List<TagKey<Item>> tagOps = new ArrayList<>();
        ItemStack filterStack = filterStacks.getOrDefault(index, ItemStack.EMPTY);
        if (!filterStack.isEmpty()) {
            tagOps.addAll(filterStack.getTags().toList());
        }

        if (!tagOps.isEmpty()) {
            GuiButton fromItemButton = bg.addChild(new GuiButton(""))
                    .setSize(12, 12)
                    .setMaxPos(matchContainer.maxXPos(), matchContainer.yPos() - 1, false);
            fromItemButton.addChild(new GuiStackIcon(filterStack))
                    .setPosAndSize(fromItemButton)
                    .setInsets(0, 0, 0, 0)
                    .setHoverOverride(Collections.singletonList(new TranslatableComponent("module." + MODID + ".junk_filter.select_from_item")));

            fromItemButton.onPressed(() -> {
                scrollElement.clearElements();
                scrollElement.resetScrollPositions();
                matchingLabel.setLabelText(I18n.get("module." + MODID + ".junk_filter.select_or_enter"));
                for (TagKey<Item> tag : tagOps) {
                    GuiButton button = new GuiButton(tag.location().toString())
                            .setYSize(12)
                            .setRectFillColourGetter((hovering, disabled) -> GuiToolkit.Palette.Ctrl.fill(hovering))
                            .setRectBorderColourGetter((hovering, disabled) -> 0)
                            .setHoverText(tag.location().toString());
                    scrollElement.addElement(button);
                    button.onPressed(() -> textField.setValue(tag.location().toString()));
                }
                scrollElement.reloadElement();
            });
        }

        textField.onValueChanged(s -> {
            ResourceLocation location = ResourceLocation.tryParse(s);
            TagKey<Item> key = filterTags.get(index);
            if (s.isEmpty() && key == null) return;
            if (location == null && filterTags.containsKey(index)) {
                filterTags.remove(index);
                sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
                return;
            } else if (location == null || (key != null && location.equals(key.location()) && !scrollElement.getScrollingElements().isEmpty())) {
                return;
            }

            key = ItemTags.create(location);
            ITagManager<Item> tags = ForgeRegistries.ITEMS.tags();
            if (tags == null) return;

            scrollElement.clearElements();
            scrollElement.resetScrollPositions();
            matchingLabel.setLabelText(I18n.get("module." + MODID + ".junk_filter.matching"));

            List<Item> matchingItems = tags.getTag(key).stream().toList();
            if (matchingItems.isEmpty()) {
                filterTags.remove(index);
                sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
                return;
            }

            GuiElement<?> container = new GuiElement<>();

            for (int i = 0; i < matchingItems.size(); i++) {
                Item item = matchingItems.get(i);
                GuiStackIcon icon = new GuiStackIcon(new ItemStack(item));
                icon.setPos((i % 7) * 18, (i / 7) * 18);
                container.addChild(icon);
            }
            container.setBoundsToChildren();

            scrollElement.addElement(container);
            scrollElement.reloadElement();

            filterTags.put(index, key);
            sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
        });

        textField.onReturnPressed(dialog::close);

        dialog.showCenter((int) parent.getRenderZLevel() + 200);

        if (filterTags.containsKey(index)) {
            textField.setValue(filterTags.get(index).location().toString());
        }
    }

    @Override
    public void handleClientMessage(MCDataInput input) {
        readExtraData(input.readCompoundNBT());
    }

    public boolean isStackJunk(ItemStack stack) {
        if (!isEnabled()) {
            return false;
        }

        for (int i = 0; i < SLOTS; i++) {
            if (filterTags.containsKey(i)) {
                TagKey<Item> key = filterTags.get(i);
                if (stack.is(key)) {
                    return true;
                }
            } else if (filterStacks.containsKey(i)) {
                Item item = filterStacks.get(i).getItem();
                if (stack.is(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Predicate<ItemStack> createJunkTest() {
        if (!isEnabled()) {
            return e -> false;
        }

        Predicate<ItemStack> filterTest = null;
        for (int i = 0; i < SLOTS; i++) {
            Predicate<ItemStack> slotTest;
            if (filterTags.containsKey(i)) {
                TagKey<Item> key = filterTags.get(i);
                slotTest = e -> e.is(key);
            } else if (filterStacks.containsKey(i)) {
                Item item = filterStacks.get(i).getItem();
                slotTest = e -> e.is(item);
            } else {
                continue;
            }
            filterTest = filterTest == null ? slotTest : filterTest.or(slotTest);
        }

        return filterTest == null ? e -> false : filterTest;
    }

    public boolean isEnabled() {
        return filterEnabled.getValue();
    }

    @Override
    protected CompoundTag writeExtraData(CompoundTag nbt) {
        super.writeExtraData(nbt);
        ListTag itemList = new ListTag();
        for (int i = 0; i < SLOTS; ++i) {
            if (filterStacks.containsKey(i)) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("slot", (byte) i);
                filterStacks.get(i).save(compoundtag);
                itemList.add(compoundtag);
            }
        }
        nbt.put("items", itemList);

        ListTag tagList = new ListTag();
        filterTags.forEach((slot, key) -> {
            CompoundTag tag = new CompoundTag();
            tag.putByte("slot", (byte) slot.intValue());
            tag.putString("key", key.location().toString());
            tagList.add(tag);
        });
        nbt.put("tags", tagList);
        return nbt;
    }

    @Override
    protected void readExtraData(CompoundTag nbt) {
        super.readExtraData(nbt);
        filterStacks.clear();
        filterTags.clear();
        ListTag itemList = nbt.getList("items", 10);
        for (int i = 0; i < itemList.size(); ++i) {
            CompoundTag compoundtag = itemList.getCompound(i);
            int slot = compoundtag.getByte("slot");
            if (slot >= 0 && slot < SLOTS) {
                filterStacks.put(slot, ItemStack.of(compoundtag));
            }
        }

        ListTag tagList = nbt.getList("tags", 10);
        for (int i = 0; i < tagList.size(); ++i) {
            CompoundTag tag = tagList.getCompound(i);
            int slot = tag.getByte("slot");
            TagKey<Item> key = ItemTags.create(new ResourceLocation(tag.getString("key")));
            if (slot >= 0 && slot < SLOTS) {
                filterTags.put(slot, key);
            }
        }
    }

    @Override
    public boolean renderModuleOverlay(GuiElement<?> parent, ModuleContext context, MultiBufferSource getter, PoseStack poseStack, int x, int y, int width, int height, double mouseX, double mouseY, float partialTicks, int hoverTicks) {
        if (Screen.hasShiftDown()) {
            if (hoverTicks <= 10) return false;
            Minecraft mc = Minecraft.getInstance();
            Item item = getModule().getItem();
            ItemStack stack = new ItemStack(item);
            writeToItemStack(stack, context);
            List<Component> list = stack.getTooltipLines(mc.player, mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
            parent.getScreen().renderTooltip(poseStack, list, Optional.empty(), (int) mouseX, (int) mouseY);
            return true;
        }
        if (hoverTicks <= 5) return false;

        List<Component> list = new ArrayList<>();
        list.add(new TranslatableComponent("module." + MODID + ".junk_filter.filter_slot").withStyle(ChatFormatting.YELLOW));

        int slotX = MathHelper.clip((int) (((mouseX - x) / width) * 3), 0, 2);
        int slotY = MathHelper.clip((int) (((mouseY - y) / height) * 3), 0, 2);
        int index = slotX + (slotY * 3);
        ItemStack filter = filterStacks.getOrDefault(index, ItemStack.EMPTY);
        TagKey<Item> tag = filterTags.get(index);

        if (tag != null) {
            list.add(new TranslatableComponent("module." + MODID + ".junk_filter.filter_tag").withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(new TextComponent(tag.location().toString()).withStyle(ChatFormatting.GOLD)));
        } else if (!filter.isEmpty()) {
            Component name = filter.getHoverName();
            list.add(new TranslatableComponent("module." + MODID + ".junk_filter.filter_item").withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(name instanceof MutableComponent ? ((MutableComponent) name).withStyle(ChatFormatting.GOLD) : name));
        }

        list.add(new TranslatableComponent("module." + MODID + ".junk_filter.set_item_filter").withStyle(ChatFormatting.DARK_GRAY));
        list.add(new TranslatableComponent("module." + MODID + ".junk_filter.configure_slot").withStyle(ChatFormatting.DARK_GRAY));
        list.add(new TranslatableComponent("module." + MODID + ".junk_filter.clear_slot").withStyle(ChatFormatting.DARK_GRAY));

        parent.getScreen().renderTooltip(poseStack, list, Optional.empty(), (int) mouseX, (int) mouseY);
        return true;
    }
}
