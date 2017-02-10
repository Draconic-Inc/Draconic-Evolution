package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.MGuiBackground;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.inventory.ContainerReactor;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by brandon3055 on 10/02/2017.
 */
public class GuiReactor extends ModularGuiContainer<ContainerReactor> implements IMGuiListener {

    public GuiReactor(EntityPlayer player, TileReactorCore tile) {
        super(new ContainerReactor(player, tile));
        this.xSize = 248;
        this.ySize = 222;
    }

    @Override
    public void initGui() {
        super.initGui();
//        MGuiElementBase e;
        manager.add(new MGuiBackground(this, guiLeft, guiTop, 0, 0, xSize, ySize, "draconicevolution:"+DETextures.GUI_REACTOR));

//        manager.add(new MGuiTexturedPointer(this, guiLeft + 11, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)){
//            @Override
//            public double getPos() {
//                return 0; //TODO Read value directly from tile
//            }
//        });
//        manager.add(new MGuiTexturedPointer(this, guiLeft + 35, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)){
//            @Override
//            public double getPos() {
//                return 0.25; //TODO Read value directly from tile
//            }
//        });
//        manager.add(new MGuiTexturedPointer(this, guiLeft + 199, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)){
//            @Override
//            public double getPos() {
//                return 0.75; //TODO Read value directly from tile
//            }
//        });
//        manager.add(new MGuiTexturedPointer(this, guiLeft + 223, guiTop + 5, 14, 112, 0, 222, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)){
//            @Override
//            public double getPos() {
//                return 1; //TODO Read value directly from tile
//            }
//        });
//
////        manager.add(e = new MGuiTexturedPointer(this, guiLeft, guiTop + 203, xSize, 14, 4, 200, 5, ResourceHelperDE.getResource(DETextures.GUI_REACTOR)));
////        e.addChild(new MGuiBorderedRect(this, e.xPos, e.yPos, e.xSize, e.ySize));
////        ((MGuiTexturedPointer)e).setHorizontal(true);

        manager.initElements();
    }


    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {

    }
}
