package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.draconicevolution.api.itemconfig.*;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.ItemUpgradeRegistry;
import com.brandon3055.draconicevolution.api.itemupgrade.SimpleUpgrade;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.IDualModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField.EnumControlType.*;

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
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        registry.register(stack, new DoubleConfigField("Test1", 0, 0, 100, "This is a description!", PLUS1_MINUS1));
        registry.register(stack, new IntegerConfigField("Test2", 0, 0, 100, "This is a description!", PLUS2_MINUS2));
        registry.register(stack, new BooleanConfigField("Test3", false, "This is a description!"));
        registry.register(stack, new BooleanConfigField("Test4", true, "This is a description!"));
        registry.register(stack, new DoubleConfigField("Test5", 22.53, 0, 100, "This is a description!", PLUS3_MINUS3));
        registry.register(stack, new IntegerConfigField("Slide", 43, 25, 100, "This is a description!", SLIDER));
        registry.register(stack, new DoubleConfigField("Slide2", 10, 0, 3453, "This is a description!", SLIDER));
        registry.register(stack, new DoubleConfigField("Slide3", 0, 0, 1, "This is a description!", SLIDER).setExtension(" Some extension here"));
        registry.register(stack, new BooleanConfigField("Test7", false, "This is a description!"));
        registry.register(stack, new BooleanConfigField("Test8", true, "This is a description!"));
        registry.register(stack, new BooleanConfigField("Test9", true, "This is a description!"));
        registry.register(stack, new BooleanConfigField("Test10", true, "This is a description!").setOnOffTxt("Some On Text Here...", "Some Off Text Here..."));

        registry.register(stack, new BooleanConfigField("Test11", true, "This is a description!"));
        registry.register(stack, new BooleanConfigField("Test12", true, "This is a description!"));

        String s = "This is going to be a very long description that will allow me to test how well line wrapping works. Now... I have no idea what to add to this description to make it longer... Perhaps i could talk about my plans for the config system? Naa that would take too long... Not to mention i dont want to have to support hundred line descriptions... Oh hay... This should be long enough :P";

        registry.register(stack, new BooleanConfigField("Test13", true, s));
        registry.register(stack, new AOEConfigField("TestAOE", 1, 0, 50, s));
        registry.register(stack, new IntegerConfigField("TestSI", 43, 0, 150, "This is a description!", SELECTIONS));
        registry.register(stack, new IntegerConfigField("Test.", 43, 0, 150, s, PLUS3_MINUS3));
//        fieldMap.put("Test14", new BooleanConfigField("Test14", true));

//        fieldMap.put("Test15", new BooleanConfigField("Test11", true));
//        fieldMap.put("Test16", new BooleanConfigField("Test12", true));
//        fieldMap.put("Test17", new BooleanConfigField("Test13", true));
//        fieldMap.put("Test18", new BooleanConfigField("Test14", true));
//        fieldMap.put("Test19", new BooleanConfigField("Test11", true));
//        fieldMap.put("Test20", new BooleanConfigField("Test12", true));
//        fieldMap.put("Test21", new BooleanConfigField("Test13", true));
//        fieldMap.put("Test22", new BooleanConfigField("Test14", true));
        return registry;
    }


    //endregion

    //region Upgrade


    @Override
    public ItemUpgradeRegistry getValidUpgrades(ItemStack stack, ItemUpgradeRegistry upgradeRegistry) {
        upgradeRegistry.register(stack, new SimpleUpgrade(UpgradeHelper.RF_CAPACITY));
        return upgradeRegistry;
    }

    @Override
    public int getUpgradeCapacity(ItemStack stack) {
        return 3;
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
