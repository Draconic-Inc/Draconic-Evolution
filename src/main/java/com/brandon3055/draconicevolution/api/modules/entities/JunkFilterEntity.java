package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.gui.modular.sprite.Material;
import com.brandon3055.brandonscore.api.BCStreamCodec;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.NoData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.ItemData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 21/01/2023
 */
public class JunkFilterEntity extends FilteredModuleEntity<NoData> {

    protected BooleanProperty filterEnabled = createEnabledProperty("junk_filter_mod", true);

    public static final Codec<JunkFilterEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(ModuleEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            TAGS_CODEC.fieldOf("filter_tags").forGetter(e -> e.filterTags),
            STACKS_CODEC.fieldOf("filter_stacks").forGetter(e -> e.filterStacks),
            BooleanProperty.CODEC.fieldOf("enabled").forGetter(e -> e.filterEnabled)
    ).apply(builder, JunkFilterEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, JunkFilterEntity> STREAM_CODEC = BCStreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, BCStreamCodec.tagKeyCodec(Registries.ITEM)), e -> e.filterTags,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, ItemStack.STREAM_CODEC), e -> e.filterStacks,
            BooleanProperty.STREAM_CODEC, e -> e.filterEnabled,
            JunkFilterEntity::new
    );

    public static <T> Codec<Map<Integer, T>> intObjectMap(Codec<T> codec) {
        return Codec.unboundedMap(Codec.INT, codec);
    }

    public JunkFilterEntity(Module<NoData> module) {
        super(module, 9);
    }

    JunkFilterEntity(Module<?> module, int gridX, int gridY, Map<Integer, TagKey<Item>> filterTags, Map<Integer, ItemStack> filterStacks, BooleanProperty filterEnabled) {
        super((Module<NoData>) module, gridX, gridY, 9, filterTags, filterStacks);
        this.filterEnabled = filterEnabled;
    }

    @Override
    public ModuleEntity<?> copy() {
        Map<Integer, ItemStack> stacks = new HashMap<>();
        filterStacks.forEach((integer, stack) -> stacks.put(integer, stack.copy()));
        return new JunkFilterEntity(module, getGridX(), getGridY(), new HashMap<>(filterTags), stacks, filterEnabled.copy());
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
    @OnlyIn(Dist.CLIENT)
    protected Material getSlotOverlay() {
        return BCGuiTextures.get("slots/trash");
    }

    @Override
    public boolean isEnabled() {
        return filterEnabled.getValue();
    }

    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        super.saveEntityToStack(stack, context);
        stack.set(ItemData.BOOL_ITEM_PROP_1, filterEnabled.copy());
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        super.loadEntityFromStack(stack, context);
        filterEnabled = stack.getOrDefault(ItemData.BOOL_ITEM_PROP_1, filterEnabled).copy();
    }
}
