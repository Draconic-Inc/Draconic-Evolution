package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.WhatsBrokenGui;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.item.Item.Properties;

/**
 * Created by brandon3055 on 22/09/2016.
 */
public class InfoTablet extends Item {

    public static boolean nagShown = false;

    public InfoTablet(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        if (worldIn.isClientSide) {
            openPIGui();
        }
        return super.use(worldIn, playerIn, hand);
    }


    @OnlyIn(Dist.CLIENT)
    public static void openPIGui() {
//        if (Loader.isModLoaded("projectintelligence")) {
//            return doOpenPiGui();
//        }
        Minecraft.getInstance().setScreen(new WhatsBrokenGui());
//        return false;
    }

//    @Optional.Method(modid = "projectintelligence")
    public static boolean doOpenPiGui() {
//        if (PiAPI.isAPIAvalible()) {
//            PiAPI.openModPage(null, DraconicEvolution.MODID);
//            return true;
//        }
        return false;
    }

//    @OnlyIn(Dist.CLIENT)
//    private void openGui() {
//        try {
//            Screen screen = new Screen() {
//                @Override
//                public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//                    super.drawScreen(mouseX, mouseY, partialTicks);
//                }
//            };
//            ObfMapping mapping = new ObfMapping("net/minecraft/client/gui/Screen", "field_175286_t");
//            ReflectionManager.setField(mapping, screen, new URI("https://minecraft.curseforge.com/projects/project-intelligence"));
//            GuiConfirmOpenLink guiConfirm = new GuiConfirmOpenLink(screen, "https://minecraft.curseforge.com/projects/project-intelligence", 31102009, true) {
//                @Override
//                public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//                    super.drawScreen(mouseX, mouseY, partialTicks);
//                    GuiHelper.drawCenteredSplitString(this.fontRenderer, "The documentation built into DE is now obsolete and will be removed in the next update. To access the new and improved in game documentation for DE please install Project Intelligence. (You will only see this message once per game session)", this.width / 2, 105, this.width - 50, 16764108, false);
//                }
//            };
//            guiConfirm.disableSecurityWarning();
//            Minecraft.getInstance().displayGuiScreen(guiConfirm);
//        }
//        catch (Throwable e) {
//            e.printStackTrace();
//            Minecraft.getInstance().player.sendMessage(new StringTextComponent("Please install Project Intelligence. https://minecraft.curseforge.com/projects/project-intelligence"));
//        }
//    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
//        super.addInformation(stack, world, tooltip, advanced);
//    }
}
