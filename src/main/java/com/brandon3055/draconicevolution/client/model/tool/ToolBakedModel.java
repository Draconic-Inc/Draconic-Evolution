package com.brandon3055.draconicevolution.client.model.tool;

import codechicken.lib.model.BakedModelProperties;
import codechicken.lib.model.bakedmodels.AbstractBakedPropertiesModel;
import codechicken.lib.util.TransformUtils;
import com.brandon3055.draconicevolution.DEConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 29/06/2017.
 * Handles switching between the 3d variant and 2d variant based on transform type.
 * Suppliers are used so we can dynamically re-bake models runtime.
 */
public class ToolBakedModel extends AbstractBakedPropertiesModel implements IPerspectiveAwareModel {

    private Supplier<IBakedModel> simpleModel;
    private Supplier<IBakedModel> fancyModel;
    private BiFunction<TransformType, IModelState, IModelState> fancyOverrideProcessor;

    public ToolBakedModel(BakedModelProperties properties, Supplier<IBakedModel> simpleModel, Supplier<IBakedModel> fancyModel, BiFunction<TransformType, IModelState, IModelState> fancyOverrideProcessor) {
        super(properties);
        this.simpleModel = simpleModel;
        this.fancyModel = fancyModel;
        this.fancyOverrideProcessor = fancyOverrideProcessor;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return simpleModel.get().getQuads(state, side, rand);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType transformType) {

        IModelState state;
        IBakedModel model;
        if (DEConfig.disable3DModels) {
            //Override to standard model if config tells us to.
            model = this;
        } else {
            model = fancyModel.get();
        }

        switch (transformType) {
            case GROUND:
                state = TransformUtils.DEFAULT_ITEM;
                break;
            case GUI:
                //Force gui to the standard mode, Fall through to default because switch.
                model = this;
            default:
                state = TransformUtils.DEFAULT_TOOL;
                break;
        }

        if (!DEConfig.disable3DModels && fancyOverrideProcessor != null) {
            state = fancyOverrideProcessor.apply(transformType, state);
        }

        return MapWrapper.handlePerspective(model, state, transformType);
    }
}
