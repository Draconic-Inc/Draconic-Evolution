package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

import javax.annotation.Nullable;
import java.util.Random;

@Deprecated //Old vanilla phase
public class LandingPhase extends Phase {
   private Vector3d targetLocation;

   public LandingPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
   }

   @Override
   public void clientTick() {
      Vector3d vector3d = this.guardian.getHeadLookVec(1.0F).normalize();
      vector3d.rotateYaw((-(float)Math.PI / 4F));
      double d0 = this.guardian.dragonPartHead.getPosX();
      double d1 = this.guardian.dragonPartHead.getPosYHeight(0.5D);
      double d2 = this.guardian.dragonPartHead.getPosZ();

      for(int i = 0; i < 8; ++i) {
         Random random = this.guardian.getRNG();
         double d3 = d0 + random.nextGaussian() / 2.0D;
         double d4 = d1 + random.nextGaussian() / 2.0D;
         double d5 = d2 + random.nextGaussian() / 2.0D;
         Vector3d vector3d1 = this.guardian.getMotion();
         this.guardian.world.addParticle(ParticleTypes.DRAGON_BREATH, d3, d4, d5, -vector3d.x * (double)0.08F + vector3d1.x, -vector3d.y * (double)0.3F + vector3d1.y, -vector3d.z * (double)0.08F + vector3d1.z);
         vector3d.rotateYaw(0.19634955F);
      }

   }

   @Override
   public void serverTick() {
      if (this.targetLocation == null) {
         this.targetLocation = Vector3d.copyCenteredHorizontally(this.guardian.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION));
      }

      if (this.targetLocation.squareDistanceTo(this.guardian.getPosX(), this.guardian.getPosY(), this.guardian.getPosZ()) < 1.0D) {
         this.guardian.getPhaseManager().getPhase(PhaseType.SITTING_FLAMING).resetFlameCount();
         this.guardian.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
      }

   }

   @Override
   public float getMaxRiseOrFall() {
      return 1.5F;
   }

   @Override
   public float getYawFactor() {
      float f = MathHelper.sqrt(Entity.horizontalMag(this.guardian.getMotion())) + 1.0F;
      float f1 = Math.min(f, 40.0F);
      return f1 / f;
   }

   @Override
   public void initPhase() {
      this.targetLocation = null;
   }

   @Override
   @Nullable
   public Vector3d getTargetLocation() {
      return this.targetLocation;
   }

   @Override
   public PhaseType<LandingPhase> getType() {
      return PhaseType.LANDING;
   }
}
