package com.brandon3055.draconicevolution.client.render.shaders;

import codechicken.lib.texture.SpriteSheetManager;
import codechicken.lib.texture.TextureFX;
import net.minecraft.client.shader.Framebuffer;

/**
 * Created by brandon3055 on 21/11/2016.
 */
public class CrystalTexture extends TextureFX {

    private Framebuffer framebuffer;

    public CrystalTexture(int spriteIndex, SpriteSheetManager.SpriteSheet sheet) {
        super(spriteIndex, sheet);
    }

    public CrystalTexture(int size, String name) {
        super(size, name);
        this.framebuffer = new Framebuffer(size, size, false);
    }

    @Override
    public void onTick() {
        framebuffer.bindFramebufferTexture();




//        imageData = framebuffer.
    }
}
