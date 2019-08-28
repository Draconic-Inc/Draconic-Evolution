package com.brandon3055.draconicevolution.blocks.tileentity;

import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyReceiver;
import com.brandon3055.brandonscore.blocks.TileEnergyInventoryBase;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.blocks.machines.Grinder;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;
import java.util.UUID;

public class TileGrinder extends TileEnergyInventoryBase implements IEnergyReceiver, ITickable {

    public final ManagedBool active = register("active", new ManagedBool(false)).syncViaTile().saveToTile().trigerUpdate().finish();
    public static FakePlayer fakePlayer;
    private AxisAlignedBB killBox;
    public boolean powered = false;

    public TileGrinder() {
        setInventorySize(1);
        setEnergySyncMode().syncViaContainer();
        setCapacityAndTransfer(500000, 32000, 0);
        setShouldRefreshOnBlockChange();
    }

    @Override
    public void update() {
        super.update();
        if (world.isRemote) {
            return;
        }

        active.value = getEnergyStored() > 0 && !powered;

        if (active.value) {
            updateGrinding();
        }

        if (getEnergyStored() < getMaxEnergyStored() && getStackInSlot(0) != null) {
            energyStorage.receiveEnergy(extractEnergyFromItem(getStackInSlot(0), energyStorage.receiveEnergy(32000, true), false), false);
        }
    }

    //region Killing Code

    private int coolDown = 0;

    private void updateGrinding() {
        //Create the fake player if it does not already exist
        if (fakePlayer == null) {
            fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.fromString("5b5689b9-e43d-4282-a42a-dc916f3616b7"), "[Draconic-Evolution]"));
        }

        //Create the kill box if it does not already exist
        if (killBox == null) {
            updateKillBox();
        }

        int eph = DEConfig.grinderEnergyPerHeart;

        //Only run if there is a reasonable energy buffer
        if (getEnergyStored() < eph * 50) {
            return;
        }

        //If the grinder is currently in cooldown mode tick down the timer and return.
        if (coolDown > 0) {
            coolDown--;
            return;
        }

        //Find an attack target
        EntityLivingBase target = findTarget();

        //If no target was found go back into cooldown mode for 5 seconds
        if (target == null) {
//            LogHelper.dev("Grinder: No targets found. Waiting 5 seconds...");
            coolDown = 100;
            return;
        }

        float health = target.getHealth();

        //Ensure teh minimum damage dealt is 5 hearts. This is to help prevent endless hurt loops due to mobs with armor.
        if (health < 5) {
            health = 5;
        }

        //Calculate energy cost
        int cost = (int) (health * (float) eph);
        boolean isConfirmedKill = false;

        //Restrict to the current energy stored if the cost is higher than the energy stored.
        if (cost > getEnergyStored()) {
            cost = getEnergyStored();
        }
        else {
            isConfirmedKill = true;
        }

        float damage = ((float) cost / (float) eph) * 1.1F;

        DamageSource source = DamageSource.causePlayerDamage(fakePlayer);

        //Dont mess around. If we know the mob should die lets just make it die!
        if (isConfirmedKill) {
            damage = Float.MAX_VALUE;
        }

        //Attack the mob and enter cooldown mode for 5 ticks is successful. Else cooldown for 3 ticks.
        if (target.attackEntityFrom(source, damage)) {
            LogHelper.dev("Grinder: Dealt " + damage + " damage to entity: " + target);
            energyStorage.modifyEnergyStored(-cost);
            coolDown = 2;
        }
        else {
            LogHelper.dev("Grinder: Failed to deal damage to entity: " + EntityList.getEntityString(target) + " Waiting 3 ticks...");
            LogHelper.dev("Grinder: Blacklisted Entities: " + DEConfig.grinderBlacklist);
            coolDown = 3;
        }

        //If a mob was killed reduce the despawn time of any xp dropped.
        if (coolDown == 2) {
            List<EntityXPOrb> xp = world.getEntitiesWithinAABB(EntityXPOrb.class, killBox.grow(4, 4, 4));
            for (EntityXPOrb orb : xp) {
                if (orb.xpOrbAge < 5400) {
                    orb.xpOrbAge = 5700;
                }
            }
        }
    }

    private EntityLivingBase findTarget() {
        if (killBox == null) {
            return null;
        }

        List<EntityLivingBase> entitiesInRange = world.getEntitiesWithinAABB(EntityLivingBase.class, killBox, grinderPredicate);

        EntityLivingBase found = null;
        for (EntityLivingBase livingBase : entitiesInRange) {
            if (livingBase.isEntityAlive() && !isBlackListed(livingBase)) {
                found = livingBase;
                LogHelper.dev("Grinder: Found next target: " + livingBase);
                if (found.getIsInvulnerable()) {
                    LogHelper.dev("Grinder: Target is invulnerable! searching for softer target...");
                    continue;
                }
                return found;
            }
        }

        return found;
    }

    private boolean isBlackListed(EntityLivingBase livingBase) {
        if (DEConfig.grinderBlacklist.isEmpty()) return false;
        ResourceLocation reg = EntityList.getKey(livingBase);
        return reg != null && DEConfig.grinderBlacklist.contains(reg.toString());
    }

    public void updateKillBox() {
        IBlockState state = world.getBlockState(pos);
        EnumFacing facing = state.getValue(Grinder.FACING);
        LogHelper.dev("Update Kill Box: " + facing);
        BlockPos pos1 = pos.add(-3, -3, -3);
        BlockPos pos2 = pos.add(4, 4, 4);
        pos1 = pos1.add(facing.getXOffset() * 4, 0, facing.getZOffset() * 4);
        pos2 = pos2.add(facing.getXOffset() * 4, 0, facing.getZOffset() * 4);
        killBox = new AxisAlignedBB(pos1, pos2);
    }

    public AxisAlignedBB getKillBoxForRender() {
        if (killBox == null) {
            updateKillBox();
        }
        return killBox;
    }


    //endregion

    @Override
    public void updateBlock() {
        super.updateBlock();
        updateKillBox();
    }

    //region IEnergyProvider
    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return super.receiveEnergy(from, maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return super.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return super.getMaxEnergyStored();
    }
    //endregion

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() instanceof IEnergyContainerItem;
    }

    private static GrinderPredicate grinderPredicate = new GrinderPredicate();

    private static class GrinderPredicate implements Predicate<EntityLivingBase> {

        @Override
        public boolean apply(EntityLivingBase input) {
            return !(input instanceof EntityPlayer) && input.isEntityAlive();
        }
    }
}
