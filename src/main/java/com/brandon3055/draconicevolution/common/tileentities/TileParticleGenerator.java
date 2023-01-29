package com.brandon3055.draconicevolution.common.tileentities;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.brandon3055.draconicevolution.client.handler.ParticleHandler;
import com.brandon3055.draconicevolution.client.render.particle.ParticleCustom;
import com.brandon3055.draconicevolution.client.render.particle.Particles;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper.TileLocation;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.integration.computers.IDEPeripheral;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileParticleGenerator extends TileEntity implements IDEPeripheral {

    public boolean particles_enabled = true;

    public int red = 0;
    public int green = 0;
    public int blue = 0;
    public int random_red = 0;
    public int random_green = 0;
    public int random_blue = 0;
    public float motion_x = 0.0F;
    public float motion_y = 0.0F;
    public float motion_z = 0.0F;
    public float random_motion_x = 0.0F;
    public float random_motion_y = 0.0F;
    public float random_motion_z = 0.0F;
    public float scale = 1F;
    public float random_scale = 0F;
    public int life = 100;
    public int random_life = 0;
    public float spawn_x = 0;
    public float spawn_y = 0;
    public float spawn_z = 0;
    public float random_spawn_x = 0;
    public float random_spawn_y = 0;
    public float random_spawn_z = 0;
    public int page = 1;
    public int fade = 0;
    public int spawn_rate = 1;
    public boolean collide = false;
    public int selected_particle = 1;
    public int selected_max = 3;
    public float gravity = 0F;
    public boolean active = true;
    public boolean signal = false;
    public boolean inverted = false;
    TileLocation master = new TileLocation();
    public float rotation = 0;
    public boolean stabalizerMode = false;

    // beam
    public boolean beam_enabled = false;
    public boolean render_core = false;

    public int beam_red = 0;
    public int beam_green = 0;
    public int beam_blue = 0;
    public float beam_scale = 1F;
    public float beam_pitch = 0F;
    public float beam_yaw = 0F;
    public float beam_length = 0F;
    public float beam_rotation = 0F;

    private int tick = 0;

    @SideOnly(Side.SERVER)
    @Override
    public boolean canUpdate() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) return;
        rotation += 0.5F;
        if (stabalizerMode) {

            spawnStabilizerParticle();
        }
        if (stabalizerMode) return;

        if (signal && !inverted) active = true;
        else if (!signal && inverted) active = true;
        else active = false;

        if (tick >= spawn_rate && active && particles_enabled) {
            tick = 0;

            Random rand = worldObj.rand;

            float MX = motion_x + (random_motion_x * rand.nextFloat());
            float MY = motion_y + (random_motion_y * rand.nextFloat());
            float MZ = motion_z + (random_motion_z * rand.nextFloat());
            float SCALE = scale + (random_scale * rand.nextFloat());
            double spawnX = xCoord + spawn_x + (random_spawn_x * rand.nextFloat());
            double spawnY = yCoord + spawn_y + (random_spawn_y * rand.nextFloat());
            double spawnZ = zCoord + spawn_z + (random_spawn_z * rand.nextFloat());

            ParticleCustom particle = new ParticleCustom(
                    worldObj,
                    spawnX + 0.5,
                    spawnY + 0.5,
                    spawnZ + 0.5,
                    MX,
                    MY,
                    MZ,
                    SCALE,
                    collide,
                    this.selected_particle);
            particle.red = this.red + rand.nextInt(random_red + 1);
            particle.green = this.green + rand.nextInt(random_green + 1);
            particle.blue = this.blue + rand.nextInt(random_blue + 1);
            particle.maxAge = this.life + rand.nextInt(random_life + 1);
            particle.fadeTime = this.fade;
            particle.fadeLength = this.fade;
            particle.gravity = this.gravity;

            ParticleHandler.spawnCustomParticle(particle, 256);

        } else tick++;
    }

    @SideOnly(Side.CLIENT)
    private void spawnStabilizerParticle() {
        if (getMaster() == null || worldObj.getTotalWorldTime() % (20) != 1) return;

        double x = xCoord + 0.5;
        double y = yCoord + 0.5;
        double z = zCoord + 0.5;
        int direction = 0;

        if (getMaster().xCoord > xCoord) direction = 0;
        else if (getMaster().xCoord < xCoord) direction = 1;
        else if (getMaster().zCoord > zCoord) direction = 2;
        else if (getMaster().zCoord < zCoord) direction = 3;

        Particles.EnergyBeamParticle particle = new Particles.EnergyBeamParticle(
                worldObj,
                x,
                y,
                z,
                getMaster().xCoord + 0.5,
                getMaster().zCoord + 0.5,
                direction,
                false);
        Particles.EnergyBeamParticle particle2 = new Particles.EnergyBeamParticle(
                worldObj,
                x,
                y,
                z,
                getMaster().xCoord + 0.5,
                getMaster().zCoord + 0.5,
                direction,
                true);
        ParticleHandler.spawnCustomParticle(particle, 60);
        ParticleHandler.spawnCustomParticle(particle2, 60);
    }

    public void toggleInverted() {
        inverted = !inverted;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        this.writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        master.writeToNBT(compound, "Key");
        compound.setBoolean("StabalizerMode", stabalizerMode);
        getBlockNBT(compound);

        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        master.readFromNBT(compound, "Key");
        stabalizerMode = compound.getBoolean("StabalizerMode");
        setBlockNBT(compound);
        super.readFromNBT(compound);
    }

    public TileEnergyStorageCore getMaster() {
        if (master == null) return null;
        TileEnergyStorageCore tile = (worldObj.getTileEntity(master.getXCoord(), master.getYCoord(), master.getZCoord())
                != null
                && worldObj.getTileEntity(
                        master.getXCoord(),
                        master.getYCoord(),
                        master.getZCoord()) instanceof TileEnergyStorageCore)
                                ? (TileEnergyStorageCore) worldObj
                                        .getTileEntity(master.getXCoord(), master.getYCoord(), master.getZCoord())
                                : null;
        return tile;
    }

    public void setMaster(TileLocation master) {
        this.master = master;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public void getBlockNBT(NBTTagCompound compound) {
        compound.setInteger("Red", red);
        compound.setInteger("Green", green);
        compound.setInteger("Blue", blue);
        compound.setInteger("RandomRed", random_red);
        compound.setInteger("RandomGreen", random_green);
        compound.setInteger("RandomBlue", random_blue);
        compound.setFloat("MotionX", motion_x);
        compound.setFloat("MotionY", motion_y);
        compound.setFloat("MotionZ", motion_z);
        compound.setFloat("RandomMotionX", random_motion_x);
        compound.setFloat("RandomMotionY", random_motion_y);
        compound.setFloat("RandomMotionZ", random_motion_z);
        compound.setFloat("Scale", scale);
        compound.setFloat("RandomScale", random_scale);
        compound.setInteger("Life", life);
        compound.setInteger("RandomLife", random_life);
        compound.setFloat("SpawnX", spawn_x);
        compound.setFloat("SpawnY", spawn_y);
        compound.setFloat("SpawnZ", spawn_z);
        compound.setFloat("RandomSpawnX", random_spawn_x);
        compound.setFloat("RandomSpawnY", random_spawn_y);
        compound.setFloat("RandomSpawnZ", random_spawn_z);
        compound.setInteger("Page", page);
        compound.setInteger("SpawnRate", spawn_rate);
        compound.setBoolean("CanCollide", collide);
        compound.setInteger("Fade", fade);
        compound.setInteger("SelectedParticle", selected_particle);
        compound.setFloat("Gravity", gravity);
        compound.setBoolean("Active", active);
        compound.setBoolean("Signal", signal);
        compound.setBoolean("Inverted", inverted);
        compound.setBoolean("particles_enabled", particles_enabled);

        compound.setBoolean("beam_enabled", beam_enabled);
        compound.setBoolean("render_core", render_core);
        compound.setInteger("beam_red", beam_red);
        compound.setInteger("beam_green", beam_green);
        compound.setInteger("beam_blue", beam_blue);
        compound.setFloat("beam_scale", beam_scale);
        compound.setFloat("beam_pitch", beam_pitch);
        compound.setFloat("beam_yaw", beam_yaw);
        compound.setFloat("beam_length", beam_length);
        compound.setFloat("beam_rotation", beam_rotation);
    }

    public void setBlockNBT(NBTTagCompound compound) {
        red = compound.getInteger("Red");
        green = compound.getInteger("Green");
        blue = compound.getInteger("Blue");
        random_red = compound.getInteger("RandomRed");
        random_green = compound.getInteger("RandomGreen");
        random_blue = compound.getInteger("RandomBlue");
        motion_x = compound.getFloat("MotionX");
        motion_y = compound.getFloat("MotionY");
        motion_z = compound.getFloat("MotionZ");
        random_motion_x = compound.getFloat("RandomMotionX");
        random_motion_y = compound.getFloat("RandomMotionY");
        random_motion_z = compound.getFloat("RandomMotionZ");
        scale = compound.getFloat("Scale");
        random_scale = compound.getFloat("RandomScale");
        life = compound.getInteger("Life");
        random_life = compound.getInteger("RandomLife");
        spawn_x = compound.getFloat("SpawnX");
        spawn_y = compound.getFloat("SpawnY");
        spawn_z = compound.getFloat("SpawnZ");
        random_spawn_x = compound.getFloat("RandomSpawnX");
        random_spawn_y = compound.getFloat("RandomSpawnY");
        random_spawn_z = compound.getFloat("RandomSpawnZ");
        page = compound.getInteger("Page");
        spawn_rate = compound.getInteger("SpawnRate");
        collide = compound.getBoolean("CanCollide");
        fade = compound.getInteger("Fade");
        selected_particle = compound.getInteger("SelectedParticle");
        gravity = compound.getFloat("Gravity");
        active = compound.getBoolean("Active");
        signal = compound.getBoolean("Signal");
        inverted = compound.getBoolean("Inverted");
        particles_enabled = compound.getBoolean("particles_enabled");

        beam_enabled = compound.getBoolean("beam_enabled");
        render_core = compound.getBoolean("render_core");
        beam_red = compound.getInteger("beam_red");
        beam_green = compound.getInteger("beam_green");
        beam_blue = compound.getInteger("beam_blue");
        beam_scale = compound.getFloat("beam_scale");
        beam_pitch = compound.getFloat("beam_pitch");
        beam_yaw = compound.getFloat("beam_yaw");
        beam_length = compound.getFloat("beam_length");
        beam_rotation = compound.getFloat("beam_rotation");
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 655360.0D;
    }

    public static double limit(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static int limit(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    @Override
    public String getName() {
        return "particle_generator";
    }

    @Override
    public String[] getMethodNames() {
        return new String[] { "setGeneratorProperty", "getGeneratorState", "resetGeneratorState" };
    }

    @Override
    public Object[] callMethod(String method, Object... args) {
        if (method.startsWith("setGeneratorProperty")) {
            if (args.length != 2) return new Object[] { false };
            else if (!(args[0] instanceof String)) return new Object[] { false };

            /* Particles */
            if (args[0].equals("particles_enabled") && args[1] instanceof Boolean) {
                particles_enabled = (Boolean) args[1];
            } else if (args[0].equals("red") && args[1] instanceof Double) {
                red = limit(((Double) args[1]).intValue(), 0, 255);
            } else if (args[0].equals("green") && args[1] instanceof Double) {
                green = limit(((Double) args[1]).intValue(), 0, 255);
            } else if (args[0].equals("blue") && args[1] instanceof Double) {
                blue = limit(((Double) args[1]).intValue(), 0, 255);
            } else if (args[0].equals("random_red") && args[1] instanceof Double) {
                random_red = limit(((Double) args[1]).intValue(), 0, 255);
            } else if (args[0].equals("random_green") && args[1] instanceof Double) {
                random_green = limit(((Double) args[1]).intValue(), 0, 255);
            } else if (args[0].equals("random_blue") && args[1] instanceof Double) {
                random_blue = limit(((Double) args[1]).intValue(), 0, 255);
            } else if (args[0].equals("motion_x") && args[1] instanceof Double) {
                motion_x = (float) limit((Double) args[1], -5F, 5F);
            } else if (args[0].equals("motion_y") && args[1] instanceof Double) {
                motion_y = (float) limit((Double) args[1], -5F, 5F);
            } else if (args[0].equals("motion_z") && args[1] instanceof Double) {
                motion_z = (float) limit((Double) args[1], -5F, 5F);
            } else if (args[0].equals("random_motion_x") && args[1] instanceof Double) {
                random_motion_x = (float) limit((Double) args[1], -5F, 5F);
            } else if (args[0].equals("random_motion_y") && args[1] instanceof Double) {
                random_motion_y = (float) limit((Double) args[1], -5F, 5F);
            } else if (args[0].equals("random_motion_z") && args[1] instanceof Double) {
                random_motion_z = (float) limit((Double) args[1], -5F, 5F);
            } else if (args[0].equals("scale") && args[1] instanceof Double) {
                scale = (float) limit((Double) args[1], 0.01F, 50F);
            } else if (args[0].equals("random_scale") && args[1] instanceof Double) {
                random_scale = (float) limit((Double) args[1], 0.01F, 50F);
            } else if (args[0].equals("life") && args[1] instanceof Double) {
                life = limit(((Double) args[1]).intValue(), 0, 1000);
            } else if (args[0].equals("random_life") && args[1] instanceof Double) {
                random_life = limit(((Double) args[1]).intValue(), 0, 1000);
            } else if (args[0].equals("spawn_x") && args[1] instanceof Double) {
                spawn_x = (float) limit((Double) args[1], -50F, 50F);
            } else if (args[0].equals("spawn_y") && args[1] instanceof Double) {
                spawn_y = (float) limit((Double) args[1], -50F, 50F);
            } else if (args[0].equals("spawn_z") && args[1] instanceof Double) {
                spawn_z = (float) limit((Double) args[1], -50F, 50F);
            } else if (args[0].equals("random_spawn_x") && args[1] instanceof Double) {
                random_spawn_x = (float) limit((Double) args[1], -50F, 50F);
            } else if (args[0].equals("random_spawn_y") && args[1] instanceof Double) {
                random_spawn_y = (float) limit((Double) args[1], -50F, 50F);
            } else if (args[0].equals("random_spawn_z") && args[1] instanceof Double) {
                random_spawn_z = (float) limit((Double) args[1], -50F, 50F);
            } else if (args[0].equals("fade") && args[1] instanceof Double) {
                fade = limit(((Double) args[1]).intValue(), 0, 100);
            } else if (args[0].equals("spawn_rate") && args[1] instanceof Double) {
                spawn_rate = limit(((Double) args[1]).intValue(), 1, 200);
            } else if (args[0].equals("collide") && args[1] instanceof Double) {
                collide = (Boolean) args[1];
            } else if (args[0].equals("selected_particle") && args[1] instanceof Double) {
                selected_particle = limit(((Double) args[1]).intValue(), 1, selected_max);
            } else if (args[0].equals("gravity") && args[1] instanceof Double) {
                gravity = (float) limit((Double) args[1], -5F, 5F);
            }
            /* Beam */
            else if (args[0].equals("beam_enabled") && args[1] instanceof Boolean) {
                beam_enabled = (Boolean) args[1];
            } else if (args[0].equals("render_core") && args[1] instanceof Boolean) {
                render_core = (Boolean) args[1];
            } else if (args[0].equals("beam_red") && args[1] instanceof Double) {
                beam_red = limit(((Double) args[1]).intValue(), 0, 255);
            } else if (args[0].equals("beam_green") && args[1] instanceof Double) {
                beam_green = limit(((Double) args[1]).intValue(), 0, 255);
            } else if (args[0].equals("beam_blue") && args[1] instanceof Double) {
                beam_blue = limit(((Double) args[1]).intValue(), 0, 255);
            } else if (args[0].equals("beam_scale") && args[1] instanceof Double) {
                beam_scale = (float) limit((Double) args[1], -0F, 5F);
            } else if (args[0].equals("beam_pitch") && args[1] instanceof Double) {
                beam_pitch = (float) limit((Double) args[1], -180F, 180F);
            } else if (args[0].equals("beam_yaw") && args[1] instanceof Double) {
                beam_yaw = (float) limit((Double) args[1], -180F, 180F);
            } else if (args[0].equals("beam_length") && args[1] instanceof Double) {
                beam_length = (float) limit((Double) args[1], -0F, 320F);
            } else if (args[0].equals("beam_rotation") && args[1] instanceof Double) {
                beam_rotation = (float) limit((Double) args[1], -1F, 1F);
            } else {
                return new Object[] { false };
            }

            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            return new Object[] { true };
        } else if (method.startsWith("getGeneratorState")) {
            Map<Object, Object> map = new HashMap<Object, Object>();

            /* Particles */
            map.put("particles_enabled", particles_enabled);
            map.put("red", red);
            map.put("green", green);
            map.put("blue", blue);
            map.put("random_red", random_red);
            map.put("random_green", random_green);
            map.put("random_blue", random_blue);
            map.put("motion_x", motion_x);
            map.put("motion_y", motion_y);
            map.put("motion_z", motion_z);
            map.put("random_motion_x", random_motion_x);
            map.put("random_motion_y", random_motion_y);
            map.put("random_motion_z", random_motion_z);
            map.put("scale", scale);
            map.put("random_scale", random_scale);
            map.put("life", life);
            map.put("random_life", random_life);
            map.put("spawn_x", spawn_x);
            map.put("spawn_y", spawn_y);
            map.put("spawn_z", spawn_z);
            map.put("random_spawn_x", random_spawn_x);
            map.put("random_spawn_y", random_spawn_y);
            map.put("random_spawn_z", random_spawn_z);
            map.put("fade", fade);
            map.put("spawn_rate", spawn_rate);
            map.put("collide", collide);
            map.put("selected_particle", selected_particle);
            map.put("gravity", gravity);

            /* Beam */
            map.put("beam_enabled", beam_enabled);
            map.put("render_core", render_core);
            map.put("beam_red", beam_red);
            map.put("beam_green", beam_green);
            map.put("beam_blue", beam_blue);
            map.put("beam_scale", beam_scale);
            map.put("beam_pitch", beam_pitch);
            map.put("beam_yaw", beam_yaw);
            map.put("beam_length", beam_length);
            map.put("beam_rotation", beam_rotation);

            return new Object[] { map };
        } else if (method.startsWith("resetGeneratorState")) {
            particles_enabled = true;
            red = 0;
            green = 0;
            blue = 0;
            random_red = 0;
            random_green = 0;
            random_blue = 0;
            motion_x = 0.0F;
            motion_y = 0.0F;
            motion_z = 0.0F;
            random_motion_x = 0.0F;
            random_motion_y = 0.0F;
            random_motion_z = 0.0F;
            scale = 1F;
            random_scale = 0F;
            life = 100;
            random_life = 0;
            spawn_x = 0;
            spawn_y = 0;
            spawn_z = 0;
            random_spawn_x = 0;
            random_spawn_y = 0;
            random_spawn_z = 0;
            page = 1;
            fade = 0;
            spawn_rate = 1;
            collide = false;
            selected_particle = 1;
            gravity = 0F;

            // beam
            beam_enabled = false;
            render_core = false;

            beam_red = 0;
            beam_green = 0;
            beam_blue = 0;
            beam_scale = 1F;
            beam_pitch = 0F;
            beam_yaw = 0F;
            beam_length = 0F;
            beam_rotation = 0F;

            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            return new Object[] { true };
        }

        return new Object[] { 0 };
    }
}
