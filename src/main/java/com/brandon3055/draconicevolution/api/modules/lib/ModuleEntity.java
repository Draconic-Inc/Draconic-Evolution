package com.brandon3055.draconicevolution.api.modules.lib;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.render.RenderTypes;
import com.brandon3055.draconicevolution.init.ClientInit;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 18/4/20.
 */
public class ModuleEntity<T extends ModuleData<T>> {

    protected final Module<T> module;
    protected ModuleHost host;
    protected Map<String, ConfigProperty> propertyMap = new HashMap<>();
    protected boolean savePropertiesToItem = false;
    protected int gridX;
    protected int gridY;

    public ModuleEntity(Module<T> module) {
        this.module = module;
    }

    public void setHost(ModuleHost host) {
        this.host = host;
    }

    public void tick(ModuleContext context) {

    }

    /**
     * Called when this module is installed into a module host
     * After readFromItemStack
     */
    public void onInstalled(ModuleContext context) {

    }

    /**
     * Called when this module is removed from a module host
     * After writeToItemStack
     */
    public void onRemoved(ModuleContext context) {

    }

    /**
     * This can be used to add per module properties.
     * Properties should be added via your {@link ModuleEntity} constructor. These will be saved and loaded along
     * with the rest of the entities data. If you set 'savePropertiesToItem' to true the properties
     * will also be saved and loaded from the ItemStack when this module is removed or installed.<br><br>
     * <p>
     * These properties will be generated for every single instance of this module that is installed.
     * Therefor these are best used with modules that have a max install count of 1.<br><br>
     * <p>
     * If you need to add "global" where you have a single set of properties that apply to all installed modules
     * rather than a set of properties for each module see {@link ModuleType#getTypeProperties(ModuleData, Map)} )}
     *
     * @param property the property to add.
     * @return the property for convenience.
     * @see ModuleType#getTypeProperties(ModuleData, Map)
     */
    public ConfigProperty addProperty(ConfigProperty property) {
        propertyMap.put(property.getName(), property);
        property.generateUnique();
        return property;
    }

    public Collection<ConfigProperty> getEntityProperties() {
        return propertyMap.values();
    }

    /**
     * This method allows this module entities to apply attribute modifiers to the items they are installed in.
     *
     * @param slot  the equipment slot that the item containing this module is in.
     * @param stack The ItemStack containing this module/modules
     * @param map   The map to which the modifiers must be added.
     */
    public void getAttributeModifiers(EquipmentSlot slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map) {

    }

    /**
     * This is called whenever the module grid changes.
     * This allows you to cache module data as long as you clear said cache when this is called.
     */
    public void clearCaches() {}

//    /**
//     * If you are using {@link #getAttributeModifiers(EquipmentSlotType, ItemStack, Multimap)} to add custom attributes you MUST also
//     * implement this method and add all of your attribute id's to the provided list. This list is used to refresh or remove attributes added by modules.
//     * @param list the list to which you must add your attribute id's
//     */
//    public void getAttributeIDs(List<UUID> list) {}

    public void writeToNBT(CompoundTag compound) {
        compound.putByte("x", (byte) gridX);
        compound.putByte("y", (byte) gridY);
        if (!propertyMap.isEmpty()) {
            CompoundTag properties = new CompoundTag();
            propertyMap.forEach((name, property) -> properties.put(name, property.serializeNBT()));
            compound.put("properties", properties);
        }
        writeExtraData(compound);
    }

    public void readFromNBT(CompoundTag compound) {
        gridX = compound.getByte("x");
        gridY = compound.getByte("y");
        CompoundTag properties = compound.getCompound("properties");
        propertyMap.forEach((name, property) -> property.deserializeNBT(properties.getCompound(name)));
        readExtraData(compound);
    }

    /**
     * Called when the module is about to be removed from the module grid.
     * This allows you to store data on the module item stack.<br>
     * Note: there is no guarantee the module will actually be removed at this point so do not modify the context.
     *
     * @param stack The module stack
     */
    public void writeToItemStack(ItemStack stack, ModuleContext context) {
        if (savePropertiesToItem && !propertyMap.isEmpty()) {
            CompoundTag properties = stack.getOrCreateTagElement("properties");
            propertyMap.forEach((name, property) -> properties.put(name, property.serializeNBT()));
        }
        if (stack.hasTag()) {
            writeExtraData(stack.getTag());
        } else {
            CompoundTag tag = new CompoundTag();
            writeExtraData(tag);
            if (!tag.isEmpty()) {
                stack.setTag(tag);
            }
        }
    }

    /**
     * Called when a module is about to be inserted into a module grid.
     * This allows you to load any previously saved data.<br>
     * Note: there is no guarantee the module will actually be installed at this point so do not modify the context.
     *
     * @param stack The module stack
     * @see #writeToItemStack(ItemStack, ModuleContext)
     */
    public void readFromItemStack(ItemStack stack, ModuleContext context) {
        CompoundTag properties;
        if (savePropertiesToItem && (properties = stack.getTagElement("properties")) != null) {
            propertyMap.forEach((name, property) -> property.deserializeNBT(properties.getCompound(name)));
        }
        if (stack.hasTag()) {
            readExtraData(stack.getTag());
        }
    }

    /**
     * Convenient method for storage extra data both when installed in a host
     * and when in item form.
     *
     * @param nbt The tag to write your data to. Keep in mind this will be the raw CompoundTag from writeToNBT or the raw ItemStack tag
     * @return the nbt tag that was passed in.
     */
    protected CompoundTag writeExtraData(CompoundTag nbt) {
        return nbt;
    }

    /**
     * Read stored data from item or when loaded in a host.
     */
    protected void readExtraData(CompoundTag nbt) {
    }

    //region Setters / Getters

    public Module<T> getModule() {
        return module;
    }

    public void setPos(int gridX, int gridY) {
        setGridX(gridX);
        setGridY(gridY);
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public int getMaxGridX() {
        return gridX + module.getProperties().getWidth();
    }

    public int getMaxGridY() {
        return gridY + module.getProperties().getHeight();
    }

    public int getWidth() {
        return module.getProperties().getWidth();
    }

    public int getHeight() {
        return module.getProperties().getHeight();
    }

    //end

    @OnlyIn(Dist.CLIENT)
    public void renderModule(GuiElement<?> parent, MultiBufferSource getter, PoseStack poseStack, int x, int y, int width, int height, double mouseX, double mouseY, boolean stackRender, float partialTicks) {
        if (stackRender) {
            poseStack.translate(0, 0, 210);
        }
        int colour = getModuleColour(module);
        GuiHelper.drawRect(getter, poseStack, x, y, width, height, colour);
        GuiHelper.drawBorderedRect(getter, poseStack, x, y, width, height, 1, colour, GuiHelper.mixColours(colour, 0x20202000, true));

        if (module.getProperties().getTechLevel() == TechLevel.CHAOTIC) {
            VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(RenderType.glint()), poseStack);
            builder.vertex(x, y + height, 0).uv(0, ((float) height / width) / 64F).endVertex();
            builder.vertex(x + width, y + height, 0).uv(((float) width / height) / 64F, ((float) height / width) / 64F).endVertex();
            builder.vertex(x + width, y, 0).uv(((float) width / height) / 64F, 0).endVertex();
            builder.vertex(x, y, 0).uv(0, 0).endVertex();
            RenderUtils.endBatch(getter);
        }

        TextureAtlasSprite sprite = ClientInit.moduleSpriteUploader.getSprite(module);
        float ar = (float) sprite.getWidth() / (float) sprite.getHeight();
        float iar = (float) sprite.getHeight() / (float) sprite.getWidth();

        VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(RenderTypes.MODULE_TYPE), poseStack);
        if (iar * width <= height) { //Fit Width
            double h = width * iar;
            GuiHelper.drawSprite(builder, x, y + (height / 2D) - (h / 2D), width, h, sprite);
        } else { //Fit height
            double w = height * ar;
            GuiHelper.drawSprite(builder, x + (width / 2D) - (w / 2D), y, w, height, sprite);
        }

        //Hover highlight
        if (stackRender) {
            poseStack.translate(0, 0, -210);
        } else if (GuiHelper.isInRect(x, y, width, height, mouseX, mouseY)) {
            GuiHelper.drawRect(getter, poseStack, x, y, width, height, 0x50FFFFFF);
        }
    }

    /**
     * This should be used primarily for things like rendering tool tips.
     * This render method may be blocked by other overlay rendering so don't count on it to always get called.
     *
     * @return true to block further overlay rendering. (Equivalent to returning true in {@link com.brandon3055.brandonscore.client.gui.modulargui.GuiElement#renderOverlayLayer(Minecraft, int, int, float)} )
     */
    @OnlyIn(Dist.CLIENT)
    public boolean renderModuleOverlay(GuiElement<?> parent, ModuleContext context, MultiBufferSource getter, PoseStack poseStack, int x, int y, int width, int height, double mouseX, double mouseY, float partialTicks, int hoverTicks) {
        if (hoverTicks > 10) {
            Minecraft mc = Minecraft.getInstance();
            Item item = getModule().getItem();
            ItemStack stack = new ItemStack(item);
            writeToItemStack(stack, context);
            List<Component> list = stack.getTooltipLines(mc.player, mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
            ;
            parent.getScreen().renderTooltip(poseStack, list, Optional.empty(), (int) mouseX, (int) mouseY);
            return true;
        }
        return false;
    }

    /**
     * Use to add information to the module item's tool tip.
     * This also shows when hovering over the module in the grid because bu default the grid just
     * renders the item tooltip.
     *
     * @param list The list to which tool tip entries should be added
     */
    public void addToolTip(List<Component> list) {}

    /**
     * Use this method to display information in the tooltip of the {@link com.brandon3055.draconicevolution.items.equipment.IModularItem} this module is installed in.
     * This will be added directly after the '[Modular Item]' text and before {@link ModuleData#addHostHoverText}.
     * The recommended implementation is to display this information when the shift key is pressed.
     * <p>
     * For a {@link ModuleData} based implementation see {@link ModuleData#addHostHoverText(ItemStack, Level, List, TooltipFlag)}
     *
     * @param stack   The modular item this tool tip will be added to
     * @param level   The current level
     * @param tooltip The tooltip list
     */
    @OnlyIn(Dist.CLIENT)
    public void addHostHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {}

    /**
     * Called client side when a module in the grid is clicked. This is called before the normal container click.
     * Returning true will prevent the normal server/client container click interaction from occurring.
     *
     * @param parent The parent gui element (Should be a {@link com.brandon3055.draconicevolution.client.gui.ModuleGridRenderer})
     * @param player The player.
     * @param x      Module gui X position
     * @param y      Module gui Y position
     * @param width  Module width
     * @param height Module height
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button The mouse button pressed.
     * @return true to prevent further click procxessing.
     */
    public boolean clientModuleClicked(GuiElement<?> parent, Player player, int x, int y, int width, int height, double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Called when a module in the grid is clicked. Can be used to add module interactions.
     * Return true to disable the default click action (prevent module pickup)
     * Will be called both client and server. The return value must be the same for both sides.
     *
     * @param player    The player.
     * @param x         The x position of the click within the module. 0->1 where 0 is the far left of the module and 1 is the far right.
     * @param y         The y position of the click within the module. 0->1 where 0 is the top of the module and 1 is the bottom.
     * @param button    The mouse button pressed.
     * @param clickType The click type.
     * @return true to prevent module pickup from slot. (Returning different values for client and server may result in desync)
     */
    public boolean moduleClicked(Player player, double x, double y, int button, ClickType clickType) {
        return false;
    }

    //region Helpers

    public boolean checkPos(int gridX, int gridY) {
        return this.gridX == gridX && this.gridY == gridY;
    }

    public boolean isPosValid(int gridWidth, int gridHeight) {
        return gridX >= 0 && gridY >= 0 && getMaxGridX() < gridWidth && getMaxGridY() < gridHeight;
    }

    /**
     * Returns true if the specified grid coordinates are within this modules grid bounds.
     *
     * @param gridX text x position
     * @param gridY text y position
     * @return true if the specified position is within this modules grid bounds.
     */
    public boolean contains(int gridX, int gridY) {
        return gridX >= this.gridX && gridX < this.gridX + module.getProperties().getWidth() && gridY >= this.gridY && gridY < this.gridY + module.getProperties().getHeight();
    }

    /**
     * Returns true if any part of this module overlaps with any part of the other module.
     */
    public boolean intersects(ModuleEntity other) {
        int tw = getWidth();
        int th = getHeight();
        int rw = other.getWidth();
        int rh = other.getHeight();
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = gridX;
        int ty = gridY;
        int rx = other.gridX;
        int ry = other.gridY;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }

    protected int getModuleColour(Module<?> module) {
        return switch (module.getProperties().getTechLevel()) {
            case DRACONIUM -> 0xff1e4596;
            case WYVERN -> 0xFF3c1551;
            case DRACONIC -> 0xFFcb2a00;
            case CHAOTIC -> 0xFF111111;
        };
    }

    @Override
    public String toString() {
        return "ModuleEntity{" +
                "module=" + module.getRegistryName() +
                ", gridX=" + gridX +
                ", gridY=" + gridY +
                '}';
    }

    /**
     * Send a message to the server side ModuleEntity.
     * Handle the message using {@link #handleClientMessage(MCDataInput)}
     *
     * @param dataConsumer The data consumer
     */
    public void sendMessageToServer(Consumer<MCDataOutput> dataConsumer) {
        DraconicNetwork.sendModuleMessage(getGridX(), getGridY(), dataConsumer);
    }

    /**
     * Handle a message sent from the client side module entity.
     * Send message using {@link #sendMessageToServer(Consumer)}
     *
     * @param input The message data
     */
    public void handleClientMessage(MCDataInput input) {

    }
}
