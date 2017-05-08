package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.blocks.tileentity.TileChaosCrystal;
import com.brandon3055.draconicevolution.lib.DEDamageSources;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Brandon on 4/07/2014.
 */
public class EntityChaosGuardian extends EntityDragonOld {//summon DraconicEvolution.ChaosGuardian

    private static final List<Block> DESTRUCTION_BLACKLIST = ImmutableList.of(Blocks.END_STONE, Blocks.OBSIDIAN);
    private static final DataParameter<Optional<BlockPos>> CRYSTAL_POSITION = EntityDataManager.<Optional<BlockPos>>createKey(EntityChaosGuardian.class, DataSerializers.OPTIONAL_BLOCK_POS);

    private Entity target;

    /**
     * The spawn coordinates of this dragon
     */
    public int homeX = 0;
    public int homeY = -1;
    public int homeZ = 0;
    public boolean homeSet = false;

    //Re Implemented from 1.7
    public double targetX;
    public double targetY;
    public double targetZ;
    public boolean forceNewTarget;


    /**
     * How long until the next attack sequence
     */
    private int nextAttackTimer = 100;
    /**
     * The current attack. -1 = not attacking
     */
    private int attackInProgress = -1;
    /**
     * How long until the current attack sequence ends
     */
    private int attackTimer = 0;
    /**
     * The behaviour before the attack started (used to reset to previous behaviour after charge attacks)
     */
    private EnumBehaviour previousBehaviour = EnumBehaviour.ROAMING;
    /**
     * How long until a new ignition charge can be fired
     */
    private int ignitionChargeTimer = 0;

    /**
     * A list of all Chaos Crystals in range
     */
    public List<EntityGuardianCrystal> crystals = null;
    /**
     * Number of crystals that are still active
     */
    public int activeCrystals = 0;

    public EntityGuardianCrystal healingChaosCrystal;
    //	public int connectedCrystalID = -1;
//    public int crystalX = 0;
//    public int crystalY = -1;
//    public int crystalZ = 0;

    private static final int ATTACK_FIREBALL_CHARGE = 0;
    private static final int ATTACK_FIREBALL_CHASER = 1;
    private static final int ATTACK_ENERGY_CHASER = 2;
    private static final int ATTACK_CHAOS_CHASER = 3;
    private static final int ATTACK_TELEPORT = 4;

    public float circlePosition = 0;
    public float circleDirection = 1;

    public EnumBehaviour behaviour = EnumBehaviour.ROAMING;
    private final BossInfoServer bossInfo = (BossInfoServer) (new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setDarkenSky(true).setCreateFog(true).setPlayEndBossMusic(true);

    public EntityChaosGuardian(World par1World) {
        super(par1World);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
//		dataWatcher.addObject(20, connectedCrystalID);
        dataManager.register(CRYSTAL_POSITION, Optional.<BlockPos>absent());


//        dataWatcher.addObject(21, crystalX);
//        dataWatcher.addObject(22, crystalY);
//        dataWatcher.addObject(23, crystalZ);
    }

    @Override
    public boolean isNoDespawnRequired() {
        return true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2000);
    }

    @Override
    public void onLivingUpdate() {
        if (ticksExisted % 20 == 0 && !worldObj.isRemote) {
            LogHelper.dev("UUID: " + this.getUniqueID() + " PersistentUUID: " + this.getPersistentID() + " ID: " + getEntityId());
        }
//        setHealth(0);
        //  setHealth(1);
        //setDead();
        //LogHelper.info(getMaxHealth()+" "+getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());

        //if (!worldObj.isRemote)LogHelper.info(homeX+" "+homeY+" "+ homeZ);
        //LogHelper.info(getHealth());

        //setHealth(0);
        // if (!worldObj.isRemote)LogHelper.info(behaviour);
        //if (player != null) player.setLocationAndAngles(posX, posY, posZ, 0, 0);
        //setPosition(10000, 100, 0);

        //Set home position when spawned
        if (!homeSet) {
            homeX = (int) posX;
            homeY = (int) posY;
            homeZ = (int) posZ;

            targetX = homeX;
            targetZ = homeZ;
            homeSet = true;
        }
        if (crystals == null) updateCrystals();

        float f;
        float f1;
        float moveSpeedMultiplier = behaviour.dragonSpeed;

        if (this.worldObj.isRemote) {
//			connectedCrystalID = dataWatcher.getWatchableObjectInt(20);

//            crystalX = dataWatcher.getWatchableObjectInt(21);
//            crystalY = dataWatcher.getWatchableObjectInt(22);
//            crystalZ = dataWatcher.getWatchableObjectInt(23);
//			if (ticksExisted % 10 == 0 && connectedCrystalID != -1 && worldObj.getEntityByID(connectedCrystalID) instanceof EntityChaosCrystal) healingChaosCrystal = (EntityChaosCrystal)worldObj.getEntityByID(connectedCrystalID);
//			else if (connectedCrystalID == -1 && healingChaosCrystal != null) healingChaosCrystal = null;

            f = MathHelper.cos(this.animTime * (float) Math.PI * 2.0F);
            f1 = MathHelper.cos(this.prevAnimTime * (float) Math.PI * 2.0F);

            if (f1 <= -0.3F && f >= -0.3F) {
                if (deathTicks <= 0) {
                    this.worldObj.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.HOSTILE, 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
                }
            }
        }

        this.prevAnimTime = this.animTime;
        float f2;

        if (!worldObj.isRemote) {
//			dataWatcher.updateObject(20, connectedCrystalID);
//            dataWatcher.updateObject(21, crystalX);
//            dataWatcher.updateObject(22, crystalY);
//            dataWatcher.updateObject(23, crystalZ);
            updateTarget();

            if (Utils.getClosestPlayer(worldObj, posX, posY, posZ, 500, true, true) == null && getDistance(homeX, homeY, homeZ) < 100) {
                LogHelper.dev("Preparing to unload Guardian...");
                DragonChunkLoader.updateLoaded(this);
                behaviour = EnumBehaviour.ROAMING;
                motionX = motionY = motionZ = 0;

                double posX = this.posX - (this.posX % 16) + 8;
                double posZ = this.posZ - (this.posZ % 16) + 8;
                int chunkX = (int)posX / 16;
                int chunkZ = (int)posZ / 16;

                setPosition(posX, posY, posZ);
                LogHelper.dev(String.format("Position (%s, %s) x=%s, z=%s %s", chunkX, chunkZ, posX, posZ, this));

                if (chunkX != chunkCoordX || chunkZ != chunkCoordZ) {
                    worldObj.getChunkFromChunkCoords(chunkCoordX, chunkCoordZ).removeEntity(this);
                    worldObj.getChunkFromChunkCoords(chunkX, chunkZ).addEntity(this);
                    LogHelper.dev("Corrected entity chunk position!!!!!!!");
                }

                worldObj.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();

                DragonChunkLoader.stopLoading(this);
                LogHelper.dev("Guardian Unloaded.");
                return;
            } else {
                if (getHealth() > 0) {
                    DragonChunkLoader.updateLoaded(this);
                }
            }

            if (deathTicks > 0) {
                DragonChunkLoader.stopLoading(this);
            }

            customAIUpdate();
            if (behaviour == EnumBehaviour.FIREBOMB && Utils.getDistanceAtoB(posX, posY, posZ, homeX, homeY + 30, homeZ) <= 3) {
                moveSpeedMultiplier = 0;
            }
        }

        if (this.getHealth() <= 0.0F) {
            behaviour = EnumBehaviour.DEAD;
            f = (this.rand.nextFloat() - 0.5F) * 8.0F;
            f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX + (double) f, this.posY + 2.0D + (double) f1, this.posZ + (double) f2, 0.0D, 0.0D, 0.0D);
//            this.worldObj.spawnParticle("largeexplode", this.posX + (double) f, this.posY + 2.0D + (double) f1, this.posZ + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        this.updateDragonEnderCrystal();
        f = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
        f *= moveSpeedMultiplier == 0 ? 1 : moveSpeedMultiplier;
        f *= (float) Math.pow(2.0D, this.motionY);

        if (this.slowed) {
            this.animTime += f * 0.5F;
        } else {
            this.animTime += f;
        }

        this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);

        if (this.ringBufferIndex < 0) {
            for (int i = 0; i < this.ringBuffer.length; ++i) {
                this.ringBuffer[i][0] = (double) this.rotationYaw;
                this.ringBuffer[i][1] = this.posY;
            }
        }

        if (++this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
        }

        this.ringBuffer[this.ringBufferIndex][0] = (double) this.rotationYaw;
        this.ringBuffer[this.ringBufferIndex][1] = this.posY;
        double d0;
        double d1;
        double d2;
        double d10;
        float f12;

        if (this.worldObj.isRemote) {
            if (this.newPosRotationIncrements > 0) {
                d10 = this.posX + (this.interpTargetX - this.posX) / (double) this.newPosRotationIncrements;
                d0 = this.posY + (this.interpTargetY - this.posY) / (double) this.newPosRotationIncrements;
                d1 = this.posZ + (this.interpTargetZ - this.posZ) / (double) this.newPosRotationIncrements;
                d2 = MathHelper.wrapDegrees(this.interpTargetYaw - (double) this.rotationYaw);
                this.rotationYaw = (float) ((double) this.rotationYaw + d2 / (double) this.newPosRotationIncrements);
                this.rotationPitch = (float) ((double) this.rotationPitch + (this.interpTargetPitch - (double) this.rotationPitch) / (double) this.newPosRotationIncrements);
                --this.newPosRotationIncrements;
                this.setPosition(d10, d0, d1);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
        } else {

            if (target != null && (target.isDead || !target.isEntityAlive() || target.getDistance(posX, posY, posZ) > 300)) {
                target = null;
            }
            d10 = this.targetX - this.posX;
            d0 = this.targetY - this.posY;
            d1 = this.targetZ - this.posZ;
            d2 = d10 * d10 + d0 * d0 + d1 * d1;

            if (this.target != null) {
                if (behaviour == EnumBehaviour.CIRCLE_PLAYER) {
                    this.targetX = this.target.posX + (int) (Math.cos(circlePosition) * 60);
                    this.targetZ = this.target.posZ + (int) (Math.sin(circlePosition) * 60);
                    moveSpeedMultiplier = 1F + Math.min(((float) Utils.getDistanceAtoB(targetX, targetZ, posX, posZ) / 50) * 3F, 3F);
                } else {
                    this.targetX = this.target.posX;
                    this.targetZ = this.target.posZ;
                }

                double d3 = this.targetX - this.posX;
                double d5 = this.targetZ - this.posZ;
                double d7 = Math.sqrt(d3 * d3 + d5 * d5);
                double d8 = 0.4000000059604645D + d7 / 80.0D - 1.0D;

                if (d8 > 10.0D) {
                    d8 = 10.0D;
                }

                this.targetY = this.target.getEntityBoundingBox().minY + d8 + (behaviour == EnumBehaviour.CIRCLE_PLAYER ? 25 : 0);
            } else if (behaviour != EnumBehaviour.FIREBOMB) {
                this.targetX += this.rand.nextGaussian() * 2.0D;
                this.targetZ += this.rand.nextGaussian() * 2.0D;
            }

            if (this.forceNewTarget || d2 < 100.0D || d2 > 22500.0D || this.isCollidedHorizontally || this.isCollidedVertically) {
                this.setNewTarget();
            }


            d0 /= (double) MathHelper.sqrt_double(d10 * d10 + d1 * d1);
            //if (isUber) f12 = 1.0F;//Verticle Motion Speed
            //else
            f12 = 0.6F;

            if (d0 < (double) (-f12)) {
                d0 = (double) (-f12);
            }

            if (d0 > (double) f12) {
                d0 = (double) f12;
            }

            this.motionY += d0 * 0.10000000149011612D;
            this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
            double d4 = 180.0D - Math.atan2(d10, d1) * 180.0D / Math.PI;
            double d6 = MathHelper.wrapDegrees(d4 - (double) this.rotationYaw);

            if (d6 > 50.0D) {
                d6 = 50.0D;
            }

            if (d6 < -50.0D) {
                d6 = -50.0D;
            }

            Vec3d vec3 = new Vec3d(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ).normalize();
            Vec3d vec32 = new Vec3d((double) MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F), this.motionY, (double) (-MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F))).normalize();
            float f5 = (float) (vec32.dotProduct(vec3) + 0.5D) / 1.5F;

            if (f5 < 0.0F) {
                f5 = 0.0F;
            }

            this.randomYawVelocity *= 0.8F;
            float f6 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
            double d9 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0D + 1.0D;

            if (d9 > 40.0D) {
                d9 = 40.0D;
            }

            this.randomYawVelocity = (float) ((double) this.randomYawVelocity + d6 * (0.699999988079071D / d9 / (double) f6));
            this.rotationYaw += this.randomYawVelocity * 0.1F;
            float f7 = (float) (2.0D / (d9 + 1.0D));
            float f8 = 0.06F;
            this.moveRelative(0.0F, -1.0F, f8 * (f5 * f7 + (1.0F - f7)));

            if (this.slowed) {
                this.moveEntity(this.motionX * 0.800000011920929D * moveSpeedMultiplier, this.motionY * 0.800000011920929D * moveSpeedMultiplier, this.motionZ * 0.800000011920929D * moveSpeedMultiplier);
            } else {
                this.moveEntity(this.motionX * moveSpeedMultiplier, this.motionY * moveSpeedMultiplier, this.motionZ * moveSpeedMultiplier);
            }

            Vec3d vec31 = new Vec3d(this.motionX, this.motionY, this.motionZ).normalize();
            float f9 = (float) (vec31.dotProduct(vec32) + 1.0D) / 2.0F;
            f9 = 0.8F + 0.15F * f9;
            this.motionX *= (double) f9;
            this.motionZ *= (double) f9;
            this.motionY *= 0.9100000262260437D;
        }

        this.renderYawOffset = this.rotationYaw;
        this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
        this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
        this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
        this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
        this.dragonPartBody.height = 3.0F;
        this.dragonPartBody.width = 5.0F;
        this.dragonPartWing1.height = 2.0F;
        this.dragonPartWing1.width = 4.0F;
        this.dragonPartWing2.height = 3.0F;
        this.dragonPartWing2.width = 4.0F;
        f1 = (float) (this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
        f2 = MathHelper.cos(f1);
        float f10 = -MathHelper.sin(f1);
        float f3 = this.rotationYaw * (float) Math.PI / 180.0F;
        float f11 = MathHelper.sin(f3);
        float f4 = MathHelper.cos(f3);
        this.dragonPartBody.onUpdate();
        this.dragonPartBody.setLocationAndAngles(this.posX + (double) (f11 * 0.5F), this.posY, this.posZ - (double) (f4 * 0.5F), 0.0F, 0.0F);
        this.dragonPartWing1.onUpdate();
        this.dragonPartWing1.setLocationAndAngles(this.posX + (double) (f4 * 4.5F), this.posY + 2.0D, this.posZ + (double) (f11 * 4.5F), 0.0F, 0.0F);
        this.dragonPartWing2.onUpdate();
        this.dragonPartWing2.setLocationAndAngles(this.posX - (double) (f4 * 4.5F), this.posY + 2.0D, this.posZ - (double) (f11 * 4.5F), 0.0F, 0.0F);

        if (!this.worldObj.isRemote && this.hurtTime == 0) {
            this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
            this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
            this.attackEntitiesInList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.getEntityBoundingBox().expand(1.0D, 1.0D, 1.0D)));
        }

        double[] adouble1 = this.getMovementOffsets(5, 1.0F);
        double[] adouble = this.getMovementOffsets(0, 1.0F);
        f12 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
        float f13 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
        this.dragonPartHead.onUpdate();
        this.dragonPartHead.setLocationAndAngles(this.posX + (double) (f12 * 5.5F * f2), this.posY + (adouble[1] - adouble1[1]) * 1.0D + (double) (f10 * 5.5F), this.posZ - (double) (f13 * 5.5F * f2), 0.0F, 0.0F);

        for (int j = 0; j < 3; ++j) {
            EntityDragonPart entitydragonpart = null;

            if (j == 0) {
                entitydragonpart = this.dragonPartTail1;
            }

            if (j == 1) {
                entitydragonpart = this.dragonPartTail2;
            }

            if (j == 2) {
                entitydragonpart = this.dragonPartTail3;
            }

            double[] adouble2 = this.getMovementOffsets(12 + j * 2, 1.0F);
            float f14 = this.rotationYaw * (float) Math.PI / 180.0F + this.simplifyAngle(adouble2[0] - adouble1[0]) * (float) Math.PI / 180.0F * 1.0F;
            float f15 = MathHelper.sin(f14);
            float f16 = MathHelper.cos(f14);
            float f17 = 1.5F;
            float f18 = (float) (j + 1) * 2.0F;
            entitydragonpart.onUpdate();
            entitydragonpart.setLocationAndAngles(this.posX - (double) ((f11 * f17 + f15 * f18) * f2), this.posY + (adouble2[1] - adouble1[1]) * 1.0D - (double) ((f18 + f17) * f10) + 1.5D, this.posZ + (double) ((f4 * f17 + f16 * f18) * f2), 0.0F, 0.0F);
        }

        if (!this.worldObj.isRemote) {
            this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getEntityBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartBody.getEntityBoundingBox());
        }

//        getPhaseManager().setPhase(PhaseList.HOLDING_PATTERN);
    }

    public void onCrystalTargeted(EntityPlayer player, boolean destroyed) {
        if (behaviour == EnumBehaviour.DEAD) return;
        target = player;
        if (destroyed || behaviour == EnumBehaviour.LOW_HEALTH_STRATEGY) {
            attackInProgress = ATTACK_CHAOS_CHASER;
            behaviour = EnumBehaviour.CHARGING;
            nextAttackTimer = 20;
            attackTimer = 100;
            updateCrystals();
        } else {
            attackInProgress = ATTACK_FIREBALL_CHARGE;
            previousBehaviour = behaviour;
            behaviour = EnumBehaviour.CHARGING;
            nextAttackTimer = 20;
            attackTimer = 1000;
        }
        if (deathTicks <= 0) {
            this.worldObj.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 20.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
        }
    }

    public void updateCrystals() {
        if (crystals == null) crystals = new ArrayList<EntityGuardianCrystal>();
        List<EntityGuardianCrystal> list = worldObj.getEntitiesWithinAABB(EntityGuardianCrystal.class, new AxisAlignedBB(homeX, homeY, homeZ, homeX, homeY, homeZ).expand(200, 200, 200));
        activeCrystals = 0;
        for (EntityGuardianCrystal crystal : list) {
            if (!crystals.contains(crystal)) crystals.add(crystal);
            if (crystal.isAlive()) activeCrystals++;
        }
    }

    private void customAIUpdate() {

        if (getHealth() > 0 && getHealth() < getMaxHealth() * 0.2F) behaviour = EnumBehaviour.LOW_HEALTH_STRATEGY;

        if (ticksExisted % 200 == 0) {
            crystals = null;
            activeCrystals = 0;
            setCrystalPos(null);
//            crystalY = -1;
//			connectedCrystalID = -1;
            healingChaosCrystal = null;
            updateCrystals();
        }


        switch (behaviour) {
            case ROAMING:
                if (Utils.getClosestPlayer(worldObj, homeX, homeY, homeZ, 200, true) != null) selectNewBehaviour();
                break;

            case GO_HOME:
                if (Utils.getDistanceAtoB(posX, posZ, homeX, homeZ) < 70) selectNewBehaviour();
                break;

            case GUARDING:
                break;

            case CHARGING:
                if (Utils.getDistanceAtoB(posX, posZ, homeX, homeZ) > 300) behaviour = EnumBehaviour.GO_HOME;
                break;

            case CIRCLE_PLAYER:
                circlePosition += (0.02F * circleDirection);
                if (Utils.getDistanceAtoB(posX, posZ, homeX, homeZ) > 300 || posY > 250)
                    behaviour = EnumBehaviour.GO_HOME;
                break;

            case LOW_HEALTH_STRATEGY:
                if (Utils.getClosestPlayer(worldObj, targetX, targetY, targetZ, 60, true) != null && attackInProgress != ATTACK_TELEPORT) {
                    int escape = 0;
                    boolean flag = false;
                    while (!flag && escape < 50) {
                        targetX = homeX + ((rand.nextDouble() - 0.5D) * 220D);
                        targetY = homeY + 30 + rand.nextDouble() * 20D;
                        targetZ = homeZ + ((rand.nextDouble() - 0.5D) * 220D);
                        if (Utils.getClosestPlayer(worldObj, targetX, targetY, targetZ, 60D, true) == null) {
                            flag = true;
                        }
                        escape++;
                    }
                    target = null;
                }

                break;

            case DEAD:
                target = null;
                targetX = homeX;
                targetY = homeY;
                targetZ = homeZ;
                break;
        }
//ignitionChargeTimer = 10;

        if (behaviour == EnumBehaviour.DEAD) return;

        if (ticksExisted % 1000 == 0 && rand.nextBoolean()) selectNewBehaviour();

        if (ignitionChargeTimer > 1 || (ignitionChargeTimer == 1 && ticksExisted % 20 == 0) && !DEConfig.disableGuardianCrystalRespawn) {
            ignitionChargeTimer--;
        }
        if (ignitionChargeTimer <= 0 && !worldObj.isRemote) {
            if ((ticksExisted - 19) % 20 == 0) {
                ignitionChargeTimer = (behaviour == EnumBehaviour.LOW_HEALTH_STRATEGY ? 1000 : 2000) + rand.nextInt(600);
            }

            if (activeCrystals < crystals.size() && ticksExisted % 10 == 0) {
                EntityGuardianCrystal closest = null;
                for (EntityGuardianCrystal crystal : crystals) {
                    if (!crystal.isAlive() && (closest == null || getDistanceToEntity(crystal) < getDistanceToEntity(closest))) {
                        closest = crystal;
                    }
                }
                if (closest != null) {
                    EntityGuardianProjectile charge = new EntityGuardianProjectile(worldObj, EntityGuardianProjectile.IGNITION_CHARGE, closest, 0, this);
                    charge.setPosition(dragonPartHead.posX + Math.cos((rotationYaw - 90) / 180.0F * (float) Math.PI) * 2, dragonPartHead.posY + 1.5, dragonPartHead.posZ + Math.sin((rotationYaw - 90) / 180.0F * (float) Math.PI) * 2);
                    worldObj.spawnEntityInWorld(charge);
                }
            }
        }

        updateAttack();
        bossInfo.setPercent(getHealth() / getMaxHealth());
    }

    private void updateAttack() {
        if (worldObj.isRemote || behaviour == EnumBehaviour.DEAD) return;

        if (behaviour == EnumBehaviour.FIREBOMB && Utils.getDistanceAtoB(posX, posY, posZ, homeX, homeY + 30, homeZ) <= 3) {
            if (target == null || ticksExisted % 100 == 0) {
                setNewTarget();
            }
            if (target != null) {
                double distance = Utils.getDistanceAtoB(target.posX, target.posZ, dragonPartHead.posX, dragonPartHead.posZ);
                if (Utils.getDistanceAtoB(target.posX, target.posZ, posX, posZ) < 5) distance *= -1;
                float anglePitch = (float) Math.toDegrees(Math.atan2(target.posY - dragonPartHead.posY, distance)) * -1F;
                float angleYaw = (float) Math.toDegrees(Math.atan2(target.posX - dragonPartHead.posX, target.posZ - posZ)) * -1F;
                rotationPitch = anglePitch;
                if (Utils.getDistanceAtoB(target.posX, target.posZ, posX, posZ) > 8) {
                    rotationYaw = angleYaw + 180;
                }

                if (ticksExisted % 2 == 0) {
                    EntityGuardianProjectile projectile = new EntityGuardianProjectile(worldObj, EntityGuardianProjectile.FIREBOMB, target instanceof EntityLivingBase ? (EntityLivingBase) target : null, 5F + (rand.nextFloat() * 8F), this);
                    projectile.setPosition(dragonPartHead.posX + Math.cos((rotationYaw - 90) / 180.0F * (float) Math.PI) * 2, dragonPartHead.posY + 1.5, dragonPartHead.posZ + Math.sin((rotationYaw - 90) / 180.0F * (float) Math.PI) * 2);
                    worldObj.spawnEntityInWorld(projectile);
                }
            }


        } else if (nextAttackTimer > 0) nextAttackTimer--;
        else if (nextAttackTimer == 0) {

            Entity attackTarget = target;
            @SuppressWarnings("unchecked") List<EntityPlayer> targets = attackTarget == null ? worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(homeX, homeY, homeZ, homeX, homeY, homeZ).expand(100, 100, 100)) : null;

            if (targets != null && targets.size() > 0) {
                Iterator<EntityPlayer> i = targets.iterator();
                while (i.hasNext()) {
                    if (i.next().capabilities.isCreativeMode) i.remove();
                }
            }

            if (attackTarget == null && targets != null && targets.size() > 0)
                attackTarget = targets.get(rand.nextInt(targets.size()));
            if (attackTarget == null) return;


            //Select an attack
            if (attackInProgress == -1) {
                selectNewAttack();
                switch (attackInProgress) {
                    case ATTACK_FIREBALL_CHARGE: {
                        attackTimer = 90 + rand.nextInt(80);
                        previousBehaviour = behaviour;
                        behaviour = EnumBehaviour.CHARGING;
                    }
                    break;
                    case ATTACK_FIREBALL_CHASER:
                        attackTimer = 10 + rand.nextInt(80);
                        break;
                    case ATTACK_ENERGY_CHASER:
                        attackTimer = 10 + rand.nextInt(80);
                        break;
                    case ATTACK_CHAOS_CHASER:
                        attackTimer = 10 + rand.nextInt(80);
                        break;
                    case ATTACK_TELEPORT:
                        attackTimer = 90 + rand.nextInt(80);
                        break;
                }
            }

            switch (attackInProgress) {
                case ATTACK_FIREBALL_CHARGE:
                    if (target == null && behaviour == EnumBehaviour.CHARGING) target = attackTarget;
                    if (Utils.getDistanceAtoB(posX, posY, posZ, attackTarget.posX, attackTarget.posY, attackTarget.posZ) > 10) {
                        if (attackTimer % 2 == 0) {
                            EntityGuardianProjectile projectile = new EntityGuardianProjectile(worldObj, EntityGuardianProjectile.FIREBOMB, attackTarget instanceof EntityLivingBase ? (EntityLivingBase) attackTarget : null, 5F + (rand.nextFloat() * 8F), this);
                            projectile.setPosition(dragonPartHead.posX, dragonPartHead.posY, dragonPartHead.posZ);
                            worldObj.spawnEntityInWorld(projectile);
                        }

                        double distance = Utils.getDistanceAtoB(attackTarget.posX, attackTarget.posZ, dragonPartHead.posX, dragonPartHead.posZ);
                        float angle = (float) Math.toDegrees(Math.atan2(attackTarget.posY - dragonPartHead.posY, distance)) * -1F;
                        rotationPitch = angle;

                    } else attackTimer = 0;
                    break;
                case ATTACK_FIREBALL_CHASER:
                    if (attackTimer % 10 == 0) {
                        EntityGuardianProjectile projectile = new EntityGuardianProjectile(worldObj, EntityGuardianProjectile.FIRE_CHASER, attackTarget instanceof EntityLivingBase ? (EntityLivingBase) attackTarget : null, 5F + (rand.nextFloat() * 2F), this);
                        projectile.setPosition(dragonPartHead.posX, dragonPartHead.posY, dragonPartHead.posZ);
                        worldObj.spawnEntityInWorld(projectile);
                    }
                    break;
                case ATTACK_ENERGY_CHASER:
                    if (attackTimer % 10 == 0) {
                        EntityGuardianProjectile projectile = new EntityGuardianProjectile(worldObj, EntityGuardianProjectile.ENERGY_CHASER, attackTarget instanceof EntityLivingBase ? (EntityLivingBase) attackTarget : null, 5F + (rand.nextFloat() * 10F), this);
                        projectile.setPosition(dragonPartHead.posX, dragonPartHead.posY, dragonPartHead.posZ);
                        worldObj.spawnEntityInWorld(projectile);
                    }
                    break;
                case ATTACK_CHAOS_CHASER:
                    if (attackTimer % 10 == 0) {
                        EntityGuardianProjectile projectile = new EntityGuardianProjectile(worldObj, EntityGuardianProjectile.CHAOS_CHASER, attackTarget instanceof EntityLivingBase ? (EntityLivingBase) attackTarget : null, 5F + (rand.nextFloat() * 10F), this);
                        projectile.setPosition(dragonPartHead.posX, dragonPartHead.posY, dragonPartHead.posZ);
                        worldObj.spawnEntityInWorld(projectile);
                    }
                    break;
                case ATTACK_TELEPORT:
                    if (target == null) target = Utils.getClosestPlayer(worldObj, posX, posY, posZ, 100, false);
                    if (target == null) {
                        attackInProgress = -1;
                        return;
                    }
                    if (Utils.getDistanceAtoB(posX, posY, posZ, attackTarget.posX, attackTarget.posY, attackTarget.posZ) > 15) {
                        if (attackTimer % 2 == 0) {
                            EntityGuardianProjectile projectile = new EntityGuardianProjectile(worldObj, EntityGuardianProjectile.TELEPORT, attackTarget instanceof EntityLivingBase ? (EntityLivingBase) attackTarget : null, 5F + (rand.nextFloat() * 8F), this);
                            projectile.setPosition(dragonPartHead.posX, dragonPartHead.posY, dragonPartHead.posZ);
                            worldObj.spawnEntityInWorld(projectile);
                        }

                        double distance = Utils.getDistanceAtoB(attackTarget.posX, attackTarget.posZ, dragonPartHead.posX, dragonPartHead.posZ);
                        float angle = (float) Math.toDegrees(Math.atan2(attackTarget.posY - dragonPartHead.posY, distance)) * -1F;
                        rotationPitch = angle;

                    } else attackTimer = 0;
                    break;
            }

            attackTimer--;
            if (attackTimer <= -1) {
                if (attackInProgress == ATTACK_FIREBALL_CHARGE) behaviour = previousBehaviour;
                attackInProgress = -1;
                nextAttackTimer = -1;
            }
        } else
            nextAttackTimer = behaviour == EnumBehaviour.LOW_HEALTH_STRATEGY ? 10 + rand.nextInt(50) : 60 + rand.nextInt(200);
    }

    private static final List<WeightedAttack> weightedAttacks = Lists.newArrayList(new WeightedAttack(16, ATTACK_FIREBALL_CHARGE), new WeightedAttack(14, ATTACK_FIREBALL_CHASER), new WeightedAttack(12, ATTACK_ENERGY_CHASER), new WeightedAttack(10, ATTACK_CHAOS_CHASER));

    private static final List<WeightedAttack> weightedLowHealthAttaxks = Lists.newArrayList(new WeightedAttack(5, ATTACK_FIREBALL_CHASER), new WeightedAttack(5, ATTACK_TELEPORT), new WeightedAttack(10, ATTACK_ENERGY_CHASER), new WeightedAttack(15, ATTACK_CHAOS_CHASER));

    private static final List<WeightedBehaviour> weightedBehaviours = Lists.newArrayList(new WeightedBehaviour(1, EnumBehaviour.LOW_HEALTH_STRATEGY), new WeightedBehaviour(10, EnumBehaviour.GUARDING), new WeightedBehaviour(4, EnumBehaviour.CHARGING), new WeightedBehaviour(12, EnumBehaviour.FIREBOMB), new WeightedBehaviour(20, EnumBehaviour.CIRCLE_PLAYER));

    private void selectNewAttack() {
        if (behaviour == EnumBehaviour.DEAD) return;
        if (behaviour == EnumBehaviour.LOW_HEALTH_STRATEGY) {
            attackInProgress = ((WeightedAttack) WeightedRandom.getRandomItem(rand, weightedLowHealthAttaxks)).attack;
        } else if (behaviour != EnumBehaviour.FIREBOMB) {
            attackInProgress = ((WeightedAttack) WeightedRandom.getRandomItem(rand, weightedAttacks)).attack;
        } else {
            attackInProgress = ATTACK_ENERGY_CHASER;
        }
    }

    private void selectNewBehaviour() {
        if (worldObj.isRemote || behaviour == EnumBehaviour.DEAD) return;
        EnumBehaviour newBehaviour = behaviour;
        while (newBehaviour == behaviour)
            newBehaviour = WeightedRandom.getRandomItem(rand, weightedBehaviours).randomBehaviour;
        behaviour = newBehaviour;
        previousBehaviour = behaviour;
    }

    private void updateTarget() {
        switch (behaviour) {
            case ROAMING:


                break;
            case GO_HOME:


                break;
            case GUARDING:


                break;
            case CHARGING:


                break;
            case FIREBOMB:
                if (Utils.getDistanceAtoB(posX, posY, posZ, homeX, homeY + 30, homeZ) > 3) {
                    targetX = homeX;
                    targetY = homeY + 30;
                    targetZ = homeZ;
                }

                break;
            case CIRCLE_PLAYER:


                break;
            case LOW_HEALTH_STRATEGY:


                break;
            case DEAD:
                this.targetX = homeX;
                this.targetY = homeY;
                this.targetZ = homeZ;
                this.target = null;
                break;
        }
    }

    private void setNewTarget() {
        if (behaviour == EnumBehaviour.DEAD) {
            return;
        }
        this.forceNewTarget = false;

        switch (behaviour) {
            case ROAMING:

                boolean flag = false;
                do {
                    targetX = homeX;
                    targetY = homeY + 30 + (rand.nextDouble() * 30);
                    targetZ = homeZ;
                    targetX += rand.nextFloat() * 120.0F - 60.0F;
                    targetZ += rand.nextFloat() * 120.0F - 60.0F;
                    double d0 = posX - targetX;
                    double d1 = posY - targetY;
                    double d2 = posZ - targetZ;
                    flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;
                } while (!flag);
                this.target = null;

                break;
            case GO_HOME:
            case GUARDING:

                this.targetX = homeX;
                targetY = homeY + 25 + (rand.nextDouble() * 30);
                this.targetZ = homeZ;
                this.target = null;

                break;
            case CHARGING:
            case CIRCLE_PLAYER:
                this.target = Utils.getClosestPlayer(worldObj, homeX, homeY, homeZ, 200, false);
                break;
            case LOW_HEALTH_STRATEGY:


                break;
            case FIREBOMB:
                @SuppressWarnings("unchecked") List<EntityPlayer> targets = worldObj.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().expand(150, 150, 150), EntitySelectors.CAN_AI_TARGET);
                target = null;
                while (targets.size() > 0 && target == null) {
                    EntityPlayer potentialTarget = targets.get(rand.nextInt(targets.size()));
                    if (worldObj.rayTraceBlocks(new Vec3d(posX, posY, posZ), new Vec3d(potentialTarget.posX, potentialTarget.posY, potentialTarget.posZ)) == null) {
                        target = potentialTarget;
                    } else targets.remove(potentialTarget);
                }

                break;
            case DEAD:


                break;
        }

    }

    @Override
    public boolean attackEntityFromPart(EntityDragonPart part, DamageSource damageSource, float dmg) {
        if (behaviour == EnumBehaviour.DEAD) return false;

        if (part != this.dragonPartHead) {
            dmg = dmg / 4.0F + 1.0F;
        }

        if (dmg > 50) dmg -= ((dmg - 50) * 0.7);

        switch (behaviour) {
            case ROAMING:
                break;
            case GO_HOME:
                break;
            case GUARDING:
                if (rand.nextInt(5) == 0) selectNewBehaviour();
                break;
            case CHARGING:
                if (rand.nextInt(6) == 0) selectNewBehaviour();
                break;
            case CIRCLE_PLAYER:
                if (rand.nextInt(6) == 0) selectNewBehaviour();
                else if (rand.nextInt(4) == 0) {
                    circleDirection *= -1;
                }

                break;
            case LOW_HEALTH_STRATEGY:
                if (rand.nextInt(6) == 0 && getHealth() >= getMaxHealth() * 0.2F) {
                    selectNewBehaviour();
                }
                if (damageSource.getEntity() instanceof EntityPlayer && attackInProgress != ATTACK_TELEPORT) {
                    int escape = 0;
                    boolean flag = false;
                    while (!flag && escape < 50) {
                        targetX = homeX + ((rand.nextDouble() - 0.5D) * 260D);
                        targetY = homeY + 20 + (rand.nextDouble() - 0.5D) * 50D;
                        targetZ = homeZ + ((rand.nextDouble() - 0.5D) * 260D);
                        if (this.getDistanceToEntity(damageSource.getEntity()) >= 70) {
                            flag = true;
                        }
                        escape++;
                    }
                    target = null;
                }

                break;
            case FIREBOMB:
                if ((target == null && Utils.getDistanceAtoB(posX, posY, posZ, homeX, homeY + 30, homeZ) <= 3) || rand.nextInt(5) == 0) {
                    selectNewBehaviour();
                }
                if (damageSource.getEntity() instanceof EntityPlayer && damageSource.getEntity() != target && worldObj.rayTraceBlocks(new Vec3d(posX, posY, posZ), new Vec3d(damageSource.getEntity().posX, damageSource.getEntity().posY, damageSource.getEntity().posZ)) == null) {
                    target = damageSource.getEntity();
                }
                break;
            case DEAD:
                break;
        }

        if ((damageSource.getEntity() instanceof EntityPlayer || damageSource.isExplosion()) && healingChaosCrystal == null)//tod reanable this
        {
            super.attackEntityFrom(damageSource, dmg);
        } else if (damageSource.getEntity() instanceof EntityPlayer) {
            ((EntityPlayer) damageSource.getEntity()).addChatComponentMessage(new TextComponentTranslation("msg.de.guardianAttackBlocked.txt").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
        }

        return true;
    }

    private static enum EnumBehaviour {
        /**
         * Will roam around home until a player is spotted
         */
        ROAMING(1F),
        /**
         * Will head home
         */
        GO_HOME(1.3F),
        /**
         * Will will fly around above home attacking players
         */
        GUARDING(0.8F),
        /**
         * Will charge players as the vanilla dragon dose
         */
        CHARGING(2F),
        /**
         * Will fly to centre of island and unleash hell
         */
        FIREBOMB(1.5F),
        /**
         * Will circle a player and shoot at that player
         */
        CIRCLE_PLAYER(1.2F),
        /**
         * Will try to avoid players, will try to teleport players, will try to relight crystals
         */
        LOW_HEALTH_STRATEGY(2F),
        /**
         * will die...
         */
        DEAD(0.5F);
        public float dragonSpeed;

        private EnumBehaviour(float dragonSpeed) {
            this.dragonSpeed = dragonSpeed;
        }

    }

    private static class WeightedAttack extends WeightedRandom.Item {
        public int attack;

        public WeightedAttack(int weight, int attack) {
            super(weight);
            this.attack = attack;
        }
    }

    private static class WeightedBehaviour extends WeightedRandom.Item {
        public EnumBehaviour randomBehaviour;

        public WeightedBehaviour(int weight, EnumBehaviour randomBehaviour) {
            super(weight);
            this.randomBehaviour = randomBehaviour;
        }
    }

    @Override
    protected void onDeathUpdate() {
        if (deathTicks == 0) {

            TileEntity tile = worldObj.getTileEntity(new BlockPos(homeX, homeY, homeZ));
            if (tile instanceof TileChaosCrystal) {
                ((TileChaosCrystal) tile).setDefeated();
            } else {
                boolean breac = false;
                for (int x = homeX - 100; x < homeX + 100; x++) {
                    for (int y = homeY - 100; y < homeY + 100; y++) {
                        if (y < 0 || y > 255) continue;
                        for (int z = homeZ - 100; z < homeZ + 100; z++) {
                            tile = worldObj.getTileEntity(new BlockPos(x, y, z));
                            if (tile instanceof TileChaosCrystal) {
                                ((TileChaosCrystal) tile).setDefeated();
                                breac = true;
                            }
                            if (breac) {
                                break;
                            }
                        }
                        if (breac) {
                            break;
                        }
                    }
                    if (breac) {
                        break;
                    }
                }
            }
        }

        ++this.deathTicks;

        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + (double) f, this.posY + 2.0D + (double) f1, this.posZ + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        int i;
        int j;

        if (this.deathTicks == 1) {
            this.worldObj.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ENDERDRAGON_DEATH, SoundCategory.HOSTILE, 50.0F, 1F, false);
        }

        if (getDistance(homeX, homeY, homeZ) < 20 && deathTicks % 2 == 0) {
            EntityLightningBolt bolt = new EntityLightningBolt(worldObj, homeX, homeY + 1, homeZ, true);
            bolt.ignoreFrustumCheck = true;
            worldObj.addWeatherEffect(bolt);
        }

        if (getDistance(homeX, homeY, homeZ) < 5 && !this.worldObj.isRemote) {
            i = 200000;

            while (i > 0) {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }

            //spawnEgg();
            updateCrystals();
            for (EntityGuardianCrystal crystal : crystals) crystal.setDeathTimer();
            setDead();
        }
    }

    private void collideWithEntities(List par1List) {
//		if (true) return;
//
//		double d0 = (this.dragonPartBody.getEntityBoundingBox().minX + this.dragonPartBody.getEntityBoundingBox().maxX) / 2.0D;
//		double d1 = (this.dragonPartBody.getEntityBoundingBox().minZ + this.dragonPartBody.getEntityBoundingBox().maxZ) / 2.0D;
//		Iterator iterator = par1List.iterator();
//
//		while (iterator.hasNext()) {
//			Entity entity = (Entity) iterator.next();
//
//			if (entity instanceof EntityLivingBase) {
//				double d2 = entity.posX - d0;
//				double d3 = entity.posZ - d1;
//				double d4 = d2 * d2 + d3 * d3;
//				if (rand.nextInt(isUber ? 3 : 10) == 0) entity.addVelocity(d2 / d4 * 8.0D, 5.20000000298023224D, d3 / d4 * 8.0D);
//				entity.velocityChanged = true;
//				((EntityLivingBase)entity).setLastAttacker(this);
//			}
//			if (entity instanceof EntityLivingBase && isUber){
//				((EntityLivingBase)entity).setLastAttacker(this);
//				entity.attackEntityFrom(new DamageSourceChaos(this), 20F);
//			}
//		}
    }

    private void attackEntitiesInList(List par1List) {
        if (behaviour == EnumBehaviour.CHARGING) {
            boolean hasAttacked = false;
            for (int i = 0; i < par1List.size(); ++i) {
                Entity entity = (Entity) par1List.get(i);

                if (entity instanceof EntityPlayer) {
                    ((EntityLivingBase) entity).setLastAttacker(this);
                    entity.attackEntityFrom(new DEDamageSources.DamageSourceChaos(this), 50F);
                    hasAttacked = true;
                }
            }

            if (hasAttacked && rand.nextInt(2) == 0) behaviour = EnumBehaviour.GUARDING;
        }
    }

    private void updateDragonEnderCrystal() {
        if (worldObj.isRemote) {
            BlockPos pos = getCrystalPos();
            if (healingChaosCrystal == null && pos != null && ticksExisted % 10 == 0) {
                List<EntityGuardianCrystal> list = worldObj.getEntitiesWithinAABB(EntityGuardianCrystal.class, new AxisAlignedBB(pos.add(-2, -2, -2), pos.add(3, 3, 3)));
                if (list.size() > 0) {
                    healingChaosCrystal = list.get(0);
                }
            } else if (healingChaosCrystal != null) {
                if (pos == null || Utils.getDistanceSq(healingChaosCrystal.posX, healingChaosCrystal.posY, healingChaosCrystal.posZ, pos.getX(), pos.getY(), pos.getZ()) > 10) {
                    healingChaosCrystal = null;
                }
            }

            return;
        }

        if (getHealth() <= 0) {
            healingChaosCrystal = null;
            setCrystalPos(null);
//			connectedCrystalID = -1;
            return;
        }

        if (this.healingChaosCrystal != null) {
            if (!healingChaosCrystal.isAlive()) {
                this.attackEntityFromPart(this.dragonPartHead, DamageSource.causeExplosionDamage((Explosion) null), 10.0F);
                healingChaosCrystal = null;
            } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 2F);
            }
        }

        if (this.rand.nextInt(10) == 0) {
            EntityGuardianCrystal closest = null;
            for (EntityGuardianCrystal crystal : crystals)
                if (crystal.isAlive() && (closest == null || getDistanceToEntity(crystal) < getDistanceToEntity(closest)))
                    closest = crystal;
            healingChaosCrystal = closest;
            if (healingChaosCrystal != null) {
//				connectedCrystalID = healingChaosCrystal.getEntityId();
                setCrystalPos(new BlockPos((int) Math.floor(healingChaosCrystal.posX), (int) Math.floor(healingChaosCrystal.posY), (int) Math.floor(healingChaosCrystal.posZ)));
//                crystalX = (int) Math.floor(healingChaosCrystal.posX);
//                crystalY = (int) Math.floor(healingChaosCrystal.posY);
//                crystalZ = (int) Math.floor(healingChaosCrystal.posZ);
            } else {
                setCrystalPos(null);
//                crystalY = -1;
//				connectedCrystalID = -1;
            }
        }
    }

    private float simplifyAngle(double par1) {
        return (float) MathHelper.wrapDegrees(par1);
    }

    private boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB) {
        //if (!ConfigHandler.dragonBreaksBlocks) return false;

        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxX);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.maxY);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for (int x = i; x <= l; ++x) {
            for (int y = j; y <= i1; ++y) {
                for (int z = k; z <= j1; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = this.worldObj.getBlockState(pos);
                    Block block = state.getBlock();

                    if (!worldObj.isAirBlock(pos) && !DESTRUCTION_BLACKLIST.contains(block)) {
                        if (block.canEntityDestroy(state, worldObj, pos, this) && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
                            flag1 = this.worldObj.setBlockToAir(pos) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            double d1 = par1AxisAlignedBB.minX + (par1AxisAlignedBB.maxX - par1AxisAlignedBB.minX) * (double) this.rand.nextFloat();
            double d2 = par1AxisAlignedBB.minY + (par1AxisAlignedBB.maxY - par1AxisAlignedBB.minY) * (double) this.rand.nextFloat();
            double d0 = par1AxisAlignedBB.minZ + (par1AxisAlignedBB.maxZ - par1AxisAlignedBB.minZ) * (double) this.rand.nextFloat();
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d1, d2, d0, 0.0D, 0.0D, 0.0D);
        }

        return flag;
    }

    private void spawnEgg() {
//		if (ConfigHandler.dragonEggSpawnLocation[0] != 0 || ConfigHandler.dragonEggSpawnLocation[1] != 0 || ConfigHandler.dragonEggSpawnLocation[1] != 0 && !isUber) {
//			homeX = ConfigHandler.dragonEggSpawnLocation[0];
//			homeY = ConfigHandler.dragonEggSpawnLocation[1];
//			homeZ = ConfigHandler.dragonEggSpawnLocation[2];
//		}
//
//		BlockEndPortal.field_149948_a = true;
//
//		if (createPortal || isUber) {
//			createEnderPortal(homeX, homeZ);
//		}
//		LogHelper.info("spawn egg");
//		if (worldObj.getBlock(homeX, homeY + 1, homeZ) == Blocks.air) {
//			worldObj.setBlock(homeX, homeY + 1, homeZ, Blocks.dragon_egg);
//			LogHelper.info("spawn egg2 " + homeX + " " + homeY + " " + homeZ);
//		}else {
//			for (int i = homeY + 1; i < 250; i++) {
//				if (worldObj.getBlock(homeX, i, homeZ) == Blocks.air) {
//					worldObj.setBlock(homeX, i, homeZ, Blocks.dragon_egg);
//					LogHelper.info("spawn egg3");
//					break;
//				}
//			}
//		}
//
//		for (int iX = homeX - 2; iX <= homeX + 2; iX++)
//		{
//			for (int iZ = homeZ - 2; iZ <= homeZ + 2; iZ++)
//			{
//
//				if (worldObj.getBlock(iX, homeY - 4, iZ) == Blocks.bedrock && !(iX == homeX && iZ == homeZ))
//				{
//					worldObj.setBlock(iX, homeY - 3, iZ, Blocks.end_portal);
//				}
//			}
//		}
//
//
//		worldObj.setBlock(homeX - 1, homeY - 1, homeZ, Blocks.torch);
//		worldObj.setBlock(homeX + 1, homeY - 1, homeZ, Blocks.torch);
//		worldObj.setBlock(homeX, homeY - 1, homeZ - 1, Blocks.torch);
//		worldObj.setBlock(homeX, homeY - 1, homeZ + 1, Blocks.torch);
//
//
//		BlockEndPortal.field_149948_a = false;
    }

    private void createEnderPortal(int par1, int par2) {
//        int b0 = homeY - 3;
//        byte b1 = 4;
//
//        for (int k = b0 - 1; k <= b0 + 32; ++k) {
//            for (int l = par1 - b1; l <= par1 + b1; ++l) {
//                for (int i1 = par2 - b1; i1 <= par2 + b1; ++i1) {
//                    double d0 = (double) (l - par1);
//                    double d1 = (double) (i1 - par2);
//                    double d2 = d0 * d0 + d1 * d1;
//
//                    if (d2 <= ((double) b1 - 0.5D) * ((double) b1 - 0.5D)) {
//                        if (k < b0) {
//                            if (d2 <= ((double) (b1 - 1) - 0.5D) * ((double) (b1 - 1) - 0.5D)) {
//                                this.worldObj.setBlock(l, k, i1, Blocks.bedrock);
//                            }
//                        } else if (k > b0) {
//                            this.worldObj.setBlock(l, k, i1, Blocks.air);
//                        } else if (d2 > ((double) (b1 - 1) - 0.5D) * ((double) (b1 - 1) - 0.5D)) {
//                            this.worldObj.setBlock(l, k, i1, Blocks.bedrock);
//                        }
//                    }
//                }
//            }
//        }
//
//        this.worldObj.setBlock(par1, b0 + 0, par2, Blocks.bedrock);
//        this.worldObj.setBlock(par1, b0 + 1, par2, Blocks.bedrock);
//        this.worldObj.setBlock(par1, b0 + 2, par2, Blocks.bedrock);
//        this.worldObj.setBlock(par1, b0 + 3, par2, Blocks.bedrock);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("HomeXCoord", homeX);
        compound.setInteger("HomeYCoord", homeY);
        compound.setInteger("HomeZCoord", homeZ);
        compound.setString("Behaviour", behaviour.name());
        compound.setBoolean("HomeSet", homeSet);
        LogHelper.bigDev("ChaosGuardian: Save");
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        homeX = compound.getInteger("HomeXCoord");
        homeY = compound.getInteger("HomeYCoord");
        homeZ = compound.getInteger("HomeZCoord");
        if (compound.hasKey("Behaviour")) behaviour = EnumBehaviour.valueOf(compound.getString("Behaviour"));
        homeSet = compound.getBoolean("HomeSet");
        targetX = homeX;
        targetZ = homeZ;
        LogHelper.bigDev("ChaosGuardian: Load");
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected void despawnEntity() {

    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float dmg) {

        return super.attackEntityFrom(damageSource, dmg);
    }

    @Nullable
    @Override
    public Entity changeDimension(int dimensionIn) {
        return this;
    }

    public void setCrystalPos(@Nullable BlockPos pos) {
        this.getDataManager().set(CRYSTAL_POSITION, Optional.fromNullable(pos));
    }

    @Nullable
    public BlockPos getCrystalPos() {
        return (BlockPos) ((Optional) this.getDataManager().get(CRYSTAL_POSITION)).orNull();
    }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        bossInfo.removePlayer(player);
    }

    @Override
    public boolean isNonBoss() {
        return false;
    }

    //    @SideOnly(Side.CLIENT)
//    public float getHeadPartYOffset(int p_184667_1_, double[] p_184667_2_, double[] p_184667_3_)
//    {
//        IPhase iphase = this.phaseManager.getCurrentPhase();
//        PhaseList<? extends IPhase > phaselist = iphase.getPhaseList();
//        double d0;
//
//        if (phaselist != PhaseList.LANDING && phaselist != PhaseList.TAKEOFF)
//        {
//            if (iphase.getIsStationary())
//            {
//                d0 = (double)p_184667_1_;
//            }
//            else if (p_184667_1_ == 6)
//            {
//                d0 = 0.0D;
//            }
//            else
//            {
//                d0 = p_184667_3_[1] - p_184667_2_[1];
//            }
//        }
//        else
//        {
//            BlockPos blockpos = this.worldObj.getTopSolidOrLiquidBlock(WorldGenEndPodium.END_PODIUM_LOCATION);
//            float f = Math.max(MathHelper.sqrt_double(this.getDistanceSqToCenter(blockpos)) / 4.0F, 1.0F);
//            d0 = (double)((float)p_184667_1_ / f);
//        }
//
//        return (float)d0;
//    }


    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return null;
    }
}
