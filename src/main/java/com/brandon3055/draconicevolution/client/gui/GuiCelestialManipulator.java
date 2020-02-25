//package com.brandon3055.draconicevolution.client.gui;
//
//import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
//import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
//import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
//import com.brandon3055.draconicevolution.inventory.ContainerDummy;
//import net.minecraft.entity.player.PlayerEntity;
//
///**
// * Created by brandon3055 on 17/10/2016.
// */
//public class GuiCelestialManipulator extends ModularGuiContainer<ContainerDummy> {
//
//    private PlayerEntity player;
//    private TileCelestialManipulator tile;
////    private MGuiEffectRenderer effectRenderer;
////    private MGuiButtonSolid weatherMode;
////    private MGuiButtonSolid sunMode;
////    private List<MGuiElementBase> weatherControls = new ArrayList<>();
////    private List<MGuiElementBase> sunControls = new ArrayList<>();
////    private MGuiEnergyBar energyBar;
////    private MGuiBorderedRect rsBackGround;
////    private MGuiElementBase[] rsControlButtons = new MGuiElementBase[10];
////    private double rsTabAnim = 0;
////    private boolean rsTabEnabled = false;
//
//    public GuiCelestialManipulator(PlayerEntity player, TileCelestialManipulator tile) {
//        super(new ContainerDummy(tile, player, 10, 120));
//
//        this.xSize = 180;
//        this.ySize = 200;
//
//        this.player = player;
//        this.tile = tile;
//    }
//
//    @Override
//    public void addElements(GuiElementManager manager) {
//
//    }
//
////    @Override
////    public void initGui() {
////        super.initGui();
////
////        manager.clear();
////        weatherControls.clear();
////        sunControls.clear();
////        manager.add(MGuiBackground.newGenericBackground(this, guiLeft, guiTop + 97, xSize, ySize - 97));
////        manager.add(new MGuiLabel(this, guiLeft, guiTop - 12, xSize, 12, DEFeatures.celestialManipulator.getLocalizedName()).setTextColour(InfoHelper.GUI_TITLE));
////        manager.add(new MGuiElementBase(this) {
////            @Override
////            public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
////                GlStateManager.color(1, 1, 1);
////                GuiHelper.drawPlayerSlots(GuiCelestialManipulator.this, guiLeft + (GuiCelestialManipulator.this.xSize / 2), guiTop + 119, true);
////            }
////        });
////        manager.add(effectRenderer = new MGuiEffectRenderer(this).setParticleTexture(DEParticles.DE_SHEET.toString()));
////        manager.add(weatherMode = new MGuiButtonSolid(this, "WEATHER_MODE", guiLeft + 5, guiTop + 1, 50, 12, I18n.format("gui.de.celMod.weather")) {
////            @Override
////            public int getTextColour(boolean hovered, boolean disabled) {
////                return disabled ? 0x90600000 : hovered ? 0x9080FFFF : 0x9060FF60;
////            }
////
////            @Override
////            public int getBorderColour(boolean hovering, boolean disabled) {
////                return disabled ? 0x9000FF00 : hovering ? 0x9000FF00 : 0x9000FFFF;
////            }
////
////        }.setColours(0x90000000, 0x90111111, 0x90222222));
////        manager.add(sunMode = new MGuiButtonSolid(this, "SUN_MODE", guiLeft + xSize - 55, guiTop + 1, 50, 12, I18n.format("gui.de.celMod.time")) {
////            @Override
////            public int getTextColour(boolean hovered, boolean disabled) {
////                return disabled ? 0x90600000 : hovered ? 0x9080FFFF : 0x9060FF60;
////            }
////
////            @Override
////            public int getBorderColour(boolean hovering, boolean disabled) {
////                return disabled ? 0x9000FF00 : hovering ? 0x9000FF00 : 0x9000FFFF;
////            }
////
////        }.setColours(0x90000000, 0x90111111, 0x90222222));
////        manager.add(energyBar = new MGuiEnergyBar(this, guiLeft + 9, guiTop + 102, xSize - 18, 14).setEnergy(tile.getEnergyStored(Direction.UP), tile.getMaxEnergyStored(Direction.UP)).setHorizontal(true), 4);
////        manager.add(new MGuiBorderedRect(this, guiLeft + 2, guiTop - 1, xSize - 4, ySize - 102).setFillColour(0x40000000).setBorderColour(0x90000000));
////
////        int i = 26;
////        weatherControls.add(new MGuiButtonSolid(this, "STOP_RAIN", guiLeft + 4, guiTop + i, xSize - 8, 14, I18n.format("gui.de.celMod.stopRain")));
////        weatherControls.add(new MGuiButtonSolid(this, "START_RAIN", guiLeft + 4, guiTop + (i += 22), xSize - 8, 14, I18n.format("gui.de.celMod.startRain")));
////        weatherControls.add(new MGuiButtonSolid(this, "START_STORM", guiLeft + 4, guiTop + (i += 22), xSize - 8, 14, I18n.format("gui.de.celMod.startStorm")));
////
////        i = 20;
////        sunControls.add(new MGuiLabel(this, guiLeft, guiTop + i, xSize, 12, I18n.format("gui.de.celMod.skipTo")));
////        i += 12;
////        sunControls.add(new MGuiButtonSolid(this, "SUN_RISE", guiLeft + 4, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.sunRise")));
////        sunControls.add(new MGuiButtonSolid(this, "MID_DAY", guiLeft + 4 + xSize / 3 - 2, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.midDay")));
////        sunControls.add(new MGuiButtonSolid(this, "SUN_SET", guiLeft + 4 + (xSize / 3) * 2 - 4, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.sunSet")));
////        i += 20;
////        sunControls.add(new MGuiButtonSolid(this, "MOON_RISE", guiLeft + 4, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.moonRise")));
////        sunControls.add(new MGuiButtonSolid(this, "MIDNIGHT", guiLeft + 4 + xSize / 3 - 2, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.midnight")));
////        sunControls.add(new MGuiButtonSolid(this, "MOON_SET", guiLeft + 4 + (xSize / 3) * 2 - 4, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.moonSet")));
////        i += 20;
////        sunControls.add(new MGuiButtonSolid(this, "SKIP_24", guiLeft + 4, guiTop + i, xSize / 2 - 5, 14, I18n.format("gui.de.celMod.skip24")));
////        sunControls.add(new MGuiButtonSolid(this, "STOP", guiLeft + 1 + xSize / 2, guiTop + i, xSize / 2 - 5, 14, I18n.format("gui.de.celMod.stop")));
////
////        updateControls();
////
////        manager.add(rsBackGround = new MGuiBorderedRect(this, guiLeft + xSize, guiTop + 97, 18, 18).setBorderColour(0xFF505050));
////        rsBackGround.addChild(new MGuiStackIcon(this, rsBackGround.xPos, rsBackGround.yPos, rsBackGround.xSize, rsBackGround.ySize, new StackReference("redstone")).setToolTip(false));
////        manager.add(new MGuiButtonSolid(this, "TOGGLE_RS_PANEL", rsBackGround.xPos, rsBackGround.yPos, rsBackGround.xSize, rsBackGround.ySize, "").setColours(0, 0, 0).setToolTip(new String[]{I18n.format("generic.configureRedstone.txt")}).setToolTipDelay(2));
////
////        RawColumns builder = new RawColumns(rsBackGround.xPos, rsBackGround.yPos + 18, 3, 18, 3);
////        for (int b = 0; b < rsControlButtons.length; b++) {
////            builder.add(rsControlButtons[b] = new MGuiBackground(this, 1, 1, 36, 40 + b * 18, 18, 18, "draconicevolution:textures/gui/widgets.png"));
////            rsControlButtons[b].addChild(new MGuiButtonSolid(this, b, rsControlButtons[b].xPos - 1, rsControlButtons[b].yPos - 1, 20, 20, "") {
////                @Override
////                public int getBorderColour(boolean hovering, boolean disabled) {
////                    return disabled ? 0xFFf00000 : hovering ? 0xFF707070 : 0xFF505050;
////                }
////            }.setColours(0, 0xFF505050, 0xFF707070).setToolTip(new String[]{I18n.format("gui.de.celMod.rs." + b)}).addToGroup("RS_BUTTON"));
////        }
////        builder.finish(manager, 0);
////
////        manager.initElements();
////    }
////
////    private void updateControls() {
////        if (tile.weatherMode.get()) {
////            for (MGuiElementBase elementBase : sunControls) {
////                manager.remove(elementBase);
////            }
////            for (MGuiElementBase elementBase : weatherControls) {
////                if (!manager.getElements().contains(elementBase)) {
////                    manager.add(elementBase);
////                }
////            }
////            sunMode.disabled = false;
////            weatherMode.disabled = true;
////        }
////        else {
////            for (MGuiElementBase elementBase : sunControls) {
////                if (!manager.getElements().contains(elementBase)) {
////                    manager.add(elementBase);
////                }
////            }
////            for (MGuiElementBase elementBase : weatherControls) {
////                manager.remove(elementBase);
////            }
////            sunMode.disabled = true;
////            weatherMode.disabled = false;
////        }
////
////    }
////
////    @Override
////    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
////        if (eventElement instanceof MGuiButton) {
////            if (((MGuiButton) eventElement).buttonName.equals("TOGGLE_RS_PANEL")) {
////                rsTabEnabled = !rsTabEnabled;
////            }
////            else if (eventElement.isInGroup("RS_BUTTON")) {
////                tile.sendPacketToServer(output -> output.writeInt(((MGuiButton) eventElement).buttonId), 1);
////            }
////            else {
////                tile.sendPacketToServer(output -> output.writeString(((MGuiButton) eventElement).buttonName), 0);
////            }
////        }
////    }
////
////    private Rectangle animRect = new Rectangle(0, 0, 0, 0);
////
////    @Override
////    public void updateScreen() {
////        energyBar.setEnergy(tile.energySync.get());
////        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
////        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
////
////        for (MGuiElementBase element : manager.getElements()) {
////            if (element instanceof MGuiButton && (element.isMouseOver(mouseX, mouseY) || ((element == sunMode || element == weatherMode) && ((MGuiButton) element).disabled))) {
////                GuiParticle particle = new GuiParticle(mc.world, element.xPos + 4 + mc.world.rand.nextInt(element.xSize - 8), element.yPos + (element.ySize / 2));
////                particle.setScale(0.1F);
////                particle.setRBGColorF(1, 1, 1);
////                effectRenderer.addEffect(particle);
////            }
////        }
////
////        updateControls();
////
////        effectRenderer.addEffect(new GuiParticle(mc.world, guiLeft + xSize / 2, guiTop + 6));
////
////        if (rsTabEnabled && rsTabAnim < 1) {
////            rsTabAnim += 0.2;
////        }
////        else if (!rsTabEnabled && rsTabAnim > 0) {
////            rsTabAnim -= 0.2;
////        }
////        if (rsTabAnim > 1) rsTabAnim = 1;
////        else if (rsTabAnim < 0) rsTabAnim = 0;
////
////        rsBackGround.xSize = 18 + (int) (rsTabAnim * 48);
////        rsBackGround.ySize = 18 + (int) (rsTabAnim * 84);
////
////        animRect = new Rectangle(rsBackGround.xPos, rsBackGround.yPos, rsBackGround.xSize, rsBackGround.ySize);
////
////        for (MGuiElementBase elementBase : rsControlButtons) {
////            if (elementBase != null) {
////                elementBase.setEnabled(elementBase.xPos < animRect.x + animRect.width - 18 && elementBase.yPos < animRect.y + animRect.height - 18);
////                MGuiElementBase e;
////                if (elementBase.childElements.size() > 0 && (e = elementBase.childElements.get(0)) instanceof MGuiButton) {
////                    ((MGuiButton) e).disabled = tile.rsMode.get() == ((MGuiButton) e).buttonId;
////                }
////            }
////        }
////
////        super.updateScreen();
////    }
////
////    @Override
////    public List<Rectangle> getGuiExtraAreas() {
////        List<Rectangle> list = new ArrayList<>();
////
////        list.add(animRect);
////
////        return list;
////    }
////
////    private class GuiParticle extends GuiEffect {
////
////        protected GuiParticle(World world, double posX, double posY) {
////            super(world, posX, posY);
////
////            float speed = 5F;
////            this.motionX = (-0.5F + rand.nextFloat()) * speed;
////            this.motionY = (-0.5F + rand.nextFloat()) * speed / 4F;
////            this.particleMaxAge = 10 + rand.nextInt(10);
////            this.particleScale = 0.5F;
////            this.particleTextureIndexX = 0;
////            this.particleTextureIndexY = 1;
////            this.particleRed = 0;
////        }
////
////        @Override
////        public GuiEffect setScale(float scale) {
////            float speed = 5F * scale;
////            this.motionX = (-0.5F + rand.nextFloat()) * speed;
////            this.motionY = (-0.5F + rand.nextFloat()) * speed;
////            return super.setScale(scale);
////        }
////
////        @Override
////        public void onUpdate() {
////            super.onUpdate();
////
////            particleTextureIndexX = rand.nextInt(5);
////            int ttd = particleMaxAge - particleAge;
////            if (ttd < 10) {
////                particleScale = ttd / 10F;
////            }
////            if (ttd == 1) {
////                particleScale = 0.5F;
////                setExpired();
////            }
////
////        }
////
////        @Override
////        public void renderParticle(float partialTicks) {
////            if (particleAge == 0) {
////                return;
////            }
////            super.renderParticle(partialTicks);
////        }
////    }
//}
