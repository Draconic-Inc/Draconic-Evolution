package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class PhaseType<T extends IPhase> {
   private static PhaseType<?>[] phases = new PhaseType[0];
   public static final PhaseType<StartPhase> START = create(StartPhase.class, "HoldingPattern");
   public static final PhaseType<CoverFirePhase> COVER_FIRE = create(CoverFirePhase.class, "CoverFire");
   public static final PhaseType<LandingApproachPhase> LANDING_APPROACH = create(LandingApproachPhase.class, "LandingApproach");
   public static final PhaseType<LandingPhase> LANDING = create(LandingPhase.class, "Landing");
   public static final PhaseType<TakeoffPhase> TAKEOFF = create(TakeoffPhase.class, "Takeoff");
   public static final PhaseType<FlamingSittingPhase> SITTING_FLAMING = create(FlamingSittingPhase.class, "SittingFlaming");
   public static final PhaseType<ScanningSittingPhase> SITTING_SCANNING = create(ScanningSittingPhase.class, "SittingScanning");
   public static final PhaseType<AttackingSittingPhase> SITTING_ATTACKING = create(AttackingSittingPhase.class, "SittingAttacking");
   public static final PhaseType<ChargingPlayerPhase> CHARGE_PLAYER = create(ChargingPlayerPhase.class, "ChargingPlayer");
   public static final PhaseType<BombardPlayerPhase> BOMBARD_PLAYER = create(BombardPlayerPhase.class, "BombardPlayer");
   public static final PhaseType<DyingPhase> DYING = create(DyingPhase.class, "Dying");
   public static final PhaseType<HoverPhase> HOVER = create(HoverPhase.class, "Hover");
   private final Class<? extends IPhase> clazz;
   private final int id;
   private final String name;

   private PhaseType(int idIn, Class<? extends IPhase> clazzIn, String nameIn) {
      this.id = idIn;
      this.clazz = clazzIn;
      this.name = nameIn;
   }

   public IPhase createPhase(DraconicGuardianEntity guardisn) {
      try {
         Constructor<? extends IPhase> constructor = this.getConstructor();
         return constructor.newInstance(guardisn);
      } catch (Exception exception) {
         throw new Error(exception);
      }
   }

   protected Constructor<? extends IPhase> getConstructor() throws NoSuchMethodException {
      return this.clazz.getConstructor(DraconicGuardianEntity.class);
   }

   public int getId() {
      return this.id;
   }

   public String toString() {
      return this.name + " (#" + this.id + ")";
   }

   public static PhaseType<?> getById(int idIn) {
      return idIn >= 0 && idIn < phases.length ? phases[idIn] : START;
   }

   public static int getTotalPhases() {
      return phases.length;
   }

   private static <T extends IPhase> PhaseType<T> create(Class<T> phaseIn, String nameIn) {
      PhaseType<T> phasetype = new PhaseType<>(phases.length, phaseIn, nameIn);
      phases = Arrays.copyOf(phases, phases.length + 1);
      phases[phasetype.getId()] = phasetype;
      return phasetype;
   }
}
