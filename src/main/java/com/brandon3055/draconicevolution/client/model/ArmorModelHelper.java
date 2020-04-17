package com.brandon3055.draconicevolution.client.model;

import codechicken.lib.texture.AtlasRegistrar;
import codechicken.lib.texture.IIconRegister;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;

/**
 * Created by brandon3055 on 8/07/2016.
 */
public class ArmorModelHelper implements IIconRegister {

    @Override
    public void registerIcons(AtlasRegistrar textureMap) {

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

//        DEContent.draconicHelm.model = null;
//        DEContent.draconicChest.model = null;
//        DEContent.draconicLegs.model = null;
//        DEContent.draconicBoots.model = null;
//
//        DEContent.wyvernHelm.model = null;
//        DEContent.wyvernChest.model = null;
//        DEContent.wyvernLegs.model = null;
//        DEContent.wyvernBoots.model = null;

        ClientEventHandler.shieldModel = null;
    }
}
