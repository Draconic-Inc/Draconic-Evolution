package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.integration.PIHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Created by brandon3055 on 22/09/2016.
 */
public class InfoTablet extends Item {

    public InfoTablet(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
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
            Player player = Minecraft.getInstance().player;
            MutableComponent message = Component.literal("Project Intelligence is required to view DE documentation. ").withStyle(ChatFormatting.RED);
            MutableComponent link = Component.literal("[Click here to view curse page]").withStyle(ChatFormatting.BLUE);
            link.setStyle(link.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/project-intelligence")));
            link.setStyle(link.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("https://www.curseforge.com/minecraft/mc-mods/project-intelligence"))));
            message.append(link);
            player.sendSystemMessage(message);
        }
    }
}
