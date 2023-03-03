package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public record ProjectileData(float velocity, float accuracy, float antiGrav, float penetration, float damage) implements ModuleData<ProjectileData> {

    @Override
    public float accuracy() {
        return accuracy > 1 ? 1 : accuracy;
    }

    @Override
    public float antiGrav() {
        return antiGrav > 1 ? 1 : antiGrav;
    }

    @Override
    public ProjectileData combine(ProjectileData other) {
        return new ProjectileData(velocity + other.velocity, accuracy + other.accuracy, antiGrav + other.antiGrav, penetration + other.penetration, damage + other.damage);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        if (velocity() != 0) {
            int vel = Math.round((velocity() * 100));
            map.put(new TranslatableComponent("module.draconicevolution.proj_velocity.name"), new TranslatableComponent("module.draconicevolution.proj_velocity.value", vel > 0 ? "+" + vel : vel, Math.round(60 * (velocity() + 1))));
        }
        if (accuracy() != 0) {
            int acc = Math.round(accuracy() * -100);
            map.put(new TranslatableComponent("module.draconicevolution.proj_accuracy.name"), new TranslatableComponent("module.draconicevolution.proj_accuracy.value", acc > 0 ? "+" + acc : acc));
        }
        if (antiGrav() != 0) {
            int antiGrav = Math.round(antiGrav() * 100);
            map.put(new TranslatableComponent("module.draconicevolution.proj_grav_comp.name"), new TranslatableComponent("module.draconicevolution.proj_grav_comp.value", antiGrav > 0 ? "+" + antiGrav : antiGrav));
        }
        if (penetration() > 0 && (penetration() > velocity() || !stack)) {
            int pen = Math.round(penetration() * 100);
            map.put(new TranslatableComponent("module.draconicevolution.proj_penetration.name"), new TranslatableComponent("module.draconicevolution.proj_penetration.value", pen > 0 ? "+" + pen : pen));
            if (stack) {
                map.put(new TranslatableComponent("module.draconicevolution.proj_penetration.info"), null);
                map.put(new TranslatableComponent("module.draconicevolution.proj_penetration.info2"), null);
            }
        }
        if (damage() != 0) {
            int damage = Math.round(damage() * 100);
            map.put(new TranslatableComponent("module.draconicevolution.proj_damage.name"), new TranslatableComponent("module.draconicevolution.proj_damage.value", damage > 0 ? "+" + damage : damage));
        }
    }
}
