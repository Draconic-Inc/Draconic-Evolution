package com.brandon3055.draconicevolution.client.render.item;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderMobSoul implements IItemRenderer {
    private Minecraft mc;
    private Entity randomEntity = null;
    private String[] randomEntitys = new String[] {
        "Pig",
        "Sheep",
        "Enderman",
        "Zombie",
        "Creeper",
        "Cow",
        "Chicken",
        "Ozelot",
        "Witch",
        "Wolf",
        "MushroomCow",
        "Squid",
        "EntityHorse",
        "Spider",
        "Skeleton",
        "Blaze",
        "Bat",
        "Villager",
        "Silverfish"
    };

    public RenderMobSoul() {
        this.mc = Minecraft.getMinecraft();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED
                || type == ItemRenderType.EQUIPPED_FIRST_PERSON
                || type == ItemRenderType.INVENTORY
                || type == ItemRenderType.ENTITY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Entity mob = EntityList.createEntityByName(ItemNBTHelper.getString(item, "Name", "Pig"), mc.theWorld);
        randomEntity = EntityList.createEntityByName(
                randomEntitys[(int) ((Minecraft.getSystemTime() / 1000) % 18)], mc.theWorld);
        if (ItemNBTHelper.getString(item, "Name", "Pig").equals("Any")) mob = randomEntity;
        if (mob instanceof EntitySkeleton)
            ((EntitySkeleton) mob).setSkeletonType(ItemNBTHelper.getInteger(item, "SkeletonType", 0));

        if (mob == null) {
            LogHelper.error("Invalid Mob Name:" + ItemNBTHelper.getString(item, "Name", "Pig"));
            mob = EntityList.createEntityByName("Pig", mc.theWorld);
        }
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glScalef(0.5F, 0.5F, 0.5F);

        if (type == ItemRenderType.INVENTORY) {
            GL11.glPushMatrix();
            GL11.glScalef(13F, 13F, 13F);
            GL11.glTranslated(1.2, 2.2, 0);
            GL11.glRotatef(180F, 1F, 0F, 0F);
            GL11.glRotatef(mc.getSystemTime() / -10, 0F, 1F, 0F);
            GL11.glRotatef(-20F, 1F, 0F, 0F);
            RenderManager.instance.renderEntityWithPosYaw(mob, 0, 0, 0, 0F, 1F);
            GL11.glPopMatrix();
        } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glPushMatrix();
            GL11.glScalef(0.8F, 0.8F, 0.8F);
            GL11.glTranslated(2, 0.5, 0);
            GL11.glRotatef(20F, 0F, 0F, 1F);
            GL11.glRotatef(mc.getSystemTime() / -10, 0F, 1F, 0F);
            GL11.glRotatef(-20F, 1F, 0F, 0F);
            RenderManager.instance.renderEntityWithPosYaw(mob, 0, 0, 0, 0F, 1F);
            GL11.glPopMatrix();
        } else if (type == ItemRenderType.EQUIPPED) {
            GL11.glPushMatrix();
            GL11.glScalef(0.8F, 0.8F, 0.8F);
            GL11.glTranslated(1, 0.5, 0);
            GL11.glRotatef(20F, 0F, 0F, 1F);
            GL11.glRotatef(mc.getSystemTime() / -10, 0F, 1F, 0F);
            GL11.glRotatef(-20F, 1F, 0F, 0F);
            RenderManager.instance.renderEntityWithPosYaw(mob, 0, 0, 0, 0F, 1F);
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            GL11.glRotatef(mc.getSystemTime() / -10, 0F, 1F, 0F);
            GL11.glRotatef(-20F, 1F, 0F, 0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderManager.instance.renderEntityWithPosYaw(mob, 0, 0, 0, 0F, 1F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
