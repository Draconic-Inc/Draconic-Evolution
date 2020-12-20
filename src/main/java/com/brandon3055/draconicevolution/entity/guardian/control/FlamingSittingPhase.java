package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

@Deprecated //Old vanilla phase
public class FlamingSittingPhase extends SittingPhase {
   private int flameTicks;
   private int flameCount;
   private AreaEffectCloudEntity areaEffectCloud;

   public FlamingSittingPhase(DraconicGuardianEntity guardisn) {
      super(guardisn);
   }

   public void clientTick() {
      ++this.flameTicks;
      if (this.flameTicks % 2 == 0 && this.flameTicks < 10) {
         Vector3d vector3d = this.guardian.getHeadLookVec(1.0F).normalize();
         vector3d.rotateYaw((-(float)Math.PI / 4F));
         double d0 = this.guardian.dragonPartHead.getPosX();
         double d1 = this.guardian.dragonPartHead.getPosYHeight(0.5D);
         double d2 = this.guardian.dragonPartHead.getPosZ();

         for(int i = 0; i < 8; ++i) {
            double d3 = d0 + this.guardian.getRNG().nextGaussian() / 2.0D;
            double d4 = d1 + this.guardian.getRNG().nextGaussian() / 2.0D;
            double d5 = d2 + this.guardian.getRNG().nextGaussian() / 2.0D;

            for(int j = 0; j < 6; ++j) {
               this.guardian.world.addParticle(ParticleTypes.DRAGON_BREATH, d3, d4, d5, -vector3d.x * (double)0.08F * (double)j, -vector3d.y * (double)0.6F, -vector3d.z * (double)0.08F * (double)j);
            }

            vector3d.rotateYaw(0.19634955F);
         }
      }

   }

   public void serverTick() {
      ++this.flameTicks;
      if (this.flameTicks >= 200) {
         if (this.flameCount >= 4) {
            this.guardian.getPhaseManager().setPhase(PhaseType.TAKEOFF);
         } else {
            this.guardian.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
         }
      } else if (this.flameTicks == 10) {
         Vector3d vector3d = (new Vector3d(this.guardian.dragonPartHead.getPosX() - this.guardian.getPosX(), 0.0D, this.guardian.dragonPartHead.getPosZ() - this.guardian.getPosZ())).normalize();
         float f = 5.0F;
         double d0 = this.guardian.dragonPartHead.getPosX() + vector3d.x * 5.0D / 2.0D;
         double d1 = this.guardian.dragonPartHead.getPosZ() + vector3d.z * 5.0D / 2.0D;
         double d2 = this.guardian.dragonPartHead.getPosYHeight(0.5D);
         double d3 = d2;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(d0, d2, d1);

         while(this.guardian.world.isAirBlock(blockpos$mutable)) {
            --d3;
            if (d3 < 0.0D) {
               d3 = d2;
               break;
            }

            blockpos$mutable.setPos(d0, d3, d1);
         }

         d3 = (double)(MathHelper.floor(d3) + 1);
         this.areaEffectCloud = new AreaEffectCloudEntity(this.guardian.world, d0, d3, d1);
         this.areaEffectCloud.setOwner(this.guardian);
         this.areaEffectCloud.setRadius(5.0F);
         this.areaEffectCloud.setDuration(200);
         this.areaEffectCloud.setParticleData(ParticleTypes.DRAGON_BREATH);
         this.areaEffectCloud.addEffect(new EffectInstance(Effects.INSTANT_DAMAGE));
         this.guardian.world.addEntity(this.areaEffectCloud);
      }

   }

   public void initPhase() {
      this.flameTicks = 0;
      ++this.flameCount;
   }

   public void removeAreaEffect() {
      if (this.areaEffectCloud != null) {
         this.areaEffectCloud.remove();
         this.areaEffectCloud = null;
      }

   }

   public PhaseType<FlamingSittingPhase> getType() {
      return PhaseType.SITTING_FLAMING;
   }

   public void resetFlameCount() {
      this.flameCount = 0;
   }
}
