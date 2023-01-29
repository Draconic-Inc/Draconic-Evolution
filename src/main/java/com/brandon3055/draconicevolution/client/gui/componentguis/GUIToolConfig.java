package com.brandon3055.draconicevolution.client.gui.componentguis;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;

import com.brandon3055.brandonscore.client.gui.guicomponents.*;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.GuiHudConfig;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentConfigItemButton;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentFieldAdjuster;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentFieldButton;
import com.brandon3055.draconicevolution.common.container.ContainerAdvTool;
import com.brandon3055.draconicevolution.common.handler.ContributorHandler;
import com.brandon3055.draconicevolution.common.items.weapons.BowHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.network.ContributorPacket;
import com.brandon3055.draconicevolution.common.network.ItemConfigPacket;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;

/**
 * Created by Brandon on 26/12/2014.
 */
public class GUIToolConfig extends GUIBase {

    public EntityPlayer player;
    private static final ResourceLocation inventoryTexture = new ResourceLocation(
            References.RESOURCESPREFIX + "textures/gui/ToolConfig.png");
    private int screenLevel = 0;
    private ItemStack editingItem;
    private ContainerAdvTool container;
    private int slot;

    public GUIToolConfig(EntityPlayer player, ContainerAdvTool container) {
        super(container, 198, 89);
        this.container = container;
        this.player = player;
        container.setSlotsActive(false);
        addDependentComponents();
    }

    @Override
    public void initGui() {
        super.initGui();
        if (ContributorHandler.contributors.containsKey(player.getCommandSenderName())) {
            ContributorHandler.Contributor contributor = ContributorHandler.contributors
                    .get(player.getCommandSenderName());
            if (!contributor.isUserValid(player)) return;

            buttonList.clear();

            if (contributor.contributionLevel >= 1) buttonList.add(
                    new GuiButton(
                            0,
                            guiLeft - 150,
                            guiTop,
                            150,
                            20,
                            "Contributor Wings: " + (contributor.contributorWingsEnabled ? "Enabled" : "Disabled")));
            if (contributor.contribution.toLowerCase().contains("patreon")) buttonList.add(
                    new GuiButton(
                            1,
                            guiLeft - 150,
                            guiTop + 22,
                            150,
                            20,
                            "Patreon Badge: " + (contributor.patreonBadgeEnabled ? "Enabled" : "Disabled")));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (ContributorHandler.contributors.containsKey(player.getCommandSenderName())) {
            ContributorHandler.Contributor contributor = ContributorHandler.contributors
                    .get(player.getCommandSenderName());
            if (!contributor.isUserValid(player)) return;

            if (button.id == 0) {
                contributor.contributorWingsEnabled = !contributor.contributorWingsEnabled;
                button.displayString = "Contributor Wings: "
                        + (contributor.contributorWingsEnabled ? "Enabled" : "Disabled");
            } else if (button.id == 1) {
                contributor.patreonBadgeEnabled = !contributor.patreonBadgeEnabled;
                button.displayString = "Patreon Badge: " + (contributor.patreonBadgeEnabled ? "Enabled" : "Disabled");
            }

            DraconicEvolution.network.sendToServer(
                    new ContributorPacket(
                            player.getCommandSenderName(),
                            contributor.contributorWingsEnabled,
                            contributor.patreonBadgeEnabled));
        }
    }

    @Override
    protected ComponentCollection assembleComponents() {
        ComponentCollection c = new ComponentCollection(0, 0, xSize, ySize, this).setOpenBoarders();
        c.addComponent(new ComponentTexturedRect(0, -15, 198, 20, inventoryTexture)).setGroup("TEXT_TAB");
        c.addComponent(new ComponentTexturedRect(0, 0, 198, 89, inventoryTexture)).setGroup("BACKGROUND");
        c.addComponent(new ComponentTexturedRect(0, 13, 0, 9, 198, 80, inventoryTexture, false))
                .setGroup("BACKGROUND_EXTENSION");
        c.addComponent(new ComponentButton(3, 26, 20, 12, 0, this, "<=", StatCollector.translateToLocal("gui.back")))
                .setGroup("BUTTONS").setName("BACK_BUTTON");
        c.addComponent(
                new ComponentButton(
                        3,
                        39,
                        20,
                        12,
                        1,
                        this,
                        "Inv",
                        StatCollector.translateToLocal("gui.de.itemInventory.txt")))
                .setGroup("BUTTONS").setName("INVENTORY_BUTTON");
        c.addComponent(new ComponentFieldAdjuster(4, 34, null, this)).setGroup("FIELD_BUTTONS")
                .setName("FIELD_CONFIG_BUTTON_ARRAY");
        c.addComponent(
                new ComponentButton(
                        0,
                        ySize,
                        xSize,
                        14,
                        2,
                        this,
                        StatCollector.translateToLocal("gui.de.configureGuiElements.txt")).setGroup("INV_SCREEN"));

        ComponentTextField textField = (ComponentTextField) new ComponentTextField(this, 3, -12, 191, 12)
                .setLabel(StatCollector.translateToLocal("gui.de.profile.txt") + ":", 0xE0E0E0).setGroup("TEXT_TAB")
                .setName("PROFILE_TEXT_FIELD");
        c.addComponent(textField);
        return c;
    }

    protected void addDependentComponents() {
        for (int x = 0; x < 9; x++) {
            collection.addComponent(new ComponentConfigItemButton(29 + 18 * x, 64, x, player)).setGroup("INV_SCREEN");
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                collection.addComponent(new ComponentConfigItemButton(29 + 18 * x, 7 + y * 18, x + y * 9 + 9, player))
                        .setGroup("INV_SCREEN");
            }
        }

        for (int y = 0; y < 4; y++) {
            collection.addComponent(new ComponentConfigItemButton(6, 7 + y * 19, 39 - y, player))
                    .setGroup("INV_SCREEN");
        }

        setLevel(0);
    }

    public void updateItemButtons() {
        for (ComponentBase component : collection.getComponents()) {
            if (component instanceof ComponentConfigItemButton) {
                ((ComponentConfigItemButton) component).refreshState();
            }
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        if (buttonPressed) return;

        int fieldOffsetX = 24;
        int fieldOffsetY = 5;

        for (ComponentBase component : collection.getComponents()) {
            if (component.isEnabled() && component instanceof ComponentConfigItemButton
                    && component.isMouseOver(x - this.guiLeft, y - this.guiTop)
                    && ((ComponentConfigItemButton) component).hasValidItem) {
                ItemStack stack = player.inventory.getStackInSlot(((ComponentConfigItemButton) component).slot);
                if (stack == null || !(stack.getItem() instanceof IConfigurableItem)) return;
                buttonPressed = true;
                IConfigurableItem item = (IConfigurableItem) stack.getItem();

                setEditingItem(stack, ((ComponentConfigItemButton) component).slot);
                setLevel(1);
                for (ItemConfigField field : item.getFields(stack, ((ComponentConfigItemButton) component).slot)) {
                    collection.addComponent(new ComponentFieldButton(fieldOffsetX, fieldOffsetY, player, field, this))
                            .setGroup("LIST_SCREEN");
                    fieldOffsetY += 12;
                }

                if (collection.getComponent("PROFILE_TEXT_FIELD") instanceof ComponentTextField) {
                    int preset = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
                    String presetName = ItemNBTHelper.getString(stack, "ProfileName" + preset, "Profile " + preset);
                    ((ComponentTextField) collection.getComponent("PROFILE_TEXT_FIELD")).textField.setText(presetName);
                }

                collection.addComponent(new ComponentItemRenderer(3, 5, stack)).setGroup("LIST_SCREEN");
                break;
            }
        }

        if (collection.getComponent("PROFILE_TEXT_FIELD") instanceof ComponentTextField
                && !collection.getComponent("PROFILE_TEXT_FIELD").isMouseOver(x - guiLeft, y - guiTop)) {
            ((ComponentTextField) collection.getComponent("PROFILE_TEXT_FIELD")).textField.setFocused(false);
        }
    }

    @Override
    public void buttonClicked(int id, int button) {
        super.buttonClicked(id, button);

        if (id == 0 && screenLevel > 0) { // button back
            setLevel(screenLevel - 1);
        } else if (id == 1 && editingItem != null) { // inventory button
            setLevel(3);
            Minecraft.getMinecraft().displayGuiScreen(new GUIToolInventory(player, container, this));
            // LogHelper.info("Pre send container " + Minecraft.getMinecraft().thePlayer.openContainer);
            // DraconicEvolution.network.sendToServer(new ButtonPacket(ButtonPacket.ID_TOOLINVENTORY, false));
        } else if (id == 2) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiHudConfig(this));
        }
    }

    public void setLevel(int level) {
        this.screenLevel = level;

        if (level == 0) { // inv screen
            collection.schedulRemoval("LIST_SCREEN");
            collection.setOnlyGroupEnabled("INV_SCREEN");
            collection.setGroupEnabled("BACKGROUND", true);
            collection.setComponentEnabled("BACK_BUTTON", false);
            slot = -1;
        } else if (level == 1) { // list screen
            collection.setOnlyGroupEnabled("LIST_SCREEN");
            collection.setGroupEnabled("BACKGROUND", true);
            collection.setGroupEnabled("BACKGROUND_EXTENSION", true);
            if (editingItem != null && editingItem.getItem() instanceof IConfigurableItem
                    && ((IConfigurableItem) editingItem.getItem()).hasProfiles())
                collection.setGroupEnabled("TEXT_TAB", true);
            collection.setComponentEnabled("BACK_BUTTON", true);
            if (editingItem != null && editingItem.getItem() instanceof IInventoryTool)
                collection.setComponentEnabled("INVENTORY_BUTTON", true);
            if (collection.getComponent("BACK_BUTTON") != null) collection.getComponent("BACK_BUTTON").setY(26);
        } else if (level == 2) { // field screen
            collection.setOnlyGroupEnabled("FIELD_BUTTONS");
            collection.setGroupEnabled("BACKGROUND", true);
            collection.setComponentEnabled("BACK_BUTTON", true);
            if (collection.getComponent("BACK_BUTTON") != null) collection.getComponent("BACK_BUTTON").setY(3);
            // slot = -1;
        } else if (level == 3) { // inventory screen

        }
    }

    public void setFieldBeingEdited(ItemConfigField field) {
        ((ComponentFieldAdjuster) collection.getComponent("FIELD_CONFIG_BUTTON_ARRAY")).field = field;
        setLevel(2);
    }

    public void setEditingItem(ItemStack stack, int slot) {
        this.editingItem = stack;
        container.updateInventoryStack(slot);
        this.slot = slot;
    }

    @Override
    protected boolean checkHotbarKeys(int p_146983_1_) {
        return false;
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if (collection.getComponent("PROFILE_TEXT_FIELD") instanceof ComponentTextField
                && ((ComponentTextField) collection.getComponent("PROFILE_TEXT_FIELD")).isFocused()
                && par2 != 1) {
            collection.keyTyped(par1, par2);
            return;
        }
        super.keyTyped(par1, par2);
    }

    @Override
    public void componentCallBack(ComponentBase component) {
        if (component instanceof ComponentTextField && editingItem != null && slot != -1) {
            ComponentTextField textField = (ComponentTextField) component;
            if (!StringUtils.isNullOrEmpty(textField.textField.getText())) {
                DraconicEvolution.network.sendToServer(new ItemConfigPacket(slot, textField.textField.getText()));
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if (slot > -1) editingItem = player.inventory.getStackInSlot(slot);
        if (slot > -1 && editingItem != null && editingItem.getUnlocalizedName().toLowerCase().contains("bow")) {
            BowHandler.BowProperties properties = new BowHandler.BowProperties(editingItem, player);

            if (!properties.canFire() && properties.cantFireMessage != null
                    && !properties.cantFireMessage.equals("msg.de.outOfArrows.name")) {
                fontRendererObj.drawSplitString(
                        StatCollector.translateToLocal(properties.cantFireMessage),
                        0,
                        ySize + 5,
                        xSize,
                        0xFF0000);
            }

            List<String> list = new ArrayList<String>();
            list.add(
                    StatCollector.translateToLocal("gui.de.rfPerShot.txt") + ": "
                            + Utills.addCommas(properties.calculateEnergyCost()));
            list.add(
                    StatCollector.translateToLocal("gui.de.maxDamage.txt") + ": "
                            + properties.arrowDamage * (properties.arrowSpeed * 3));
            drawHoveringText(list, xSize - 8, 0, fontRendererObj);
        }
    }
}
