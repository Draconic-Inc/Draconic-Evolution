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

        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvern_helmet"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvern_body"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvern_right_arm"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvern_left_arm"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvern_belt"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvern_right_leg"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvern_left_leg"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvern_right_boot"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/wyvern_left_boot"));

        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconic_helmet"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconic_body"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconic_right_arm"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconic_left_arm"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconic_belt"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconic_right_leg"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconic_left_leg"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconic_right_boot"));
        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/draconic_left_boot"));

        textureMap.registerSprite(ResourceHelperDE.getResource("models/armor/shield_sphere"));

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
