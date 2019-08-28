package com.brandon3055.draconicevolution.blocks;

import codechicken.lib.model.ModelRegistryHelper;
import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.GuiHandler;
import com.brandon3055.draconicevolution.blocks.tileentity.TileDraconiumChest;
import com.brandon3055.draconicevolution.client.render.item.RenderItemDraconiumChest;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileDraconiumChest;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class DraconiumChest extends BlockBCore implements ITileEntityProvider, IRenderOverride {

    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);

    public DraconiumChest() {
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDraconiumChest();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(DraconicEvolution.instance, GuiHandler.GUIID_DRACONIUM_CHEST, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;//super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    public static boolean isStackValid(ItemStack stack) {
        if (stack.getItem() == Item.getItemFromBlock(DEFeatures.draconiumChest)) {
            return false;
        }
        else if (!stack.isEmpty()) {
            String name = stack.getTranslationKey().toLowerCase(Locale.ENGLISH);
            if (name.contains("pouch") || name.contains("bag") || name.contains("strongbox") || name.contains("shulker_box")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing enumfacing = EnumFacing.HORIZONTALS[MathHelper.abs((MathHelper.floor((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % EnumFacing.HORIZONTALS.length)].getOpposite();
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileDraconiumChest) {
            ((TileDraconiumChest) tile).facing.value = enumfacing;
        }

        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileDraconiumChest) {
            ((TileDraconiumChest) tile).facing.value = ((TileDraconiumChest) tile).facing.value.rotateY();
            ((TileDraconiumChest) tile).ioCacheValid = false;
        }

        return true;
    }

    //region Rendering

    @Override
    @SideOnly(Side.CLIENT)
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileDraconiumChest.class, new RenderTileDraconiumChest());
//        ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderItemDraconiumChest());

        ModelResourceLocation modelLocation = new ModelResourceLocation(DraconicEvolution.MOD_PREFIX + feature.getName() + "#normal");
        ModelLoader.registerItemVariants(Item.getItemFromBlock(this), modelLocation);
        IBakedModel bakedModel = new RenderItemDraconiumChest();
        ModelRegistryHelper.register(modelLocation, bakedModel);
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), (ItemStack stack) -> modelLocation);

    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }


    //endregion

    @Override
    public boolean overrideShareTag() {
        return true;
    }

    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ChestColour", ItemNBTHelper.getInteger(stack, "ChestColour", 0x640096));
        return compound;
    }
}
