package com.brandon3055.draconicevolution.items;

import codechicken.lib.model.ModelRegistryHelper;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.client.render.item.RenderItemEnderEnergyManipulator;
import com.brandon3055.draconicevolution.entity.EntityEnderEnergyManipulator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenEndPodium;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by brandon3055 on 9/05/2017.
 */
public class EnderEnergyManipulator extends ItemBCore implements ICustomRender {

    public EnderEnergyManipulator() {
        this.setMaxStackSize(8);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        List<EntityEnderEnergyManipulator> list = world.getEntities(EntityEnderEnergyManipulator.class, EntitySelectors.IS_ALIVE);
        BlockPos podiumPos = WorldGenEndPodium.END_PODIUM_LOCATION;
        if (world.provider.getDimension() == 1 && Utils.getDistanceAtoB(Vec3D.getCenter(pos), new Vec3D(podiumPos.getX(), pos.getY(), podiumPos.getZ())) <= 8 && state.getBlock() == Blocks.BEDROCK && list.isEmpty()) {
            if (!world.isRemote) {
                EntityEnderEnergyManipulator entity = new EntityEnderEnergyManipulator(world);
                entity.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.spawnEntityInWorld(entity);
            }

            InventoryUtils.consumeHeldItem(player, stack, hand);
            return EnumActionResult.SUCCESS;
        }

        if (!world.isRemote) {
            if (!list.isEmpty()) {
                player.addChatComponentMessage(new TextComponentTranslation("info.de.ender_energy_manipulator.running.msg"));
            }
            else {
                player.addChatComponentMessage(new TextComponentTranslation("info.de.ender_energy_manipulator.location.msg"));
            }
        }
        return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ModelRegistryHelper.registerItemRenderer(this, new RenderItemEnderEnergyManipulator());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("info.de.ender_energy_manipulator.info.txt"));
        tooltip.add(I18n.format("info.de.ender_energy_manipulator.info2.txt"));
    }
}
