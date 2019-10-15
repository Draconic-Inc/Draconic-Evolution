package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.BCGuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiEnergyBar;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.inventory.ContainerGenerator;
import net.minecraft.entity.player.EntityPlayer;

import static com.brandon3055.brandonscore.client.gui.BCGuiToolkit.GuiLayout.DEFAULT_CONTAINER;

public class GuiGenerator extends ModularGuiContainer<ContainerGenerator> {

    public EntityPlayer player;
    private TileGenerator tile;
    private int guiUpdateTick;
//    private InfoPanel infoPanel;
    protected BCGuiToolkit<GuiGenerator> toolkit = new BCGuiToolkit<>(this, DEFAULT_CONTAINER);

    public GuiGenerator(EntityPlayer player, TileGenerator tile) {
        super(new ContainerGenerator(player, tile));

//        xSize = 176;
//        ySize = 162;

        this.tile = tile;
        this.player = player;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine template = toolkit.loadTemplate(new TBasicMachine(tile));
        template.setTitle("gui.de.title.generator");//TODO move to tile? Or make the tile name override this?
        template.energyBar.setEnergyHandler(tile.opStorage);
//
//        infoPanel = toolkit.createInfoPanel(template.background, false);
//        infoPanel.setOrigin(() -> new Point(template.themeButton.xPos(), template.themeButton.maxYPos()));

        template.infoPanel.addElement(new GuiLabel("Test Label Text").setWidthFromText(8));
        template.infoPanel.addElement(new GuiLabel("Test Label T iwh egiofh").setWidthFromText(8));

//        infoPanel.addElement(new GuiButton("Test Button").setVanillaButtonRender(true).setSize(100, 200));


        GuiEnergyBar bar2 = new GuiEnergyBar();
        template.background.addChild(bar2);
        bar2.setPos(10, 50);
        bar2.setYSize(25);

        bar2 = new GuiEnergyBar();
        template.background.addChild(bar2);
        bar2.setPos(25, 50);
        bar2.setYSize(50);

        bar2 = new GuiEnergyBar();
        template.background.addChild(bar2);
        bar2.setPos(40, 50);
        bar2.setYSize(75);

        bar2 = new GuiEnergyBar();
        template.background.addChild(bar2);
        bar2.setPos(55, 50);
        bar2.setYSize(100);

        bar2 = new GuiEnergyBar();
        template.background.addChild(bar2);
        bar2.setPos(70, 50);
        bar2.setYSize(125);

        bar2 = new GuiEnergyBar();
        template.background.addChild(bar2);
        bar2.setPos(85, 50);
        bar2.setYSize(150);

        bar2 = new GuiEnergyBar();
        template.background.addChild(bar2);
        bar2.setPos(100, 50);
        bar2.setYSize(175);

        bar2 = new GuiEnergyBar();
        template.background.addChild(bar2);
        bar2.setPos(115, 50);
        bar2.setYSize(200);


        bar2 = new GuiEnergyBar().setHorizontal(true);
        template.background.addChild(bar2);
        bar2.setPos(10, 195);
        bar2.setXSize(25);

        bar2 = new GuiEnergyBar().setHorizontal(true);
        template.background.addChild(bar2);
        bar2.setPos(10, 210);
        bar2.setXSize(50);

        bar2 = new GuiEnergyBar().setHorizontal(true);
        template.background.addChild(bar2);
        bar2.setPos(10, 225);
        bar2.setXSize(75);

        bar2 = new GuiEnergyBar().setHorizontal(true);
        template.background.addChild(bar2);
        bar2.setPos(10, 240);
        bar2.setXSize(100);

        bar2 = new GuiEnergyBar().setHorizontal(true);
        template.background.addChild(bar2);
        bar2.setPos(10, 255);
        bar2.setXSize(125);

        bar2 = new GuiEnergyBar().setHorizontal(true);
        template.background.addChild(bar2);
        bar2.setPos(10, 270);
        bar2.setXSize(150);

        bar2 = new GuiEnergyBar().setHorizontal(true);
        template.background.addChild(bar2);
        bar2.setPos(10, 285);
        bar2.setXSize(175);

        bar2 = new GuiEnergyBar().setHorizontal(true);
        template.background.addChild(bar2);
        bar2.setPos(10, 300);
        bar2.setXSize(200);


//        manager.addChild(new GuiButton("Reload Shaders").setSize(100, 12).setVanillaButtonRender(true).setYPos(30).setListener(() -> {
////            GuiEnergyBar.shaderProgram = null;
////            GuiEnergyBar.shaderProgramH = null;
//            try {
//                BCShaders.initShaders();
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        }));
    }


//        @Override
//    protected void drawGuiContainerBackgroundLayer(float f, int X, int Y) {
//        GL11.glColor4f(1, 1, 1, 1);
//
//        ResourceHelperDE.bindTexture(DETextures.GUI_GENERATOR);
//        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
//        drawTexturedModalRect(guiLeft + 63, guiTop + 34, 0, ySize, 18, 18);//fuel box
//        drawTexturedModalRect(guiLeft + 97, guiTop + 34, 18, ySize, 18, 18);//flame box
//        if (tile.itemHandler.getStackInSlot(0).isEmpty()){
//            drawTexturedModalRect(guiLeft + 63, guiTop + 34, 36, ySize, 18, 18);//fuel box
//        }
//
//        float power = (float) tile.opStorage.getEnergyStored() / (float) tile.opStorage.getMaxEnergyStored() * -1 + 1;
//        float fuel = tile.burnTimeRemaining.get() / ((float) tile.burnTime.get()) * -1 + 1;
//
//        drawTexturedModalRect(guiLeft + 83, guiTop + 11 + (int) (power * 40), xSize, (int) (power * 40), 12, 40 - (int) (power * 40));//Power bar
//        drawTexturedModalRect(guiLeft + 100, guiTop + 37 + (int) (fuel * 13), xSize, 40 + (int) (fuel * 13), 18, 18 - (int) (fuel * 13));//Power bar
//
//        fontRenderer.drawStringWithShadow(I18n.format(DEFeatures.generator.getUnlocalizedName() + ".name"), guiLeft + 64, guiTop, 0x00FFFF);
//
//        int x = X - guiLeft;
//        int y = Y - guiTop;
//        if (GuiHelper.isInRect(83, 10, 12, 40, x, y)) {
//            ArrayList<String> internal = new ArrayList<>();
//            internal.add(I18n.format("info.de.energyBuffer.txt"));
//            internal.add("" + TextFormatting.DARK_BLUE + tile.opStorage.getEnergyStored() + "/" + tile.opStorage.getMaxEnergyStored());
//            drawHoveringText(internal, x + guiLeft, y + guiTop, fontRenderer);
//        }
//    }
//
//    @Override
//    public void updateScreen() {
//        guiUpdateTick++;
//        if (guiUpdateTick >= 10) {
//            initGui();
//            guiUpdateTick = 0;
//        }
//        super.updateScreen();
//
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        this.drawDefaultBackground();
//        super.drawScreen(mouseX, mouseY, partialTicks);
//        this.renderHoveredToolTip(mouseX, mouseY);
//    }
}
