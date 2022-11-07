package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        addProperty(new IntegerProperty("testModEntProp", 0).range(0, 100));
//        savePropertiesToItem = true;
    }

    public void setHost(ModuleHost host) {
        this.host = host;
    }

    public void tick(ModuleContext context) {

    }

    /**
     * Called when this module is installed into a module host
     * After readFromItemStack
     * */
    public void onInstalled(ModuleContext context) {

    }

    /**
     * Called when this module is removed from a module host
     * After writeToItemStack
     * */
    public void onRemoved(ModuleContext context) {

    }

    /**
     * This can be used to add per module properties.
     * Properties should be added via your {@link ModuleEntity} constructor. These will be saved and loaded along
     * with the rest of the entities data. If you set 'savePropertiesToItem' to true the properties
     * will also be saved and loaded from the ItemStack when this module is removed or installed.<br><br>
     *
     * These properties will be generated for every single instance of this module that is installed.
     * Therefor these are best used with modules that have a max install count of 1.<br><br>
     *
     * If you need to add "global" where you have a single set of properties that apply to all installed modules
     * rather than a set of properties for each module see {@link ModuleType#getTypeProperties(ModuleData, Map)} )}
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
     * @param slot the equipment slot that the item containing this module is in.
     * @param stack The ItemStack containing this module/modules
     * @param map The map to which the modifiers must be added.
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
    }

    public void readFromNBT(CompoundTag compound) {
        gridX = compound.getByte("x");
        gridY = compound.getByte("y");
        CompoundTag properties = compound.getCompound("properties");
        propertyMap.forEach((name, property) -> property.deserializeNBT(properties.getCompound(name)));
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
    public void renderSlotOverlay(MultiBufferSource getter, Minecraft mc, int x, int y, int width, int height, double mouseX, double mouseY, boolean mouseOver, float partialTicks) {

    }

    /**
     * Use to add information to the module item's tool tip.
     *
     * @param list The list to which tool tip entries should be added
     */
    public void addToolTip(List<Component> list) {}

    /**
     * Use this method to display information in the tooltip of the {@link com.brandon3055.draconicevolution.items.equipment.IModularItem} this module is installed in.
     * This will be added directly after the '[Modular Item]' text and before {@link ModuleData#addHostHoverText}.
     * The recommended implementation is to display this information when the shift key is pressed.
     *
     * For a {@link ModuleData} based implementation see {@link ModuleData#addHostHoverText(ItemStack, Level, List, TooltipFlag)}
     *
     * @param stack The modular item this tool tip will be added to
     * @param level The current level
     * @param tooltip The tooltip list
     */
    @OnlyIn(Dist.CLIENT)
    public void addHostHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {}

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

    @Override
    public String toString() {
        return "ModuleEntity{" +
                "module=" + module.getRegistryName() +
                ", gridX=" + gridX +
                ", gridY=" + gridY +
                '}';
    }
}
