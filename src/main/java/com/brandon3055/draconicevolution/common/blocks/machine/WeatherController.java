package com.brandon3055.draconicevolution.common.blocks.machine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.GuiHandler;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.blocks.BlockCustomDrop;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileWeatherController;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WeatherController extends BlockCustomDrop {

    public boolean blockState = true;
    public IIcon icon_top;
    public IIcon icon_bottom;
    public IIcon icon_side_rain_off;
    public IIcon icon_side_rain_on;
    public IIcon icon_side_thunder_off;
    public IIcon icon_side_thunder_on;
    public IIcon icon_side_sun_on;
    public IIcon icon_side_sun_off;

    public WeatherController() {
        super(Material.iron);
        this.setBlockName(Strings.blockWeatherControllerName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setStepSound(soundTypeStone);
        ModBlocks.register(this);
    }

    @Override
    public boolean hasTileEntity(final int meta) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister iconRegister) {
        icon_top = iconRegister.registerIcon(References.RESOURCESPREFIX + "weather_controller/weather_controller_top");
        icon_bottom = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
        icon_side_rain_off = iconRegister
                .registerIcon(References.RESOURCESPREFIX + "weather_controller/weather_controller_rain");
        icon_side_rain_on = iconRegister
                .registerIcon(References.RESOURCESPREFIX + "weather_controller/weatherController_rain_active");
        icon_side_thunder_off = iconRegister
                .registerIcon(References.RESOURCESPREFIX + "weather_controller/weatherController_thunderstorm");
        icon_side_thunder_on = iconRegister
                .registerIcon(References.RESOURCESPREFIX + "weather_controller/weatherController_thunderstorm_active");
        icon_side_sun_off = iconRegister
                .registerIcon(References.RESOURCESPREFIX + "weather_controller/weatherController_sun");
        icon_side_sun_on = iconRegister
                .registerIcon(References.RESOURCESPREFIX + "weather_controller/weatherController_sun_active");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        IIcon icon_side = null;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileWeatherController) {
            if (((TileWeatherController) tile).charges > 0) {
                if (((TileWeatherController) tile).mode == 0) icon_side = icon_side_sun_on;
                else if (((TileWeatherController) tile).mode == 1) icon_side = icon_side_rain_on;
                else icon_side = icon_side_thunder_on;
            } else {
                if (((TileWeatherController) tile).mode == 0) icon_side = icon_side_sun_off;
                else if (((TileWeatherController) tile).mode == 1) icon_side = icon_side_rain_off;
                else icon_side = icon_side_thunder_off;
            }
        }

        if (side > 1) return icon_side;
        else if (side == 0) return icon_bottom;
        else return icon_top;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {

        if (side > 1) return icon_side_sun_on;
        else if (side == 0) return icon_bottom;
        else return icon_top;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(final Item item, final CreativeTabs tab, final List par3list) {
        par3list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public boolean onBlockActivated(final World world, final int x, final int y, final int z,
            final EntityPlayer entityPlayer, final int par1, final float par2, final float par3, final float par4) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(
                    entityPlayer,
                    DraconicEvolution.instance,
                    GuiHandler.GUIID_WEATHER_CONTROLLER,
                    world,
                    x,
                    y,
                    z);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileWeatherController();
    }

    @Override
    protected boolean dropInventory() {
        return true;
    }

    @Override
    protected boolean hasCustomDropps() {
        return false;
    }

    @Override
    protected void getCustomTileEntityDrops(TileEntity te, List<ItemStack> droppes) {}

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileWeatherController tile = (TileWeatherController) world.getTileEntity(x, y, z);
        if (tile != null) {
            if (!tile.lastTickInput) {
                if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
                    tile.activate();
                }
            }
            tile.lastTickInput = world.isBlockIndirectlyGettingPowered(x, y, z);
        }
    }
}
