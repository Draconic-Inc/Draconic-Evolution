package com.brandon3055.draconicevolution.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import cofh.redstoneflux.api.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileEnergyBase;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.effect.EffectTrackerCelestialManipulator;
import com.brandon3055.draconicevolution.client.render.effect.EffectTrackerCelestialManipulator.SubParticle;
import com.brandon3055.draconicevolution.client.sound.CelestialModifierSound;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileCelestialManipulator extends TileEnergyBase implements ITickable, IEnergyReceiver, IChangeListener {

    public final ManagedBool weatherMode = register(new ManagedBool("weatherMode", true, SAVE_NBT_SYNC_TILE));
    public final ManagedBool active = register(new ManagedBool("active", SAVE_NBT_SYNC_TILE));
    public final ManagedBool weatherToggleRunning = register(new ManagedBool("weatherToggleRunning", SYNC_TILE));
    public final ManagedBool timeWarpRunning = register(new ManagedBool("timeWarpRunning", SYNC_TILE));
    public final ManagedBool timeWarpStopping = register(new ManagedBool("timeWarpStopping", SYNC_TILE));
    public final ManagedBool redstoneSignal = register(new ManagedBool("redstoneSignal", SAVE_NBT_SYNC_TILE));
    public final ManagedByte rsMode = register(new ManagedByte("rsMode", SAVE_NBT_SYNC_CONTAINER));
    public int timer = 0;
    public boolean storm = false;
    public boolean rain = false;
    public long targetTime;

    public TileCelestialManipulator() {
        setCapacityAndTransfer(4000000, 4000000, 4000000);
        setEnergySyncMode(SYNC_CONTAINER);
    }

    @Override
    public void update() {
        super.update();

        if (weatherToggleRunning.get()) {
            timer++;
            if (world.isRemote) {
                updateWeatherEffects();
            }
            else {
                if (timer >= 230) {
                    timer = 0;
                    weatherToggleRunning.set(false);
                    world.getWorldInfo().setRaining(rain);
                    world.getWorldInfo().setThundering(storm);
                    int time = (10 * 60 * 20) + world.rand.nextInt(20 * 60 * 20);
                    world.getWorldInfo().setRainTime(rain ? time : 0);
                    world.getWorldInfo().setCleanWeatherTime(rain ? 0 : time);
                }
            }
        }
        else if (timeWarpRunning.get()) {
            timer++;
            if (world.isRemote) {
                updateSunEffect();
            }
//            else {
//                energyStored.value = energyStorage.getEnergyStored();
//                energyStored.detectAndSendChanges(this, null, false);
//            }

            if (timer > 100) {
                if (getEnergyStored() > 320) {
                    int extracted = energyStorage.extractEnergy(16000, true);
                    int ticks = extracted / 320;
                    energyStorage.extractEnergy(ticks * 320, false);
                    world.setWorldTime(world.getWorldTime() + ticks);
                }

                if (!world.isRemote) {
                    if (world.getWorldTime() >= targetTime) {
                        stopTimeWarp();
                    }
                }
            }
        }
        else if (timeWarpStopping.get()) {
            if (timer > 100) {
                timer = 100;
            }

            timer--;
            if (timer <= 0 && !world.isRemote) {
                timeWarpStopping.set(false);
            }

            if (world.isRemote && timer >= 0) {
                updateSunEffect();
            }
            else if (world.isRemote) {
                if (effects != null) {
                    effects.clear();
                }
                if (sound != null) {
                    sound.kill();
                    sound = null;
                }
            }
        }
        else if (active.get()) {
            active.set(false);
        }
        else if (world.isRemote) {
            standbyParticleEffect();
        }
    }

    public void toggleWeather(boolean rain, boolean storm) {
        if (world.isRemote || active.get()) {
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
    public void receivePacketFromClient(MCDataInput data, EntityPlayerMP client, int id) {
        if (id == 0) {
            handleInteract(data.readString(), client);
        }
        else if (id == 1) {
            int intValue = data.readInt();
            if (intValue >= 0 && intValue <= 9) {
                rsMode.set((byte) intValue);
            }
        }
    }

    public void handleInteract(String action, EntityPlayer player) {
        if (action.endsWith("STOP") && active.get() && timeWarpRunning.get()) {
            stopTimeWarp();
            return;
        }

        if (active.get()) {
            sendMessage(new TextComponentTranslation("msg.de.alreadyRunning.txt"), player);
            return;
        }

        switch (action) {
            case "WEATHER_MODE":
                weatherMode.set(true);
                return;
            case "SUN_MODE":
                weatherMode.set(false);
                return;
            case "STOP_RAIN":
                if (!world.isRaining()) {
                    sendMessage(new TextComponentTranslation("msg.de.notRaining.txt"), player);
                    return;
                }
                if (getEnergyStored() < 256000) {
                    sendMessage(new TextComponentTranslation("msg.de.insufficientPower.txt").appendText(" (256000RF)"), player);
                    return;
                }
                energyStorage.modifyEnergyStored(-256000);
                toggleWeather(false, false);
                LogHelper.info("Stopped rain! Cause: " + pos);
                return;
            case "START_RAIN":
                if (world.isRaining()) {
                    sendMessage(new TextComponentTranslation("msg.de.alreadyRaining.txt"), player);
                    return;
                }
                if (getEnergyStored() < 256000) {
                    sendMessage(new TextComponentTranslation("msg.de.insufficientPower.txt").appendText(" (256000RF)"), player);
                    return;
                }
                energyStorage.modifyEnergyStored(-256000);
                toggleWeather(true, false);
                LogHelper.info("Started rain! Cause: " + pos);
                return;
            case "START_STORM":
                if (world.isRaining() && world.isThundering()) {
                    sendMessage(new TextComponentTranslation("msg.de.alreadyStorm.txt"), player);
                    return;
                }
                if (getEnergyStored() < 384000) {
                    sendMessage(new TextComponentTranslation("msg.de.insufficientPower.txt").appendText(" (384000RF)"), player);
                    return;
                }
                energyStorage.modifyEnergyStored(-384000);
                toggleWeather(true, true);
                LogHelper.info("Started storm! Cause: " + pos);
                return;
            case "SUN_RISE":
                startTimeWarp(world.getWorldTime() + calculateTimeTill(0));
                LogHelper.info("Set time to sunrise! Cause: " + pos);
                break;
            case "MID_DAY":
                startTimeWarp(world.getWorldTime() + calculateTimeTill(5900));
                LogHelper.info("Set time to midday! Cause: " + pos);
                break;
            case "SUN_SET":
                startTimeWarp(world.getWorldTime() + calculateTimeTill(12000));
                LogHelper.info("Set time to sunset! Cause: " + pos);
                break;
            case "MOON_RISE":
                startTimeWarp(world.getWorldTime() + calculateTimeTill(13000));
                LogHelper.info("Set time to moonrise! Cause: " + pos);
                break;
            case "MIDNIGHT":
                startTimeWarp(world.getWorldTime() + calculateTimeTill(17900));
                LogHelper.info("Set time to midnight! Cause: " + pos);
                break;
            case "MOON_SET":
                startTimeWarp(world.getWorldTime() + calculateTimeTill(22500));
                LogHelper.info("Set time to moonset! Cause: " + pos);
                break;
            case "SKIP_24":
                startTimeWarp(world.getWorldTime() + 24000);
                LogHelper.info("Skipped one day! Cause: " + pos);
                break;
        }
    }

    private void sendMessage(ITextComponent message, EntityPlayer player) {
        if (player != null) {
            player.sendMessage(message);
        }
    }

    private int calculateTimeTill(int time) {
        int currentTime = (int) (world.getWorldTime() % 24000);
        return currentTime > time ? (24000 - (currentTime - time)) : time - currentTime;
    }

    //endregion

    @SideOnly(Side.CLIENT)
    private CelestialModifierSound sound;

    //region Weather Effect

    @SideOnly(Side.CLIENT)
    private List<EffectTrackerCelestialManipulator> effects;

    @SideOnly(Side.CLIENT)
    public void startWeatherEffect() {
        timer = 0;

        effects = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            effects.add(new EffectTrackerCelestialManipulator(world, Vec3D.getCenter(pos).add(0, 1.5, 0), Vec3D.getCenter(pos).add(0, 1.5, 0)));
        }

        Vec3D vec = Vec3D.getCenter(pos.add(0, 1, 0));
        sound = new CelestialModifierSound(DESoundHandler.electricBuzz);
        sound.updateSound(vec, 0.01F, 0.5F);
        FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
        world.playSound(vec.x, vec.y, vec.z, DESoundHandler.fusionComplete, SoundCategory.BLOCKS, getSoundVolume(), 0.5F, false);
    }

    @SideOnly(Side.CLIENT)
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

        Vec3D effectFocus = Vec3D.getCenter(pos).add(0, height, 0);

        if (timer == riseEnd) {
            world.playSound(effectFocus.x, effectFocus.y, effectFocus.z, DESoundHandler.fusionComplete, SoundCategory.BLOCKS, 1F, 1F, false);
        }
        else if (timer == ascendStart) {
            world.playSound(effectFocus.x, effectFocus.y, effectFocus.z, DESoundHandler.fusionComplete, SoundCategory.BLOCKS, 1F, 2F, false);
            for (int i = 0; i < 100; i++) {
                try {
                    SubParticle particle = new SubParticle(world, effects.get(world.rand.nextInt(effects.size())).pos);
                    particle.setScale(2);
                    BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, particle, 128, true);
                }
                catch (Exception e) {
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
            world.playSound(effectFocus.x, effectFocus.y, effectFocus.z, DESoundHandler.boom, SoundCategory.BLOCKS, DEConfig.disableLoudCelestialManipulator ? 1 : 100, 1F, false);
            timer = 0;
            weatherToggleRunning.set(false);
            effects.clear();
            sound.kill();
            sound = null;
        }
    }

    //endregion

    //region Sun Effect

    @SideOnly(Side.CLIENT)
    public void startSunEffect() {
        timeWarpRunning.set(true);
        timeWarpStopping.set(false);
        timer = 0;

        effects = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            effects.add(new EffectTrackerCelestialManipulator(world, Vec3D.getCenter(pos).add(0, 1.5, 0), Vec3D.getCenter(pos).add(0, 1.5, 0)));
        }

        effects.get(0).red = 1F;
        effects.get(0).green = 0.821F;
        effects.get(0).blue = 0.174F;
        effects.get(0).renderBolts = false;

        effects.get(1).red = 1F;
        effects.get(1).green = 1F;
        effects.get(1).blue = 1F;
        effects.get(1).renderBolts = false;

        sound = new CelestialModifierSound(DESoundHandler.sunDialEffect);
        sound.updateSound(Vec3D.getCenter(pos), getSoundVolume(), 0.5F);
        FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
    }

    @SideOnly(Side.CLIENT)
    public void stopSunEffect() {
//        timeWarpRunning.value = false;
//        timeWarpStopping.value = true;
//        if (timer > 100) {
//            timer = 100;
//        }
    }

    @SideOnly(Side.CLIENT)
    public void updateSunEffect() {
        if (effects == null || effects.size() < 2 || sound == null) {
            startSunEffect();
        }

        double depProg = Math.min(timer / 100F, 1D);

        EffectTrackerCelestialManipulator effect;
        Vec3D focus = Vec3D.getCenter(pos).add(0, 5.5 * depProg, 0);
        sound.updateSound(focus, (float) depProg, 0.5F + (float) depProg);

        if (timer % 4 == 0) {
            for (int i = 0; i < (timer % 20 <= 10 ? 10 : 2); i++) {
                BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new EffectTrackerCelestialManipulator.SubParticle2(world, focus, effects.get(i % 2)), 128, true);
            }
        }


        double rotation = (world.getWorldTime() % 24000) / 24000D;
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
            }
            else {
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

    @SideOnly(Side.CLIENT)
    private void standbyParticleEffect() {
        SubParticle particle = new SubParticle(world, Vec3D.getCenter(pos));
        particle.setScale(0.2F);
        BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, particle);
        if (effects != null) {
            effects = null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderEffects(float partialTicks) {
        if (effects != null) {
            if (weatherToggleRunning.get()) {
                ResourceHelperDE.bindTexture(DETextures.FUSION_PARTICLE);
            }
            else {
                ResourceHelperDE.bindTexture(DETextures.CELESTIAL_PARTICLE);
            }

            Tessellator tessellator = Tessellator.getInstance();

            //Pre-Render
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);

            for (EffectTrackerCelestialManipulator effect : effects) {
                effect.renderEffect(tessellator, partialTicks);
            }

            //Post-Render
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    //endregion

    private String[] ACTIONS = new String[]{"STOP_RAIN", "START_RAIN", "START_STORM", "SUN_RISE", "MID_DAY", "SUN_SET", "MOON_RISE", "MIDNIGHT", "MOON_SET", "SKIP_24"};

    @Override
    public void onNeighborChange(BlockPos blockChanged) {
        if (world.isBlockPowered(pos)) {
            if (!redstoneSignal.get()) {
                redstoneSignal.set(true);
                handleInteract(ACTIONS[rsMode.get()], null);
            }
        }
        else {
            if (redstoneSignal.get()) {
                redstoneSignal.set(false);
                if (timeWarpRunning.get() && rsMode.get() == 9) {
                    stopTimeWarp();
                }
            }
        }
    }

    private float getSoundVolume() {
        return DEConfig.disableLoudCelestialManipulator ? 1 : 10;
    }
}
