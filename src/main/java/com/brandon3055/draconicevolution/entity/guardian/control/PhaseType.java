package com.brandon3055.draconicevolution.entity.guardian.control;

import com.brandon3055.draconicevolution.entity.guardian.DraconicGuardianEntity;
import net.minecraft.util.WeightedRandom;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhaseType<T extends IPhase> {
   private static PhaseType<?>[] phases = new PhaseType[0];
   public static final PhaseType<StartPhase> START = create(StartPhase.class, "HoldingPattern");
   public static final PhaseType<CoverFirePhase> COVER_FIRE = create(CoverFirePhase.class, "CoverFire");
   public static final PhaseType<ApproachPositionPhase> APPROACH_POSITION = create(ApproachPositionPhase.class, "ApproachPos");
   public static final PhaseType<ChargingPlayerPhase> CHARGE_PLAYER = create(ChargingPlayerPhase.class, "ChargingPlayer");
   public static final PhaseType<BombardPlayerPhase> BOMBARD_PLAYER = create(BombardPlayerPhase.class, "BombardPlayer");
   public static final PhaseType<DyingPhase> DYING = create(DyingPhase.class, "Dying");
   public static final PhaseType<HoverPhase> HOVER = create(HoverPhase.class, "Hover");
   public static final PhaseType<LaserBeamPhase> LASER_BEAM = create(LaserBeamPhase.class, "LaserBeam");
   public static final PhaseType<ShockwavePhase> SHOCKWAVE = create(ShockwavePhase.class, "ShockWave");
   public static final PhaseType<GroundEffectPhase> GROUND_EFFECTS = create(GroundEffectPhase.class, "GroundEffects");
   public static final PhaseType<ArialBombardPhase> ARIAL_BOMBARD = create(ArialBombardPhase.class, "ArialBombard");

   public static List<WeightedPhase> NORMAL_WEIGHTED = new ArrayList<>();
   public static List<WeightedPhase> AGGRESSIVE_WEIGHTED = new ArrayList<>();
   static {
      initPhaseWeights();
   }

   public static void initPhaseWeights() {
      NORMAL_WEIGHTED.clear();
      NORMAL_WEIGHTED.add(new WeightedPhase(CHARGE_PLAYER,  100));   //Guardian "Melee" attack. Has to make contact with the player to "take a bite out of them"
      NORMAL_WEIGHTED.add(new WeightedPhase(BOMBARD_PLAYER, 200));   //Standard fireball attack
      NORMAL_WEIGHTED.add(new WeightedPhase(LASER_BEAM,     60));    //Self-explanatory
      NORMAL_WEIGHTED.add(new WeightedPhase(GROUND_EFFECTS, 50));    //This is the long charge attack with the withers and the stuff and things.
      NORMAL_WEIGHTED.add(new WeightedPhase(ARIAL_BOMBARD,  70));    //This is the short charge attack where it just fires volleys of fireballs at you.

      //Attacking the guardian increases an "aggression level" Once this level reaches maximum (usually after 5-10 minutes of fighting)
      //The guardian switches to these weights.

      AGGRESSIVE_WEIGHTED.clear();
      AGGRESSIVE_WEIGHTED.add(new WeightedPhase(BOMBARD_PLAYER,   50));
      AGGRESSIVE_WEIGHTED.add(new WeightedPhase(LASER_BEAM,       150));
      AGGRESSIVE_WEIGHTED.add(new WeightedPhase(GROUND_EFFECTS,   50));
      AGGRESSIVE_WEIGHTED.add(new WeightedPhase(ARIAL_BOMBARD,    150));
   }

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

   public static class WeightedPhase extends WeightedRandom.Item {
      public final PhaseType<?> phase;
      public WeightedPhase(PhaseType<?> phase, int weight) {
         super(weight);
         this.phase = phase;
      }
   }
}
