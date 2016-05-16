package com.brandon3055.draconicevolution.client;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.draconicevolution.client.render.particle.ParticleEnergy;
import com.brandon3055.draconicevolution.client.render.particle.ParticleEnergyCoreFX;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 23/4/2016.
 * A list of all of DE's particles
 */
public class DEParticles {
    private static ResourceLocation DE_SHEET = ResourceHelperDE.getResource("textures/particle/particles.png");

    public static int ENERGY_PARTICLE;
    public static int ENERGY_CORE_FX;


    public static void register(){
        ENERGY_PARTICLE = BCEffectHandler.registerFX(DE_SHEET, new ParticleEnergy.Factory());
        ENERGY_CORE_FX = BCEffectHandler.registerFX(DE_SHEET, new ParticleEnergyCoreFX.Factory());
    }

}
