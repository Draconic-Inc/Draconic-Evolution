package com.brandon3055.draconicevolution.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 23/8/2015.
 */
public class EntityDragonProjectile extends Entity {

	public int type;
	public Entity target;
	public float power;
	public boolean isChaser;

	//public static final int FIREBALL = 0; 			/** Generic fireball a lot more powerful then ghast fireball */
	public static final int FIREBOMB = 1;			/** Large fireball with a trail and large fiery AOE */
	public static final int ENDER_PORT = 2;			/** Ender pearl which teleports the player away if it hits. */
	public static final int FIRE_CHASER = 3;		/** Fireball that chases the player it is fired at (Explodes on impact with blocks) */
	public static final int ENERGY_CHASER = 4;		/** Energy gall that chases the player. (Can pass through blocks) */
	public static final int CHAOS_CHASER = 5;		/** Chases player. On impact splits into mini chaos charges which lock on to other or the same player (can pass through blocks) */
	public static final int MINI_CHAOS_CHASER = 6;	/** ^ */
	public static final int IGNITION_CHARGE = 7;	/** Reignites Crystals */

	public EntityDragonProjectile(World world){
		this(world, 1, null, 5);
	}

	public EntityDragonProjectile(World world, int type, Entity target, float power) {
		super(world);
		this.type = type;
		this.target = target;
		this.power = power;
		this.isChaser = type == FIRE_CHASER || type == ENERGY_CHASER || type == CHAOS_CHASER || type == MINI_CHAOS_CHASER || type == IGNITION_CHARGE;
		this.setSize(0.5F, 0.5F);
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {

	}
}
