package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;

import com.brandon3055.draconicevolution.client.handler.ParticleHandler;
import com.brandon3055.draconicevolution.client.render.particle.Particles.AdvancedSeekerParticle;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.multiblock.MultiblockHelper;
import com.brandon3055.draconicevolution.common.entity.EntityCustomDragon;
import com.brandon3055.draconicevolution.common.entity.ExtendedPlayer;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 8/08/2014.
 */
public class TileEnderResurrection extends TileEntity {

    private MultiblockHelper.TileLocation diamondPillars[] = new MultiblockHelper.TileLocation[4];
    private MultiblockHelper.TileLocation draconiumPillars[] = new MultiblockHelper.TileLocation[4];
    private ExtendedPlayer playerProps;
    private EntityPlayer owner;
    private boolean spawnInProgress = false;
    private int timer = 0;
    private double level = 0;

    public TileEnderResurrection() {
        for (int i = 0; i < diamondPillars.length; i++) {
            diamondPillars[i] = new MultiblockHelper.TileLocation();
        }
        for (int i = 0; i < draconiumPillars.length; i++) {
            draconiumPillars[i] = new MultiblockHelper.TileLocation();
        }
    }

    @Override
    public void updateEntity() {
        if (spawnInProgress) {
            // if (timer < 1800) timer = 2200;
            if (!arePillarsValid() || !isBaseValid()) spawnInProgress = false;
            effectDrive();
            findAndActivateChrystals(timer - 200, true);
            if (timer > 2390 && arePillarsValid() && isBaseValid()) {
                spawn();
                spawnInProgress = false;
            }
            if (timer < 556 || timer > 2300) timer++;
            else timer += 2;
            if (ConfigHandler.sumonRitualAccelerated) timer += 20;
        }
    }

    private void spawn() {
        if (owner == null) return;
        for (int i = 0; i < diamondPillars.length; i++) {
            if (diamondPillars[i] == null) return;
            worldObj.setBlockToAir(
                    diamondPillars[i].getXCoord(),
                    diamondPillars[i].getYCoord(),
                    diamondPillars[i].getZCoord());
            if (!worldObj.isRemote) worldObj.createExplosion(
                    owner,
                    diamondPillars[i].getXCoord() + 0.5,
                    diamondPillars[i].getYCoord() + 0.5,
                    diamondPillars[i].getZCoord() + 0.5,
                    3,
                    true);
        }
        for (int i = 0; i < draconiumPillars.length; i++) {
            if (draconiumPillars[i] == null) return;
            worldObj.setBlockToAir(
                    draconiumPillars[i].getXCoord(),
                    draconiumPillars[i].getYCoord(),
                    draconiumPillars[i].getZCoord());
            if (!worldObj.isRemote) worldObj.createExplosion(
                    owner,
                    draconiumPillars[i].getXCoord() + 0.5,
                    draconiumPillars[i].getYCoord() + 0.5,
                    draconiumPillars[i].getZCoord() + 0.5,
                    3,
                    true);
        }

        worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        if (!worldObj.isRemote) worldObj.createExplosion(owner, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 3, true);
        if (!worldObj.isRemote) worldObj.createExplosion(null, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, 10, false);

        if (level > 10) level = 10;
        EntityCustomDragon dragon = new EntityCustomDragon(worldObj, 200D + level * 50, 10F + (float) level * 5F);
        dragon.setPosition(xCoord, yCoord + 60, zCoord);
        dragon.onSpawnWithEgg(null);
        if (!worldObj.isRemote) worldObj.spawnEntityInWorld(dragon);
        playerProps.setSpawnCount(playerProps.getSpawnCount() + 1);
    }

    private void lEffects() {
        int x = -100 + worldObj.rand.nextInt(200);
        int z = -100 + worldObj.rand.nextInt(200);
        EntityLightningBolt bolt = new EntityLightningBolt(worldObj, x, worldObj.getTopSolidOrLiquidBlock(z, z) - 1, z);
        if (!worldObj.isRemote) worldObj.addWeatherEffect(bolt);
        EntityPlayer p = worldObj.getClosestPlayer(xCoord, yCoord, zCoord, 100);
        float yaw = p.rotationYaw;
        float pitch = p.rotationPitch;
        float mod = 0.1F;
        yaw += worldObj.rand.nextBoolean() ? mod : -mod;
        pitch += worldObj.rand.nextBoolean() ? mod : -mod;
        // yaw += (-0.5F+worldObj.rand.nextFloat())*mod;
        // pitch += (-0.5F+worldObj.rand.nextFloat())*mod;
        if (worldObj.isRemote) p.setLocationAndAngles(p.posX, p.posY - 1.6, p.posZ, yaw, pitch);
    }

    private void effectDrive() {
        if (timer == 1 || worldObj.rand.nextInt(50) == 0) randomBolt();
        if (timer > 100 && worldObj.rand.nextInt(Math.max(1, 50 - (timer / 40))) == 0) randomBolt();

        if (timer == 10 || timer == 20 || timer == 30 || timer == 40) draconiumStrikes(timer / 10);
        if (worldObj.rand.nextInt(50) == 0) draconiumStrikes(worldObj.rand.nextInt(4) + 1);

        if (timer > 60 && worldObj.isRemote) coreParticles();

        findAndActivateChrystals(timer, false);
        findAndActivateChrystals(timer - 50, false);
        findAndActivateChrystals(timer - 100, false);

        // if (timer == 1) worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D,
        // "draconicevolution:boom", 10F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        // if (timer == 1 || worldObj.rand.nextInt(50) == 0) worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D,
        // zCoord + 0.5D, "ambient.weather.thunder", 10F, worldObj.rand.nextFloat() * 0.1F + 0.9F);

        if (worldObj.rand.nextInt(50) == 0 && timer < 300) centreStrike();
    }

    private void draconiumStrikes(int pillar) {
        if (pillar != 1 && pillar != 2 && pillar != 3 && pillar != 4) return;
        pillar--;
        if (draconiumPillars[pillar] == null) return;
        EntityLightningBolt bolt = new EntityLightningBolt(
                worldObj,
                draconiumPillars[pillar].getXCoord(),
                draconiumPillars[pillar].getYCoord(),
                draconiumPillars[pillar].getZCoord());
        if (!worldObj.isRemote) worldObj.addWeatherEffect(bolt);
    }

    private void findAndActivateChrystals(int y, boolean spawnCrystal) {
        if (worldObj.isRemote) return;
        if (y < 0 || y > 250) return;
        for (int x = -200; x < 200; x++) {
            for (int z = -200; z < 200; z++) {
                if (worldObj.getBlock(x, y, z) == Blocks.bedrock) {
                    boolean flag = true;
                    for (int x1 = x - 1; x1 <= x + 1; x1++) {
                        for (int y1 = y - 1; y1 <= y + 1; y1++) {
                            for (int z1 = z - 1; z1 <= z + 1; z1++) {
                                if ((x1 != x || y1 != y || z1 != z) && worldObj.getBlock(x1, y1, z1) == Blocks.bedrock)
                                    flag = false;
                            }
                        }
                    }

                    if (flag && !worldObj.isRemote) {
                        if (spawnCrystal) {
                            // EntityEnderCrystal crystal = new EntityEnderCrystal(worldObj);
                            // crystal.setPosition(x + 0.5, y + 1, z + 0.5);
                            // worldObj.spawnEntityInWorld(crystal);

                            NBTTagCompound nbttagcompound = new NBTTagCompound();
                            nbttagcompound.setString("id", "EnderCrystal");
                            Entity crystal = EntityList.createEntityFromNBT(nbttagcompound, worldObj);
                            crystal.setPosition(x + 0.5, y + 1, z + 0.5);
                            worldObj.spawnEntityInWorld(crystal);
                        } else worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, x, y + 1, z));
                    }
                }
            }
        }
    }

    private void centreStrike() {
        EntityLightningBolt bolt = new EntityLightningBolt(worldObj, xCoord, yCoord, zCoord);
        if (!worldObj.isRemote) worldObj.addWeatherEffect(bolt);
    }

    private void randomBolt() {
        int x = -100 + worldObj.rand.nextInt(200);
        int z = -100 + worldObj.rand.nextInt(200);
        EntityLightningBolt bolt = new EntityLightningBolt(worldObj, x, worldObj.getTopSolidOrLiquidBlock(z, z) - 1, z);
        if (!worldObj.isRemote) worldObj.addWeatherEffect(bolt);
    }

    public boolean onActivated(EntityPlayer player) {
        boolean flag = true;
        if (worldObj.provider.dimensionId != 1) {
            if (worldObj.isRemote)
                player.addChatComponentMessage(new ChatComponentTranslation("msg.SpawnDragonMustBeInTheEnd.txt"));
            return true;
        }
        if (xCoord > 100 || zCoord > 100 || xCoord < -100 || zCoord < -100) {
            if (worldObj.isRemote)
                player.addChatComponentMessage(new ChatComponentTranslation("msg.SpawnDragonToFarFrom00.txt"));
            return false;
        }
        playerProps = ExtendedPlayer.get(player);
        owner = player;

        if (spawnInProgress) {
            for (int i = 0; i < draconiumPillars.length; i++) {
                if (draconiumPillars[i] == null || !arePillarsValid()) return false;
                worldObj.setBlock(
                        draconiumPillars[i].getXCoord(),
                        draconiumPillars[i].getYCoord(),
                        draconiumPillars[i].getZCoord(),
                        ModBlocks.draconiumBlock);
            }
            flag = false;
        }
        if (!isBaseValid()) flag = false;
        if (!findPillars()) flag = false;
        if (!arePillarsValid()) flag = false;

        if (!spawnInProgress && flag) timer = 0;

        spawnInProgress = flag;
        level = (double) playerProps.getSpawnCount() / 2D;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        if (flag && !worldObj.isRemote) LogHelper.info("Starting Ritual of Ender Resurrection (Level " + level + ")");
        return flag;
    }

    private boolean isBaseValid() {
        if (worldObj.getBlock(xCoord + 1, yCoord, zCoord) != Blocks.obsidian
                || worldObj.getBlock(xCoord - 1, yCoord, zCoord) != Blocks.obsidian
                || worldObj.getBlock(xCoord, yCoord, zCoord + 1) != Blocks.obsidian
                || worldObj.getBlock(xCoord, yCoord, zCoord - 1) != Blocks.obsidian)
            return false;
        if (worldObj.getBlock(xCoord + 1, yCoord, zCoord + 1) != Blocks.glowstone
                || worldObj.getBlock(xCoord - 1, yCoord, zCoord - 1) != Blocks.glowstone
                || worldObj.getBlock(xCoord - 1, yCoord, zCoord + 1) != Blocks.glowstone
                || worldObj.getBlock(xCoord + 1, yCoord, zCoord - 1) != Blocks.glowstone)
            return false;
        return true;
    }

    private boolean arePillarsValid() {
        for (int i = 0; i < diamondPillars.length; i++) {
            if (diamondPillars[i] == null) return false;
            if (worldObj.getBlock(
                    diamondPillars[i].getXCoord(),
                    diamondPillars[i].getYCoord(),
                    diamondPillars[i].getZCoord()) != Blocks.diamond_block
                    || worldObj.getBlock(
                            diamondPillars[i].getXCoord(),
                            diamondPillars[i].getYCoord() - 1,
                            diamondPillars[i].getZCoord()) != Blocks.quartz_block)
                return false;
        }
        for (int i = 0; i < draconiumPillars.length; i++) {
            if (draconiumPillars[i] == null) return false;
            if ((worldObj.getBlock(
                    draconiumPillars[i].getXCoord(),
                    draconiumPillars[i].getYCoord(),
                    draconiumPillars[i].getZCoord()) != ModBlocks.draconiumBlock
                    && worldObj.getBlockMetadata(
                            draconiumPillars[i].getXCoord(),
                            draconiumPillars[i].getYCoord(),
                            draconiumPillars[i].getZCoord()) != 2)
                    || worldObj.getBlock(
                            draconiumPillars[i].getXCoord(),
                            draconiumPillars[i].getYCoord() - 1,
                            draconiumPillars[i].getZCoord()) != Blocks.quartz_block
                    || worldObj.getBlock(
                            draconiumPillars[i].getXCoord(),
                            draconiumPillars[i].getYCoord() - 2,
                            draconiumPillars[i].getZCoord()) != Blocks.quartz_block)
                return false;
        }
        return true;
    }

    private boolean findPillars() {
        int xzRange = 6;
        int draconiumCount = 0;
        int diamondCount = 0;
        for (int x = xCoord - xzRange; x < xCoord + xzRange; x++) {
            for (int y = yCoord + 2; y < yCoord + 5; y++) {
                for (int z = zCoord - xzRange; z < zCoord + xzRange; z++) {
                    if (worldObj.getBlock(x, y, z) == ModBlocks.draconiumBlock && isPillarValid(0, x, y, z)
                            && worldObj.getBlockMetadata(x, y, z) == 2
                            && draconiumCount < 4) {
                        draconiumPillars[draconiumCount] = new MultiblockHelper.TileLocation(x, y, z);
                        draconiumCount++;
                    }
                }
            }
        }

        for (int x = xCoord - xzRange; x < xCoord + xzRange; x++) {
            for (int y = yCoord + 1; y < yCoord + 4; y++) {
                for (int z = zCoord - xzRange; z < zCoord + xzRange; z++) {
                    if (worldObj.getBlock(x, y, z) == Blocks.diamond_block && isPillarValid(1, x, y, z)
                            && diamondCount < 4) {
                        diamondPillars[diamondCount] = new MultiblockHelper.TileLocation(x, y, z);
                        diamondCount++;
                    }
                }
            }
        }
        return draconiumCount == 4 && diamondCount == 4;
    }

    @SideOnly(Side.CLIENT)
    private void coreParticles() {
        Random rand = new Random();
        {
            float mM = 0.4F;
            for (int i = 0; i < draconiumPillars.length; i++) {
                if (draconiumPillars[i] == null) return;
                AdvancedSeekerParticle particle = new AdvancedSeekerParticle(
                        worldObj,
                        draconiumPillars[i].getXCoord() + 0.5,
                        draconiumPillars[i].getYCoord() + 0.5,
                        draconiumPillars[i].getZCoord() + 0.5,
                        xCoord + 0.5,
                        yCoord + 0.5,
                        zCoord + 0.5,
                        2,
                        0F,
                        1.0F,
                        1.0F,
                        100);
                AdvancedSeekerParticle particle2;
                if (timer < 300) particle2 = new AdvancedSeekerParticle(
                        worldObj,
                        draconiumPillars[i].getXCoord() + 0.5,
                        draconiumPillars[i].getYCoord() + 0.5,
                        draconiumPillars[i].getZCoord() + 0.5,
                        xCoord + 0.5,
                        yCoord + 0.5,
                        zCoord + 0.5,
                        1,
                        1F,
                        0.0F,
                        0F,
                        40);
                else {
                    particle2 = new AdvancedSeekerParticle(
                            worldObj,
                            draconiumPillars[i].getXCoord() + 0.5,
                            draconiumPillars[i].getYCoord() + 0.5,
                            draconiumPillars[i].getZCoord() + 0.5,
                            xCoord + rand.nextFloat(),
                            yCoord + 3 + rand.nextFloat(),
                            zCoord + rand.nextFloat(),
                            3,
                            1F,
                            0.0F,
                            0F,
                            70,
                            timer);
                }
                if (timer > 2000) {
                    AdvancedSeekerParticle toDiamond = new AdvancedSeekerParticle(
                            worldObj,
                            xCoord + 0.5,
                            yCoord + 0.5,
                            zCoord + 0.5,
                            diamondPillars[i].getXCoord() + rand.nextFloat(),
                            diamondPillars[i].getYCoord() + rand.nextFloat(),
                            diamondPillars[i].getZCoord() + rand.nextFloat(),
                            3,
                            0,
                            1,
                            0,
                            100);
                    ParticleHandler.spawnCustomParticle(toDiamond, 250);
                    if (timer > 2100) {
                        AdvancedSeekerParticle toSpawn = new AdvancedSeekerParticle(
                                worldObj,
                                diamondPillars[i].getXCoord() + rand.nextFloat(),
                                diamondPillars[i].getYCoord() + rand.nextFloat(),
                                diamondPillars[i].getZCoord() + rand.nextFloat(),
                                xCoord - 3.5 + (double) (rand.nextFloat() * 7F),
                                yCoord + 60 + rand.nextInt(5),
                                zCoord - 3.5 + (double) (rand.nextFloat() * 7F),
                                3,
                                0F,
                                1F,
                                0F,
                                100,
                                timer);
                        ParticleHandler.spawnCustomParticle(toSpawn, 250);
                    }
                }

                particle.motionX = (rand.nextFloat() - 0.5) * mM;
                particle.motionZ = (rand.nextFloat() - 0.5) * mM;
                particle2.motionX = (rand.nextFloat() - 0.5) * mM;
                particle2.motionZ = (rand.nextFloat() - 0.5) * mM;
                ParticleHandler.spawnCustomParticle(particle, 250);
                ParticleHandler.spawnCustomParticle(particle2, 250);
            }

            if (timer > 300) {
                int t;
                if (timer > 700) t = timer;
                else t = 700;
                AdvancedSeekerParticle particle3 = new AdvancedSeekerParticle(
                        worldObj,
                        xCoord + rand.nextFloat(),
                        yCoord + 0.5,
                        zCoord + rand.nextFloat(),
                        xCoord + rand.nextFloat(),
                        yCoord + 3 + rand.nextFloat(),
                        zCoord + rand.nextFloat(),
                        3,
                        0F,
                        1.0F,
                        1.0F,
                        70,
                        t);
                ParticleHandler.spawnCustomParticle(particle3, 250);
            }
            if (timer > 1000) {
                float red;
                float green;
                float blue;
                float f = rand.nextFloat() * 0.6F + 0.4F;
                red = green = blue = 1.0F * f;
                green *= 0.3F;
                red *= 0.9F;
                AdvancedSeekerParticle particle4 = new AdvancedSeekerParticle(
                        worldObj,
                        xCoord + 0.5,
                        yCoord + 3.5,
                        zCoord + 0.5,
                        xCoord + rand.nextFloat(),
                        yCoord + 3 + rand.nextFloat(),
                        zCoord + rand.nextFloat(),
                        1,
                        red,
                        green,
                        blue,
                        100);
                particle4.motionX = (rand.nextFloat() - 0.5) * mM;
                particle4.motionZ = (rand.nextFloat() - 0.5) * mM;
                ParticleHandler.spawnCustomParticle(particle4, 250);
            }
        }
    }

    /**
     * Draconium = 0, Diamond = 1
     */
    private boolean isPillarValid(int type, int x, int y, int z) {
        if (type == 0) {
            if (worldObj.getBlock(x, y - 1, z) == Blocks.quartz_block
                    && worldObj.getBlock(x, y - 2, z) == Blocks.quartz_block
                    && worldObj.getBlockMetadata(x, y - 1, z) == 2
                    && worldObj.getBlockMetadata(x, y - 2, z) == 2)
                return true;
        } else if (type == 1) {
            if (worldObj.getBlock(x, y - 1, z) == Blocks.quartz_block && worldObj.getBlockMetadata(x, y - 1, z) == 2)
                return true;
        }
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("SpawnInProgress", spawnInProgress);
        compound.setInteger("Timer", timer);
        for (int i = 0; i < diamondPillars.length; i++) {
            if (diamondPillars[i] != null) diamondPillars[i].writeToNBT(compound, String.valueOf(i));
        }
        for (int i = 0; i < draconiumPillars.length; i++) {
            if (draconiumPillars[i] != null) draconiumPillars[i].writeToNBT(compound, "D" + String.valueOf(i));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        spawnInProgress = compound.getBoolean("SpawnInProgress");
        timer = compound.getInteger("Timer");
        for (int i = 0; i < diamondPillars.length; i++) {
            if (diamondPillars[i] != null) diamondPillars[i].readFromNBT(compound, String.valueOf(i));
        }
        for (int i = 0; i < draconiumPillars.length; i++) {
            if (draconiumPillars[i] != null) draconiumPillars[i].readFromNBT(compound, "D" + String.valueOf(i));
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }
}
