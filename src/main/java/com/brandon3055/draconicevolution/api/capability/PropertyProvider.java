package com.brandon3055.draconicevolution.api.capability;

import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by brandon3055 on 2/5/20.
 * This capability forms the base of the DE item configuration system.
 * Note any item implementing this MUST also implement the share tag read and write functions from {@link DECapabilities} Or something similar.
 * @see DECapabilities#writeToShareTag(ItemStack, CompoundNBT)
 * @see DECapabilities#readFromShareTag(ItemStack, CompoundNBT)
 */
public interface PropertyProvider extends INBTSerializable<CompoundNBT> {

    /**
     * This must be a completely unique id that can be used to identify this specific property provider.
     *
     * @return the unique id for this property provider.
     */
    UUID getProviderID();

    /**
     * This should be unique to this "type" of provider. (type in most cases refers to the item this provider belongs to)<br>
     * e.g. staff_of_power<br><br>
     * This should ignore the item tier and only focus on the item type. So for example all pickaxes should for example use "de_pickaxe"<br>
     * This way when the user upgrades an item they dont need to reconfigure their config screen.<br><br>
     *
     * @return the name of this property provider.
     */
    String getProviderName();

    /**
     * In the event there are somehow multiple providers with the same ID (possibly due to creative duplication or some otehr means of stack duplication)
     * This will be called on all but one of the duplicate providers in order to generate new unique id's.
     */
    void regenProviderID();

    Collection<ConfigProperty> getProperties();

    /**
     * Retrieves a property with the specified name if one exists.
     *
     * @param propertyName the name of the property to retrieve.
     */
    @Nullable
    ConfigProperty getProperty(String propertyName);
}
