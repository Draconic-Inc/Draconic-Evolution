package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.inventory.InventoryUtils;
import com.brandon3055.brandonscore.api.BCStreamCodec;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.NoData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ItemData;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 21/01/2023
 */
public class EnderCollectionEntity extends FilteredModuleEntity<NoData> {

    protected CompoundTag frequencyTag = new CompoundTag();
    protected BooleanProperty filterEnabled = createEnabledProperty("ender_collection_mod", false);

    public static final Codec<EnderCollectionEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(ModuleEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            TAGS_CODEC.fieldOf("filter_tags").forGetter(e -> e.filterTags),
            STACKS_CODEC.fieldOf("filter_stacks").forGetter(e -> e.filterStacks),
            CompoundTag.CODEC.fieldOf("frequency_tag").forGetter(e -> e.frequencyTag),
            BooleanProperty.CODEC.fieldOf("enabled").forGetter(e -> e.filterEnabled)
    ).apply(builder, EnderCollectionEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnderCollectionEntity> STREAM_CODEC = BCStreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, BCStreamCodec.tagKeyCodec(Registries.ITEM)), e -> e.filterTags,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, ItemStack.STREAM_CODEC), e -> e.filterStacks,
            ByteBufCodecs.COMPOUND_TAG, e -> e.frequencyTag,
            BooleanProperty.STREAM_CODEC, e -> e.filterEnabled,
            EnderCollectionEntity::new
    );

    public EnderCollectionEntity(Module<NoData> module) {
        super(module, module.getProperties().getTechLevel() == TechLevel.DRACONIC ? 9 : 0);
//        addEnabledProperty("ender_collection_mod", false);
    }

    EnderCollectionEntity(Module<?> module, int gridX, int gridY, Map<Integer, TagKey<Item>> filterTags, Map<Integer, ItemStack> filterStacks, CompoundTag frequencyTag, BooleanProperty filterEnabled) {
        super((Module<NoData>) module, gridX, gridY, module.getProperties().getTechLevel() == TechLevel.DRACONIC ? 9 : 0, filterTags, filterStacks);
        this.frequencyTag = frequencyTag;
        this.filterEnabled = filterEnabled;
    }

    @Override
    public ModuleEntity<?> copy() {
        Map<Integer, ItemStack> stacks = new HashMap<>();
        filterStacks.forEach((integer, stack) -> stacks.put(integer, stack.copy()));
        return new EnderCollectionEntity(module, getGridX(), getGridY(), new HashMap<>(filterTags), stacks, frequencyTag.copy(), filterEnabled.copy());
    }

    @Override
    public void getEntityProperties(List<ConfigProperty> properties) {
        super.getEntityProperties(properties);
        properties.add(filterEnabled);
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
    @OnlyIn (Dist.CLIENT)
    protected Material getSlotOverlay() {
        return BCGuiTextures.get("slots/filter");
    }

    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        super.saveEntityToStack(stack, context);
        stack.set(ItemData.BOOL_ITEM_PROP_1, filterEnabled.copy());
        stack.set(ItemData.ENDER_MODULE_FREQUENCY, CustomData.of(frequencyTag));
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        super.loadEntityFromStack(stack, context);
        filterEnabled = stack.getOrDefault(ItemData.BOOL_ITEM_PROP_1, filterEnabled).copy();
        frequencyTag = stack.getOrDefault(ItemData.ENDER_MODULE_FREQUENCY, CustomData.EMPTY).copyTag();
    }

    public List<ItemStack> insertStacks(Player player, Collection<ItemStack> stacks, IOPStorage opStorage) {
        if (opStorage == null) return new ArrayList<>(stacks);
        Container container;
        if (ModHelper.ENDERSTORAGE) {
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
        if (ModHelper.ENDERSTORAGE) {
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
        return new EnumColour[]{frequency.left(), frequency.middle(), frequency.right()};
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void renderModule(GuiElement<?> parent, GuiRender render, int x, int y, int width, int height, double mouseX, double mouseY, boolean renderStack, float partialTicks) {
        super.renderModule(parent, render, x, y, width, height, mouseX, mouseY, renderStack, partialTicks);
        if (frequencyTag.isEmpty() || !ModHelper.ENDERSTORAGE) {
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
        float hp = 1 / 32F;
        for (int i = 0; i < 3; i++) {
            float px = mid - (w / 2F) - (p * 3) + (i * (p * 3));
            float u = (hp * 3) + ((hp * 8) * (colours[i].getWoolMeta() % 4));
            //noinspection IntegerDivisionInFloatingPointContext
            float v = (hp * 2) + ((hp * 8) * (colours[i].getWoolMeta() / 4));
            render.partialSprite(mat.renderType(GuiRender::texColType), px, py, px + w, py + h, mat.sprite(), u, v, u + (hp * 2), v + (hp * 4), 1, 1, 1, alpha);
        }
        if (alpha != 1) {
            render.pose().translate(0, 0, -201);
        }
    }
}
