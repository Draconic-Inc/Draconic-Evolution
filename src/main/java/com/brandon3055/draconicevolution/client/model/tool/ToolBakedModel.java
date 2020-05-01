//package com.brandon3055.draconicevolution.client.model.tool;
//
//import codechicken.lib.model.bakedmodels.AbstractBakedPropertiesModel;
//import codechicken.lib.model.bakedmodels.ModelProperties;
//import codechicken.lib.util.TransformUtils;
//import com.brandon3055.draconicevolution.DEConfig;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.renderer.model.BakedQuad;
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.model.ItemCameraTransforms;
//import net.minecraft.client.renderer.model.ItemOverrideList;
//import net.minecraft.util.Direction;
//import net.minecraftforge.client.model.PerspectiveMapWrapper;
//import net.minecraftforge.client.model.data.IModelData;
//import net.minecraftforge.common.model.IModelState;
//import org.apache.commons.lang3.tuple.Pair;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//import javax.vecmath.Matrix4f;
//import java.util.List;
//import java.util.Random;
//import java.util.function.BiFunction;
//import java.util.function.Supplier;
//
///**
// * Created by covers1624 on 29/06/2017.
// * Handles switching between the 3d variant and 2d variant based on transform type.
// * Suppliers are used so we can dynamically re-bake models runtime.
// */
//public class ToolBakedModel extends AbstractBakedPropertiesModel {
//
//    private Supplier<IBakedModel> simpleModel;
//    private Supplier<IBakedModel> fancyModel;
//    private BiFunction<ItemCameraTransforms.TransformType, IModelState, IModelState> fancyOverrideProcessor;
//
//    public ToolBakedModel(ModelProperties properties, Supplier<IBakedModel> simpleModel, Supplier<IBakedModel> fancyModel, BiFunction<ItemCameraTransforms.TransformType, IModelState, IModelState> fancyOverrideProcessor) {
//        super(properties);
//        this.simpleModel = simpleModel;
//        this.fancyModel = fancyModel;
//        this.fancyOverrideProcessor = fancyOverrideProcessor;
//    }
//
//    @Override
//    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
//        return simpleModel.get().getQuads(state, side, rand);
//    }
//
//    @Override
//    public ItemOverrideList getOverrides() {
//        return ItemOverrideList.EMPTY;
//    }
//
//    @Override
//    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType transformType) {
//
//        IModelState state;
//        IBakedModel model;
//        if (DEConfig.disable3DModels) {
//            //Override to standard model if config tells us to.
//            model = this;
//        } else {
//            model = fancyModel.get();
//        }
//
//        switch (transformType) {
//            case GROUND:
//                state = TransformUtils.DEFAULT_ITEM;
//                break;
//            case GUI:
//                //Force gui to the standard mode, Fall through to default because switch.
//                model = this;
//            default:
//                state = TransformUtils.DEFAULT_TOOL;
//                break;
//        }
//
//        if (!DEConfig.disable3DModels && fancyOverrideProcessor != null) {
//            state = fancyOverrideProcessor.apply(transformType, state);
//        }
//
//        return PerspectiveMapWrapper.handlePerspective(model, state, transformType);
//    }
//}
