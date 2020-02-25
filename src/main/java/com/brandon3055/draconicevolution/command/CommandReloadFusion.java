//package com.brandon3055.draconicevolution.command;
//
//import com.brandon3055.brandonscore.lib.DelayedTask;
//import com.brandon3055.draconicevolution.integration.jei.DEJEIPlugin;
//import com.brandon3055.draconicevolution.lib.RecipeManager;
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.text.StringTextComponent;
//
///**
// * Created by brandon3055 on 3/12/18.
// */
//public class CommandReloadFusion extends CommandBase {
//    @Override
//    public String getName() {
//        return "de_reload_custom_fusion";
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "Usage: /de_reload_custom_fusion";
//    }
//
//    @Override
//    public int getRequiredPermissionLevel() {
//        return 3;
//    }
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        DelayedTask.run(0, () -> {
//            RecipeManager.FUSION_REGISTRY.getRecipes().forEach(DEJEIPlugin.jeiRuntime.getRecipeRegistry()::removeRecipe);
//            try {
//                RecipeManager.reloadCustomFusionRecipes();
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//                sender.sendMessage(new StringTextComponent(e.getMessage()));
//            }
//            RecipeManager.FUSION_REGISTRY.getRecipes().forEach(DEJEIPlugin.jeiRuntime.getRecipeRegistry()::addRecipe);
//        });
//    }
//}
