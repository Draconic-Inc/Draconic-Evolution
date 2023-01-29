package com.brandon3055.draconicevolution.common.magic;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Created by Brandon on 10/11/2014.
 */
public class PotionHandler {

    public static void init() {}

    public static class PotionBase extends Potion {

        public PotionBase(int id, boolean isBad, int colour) {
            super(id, isBad, colour);
        }

        @Override
        public boolean shouldRenderInvText(PotionEffect effect) {
            return false;
        }

        @Override
        public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
            // mc.fontRenderer.drawString(StatCollector.translateToLocal("magic.de.effect.txt"), x+7, y+6, 0xffffff);
            // if (effect.getPotionID() == potionFlight.id)
            // mc.fontRenderer.drawString(StatCollector.translateToLocal("magic.de.flight.txt"), x+7, y+17,
            // 0x00ff00);
            // else if (effect.getPotionID() == potionUpHillStep.id)
            // mc.fontRenderer.drawString(StatCollector.translateToLocal("magic.de.highStep.txt"), x+7, y+17,
            // 0x00ff00);
            // else if (effect.getPotionID() == potionSpeed.id)
            // mc.fontRenderer.drawString(StatCollector.translateToLocal("magic.de.swiftness.txt"), x+7, y+17,
            // 0x00ff00);
            // else if (effect.getPotionID() == potionFireResist.id)
            // mc.fontRenderer.drawString(StatCollector.translateToLocal("magic.de.fireImmune.txt"), x+7, y+17,
            // 0x00ff00);
            // else if (effect.getPotionID() == potionJumpBoost.id)
            // mc.fontRenderer.drawString(StatCollector.translateToLocal("magic.de.jumpBoost.txt"), x+7, y+17,
            // 0x00ff00);
            // if (effect.getAmplifier() > 0)mc.fontRenderer.drawString("lvl: "+(effect.getAmplifier()+1), x+86, y+12,
            // 0xff0000);
        }
    }
}
