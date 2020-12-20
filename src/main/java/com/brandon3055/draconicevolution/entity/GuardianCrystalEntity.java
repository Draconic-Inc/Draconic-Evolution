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
   private static final DataParameter<Optional<BlockPos>> BEAM_TARGET = EntityDataManager.createKey(GuardianCrystalEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
   private static final DataParameter<Boolean> SHOW_BOTTOM = EntityDataManager.createKey(GuardianCrystalEntity.class, DataSerializers.BOOLEAN);
   private UUID managerId;
   public int innerRotation;

   public GuardianCrystalEntity(EntityType<?> type, World world) {
      super(type, world);
      this.preventEntitySpawning = true;
      this.innerRotation = this.rand.nextInt(100000);
   }

   public GuardianCrystalEntity(World worldIn, double x, double y, double z, UUID managerId) {
      this(DEContent.guardianCrystal, worldIn);
      this.managerId = managerId;
      this.setPosition(x, y, z);
   }

   public UUID getManagerId() {
      return managerId;
   }

   public void setManagerId(UUID managerId) {
      this.managerId = managerId;
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   @Override
   protected void registerData() {
      this.getDataManager().register(BEAM_TARGET, Optional.empty());
      this.getDataManager().register(SHOW_BOTTOM, true);
   }

   @Override
   public void tick() {
      ++this.innerRotation;
      if (this.world instanceof ServerWorld) {
         BlockPos blockpos = this.getPosition();
         if (((ServerWorld)this.world).func_241110_C_() != null && this.world.getBlockState(blockpos).isAir()) {
            this.world.setBlockState(blockpos, AbstractFireBlock.getFireForPlacement(this.world, blockpos));
         }
      }
   }

   @Override
   protected void writeAdditional(CompoundNBT compound) {
      if (this.getBeamTarget() != null) {
         compound.put("BeamTarget", NBTUtil.writeBlockPos(this.getBeamTarget()));
      }

      compound.putBoolean("ShowBottom", this.shouldShowBottom());
      compound.putUniqueId("manager_id", managerId);
   }

   @Override
   protected void readAdditional(CompoundNBT compound) {
      if (compound.contains("BeamTarget", 10)) {
         this.setBeamTarget(NBTUtil.readBlockPos(compound.getCompound("BeamTarget")));
      }

      if (compound.contains("ShowBottom", 1)) {
         this.setShowBottom(compound.getBoolean("ShowBottom"));
      }
      managerId = compound.getUniqueId("manager_id");
   }

   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (source.getTrueSource() instanceof EnderDragonEntity) {
         return false;
      } else {
         if (!this.removed && !this.world.isRemote) {
            this.remove();
            if (!source.isExplosion()) {
               this.world.createExplosion((Entity)null, this.getPosX(), this.getPosY(), this.getPosZ(), 10.0F, Explosion.Mode.DESTROY);
            }

            this.onCrystalDestroyed(source);
         }

         return true;
      }
   }

   @Override
   public void onKillCommand() {
      this.onCrystalDestroyed(DamageSource.GENERIC);
      super.onKillCommand();
   }

   private void onCrystalDestroyed(DamageSource source) {
      if (world instanceof ServerWorld && managerId != null) {
         WorldEntity worldEntity = WorldEntityHandler.getWorldEntity(world, managerId);
         if (worldEntity instanceof GuardianFightManager) {
            ((GuardianFightManager) worldEntity).onCrystalDestroyed(this, source);
         }
      }

   }

   public void setBeamTarget(@Nullable BlockPos beamTarget) {
      this.getDataManager().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return this.getDataManager().get(BEAM_TARGET).orElse((BlockPos)null);
   }

   public void setShowBottom(boolean showBottom) {
      this.getDataManager().set(SHOW_BOTTOM, showBottom);
   }

   public boolean shouldShowBottom() {
      return this.getDataManager().get(SHOW_BOTTOM);
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return super.isInRangeToRenderDist(distance) || this.getBeamTarget() != null;
   }

   @Override
   public IPacket<?> createSpawnPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
