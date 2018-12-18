package com.brandon3055.draconicevolution.items;

import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.projectintelligence.api.PiAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;

/**
 * Created by brandon3055 on 22/09/2016.
 */
public class InfoTablet extends ItemBCore {

    public static boolean nagShown = false;

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (worldIn.isRemote) {
            if (!openPIGui()) {
                openGui();
            }
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    public static boolean openPIGui() {
        if (Loader.isModLoaded("projectintelligence")) {
            return doOpenPiGui();
        }
        return false;
    }

    @Optional.Method(modid = "projectintelligence")
    public static boolean doOpenPiGui() {
        if (PiAPI.isAPIAvalible()) {
            PiAPI.openModPage(null, DraconicEvolution.MODID);
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private void openGui() {
        if (nagShown) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiModWiki());
        }
        else {
            nagShown = false;
            try {
                ObfMapping mapping = new ObfMapping("net/minecraft/client/gui/GuiScreen", "field_175286_t");
                GuiModWiki guiModWiki = new GuiModWiki();
                ReflectionManager.setField(mapping, guiModWiki, new URI("https://minecraft.curseforge.com/projects/project-intelligence"));

                GuiConfirmOpenLink guiConfirm = new GuiConfirmOpenLink(guiModWiki, "https://minecraft.curseforge.com/projects/project-intelligence", 31102009, true) {
                    @Override
                    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
                        super.drawScreen(mouseX, mouseY, partialTicks);
                        GuiHelper.drawCenteredSplitString(this.fontRenderer, "The documentation built into DE is now obsolete and will be removed in the next update. To access the new and improved in game documentation for DE please install Project Intelligence. (You will only see this message once per game session)", this.width / 2, 105, this.width - 50, 16764108, false);
                    }
                };
                guiConfirm.disableSecurityWarning();
                Minecraft.getMinecraft().displayGuiScreen(guiConfirm);
            }
            catch (Throwable e) {
                e.printStackTrace();
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Please install Project Intelligence. https://minecraft.curseforge.com/projects/project-intelligence"));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);
    }
}
