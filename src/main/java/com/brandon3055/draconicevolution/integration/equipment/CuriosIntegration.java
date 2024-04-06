package com.brandon3055.draconicevolution.integration.equipment;

import com.brandon3055.brandonscore.capability.MultiCapabilityProvider;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.lib.WTFException;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 6/1/21
 */
public class CuriosIntegration extends EquipmentManager {

    public static final TagKey<Item> CURIO_TAG = ItemTags.create(new ResourceLocation("curios", "curio"));
    public static final TagKey<Item> BODY_TAG = ItemTags.create(new ResourceLocation("curios", "body"));

    public static Capability<ICurio> CURIO_CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static void sendIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CURIO.getMessageBuilder().size(2).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BODY.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
    }

    @Override
    public void addEquipCaps(ItemStack stack, MultiCapabilityProvider provider) {
        provider.addUnsavedCap(CURIO_CAP, new CurioWrapper(stack));
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getInventory(LivingEntity entity) {
        return CuriosApi.getCuriosHelper().getEquippedCurios(entity);
    }

    @Override
    public ItemStack findMatchingItem(Item item, LivingEntity entity) {
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(entity, item)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack findMatchingItem(Predicate<ItemStack> predicate, LivingEntity entity) {
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(entity, predicate)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public List<ResourceLocation> getSlotIcons(LivingEntity entity) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity);
        if (optional.isPresent()) {
            ICuriosItemHandler handler = optional.orElseThrow(WTFException::new);
            List<ResourceLocation> icons = new ArrayList<>();
            handler.getCurios().forEach((s, h) -> {
                for (int i = 0; i < h.getSlots(); i++) {
                    ResourceLocation icon = CuriosApi.getSlotIcon(s); //Why couldnt this just be the full path?
                    icons.add(new ResourceLocation(icon.getNamespace(), "textures/" + icon.getPath() + ".png"));
                }
            });
            return icons;
        }
        return Collections.emptyList();
    }

    /**
     * Data Gen
     */
    public static void generateTags(Function<TagKey<Item>, TagsProvider.TagAppender> builder) {
        builder.apply(CURIO_TAG).add(
                DEContent.DISLOCATOR_ADVANCED.getKey(),
                DEContent.MAGNET.getKey(),
                DEContent.MAGNET_ADVANCED.getKey(),
                DEContent.CAPACITOR_WYVERN.getKey(),
                DEContent.CAPACITOR_DRACONIC.getKey(),
                DEContent.CAPACITOR_CHAOTIC.getKey(),
                DEContent.CAPACITOR_CREATIVE.getKey());

        builder.apply(BODY_TAG).add(DEContent.CHESTPIECE_WYVERN.getKey(), DEContent.CHESTPIECE_DRACONIC.getKey(), DEContent.CHESTPIECE_CHAOTIC.getKey());
    }
}
