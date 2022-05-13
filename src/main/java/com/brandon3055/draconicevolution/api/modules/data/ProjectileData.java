package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class ProjectileData implements ModuleData<ProjectileData> {
    private final float velocity;
    private final float accuracy;
    private final float antiGrav;
    private final float penetration;
    private final float damage;

    public ProjectileData(float velocity, float accuracy, float antiGrav, float penetration, float damage) {
        this.accuracy = accuracy;
        this.antiGrav = antiGrav;
        this.penetration = penetration;
        this.velocity = velocity;
        this.damage = damage;
    }

    public float getAccuracy() {
        return accuracy > 1 ? 1 : accuracy;
    }

    public float getVelocity() {
        return velocity;
    }

    public float getAntiGrav() {
        return antiGrav > 1 ? 1 : antiGrav;
    }

    public float getPenetration() {
        return penetration;
    }

    public float getDamage() {
        return damage;
    }

    @Override
    public ProjectileData combine(ProjectileData other) {
        return new ProjectileData(velocity + other.velocity, accuracy + other.accuracy, antiGrav + other.antiGrav, penetration + other.penetration, damage + other.damage);
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context, boolean stack) {
        if (getVelocity() != 0) {
            int vel = Math.round((getVelocity() * 100));
            map.put(new TranslatableComponent("module.draconicevolution.proj_velocity.name"), new TranslatableComponent("module.draconicevolution.proj_velocity.value", vel > 0 ? "+" + vel : vel, Math.round(60 * (getVelocity() + 1))));
        }
        if (getAccuracy() != 0) {
            int acc = Math.round(getAccuracy() * -100);
            map.put(new TranslatableComponent("module.draconicevolution.proj_accuracy.name"), new TranslatableComponent("module.draconicevolution.proj_accuracy.value", acc > 0 ? "+" + acc : acc));
        }
        if (getAntiGrav() != 0) {
            int antiGrav = Math.round(getAntiGrav() * 100);
            map.put(new TranslatableComponent("module.draconicevolution.proj_grav_comp.name"), new TranslatableComponent("module.draconicevolution.proj_grav_comp.value", antiGrav > 0 ? "+" + antiGrav : antiGrav));
        }
        if (getPenetration() > 0 && (getPenetration() > getVelocity() || !stack)) {
            int pen = Math.round(getPenetration() * 100);
            map.put(new TranslatableComponent("module.draconicevolution.proj_penetration.name"), new TranslatableComponent("module.draconicevolution.proj_penetration.value", pen > 0 ? "+" + pen : pen));
            if (stack) {
                map.put(new TranslatableComponent("module.draconicevolution.proj_penetration.info"), null);
                map.put(new TranslatableComponent("module.draconicevolution.proj_penetration.info2"), null);
            }
        }
        if (getDamage() != 0) {
            int damage = Math.round(getDamage() * 100);
            map.put(new TranslatableComponent("module.draconicevolution.proj_damage.name"), new TranslatableComponent("module.draconicevolution.proj_damage.value", damage > 0 ? "+" + damage : damage));
        }
    }
}
