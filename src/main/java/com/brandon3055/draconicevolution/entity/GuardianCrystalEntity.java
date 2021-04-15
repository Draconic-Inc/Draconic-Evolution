package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.worldentity.WorldEntity;
import com.brandon3055.brandonscore.worldentity.WorldEntityHandler;
import com.brandon3055.draconicevolution.entity.guardian.GuardianFightManager;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class GuardianCrystalEntity extends Entity {
   private static final DataParameter<Optional<BlockPos>> BEAM_TARGET = EntityDataManager.defineId(GuardianCrystalEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
   private static final DataParameter<Boolean> SHOW_BOTTOM = EntityDataManager.defineId(GuardianCrystalEntity.class, DataSerializers.BOOLEAN);
   private UUID managerId;
   public int innerRotation;

   public GuardianCrystalEntity(EntityType<?> type, World world) {
      super(type, world);
      this.blocksBuilding = true;
      this.innerRotation = this.random.nextInt(100000);
   }

   public GuardianCrystalEntity(World worldIn, double x, double y, double z, UUID managerId) {
      this(DEContent.guardianCrystal, worldIn);
      this.managerId = managerId;
      this.setPos(x, y, z);
   }

   public UUID getManagerId() {
      return managerId;
   }

   public void setManagerId(UUID managerId) {
      this.managerId = managerId;
   }

   @Override
   protected boolean isMovementNoisy() {
      return false;
   }

   @Override
   protected void defineSynchedData() {
      this.getEntityData().define(BEAM_TARGET, Optional.empty());
      this.getEntityData().define(SHOW_BOTTOM, true);
   }

   @Override
   public void tick() {
      ++this.innerRotation;
      if (this.level instanceof ServerWorld) {
         BlockPos blockpos = this.blockPosition();
         if (((ServerWorld)this.level).dragonFight() != null && this.level.getBlockState(blockpos).isAir()) {
            this.level.setBlockAndUpdate(blockpos, AbstractFireBlock.getState(this.level, blockpos));
         }
      }
   }

   @Override
   protected void addAdditionalSaveData(CompoundNBT compound) {
      if (this.getBeamTarget() != null) {
         compound.put("BeamTarget", NBTUtil.writeBlockPos(this.getBeamTarget()));
      }

      compound.putBoolean("ShowBottom", this.shouldShowBottom());
      compound.putUUID("manager_id", managerId);
   }

   @Override
   protected void readAdditionalSaveData(CompoundNBT compound) {
      if (compound.contains("BeamTarget", 10)) {
         this.setBeamTarget(NBTUtil.readBlockPos(compound.getCompound("BeamTarget")));
      }

      if (compound.contains("ShowBottom", 1)) {
         this.setShowBottom(compound.getBoolean("ShowBottom"));
      }
      managerId = compound.getUUID("manager_id");
   }

   @Override
   public boolean isPickable() {
      return true;
   }

   @Override
   public boolean hurt(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (source.getEntity() instanceof EnderDragonEntity) {
         return false;
      } else {
         if (!this.removed && !this.level.isClientSide) {
            this.remove();
            if (!source.isExplosion()) {
               this.level.explode((Entity)null, this.getX(), this.getY(), this.getZ(), 10.0F, Explosion.Mode.DESTROY);
            }

            this.onCrystalDestroyed(source);
         }

         return true;
      }
   }

   @Override
   public void kill() {
      this.onCrystalDestroyed(DamageSource.GENERIC);
      super.kill();
   }

   private void onCrystalDestroyed(DamageSource source) {
      if (level instanceof ServerWorld && managerId != null) {
         WorldEntity worldEntity = WorldEntityHandler.getWorldEntity(level, managerId);
         if (worldEntity instanceof GuardianFightManager) {
            ((GuardianFightManager) worldEntity).onCrystalDestroyed(this, source);
         }
      }

   }

   public void setBeamTarget(@Nullable BlockPos beamTarget) {
      this.getEntityData().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return this.getEntityData().get(BEAM_TARGET).orElse((BlockPos)null);
   }

   public void setShowBottom(boolean showBottom) {
      this.getEntityData().set(SHOW_BOTTOM, showBottom);
   }

   public boolean shouldShowBottom() {
      return this.getEntityData().get(SHOW_BOTTOM);
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double distance) {
      return super.shouldRenderAtSqrDistance(distance) || this.getBeamTarget() != null;
   }

   @Override
   public IPacket<?> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
