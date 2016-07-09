package com.brandon3055.draconicevolution.client.handler;


import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.brandonscore.utils.DataUtils.XZPair;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.items.armor.DraconicArmor;
import com.brandon3055.draconicevolution.items.armor.WyvernArmor;
import com.brandon3055.draconicevolution.items.tools.MiningToolBase;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.util.*;

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
    private static int remountTicksRemaining = 0;
    private static int remountEntityID = 0;
    public static float energyCrystalAlphaValue = 0f;
    public static float energyCrystalAlphaTarget = 0f;
    public static boolean playerHoldingWrench = false;
    public static Minecraft mc;
    private static Random rand = new Random();
//	private static IModelCustom shieldSphere;

    public ClientEventHandler() {
//		shieldSphere = AdvancedModelLoader.loadModel(ResourceHandler.getResource("models/shieldSphere.obj"));
    }

    @SubscribeEvent
    public void drawHUD(RenderGameOverlayEvent.Post event) {
        HudHandler.drawHUD(event);
    }//TODO Rename event

    @SubscribeEvent
    public void tickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END )
        if (event.phase != TickEvent.Phase.START || event.type != TickEvent.Type.CLIENT || event.side != Side.CLIENT)
            return;
        elapsedTicks++;

        HudHandler.clientTick();

        for (Iterator<Map.Entry<EntityPlayer, XZPair<Float, Integer>>> i = playerShieldStatus.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<EntityPlayer, XZPair<Float, Integer>> entry = i.next();
            if (elapsedTicks - entry.getValue().getValue() > 5) i.remove();
        }


        if (mc == null) mc = Minecraft.getMinecraft();
        else if (mc.theWorld != null) {

//			HudHandler.clientTick();

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

//			playerHoldingWrench = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == ModItems.wrench;

            searchForPlayerMount();
        }
    }

    @SubscribeEvent
    public void fovUpdate(FOVUpdateEvent event) {

        //region Bow FOV Update
//		if (event.entity.getHeldItem()!= null && (event.entity.getHeldItem().getItem() instanceof WyvernBow || event.entity.getHeldItem().getItem() instanceof DraconicBow) && Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed()){
//
//			BowHandler.BowProperties properties = new BowHandler.BowProperties(event.entity.getHeldItem(), event.entity);
//
//			event.newfov = ((6 - properties.zoomModifier) / 6) * event.fov;
//
////			if (ItemNBTHelper.getString(event.entity.getItemInUse(), "mode", "").equals("sharpshooter")){
////				if (event.entity.getItemInUse().getItem() instanceof WyvernBow) zMax = 1.35f;
////				else if (event.entity.getItemInUse().getItem() instanceof DraconicBow) zMax = 2.5f;
////				bowZoom = true;
////				tickSet = elapsedTicks;
////			}
//
//		}
//		//endregion
//
//		//region Armor move speed FOV effect cancellation
//		CustomArmorHandler.ArmorSummery summery = new CustomArmorHandler.ArmorSummery().getSummery(event.entity);
//
//		if (summery != null && summery.speedModifier > 0){
//			IAttributeInstance iattributeinstance = event.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
//			float f = (float)((iattributeinstance.getAttributeValue() / (double)event.entity.capabilities.getWalkSpeed() + 1.0D) / 2.0D);
//			event.newfov /= f;
//		}

        //endregion
    }

    private void searchForPlayerMount() {
//		if (remountTicksRemaining > 0){
//			Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(remountEntityID);
//			if (e != null){
//				Minecraft.getMinecraft().thePlayer.mountEntity(e);
//				LogHelper.info("Successfully placed player on mount after "+(500 - remountTicksRemaining)+" ticks");
//				remountTicksRemaining = 0;
//				return;
//			}
//			remountTicksRemaining--;
//			if (remountTicksRemaining == 0){
//				LogHelper.error("Unable to locate player mount after 500 ticks! Aborting");
//				DraconicEvolution.network.sendToServer(new MountUpdatePacket(-1));
//			}
//		}
    }

    public static void tryRepositionPlayerOnMount(int id) {
        if (remountTicksRemaining == 500) return;
        remountTicksRemaining = 500;
        remountEntityID = id;
        LogHelper.info("Started checking for player mount"); //Todo move to core as this is part of the teleporter
    }

    @SubscribeEvent
    public void renderPlayerEvent(RenderPlayerEvent.Post event) {
//		if (playerShieldStatus.containsKey(event.entityPlayer)) {
//			GL11.glPushMatrix();
//			GL11.glDepthMask(false);
//			GL11.glDisable(GL11.GL_CULL_FACE);
//			GL11.glDisable(GL11.GL_ALPHA_TEST);
//			GL11.glEnable(GL11.GL_BLEND);
//			GL11.glDisable(GL11.GL_LIGHTING);
//			ResourceHelperDE.bindTexture("textures/models/shieldSphere.png");
//
//			float p = 1;//playerShieldStatus.get(event.getEntityPlayer()).getKey();
//
//			EntityPlayer viewingPlayer = Minecraft.getMinecraft().thePlayer;
//
//			int i = 5;// - (elapsedTicks - playerShieldStatus.get(event.entityPlayer).getValue());
//
//			GL11.glColor4f(1F - p, 0F, p, i / 5F);
//
//			if (viewingPlayer != event.getEntityPlayer()){
//				double translationXLT = event.getEntityPlayer().prevPosX - viewingPlayer.prevPosX;
//				double translationYLT = event.getEntityPlayer().prevPosY - viewingPlayer.prevPosY;
//				double translationZLT = event.getEntityPlayer().prevPosZ - viewingPlayer.prevPosZ;
//
//				double translationX = translationXLT + (((event.getEntityPlayer().posX - viewingPlayer.posX) - translationXLT) * event.getPartialRenderTick());
//				double translationY = translationYLT + (((event.getEntityPlayer().posY - viewingPlayer.posY) - translationYLT) * event.getPartialRenderTick());
//				double translationZ = translationZLT + (((event.getEntityPlayer().posZ - viewingPlayer.posZ) - translationZLT) * event.getPartialRenderTick());
//
//				GL11.glTranslated(translationX, translationY + 1.1, translationZ);
//			}
//			else{
//				GL11.glTranslated(0, -0.5, 0);
//			}
//
//			GL11.glScaled(1, 1.5, 1);
//
//			shieldSphere.renderAll();
//
//			GL11.glEnable(GL11.GL_CULL_FACE);
//			GL11.glEnable(GL11.GL_ALPHA_TEST);
//			GL11.glDisable(GL11.GL_BLEND);
//			GL11.glEnable(GL11.GL_LIGHTING);
//			GL11.glDepthMask(true);
//			GL11.glPopMatrix();
//		}
    }

    @SubscribeEvent
    public void renderArmorEvent(RenderPlayerEvent.SetArmorModel event) {
        if (event.isCanceled()) {
            return;
        }
        if (event.getStack() != null && (event.getStack().getItem() instanceof DraconicArmor || event.getStack().getItem() instanceof WyvernArmor)) {
            ItemArmor itemarmor = (ItemArmor) event.getStack().getItem();


//            ModelBiped modelbiped = itemarmor.getArmorModel(event.getEntityPlayer(), event.getStack(), event.getSlot(), event.getRenderer().getMainModel());
//            event.getRenderer().setRenderPassModel(modelbiped);
//            modelbiped.onGround = event.renderer.modelBipedMain.onGround;
//            modelbiped.isRiding = event.renderer.modelBipedMain.isRiding;
//            modelbiped.isChild = event.renderer.modelBipedMain.isChild;
            event.setResult(1);
        }
    }

    @SubscribeEvent
    public void guiOpenEvent(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMainMenu && rand.nextInt(300) == 0){
            try {
                ReflectionHelper.setPrivateValue(GuiMainMenu.class, (GuiMainMenu)event.getGui(), Utils.addCommas(Long.MAX_VALUE) + " RF!!!!", "splashText", "field_110353_x");
            }
            catch (Exception e){}
        }
    }

    @SubscribeEvent
    public void renderWorldEvent(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        World world = player.getEntityWorld();
        ItemStack stack = player.getHeldItemMainhand();
        Minecraft mc = Minecraft.getMinecraft();

        if (stack == null || !(stack.getItem() instanceof MiningToolBase) || !ToolConfigHelper.getBooleanField("showDigAOE", stack)){
            return;
        }

        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK){
            return;
        }

        BlockPos pos = mc.objectMouseOver.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        MiningToolBase tool = (MiningToolBase)stack.getItem();

        if (!tool.isToolEffective(stack, state)) {
            return;
        }

        renderMiningAOE(world, stack, pos, player, event.getPartialTicks());
    }

    private void renderMiningAOE(World world, ItemStack stack, BlockPos pos, EntityPlayerSP player, float partialTicks) {
        MiningToolBase tool = (MiningToolBase)stack.getItem();
        PairKV<BlockPos, BlockPos> aoe = tool.getMiningArea(pos, player, tool.getDigAOE(stack), tool.getDigDepth(stack));
        List<BlockPos> blocks = Lists.newArrayList(BlockPos.getAllInBox(aoe.getKey(), aoe.getValue()));
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        double offsetX = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks;
        double offsetY = player.prevPosY + (player.posY - player.prevPosY) * (double)partialTicks;
        double offsetZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();


        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (BlockPos block : blocks){
            IBlockState state = world.getBlockState(block);

            if (!tool.isToolEffective(stack, state)){
                continue;
            }

            double renderX = block.getX() - offsetX;
            double renderY = block.getY() - offsetY;
            double renderZ = block.getZ() - offsetZ;

            AxisAlignedBB box = new AxisAlignedBB(renderX, renderY, renderZ, renderX + 1, renderY + 1, renderZ + 1).contract(0.49D);

  //          buffer.pos(renderX, renderY, renderZ).color(1F, 1F, 1F, 1F).endVertex();
//            buffer.pos(renderX + 1, renderY + 1, renderZ + 1).color(1F, 1F, 1F, 1F).endVertex();

            double rDist = Utils.getDistanceSq(pos.getX(), pos.getY(), pos.getZ(), block.getX(), block.getY(), block.getZ());


            float colour = 1F - (float)rDist / 100F;
            if (colour < 0.1F) {
                colour = 0.1F;
            }
            float alpha = colour;
            if (alpha < 0.15) {
                alpha = 0.15F;
            }

            float r = 0F;
            float g = 1F;
            float b = 1F;


            buffer.pos(box.minX, box.minY, box.minZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
            buffer.pos(box.maxX, box.maxY, box.maxZ).color(r * colour, g * colour, b * colour, alpha).endVertex();

            buffer.pos(box.maxX, box.minY, box.minZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
            buffer.pos(box.minX, box.maxY, box.maxZ).color(r * colour, g * colour, b * colour, alpha).endVertex();

            buffer.pos(box.minX, box.minY, box.maxZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
            buffer.pos(box.maxX, box.maxY, box.minZ).color(r * colour, g * colour, b * colour, alpha).endVertex();

            buffer.pos(box.maxX, box.minY, box.maxZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
            buffer.pos(box.minX, box.maxY, box.minZ).color(r * colour, g * colour, b * colour, alpha).endVertex();

//
//            buffer.begin(3, DefaultVertexFormats.POSITION);
//            buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
//            buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
//            buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
//            buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
//            buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
//            tessellator.draw();
//            buffer.begin(3, DefaultVertexFormats.POSITION);
//            buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
//            buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
//            buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
//            buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
//            buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
//            tessellator.draw();
//            buffer.begin(1, DefaultVertexFormats.POSITION);
//            buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
//            buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
//            buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
//            buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
//            buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
//            buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
//            buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
//            buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
//            tessellator.draw();

        }

        tessellator.draw();

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
