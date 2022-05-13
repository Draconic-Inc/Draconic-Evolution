package com.brandon3055.draconicevolution.entity.guardian.control;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface IPhase {
   boolean getIsStationary();

   void clientTick();

   void serverTick();

   /**
    * A server side tick method that gets called for all initialized phases every tick regardless of which one is active.
    * */
   void globalServerTick();

   void onCrystalAttacked(GuardianCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable Player plyr, float damage, boolean destroyed);

   void initPhase();

   void removeAreaEffect();

   float getMaxRiseOrFall();

   float getYawFactor();

   PhaseType<? extends IPhase> getType();

   @Nullable
   Vec3 getTargetLocation();

   /**
    * @param source The source of the damage
    * @param damage The amount of damage
    * @param shield The current shield power
    * @param effective True if the damage will be applied to the shield/guardian or false if its blocked by the crystals.
    * @return the damage to be applied.
    */
   float onAttacked(DamageSource source, float damage, float shield, boolean effective);

   default double getGuardianSpeed() {
      return 1.5;
   }

   default boolean highVerticalAgility() {
      return false;
   }

   default void handlePacket(MCDataInput input, int func) {}

   void sendPacket(Consumer<MCDataOutput> callBack, int func);

   /**
    * @param player Optional player to target. Some phases will use this and some will not.
    */
   default void targetPlayer(Player player) {}

   default boolean isInvulnerable() {
      return false;
   }
}
