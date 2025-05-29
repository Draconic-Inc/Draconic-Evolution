package com.brandon3055.draconicevolution.client;

import com.brandon3055.brandonscore.client.particle.IntParticleData;
import com.brandon3055.draconicevolution.client.render.particle.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 23/4/2016.
 * A list of all of DE's particles
 */
public class DEParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MODID);

    public static void init(IEventBus modBus) {
        PARTICLE_TYPES.register(modBus);
        modBus.addListener(DEParticles::registerFactories);
    }

    //@formatter:off
    public static final DeferredHolder<ParticleType<?>, ParticleType<IntParticleData>>          FLAME                   = PARTICLE_TYPES.register("flame",                  () -> register(false, IntParticleData::codec, IntParticleData::streamCodec));
    public static final DeferredHolder<ParticleType<?>, ParticleType<IntParticleData>>          LINE_INDICATOR          = PARTICLE_TYPES.register("line_indicator",         () -> register(false, IntParticleData::codec, IntParticleData::streamCodec));
    public static final DeferredHolder<ParticleType<?>, ParticleType<IntParticleData>>          ENERGY                  = PARTICLE_TYPES.register("energy",                 () -> register(false, IntParticleData::codec, IntParticleData::streamCodec));
    public static final DeferredHolder<ParticleType<?>, ParticleType<IntParticleData>>          ENERGY_BASIC            = PARTICLE_TYPES.register("energy_basic",           () -> register(false, IntParticleData::codec, IntParticleData::streamCodec));
    public static final DeferredHolder<ParticleType<?>, ParticleType<IntParticleData>>          ENERGY_CORE             = PARTICLE_TYPES.register("energy_core",            () -> register(false, IntParticleData::codec, IntParticleData::streamCodec));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType>                     GUARDIAN_PROJECTILE     = PARTICLE_TYPES.register("guardian_projectile",    () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType>                     BLINK                   = PARTICLE_TYPES.register("blink",                  () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType>                     GUARDIAN_CLOUD          = PARTICLE_TYPES.register("guardian_cloud",         () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType>                     GUARDIAN_BEAM           = PARTICLE_TYPES.register("guardian_beam",          () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, ParticleType<IntParticleData>>          SPARK                   = PARTICLE_TYPES.register("spark",                  () -> register(false, IntParticleData::codec, IntParticleData::streamCodec));
    //@formatter:on

    @OnlyIn (Dist.CLIENT)
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FLAME.get(), CustomFlameParticle.Factory::new);
        event.registerSpriteSet(LINE_INDICATOR.get(), ParticleLineIndicator.Factory::new);
        event.registerSpriteSet(ENERGY.get(), ParticleEnergy.Factory::new);
        event.registerSpriteSet(ENERGY_BASIC.get(), ParticleEnergyBasic.Factory::new);
        event.registerSpriteSet(ENERGY_CORE.get(), ParticleEnergyCoreFX.Factory::new);
        event.registerSpriteSet(GUARDIAN_PROJECTILE.get(), GuardianProjectileParticle.Factory::new);
        event.registerSpriteSet(BLINK.get(), BlinkParticle.Factory::new);
        event.registerSpriteSet(GUARDIAN_CLOUD.get(), GuardianCloudParticle.Factory::new);
        event.registerSpriteSet(GUARDIAN_BEAM.get(), GuardianBeamParticle.Factory::new);
        event.registerSpriteSet(SPARK.get(), SparkParticle.Factory::new);
    }


    @OnlyIn (Dist.CLIENT)
    public static Particle addParticleDirect(Level world, Particle particle) {
        if (world instanceof ClientLevel) {
            Minecraft mc = Minecraft.getInstance();
            Camera activerenderinfo = mc.gameRenderer.getMainCamera();
            if (mc != null && activerenderinfo.isInitialized() && mc.particleEngine != null) {
                mc.particleEngine.add(particle);
                return particle;
            }
        }
        return null;
    }

    private static <T extends ParticleOptions> ParticleType<T> register(
            boolean overrideLimiter,
            final Function<ParticleType<T>, MapCodec<T>> codec,
            final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodec
    ) {
        return new ParticleType<T>(overrideLimiter) {
            @Override
            public MapCodec<T> codec() {
                return codec.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec.apply(this);
            }
        };
    }
}
