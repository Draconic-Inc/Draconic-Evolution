package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.ITeleportEndPoint;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler;
import com.brandon3055.draconicevolution.handlers.DislocatorLinkHandler.LinkData;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import com.brandon3055.draconicevolution.network.PacketDislocatorUpdateRequest;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.UUID;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class DislocatorBound extends Dislocator implements IRenderOverride {

    public DislocatorBound() {
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.addName(0, "bound");
        this.addName(1, "p2p");
        this.addName(2, "player");
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 1));
            items.add(new ItemStack(this, 1, 2));
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (entity.ticksExisted % 20 == 0) {
            if (!world.isRemote && isValid(stack) && !isPlayer(stack) && entity instanceof EntityPlayer) {
                DislocatorLinkHandler.updateLink(world, stack, (EntityPlayer) entity);
            }
        }
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (entityItem.age % 20 == 0) {
            ItemStack stack = entityItem.getItem();
            if (!entityItem.world.isRemote && isValid(stack) && !isPlayer(stack)) {
                DislocatorLinkHandler.updateLink(entityItem.world, stack, new BlockPos(entityItem), entityItem.dimension);
            }
        }
        return super.onEntityItemUpdate(entityItem);
    }

    //region Item Interact

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (player.world.isRemote) {
            return true;
        }
        TeleportLocation location = getLocation(stack, player.world);
        if (location == null) {
            if (isPlayer(stack)) {
                player.sendMessage(new TextComponentTranslation("info.de.bound_dislocator.cant_find_player").setStyle(new Style().setColor(TextFormatting.RED)));
            }
            else {
                player.sendMessage(new TextComponentTranslation("info.de.bound_dislocator.cant_find_target").setStyle(new Style().setColor(TextFormatting.RED)));
            }

            return true;
        }

        if (!entity.isNonBoss() || !(entity instanceof EntityLiving)) {
            return true;
        }

        DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);

        location.setPitch(player.rotationPitch);
        location.setYaw(player.rotationYaw);
        notifyArriving(stack, player.world, entity);
        location.teleport(entity);

        DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);

        player.sendMessage(new TextComponentString(new TextComponentTranslation("msg.teleporterSentMob.txt").getFormattedText() + " x:" + (int) location.getXCoord() + " y:" + (int) location.getYCoord() + " z:" + (int) location.getZCoord() + " Dimension: " + location.getDimensionName()));

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.world.isRemote) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (stack.getMetadata() == 1 && !world.isRemote) {
            ItemStack boundA = new ItemStack(DEFeatures.dislocatorBound, 1);
            ItemStack boundB = new ItemStack(DEFeatures.dislocatorBound, 1);
            String uuid = UUID.randomUUID().toString();
            ItemNBTHelper.setString(boundA, "LinkKey", uuid);
            ItemNBTHelper.setString(boundB, "LinkKey", uuid);
            ItemNBTHelper.setByte(boundA, "Side", (byte) 0);
            ItemNBTHelper.setByte(boundB, "Side", (byte) 1);
            player.setHeldItem(hand, ItemStack.EMPTY);
            InventoryUtils.givePlayerStack(player, boundA);
            InventoryUtils.givePlayerStack(player, boundB);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        else if (stack.getMetadata() == 2 && !world.isRemote) {
            stack.setItemDamage(0);
            ItemNBTHelper.setString(stack, "PlayerLink", player.getGameProfile().getId().toString());
            ItemNBTHelper.setString(stack, "PlayerName", player.getName());
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        else {
            TeleportLocation location = getLocation(stack, world);
            if (location == null) {
                if (isPlayer(stack)) {
                    player.sendMessage(new TextComponentTranslation("info.de.bound_dislocator.cant_find_player").setStyle(new Style().setColor(TextFormatting.RED)));
                }
                else {
                    player.sendMessage(new TextComponentTranslation("info.de.bound_dislocator.cant_find_target").setStyle(new Style().setColor(TextFormatting.RED)));
                }
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }

            DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);

            location.setPitch(player.rotationPitch);
            location.setYaw(player.rotationYaw);
            notifyArriving(stack, player.world, player);
            location.teleport(player);

            DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);

            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
    }

    public boolean isValid(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        return stack.getItem() == DEFeatures.dislocatorBound && compound != null && stack.getMetadata() == 0 && (compound.hasKey("PlayerLink") || (compound.hasKey("LinkKey") && compound.hasKey("Side")));
    }

    public boolean isPlayer(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("PlayerLink");
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
        LinkData data = DislocatorLinkHandler.getLink(stack, world);
        if (isPlayer(stack)) {
            MinecraftServer server = world.getMinecraftServer();
            if (server == null) return null;
            EntityPlayer player = server.getPlayerList().getPlayerByUUID(UUID.fromString(getPlayerID(stack)));
            if (player == null) return null;
            return new TeleportLocation(player.posX, player.posY + 0.2, player.posZ, player.dimension);
        }
        Vec3D pos = DislocatorLinkHandler.getLinkPos(world, stack);
        if (data != null && pos != null) {
            return new TeleportLocation(pos.x, pos.y, pos.z, data.dimension);
        }
        return null;
    }

    public void notifyArriving(ItemStack stack, World world, Entity entity) {
        LinkData data = DislocatorLinkHandler.getLink(stack, world);
        if (data != null && isValid(stack) && !isPlayer(stack)) {
            if (world.getMinecraftServer() == null || !DimensionManager.isDimensionRegistered(data.dimension)) {
                return;
            }
            TileEntity tile = world.getMinecraftServer().getWorld(data.dimension).getTileEntity(data.pos);
            if (tile instanceof ITeleportEndPoint) {
                ((ITeleportEndPoint) tile).entityArriving(entity);
            }
        }
    }

    //endregion

    //region Misc

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
        if (stack.getMetadata() == 0) {
            if (!isValid(stack)) {
                tooltip.add(TextFormatting.RED + "Error this item is not valid! (Item is missing required NBT data)");
            }
            else if (isPlayer(stack)) {
                tooltip.add(TextFormatting.BLUE + I18n.format("info.de.bound_dislocator.player_link") + ": " + ItemNBTHelper.getString(stack, "PlayerName", "Unknown Player"));
            }
            else {
                if (ClientEventHandler.elapsedTicks % 20 == 0) {
                    DraconicEvolution.network.sendToServer(new PacketDislocatorUpdateRequest(getLinkToID(stack)));
                }
                tooltip.add(TextFormatting.GRAY + I18n.format("info.de.bound_dislocator.key") + ": " + ItemNBTHelper.getString(stack, "LinkKey", "Error... No Key") + "|" + ItemNBTHelper.getByte(stack, "Side", (byte) -1));
                LinkData link = DislocatorLinkHandler.getLink(stack, world);
                if (link == null) {
                    tooltip.add(TextFormatting.BLUE + I18n.format("info.de.bound_dislocator.bound_to") + ": " + I18n.format("info.de.bound_dislocator.unknown_link"));
                }
                else if (link.isPlayer) {
                    tooltip.add(TextFormatting.BLUE + I18n.format("info.de.bound_dislocator.in_player_inventory"));
                }
                else {
                    tooltip.add(TextFormatting.BLUE + I18n.format("info.de.bound_dislocator.bound_to") + ": " + String.format("[x:%s, y:%s, z:%s, dim:%s]", link.pos.getX(), link.pos.getY(), link.pos.getZ(), link.dimension));
                }
            }
        }
        else if (stack.getMetadata() == 1) {
            tooltip.add(TextFormatting.GREEN + I18n.format("info.de.bound_dislocator.click_to_link"));
        }
        else if (stack.getMetadata() == 2) {
            tooltip.add(TextFormatting.GREEN + I18n.format("info.de.bound_dislocator.click_to_link_self"));
        }
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

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ModelLoader.setCustomMeshDefinition(this, stack -> modelLocation);
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    //endregion
}
