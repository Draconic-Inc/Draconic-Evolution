package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.client.ModuleTextures;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 25/01/2023
 */
public abstract class FilteredModuleEntity<T extends ModuleData<T>> extends ModuleEntity<T> {

    protected BooleanProperty filterEnabled;
    protected IntObjectMap<ItemStack> filterStacks = new IntObjectHashMap<>();
    protected IntObjectMap<TagKey<Item>> filterTags = new IntObjectHashMap<>();
    protected final int slotsCount;

    public FilteredModuleEntity(Module<T> module, int slotsCount) {
        super(module);
        this.slotsCount = slotsCount;
        savePropertiesToItem = true;
    }

    protected void addEnabledProperty(String moduleName, boolean includeFilter) {
        addProperty(filterEnabled = new BooleanProperty(moduleName + ".enabled", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
        if (includeFilter) {
            Function<Boolean, Component> nameGen = (trim) -> {
                MutableComponent component = Component.translatable("item_prop." + MODID + "." + moduleName + ".enabled");
                boolean first = true;
                for (int i = 0; i < slotsCount; i++) {
                    String append = null;
                    if (filterTags.containsKey(i)) {
                        append = filterTags.get(i).location().toString();
                    } else if (filterStacks.containsKey(i)) {
                        append = filterStacks.get(i).getHoverName().getString();
                    }
                    if (append != null) {
                        if (trim) {
                            int trimLen = Math.min(18, 32 - component.getString().length());
                            if (trimLen <= 0) break;
                            append = Utils.trimString(append, trimLen, "...");
                        }
                        if (first) {
                            component.append(": ");
                            first = false;
                        } else {
                            component.append(", ");
                        }
                        component.append(Component.literal(append).withStyle(ChatFormatting.GRAY));
                    }
                }
                return component;
            };

            filterEnabled.setDisplayName(() -> nameGen.apply(true));
            filterEnabled.setToolTip(() -> nameGen.apply(false));
        }
    }

    protected abstract List<Slot> layoutSlots(int x, int y, int width, int height);

    //Render

    @OnlyIn(Dist.CLIENT)
    protected abstract Material getSlotOverlay();

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderModule(GuiElement<?> parent, GuiRender render, int x, int y, int width, int height, double mouseX, double mouseY, boolean renderStack, float partialTicks) {
        if (slotsCount == 0) {
            super.renderModule(parent, render, x, y, width, height, mouseX, mouseY, renderStack, partialTicks);
            return;
        }

        float dist = (float) Utils.distToRect(x, y, width, height, mouseX, mouseY);
        float alpha = dist <= 10 ? (dist / 10F) : 1;
        render.pose().pushPose();

        List<Slot> slots = layoutSlots(x, y, width, height);

        //Draw slots and stacks
        if (alpha < 1) {
            Material slotTex = BCGuiTextures.getThemed("slot");
            Material overlayTex = getSlotOverlay();

            //Draw Slots
            for (Slot slot : slots) {
                render.tex(slotTex, slot.x, slot.y, slot.size, slot.size);
                if (overlayTex == null || filterStacks.containsKey(slot.index)) continue;
                render.tex(overlayTex, slot.x, slot.y, slot.size, slot.size);
            }

            render.pose().translate(0, 0, 100);
            //Draw Items
            for (Slot slot : slots) {
                ItemStack stack = filterStacks.getOrDefault(slot.index, ItemStack.EMPTY);
                if (filterTags.containsKey(slot.index)) {
                    ITagManager<Item> tags = ForgeRegistries.ITEMS.tags();
                    if (tags != null) {
                        List<Item> matchingItems = tags.getTag(filterTags.get(slot.index)).stream().toList();
                        stack = new ItemStack(matchingItems.get((TimeKeeper.getClientTick() / 10) % matchingItems.size()));
                    }
                }

                double itemX = slot.x + (slot.size / 16);
                double itemY = slot.y + (slot.size / 16);
                double itemSize = slot.size - ((slot.size / 16) * 2);
                if (!stack.isEmpty()) {
                    render.renderItem(stack, itemX, itemY, itemSize);
                }
                if (GuiRender.isInRect(slot.x, slot.y, slot.size, slot.size, mouseX, mouseY)) {
                    render.pose().translate(0, 0, 100);
                    render.rect(itemX, itemY, itemSize, itemSize, 0x80ffffff);
                    render.pose().translate(0, 0, -100);
                }
            }
            render.pose().translate(0, 0, 100);
        } else if (renderStack) {
            render.pose().translate(0, 0, 200);
        }

        //Draw module texture
        if (alpha > 0) {
            int bgColour = (getModuleColour(module) & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
            render.rect(x, y, width, height, bgColour);
            render.borderRect(x, y, width, height, 1, bgColour, GuiRender.mixColours(bgColour, 0x20202000, true));

            Material texture = ModuleTextures.get(module);
            TextureAtlasSprite sprite = texture.sprite();;
            float ar = (float) sprite.contents().width() / (float) sprite.contents().height();
            float iar = (float) sprite.contents().height() / (float) sprite.contents().width();

            if (iar * width <= height) { //Fit Width
                double h = width * iar;
                render.tex(texture, x, y + (height / 2D) - (h / 2D), width, h, 1F, 1F, 1F, alpha);
            } else { //Fit height
                double w = height * ar;
                render.tex(texture, x + (width / 2D) - (w / 2D), y, w, height, 1F, 1F, 1F, alpha);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderModuleOverlay(GuiElement<?> parent, ModuleContext context, GuiRender render, int x, int y, int width, int height, double mouseX, double mouseY, float partialTicks, int hoverTicks) {
        if (slotsCount == 0) {
            return super.renderModuleOverlay(parent, context, render, x, y, width, height, mouseX, mouseY, partialTicks, hoverTicks);
        }

        if (Screen.hasShiftDown()) {
            if (hoverTicks <= 10) return false;
            Minecraft mc = Minecraft.getInstance();
            Item item = getModule().getItem();
            ItemStack stack = new ItemStack(item);
            writeToItemStack(stack, context);
            List<Component> list = stack.getTooltipLines(mc.player, mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
            render.componentTooltip(list, (int) mouseX, (int) mouseY);
            return true;
        }
        if (hoverTicks <= 5) return false;

        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("module." + MODID + ".filtered_module.filter_slot").withStyle(ChatFormatting.YELLOW));

        int slotX = MathHelper.clip((int) (((mouseX - x) / width) * 3), 0, 2);
        int slotY = MathHelper.clip((int) (((mouseY - y) / height) * 3), 0, 2);
        int index = slotX + (slotY * 3);
        ItemStack filter = filterStacks.getOrDefault(index, ItemStack.EMPTY);
        TagKey<Item> tag = filterTags.get(index);

        if (tag != null) {
            list.add(Component.translatable("module." + MODID + ".filtered_module.filter_tag").withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.literal(tag.location().toString()).withStyle(ChatFormatting.GOLD)));
        } else if (!filter.isEmpty()) {
            Component name = filter.getHoverName();
            list.add(Component.translatable("module." + MODID + ".filtered_module.filter_item").withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(name instanceof MutableComponent ? ((MutableComponent) name).withStyle(ChatFormatting.GOLD) : name));
        }

        list.add(Component.translatable("module." + MODID + ".filtered_module.set_item_filter").withStyle(ChatFormatting.DARK_GRAY));
        list.add(Component.translatable("module." + MODID + ".filtered_module.configure_slot").withStyle(ChatFormatting.DARK_GRAY));
        list.add(Component.translatable("module." + MODID + ".filtered_module.clear_slot").withStyle(ChatFormatting.DARK_GRAY));

        render.componentTooltip(list, (int) mouseX, (int) mouseY);
        return true;
    }

    //Interact

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean clientModuleClicked(GuiElement<?> parent, Player player, int x, int y, int width, int height, double mouseX, double mouseY, int button) {
        if (slotsCount == 0) {
            return false;
        }

        List<Slot> slots = layoutSlots(x, y, width, height);
        Slot slot = slots.stream().filter(e -> e.isInSlot(mouseX, mouseY)).findAny().orElse(null);
        if (slot == null) return false;

        int index = slot.index;
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
        throw new NotImplementedException("TODO");
//        GuiTexture bg = GuiTexture.newDynamicTexture(148, 150, () -> BCGuiSprites.getThemed("background_dynamic"));
//        GuiPopUpDialogBase<?> dialog = new GuiPopUpDialogBase<>(parent);
//        dialog.setPosAndSize(bg);
//        dialog.setDragBar(20);
//        dialog.addChild(bg);
//
//        GuiLabel heading = bg.addChild(new GuiLabel(Component.translatable("module." + MODID + ".filtered_module.filter_by_tag")))
//                .setRelPos(bg, 0, 5)
//                .setSize(bg.xSize(), 9)
//                .setTextColGetter(GuiToolkit.Palette.BG::text)
//                .setShadowStateSupplier(() -> BCConfig.darkMode);
//
//        GuiTextField textField = bg.addChild(new GuiTextField())
//                .setPos(bg.xPos() + 5, heading.maxYPos() + 2)
//                .setMaxXPos(bg.maxXPos() - 5, true)
//                .setYSize(12)
//                .setTextColor(GuiToolkit.Palette.Ctrl::text)
//                .setShadow(false)
//                .addBackground(GuiToolkit.Palette.Ctrl::fill, hovering -> GuiToolkit.Palette.Ctrl.accentLight(false))
//                .setSuggestion(I18n.get("module." + MODID + ".filtered_module.filter_example"));
//
//        GuiLabel matchingLabel = bg.addChild(new GuiLabel(I18n.get("module." + MODID + ".filtered_module.matching")))
//                .setPos(bg.xPos(), textField.maxYPos() + 6)
//                .setSize(bg.xSize(), 9)
//                .setTextColGetter(GuiToolkit.Palette.BG::text)
//                .setShadowStateSupplier(() -> BCConfig.darkMode);
//
//        GuiElement<?> matchContainer = bg.addChild(new GuiBorderedRect())
//                .setPos(textField.xPos(), matchingLabel.maxYPos() + 2)
//                .setMaxPos(textField.maxXPos(), bg.maxYPos() - 5, true)
//                .setColours(GuiToolkit.Palette.Slot.fill(), GuiToolkit.Palette.Slot.accentDark(), GuiToolkit.Palette.Slot.accentLight());
//
//        GuiSlideControl scrollBar = new GuiSlideControl(GuiSlideControl.SliderRotation.VERTICAL)
//                .setPos(matchContainer.maxXPos() - 11, matchContainer.yPos() + 1)
//                .setMaxPos(matchContainer.maxXPos() - 1, matchContainer.maxYPos() - 1, true)
//                .setBackgroundElement(GuiTexture.newDynamicTexture(BCGuiSprites.themedGetter("button_disabled")))
//                .setSliderElement(GuiTexture.newDynamicTexture(BCGuiSprites.themedGetter("button_borderless")))
//                .onReload(GuiSlideControl::updateElements)
//                .setEnabledCallback(() -> true);
//
//        GuiScrollElement scrollElement = matchContainer.addChild(new GuiScrollElement())
//                .setRelPos(matchContainer, 1, 1)
//                .setListMode(GuiScrollElement.ListMode.VERT_LOCK_POS_WIDTH)
//                .setMaxPos(matchContainer.maxXPos() - 11, matchContainer.maxYPos() - 2, true)
//                .setVerticalScrollBar(scrollBar)
//                .setStandardScrollBehavior();
//
//        List<TagKey<Item>> tagOps = new ArrayList<>();
//        ItemStack filterStack = filterStacks.getOrDefault(index, ItemStack.EMPTY);
//        if (!filterStack.isEmpty()) {
//            tagOps.addAll(filterStack.getTags().toList());
//        }
//
//        if (!tagOps.isEmpty()) {
//            GuiButton fromItemButton = bg.addChild(new GuiButton(""))
//                    .setSize(12, 12)
//                    .setMaxPos(matchContainer.maxXPos(), matchContainer.yPos() - 1, false);
//            fromItemButton.addChild(new GuiStackIcon(filterStack))
//                    .setPosAndSize(fromItemButton)
//                    .setInsets(0, 0, 0, 0)
//                    .setHoverOverride(Collections.singletonList(Component.translatable("module." + MODID + ".filtered_module.select_from_item")));
//
//            fromItemButton.onPressed(() -> {
//                scrollElement.clearElements();
//                scrollElement.resetScrollPositions();
//                matchingLabel.setLabelText(I18n.get("module." + MODID + ".filtered_module.select_or_enter"));
//                for (TagKey<Item> tag : tagOps) {
//                    GuiButton button = new GuiButton(tag.location().toString())
//                            .setYSize(12)
//                            .setRectFillColourGetter((hovering, disabled) -> GuiToolkit.Palette.Ctrl.fill(hovering))
//                            .setRectBorderColourGetter((hovering, disabled) -> 0)
//                            .setHoverText(tag.location().toString());
//                    scrollElement.addElement(button);
//                    button.onPressed(() -> {
//                        filterTags.remove(index); //Ensures the scroll element is reloaded even if this tag was already selected.
//                        textField.setValue(tag.location().toString());
//                    });
//                }
//                scrollElement.reloadElement();
//            });
//        }
//
//        textField.onValueChanged(s -> {
//            ResourceLocation location = ResourceLocation.tryParse(s);
//            TagKey<Item> key = filterTags.get(index);
//            if (s.isEmpty() && key == null) return;
//            if (location == null && filterTags.containsKey(index)) {
//                filterTags.remove(index);
//                sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
//                return;
//            } else if (location == null || (key != null && location.equals(key.location()) && !scrollElement.getScrollingElements().isEmpty())) {
//                return;
//            }
//
//            key = ItemTags.create(location);
//            ITagManager<Item> tags = ForgeRegistries.ITEMS.tags();
//            if (tags == null) return;
//
//            scrollElement.clearElements();
//            scrollElement.resetScrollPositions();
//            matchingLabel.setLabelText(I18n.get("module." + MODID + ".filtered_module.matching"));
//
//            List<Item> matchingItems = tags.getTag(key).stream().toList();
//            if (matchingItems.isEmpty()) {
//                filterTags.remove(index);
//                sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
//                return;
//            }
//
//            GuiElement<?> container = new GuiElement<>();
//
//            for (int i = 0; i < matchingItems.size(); i++) {
//                Item item = matchingItems.get(i);
//                GuiStackIcon icon = new GuiStackIcon(new ItemStack(item));
//                icon.setPos((i % 7) * 18, (i / 7) * 18);
//                container.addChild(icon);
//            }
//            container.setBoundsToChildren();
//
//            scrollElement.addElement(container);
//            scrollElement.reloadElement();
//
//            filterTags.put(index, key);
//            sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
//        });
//
//        textField.onReturnPressed(dialog::close);
//
//        dialog.showCenter((int) parent.getRenderZLevel() + 200);
//
//        if (filterTags.containsKey(index)) {
//            textField.setValue(filterTags.get(index).location().toString());
//        }
    }

    @Override
    public void handleClientMessage(MCDataInput input) {
        readExtraData(input.readCompoundNBT());
    }

    //Filtering

    public boolean testStack(ItemStack stack) {
        if (!isEnabled()) {
            return false;
        }

        for (int i = 0; i < slotsCount; i++) {
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

    public Predicate<ItemStack> createFilterTest() {
        if (!isEnabled()) {
            return e -> false;
        }

        Predicate<ItemStack> filterTest = null;
        for (int i = 0; i < slotsCount; i++) {
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

    public boolean isEmpty() {
        return slotsCount == 0 || filterStacks.isEmpty() && filterTags.isEmpty();
    }

    public boolean isEnabled() {
        return filterEnabled == null || filterEnabled.getValue();
    }

    //Data

    @Override
    protected CompoundTag writeExtraData(CompoundTag nbt) {
        super.writeExtraData(nbt);
        if (slotsCount == 0) return nbt;

        ListTag itemList = new ListTag();
        filterStacks.forEach((slot, stack) -> {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putByte("slot", (byte) slot.intValue());
            filterStacks.get(slot).save(compoundtag);
            itemList.add(compoundtag);
        });
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
        if (slotsCount == 0) return;

        filterStacks.clear();
        filterTags.clear();
        ListTag itemList = nbt.getList("items", 10);
        for (int i = 0; i < itemList.size(); ++i) {
            CompoundTag compoundtag = itemList.getCompound(i);
            int slot = compoundtag.getByte("slot");
            if (slot >= 0 && slot < slotsCount) {
                filterStacks.put(slot, ItemStack.of(compoundtag));
            }
        }

        ListTag tagList = nbt.getList("tags", 10);
        for (int i = 0; i < tagList.size(); ++i) {
            CompoundTag tag = tagList.getCompound(i);
            int slot = tag.getByte("slot");
            TagKey<Item> key = ItemTags.create(new ResourceLocation(tag.getString("key")));
            if (slot >= 0 && slot < slotsCount) {
                filterTags.put(slot, key);
            }
        }
    }


    protected record Slot(int index, double x, double y, double size) {
        public boolean isInSlot(double testX, double textY) {
            return GuiRender.isInRect(x, y, size, size, testX, textY);
        }
    }
}
