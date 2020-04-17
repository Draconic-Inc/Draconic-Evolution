package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.*;
import codechicken.lib.vec.uv.IconTransformation;
import com.brandon3055.draconicevolution.blocks.machines.Generator;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.client.model.animation.TileEntityRendererFast;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 1/3/20.
 */
public class RenderTileGenerator extends TileEntityRendererFast<TileGenerator> {

    private static CCModel fanModel;

    public RenderTileGenerator() {
        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/block/generator/generator_fan.obj"), GL11.GL_QUADS, null);
        fanModel = CCModel.combine(map.values());
    }

    @Override
    public void renderTileEntityFast(TileGenerator te, double x, double y, double z, float partialTicks, int destroyStage, BufferBuilder buffer) {
        IconTransformation icon = new IconTransformation(DETextures.getDETexture("models/block/generator/generator_2"));
        CCRenderState state = CCRenderState.instance();
        state.reset();
        state.bind(buffer);
        state.setBrightness(te.getWorld(), te.getPos());

        Matrix4 mat = new Matrix4();
        mat.apply(new Translation(x + .5, y + .5, z + .5));
        mat.apply(new Rotation(te.getBlockState().get(Generator.FACING).getOpposite().getHorizontalAngle() * -MathHelper.torad, 0, 1, 0));
        mat.apply(new Scale(0.0625));
        mat.apply(new Rotation((te.rotation + (te.rotationSpeed * partialTicks)), 1, 0, 0).at(new Vector3(0, -1.5, -4.5)));

        fanModel.render(state, LightModel.standardLightModel, icon, mat);
    }
}
