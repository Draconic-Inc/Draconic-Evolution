package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileEnergyBase;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.network.PacketTileMessage;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableByte;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 28/09/2016.
 */
public class TileCelestialManipulator extends TileEnergyBase implements ITickable, IEnergyReceiver, IChangeListener {

    public final SyncableBool WEATHER_MODE = new SyncableBool(true, true, true, false);
    public final SyncableBool ACTIVE = new SyncableBool(false, true, false, false);
    public final SyncableBool weatherToggleRunning = new SyncableBool(false, true, false, false);
    public final SyncableBool timeWarpRunning = new SyncableBool(false, true, false, false);
    public final SyncableBool timeWarpStopping = new SyncableBool(false, true, false, false);
    public final SyncableBool redstoneSignal = new SyncableBool(false, true, false, false);
    public final SyncableByte rsMode = new SyncableByte((byte) 0, false, true, false);
    public int timer = 0;
    public boolean storm = false;
    public boolean rain = false;
    public long targetTime;

    public TileCelestialManipulator() {
        setCapacityAndTransfer(4000000, 4000000, 4000000);
        registerSyncableObject(WEATHER_MODE, true);
        registerSyncableObject(ACTIVE, true);
        registerSyncableObject(energyStored, false);
        registerSyncableObject(weatherToggleRunning, false);
        registerSyncableObject(timeWarpRunning, false);
        registerSyncableObject(timeWarpStopping, false);
        registerSyncableObject(redstoneSignal, true);
        registerSyncableObject(rsMode, true);
    }

    @Override
    public void update() {
        detectAndSendChanges();

        if (weatherToggleRunning.value) {
            timer++;
            if (worldObj.isRemote) {
                updateWeatherEffects();
            }
            else {
                if (timer >= 230) {
                    timer = 0;
                    weatherToggleRunning.value = false;
                    worldObj.getWorldInfo().setRaining(rain);
                    worldObj.getWorldInfo().setThundering(storm);
                }
            }
        }
        else if (timeWarpRunning.value) {
            timer++;
            if (worldObj.isRemote) {
                updateSunEffect();
            }
            else {
                energyStored.value = energyStorage.getEnergyStored();
                energyStored.detectAndSendChanges(this, null, false);
            }

            if (timer > 100) {
                if (getEnergyStored() > 320) {
                    int extracted = energyStorage.extractEnergy(16000, true);
                    int ticks = extracted / 320;
                    energyStorage.extractEnergy(ticks * 320, false);
                    worldObj.setWorldTime(worldObj.getWorldTime() + ticks);
                }

                if (!worldObj.isRemote) {
                    if (worldObj.getWorldTime() >= targetTime) {
                        stopTimeWarp();
                    }
                }
            }
        }
        else if (timeWarpStopping.value) {
            if (timer > 100) {
                timer = 100;
            }

            timer--;
            if (timer <= 0 && !worldObj.isRemote) {
                timeWarpStopping.value = false;
            }

            if (worldObj.isRemote && timer >= 0) {
                updateSunEffect();
            }
            else if (worldObj.isRemote) {
                if (effects != null) {
                    effects.clear();
                }
                if (sound != null) {
                    sound.kill();
                    sound = null;
                }
            }
        }
        else if (ACTIVE.value) {
            ACTIVE.value = false;
        }
        else if (worldObj.isRemote) {
            standbyParticleEffect();
        }
    }

    public void toggleWeather(boolean rain, boolean storm) {
        if (worldObj.isRemote || ACTIVE.value) {
            return;
        }

        this.storm = storm;
        this.rain = rain;
        timer = 0;
        weatherToggleRunning.value = true;
//        sendPacketToClients(new PacketTileMessage(this, (byte) 0, 0, false), new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
        ACTIVE.value = true;
    }

    public void startTimeWarp(long targetTime) {
        ACTIVE.value = true;
        this.targetTime = targetTime;
        timer = 0;
        timeWarpRunning.value = true;
//        sendPacketToClients(new PacketTileMessage(this, (byte) 1, 0, false), new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
    }

    public void stopTimeWarp() {
//        sendPacketToClients(new PacketTileMessage(this, (byte) 2, 0, false), new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
        timeWarpRunning.value = false;
        timeWarpStopping.value = true;
    }

    @Override
    public void receivePacketFromServer(PacketTileMessage packet) {
//        if (packet.getIndex() == 0) {
//            startWeatherEffect();
//        }
//        else if (packet.getIndex() == 1) {
//            startSunEffect();
//        }
//        else if (packet.getIndex() == 2) {
//            stopSunEffect();
//        }
    }

    //region Interact

    @Override
    public void receivePacketFromClient(PacketTileMessage packet, EntityPlayerMP client) {
        if (packet.getIndex() == 0 && packet.stringValue != null) {
            handleInteract(packet.stringValue, client);
        }
        else if (packet.getIndex() == 1 && packet.intValue >= 0 && packet.intValue <= 9) {
            rsMode.value = (byte) packet.intValue;
        }
    }

    public void handleInteract(String action, EntityPlayer player) {
        if (action.endsWith("STOP") && ACTIVE.value && timeWarpRunning.value) {
            stopTimeWarp();
            return;
        }

        if (ACTIVE.value) {
            sendMessage(new TextComponentTranslation("msg.de.alreadyRunning.txt"), player);
            return;
        }

        switch (action) {
            case "WEATHER_MODE":
                WEATHER_MODE.value = true;
                return;
            case "SUN_MODE":
                WEATHER_MODE.value = false;
                return;
            case "STOP_RAIN":
                if (!worldObj.isRaining()) {
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
                if (worldObj.isRaining()) {
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
                if (worldObj.isRaining() && worldObj.isThundering()) {
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
                startTimeWarp(worldObj.getWorldTime() + calculateTimeTill(0));
                LogHelper.info("Set time to sunrise! Cause: " + pos);
                break;
            case "MID_DAY":
                startTimeWarp(worldObj.getWorldTime() + calculateTimeTill(5900));
                LogHelper.info("Set time to midday! Cause: " + pos);
                break;
            case "SUN_SET":
                startTimeWarp(worldObj.getWorldTime() + calculateTimeTill(12000));
                LogHelper.info("Set time to sunset! Cause: " + pos);
                break;
            case "MOON_RISE":
                startTimeWarp(worldObj.getWorldTime() + calculateTimeTill(13000));
                LogHelper.info("Set time to moonrise! Cause: " + pos);
                break;
            case "MIDNIGHT":
                startTimeWarp(worldObj.getWorldTime() + calculateTimeTill(17900));
                LogHelper.info("Set time to midnight! Cause: " + pos);
                break;
            case "MOON_SET":
                startTimeWarp(worldObj.getWorldTime() + calculateTimeTill(22500));
                LogHelper.info("Set time to moonset! Cause: " + pos);
                break;
            case "SKIP_24":
                startTimeWarp(worldObj.getWorldTime() + 24000);
                LogHelper.info("Skipped one day! Cause: " + pos);
                break;
        }
    }

    private void sendMessage(ITextComponent message, EntityPlayer player) {
        if (player != null) {
            player.addChatComponentMessage(message);
        }
    }

    private int calculateTimeTill(int time) {
        int currentTime = (int) (worldObj.getWorldTime() % 24000);
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
            effects.add(new EffectTrackerCelestialManipulator(worldObj, Vec3D.getCenter(pos).add(0, 1.5, 0), Vec3D.getCenter(pos).add(0, 1.5, 0)));
        }

        Vec3D vec = Vec3D.getCenter(pos.add(0, 1, 0));
        sound = new CelestialModifierSound(DESoundHandler.electricBuzz);
        sound.updateSound(vec, 0.01F, 0.5F);
        FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
        worldObj.playSound(vec.x, vec.y, vec.z, DESoundHandler.fusionComplete, SoundCategory.BLOCKS, getSoundVolume(), 0.5F, false);
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
            worldObj.playSound(effectFocus.x, effectFocus.y, effectFocus.z, DESoundHandler.fusionComplete, SoundCategory.BLOCKS, 1F, 1F, false);
        }
        else if (timer == ascendStart) {
            worldObj.playSound(effectFocus.x, effectFocus.y, effectFocus.z, DESoundHandler.fusionComplete, SoundCategory.BLOCKS, 1F, 2F, false);
            for (int i = 0; i < 100; i++) {
                try {
                    SubParticle particle = new SubParticle(worldObj, effects.get(worldObj.rand.nextInt(effects.size())).pos);
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
            worldObj.playSound(effectFocus.x, effectFocus.y, effectFocus.z, DESoundHandler.boom, SoundCategory.BLOCKS, DEConfig.disableLoudCelestialManipulator ? 1 : 100, 1F, false);
            timer = 0;
            weatherToggleRunning.value = false;
            effects.clear();
            sound.kill();
            sound = null;
        }
    }

    //endregion

    //region Sun Effect

    @SideOnly(Side.CLIENT)
    public void startSunEffect() {
        timeWarpRunning.value = true;
        timeWarpStopping.value = false;
        timer = 0;

        effects = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            effects.add(new EffectTrackerCelestialManipulator(worldObj, Vec3D.getCenter(pos).add(0, 1.5, 0), Vec3D.getCenter(pos).add(0, 1.5, 0)));
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
                BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, new EffectTrackerCelestialManipulator.SubParticle2(worldObj, focus, effects.get(i % 2)), 128, true);
            }
        }


        double rotation = (worldObj.getWorldTime() % 24000) / 24000D;
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
        SubParticle particle = new SubParticle(worldObj, Vec3D.getCenter(pos));
        particle.setScale(0.2F);
        BCEffectHandler.spawnFXDirect(DEParticles.DE_SHEET, particle);
        if (effects != null) {
            effects = null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderEffects(float partialTicks) {
        if (effects != null) {
            if (weatherToggleRunning.value) {
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

    private String[] ACTIONS = new String[] {"STOP_RAIN", "START_RAIN", "START_STORM", "SUN_RISE", "MID_DAY", "SUN_SET", "MOON_RISE", "MIDNIGHT", "MOON_SET", "SKIP_24"};

    @Override
    public void onNeighborChange() {
        if (worldObj.isBlockPowered(pos)) {
            if (!redstoneSignal.value) {
                redstoneSignal.value = true;
                handleInteract(ACTIONS[rsMode.value], null);
            }
        }
        else {
            if (redstoneSignal.value) {
                redstoneSignal.value = false;
                if (timeWarpRunning.value && rsMode.value == 9) {
                    stopTimeWarp();
                }
            }
        }
    }

    private float getSoundVolume() {
        return DEConfig.disableLoudCelestialManipulator ? 1 : 10;
    }
}
