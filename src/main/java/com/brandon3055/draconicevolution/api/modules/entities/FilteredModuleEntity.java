package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.gui.modular.elements.*;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.TextState;
import codechicken.lib.gui.modular.lib.geometry.Align;
import codechicken.lib.gui.modular.lib.geometry.Axis;
import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiDialogBase;
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
import net.covers1624.quack.collection.FastStream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
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

    @OnlyIn (Dist.CLIENT)
    protected abstract Material getSlotOverlay();

    @Override
    @OnlyIn (Dist.CLIENT)
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
                render.texRect(slotTex, slot.x, slot.y, slot.size, slot.size);
                if (overlayTex == null || filterStacks.containsKey(slot.index)) continue;
                render.texRect(overlayTex, slot.x, slot.y, slot.size, slot.size);
            }

            render.pose().translate(0, 0, 100);
            //Draw Items
            for (Slot slot : slots) {
                ItemStack stack = filterStacks.getOrDefault(slot.index, ItemStack.EMPTY);
                if (filterTags.containsKey(slot.index)) {
                    List<Item> matchingItems = FastStream.of(BuiltInRegistries.ITEM.getTagOrEmpty(filterTags.get(slot.index))).map(Holder::value).toList();
                    stack = new ItemStack(matchingItems.get((TimeKeeper.getClientTick() / 10) % matchingItems.size()));
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
            TextureAtlasSprite sprite = texture.sprite();
            ;
            float ar = (float) sprite.contents().width() / (float) sprite.contents().height();
            float iar = (float) sprite.contents().height() / (float) sprite.contents().width();

            if (iar * width <= height) { //Fit Width
                double h = width * iar;
                render.texRect(texture, x, y + (height / 2D) - (h / 2D), width, h, 1F, 1F, 1F, alpha);
            } else { //Fit height
                double w = height * ar;
                render.texRect(texture, x + (width / 2D) - (w / 2D), y, w, height, 1F, 1F, 1F, alpha);
            }
        }
    }

    @Override
    @OnlyIn (Dist.CLIENT)
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
    @OnlyIn (Dist.CLIENT)
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

    @OnlyIn (Dist.CLIENT)
    private void displayTagDialog(GuiElement<?> parent, int index) {
        GuiDialogBase dialog = new GuiDialogBase(parent);
        dialog.setCloseOnOutsideClick(true);
        dialog.addMoveHandle(20);
        dialog.enableCursors(true);
        Constraints.size(dialog, 152, 150);
        dialog.placeCenter();

        GuiElement<?> root = dialog.getContentElement();
        root.setOpaque(true);
        root.jeiExclude();

        Constraints.bind(new GuiTexture(root, () -> BCGuiTextures.getThemed("background_dynamic")), root);

        GuiText heading = new GuiText(root, Component.translatable("module." + MODID + ".filtered_module.filter_by_tag"))
                .constrain(WIDTH, relative(root.get(WIDTH), -10))
                .constrain(HEIGHT, literal(9))
                .setTextColour(GuiToolkit.Palette.BG::text)
                .setShadow(() -> BCConfig.darkMode)
                .setAlignment(Align.CENTER);
        Constraints.placeInside(heading, root, Constraints.LayoutPos.TOP_CENTER, 0, 5);

        GuiRectangle fieldBg = new GuiRectangle(root)
                .constrain(WIDTH, relative(root.get(WIDTH), -10))
                .constrain(HEIGHT, literal(12));
        fieldBg.rectangle(() -> GuiToolkit.Palette.Ctrl.fill(fieldBg.isMouseOver()), () -> GuiToolkit.Palette.Ctrl.accentLight(false));

        GuiTextField textField = new GuiTextField(fieldBg)
                .setMaxLength(512)
                .setTextColor(GuiToolkit.Palette.Ctrl::text)
                .setShadow(false)
                .setSuggestionShadow(false)
                .setSuggestion(Component.translatable("module." + MODID + ".filtered_module.filter_example"));
        Constraints.bind(textField, fieldBg, 0, 4, 0, 4);
        Constraints.placeOutside(fieldBg, heading, Constraints.LayoutPos.BOTTOM_CENTER, 0, 2);

        GuiText matchingLabel = new GuiText(root, Component.translatable("module." + MODID + ".filtered_module.matching"))
                .constrain(WIDTH, relative(root.get(WIDTH), -10))
                .constrain(HEIGHT, literal(9))
                .setTextColour(GuiToolkit.Palette.BG::text)
                .setShadow(() -> BCConfig.darkMode)
                .setAlignment(Align.CENTER);
        Constraints.placeOutside(matchingLabel, fieldBg, Constraints.LayoutPos.BOTTOM_CENTER, 0, 4);

        GuiRectangle matchContainer = new GuiRectangle(root)
                .constrain(WIDTH, relative(root.get(WIDTH), -10))
                .constrain(LEFT, relative(root.get(LEFT), 5))
                .constrain(TOP, relative(matchingLabel.get(BOTTOM), 2))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -5))
                .shadedRect(GuiToolkit.Palette.Slot::accentDark, GuiToolkit.Palette.Slot::accentLight, GuiToolkit.Palette.Slot::fill);

        var scrollBar = GuiSlider.vanillaScrollBar(matchContainer, Axis.Y);
        scrollBar.container().shadedRect(() -> 0, () -> 0, () -> scrollBar.container().isMouseOver() ? 0x30FFFFFF : 0);
        scrollBar.container()
                .constrain(WIDTH, literal(8))
                .constrain(TOP, match(matchContainer.get(TOP)))
                .constrain(BOTTOM, match(matchContainer.get(BOTTOM)))
                .constrain(RIGHT, match(matchContainer.get(RIGHT)));

        GuiScrolling scrolling = new GuiScrolling(matchContainer)
                .constrain(LEFT, relative(matchContainer.get(LEFT), 2))
                .constrain(RIGHT, relative(scrollBar.container().get(LEFT), 0))
                .constrain(TOP, relative(matchContainer.get(TOP), 2))
                .constrain(BOTTOM, relative(matchContainer.get(BOTTOM), -2));

        scrolling.installContainerElement(new GuiElement<>(scrolling));
        scrolling.getContentElement()
                .constrain(WIDTH, null)
                .constrain(LEFT, match(scrolling.get(LEFT)))
                .constrain(RIGHT, match(scrolling.get(RIGHT)));

        scrollBar.slider().setSliderState(scrolling.scrollState(Axis.Y));
        scrollBar.slider().setScrollableElement(scrolling);

        List<TagKey<Item>> tagOps = new ArrayList<>();
        ItemStack filterStack = filterStacks.getOrDefault(index, ItemStack.EMPTY);
        if (!filterStack.isEmpty()) {
            tagOps.addAll(filterStack.getTags().toList());
        }

        if (!tagOps.isEmpty()) {
            GuiButton fromItemButton = new GuiButton(root);
            Constraints.size(fromItemButton, 12, 12);
            Constraints.placeOutside(fromItemButton, matchContainer, Constraints.LayoutPos.TOP_RIGHT, -12, -1);
            Constraints.bind(new GuiItemStack(fromItemButton, filterStack).setTooltip(Component.translatable("module." + MODID + ".filtered_module.select_from_item")), fromItemButton);

            fromItemButton.onPress(() -> {
                GuiElement<?> content = scrolling.getContentElement();
                content.getChildren().forEach(content::removeChild);
                scrolling.scrollState(Axis.Y).setPos(0);
                matchingLabel.setText(Component.translatable("module." + MODID + ".filtered_module.select_or_enter"));

                int yOffset = 0;
                for (TagKey<Item> tag : tagOps) {
                    GuiButton button = GuiButton.flatColourButton(content, () -> Component.literal(tag.location().toString()), GuiToolkit.Palette.Ctrl::fill)
                            .constrain(HEIGHT, literal(12))
                            .constrain(LEFT, match(content.get(LEFT)))
                            .constrain(RIGHT, match(content.get(RIGHT)))
                            .constrain(TOP, relative(content.get(TOP), yOffset))
                            .setTooltip(Component.literal(tag.location().toString()));
                    button.getLabel().setTrim(true);
                    button.onPress(() -> {
                        filterTags.remove(index); //Ensures the scroll element is reloaded even if this tag was already selected.
                        textField.setValue(tag.location().toString());
                    });
                    yOffset += 13;
                }
            });
        }

        textField.setTextState(TextState.simpleState("", s -> {
            ResourceLocation location = ResourceLocation.tryParse(s);
            TagKey<Item> key = filterTags.get(index);
            if (s.isEmpty() && key == null) return;

            GuiElement<?> content = scrolling.getContentElement();

            if (location == null && filterTags.containsKey(index)) {
                filterTags.remove(index); //Remove old key from server
                sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
                return;
            } else if (location == null || (key != null && location.equals(key.location()) && !content.getChildren().isEmpty())) {
                return;
            }

            key = ItemTags.create(location);

            content.getChildren().forEach(content::removeChild);
            scrolling.scrollState(Axis.Y).setPos(0);
            matchingLabel.setText(Component.translatable("module." + MODID + ".filtered_module.matching"));

            List<Item> matchingItems = FastStream.of(BuiltInRegistries.ITEM.getTagOrEmpty(key)).map(Holder::value).toList();
            if (matchingItems.isEmpty()) {
                filterTags.remove(index);
                sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
                return;
            }

            for (int i = 0; i < matchingItems.size(); i++) {
                Item item = matchingItems.get(i);
                GuiItemStack icon = new GuiItemStack(content, new ItemStack(item));
                Constraints.size(icon, 18, 18);
                Constraints.placeInside(icon, content, Constraints.LayoutPos.TOP_LEFT, (int) (i % 7) * 19, (int) (i / 7) * 19D);
            }

            filterTags.put(index, key);
            sendMessageToServer(e -> e.writeCompoundNBT(writeExtraData(new CompoundTag())));
        }));

        textField.setEnterPressed(dialog::close);

        if (filterTags.containsKey(index)) {
            textField.setValue(filterTags.get(index).location().toString());
        }
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
