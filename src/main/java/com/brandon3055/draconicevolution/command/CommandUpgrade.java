package com.brandon3055.draconicevolution.command;

import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 29/06/2016.
 */
public class CommandUpgrade extends CommandBase {
    @Override
    public String getCommandName() {
        return "de_upgrade";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/de_upgrade";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0){
            sender.addChatMessage(new TextComponentString("/de_upgrade clear"));
            sender.addChatMessage(new TextComponentString("/de_upgrade list"));
            sender.addChatMessage(new TextComponentString("/de_upgrade <upgade> <level>"));
            return;
        }

        ItemStack stack = getCommandSenderAsPlayer(sender).getHeldItemMainhand();
        if (stack == null){
            throw new CommandException("Item Not Found");
        }

        if (!(stack.getItem() instanceof IUpgradableItem)){
            throw new CommandException("Item Not Upgradable");
        }

        if (args.length == 1 && args[0].equals("clear")){
            if (stack.hasTagCompound()){
                stack.getTagCompound().setTag(UpgradeHelper.UPGRADE_TAG, new NBTTagCompound());
            }
            return;
        }

        if (args.length == 1 && args[0].equals("list")){
            for (String s : ((IUpgradableItem) stack.getItem()).getValidUpgrades(stack)) {
                sender.addChatMessage(new TextComponentString(s));
            }
            return;
        }

        if (args.length == 2) {
            List<String> list = ((IUpgradableItem) stack.getItem()).getValidUpgrades(stack);
            if (!list.contains(args[0])){
                throw new CommandException("Upgrade is invalid or not applicable for this item");
            }

            try {
                int level = Integer.parseInt(args[1]);
                UpgradeHelper.setUpgradeLevel(stack, args[0], level);
                return;
            }
            catch (Exception e) {
                throw new CommandException("Expected Number... Found "+args[1]);
            }

        }

        sender.addChatMessage(new TextComponentString("/de_upgrade clear"));
        sender.addChatMessage(new TextComponentString("/de_upgrade list"));
        sender.addChatMessage(new TextComponentString("/de_upgrade <upgade> <level>"));
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {

        try {
            ItemStack stack = getCommandSenderAsPlayer(sender).getHeldItemMainhand();
            List<String> list = new ArrayList<String>();
            list.add("clear");
            list.add("list");
            list.addAll(((IUpgradableItem) stack.getItem()).getValidUpgrades(stack));
            return getListOfStringsMatchingLastWord(args, list);
        }
        catch (Exception e) {}

        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}
