package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.draconicevolution.api.itemconfig.*;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgrade;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeRegistry;
import com.brandon3055.draconicevolution.client.model.IDualModel;
import com.brandon3055.draconicevolution.items.tools.util.AOEConfigField;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class ToolBase extends ItemEnergyBase implements ICustomRender, IDualModel, IUpgradableItem, IConfigurableItem {


    //region Config

    @Override
    public int getProfileCount(ItemStack stack) {
        return 5;
    }

    @Override
    public Map<String, IItemConfigField> getFields(ItemStack stack, Map<String, IItemConfigField> fieldMap) {
        fieldMap.put("Test1", new DoubleConfigField("Test1", 0, 0, 100, "This is a description!"));
        fieldMap.put("Test2", new IntegerConfigField("Test2", 0, 0, 100, "This is a description!"));
        fieldMap.put("Test3", new BooleanConfigField("Test3", false, "This is a description!"));
        fieldMap.put("Test4", new BooleanConfigField("Test4", true, "This is a description!"));
        fieldMap.put("Test5", new DoubleConfigField("Test5", 22.53, 0, 100, "This is a description!"));
        fieldMap.put("Test6", new IntegerConfigField("Test6", 43, 0, 100, "This is a description!"));
        fieldMap.put("Test7", new BooleanConfigField("Test7", false, "This is a description!"));
        fieldMap.put("Test8", new BooleanConfigField("Test8", true, "This is a description!"));
        fieldMap.put("Test9", new BooleanConfigField("Test9", true, "This is a description!"));
        fieldMap.put("Test10", new BooleanConfigField("Test10", true, "This is a description!"));

        fieldMap.put("Test11", new BooleanConfigField("Test11", true, "This is a description!"));
        fieldMap.put("Test12", new BooleanConfigField("Test12", true, "This is a description!"));

        String s = "This is going to be a very long description that will allow me to test how well line wrapping works. Now... I have no idea what to add to this description to make it longer... Perhaps i could talk about my plans for the config system? Naa that would take too long... Not to mention i dont want to have to support hundred line descriptions... Oh hay... This should be long enough :P";

        fieldMap.put("Test13", new BooleanConfigField("Test13", true, s));
        fieldMap.put("TestAOE", new AOEConfigField("TestAOE", 1, 0, 4, s));
//        fieldMap.put("Test14", new BooleanConfigField("Test14", true));

//        fieldMap.put("Test15", new BooleanConfigField("Test11", true));
//        fieldMap.put("Test16", new BooleanConfigField("Test12", true));
//        fieldMap.put("Test17", new BooleanConfigField("Test13", true));
//        fieldMap.put("Test18", new BooleanConfigField("Test14", true));
//        fieldMap.put("Test19", new BooleanConfigField("Test11", true));
//        fieldMap.put("Test20", new BooleanConfigField("Test12", true));
//        fieldMap.put("Test21", new BooleanConfigField("Test13", true));
//        fieldMap.put("Test22", new BooleanConfigField("Test14", true));
        return fieldMap;
    }


    //endregion

    //region Upgrade


    @Override
    public Map<IUpgrade, Integer> getValidUpgrades(ItemStack stack, Map<IUpgrade, Integer> upgrades) {
        upgrades.put(UpgradeRegistry.RF_CAPACITY, -1);
        return upgrades;
    }

    @Override
    public int getUpgradeSlots(ItemStack stack) {
        return 3;
    }

    @Override
    public int getMaxUpgradeTier(ItemStack stack) {
        return 1;
    }

    //endregion

    //region Custom Item Rendering

    public ModelResourceLocation modelLocation;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        modelLocation = new ModelResourceLocation("draconicevolution:"+feature.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, modelLocation);
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    @Override
    public ModelResourceLocation getModelLocation() {
        return modelLocation;
    }

    //endregion
}
