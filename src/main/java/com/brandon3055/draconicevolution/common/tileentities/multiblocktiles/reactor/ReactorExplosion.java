package com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor;

import com.brandon3055.brandonscore.common.handlers.IProcess;
import com.brandon3055.brandonscore.common.handlers.ProcessHandler;
import com.brandon3055.brandonscore.common.utills.Utills;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by brandon3055 on 12/8/2015.
 */
public class ReactorExplosion implements IProcess {

	public static DamageSource fusionExplosion = new DamageSource("damage.de.fusionExplode").setExplosion().setDamageBypassesArmor().setDamageIsAbsolute().setDamageAllowedInCreativeMode();

	private World worldObj;
	private int xCoord;
	private int yCoord;
	private int zCoord;
	private float power;
	private Random random = new Random();

	private double expansion = 0;

	public ReactorExplosion(World world, int x, int y, int z, float power){
		this.worldObj = world;
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
		this.power = power;
		isDead = world.isRemote;
	}

	@Override
	public void updateProcess() {

		int OD = (int) expansion;
		int ID = OD - 1;
		int size = (int) expansion;

		for (int x = xCoord - size; x < xCoord + size; x++){
			for (int z = zCoord - size; z < zCoord + size; z++){
				double dist = Utills.getDistanceAtoB(x, z, xCoord, zCoord);
				if (dist < OD && dist >= ID)
				{
					float tracePower = power - (float)(expansion/10D);
					tracePower *= 1F + ((random.nextFloat() - 0.5F) * 0.2);
					ProcessHandler.addProcess(new ReactorExplosionTrace(worldObj, x, yCoord, z, tracePower, random));
				}
			}
		}

		isDead = expansion >= power * 10;
		expansion += 1;
	}

	private boolean isDead = false;

	@Override
	public boolean isDead() {
		return isDead;
	}
}
