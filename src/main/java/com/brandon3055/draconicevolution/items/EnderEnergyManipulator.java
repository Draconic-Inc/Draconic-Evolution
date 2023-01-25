package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.Utils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 9/05/2017.
 */
public class EnderEnergyManipulator extends Item /*implements IRenderOverride*/ {

    private static final EntityTypeTest<Entity, Entity> ANY_TYPE = new EntityTypeTest<>() {
        public Entity tryCast(Entity p_175109_) {
            return p_175109_;
        }

        public Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    };

    public EnderEnergyManipulator(Properties properties) {
        super(properties);
//        this.setMaxStackSize(8);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        assert false; //Not Implemented
        Player player = context.getPlayer();
        Level cWorld = context.getLevel();
        InteractionHand hand = context.getHand();
        BlockPos pos = context.getClickedPos();

        if (cWorld instanceof ServerLevel) {
            ServerLevel world = (ServerLevel) cWorld;

            ItemStack stack = context.getItemInHand();
            BlockState state = world.getBlockState(pos);
            List<? extends Entity> list = world.getEntities(ANY_TYPE, Entity::isAlive);
            if (world.dimension() == Level.END && Utils.getDistance(Vec3D.getCenter(pos), new Vec3D(0, pos.getY(), 0)) <= 8 && state.getBlock() == Blocks.BEDROCK && list.isEmpty()) {
                if (!world.isClientSide) {
//                    EntityEnderEnergyManipulator entity = new EntityEnderEnergyManipulator(world);
//                    entity.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
//                    world.addEntity(entity);
                }

                InventoryUtils.consumeHeldItem(player, stack, hand);
                return InteractionResult.SUCCESS;
            }

            if (!world.isClientSide) {
                if (!list.isEmpty()) {
                    player.sendMessage(new TranslatableComponent("info.de.ender_energy_manipulator.running.msg"), Util.NIL_UUID);
                } else {
                    player.sendMessage(new TranslatableComponent("info.de.ender_energy_manipulator.location.msg"), Util.NIL_UUID);
                }
            }
        }
        return super.useOn(context);
    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ModelRegistryHelper.registerItemRenderer(this, new RenderItemEnderEnergyManipulator());
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return false;
//    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslatableComponent("info.de.ender_energy_manipulator.info.txt"));
        tooltip.add(new TranslatableComponent("info.de.ender_energy_manipulator.info2.txt"));
    }
}
