package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.integration.PIHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by brandon3055 on 22/09/2016.
 */
public class InfoTablet extends Item {

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
        if (PIHelper.isInstalled()) {
            PIHelper.openMod(null, DraconicEvolution.MODID);
        } else {
            PlayerEntity player = Minecraft.getInstance().player;
            IFormattableTextComponent message = new StringTextComponent("Project Intelligence is required to view DE documentation. ").withStyle(TextFormatting.RED);
            IFormattableTextComponent link = new StringTextComponent("[Click here to view curse page]").withStyle(TextFormatting.BLUE);
            link.setStyle(link.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/project-intelligence")));
            link.setStyle(link.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("https://www.curseforge.com/minecraft/mc-mods/project-intelligence"))));
            message.append(link);
            player.sendMessage(message, Util.NIL_UUID);
        }
    }
}
