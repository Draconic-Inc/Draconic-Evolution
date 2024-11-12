package com.brandon3055.draconicevolution.client.render.tile.fxhandlers;

import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionStateMachine.FusionState;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingInjector;
import com.brandon3055.draconicevolution.client.sound.FusionRotationSound;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by brandon3055 on 12/7/21
 * Client side effect handler for fusion crafting core
 * Everything created by this should be completely based on the current tile state.
 * I dont want to have to deal with any saving, loading or synchronizing data.
 * Everything i should need is already synced by the tile.
 */
public class FusionTileFXHandler implements ITileFXHandler {
    private static Random rand = new Random();
    private TileFusionCraftingCore core;
    private float rotationTick = 0;
    private float rotationSpeed = 0;
    private int coreDischarge = -1;

    private int baseCraftTime = 300;
    private int translateStartTime = 0;//15;
    private int rotStartTime = 30;// + 15;
    private int beamStartTime = 60;// + 15;
    private int dieOutStart = 100;
    private float animRadius = 2;
    public float injectTime = 0;
    public float chargeState = 0;
    private int runTick = 0;
    private FusionRotationSound sound = null;

    public FusionTileFXHandler(TileFusionCraftingCore core) {
        this.core = core;
    }

    @Override
    public void tick() {
        RecipeHolder<IFusionRecipe> recipe;
        if (!core.isCrafting() || (recipe = core.getActiveRecipe()) == null) {
            rotationTick = -3;
            sound = null;
            injectTime = 0;
            chargeState = 0;
            runTick = -1;
            return;
        }

        FusionState state = core.getFusionState();
        if (state.ordinal() < FusionState.CRAFTING.ordinal()) {
            rotationTick = -3;
            rotationSpeed = 0;
            injectTime = 0;
            runTick = -1;
        } else {
            float prevTick = rotationTick;
            if (runTick <= 0) {
                core.getLevel().playLocalSound(core.getBlockPos().getX() + 0.5, core.getBlockPos().getY() + 0.5, core.getBlockPos().getZ() + 0.5, DESounds.FUSION_COMPLETE.get(), SoundSource.BLOCKS, 0.5F, 0.5F, false);
            }
            if (runTick == -1) {
                Vector3 corePos = Vector3.fromTileCenter(core);
                getIngredients(0).forEach(e -> core.getLevel().addParticle(ParticleTypes.EXPLOSION, corePos.x + e.pos.x, corePos.y + e.pos.y, corePos.z + e.pos.z, 1.0D, 0.0D, 0.0D));
            }
            rotationTick += rotationSpeed;
            runTick++;
            rotationSpeed = ((float) baseCraftTime / Math.max(core.craftAnimLength.get(), 1));
            if (rotationTick + 3 >= rotStartTime && prevTick + 3 < rotStartTime + 3) {
                core.getLevel().playLocalSound(core.getBlockPos().getX() + 0.5, core.getBlockPos().getY() + 0.5, core.getBlockPos().getZ() + 0.5, DESounds.FUSION_COMPLETE.get(), SoundSource.BLOCKS, 2F, 0.5F, false);
                if (sound == null) {
                    sound = new FusionRotationSound(core);
                    sound.setPitch(0.5F + (1.5F * (rotationSpeed - 1F)));
                    Minecraft.getInstance().getSoundManager().play(sound);
                }
            }
            injectTime = Math.max(0, (rotationTick - beamStartTime) / (float) (baseCraftTime - beamStartTime));
            if (injectTime > 0) {
                if (TimeKeeper.getClientTick() % 5 == 0) {
                    core.getLevel().playLocalSound(core.getBlockPos().getX() + 0.5, core.getBlockPos().getY() + 0.5, core.getBlockPos().getZ() + 0.5, DESounds.ENERGY_BOLT.get(), SoundSource.BLOCKS, 1F, 1F, false);
                }
            }
        }


        long totalCharge = core.getInjectors()
                .stream()
                .mapToLong(IFusionInjector::getInjectorEnergy)
                .sum();
        chargeState = totalCharge / (float)recipe.value().getEnergyCost();
        float arcChance = (chargeState * (1F/10F)) + (core.craftAnimProgress.get() * (1/5F)) + (rotationSpeed > 1 ? ((rotationSpeed - 1) * 0.25F) : 0F);

        if (coreDischarge != -1) {
            coreDischarge = -1;
        } else if (rand.nextFloat() < arcChance) {
            List<IngredFX> ingreds = getIngredients(0);
            if (ingreds.isEmpty()) return;
            coreDischarge = rand.nextInt(ingreds.size());
            Vector3 pos = Vector3.fromTileCenter(core).add(ingreds.get(coreDischarge).pos);
            core.getLevel().playLocalSound(pos.x, pos.y, pos.z, DESounds.ENERGY_BOLT.get(), SoundSource.BLOCKS, 2F, 1F, false);
        }
    }

    public List<IngredFX> getIngredients(float partialTicks) {
        List<IngredFX> ingredFXES = new ArrayList<>();
        Vector3 corePos = Vector3.fromTileCenter(core);
        int injCount = (int) core.getInjectors().stream().filter(e -> !e.getInjectorStack().isEmpty()).count();

        double baseRotateSpeed = 16; //Rotations per minute
        int animLen = core.craftAnimLength.get();
        if (animLen != 0) {
            double multiplier = 300D / animLen;
            baseRotateSpeed *= multiplier;
        }

        baseRotateSpeed /= 1200; //Convert to rotations per tick
        baseRotateSpeed *= Math.PI * 2; //Convert to Radians per tick

        float rotateAnim = getRotationAnim(partialTicks);
        int i = 0;
        for (IFusionInjector iInjector : core.getInjectors()) {
            if (iInjector.getInjectorStack().isEmpty()) {
                continue;
            }

            TileFusionCraftingInjector injector = (TileFusionCraftingInjector) iInjector;
            Vector3 injPos = Vector3.fromTileCenter(injector).subtract(corePos);
            injPos.add(Vector3.fromVec3i(injector.getRotation().getNormal()).multiply(.45));

            float startAngle = ((i / (float)injCount) * (float)Math.PI * 2F);
            startAngle += (rotateAnim >= rotStartTime ? rotateAnim - rotStartTime : 0) * baseRotateSpeed;
            double x = Mth.cos(startAngle) * animRadius;
            double z = Mth.sin(startAngle) * animRadius;
            Vector3 animPos = new Vector3(x, 0, z);
            if (rotateAnim < rotStartTime) {
                animPos = MathUtils.interpolateVec3(injPos, animPos, rotationTick - translateStartTime > 0 ? (rotateAnim  - translateStartTime) / (rotStartTime  - translateStartTime) : 0);
            }

            IngredFX ingredFX = new IngredFX(animPos, injector);
            if (i == coreDischarge) {
                ingredFX.arcPos = Vector3.ZERO;
            }
            if (rotateAnim > 0) {
                ingredFX.coreAnim = Math.min(1, (rotateAnim / translateStartTime) * 2F);
            }

            ingredFX.beamAnim = rotateAnim - beamStartTime;
            ingredFX.dieOut = Mth.clamp(1F - ((rotateAnim - dieOutStart) / (baseCraftTime - dieOutStart)), 0F, 1F);
            ingredFXES.add(ingredFX);
            i++;
        }

        return ingredFXES;
    }

    public boolean renderActive() {
        return core.isCrafting();
    }

    public float getRotationAnim(float partialTicks) {
        return rotationTick + (rotationSpeed * partialTicks);
    }

    public static class IngredFX {
        /** FX position relative to center of core*/
        public Vector3 pos;
        private IFusionInjector injector;
        public Vector3 arcPos = null;
        public float beamAnim = 0;
        public float coreAnim = 0;
        public float dieOut = 1;
        public IngredFX(Vector3 pos, IFusionInjector injector) {
            this.pos = pos;
            this.injector = injector;
        }

        public float getChargeAnim(float partialTicks) {
            return TimeKeeper.getClientTick() + partialTicks; //TODO Maybe get this from the client side injector so i can have control over anim speed based on charge state.
        }

        public float getCharge() {
            return injector.getInjectorEnergy() / (float)injector.getEnergyRequirement();
        }
    }
}
