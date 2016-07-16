package com.brandon3055.draconicevolution.client.model;

import codechicken.lib.render.TextureUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.client.renderer.texture.TextureMap;

/**
 * Created by brandon3055 on 8/07/2016.
 */
public class ArmorModelHelper implements TextureUtils.IIconRegister {

    @Override
    public void registerIcons(TextureMap textureMap) {

        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvernHelmet"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvernBody"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvernRightArm"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvernLeftArm"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvernBelt"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvernRightLeg"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvernLeftLeg"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvernRightBoot"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvernLeftBoot"));

        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconicHelmet"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconicBody"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconicRightArm"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconicLeftArm"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconicBelt"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconicRightLeg"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconicLeftLeg"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconicRightBoot"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconicLeftBoot"));

        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/shieldSphere"));

        DEFeatures.draconicHelm.model = null;
        DEFeatures.draconicChest.model = null;
        DEFeatures.draconicLegs.model = null;
        DEFeatures.draconicBoots.model = null;

        DEFeatures.wyvernHelm.model = null;
        DEFeatures.wyvernChest.model = null;
        DEFeatures.wyvernLegs.model = null;
        DEFeatures.wyvernBoots.model = null;

        ClientEventHandler.shieldModel = null;
    }
}
