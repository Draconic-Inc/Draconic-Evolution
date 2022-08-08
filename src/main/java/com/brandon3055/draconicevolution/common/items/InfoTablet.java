package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.GuiHandler;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Brandon on 29/09/2014.
 */
public class InfoTablet extends ItemDE {

    public InfoTablet() {
        this.setUnlocalizedName(Strings.infoTabletName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setMaxStackSize(1);
        ModItems.register(this);
        // GameRegistry.registerItem(this, Strings.infoTabletName);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "stone_tablet");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack p_77659_1_, World world, EntityPlayer player) {
        FMLNetworkHandler.openGui(
                player,
                DraconicEvolution.instance,
                GuiHandler.GUIID_MANUAL,
                world,
                (int) player.posX,
                (int) player.posY,
                (int) player.posZ);
        return super.onItemRightClick(p_77659_1_, world, player);
    }
}
