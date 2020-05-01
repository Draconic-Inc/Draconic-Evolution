package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileStabilizedSpawner;
import com.brandon3055.draconicevolution.items.MobSoul;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class StabilizedSpawner extends BlockBCore {

    public StabilizedSpawner(Properties properties) {
        super(properties);
        setMobResistant(true);
    }

    @Override
    public boolean isBlockFullCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileStabilizedSpawner();
    }

//    @OnlyIn(Dist.CLIENT)
//    public BlockRenderLayer getBlockLayer() {
//        return BlockRenderLayer.CUTOUT;
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void registerRenderer(Feature feature) {
//        ClientRegistry.bindTileEntitySpecialRenderer(TileStabilizedSpawner.class, new RenderTileStabilizedSpawner());
//
//        ModelResourceLocation modelLocation = new ModelResourceLocation(DraconicEvolution.MOD_PREFIX + feature.getName() + "#inventory");
//        ModelLoader.registerItemVariants(Item.getItemFromBlock(this), modelLocation);
//        IBakedModel bakedModel = new RenderItemStabilizedSpawner(iBakedModels -> iBakedModels.getObject(new ModelResourceLocation(DraconicEvolution.MOD_PREFIX + feature.getName())));
//        ModelRegistryHelper.register(modelLocation, bakedModel);
//        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), (ItemStack stack) -> modelLocation);
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }


    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    public void setStackData(ItemStack stack, String entityString, TileStabilizedSpawner.SpawnerTier tier) {
        setStackDataTier(stack, tier);
        setStackDataEntity(stack, entityString);
    }

    public void setStackDataTier(ItemStack stack, TileStabilizedSpawner.SpawnerTier tier) {
        CompoundNBT managedData = stack.getOrCreateChildTag(BlockBCore.BC_TILE_DATA_TAG).getCompound(BlockBCore.BC_MANAGED_DATA_FLAG);
        managedData.putByte("spawner_tier", (byte) tier.ordinal());
        stack.getOrCreateChildTag(BlockBCore.BC_TILE_DATA_TAG).put(BlockBCore.BC_MANAGED_DATA_FLAG, managedData);
    }

    //TODO
    public void setStackDataEntity(ItemStack stack, String entityString) {
        if (entityString != null) {
            ItemStack soul = new ItemStack(DEContent.mob_soul);
            DEContent.mob_soul.setEntity(MobSoul.getCachedRegName(entityString), soul);
            CompoundNBT managedData = stack.getOrCreateChildTag(BlockBCore.BC_TILE_DATA_TAG).getCompound(BlockBCore.BC_MANAGED_DATA_FLAG);
            stack.getOrCreateChildTag(BlockBCore.BC_TILE_DATA_TAG).getCompound(BlockBCore.BC_MANAGED_DATA_FLAG);
            managedData.put("mob_soul", soul.serializeNBT());
            stack.getOrCreateChildTag(BlockBCore.BC_TILE_DATA_TAG).put(BlockBCore.BC_MANAGED_DATA_FLAG, managedData);
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

//    @Override
//    public boolean isSolid(BlockState state) {
//        return false;
//    }
}
