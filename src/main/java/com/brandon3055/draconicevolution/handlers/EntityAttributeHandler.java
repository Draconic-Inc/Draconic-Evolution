package com.brandon3055.draconicevolution.handlers;

import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 19/01/2024
 */
public class EntityAttributeHandler<Data> {

    private final Map<UUID, AttribData> modifiers = new HashMap<>();

    public void register(UUID attribKey, Supplier<Attribute> attribute, BiFunction<LivingEntity, Data, @Nullable AttributeModifier> modifierFunc) {
        modifiers.put(attribKey, new AttribData(attribute, SneakyUtils.unsafeCast(modifierFunc)));
    }

    public void updateEntity(LivingEntity entity, Data extraData) {
        modifiers.forEach((uuid, data) -> {
            AttributeModifier newMod = data.modifierFunc.apply(entity, extraData);
            AttributeInstance attribute = entity.getAttribute(data.attribute.get());
            if (attribute == null) return;
            attribute.removeModifier(uuid);
            if (newMod != null) {
                attribute.addTransientModifier(newMod);
            }
        });
    }

    private record AttribData(Supplier<Attribute> attribute, BiFunction<LivingEntity, Object, @Nullable AttributeModifier> modifierFunc) {}
}
