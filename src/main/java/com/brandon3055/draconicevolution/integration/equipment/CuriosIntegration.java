package com.brandon3055.draconicevolution.integration.equipment;

import com.brandon3055.brandonscore.capability.MultiCapabilityProvider;
import com.brandon3055.brandonscore.inventory.PlayerSlot;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.data.TagsProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 6/1/21
 */
public class CuriosIntegration extends EquipmentManager {

    public static final Tags.IOptionalNamedTag<Item> CURIO_TAG = ItemTags.createOptional(new ResourceLocation("curios", "curio"));
    public static final Tags.IOptionalNamedTag<Item> BODY_TAG = ItemTags.createOptional(new ResourceLocation("curios", "body"));

    @CapabilityInject(ICurio.class)
    public static Capability<ICurio> CURIO_CAP = null;

    public static void sendIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CURIO.getMessageBuilder().build());
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
                .findEquippedCurio(item, entity)
                .map(ImmutableTriple::getRight)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack findMatchingItem(Predicate<ItemStack> predicate, LivingEntity entity) {
        return CuriosApi.getCuriosHelper()
                .findEquippedCurio(predicate, entity)
                .map(ImmutableTriple::getRight)
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Data Gen
     */
    public static void generateTags(Function<Tags.IOptionalNamedTag<Item>, TagsProvider.Builder<Item>> builder) {
        builder.apply(CURIO_TAG).add(
                DEContent.dislocator_advanced,
                DEContent.magnet,
                DEContent.magnet_advanced,
                DEContent.capacitor_wyvern,
                DEContent.capacitor_draconic,
                DEContent.capacitor_chaotic,
                DEContent.capacitor_creative);

        builder.apply(BODY_TAG).add(DEContent.chestpiece_wyvern, DEContent.chestpiece_draconic, DEContent.chestpiece_chaotic);
    }
}
