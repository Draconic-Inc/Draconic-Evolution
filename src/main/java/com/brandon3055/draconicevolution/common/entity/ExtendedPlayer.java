package com.brandon3055.draconicevolution.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Created by Brandon on 9/08/2014.
 */
public class ExtendedPlayer implements IExtendedEntityProperties {

    public static final String EXT_PROP_NAME = "DEPlayerProperties";

    private final EntityPlayer player;
    private int spawnCount;

    public ExtendedPlayer(EntityPlayer player) {
        this.player = player;
        this.spawnCount = 0;
    }

    /**
     * Used to register these extended properties for the player during EntityConstructing event This method is for
     * convenience only; it will make your code look nicer
     */
    public static void register(EntityPlayer player) {
        player.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer(player));
    }

    /**
     * Returns ExtendedPlayer properties for player This method is for convenience only; it will make your code look
     * nicer
     */
    public static ExtendedPlayer get(EntityPlayer player) {
        return (ExtendedPlayer) player.getExtendedProperties(EXT_PROP_NAME);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = new NBTTagCompound();
        properties.setInteger("SpawnCount", spawnCount);
        compound.setTag(EXT_PROP_NAME, properties);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
        spawnCount = properties.getInteger("SpawnCount");
    }

    @Override
    public void init(Entity entity, World world) {}

    public int getSpawnCount() {
        return spawnCount;
    }

    public void setSpawnCount(int count) {
        spawnCount = count;
    }
}
