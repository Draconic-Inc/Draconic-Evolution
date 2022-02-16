package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTextField;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static com.brandon3055.brandonscore.client.gui.GuiToolkit.GuiLayout.DEFAULT;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public class GuiFlowGate extends ModularGuiContainer<ContainerBCTile<TileFlowGate>> {

    private GuiToolkit<GuiFlowGate> toolkit = new GuiToolkit<>(this, DEFAULT).setTranslationPrefix("gui.draconicevolution.flow_gate");
    private TileFlowGate tile;
    private long ltMin = -1;
    private long ltMax = -1;

    public GuiFlowGate(ContainerBCTile<TileFlowGate> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.tile = container.tile;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine template = toolkit.loadTemplate(new TBasicMachine(this, tile));

        template.background.addChild(new GuiLabel(toolkit.i18n("overridden")))
                .setHoverText(toolkit.i18n("overridden.info"))
                .setSize(60, 8)
                .setTextColour(0xff0000)
                .setShadow(false)
                .setEnabledCallback(() -> tile.flowOverridden.get())
                .setPos(guiLeft() + 4, guiTop() + 4);

        GuiLabel highLabel = template.background.addChild(new GuiLabel(toolkit.i18n("redstone_high")))
                .setSize(xSize(), 8)
                .setTextColour(0xff0000)
                .setShadow(false)
                .setPos(template.playerSlots.xPos(), template.title.maxYPos() + 4);

        GuiTextField highField = toolkit.createTextField(template.background)
                .setHoverText(toolkit.i18n("redstone_high.info"))
                .setValidator(toolkit.catchyValidator(s -> s.equals("") || Long.parseLong(s) >= 0))
                .setSize(template.playerSlots.xSize() - 60, 14)
                .setPos(highLabel.xPos(), highLabel.maxYPos() + 2);

        toolkit.createButton(toolkit.i18n("apply"), template.background)
                .setPos(highField.maxXPos() + 1, highField.yPos())
                .setYSize(highField.ySize())
                .setMaxXPos(template.playerSlots.maxXPos(), true)
                .onPressed(() -> tile.setMax(highField.getText()));

        GuiLabel lowLabel = template.background.addChild(new GuiLabel(toolkit.i18n("redstone_low")))
                .setSize(xSize(), 8)
                .setTextColour(0x990000)
                .setShadow(false)
                .setPos(highField.xPos(), highField.maxYPos() + 3);

        GuiTextField lowField = toolkit.createTextField(template.background)
                .setHoverText(toolkit.i18n("redstone_low.info"))
                .setValidator(toolkit.catchyValidator(s -> s.equals("") || Long.parseLong(s) >= 0))
                .setSize(template.playerSlots.xSize() - 60, 14)
                .setPos(lowLabel.xPos(), lowLabel.maxYPos() + 2);

        toolkit.createButton(toolkit.i18n("apply"), template.background)
                .setPos(lowField.maxXPos() + 1, lowField.yPos())
                .setYSize(lowField.ySize())
                .setMaxXPos(template.playerSlots.maxXPos(), true)
                .onPressed(() -> tile.setMin(lowField.getText()));

        template.background.addChild(new GuiLabel())
                .setHoverText(toolkit.i18n("flow.info"))
                .setTrim(false)
                .setShadow(false)
                .setTextColGetter(GuiToolkit.Palette.Slot::text)
                .setSize(xSize(), 8)
                .setYPos(lowField.maxYPos() + 5)
                .setMaxXPos(template.playerSlots.maxXPos(), false)
                .setDisplaySupplier(() -> toolkit.i18n("flow") + ": " + (tile.getFlow() > 999999 ? Utils.formatNumber(tile.getFlow()) : Utils.addCommas(tile.getFlow())) + tile.getUnits())
                .setAlignment(GuiAlign.RIGHT);

        manager.onTick(() -> {
            if (tile.minFlow.get() != ltMin) {
                ltMin = tile.minFlow.get();
                lowField.setText(String.valueOf(ltMin));
            }
            if (tile.maxFlow.get() != ltMax) {
                ltMax = tile.maxFlow.get();
                highField.setText(String.valueOf(ltMax));
            }
            if (!highField.isFocused() && highField.getText().equals("")) {
                highField.setText("0");
            }
            if (!lowField.isFocused() && lowField.getText().equals("")) {
                lowField.setText("0");
            }
        });
    }
}
