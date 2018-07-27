package com.brandon3055.draconicevolution.blocks.machines;

import codechicken.lib.model.ModelRegistryHelper;
import com.brandon3055.brandonscore.blocks.BlockMobSafe;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner;
import com.brandon3055.draconicevolution.client.render.item.RenderItemStabilizedSpawner;
import com.brandon3055.draconicevolution.client.render.tile.RenderTileStabilizedSpawner;
import com.brandon3055.draconicevolution.items.MobSoul;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class StabilizedSpawner extends BlockMobSafe implements ITileEntityProvider, IRenderOverride {

    public StabilizedSpawner() {
        super(Material.ROCK);
    }

    @Override
    public boolean uberIsBlockFullCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStabilizedSpawner();
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerRenderer(Feature feature) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileStabilizedSpawner.class, new RenderTileStabilizedSpawner());

        ModelResourceLocation modelLocation = new ModelResourceLocation(DraconicEvolution.MOD_PREFIX + feature.getName() + "#inventory");
        ModelLoader.registerItemVariants(Item.getItemFromBlock(this), modelLocation);
        IBakedModel bakedModel = new RenderItemStabilizedSpawner(iBakedModels -> iBakedModels.getObject(new ModelResourceLocation(DraconicEvolution.MOD_PREFIX + feature.getName())));
        ModelRegistryHelper.register(modelLocation, bakedModel);
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), (ItemStack stack) -> modelLocation);
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
    }

    public void setStackData(ItemStack stack, String entityString, TileStabilizedSpawner.SpawnerTier tier) {
        setStackDataTier(stack, tier);
        setStackDataEntity(stack, entityString);
    }

    public void setStackDataTier(ItemStack stack, TileStabilizedSpawner.SpawnerTier tier) {
        NBTTagCompound managedData = stack.getOrCreateSubCompound("BCTileData").getCompoundTag("BCManagedData");
        managedData.setByte("spawnerTier", (byte) tier.ordinal());
        stack.getOrCreateSubCompound("BCTileData").setTag("BCManagedData", managedData);
    }

    public void setStackDataEntity(ItemStack stack, String entityString) {
        if (entityString != null) {
            ItemStack soul = new ItemStack(DEFeatures.mobSoul);
            DEFeatures.mobSoul.setEntity(MobSoul.getCachedRegName(entityString), soul);
            NBTTagCompound managedData = stack.getOrCreateSubCompound("BCTileData").getCompoundTag("BCManagedData");
            stack.getOrCreateSubCompound("BCTileData").getCompoundTag("BCManagedData");
            managedData.setTag("mobSoul", soul.serializeNBT());
            stack.getOrCreateSubCompound("BCTileData").setTag("BCManagedData", managedData);
        }
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }
}
