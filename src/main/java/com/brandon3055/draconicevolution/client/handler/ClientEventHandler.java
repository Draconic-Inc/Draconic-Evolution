package com.brandon3055.draconicevolution.client.handler;


import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.ProcessHandlerClient;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.lib.DelayedExecutor;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.brandonscore.utils.ModelUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.ICrystalBinder;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.handlers.BinderHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.items.tools.CreativeExchanger;
import com.brandon3055.draconicevolution.items.tools.MiningToolBase;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Brandon on 28/10/2014.
 */
public class ClientEventHandler {
    public static Map<EntityPlayer, PairKV<Float, Integer>> playerShieldStatus = new HashMap<EntityPlayer, PairKV<Float, Integer>>();
    public static ObfMapping splashTextMapping = new ObfMapping("net/minecraft/client/gui/GuiMainMenu", "field_110353_x");
    public static FloatBuffer winPos = GLAllocation.createDirectFloatBuffer(3);
    public static volatile int elapsedTicks;
    public static boolean playerHoldingWrench = false;
    public static Minecraft mc;
    private static Random rand = new Random();
    public static IBakedModel shieldModel = null;
    public static BlockPos explosionPos = null;
    public static double explosionAnimation = 0;
    public static int explosionTime = 0;
    public static boolean explosionRetreating = false;

    public static ShaderProgram explosionShader;

    @SubscribeEvent
    public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
        HudHandler.drawHUD(event);

        if (explosionPos != null && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            mc = Minecraft.getMinecraft();
            updateExplosionAnimation(mc, mc.world, event.getResolution(), mc.getRenderPartialTicks());
        }
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.type != TickEvent.Type.CLIENT || event.side != Side.CLIENT) {
            return;
        }

        elapsedTicks++;
        HudHandler.clientTick();

        if (explosionPos != null) {
            updateExplosion();
        }

        playerShieldStatus.entrySet().removeIf(entry -> elapsedTicks - entry.getValue().getValue() > 5);

        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            playerHoldingWrench = (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ICrystalBinder) || (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() instanceof ICrystalBinder);
        }
    }

    @SubscribeEvent
    public void renderPlayerEvent(RenderPlayerEvent.Post event) {
        if (!DEConfig.disableShieldHitEffect &&  playerShieldStatus.containsKey(event.getEntityPlayer())) {
            if (shieldModel == null) {
                try {
                    shieldModel = OBJLoader.INSTANCE.loadModel(ResourceHelperDE.getResource("models/armor/shield_sphere.obj")).bake(TransformUtils.DEFAULT_BLOCK, DefaultVertexFormats.BLOCK, TextureUtils.bakedTextureGetter);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.depthMask(false);
            GlStateManager.disableCull();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();

            float p = playerShieldStatus.get(event.getEntityPlayer()).getKey();

            EntityPlayer viewingPlayer = Minecraft.getMinecraft().player;

            int i = 5 - (elapsedTicks - playerShieldStatus.get(event.getEntityPlayer()).getValue());

            //GlStateManager.color(1F - p, 0F, p, i / 5F);

            if (viewingPlayer != event.getEntityPlayer()) {
                double translationXLT = event.getEntityPlayer().prevPosX - viewingPlayer.prevPosX;
                double translationYLT = event.getEntityPlayer().prevPosY - viewingPlayer.prevPosY;
                double translationZLT = event.getEntityPlayer().prevPosZ - viewingPlayer.prevPosZ;

                double translationX = translationXLT + (((event.getEntityPlayer().posX - viewingPlayer.posX) - translationXLT) * event.getPartialRenderTick());
                double translationY = translationYLT + (((event.getEntityPlayer().posY - viewingPlayer.posY) - translationYLT) * event.getPartialRenderTick());
                double translationZ = translationZLT + (((event.getEntityPlayer().posZ - viewingPlayer.posZ) - translationZLT) * event.getPartialRenderTick());

                GlStateManager.translate(translationX, translationY + 1.1, translationZ);
            }
            else {
                //GL11.glTranslated(0, -0.5, 0);
                GlStateManager.translate(0, 1.15, 0);
            }

            GlStateManager.scale(1, 1.5, 1);

            GlStateManager.bindTexture(Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId());

            ModelUtils.renderQuadsARGB(shieldModel.getQuads(null, null, 0), new ColourRGBA(1D - p, 0D, p, i / 5D).argb());

            GlStateManager.enableCull();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void guiOpenEvent(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMainMenu && rand.nextInt(150) == 0) {
            try {
                String s = rand.nextBoolean() ? "Icosahedrons proudly brought to you by CCL!!!" : Utils.addCommas(Long.MAX_VALUE) + " RF!!!!";
                ReflectionManager.setField(splashTextMapping, event.getGui(), s);
            }
            catch (Exception e) {
            }
        }
    }

    @SubscribeEvent
    public void renderWorldEvent(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        World world = player.getEntityWorld();
        ItemStack stack = player.getHeldItemMainhand();
        ItemStack offStack = player.getHeldItemOffhand();
        Minecraft mc = Minecraft.getMinecraft();
        float partialTicks = event.getPartialTicks();

        if (!stack.isEmpty() && stack.getItem() instanceof ICrystalBinder) {
            BinderHandler.renderWorldOverlay(player, world, stack, mc, partialTicks);
            return;
        }
        else if (!stack.isEmpty() && offStack.getItem() instanceof ICrystalBinder) {
            BinderHandler.renderWorldOverlay(player, world, offStack, mc, partialTicks);
            return;
        }


        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        if (!stack.isEmpty() && stack.getItem() == DEFeatures.creativeExchanger) {

            List<BlockPos> blocks = CreativeExchanger.getBlocksToReplace(stack, mc.objectMouseOver.getBlockPos(), world, mc.objectMouseOver.sideHit);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            double offsetX = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
            double offsetY = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks;
            double offsetZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();

            for (BlockPos block : blocks) {
                if (world.isAirBlock(block)) {
                    continue;
                }

                double renderX = block.getX() - offsetX;
                double renderY = block.getY() - offsetY;
                double renderZ = block.getZ() - offsetZ;

                Cuboid6 box = new Cuboid6(renderX, renderY, renderZ, renderX + 1, renderY + 1, renderZ + 1).expand(0.001, 0.001, 0.001);
                float colour = 1F;
                if (!world.getBlockState(block.offset(mc.objectMouseOver.sideHit)).getBlock().isReplaceable(world, block.offset(mc.objectMouseOver.sideHit))) {
                    GlStateManager.disableDepth();
                    colour = 0.2F;
                }
                GL11.glColor4f(colour, colour, colour, colour);

                RenderUtils.drawCuboidOutline(box);

                if (!world.getBlockState(block.offset(mc.objectMouseOver.sideHit)).getBlock().isReplaceable(world, block.offset(mc.objectMouseOver.sideHit))) {
                    GlStateManager.enableDepth();
                }
            }

            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }

        if (stack.isEmpty() || !(stack.getItem() instanceof MiningToolBase) || !ToolConfigHelper.getBooleanField("showDigAOE", stack)) {
            return;
        }

        BlockPos pos = mc.objectMouseOver.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        MiningToolBase tool = (MiningToolBase) stack.getItem();

        if (!tool.isToolEffective(stack, state)) {
            return;
        }

        renderMiningAOE(world, stack, pos, player, partialTicks);
    }

    private void renderMiningAOE(World world, ItemStack stack, BlockPos pos, EntityPlayerSP player, float partialTicks) {
        MiningToolBase tool = (MiningToolBase) stack.getItem();
        PairKV<BlockPos, BlockPos> aoe = tool.getMiningArea(pos, player, tool.getDigAOE(stack), tool.getDigDepth(stack));
        List<BlockPos> blocks = Lists.newArrayList(BlockPos.getAllInBox(aoe.getKey(), aoe.getValue()));
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        double offsetX = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
        double offsetY = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks;
        double offsetZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();


        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (BlockPos block : blocks) {
            IBlockState state = world.getBlockState(block);

            if (!tool.isToolEffective(stack, state)) {
                continue;
            }

            double renderX = block.getX() - offsetX;
            double renderY = block.getY() - offsetY;
            double renderZ = block.getZ() - offsetZ;

            AxisAlignedBB box = new AxisAlignedBB(renderX, renderY, renderZ, renderX + 1, renderY + 1, renderZ + 1).shrink(0.49D);

            double rDist = Utils.getDistanceSq(pos.getX(), pos.getY(), pos.getZ(), block.getX(), block.getY(), block.getZ());


            float colour = 1F - (float) rDist / 100F;
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

        }

        tessellator.draw();

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void triggerExplosionEffect(BlockPos pos) {
        explosionPos = pos;
        explosionRetreating = false;
        explosionAnimation = 0;
        explosionTime = 0;

        ProcessHandlerClient.addProcess(new DelayedExecutor(5) {
            @Override
            public void execute(Object[] args) {
                FMLClientHandler.instance().reloadRenderers();
            }
        });
    }

    private void updateExplosion() {
        if (Minecraft.getMinecraft().isGamePaused()) {
            return;
        }
        explosionTime++;
        if (!explosionRetreating) {
            explosionAnimation += 0.05;
            if (explosionAnimation >= 1) {
                explosionAnimation = 1;
                explosionRetreating = true;
            }
        }
        else {
            if (explosionAnimation <= 0) {
                explosionAnimation = 0;
                explosionPos = null;
                return;
            }
            explosionAnimation -= 0.01;
        }
    }

    private void updateExplosionAnimation(Minecraft mc, World world, ScaledResolution resolution, float partialTick) {
        //region TargetPoint Calculation

        Entity entity = mc.getRenderViewEntity();
        float x = (float) (entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTick);
        float y = (float) (entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTick);
        float z = (float) (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTick);
        Vector3 targetPos = Vector3.fromBlockPosCenter(explosionPos);
        targetPos.subtract(x, y, z);
        GLU.gluProject((float) targetPos.x, (float) targetPos.y, (float) targetPos.z, ActiveRenderInfo.MODELVIEW, ActiveRenderInfo.PROJECTION, ActiveRenderInfo.VIEWPORT, winPos);

        boolean behind = winPos.get(2) > 1;
        float screenX = behind ? -1 : winPos.get(0) / mc.displayWidth;
        float screenY = behind ? -1 : winPos.get(1) / mc.displayHeight;

        //endregion

        //region No Shader
        if (!DEShaders.useShaders() || explosionRetreating) {
            float alpha;
            if (explosionAnimation <= 0) {
                alpha = 0;
            }
            else if (explosionRetreating) {
                alpha = (float) explosionAnimation - (partialTick * 0.003F);
            }
            else {
                alpha = (float) explosionAnimation + (partialTick * 0.2F);
            }
            GuiHelper.drawColouredRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), 0x00FFFFFF | (int) (alpha * 255F) << 24);
        }
        //endregion

        else {
            if (explosionShader == null) {
                explosionShader = new ShaderProgram();
                explosionShader.attachShader(DEShaders.explosionOverlay);
            }

            explosionShader.useShader(cache -> {
                cache.glUniform2F("screenPos", screenX, screenY);
                cache.glUniform1F("intensity", (float) explosionAnimation);
                cache.glUniform2F("screenSize", mc.displayWidth, mc.displayHeight);
            });

            GuiHelper.drawColouredRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), 0xFFFFFFFF);

            explosionShader.releaseShader();
        }

    }
}