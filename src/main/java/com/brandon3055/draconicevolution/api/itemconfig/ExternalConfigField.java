package com.brandon3055.draconicevolution.api.itemconfig;

import com.brandon3055.brandonscore.inventory.PlayerSlot;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 1/06/2016.
 * This can be used to create a config button that opens a separate gui.
 */
public class ExternalConfigField extends IntegerConfigField {

    private final Object mod;
    private final int guiID;
    private final String unloacalizedButtonText;

    public ExternalConfigField(String name, String description, Object mod, int guiID, String buttonText) {
        super(name, 0, 0, 1, description, EnumControlType.TOGGLE);
        this.mod = mod;
        this.guiID = guiID;
        this.unloacalizedButtonText = buttonText;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getReadableValue() {
        return I18n.format(unloacalizedButtonText);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {}

    @Override
    public void readFromNBT(NBTTagCompound compound) {}

    @Override
    public void handleButton(EnumButton button, int data, EntityPlayer player, PlayerSlot slot) {
        player.openGui(mod, guiID, player.world, slot.getSlotIndex(), slot.getCatIndex(), 0);
    }
}
