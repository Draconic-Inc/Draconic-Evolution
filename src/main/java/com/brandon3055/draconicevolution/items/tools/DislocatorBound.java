package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler.LinkData;
import com.brandon3055.draconicevolution.handlers.DESounds;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class DislocatorBound extends Dislocator /*implements IRenderOverride*/ {
    public DislocatorBound(Properties properties) {
        super(properties);
    }


//    public DislocatorBound() {
//        this.setMaxStackSize(1);
//        this.setNoRepair();
//        this.setMaxDamage(0);
//        this.setHasSubtypes(true);
//        this.addName(0, "bound");
//        this.addName(1, "p2p");
//        this.addName(2, "player");
//    }

//    @Override
//    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
//        if (this.isInCreativeTab(tab)) {
//            items.add(new ItemStack(this, 1, 1));
//            items.add(new ItemStack(this, 1, 2));
//        }
//    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (entity.ticksExisted % 20 == 0) {
            if (!world.isRemote && isValid(stack) && !isPlayer(stack) && entity instanceof PlayerEntity) {
                DislocatorLinkHandler.updateLink(world, stack, (PlayerEntity) entity);
            }
        }
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() % 20 == 0) {
            if (!entity.world.isRemote && isValid(stack) && !isPlayer(stack)) {
                DislocatorLinkHandler.updateLink(entity.world, stack, entity.getPosition(), entity.world.getDimensionKey());
            }
        }
        return false;
    }

    //region Item Interact

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (player.world.isRemote) {
            return true;
        }
        TeleportLocation location = getLocation(stack, player.world);
        if (location == null) {
            if (isPlayer(stack)) {
                player.sendMessage(new TranslationTextComponent("info.de.bound_dislocator.cant_find_player").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            }
            else {
                player.sendMessage(new TranslationTextComponent("info.de.bound_dislocator.cant_find_target").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            }

            return true;
        }

        if (!entity.isNonBoss() || !(entity instanceof LivingEntity)) {
            return true;
        }

        BCoreNetwork.sendSound(player.world, player.getPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false);

        location.setPitch(player.rotationPitch);
        location.setYaw(player.rotationYaw);
        notifyArriving(stack, player.world, entity);
        location.teleport(entity);

        BCoreNetwork.sendSound(player.world, player.getPosition(), DESounds.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false);

        player.sendMessage(new StringTextComponent(new TranslationTextComponent("msg.teleporterSentMob.txt").getString() + " x:" + (int) location.getXCoord() + " y:" + (int) location.getYCoord() + " z:" + (int) location.getZCoord() + " Dimension: " + location.getDimensionName()), Util.DUMMY_UUID);

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.world.isRemote) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
//        if (stack.getMetadata() == 1 && !world.isRemote) {
//            ItemStack boundA = new ItemStack(DEFeatures.dislocatorBound, 1);
//            ItemStack boundB = new ItemStack(DEFeatures.dislocatorBound, 1);
//            String uuid = UUID.randomUUID().toString();
//            ItemNBTHelper.setString(boundA, "LinkKey", uuid);
//            ItemNBTHelper.setString(boundB, "LinkKey", uuid);
//            ItemNBTHelper.setByte(boundA, "Side", (byte) 0);
//            ItemNBTHelper.setByte(boundB, "Side", (byte) 1);
//            player.setHeldItem(hand, ItemStack.EMPTY);
//            InventoryUtils.givePlayerStack(player, boundA);
//            InventoryUtils.givePlayerStack(player, boundB);
//            return new ActionResult<>(ActionResultType.SUCCESS, stack);
//        }
//        else if (stack.getMetadata() == 2 && !world.isRemote) {
//            stack.setItemDamage(0);
//            ItemNBTHelper.setString(stack, "PlayerLink", player.getGameProfile().getId().toString());
//            ItemNBTHelper.setString(stack, "PlayerName", player.getName());
//            return new ActionResult<>(ActionResultType.SUCCESS, stack);
//        }
//        else {
//            TeleportLocation location = getLocation(stack, world);
//            if (location == null) {
//                if (isPlayer(stack)) {
//                    player.sendMessage(new TranslationTextComponent("info.de.bound_dislocator.cant_find_player").setStyle(new Style().setColor(TextFormatting.RED)));
//                }
//                else {
//                    player.sendMessage(new TranslationTextComponent("info.de.bound_dislocator.cant_find_target").setStyle(new Style().setColor(TextFormatting.RED)));
//                }
//                return new ActionResult<>(ActionResultType.PASS, stack);
//            }
//
//            DESoundHandler.playSoundFromServer(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
//
//            location.setPitch(player.rotationPitch);
//            location.setYaw(player.rotationYaw);
//            notifyArriving(stack, player.world, player);
//            location.teleport(player);
//
//            DESoundHandler.playSoundFromServer(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
//
//            return new ActionResult<>(ActionResultType.PASS, stack);
//        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    public boolean isValid(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        return stack.getItem() == DEContent.dislocator_p2p && compound != null && (compound.contains("PlayerLink") || (compound.contains("LinkKey") && compound.contains("Side")));
    }

    public boolean isPlayer(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("PlayerLink");
    }

    public String getLinkID(ItemStack stack) {
        return getKey(stack) + "|" + getSide(stack);
    }

    public String getPlayerID(ItemStack stack) {
        return ItemNBTHelper.getString(stack, "PlayerLink", "");
    }

    public String getLinkToID(ItemStack stack) {
        return getKey(stack) + "|" + (getSide(stack) == 0 ? 1 : 0);
    }

    public String getKey(ItemStack stack) {
        return ItemNBTHelper.getString(stack, "LinkKey", "");
    }

    public int getSide(ItemStack stack) {
        return ItemNBTHelper.getByte(stack, "Side", (byte) -1);
    }

    //endregion

    //region Teleporter

    @Override
    public TeleportLocation getLocation(ItemStack stack, World world) {
        if (world instanceof ServerWorld) {
            LinkData data = DislocatorLinkHandler.getLink(stack, (ServerWorld)world);
            if (isPlayer(stack)) {
                MinecraftServer server = world.getServer();
                if (server == null) return null;
                PlayerEntity player = server.getPlayerList().getPlayerByUUID(UUID.fromString(getPlayerID(stack)));
                if (player == null) return null;
                return new TeleportLocation(player.getPosX(), player.getPosY() + 0.2, player.getPosZ(), player.world.getDimensionKey());
            }
            Vec3D pos = DislocatorLinkHandler.getLinkPos(world, stack);
            if (data != null && pos != null) {
                return new TeleportLocation(pos.x, pos.y, pos.z, data.dimension);
            }
        }
        return null;
    }

    public void notifyArriving(ItemStack stack, World world, Entity entity) {
        if (world instanceof ServerWorld) {
            LinkData data = DislocatorLinkHandler.getLink(stack, (ServerWorld) world);
            if (data != null && isValid(stack) && !isPlayer(stack)) {
                if (world.getServer() == null /*|| !DimensionManager.isDimensionRegistered(data.dimension)*/) {
                    return;
                }
                TileEntity tile = world.getServer().getWorld(data.dimension).getTileEntity(data.pos);
                if (tile instanceof ITeleportEndPoint) {
                    ((ITeleportEndPoint) tile).entityArriving(entity);
                }
            }
        }
    }

    //endregion

    //region Misc

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
//        if (stack.getMetadata() == 0) {
//            if (!isValid(stack)) {
//                tooltip.add(new StringTextComponent("Error this item is not valid! (Item is missing required NBT data)").setStyle(new Style().setColor(TextFormatting.RED)));
//            }
//            else if (isPlayer(stack)) {
//                tooltip.add(new StringTextComponent(TextFormatting.BLUE + I18n.format("info.de.bound_dislocator.player_link") + ": " + ItemNBTHelper.getString(stack, "PlayerName", "Unknown Player")));
//            }
//            else {
//                if (ClientEventHandler.elapsedTicks % 20 == 0) {
////                    DraconicEvolution.network.sendToServer(new PacketDislocatorUpdateRequest(getLinkToID(stack)));
//                }
//                tooltip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("info.de.bound_dislocator.key") + ": " + ItemNBTHelper.getString(stack, "LinkKey", "Error... No Key") + "|" + ItemNBTHelper.getByte(stack, "Side", (byte) -1)));
//                LinkData link = DislocatorLinkHandler.getLink(stack, world);
//                if (link == null) {
//                    tooltip.add(new StringTextComponent(TextFormatting.BLUE + I18n.format("info.de.bound_dislocator.bound_to") + ": " + I18n.format("info.de.bound_dislocator.unknown_link")));
//                }
//                else if (link.isPlayer) {
//                    tooltip.add(new StringTextComponent(TextFormatting.BLUE + I18n.format("info.de.bound_dislocator.in_player_inventory")));
//                }
//                else {
//                    tooltip.add(new StringTextComponent(TextFormatting.BLUE + I18n.format("info.de.bound_dislocator.bound_to") + ": " + String.format("[x:%s, y:%s, z:%s, dim:%s]", link.pos.getX(), link.pos.getY(), link.pos.getZ(), link.dimension)));
//                }
//            }
//        }
//        else if (stack.getMetadata() == 1) {
//            tooltip.add(TextFormatting.GREEN + I18n.format("info.de.bound_dislocator.click_to_link"));
//        }
//        else if (stack.getMetadata() == 2) {
//            tooltip.add(TextFormatting.GREEN + I18n.format("info.de.bound_dislocator.click_to_link_self"));
//        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }

    private ModelResourceLocation modelLocation = new ModelResourceLocation("draconicevolution:dislocator_bound");

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ModelLoader.setCustomMeshDefinition(this, stack -> modelLocation);
//    }
//
//    @Override
//    public boolean registerNormal(Feature feature) {
//        return true;
//    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    //endregion
}
