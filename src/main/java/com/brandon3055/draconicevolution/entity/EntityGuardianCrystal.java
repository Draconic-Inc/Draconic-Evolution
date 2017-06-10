package com.brandon3055.draconicevolution.entity;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 30/8/2015.
 */
public class EntityGuardianCrystal extends EntityLivingBase {

    private static final DataParameter<Integer> SHIELD_TIME = EntityDataManager.createKey(EntityGuardianCrystal.class, DataSerializers.VARINT);
    private static final DataParameter<Float> HEALTH = EntityDataManager.createKey(EntityGuardianCrystal.class, DataSerializers.FLOAT);

    public int innerRotation;
    public float deathAnimation = 1F;
    public int shieldTime = 0;
    public EntityChaosGuardian guardian;
    private int timeTillDeath = -1;
    public float health = 50;

    public EntityGuardianCrystal(World p_i1698_1_) {
        super(p_i1698_1_);
        this.preventEntitySpawning = true;
        this.setSize(2.0F, 2.0F);
        //this.yOffset = -2;
        this.innerRotation = this.rand.nextInt(100000);
        this.setHealth(getMaxHealth());
        this.ignoreFrustumCheck = true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50F);
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void entityInit() {
        super.entityInit();
        dataManager.register(SHIELD_TIME, shieldTime);
        dataManager.register(HEALTH, health);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (!worldObj.isRemote) {
            health = getHealth();
            dataManager.set(SHIELD_TIME, shieldTime);
            dataManager.set(HEALTH, health);
        } else {
            shieldTime = dataManager.get(SHIELD_TIME);
            health = dataManager.get(HEALTH);
        }
        //setHealth(0);
        if (health > 0) {
            if (shieldTime > 0) {
                shieldTime--;
            }
            if (deathAnimation < 1F) {
                deathAnimation += 0.1F;
            }
            ++this.innerRotation;

//            int i = MathHelper.floor_double(this.posX);
//            int j = MathHelper.floor_double(this.posY);
//            int k = MathHelper.floor_double(this.posZ);
            BlockPos pos = new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));

            if (worldObj.provider instanceof WorldProviderEnd && worldObj.getBlockState(pos).isSideSolid(worldObj, pos, EnumFacing.UP) && worldObj.getBlockState(pos).getBlock() != Blocks.FIRE) {
                worldObj.setBlockState(pos, Blocks.FIRE.getDefaultState());
            }

        } else if (deathAnimation > 0) deathAnimation -= 0.1F;

        if (guardian != null && guardian.isDead) setDeathTimer();
        if (timeTillDeath > 0) timeTillDeath--;
        if (timeTillDeath == 0 && !worldObj.isRemote) {
            worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, posX, posY, posZ, false));
            worldObj.createExplosion(this, posX, posY + 2, posZ, 20, true);

            BlockPos pos = new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
            List<BlockPos> blocks = Lists.newArrayList(BlockPos.getAllInBox(pos.add(-5, -25, -5), pos.add(5, 5, 5)));

            for (BlockPos blockPos : blocks) {
                IBlockState state = worldObj.getBlockState(blockPos);

                if (state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == DEFeatures.infusedObsidian) {
                    EntityFallingBlock fallingBlock = new EntityFallingBlock(worldObj, blockPos.getX(), blockPos.getY(), blockPos.getZ(), state);
                    fallingBlock.fallTime = 2;
                    fallingBlock.shouldDropItem = false;
                    float motion = 2F;
                    fallingBlock.motionX = (rand.nextFloat() - 0.5F) * motion;
                    fallingBlock.motionY = (rand.nextFloat() - 0.5F) * motion;
                    fallingBlock.motionZ = (rand.nextFloat() - 0.5F) * motion;
                    worldObj.setBlockToAir(blockPos);
                    worldObj.spawnEntityInWorld(fallingBlock);
                }
            }
//
//            for (int x = (int) posX - 5; x <= (int) posX + 5; x++) {
//                for (int y = (int) posY - 25; y <= (int) posY + 5; y++) {
//                    for (int z = (int) posZ - 5; z <= (int) posZ + 5; z++) {
//                        Block block = worldObj.getBlock(x, y, z);
//
//                        if (block == Blocks.OBSIDIAN || block == ModBlocks.infusedObsidian) {
//                            EntityFallingBlock fallingBlock = new EntityFallingBlock(worldObj, x, y, z, block);
//                            fallingBlock.field_145812_b = 2;
//                            fallingBlock.field_145813_c = false;
//                            float motion = 2F;
//                            fallingBlock.motionX = (rand.nextFloat() - 0.5F) * motion;
//                            fallingBlock.motionY = (rand.nextFloat() - 0.5F) * motion;
//                            fallingBlock.motionZ = (rand.nextFloat() - 0.5F) * motion;
//                            worldObj.setBlockToAir(x, y, z);
//                            worldObj.spawnEntityInWorld(fallingBlock);
//                        }
//                    }
//                }
//            }
            this.setDead();
        }
    }

    @Override
    public boolean isEntityAlive() {
        return !isDead;
    }

    public void setDeathTimer() {
        if (timeTillDeath > 0) return;
        timeTillDeath = rand.nextInt(400);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float dmg) {
        if (worldObj.isRemote) {
            return false;
        }
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        else if (source.getEntity() instanceof EntityPlayer) {
            if (shieldTime <= 0 && getHealth() > 0 && !this.worldObj.isRemote) {
                setHealth(getHealth() - Math.min(getHealth(), dmg));
                if (getHealth() <= 0) {
                    worldObj.createExplosion(null, this.posX, this.posY, this.posZ, 6.0F, false);
                }
                else {
                    shieldTime = 100 + rand.nextInt(100);
                    DESoundHandler.playSoundFromServer(worldObj, posX + 0.5D, posY + 0.5D, posZ + 0.5D, DESoundHandler.shieldUp, SoundCategory.HOSTILE, 10.0F, rand.nextFloat() * 0.1F + 1.055F, false, 128);
                }
                if (getGuardian() != null){
                    getGuardian().onCrystalTargeted((EntityPlayer) source.getEntity(), getHealth() <= 0);
                }
                return true;
            } else if (shieldTime > 0 && !this.worldObj.isRemote) {
                shieldTime = 100 + rand.nextInt(100);
            }
        }
        return false;
    }

    private EntityChaosGuardian getGuardian() {
        if (guardian == null) {
            @SuppressWarnings("unchecked") List<EntityChaosGuardian> list = worldObj.getEntitiesWithinAABB(EntityChaosGuardian.class, new AxisAlignedBB(posX, posY, posZ, posX, posY, posZ).expand(512, 512, 512));
            if (list.size() > 0) guardian = list.get(0);
            if (guardian != null && guardian.crystals != null && !guardian.crystals.contains(this)) {
                guardian.crystals.add(this);
                guardian.updateCrystals();
            }
        }
        return guardian;
    }

    public void revive() {
        setHealth(getMaxHealth());
        shieldTime = 50;
        worldObj.createExplosion(null, this.posX, this.posY, this.posZ, 6.0F, false);
        if (getGuardian() != null) getGuardian().updateCrystals();
    }

    public boolean isAlive() {
        return getHealth() > 0F;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return new ArrayList<ItemStack>();
    }

    @Nullable
    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return null;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack) {

    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return EnumHandSide.RIGHT;
    }

    @Override
    public boolean isNonBoss() {
        return false;
    }
}