package com.brandon3055.draconicevolution.api.modules;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.properties.ModuleProperties;
import com.brandon3055.draconicevolution.api.modules.lib.InstallResult;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 and covers1624 on 4/16/20.
 */
public interface IModule<P extends ModuleProperties<P>> extends IForgeRegistryEntry<IModule<?>> {

    ModuleType<P> getModuleType();

    P getProperties();

    /**
     * This is just a convenience method. This should always return the same tech level as defined by the {@link ModuleProperties}
     */
    default TechLevel getModuleTechLevel() {
        return getProperties().getTechLevel();
    }

    Item getItem();

//    /**
//     * Grid textures will be uses when rendering the module in a module grid. <br/>
//     * Grid texture for a module must be located at:<br/>
//     * [modid]:textures/module/[module registry name].png<br/><br/>
//     * So the texture for module:<br/>
//     * draconicevolution:wyvern_shield<br/>
//     * Would be stored at:<br/>
//     * draconicevolution:textures/module/wyvern_shield.png<br/><br/><br/>
//     * If this returns false then the module item model will be used instead.
//     *
//     * @return true if this module has a custom grid texture.
//     */
//    default boolean hasGridTexture() {
//        return true;
//    }

//    /**
//     * Use this to set a custom grid texture for this module. This will be uses when rendering the module in a module grid.
//     * Returning null means the item model will be rendered instead.
//     * @return the icon texture for this module.
//     */
//    @Nullable
//    default ResourceLocation getGridTexture() {
//        return null;
//    }

//    /**
//     * If using a custom grid texture that is not a square texture this must be used to specify the aspect ratio of the texture.
//     * This is simply the texture width divided by the texture height.
//     *
//     * @return grid texture width divided by grid texture height.
//     */
//    default float getGridTextureAspect() {
//        return 1;
//    }


    /**
     * This allows you to prevent this module from being installed along side any other specific module.
     *
     * @param otherModule Other module.
     * @return pass with null value if this module can coexist with the other module.
     * Otherwise return fail with an ITextTranslation specifying a reason that can be displayed to the player.
     */
    default InstallResult areModulesCompatible(IModule<?> otherModule) {
        return getModuleType().areModulesCompatible(this, otherModule);
    }

    /**
     * @return The maximum number of modules of this type that can be installed (-1 = no limit)
     */
    default int maxInstallable() {
        return getModuleType().maxInstallable();
    }

    default void addInformation(List<ITextComponent> toolTip) {
        getProperties().addStats(toolTip, getModuleType());

        if (maxInstallable() != -1) {
            toolTip.add(new TranslationTextComponent("module.de.max_installable") //
                    .applyTextStyle(TextFormatting.GRAY) //
                    .appendSibling(new StringTextComponent(" ") //
                            .appendSibling(new StringTextComponent(String.valueOf(maxInstallable())) //
                                    .applyTextStyle(TextFormatting.DARK_GREEN))));
        }
    }
}
