package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.brandonscore.utils.Teleporter;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.handlers.DESoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class DislocatorAdvanced extends Dislocator implements IHudDisplay {
    public DislocatorAdvanced(Properties properties) {
        super(properties);
    }

    //    public DislocatorAdvanced() {
//        this.setMaxStackSize(1);
//        this.setNoRepair();
//    }

    //region Item Interact

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        Teleporter.TeleportLocation location = getLocation(stack, player.world);
        if (location == null) {
            if (player.world.isRemote) {
                player.sendMessage(new TranslationTextComponent("msg.teleporterUnSet.txt"));
            }
            return true;
        }

        int fuel = ItemNBTHelper.getInteger(stack, "Fuel", 0);
        World world = player.world;

        if (!player.abilities.isCreativeMode && fuel <= 0) {
            if (world.isRemote) player.sendMessage(new TranslationTextComponent("msg.teleporterOutOfFuel.txt"));
            return true;
        }

        if (entity instanceof PlayerEntity) {
            if (entity.isShiftKeyDown()) {
                location.teleport(entity);
                if (!player.abilities.isCreativeMode && fuel > 0) {
                    ItemNBTHelper.setInteger(stack, "Fuel", fuel - 1);
                }
            }
            else {
                if (world.isRemote) {
                    player.sendMessage(new TranslationTextComponent("msg.teleporterPlayerConsent.txt"));
                }
            }
            return true;
        }

        if (!entity.isNonBoss() || !(entity instanceof LivingEntity)) {
            return true;
        }

        if (!entity.world.isRemote) {
            DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
        }

        location.teleport(entity);

        if (!entity.world.isRemote) {
            DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
        }

        if (player.world.isRemote) {
            player.sendMessage(new StringTextComponent(I18n.format("msg.teleporterSentMob.txt") + " x:" + (int) location.getXCoord() + " y:" + (int) location.getYCoord() + " z:" + (int) location.getZCoord() + " Dimension: " + location.getDimensionName()));
        }

        if (!player.abilities.isCreativeMode && fuel > 0) {
            ItemNBTHelper.setInteger(stack, "Fuel", fuel - 1);
        }

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        int fuel = ItemNBTHelper.getInteger(stack, "Fuel", 0);

        if (player.isShiftKeyDown()) {
            if (world.isRemote) {
//                FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_TELEPORTER, world, (int) player.posX, (int) player.posY, (int) player.posZ);
            }
        }
        else {

            if (getLocation(stack, world) == null) {
                if (world.isRemote) {
//                    FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_TELEPORTER, world, (int) player.posX, (int) player.posY, (int) player.posZ);
                }
                return new ActionResult<>(ActionResultType.PASS, stack);
            }

            if (!player.abilities.isCreativeMode && fuel <= 0) {
                if (world.isRemote) {
                    player.sendMessage(new TranslationTextComponent("msg.teleporterOutOfFuel.txt"));
                }
                return new ActionResult<>(ActionResultType.PASS, stack);
            }

            if (!player.abilities.isCreativeMode && fuel > 0) {
                ItemNBTHelper.setInteger(stack, "Fuel", fuel - 1);
            }

            if (!world.isRemote) {
                DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }
            getLocation(stack, world).teleport(player);
            if (!world.isRemote) {
                DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, DESoundHandler.portal, SoundCategory.PLAYERS, 0.1F, player.world.rand.nextFloat() * 0.1F + 0.9F, false, 32);
            }
        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    //endregion

    //region Teleporter

    @Override
    public Teleporter.TeleportLocation getLocation(ItemStack stack, World world) {
        short selected = ItemNBTHelper.getShort(stack, "Selection", (short) 0);
        int selrctionOffset = ItemNBTHelper.getInteger(stack, "SelectionOffset", 0);
        CompoundNBT compound = stack.getTag();
        if (compound == null) {
            return null;
        }
        ListNBT list = compound.getList("Locations", 10);
        Teleporter.TeleportLocation destination = new Teleporter.TeleportLocation();
        destination.readFromNBT(list.getCompound(selected + selrctionOffset));
        if (destination.getName().isEmpty()) {
            return null;
        }

        return destination;
    }

    //endregion

    //region Misc

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        short selected = ItemNBTHelper.getShort(stack, "Selection", (short) 0);
        int selrctionOffset = ItemNBTHelper.getInteger(stack, "SelectionOffset", 0);
        CompoundNBT compound = ItemNBTHelper.getCompound(stack);

        ListNBT list = compound.getList("Locations", 10);
        String selectedDest = list.getCompound(selected + selrctionOffset).getString("Name");

        tooltip.add(new StringTextComponent(TextFormatting.GOLD + "" + selectedDest));
        if (InfoHelper.holdShiftForDetails(tooltip)) {
            tooltip.add(new StringTextComponent(TextFormatting.WHITE + I18n.format("info.teleporterInfFuel.txt") + " " + ItemNBTHelper.getInteger(stack, "Fuel", 0)));
            tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + I18n.format("info.teleporterInfGUI.txt")));
            tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + I18n.format("info.teleporterInfScroll.txt")));
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

//    @Override
//    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
//        return new EntityPersistentItem(world, location, itemstack);
//    }

    @Override
    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayData) {
        Teleporter.TeleportLocation location = getLocation(stack, world);
        if (location != null) {
            displayData.add(location.getName());
        }
        displayData.add(I18n.format("info.teleporterInfFuel.txt") + " " + ItemNBTHelper.getInteger(stack, "Fuel", 0));
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    //endregion
}
