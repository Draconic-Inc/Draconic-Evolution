package com.brandon3055.draconicevolution.client.handler;


import codechicken.lib.render.shader.ShaderObject;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.ShaderProgramBuilder;
import codechicken.lib.render.shader.UniformType;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.ProcessHandlerClient;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.client.utils.GuiHelperOld;
import com.brandon3055.brandonscore.lib.DelayedExecutor;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.energy.ICrystalBinder;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.handlers.BinderHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.Viewport;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

/**
 * Created by Brandon on 28/10/2014.
 */
@Deprecated //I eventually want to move all this out into proper dedicated handlers
public class ClientEventHandler {
    //    public static ObfMapping splashTextMapping = new ObfMapping("net/minecraft/client/gui/GuiMainMenu", "field_110353_x");
    public static FloatBuffer winPos = FloatBuffer.allocate(3);
    public static volatile int elapsedTicks;
    public static boolean playerHoldingWrench = false;
    public static Minecraft mc;
    private static Random rand = new Random();
    public static BlockPos explosionPos = null;
    public static double explosionAnimation = 0;
    public static int explosionTime = 0;
    public static boolean explosionRetreating = false;

    public static final RenderType explosionFlashType = RenderType.create(DraconicEvolution.MODID+":explosion_flash", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> DEShaders.explosionFlashShader))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)
    );

    @SubscribeEvent
    public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (explosionPos != null && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            mc = Minecraft.getInstance();
            updateExplosionAnimation(mc, event.getMatrixStack(), event.getWindow(), mc.getFrameTime());
        }
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.type != TickEvent.Type.CLIENT || event.side != LogicalSide.CLIENT) {
            return;
        }

        elapsedTicks++;

        if (explosionPos != null) {
            updateExplosion();
        }

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            playerHoldingWrench = (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof ICrystalBinder) || (!player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() instanceof ICrystalBinder);
        }
    }

    @SubscribeEvent
    public void renderPlayerEvent(RenderPlayerEvent.Post event) {
    }

    public static final Matrix4 MODELVIEW = new Matrix4();
    public static final Matrix4 PROJECTION = new Matrix4();

//    @SubscribeEvent
//    public void renderWorldEvent(RenderLevelLastEvent event) {
//        if (event.isCanceled()) {
//            return;
//        }
//        MODELVIEW.set(event.getPoseStack().last().pose());
//        PROJECTION.set(event.getProjectionMatrix());
//
//        LocalPlayer player = Minecraft.getInstance().player;
//        Level world = player.getCommandSenderWorld();
//        ItemStack stack = player.getMainHandItem();
//        ItemStack offStack = player.getOffhandItem();
//        Minecraft mc = Minecraft.getInstance();
//        float partialTicks = event.getPartialTick();
//
//        try {
//            if (!stack.isEmpty() && stack.getItem() instanceof ICrystalBinder) {
//                BinderHandler.renderWorldOverlay(player, event.getPoseStack(), world, stack, mc, partialTicks);
//                return;
//            } else if (!stack.isEmpty() && offStack.getItem() instanceof ICrystalBinder) {
//                BinderHandler.renderWorldOverlay(player, event.getPoseStack(), world, offStack, mc, partialTicks);
//                return;
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//
//
//        if (!(mc.hitResult instanceof BlockHitResult)) {
//            return;
//        }
//
////        if (!stack.isEmpty() && stack.getItem() == DEContent.creative_exchanger) {
////
////            List<BlockPos> blocks = CreativeExchanger.getBlocksToReplace(stack, ((BlockRayTraceResult) mc.hitResult).getBlockPos(), world, ((BlockRayTraceResult) mc.hitResult).getDirection());
////
////            Tessellator tessellator = Tessellator.getInstance();
////            BufferBuilder buffer = tessellator.getBuilder();
////
////            double offsetX = player.xo + (player.getX() - player.xo) * (double) partialTicks;
////            double offsetY = player.yo + (player.getY() - player.yo) * (double) partialTicks;
////            double offsetZ = player.zo + (player.getZ() - player.zo) * (double) partialTicks;
////
////            RenderSystem.enableBlend();
////            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
////            RenderSystem.color4f(1F, 1F, 1F, 1F);
////            RenderSystem.lineWidth(2.0F);
////            RenderSystem.disableTexture();
////
////            for (BlockPos block : blocks) {
////                if (world.isEmptyBlock(block)) {
////                    continue;
////                }
////
////                double renderX = block.getX() - offsetX;
////                double renderY = block.getY() - offsetY;
////                double renderZ = block.getZ() - offsetZ;
////
////                Cuboid6 box = new Cuboid6(renderX, renderY, renderZ, renderX + 1, renderY + 1, renderZ + 1).expand(0.001, 0.001, 0.001);
////                float colour = 1F;
////                if (!world.getBlockState(block.relative(((BlockRayTraceResult) mc.hitResult).getDirection())).getMaterial().isReplaceable()) {
////                    RenderSystem.disableDepthTest();
////                    colour = 0.2F;
////                }
////                GL11.glColor4f(colour, colour, colour, colour);
////
//////                RenderUtils.drawCuboidOutline(box);
////
////                if (!world.getBlockState(block.relative(((BlockRayTraceResult) mc.hitResult).getDirection())).getMaterial().isReplaceable()) {
////                    RenderSystem.enableDepthTest();
////                }
////            }
////
////            RenderSystem.enableTexture();
////            RenderSystem.disableBlend();
////        }
//
////        if (stack.isEmpty() || !(stack.getItem() instanceof MiningToolBase) || !ToolConfigHelper.getBooleanField("showDigAOE", stack)) {
////            return;
////        }
////
////        BlockPos pos = ((BlockRayTraceResult) mc.hitResult).getBlockPos();
////        BlockState state = world.getBlockState(pos);
////        MiningToolBase tool = (MiningToolBase) stack.getItem();
////
////        if (!tool.isToolEffective(stack, state)) {
////            return;
////        }
//
////        renderMiningAOE(world, stack, pos, player, partialTicks);
//    }

//    private void renderMiningAOE(World world, ItemStack stack, BlockPos pos, ClientPlayerEntity player, float partialTicks) {
//        MiningToolBase tool = (MiningToolBase) stack.getItem();
//        Pair<BlockPos, BlockPos> aoe = tool.getMiningArea(pos, player, tool.getDigAOE(stack), tool.getDigDepth(stack));
//        List<BlockPos> blocks = Lists.newArrayList(BlockPos.betweenClosed(aoe.key(), aoe.value()));
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder buffer = tessellator.getBuilder();
//
//        double offsetX = player.xo + (player.getX() - player.xo) * (double) partialTicks;
//        double offsetY = player.yo + (player.getY() - player.yo) * (double) partialTicks;
//        double offsetZ = player.zo + (player.getZ() - player.zo) * (double) partialTicks;
//
//        RenderSystem.enableBlend();
//        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//        RenderSystem.color4f(1F, 1F, 1F, 1F);
//        RenderSystem.lineWidth(2.0F);
//        RenderSystem.disableTexture();
//        RenderSystem.disableDepthTest();
//
//
//        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
//
//        for (BlockPos block : blocks) {
//            BlockState state = world.getBlockState(block);
//
//            if (!tool.isToolEffective(stack, state)) {
//                continue;
//            }
//
//            double renderX = block.getX() - offsetX;
//            double renderY = block.getY() - offsetY;
//            double renderZ = block.getZ() - offsetZ;
//
//            AxisAlignedBB box = new AxisAlignedBB(renderX, renderY, renderZ, renderX + 1, renderY + 1, renderZ + 1).deflate(0.49D);
//
//            double rDist = Utils.getDistanceSq(pos.getX(), pos.getY(), pos.getZ(), block.getX(), block.getY(), block.getZ());
//
//
//            float colour = 1F - (float) rDist / 100F;
//            if (colour < 0.1F) {
//                colour = 0.1F;
//            }
//            float alpha = colour;
//            if (alpha < 0.15) {
//                alpha = 0.15F;
//            }
//
//            float r = 0F;
//            float g = 1F;
//            float b = 1F;
//
//
//            buffer.vertex(box.minX, box.minY, box.minZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
//            buffer.vertex(box.maxX, box.maxY, box.maxZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
//
//            buffer.vertex(box.maxX, box.minY, box.minZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
//            buffer.vertex(box.minX, box.maxY, box.maxZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
//
//            buffer.vertex(box.minX, box.minY, box.maxZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
//            buffer.vertex(box.maxX, box.maxY, box.minZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
//
//            buffer.vertex(box.maxX, box.minY, box.maxZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
//            buffer.vertex(box.minX, box.maxY, box.minZ).color(r * colour, g * colour, b * colour, alpha).endVertex();
//
//        }
//
//        tessellator.end();
//
//        RenderSystem.enableDepthTest();
//        RenderSystem.enableTexture();
//        RenderSystem.disableBlend();
//    }

    public static void triggerExplosionEffect(BlockPos pos, boolean reload) {
        explosionPos = pos;
        explosionRetreating = false;
        explosionAnimation = 0;
        explosionTime = 0;

        if (reload){
            ProcessHandlerClient.addProcess(new DelayedExecutor(13) {
                @Override
                public void execute(Object[] args) {
                    Minecraft.getInstance().levelRenderer.allChanged();
                }
            });
        }
    }

    private void updateExplosion() {
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        explosionTime++;
        if (!explosionRetreating) {
            explosionAnimation += 0.05;
            if (explosionAnimation >= 1) {
                explosionAnimation = 1;
                explosionRetreating = true;
            }
        } else {
            if (explosionAnimation <= 0) {
                explosionAnimation = 0;
                explosionPos = null;
                return;
            }
            explosionAnimation -= 0.01;
        }
//        explosionTime = 10;
//        explosionAnimation = explosionTime * 0.05;
    }

    private void updateExplosionAnimation(Minecraft mc, PoseStack poseStack, Window window, float partialTick) {
        MultiBufferSource.BufferSource buffers = RenderUtils.getGuiBuffers();

        if (/*true || */explosionRetreating) {
            float alpha;
            if (explosionAnimation <= 0) {
                alpha = 0;
            } else if (explosionRetreating) {
                alpha = (float) explosionAnimation - (partialTick * 0.01F);
            } else {
                alpha = (float) explosionAnimation + (partialTick * 0.05F);
            }
            if (alpha > 1) alpha = 1;
            GuiHelper.drawRect(buffers, poseStack, 0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), 0x00FFFFFF | (int) (alpha * 255F) << 24);
            RenderUtils.endBatch(buffers);

        } else {
            Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();
            Vector3 targetPos = Vector3.fromBlockPosCenter(explosionPos);
            targetPos.subtract(camPos.x, camPos.y, camPos.z);
            Vector3 winPos = gluProject(targetPos, MODELVIEW, PROJECTION);

            boolean behind = winPos.z > 1;
            float screenX = behind ? -1 : (float) winPos.x / window.getScreenWidth();
            float screenY = behind ? -1 : (float) winPos.y / window.getScreenHeight();

            DEShaders.explosionFlashScreenPos.glUniform2f(screenX, screenY);
            DEShaders.explosionFlashIntensity.glUniform1f((float) explosionAnimation);
            DEShaders.explosionFlashScreenSize.glUniform2f(window.getScreenWidth(), window.getScreenHeight());
            GuiHelperOld.drawColouredRect(buffers.getBuffer(explosionFlashType), 0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), 0xFFFFFFFF, 0);
            RenderUtils.endBatch(buffers);
        }
    }

    //Thanks Covers1624!
    private static Vector3 gluProject(Vector3 obj, Matrix4 modelMatrix, Matrix4 projMatrix) {
        Vector4f o = new Vector4f((float) obj.x, (float) obj.y, (float) obj.z, 1.0F);
        multMatrix(modelMatrix, o);
        multMatrix(projMatrix, o);

        if (o.w() == 0) {
            return Vector3.ZERO.copy();
        }
        o.setW((1.0F / o.w()) * 0.5F);

        o.setX(o.x() * o.w() + 0.5F);
        o.setY(o.y() * o.w() + 0.5F);
        o.setZ(o.z() * o.w() + 0.5F);

        Vector3 winPos = new Vector3();
        winPos.z = o.z();

        winPos.x = o.x() * GlStateManager.Viewport.width();
        winPos.y = o.y() * GlStateManager.Viewport.height();
        return winPos;
    }

    private static void multMatrix(Matrix4 mat, Vector4f vec) {
        double x = mat.m00 * vec.x() + mat.m01 * vec.y() + mat.m02 * vec.z() + mat.m03 * vec.w();
        double y = mat.m10 * vec.x() + mat.m11 * vec.y() + mat.m12 * vec.z() + mat.m13 * vec.w();
        double z = mat.m20 * vec.x() + mat.m21 * vec.y() + mat.m22 * vec.z() + mat.m23 * vec.w();
        double w = mat.m30 * vec.x() + mat.m31 * vec.y() + mat.m32 * vec.z() + mat.m33 * vec.w();
        vec.set((float) x, (float) y, (float) z, (float) w);
    }
}