package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.effect.EffectTrackerCelestialManipulator;
import com.brandon3055.draconicevolution.client.sound.CelestialModifierSound;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.inventory.CelestialManipulatorMenu;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileCelestialManipulator extends TileBCore implements IChangeListener, MenuProvider, IInteractTile {

    public final ManagedBool weatherMode = register(new ManagedBool("weather_mode", true, DataFlags.SAVE_NBT_SYNC_TILE, DataFlags.CLIENT_CONTROL));
    public final ManagedBool active = register(new ManagedBool("active", DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedBool weatherToggleRunning = register(new ManagedBool("weather_toggle_running", DataFlags.SYNC_TILE));
    public final ManagedBool timeWarpRunning = register(new ManagedBool("time_warp_running", DataFlags.SYNC_TILE));
    public final ManagedBool timeWarpStopping = register(new ManagedBool("time_warp_stopping", DataFlags.SYNC_TILE));
    public final ManagedBool redstoneSignal = register(new ManagedBool("redstone_signal", DataFlags.SAVE_NBT_SYNC_TILE));
    public final ManagedByte rsMode = register(new ManagedByte("rs_mode", DataFlags.SAVE_NBT_SYNC_CONTAINER));
    public int timer = 0;
    public boolean storm = false;
    public boolean rain = false;
    public long targetTime;

    public OPStorage opStorage = new ModularOPStorage(this, 8000000, 4000000, 4000000);

    public TileCelestialManipulator(BlockPos pos, BlockState state) {
        super(DEContent.TILE_CELESTIAL_MANIPULATOR.get(), pos, state);
        capManager.setManaged("energy", CapabilityOP.BLOCK, opStorage).saveBoth().syncContainer();
        weatherMode.setCCSCS();
    }

    public static void register(RegisterCapabilitiesEvent event) {
        capability(event, DEContent.TILE_CELESTIAL_MANIPULATOR, CapabilityOP.BLOCK);
    }

    @Override
    public void tick() {
        super.tick();

        if (weatherToggleRunning.get()) {
            timer++;
            if (level.isClientSide) {
                updateWeatherEffects();
            } else {
                if (timer >= 230) {
                    timer = 0;
                    weatherToggleRunning.set(false);
                    level.getLevelData().setRaining(rain);
                    ((ServerLevelData) level.getLevelData()).setThundering(storm);
                    int time = (10 * 60 * 20) + level.random.nextInt(20 * 60 * 20);
                    ((ServerLevelData) level.getLevelData()).setRainTime(rain ? time : 0);
                    ((ServerLevelData) level.getLevelData()).setClearWeatherTime(rain ? 0 : time);
                }
            }
        } else if (timeWarpRunning.get()) {
            timer++;
            if (level.isClientSide) {
                updateSunEffect();
            }
            if (timer > 100) {
                if (opStorage.getEnergyStored() > 320) {
                    int extracted = opStorage.extractEnergy(16000, true);
                    int ticks = extracted / 320;
                    if (level.isClientSide) {
                        ClientLevel cLevel = (ClientLevel) level;
                        cLevel.setDayTime(cLevel.getDayTime() + ticks);
                    } else {
                        ServerLevel sLevel = (ServerLevel) level;
                        sLevel.setDayTime(sLevel.getDayTime() + ticks);
                        opStorage.extractEnergy(ticks * 320, false);
                    }
                } else {
                    stopTimeWarp();
                }
                if (!level.isClientSide && level.getDayTime() >= targetTime) {
                    stopTimeWarp();
                }
            }
        } else if (timeWarpStopping.get()) {
            if (timer > 100) {
                timer = 100;
            }

            timer--;
            if (timer <= 0 && !level.isClientSide) {
                timeWarpStopping.set(false);
            }

            if (level.isClientSide && timer >= 0) {
                updateSunEffect();
            } else if (level.isClientSide) {
                if (effects != null) {
                    effects.clear();
                }
                if (sound != null) {
                    sound.kill();
                    sound = null;
                }
            }
        } else if (active.get()) {
            active.set(false);
        } else if (level.isClientSide) {
            standbyParticleEffect();
        }
    }

    public void toggleWeather(boolean rain, boolean storm) {
        if (level.isClientSide || active.get()) {
            return;
        }

        this.storm = storm;
        this.rain = rain;
        timer = 0;
        weatherToggleRunning.set(true);
        //        sendPacketToClients(new PacketTileMessage(this, (byte) 0, 0, false), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
        active.set(true);
    }

    public void startTimeWarp(long targetTime) {
        active.set(true);
        this.targetTime = targetTime;
        timer = 0;
        timeWarpRunning.set(true);
        //        sendPacketToClients(new PacketTileMessage(this, (byte) 1, 0, false), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
    }

    public void stopTimeWarp() {
        //        sendPacketToClients(new PacketTileMessage(this, (byte) 2, 0, false), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
        timeWarpRunning.set(false);
        timeWarpStopping.set(true);
    }

    //region Interact


    @Override
    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        if (id == 0) {
            handleInteract(data.readString(), client);
        } else if (id == 1) {
            int intValue = data.readInt();
            if (intValue >= 0 && intValue <= 9) {
                rsMode.set((byte) intValue);
            }
        }
    }

    public void handleInteract(String action, Player player) {
        if (action.endsWith("STOP") && active.get() && timeWarpRunning.get()) {
            stopTimeWarp();
            return;
        }

        if (active.get()) {
            sendMessage(Component.translatable("msg." + DraconicEvolution.MODID + ".celestial_manipulator.alreadyRunning"), player);
            return;
        }

        switch (action) {
            case "STOP_RAIN":
                if (!level.isRaining()) {
                    sendMessage(Component.translatable("msg." + DraconicEvolution.MODID + ".celestial_manipulator.notRaining"), player);
                    return;
                }
                if (opStorage.getEnergyStored() < 256000) {
                    sendMessage(Component.translatable("msg." + DraconicEvolution.MODID + ".celestial_manipulator.insufficientPower").append(" (256000RF)"), player);
                    return;
                }
                opStorage.modifyEnergyStored(-256000);
                toggleWeather(false, false);
                LogHelper.info("Stopped rain! Cause: " + worldPosition);
                return;
            case "START_RAIN":
                if (level.isRaining()) {
                    sendMessage(Component.translatable("msg." + DraconicEvolution.MODID + ".celestial_manipulator.alreadyRaining"), player);
                    return;
                }
                if (opStorage.getEnergyStored() < 256000) {
                    sendMessage(Component.translatable("msg." + DraconicEvolution.MODID + ".celestial_manipulator.insufficientPower").append(" (256000RF)"), player);
                    return;
                }
                opStorage.modifyEnergyStored(-256000);
                toggleWeather(true, false);
                LogHelper.info("Started rain! Cause: " + worldPosition);
                return;
            case "START_STORM":
                if (level.isRaining() && level.isThundering()) {
                    sendMessage(Component.translatable("msg." + DraconicEvolution.MODID + ".celestial_manipulator.alreadyStorming"), player);
                    return;
                }
                if (opStorage.getEnergyStored() < 384000) {
                    sendMessage(Component.translatable("msg." + DraconicEvolution.MODID + ".celestial_manipulator.insufficientPower").append(" (384000RF)"), player);
                    return;
                }
                opStorage.modifyEnergyStored(-384000);
                toggleWeather(true, true);
                LogHelper.info("Started storm! Cause: " + worldPosition);
                return;
            case "SUN_RISE":
                startTimeWarp(level.getDayTime() + calculateTimeTill(0));
                LogHelper.info("Set time to sunrise! Cause: " + worldPosition);
                break;
            case "MID_DAY":
                startTimeWarp(level.getDayTime() + calculateTimeTill(5900));
                LogHelper.info("Set time to midday! Cause: " + worldPosition);
                break;
            case "SUN_SET":
                startTimeWarp(level.getDayTime() + calculateTimeTill(12000));
                LogHelper.info("Set time to sunset! Cause: " + worldPosition);
                break;
            case "MOON_RISE":
                startTimeWarp(level.getDayTime() + calculateTimeTill(13000));
                LogHelper.info("Set time to moonrise! Cause: " + worldPosition);
                break;
            case "MIDNIGHT":
                startTimeWarp(level.getDayTime() + calculateTimeTill(17900));
                LogHelper.info("Set time to midnight! Cause: " + worldPosition);
                break;
            case "MOON_SET":
                startTimeWarp(level.getDayTime() + calculateTimeTill(22500));
                LogHelper.info("Set time to moonset! Cause: " + worldPosition);
                break;
            case "SKIP_24":
                startTimeWarp(level.getDayTime() + 24000);
                LogHelper.info("Skipped one day! Cause: " + worldPosition);
                break;
        }
    }

    private void sendMessage(Component message, Player player) {
        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    private int calculateTimeTill(int time) {
        int currentTime = (int) (level.getDayTime() % 24000);
        return currentTime > time ? (24000 - (currentTime - time)) : time - currentTime;
    }

    //endregion

    @OnlyIn (Dist.CLIENT)
    private CelestialModifierSound sound;

    //region Weather Effect

    @OnlyIn(Dist.CLIENT)
    private List<EffectTrackerCelestialManipulator> effects;

    @OnlyIn(Dist.CLIENT)
    public void startWeatherEffect() {
        timer = 0;

        effects = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            effects.add(new EffectTrackerCelestialManipulator(level, Vec3D.getCenter(worldPosition).add(0, 1.5, 0), Vec3D.getCenter(worldPosition).add(0, 1.5, 0)));
        }

        Vec3D vec = Vec3D.getCenter(worldPosition.offset(0, 1, 0));
        sound = new CelestialModifierSound(DESounds.ELECTRIC_BUZZ.get(), worldPosition, level.random);
        sound.updateSound(vec, 0.01F, 0.5F);
        Minecraft.getInstance().getSoundManager().play(sound);
        level.playLocalSound(vec.x, vec.y, vec.z, DESounds.FUSION_COMPLETE.get(), SoundSource.BLOCKS, getSoundVolume(), 0.5F, false);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateWeatherEffects() {
        if (effects == null || effects.size() < 8 || sound == null) {
            startWeatherEffect();
            return;
        }

        double riseEnd = 20;
        double expandStart = 60;
        double expandEnd = 100;
        double ascendStart = 140;
        double ascendStop = 200;
        double ascendHeight = 500;

        double height = (Math.min(riseEnd, timer) / riseEnd) * 3D;
        double expansion = timer > expandStart ? ((Math.min(expandEnd, timer) - expandStart) / (expandEnd - expandStart)) : 0D;
        double secondaryExpand = 0;

        double ascPos = 0;

        if (timer > ascendStart) {
            ascPos = (timer - ascendStart) / (ascendStop - ascendStart);
            height += ascPos * (ascendHeight * ascPos);
            secondaryExpand = ascPos * ascPos * 100D;
        }

        Vec3D effectFocus = Vec3D.getCenter(worldPosition).add(0, height, 0);


//        if (timer < )


        double scaleTime = (timer - expandStart) / (ascendStart - expandStart);
        if (timer > expandStart && timer < ascendStart) {
            for (int i = 0; i < 10; i++) {
                Direction dir = Direction.values()[2 + level.random.nextInt(4)];
                IntParticleType.IntParticleData data = new IntParticleType.IntParticleData(DEParticles.SPARK.get(),
                        0, //R
                        127,  //G
                        255,  //B
                        (int) (1F * (level.random.nextFloat() + 0.1) * 100), //Scale
                        (int) (5D * scaleTime * 100), //Spark scale
                        10, 0, //Max Age, Additional random age
                        (int) (0), //Gravity
                        (int) (1F * 1000), //Friction
                        1 //Set velocity direct
                );
                double speed = 0.3 / 10;
                Vector3 pos = Vector3.fromTileCenter(this).add(dir.getStepX() * -0.3, 0, dir.getStepZ() * -0.3);
                level.addParticle(data, pos.x, pos.y, pos.z, speed * dir.getStepX(), 0, speed * dir.getStepZ());
            }
        }

        if (timer == ascendStart) {
            for (int i = 0; i < 100; i++) {
                IntParticleType.IntParticleData data = new IntParticleType.IntParticleData(DEParticles.SPARK.get(),
                        0, //R
                        127,  //G
                        255,  //B
                        (int) (5F * (level.random.nextFloat() + 0.1) * 100), //Scale
                        (int) (100 * 100), //Spark scale
                        80, 0, //Max Age, Additional random age
                        (int) (-0.5 * 1000), //Gravity
                        (int) (1F * 1000) //Friction
                        //Set velocity direct
                );
                Vector3 pos = Vector3.fromTileCenter(this);
                level.addParticle(data, pos.x, pos.y, pos.z, 0.5 * level.random.nextGaussian(), 0, 0.5 * level.random.nextGaussian());
            }
        }


        if (timer == riseEnd) {
//            level.playLocalSound(effectFocus.x, effectFocus.y, effectFocus.z, DESounds.fusionComplete, SoundSource.BLOCKS, 1F, 1F, false);
        } else if (timer == ascendStart) {
            level.playLocalSound(effectFocus.x, effectFocus.y, effectFocus.z, DESounds.FUSION_COMPLETE.get(), SoundSource.BLOCKS, 1F, 2F, false);
            for (int i = 0; i < 100; i++) {
                try {
                    //                    SubParticle particle = new SubParticle(world, effects.get(world.rand.nextInt(effects.size())).pos);
                    //                    particle.setScale(2);
                    //TODO particles
                    //                    BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, particle, 128, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        sound.updateSound(effectFocus.copy().add(0, -(height * 0.8), 0), (float) expansion, (float) expansion + 0.5F);

        for (EffectTrackerCelestialManipulator effect : effects) {
            effect.onUpdate();
        }

        double rotation = (double) timer * (expansion * 0.05D);
        for (EffectTrackerCelestialManipulator effect : effects) {
            effect.effectFocus = effectFocus;
            effect.scale = (float) expansion * (1F + (float) (ascPos * 5));

            double indexPos = (double) effects.indexOf(effect) / (double) effects.size();
            double offset = indexPos * (Math.PI * 2);
            double offsetX = Math.sin(rotation + offset) * expansion * (3 + secondaryExpand);
            double offsetZ = Math.cos(rotation + offset) * expansion * (3 + secondaryExpand);

            effect.pos = effectFocus.copy().add(offsetX, 0, offsetZ);

            offset = (indexPos + (1D / (double) effects.size())) * (Math.PI * 2);
            offsetX = Math.sin(rotation + offset) * expansion * (3 + secondaryExpand);
            offsetZ = Math.cos(rotation + offset) * expansion * (3 + secondaryExpand);
            effect.linkPos = effectFocus.copy().add(offsetX, 0, offsetZ);
        }

        if (timer >= 220) {
            level.playLocalSound(effectFocus.x, effectFocus.y, effectFocus.z, DESounds.BOOM.get(), SoundSource.BLOCKS, DEOldConfig.disableLoudCelestialManipulator ? 1 : 100, 1F, false);
            timer = 0;
            weatherToggleRunning.set(false);
            effects.clear();
            sound.kill();
            sound = null;
        }
    }

    //endregion

    //region Sun Effect

    @OnlyIn(Dist.CLIENT)
    public void startSunEffect() {
        timeWarpRunning.set(true);
        timeWarpStopping.set(false);
        timer = 0;

        effects = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            effects.add(new EffectTrackerCelestialManipulator(level, Vec3D.getCenter(worldPosition).add(0, 1.5, 0), Vec3D.getCenter(worldPosition).add(0, 1.5, 0)));
        }

        effects.get(0).red = 1F;
        effects.get(0).green = 0.821F;
        effects.get(0).blue = 0.174F;
        effects.get(0).renderBolts = false;

        effects.get(1).red = 1F;
        effects.get(1).green = 1F;
        effects.get(1).blue = 1F;
        effects.get(1).renderBolts = false;

        sound = new CelestialModifierSound(DESounds.SUN_DIAL_EFFECT.get(), worldPosition, level.random);
        sound.updateSound(Vec3D.getCenter(worldPosition), getSoundVolume(), 0.5F);
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    @OnlyIn(Dist.CLIENT)
    public void stopSunEffect() {
        timeWarpRunning.set(false);
        timeWarpStopping.set(true);
        if (timer > 100) {
            timer = 100;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void updateSunEffect() {
        if (effects == null || effects.size() < 2 || sound == null) {
            startSunEffect();
        }

        double depProg = Math.min(timer / 100F, 1D);

        EffectTrackerCelestialManipulator effect;
        Vec3D focus = Vec3D.getCenter(worldPosition).add(0, 5.5 * depProg, 0);
        sound.updateSound(focus, (float) depProg, 0.5F + (float) depProg);

//		if (timer % 4 == 0) {
//			for (int i = 0; i < (timer % 20 <= 10 ? 10 : 2); i++) {
//				//TODO particles
//				//                BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new EffectTrackerCelestialManipulator.SubParticle2(world, focus, effects.get(i % 2)), 128, true);
//			}
//		}

        IntParticleType.IntParticleData data = new IntParticleType.IntParticleData(DEParticles.SPARK.get(),
                255, //R
                127,  //G
                0,  //B
                (int) (1.2F * (level.random.nextFloat() + 0.1) * 100), //Scale
                (int) (0.5F * 100), //Spark scale
                30, 10, //Max Age, Additional random age
                (int) (0) //Gravity
        );
        Vector3 pos = Vector3.fromTileCenter(this);
        level.addParticle(data, pos.x, pos.y, pos.z, 0.002D, 0.04, 0.002D);

        Direction dir = Direction.values()[2 + level.random.nextInt(4)];
        data = new IntParticleType.IntParticleData(DEParticles.SPARK.get(),
                255, //R
                127,  //G
                255,  //B
                (int) (1F * (level.random.nextFloat() + 0.1) * 100), //Scale
                (int) (0.5F * 100), //Spark scale
                10, 0, //Max Age, Additional random age
                (int) (0), //Gravity
                (int) (1F * 1000), //Friction
                1 //Set velocity direct
        );
        double speed = 0.3 / 10;
        pos = Vector3.fromTileCenter(this).add(dir.getStepX() * -0.3, 0, dir.getStepZ() * -0.3);
        level.addParticle(data, pos.x, pos.y, pos.z, speed * dir.getStepX(), 0, speed * dir.getStepZ());


        double rotation = (level.getDayTime() % 24000) / 24000D;
        double offset;
        double offsetX;
        double offsetY;

        for (int i = 0; i < 2; i++) {
            effect = effects.get(i);
            effect.scale = (float) depProg;
            effect.onUpdate();
            offset = rotation * (Math.PI * 2);
            offsetX = Math.cos(offset) * 2.5 * depProg;
            offsetY = Math.sin(offset) * 2.5 * depProg;
            effect.pos = focus.copy().add(offsetX, offsetY, 0);
            rotation += 0.5F;
            effect.effectFocus = focus;
        }

        rotation = timer / 50D;

        double inc = 1D / (effects.size() - 2);
        for (int i = 2; i < effects.size(); i++) {
            effect = effects.get(i);
            offset = rotation * (Math.PI * 2);
            double x = Math.cos(offset) * 1 * depProg;
            double z = Math.sin(offset) * 1 * depProg;
            effect.scale = (float) depProg;
            effect.onUpdate();

            effect.pos = focus.copy().add(x, (i % 2 == 0 ? x : -x) * 0.8, z);
            if (i % 2 == 0) {
                effect.red = 0.9F;
                effect.green = 0.5F;
                effect.blue = 0.1F;
            } else {
                effect.red = 0.3F;
                effect.green = 0.2F;
                effect.blue = 1;
            }

            effect.effectFocus = focus;
            effect.renderBolts = false;
            rotation += inc;
        }
    }

    //endregion

    //region Rendering

    @OnlyIn(Dist.CLIENT)
    private void standbyParticleEffect() {
        IntParticleType.IntParticleData data = new IntParticleType.IntParticleData(DEParticles.SPARK.get(),
                76, //R
                0,  //G
                255,  //B
                (int) (0.4F * (level.random.nextFloat() + 0.1) * 100), //Scale
                (int) (0.15F * 100), //Spark scale
                30, 10, //Max Age, Additional random age
                (int) (0) //Gravity
        );
        Vector3 pos = Vector3.fromTileCenter(this);
        level.addParticle(data, pos.x, pos.y, pos.z, 0.002D, 0.01, 0.002D);


        //        SubParticle particle = new SubParticle(world, Vec3D.getCenter(pos));
        //        particle.setScale(0.2F);
        //TODO particles
        //        BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, particle);
        if (effects != null) {
            effects = null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void renderEffects(float partialTicks) {
        if (effects != null) {
            if (weatherToggleRunning.get()) {
                //                ResourceHelperDE.bindTexture(DETextures.FUSION_PARTICLE);
            } else {
                //                ResourceHelperDE.bindTexture(DETextures.CELESTIAL_PARTICLE);
            }

            Tesselator tessellator = Tesselator.getInstance();

            //            //Pre-Render
            //            RenderSystem.enableBlend();
            //            RenderSystem.disableLighting();
            //            RenderSystem.depthMask(true);
            //            RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);
            //
            //            for (EffectTrackerCelestialManipulator effect : effects) {
            //                effect.renderEffect(tessellator, partialTicks);
            //            }
            //
            //            //Post-Render
            //            RenderSystem.disableBlend();
            //            RenderSystem.enableLighting();
            //            RenderSystem.depthMask(true);
            //            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
        }
    }

    //endregion

    private String[] ACTIONS = new String[]{"STOP_RAIN", "START_RAIN", "START_STORM", "SUN_RISE", "MID_DAY", "SUN_SET", "MOON_RISE", "MIDNIGHT", "MOON_SET", "SKIP_24"};

    @Override
    public void onNeighborChange(BlockPos blockChanged) {
        if (level.hasNeighborSignal(worldPosition)) {
            if (!redstoneSignal.get()) {
                redstoneSignal.set(true);
                handleInteract(ACTIONS[rsMode.get()], null);
            }
        } else {
            if (redstoneSignal.get()) {
                redstoneSignal.set(false);
                if (timeWarpRunning.get() && rsMode.get() == 9) {
                    stopTimeWarp();
                }
            }
        }
    }

    private float getSoundVolume() {
        return DEOldConfig.disableLoudCelestialManipulator ? 1 : 10;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int currentWindowIndex, Inventory playerInventory, Player player) {
        return new CelestialManipulatorMenu(currentWindowIndex, player.getInventory(), this);
    }

    @Override
    public boolean onBlockActivated(BlockState state, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {

            player.openMenu(this, worldPosition);
        }
        return true;
    }
}
