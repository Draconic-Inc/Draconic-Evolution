package com.brandon3055.draconicevolution.client.handler;


import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformCache;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.ProcessHandlerClient;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.lib.DelayedExecutor;
import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEOldConfig;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import com.brandon3055.draconicevolution.api.itemconfig_dep.ToolConfigHelper;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.handlers.BinderHandler;
import com.brandon3055.draconicevolution.items.tools.CreativeExchanger;
import com.brandon3055.draconicevolution.items.tools.old.MiningToolBase;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static codechicken.lib.render.shader.ShaderObject.StandardShaderType.FRAGMENT;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by Brandon on 28/10/2014.
 */
public class ClientEventHandler {
    public static Map<PlayerEntity, Pair<Float, Integer>> playerShieldStatus = new HashMap<PlayerEntity, Pair<Float, Integer>>();
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

    public static ShaderProgram explosionShader = ShaderProgramBuilder.builder()
            .addShader("frag", shader -> shader
                    .type(FRAGMENT)
                    .source(new ResourceLocation(MODID, "shaders/explosion_overlay.frag"))
                    .uniform("screenPos", UniformType.VEC2)
                    .uniform("intensity", UniformType.FLOAT)
                    .uniform("screenSize", UniformType.VEC2)
            )
            .build();

    @SubscribeEvent
    public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
        HudHandler.drawHUD(event);

        if (explosionPos != null && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            mc = Minecraft.getInstance();
            updateExplosionAnimation(mc, mc.world, event.getWindow(), mc.getRenderPartialTicks());
        }
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.type != TickEvent.Type.CLIENT || event.side != LogicalSide.CLIENT) {
            return;
        }

        elapsedTicks++;
        HudHandler.clientTick();

        if (explosionPos != null) {
            updateExplosion();
        }

        playerShieldStatus.entrySet().removeIf(entry -> elapsedTicks - entry.getValue().value() > 5);

        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            playerHoldingWrench = (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ICrystalBinder) || (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() instanceof ICrystalBinder);
        }
    }

    @SubscribeEvent
    public void renderPlayerEvent(RenderPlayerEvent.Post event) {
        if (!DEOldConfig.disableShieldHitEffect &&  playerShieldStatus.containsKey(event.getPlayer())) {
            if (shieldModel == null) {
                try {
//                    shieldModel = OBJLoader.INSTANCE.loadModel(ResourceHelperDE.getResource("models/armor/shield_sphere.obj")).bake(TransformUtils.DEFAULT_BLOCK, DefaultVertexFormats.BLOCK, TextureUtils::getTexture);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }

            RenderSystem.pushMatrix();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.disableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.disableLighting();

            float p = playerShieldStatus.get(event.getPlayer()).key();

            PlayerEntity viewingPlayer = Minecraft.getInstance().player;

            int i = 5 - (elapsedTicks - playerShieldStatus.get(event.getPlayer()).value());

            //RenderSystem.color(1F - p, 0F, p, i / 5F);

            if (viewingPlayer != event.getPlayer()) {
                double translationXLT = event.getPlayer().prevPosX - viewingPlayer.prevPosX;
                double translationYLT = event.getPlayer().prevPosY - viewingPlayer.prevPosY;
                double translationZLT = event.getPlayer().prevPosZ - viewingPlayer.prevPosZ;

                double translationX = translationXLT + (((event.getPlayer().posX - viewingPlayer.posX) - translationXLT) * event.getPartialRenderTick());
                double translationY = translationYLT + (((event.getPlayer().posY - viewingPlayer.posY) - translationYLT) * event.getPartialRenderTick());
                double translationZ = translationZLT + (((event.getPlayer().posZ - viewingPlayer.posZ) - translationZLT) * event.getPartialRenderTick());

                RenderSystem.translated(translationX, translationY + 1.1, translationZ);
            }
            else {
                //GL11.glTranslated(0, -0.5, 0);
                RenderSystem.translated(0, 1.15, 0);
            }

            RenderSystem.scaled(1, 1.5, 1);

//            RenderSystem.bindTexture(Minecraft.getInstance().getTextureMap().getGlTextureId());

//            ModelUtils.renderQuadsARGB(shieldModel.getQuads(null, null, rand), new ColourRGBA(1D - p, 0D, p, i / 5D).argb());

            RenderSystem.enableCull();
            RenderSystem.enableAlphaTest();
            RenderSystem.disableBlend();
            RenderSystem.enableLighting();
            RenderSystem.depthMask(true);
            RenderSystem.popMatrix();
        }
    }

    @SubscribeEvent
    public void guiOpenEvent(GuiOpenEvent event) {
        if (event.getGui() instanceof MainMenuScreen && rand.nextInt(150) == 0) {
            try {
                String s = rand.nextBoolean() ? "Icosahedrons proudly brought to you by CCL!!!" : Utils.addCommas(Long.MAX_VALUE) + " RF!!!!";
                ReflectionManager.setField(splashTextMapping, event.getGui(), s);
            }
            catch (Exception e) {}
        }
    }

    public static final Matrix4 MODELVIEW = new Matrix4();
    public static final Matrix4 PROJECTION = new Matrix4();
    @SubscribeEvent
    public void renderWorldEvent(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }

        MODELVIEW.set(event.getMatrixStack().getLast().getMatrix());
        PROJECTION.set(event.getProjectionMatrix());

        ClientPlayerEntity player = Minecraft.getInstance().player;
        World world = player.getEntityWorld();
        ItemStack stack = player.getHeldItemMainhand();
        ItemStack offStack = player.getHeldItemOffhand();
        Minecraft mc = Minecraft.getInstance();
        float partialTicks = event.getPartialTicks();

        if (!stack.isEmpty() && stack.getItem() instanceof ICrystalBinder) {
            BinderHandler.renderWorldOverlay(player, world, stack, mc, partialTicks);
            return;
        }
        else if (!stack.isEmpty() && offStack.getItem() instanceof ICrystalBinder) {
            BinderHandler.renderWorldOverlay(player, world, offStack, mc, partialTicks);
            return;
        }


        if (!(mc.objectMouseOver instanceof BlockRayTraceResult)) {
            return;
        }

        if (!stack.isEmpty() && stack.getItem() == DEContent.creative_exchanger) {

            List<BlockPos> blocks = CreativeExchanger.getBlocksToReplace(stack, ((BlockRayTraceResult)mc.objectMouseOver).getPos(), world, ((BlockRayTraceResult)mc.objectMouseOver).getFace());

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            double offsetX = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
            double offsetY = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks;
            double offsetZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            RenderSystem.lineWidth(2.0F);
            RenderSystem.disableTexture();

            for (BlockPos block : blocks) {
                if (world.isAirBlock(block)) {
                    continue;
                }

                double renderX = block.getX() - offsetX;
                double renderY = block.getY() - offsetY;
                double renderZ = block.getZ() - offsetZ;

                Cuboid6 box = new Cuboid6(renderX, renderY, renderZ, renderX + 1, renderY + 1, renderZ + 1).expand(0.001, 0.001, 0.001);
                float colour = 1F;
                if (!world.getBlockState(block.offset(((BlockRayTraceResult)mc.objectMouseOver).getFace())).getMaterial().isReplaceable()) {
                    RenderSystem.disableDepthTest();
                    colour = 0.2F;
                }
                GL11.glColor4f(colour, colour, colour, colour);

//                RenderUtils.drawCuboidOutline(box);

                if (!world.getBlockState(block.offset(((BlockRayTraceResult) mc.objectMouseOver).getFace())).getMaterial().isReplaceable()) {
                    RenderSystem.enableDepthTest();
                }
            }

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        if (stack.isEmpty() || !(stack.getItem() instanceof MiningToolBase) || !ToolConfigHelper.getBooleanField("showDigAOE", stack)) {
            return;
        }

        BlockPos pos = ((BlockRayTraceResult) mc.objectMouseOver).getPos();
        BlockState state = world.getBlockState(pos);
        MiningToolBase tool = (MiningToolBase) stack.getItem();

        if (!tool.isToolEffective(stack, state)) {
            return;
        }

        renderMiningAOE(world, stack, pos, player, partialTicks);
    }

    private void renderMiningAOE(World world, ItemStack stack, BlockPos pos, ClientPlayerEntity player, float partialTicks) {
        MiningToolBase tool = (MiningToolBase) stack.getItem();
        Pair<BlockPos, BlockPos> aoe = tool.getMiningArea(pos, player, tool.getDigAOE(stack), tool.getDigDepth(stack));
        List<BlockPos> blocks = Lists.newArrayList(BlockPos.getAllInBoxMutable(aoe.key(), aoe.value()));
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        double offsetX = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
        double offsetY = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks;
        double offsetZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.lineWidth(2.0F);
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();


        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (BlockPos block : blocks) {
            BlockState state = world.getBlockState(block);

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

        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void triggerExplosionEffect(BlockPos pos) {
        explosionPos = pos;
        explosionRetreating = false;
        explosionAnimation = 0;
        explosionTime = 0;

        ProcessHandlerClient.addProcess(new DelayedExecutor(5) {
            @Override
            public void execute(Object[] args) {
                Minecraft.getInstance().worldRenderer.loadRenderers();
            }
        });
    }

    private void updateExplosion() {
        if (Minecraft.getInstance().isGamePaused()) {
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
//        explosionTime = 10;
//        explosionAnimation = explosionTime * 0.01;
    }

    public static final IntBuffer VIEWPORT = GLAllocation.createDirectByteBuffer(16 << 2).asIntBuffer();
    private void updateExplosionAnimation(Minecraft mc, World world, MainWindow resolution, float partialTick) {
        //region TargetPoint Calculation

        GL11.glGetIntegerv(GL11.GL_VIEWPORT, VIEWPORT);
        Entity entity = mc.getRenderViewEntity();
        float x = (float) (entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTick);
        float y = (float) (entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTick);
        float z = (float) (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTick);
        Vector3 targetPos = Vector3.fromBlockPosCenter(explosionPos);
        targetPos.subtract(x, y, z);
        Vector3 winPos = gluProject(targetPos, MODELVIEW, PROJECTION, VIEWPORT);

        boolean behind = winPos.z > 1;
        float screenX = behind ? -1 : (float) winPos.x / resolution.getWidth();
        float screenY = behind ? -1 : (float) winPos.y / resolution.getHeight();

        //endregion

        if (!DEConfig.reactorShaders || explosionRetreating) {
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
        else {

            UniformCache uniforms = explosionShader.pushCache();
            uniforms.glUniform2f("screenPos", screenX, screenY);
            uniforms.glUniform1f("intensity", (float) explosionAnimation);
            uniforms.glUniform2f("screenSize", resolution.getWidth(), resolution.getHeight());

            explosionShader.use();
            explosionShader.popCache(uniforms);
            GuiHelper.drawColouredRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), 0xFFFFFFFF);
            explosionShader.release();
        }
    }

    //Thanks Covers1624!
    private static Vector3 gluProject(Vector3 obj, Matrix4 modelMatrix, Matrix4 projMatrix, IntBuffer viewport) {
        Vector4f o = new Vector4f((float) obj.x, (float) obj.y, (float) obj.z, 1.0F);
        multMatrix(modelMatrix, o);
        multMatrix(projMatrix, o);

        if (o.getW() == 0) {
            return Vector3.ZERO.copy();
        }
        o.setW((1.0F / o.getW()) * 0.5F);

        o.setX(o.getX() * o.getW() + 0.5F);
        o.setY(o.getY() * o.getW() + 0.5F);
        o.setZ(o.getZ() * o.getW() + 0.5F);

        Vector3 winPos = new Vector3();
        winPos.z = o.getZ();

        winPos.x = o.getX() * viewport.get(viewport.position() + 2) + viewport.get(viewport.position() + 0);
        winPos.y = o.getY() * viewport.get(viewport.position() + 3) + viewport.get(viewport.position() + 1);
        return winPos;
    }

    private static void multMatrix(Matrix4 mat, Vector4f vec) {
        double x = mat.m00 * vec.getX() + mat.m01 * vec.getY() + mat.m02 * vec.getZ() + mat.m03 * vec.getW();
        double y = mat.m10 * vec.getX() + mat.m11 * vec.getY() + mat.m12 * vec.getZ() + mat.m13 * vec.getW();
        double z = mat.m20 * vec.getX() + mat.m21 * vec.getY() + mat.m22 * vec.getZ() + mat.m23 * vec.getW();
        double w = mat.m30 * vec.getX() + mat.m31 * vec.getY() + mat.m32 * vec.getZ() + mat.m33 * vec.getW();
        vec.set((float) x, (float) y, (float) z, (float) w);
    }
}