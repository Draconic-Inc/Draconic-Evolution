//package com.brandon3055.draconicevolution.command;
//
//import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
//import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.text.StringTextComponent;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by brandon3055 on 29/06/2016.
// */
//public class CommandUpgrade extends CommandBase {
//    @Override
//    public String getName() {
//        return "de_upgrade";
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "/de_upgrade";
//    }
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        if (args.length == 0) {
//            sender.sendMessage(new StringTextComponent("/de_upgrade clear"));
//            sender.sendMessage(new StringTextComponent("/de_upgrade list"));
//            sender.sendMessage(new StringTextComponent("/de_upgrade <upgade> <level>"));
//            return;
//        }
//
//        ItemStack stack = getCommandSenderAsPlayer(sender).getHeldItemMainhand();
//        if (stack.isEmpty()) {
//            throw new CommandException("Item Not Found");
//        }
//
//        if (!(stack.getItem() instanceof IUpgradableItem)) {
//            throw new CommandException("Item Not Upgradable");
//        }
//
//        if (args.length == 1 && args[0].equals("clear")) {
//            if (stack.hasTagCompound()) {
//                stack.getTag().setTag(UpgradeHelper.UPGRADE_TAG, new CompoundNBT());
//            }
//            return;
//        }
//
//        if (args.length == 1 && args[0].equals("list")) {
//            for (String s : ((IUpgradableItem) stack.getItem()).getValidUpgrades(stack)) {
//                sender.sendMessage(new StringTextComponent(s));
//            }
//            return;
//        }
//
//        if (args.length == 2) {
//            List<String> list = ((IUpgradableItem) stack.getItem()).getValidUpgrades(stack);
//            if (!list.contains(args[0])) {
//                throw new CommandException("Upgrade is invalid or not applicable for this item");
//            }
//
//            try {
//                int level = Integer.parseInt(args[1]);
//                UpgradeHelper.setUpgradeLevel(stack, args[0], level);
//                return;
//            }
//            catch (Exception e) {
//                throw new CommandException("Expected Number... Found " + args[1]);
//            }
//
//        }
//
//        sender.sendMessage(new StringTextComponent("/de_upgrade clear"));
//        sender.sendMessage(new StringTextComponent("/de_upgrade list"));
//        sender.sendMessage(new StringTextComponent("/de_upgrade <upgade> <level>"));
//    }
//
//    @Override
//    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
//        try {
//            ItemStack stack = getCommandSenderAsPlayer(sender).getHeldItemMainhand();
//            List<String> list = new ArrayList<>();
//            list.add("clear");
//            list.add("list");
//            list.addAll(((IUpgradableItem) stack.getItem()).getValidUpgrades(stack));
//            return getListOfStringsMatchingLastWord(args, list);
//        }
//        catch (Exception ignored) {
//        }
//        return super.getTabCompletions(server, sender, args, targetPos);
//    }
//}
