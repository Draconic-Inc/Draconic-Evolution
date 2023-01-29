package com.brandon3055.draconicevolution.client.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.brandonscore.common.utills.DataUtills.XZPair;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.handler.ContributorHandler;
import com.brandon3055.draconicevolution.common.items.armor.CustomArmorHandler;
import com.brandon3055.draconicevolution.common.items.armor.DraconicArmor;
import com.brandon3055.draconicevolution.common.items.armor.WyvernArmor;
import com.brandon3055.draconicevolution.common.items.weapons.BowHandler;
import com.brandon3055.draconicevolution.common.items.weapons.DraconicBow;
import com.brandon3055.draconicevolution.common.items.weapons.WyvernBow;
import com.brandon3055.draconicevolution.common.network.MountUpdatePacket;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 28/10/2014.
 */
public class ClientEventHandler {

    public static Map<EntityPlayer, XZPair<Float, Integer>> playerShieldStatus = new HashMap<EntityPlayer, XZPair<Float, Integer>>();

    public static int elapsedTicks;
    private static float previousFOB = 0f;
    public static float previousSensitivity = 0;
    public static boolean bowZoom = false;
    public static boolean lastTickBowZoom = false;
    public static int tickSet = 0;
    public static ItemDisplayManager statusDisplayManager = new ItemDisplayManager(60);
    private static int remountTicksRemaining = 0;
    private static int remountEntityID = 0;
    public static float energyCrystalAlphaValue = 0f;
    public static float energyCrystalAlphaTarget = 0f;
    public static boolean playerHoldingWrench = false;
    public static Minecraft mc;
    private static Random rand = new Random();
    private static IModelCustom shieldSphere;

    public ClientEventHandler() {
        shieldSphere = AdvancedModelLoader.loadModel(ResourceHandler.getResource("models/shieldSphere.obj"));
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.type != TickEvent.Type.CLIENT || event.side != Side.CLIENT)
            return;

        for (Iterator<Map.Entry<EntityPlayer, XZPair<Float, Integer>>> i = playerShieldStatus.entrySet().iterator(); i
                .hasNext();) {
            Map.Entry<EntityPlayer, XZPair<Float, Integer>> entry = i.next();
            if (elapsedTicks - entry.getValue().getValue() > 5) i.remove();
        }

        if (mc == null) mc = Minecraft.getMinecraft();
        else if (mc.theWorld != null) {
            elapsedTicks++;
            HudHandler.clientTick();

            if (bowZoom && !lastTickBowZoom) {
                previousSensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
                Minecraft.getMinecraft().gameSettings.mouseSensitivity = previousSensitivity / 3;
            } else if (!bowZoom && lastTickBowZoom) {
                Minecraft.getMinecraft().gameSettings.mouseSensitivity = previousSensitivity;
            }

            lastTickBowZoom = bowZoom;
            if (elapsedTicks - tickSet > 10) bowZoom = false;

            if (energyCrystalAlphaValue < energyCrystalAlphaTarget) energyCrystalAlphaValue += 0.01f;
            if (energyCrystalAlphaValue > energyCrystalAlphaTarget) energyCrystalAlphaValue -= 0.01f;

            if (Math.abs(energyCrystalAlphaTarget - energyCrystalAlphaValue) <= 0.02f)
                energyCrystalAlphaTarget = rand.nextFloat();

            playerHoldingWrench = mc.thePlayer.getHeldItem() != null
                    && mc.thePlayer.getHeldItem().getItem() == ModItems.wrench;

            searchForPlayerMount();
        }

        statusDisplayManager.tick();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void renderGameOverlayEvent(final RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            statusDisplayManager.drawItemStack(event.resolution);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void fovUpdate(FOVUpdateEvent event) {

        // region Bow FOV Update
        if (event.entity.getHeldItem() != null
                && (event.entity.getHeldItem().getItem() instanceof WyvernBow
                        || event.entity.getHeldItem().getItem() instanceof DraconicBow)
                && Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed()) {

            BowHandler.BowProperties properties = new BowHandler.BowProperties(
                    event.entity.getHeldItem(),
                    event.entity);

            event.newfov = ((6 - properties.zoomModifier) / 6) * event.fov;

            // if (ItemNBTHelper.getString(event.entity.getItemInUse(), "mode", "").equals("sharpshooter")){
            // if (event.entity.getItemInUse().getItem() instanceof WyvernBow) zMax = 1.35f;
            // else if (event.entity.getItemInUse().getItem() instanceof DraconicBow) zMax = 2.5f;
            // bowZoom = true;
            // tickSet = elapsedTicks;
            // }

        }
        // endregion

        // region Armor move speed FOV effect cancellation
        CustomArmorHandler.ArmorSummery summery = new CustomArmorHandler.ArmorSummery().getSummery(event.entity);

        if (summery != null && summery.speedModifier > 0) {
            IAttributeInstance iattributeinstance = event.entity
                    .getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            float f = (float) ((iattributeinstance.getAttributeValue()
                    / (double) event.entity.capabilities.getWalkSpeed() + 1.0D) / 2.0D);
            event.newfov /= f;
        }

        // endregion
    }

    private void searchForPlayerMount() {
        if (remountTicksRemaining > 0) {
            Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(remountEntityID);
            if (e != null) {
                Minecraft.getMinecraft().thePlayer.mountEntity(e);
                LogHelper.info("Successfully placed player on mount after " + (500 - remountTicksRemaining) + " ticks");
                remountTicksRemaining = 0;
                return;
            }
            remountTicksRemaining--;
            if (remountTicksRemaining == 0) {
                LogHelper.error("Unable to locate player mount after 500 ticks! Aborting");
                DraconicEvolution.network.sendToServer(new MountUpdatePacket(-1));
            }
        }
    }

    public static void tryRepositionPlayerOnMount(int id) {
        if (remountTicksRemaining == 500) return;
        remountTicksRemaining = 500;
        remountEntityID = id;
        LogHelper.info("Started checking for player mount");
    }

    @SubscribeEvent
    public void renderPlayerEvent(RenderPlayerEvent.Specials.Post event) {
        ContributorHandler.render(event);
    }

    @SubscribeEvent
    public void renderArmorEvent(RenderPlayerEvent.SetArmorModel event) {
        if (ConfigHandler.useOriginal3DArmorModel || ConfigHandler.useOldArmorModel || event.isCanceled()) return;
        if (event.stack != null
                && (event.stack.getItem() instanceof DraconicArmor || event.stack.getItem() instanceof WyvernArmor)) {
            ItemArmor itemarmor = (ItemArmor) event.stack.getItem();
            ModelBiped modelbiped = itemarmor.getArmorModel(event.entityPlayer, event.stack, event.slot);
            event.renderer.setRenderPassModel(modelbiped);
            modelbiped.onGround = event.renderer.modelBipedMain.onGround;
            modelbiped.isRiding = event.renderer.modelBipedMain.isRiding;
            modelbiped.isChild = event.renderer.modelBipedMain.isChild;
            event.result = 1;
        }
    }

    @SubscribeEvent
    public void renderPlayerEvent(RenderPlayerEvent.Post event) {
        if (playerShieldStatus.containsKey(event.entityPlayer)) {
            GL11.glPushMatrix();
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);
            ResourceHandler.bindResource("textures/models/shieldSphere.png");

            float p = playerShieldStatus.get(event.entityPlayer).getKey();

            EntityPlayer viewingPlayer = Minecraft.getMinecraft().thePlayer;

            int i = 5 - (elapsedTicks - playerShieldStatus.get(event.entityPlayer).getValue());

            GL11.glColor4f(1F - p, 0F, p, i / 5F);

            if (viewingPlayer != event.entityPlayer) {
                double translationXLT = event.entityPlayer.prevPosX - viewingPlayer.prevPosX;
                double translationYLT = event.entityPlayer.prevPosY - viewingPlayer.prevPosY;
                double translationZLT = event.entityPlayer.prevPosZ - viewingPlayer.prevPosZ;

                double translationX = translationXLT
                        + (((event.entityPlayer.posX - viewingPlayer.posX) - translationXLT) * event.partialRenderTick);
                double translationY = translationYLT
                        + (((event.entityPlayer.posY - viewingPlayer.posY) - translationYLT) * event.partialRenderTick);
                double translationZ = translationZLT
                        + (((event.entityPlayer.posZ - viewingPlayer.posZ) - translationZLT) * event.partialRenderTick);

                GL11.glTranslated(translationX, translationY + 1.1, translationZ);
            } else {
                GL11.glTranslated(0, -0.5, 0);
            }

            GL11.glScaled(1, 1.5, 1);

            shieldSphere.renderAll();

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }
    }
}
