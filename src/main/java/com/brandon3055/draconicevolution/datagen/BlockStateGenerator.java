package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.DislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.Portal;
import com.brandon3055.draconicevolution.blocks.RainSensor;
import com.brandon3055.draconicevolution.blocks.machines.EnergyPylon;
import com.brandon3055.draconicevolution.blocks.machines.Generator;
import com.brandon3055.draconicevolution.blocks.machines.Grinder;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.loaders.MultiLayerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.UP;

/**
 * Created by brandon3055 on 28/2/20.
 */
public class BlockStateGenerator extends BlockStateProvider {
    private static final Logger LOGGER = LogManager.getLogger();

    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, DraconicEvolution.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //Simple Blocks
        simpleBlock(DEContent.block_draconium);
        simpleBlock(DEContent.block_draconium_awakened, models().cubeBottomTop("awakened_draconium_block", modLoc("block/awakened_draconium_block_side"), modLoc("block/awakened_draconium_block"), modLoc("block/awakened_draconium_block")));
        simpleBlock(DEContent.infused_obsidian);
        simpleBlock(DEContent.energy_core);
        simpleBlock(DEContent.energy_core_stabilizer, models().getExistingFile(modLoc("block/energy_core_stabilizer")));
        simpleBlock(DEContent.creative_op_capacitor);
        simpleBlock(DEContent.stabilized_spawner, models().getExistingFile(modLoc("block/stabilized_spawner")));
        simpleBlock(DEContent.particle_generator, models().getExistingFile(modLoc("block/particle_generator")));
        simpleBlock(DEContent.crafting_core, models().getExistingFile(modLoc("block/crafting/fusion_crafting_core")));
        simpleBlock(DEContent.dislocation_inhibitor, models().cubeBottomTop("dislocation_inhibitor", modLoc("block/dislocation_inhibitor"), modLoc("block/parts/machine_top"), modLoc("block/parts/machine_top")));

        multiLayerBlock(DEContent.ore_draconium_overworld, mcLoc("block/stone"), modLoc("block/draconium_ore_overlay"));
        multiLayerBlock(DEContent.ore_draconium_nether, mcLoc("block/netherrack"), modLoc("block/draconium_ore_overlay"));
        multiLayerBlock(DEContent.ore_draconium_end, mcLoc("block/end_stone"), modLoc("block/draconium_ore_overlay"));
        multiLayerBlock(DEContent.ore_draconium_deepslate, mcLoc("block/deepslate"), modLoc("block/draconium_ore_overlay"));

        directionalBlock(DEContent.crafting_injector_basic, models().getExistingFile(modLoc("block/crafting/crafting_injector_draconium")));
        directionalBlock(DEContent.crafting_injector_wyvern, models().getExistingFile(modLoc("block/crafting/crafting_injector_wyvern")));
        directionalBlock(DEContent.crafting_injector_awakened, models().getExistingFile(modLoc("block/crafting/crafting_injector_draconic")));
        directionalBlock(DEContent.crafting_injector_chaotic, models().getExistingFile(modLoc("block/crafting/crafting_injector_chaotic")));

        directionalFromNorth(DEContent.fluid_gate, models().getExistingFile(modLoc("block/fluid_gate")));
        directionalFromNorth(DEContent.flux_gate, models().getExistingFile(modLoc("block/flux_gate")));

        getVariantBuilder(DEContent.rain_sensor).forAllStates(state -> ConfiguredModel.builder().modelFile(models().getExistingFile(state.getValue(RainSensor.ACTIVE) ? modLoc("block/rain_sensor_active") : modLoc("block/rain_sensor"))).build());

        directionalBlock(DEContent.potentiometer, models().getExistingFile(modLoc("block/potentiometer")));

        simpleBlock(DEContent.energy_transfuser, models().getExistingFile(modLoc("block/energy_transfuser")));

        simpleBlock(DEContent.disenchanter, models().getExistingFile(modLoc("block/disenchanter")));
        simpleBlock(DEContent.celestial_manipulator, models().getExistingFile(modLoc("block/celestial_manipulator")));
        simpleBlock(DEContent.entity_detector, models().getExistingFile(modLoc("block/entity_detector")));
        simpleBlock(DEContent.entity_detector_advanced, models().getExistingFile(modLoc("block/entity_detector_advanced")));

        dummyBlock(DEContent.crystal_io_basic);
        dummyBlock(DEContent.crystal_io_wyvern);
        dummyBlock(DEContent.crystal_io_draconic);
//        dummyBlock(DEContent.  crystal_io_chaotic);
        dummyBlock(DEContent.crystal_relay_basic);
        dummyBlock(DEContent.crystal_relay_wyvern);
        dummyBlock(DEContent.crystal_relay_draconic);
//        dummyBlock(DEContent.  crystal_relay_chaotic);
        dummyBlock(DEContent.crystal_wireless_basic);
        dummyBlock(DEContent.crystal_wireless_wyvern);
        dummyBlock(DEContent.crystal_wireless_draconic);
//        dummyBlock(DEContent.  crystal_wireless_chaotic);
        dummyBlock(DEContent.structure_block);
        dummyBlock(DEContent.chaos_crystal);
        dummyBlock(DEContent.placed_item);
        dummyBlock(DEContent.chaos_crystal_part);

        dummyBlock(DEContent.draconium_chest);
        dummyBlock(DEContent.reactor_core);
        dummyBlock(DEContent.reactor_stabilizer);
        dummyBlock(DEContent.reactor_injector);

        VariantBlockStateBuilder pylonBuilder = getVariantBuilder(DEContent.energy_pylon);
        for (EnergyPylon.Mode mode : EnergyPylon.Mode.values()) {
            String io = mode == EnergyPylon.Mode.OUTPUT ? "output" : "input";
            ModelFile model = models().cubeBottomTop("energy_pylon_" + io, modLoc("block/energy_pylon/energy_pylon_" + io), modLoc("block/energy_pylon/energy_pylon_" + io), modLoc("block/energy_pylon/energy_pylon_active_face"));
            for (Direction dir : Direction.values()) {
                pylonBuilder.partialState()
                        .with(EnergyPylon.FACING, dir)
                        .with(EnergyPylon.MODE, mode)
                        .modelForState()
                        .modelFile(model)
                        .rotationY(dir.getAxis() == Direction.Axis.Y ? 0 : 180 + (90 * dir.get2DDataValue()))
                        .rotationX(dir == UP ? 0 : dir == DOWN ? 180 : 90)
                        .addModel();

            }
        }

        simpleBlock(DEContent.dislocator_pedestal, models().getExistingFile(modLoc("block/dislocator_pedestal")));

        VariantBlockStateBuilder receptacleBuilder = getVariantBuilder(DEContent.dislocator_receptacle);
        receptacleBuilder.addModels(receptacleBuilder.partialState().with(DislocatorReceptacle.CAMO, true), ConfiguredModel.builder().modelFile(models().cubeAll("infused_obsidian", modLoc("block/infused_obsidian"))).build());
        receptacleBuilder.addModels(receptacleBuilder.partialState().with(DislocatorReceptacle.ACTIVE, false).with(DislocatorReceptacle.CAMO, false), ConfiguredModel.builder().modelFile(models().cubeAll("dislocator_receptacle_inactive", modLoc("block/dislocator_receptacle_inactive"))).build());
        receptacleBuilder.addModels(receptacleBuilder.partialState().with(DislocatorReceptacle.ACTIVE, true).with(DislocatorReceptacle.CAMO, false), ConfiguredModel.builder().modelFile(models().cubeAll("dislocator_receptacle_active", modLoc("block/dislocator_receptacle_active"))).build());

        //Generate portal block state
        ModelFile portalModel = models().getExistingFile(modLoc("block/portal/portal"));
        ModelFile portalWallX = models().getExistingFile(modLoc("block/portal/portal_wall_x"));
        ModelFile portalWallY = models().getExistingFile(modLoc("block/portal/portal_wall_y"));
        ModelFile portalWallZ = models().getExistingFile(modLoc("block/portal/portal_wall_z"));
        MultiPartBlockStateBuilder portalBuilder = getMultipartBuilder(DEContent.portal);
        for (Direction.Axis axis : Direction.Axis.values()) {
            ModelFile wallWestEast = axis == Direction.Axis.Z ? portalWallX : axis == Direction.Axis.Y ? portalWallY : portalWallZ;
            ModelFile wallUpDown = axis == Direction.Axis.Z || axis == Direction.Axis.Y ? portalWallY : portalWallZ;
            portalBuilder.part()
                    .modelFile(portalModel)
                    .rotationX(axis != Direction.Axis.Y ? 90 : 0)
                    .rotationY(axis == Direction.Axis.X ? 90 : 0)
                    .addModel()
                    .condition(Portal.VISIBLE, true)
                    .condition(Portal.AXIS, axis)
                    .end()
                    //Up
                    .part()
                    .modelFile(wallUpDown)
                    .rotationX(axis == Direction.Axis.X || axis == Direction.Axis.Z ? -90 : 0)
                    .addModel()
                    .condition(Portal.DRAW_UP, true)
                    .condition(Portal.AXIS, axis)
                    .end()
                    //Down
                    .part()
                    .modelFile(wallUpDown)
                    .rotationX(axis == Direction.Axis.X || axis == Direction.Axis.Z ? 90 : 0)
                    .rotationY(axis == Direction.Axis.Y ? 180 : 0)
                    .addModel()
                    .condition(Portal.DRAW_DOWN, true)
                    .condition(Portal.AXIS, axis)
                    .end()
                    //West
                    .part()
                    .modelFile(wallWestEast)
                    .rotationY(axis == Direction.Axis.Z ? 180 : axis == Direction.Axis.Y ? -90 : 0)
                    .addModel()
                    .condition(Portal.DRAW_WEST, true)
                    .condition(Portal.AXIS, axis)
                    .end()
                    //East
                    .part()
                    .modelFile(wallWestEast)
                    .rotationY(axis == Direction.Axis.Y ? 90 : 0)
                    .rotationX(axis == Direction.Axis.X ? 180 : 0)
                    .addModel()
                    .condition(Portal.DRAW_EAST, true)
                    .condition(Portal.AXIS, axis)
                    .end();
        }

        //Generator
        ModelFile modelGenerator = models().getExistingFile(modLoc("block/generator/generator"));
        ModelFile modelGeneratorFlame = models().getExistingFile(modLoc("block/generator/generator_flame"));
        MultiPartBlockStateBuilder generatorBuilder = getMultipartBuilder(DEContent.generator);
        for (Direction dir : FenceGateBlock.FACING.getPossibleValues()) {
            int angle = (int) dir.getOpposite().toYRot();
            generatorBuilder.part()
                    .modelFile(modelGenerator)
                    .rotationY(angle)
                    .addModel()
                    .condition(Generator.FACING, dir)
                    .end()

                    .part()
                    .modelFile(modelGeneratorFlame)
                    .rotationY(angle)
                    .addModel()
                    .condition(Generator.FACING, dir)
                    .condition(Generator.ACTIVE, true)
                    .end();
        }

        //Grinder
        ModelFile modelGrinder = models().getExistingFile(modLoc("block/grinder/grinder"));
        ModelFile modelGrinderActive = models().getExistingFile(modLoc("block/grinder/grinder_eyes"));
        MultiPartBlockStateBuilder grinderBuilder = getMultipartBuilder(DEContent.grinder);
        for (Direction dir : Direction.BY_2D_DATA) {
            int angle = (int) dir.getOpposite().toYRot();
            grinderBuilder.part().modelFile(modelGrinder).rotationY(angle).addModel().condition(Grinder.FACING, dir).end()
                    .part().modelFile(modelGrinderActive).rotationY(angle).addModel().condition(Grinder.FACING, dir).condition(Grinder.ACTIVE, true).end();
        }


        if (true) return;

        // Unnecessarily complicated example to showcase how manual building works
        ModelFile birchFenceGate = models().fenceGate("birch_fence_gate", mcLoc("block/birch_planks"));
        ModelFile birchFenceGateOpen = models().fenceGateOpen("birch_fence_gate_open", mcLoc("block/birch_planks"));
        ModelFile birchFenceGateWall = models().fenceGateWall("birch_fence_gate_wall", mcLoc("block/birch_planks"));
        ModelFile birchFenceGateWallOpen = models().fenceGateWallOpen("birch_fence_gate_wall_open", mcLoc("block/birch_planks"));
        ModelFile invisbleModel = new ModelFile.UncheckedModelFile(new ResourceLocation("builtin/generated"));
        VariantBlockStateBuilder builder = getVariantBuilder(Blocks.BIRCH_FENCE_GATE);
        for (Direction dir : FenceGateBlock.FACING.getPossibleValues()) {
            int angle = (int) dir.toYRot();
            builder
                    .partialState()
                    .with(FenceGateBlock.FACING, dir)
                    .with(FenceGateBlock.IN_WALL, false)
                    .with(FenceGateBlock.OPEN, false)
                    .modelForState()
                    .modelFile(invisbleModel)
                    .nextModel()
                    .modelFile(birchFenceGate)
                    .rotationY(angle)
                    .uvLock(true)
                    .weight(100)
                    .addModel()
                    .partialState()
                    .with(FenceGateBlock.FACING, dir)
                    .with(FenceGateBlock.IN_WALL, false)
                    .with(FenceGateBlock.OPEN, true)
                    .modelForState()
                    .modelFile(birchFenceGateOpen)
                    .rotationY(angle)
                    .uvLock(true)
                    .addModel()
                    .partialState()
                    .with(FenceGateBlock.FACING, dir)
                    .with(FenceGateBlock.IN_WALL, true)
                    .with(FenceGateBlock.OPEN, false)
                    .modelForState()
                    .modelFile(birchFenceGateWall)
                    .rotationY(angle)
                    .uvLock(true)
                    .addModel()
                    .partialState()
                    .with(FenceGateBlock.FACING, dir)
                    .with(FenceGateBlock.IN_WALL, true)
                    .with(FenceGateBlock.OPEN, true)
                    .modelForState()
                    .modelFile(birchFenceGateWallOpen)
                    .rotationY(angle)
                    .uvLock(true)
                    .addModel();
        }

        // Realistic examples using helpers
//            simpleBlock(Blocks.STONE, model -> ObjectArrays.concat(
//                    ConfiguredModel.allYRotations(model, 0, false),
//                    ConfiguredModel.allYRotations(model, 180, false),
//                    ConfiguredModel.class));

        // From here on, models are 1-to-1 copies of vanilla (except for model locations) and will be tested as such below
//        ModelFile block = models().getBuilder("block").transforms()
//                .transform(TransformType.GUI)
//                .rotation(30, 225, 0)
//                .scale(0.625f)
//                .end()
//                .transform(TransformType.GROUND)
//                .translation(0, 3, 0)
//                .scale(0.25f)
//                .end()
//                .transform(TransformType.FIXED)
//                .scale(0.5f)
//                .end()
//                .transform(TransformType.THIRDPERSON_RIGHT)
//                .rotation(75, 45, 0)
//                .translation(0, 2.5f, 0)
//                .scale(0.375f)
//                .end()
//                .transform(TransformType.FIRSTPERSON_RIGHT)
//                .rotation(0, 45, 0)
//                .scale(0.4f)
//                .end()
//                .transform(TransformType.FIRSTPERSON_LEFT)
//                .rotation(0, 225, 0)
//                .scale(0.4f)
//                .end()
//                .end();

//        models().getBuilder("cube")
//                .parent(block)
//                .element()
//                .allFaces((dir, face) -> face.texture("#" + dir.getSerializedName()).cullface(dir));

        ModelFile furnace = models().orientable("furnace", mcLoc("block/furnace_side"), mcLoc("block/furnace_front"), mcLoc("block/furnace_top"));
        ModelFile furnaceLit = models().orientable("furnace_on", mcLoc("block/furnace_side"), mcLoc("block/furnace_front_on"), mcLoc("block/furnace_top"));

        getVariantBuilder(Blocks.FURNACE)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(FurnaceBlock.LIT) ? furnaceLit : furnace)
                        .rotationY((int) state.getValue(FurnaceBlock.FACING).getOpposite().toYRot())
                        .build()
                );

        ModelFile barrel = models().cubeBottomTop("barrel", mcLoc("block/barrel_side"), mcLoc("block/barrel_bottom"), mcLoc("block/barrel_top"));
        ModelFile barrelOpen = models().cubeBottomTop("barrel_open", mcLoc("block/barrel_side"), mcLoc("block/barrel_bottom"), mcLoc("block/barrel_top_open"));
        directionalBlock(Blocks.BARREL, state -> state.getValue(BarrelBlock.OPEN) ? barrelOpen : barrel); // Testing custom state interpreter

//        logBlock((LogBlock) Blocks.ACACIA_LOG);

        stairsBlock((StairBlock) Blocks.ACACIA_STAIRS, "acacia", mcLoc("block/acacia_planks"));
        slabBlock((SlabBlock) Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS.getRegistryName(), mcLoc("block/acacia_planks"));

        fenceBlock((FenceBlock) Blocks.ACACIA_FENCE, "acacia", mcLoc("block/acacia_planks"));
        fenceGateBlock((FenceGateBlock) Blocks.ACACIA_FENCE_GATE, "acacia", mcLoc("block/acacia_planks"));

        wallBlock((WallBlock) Blocks.COBBLESTONE_WALL, "cobblestone", mcLoc("block/cobblestone"));

        paneBlock((IronBarsBlock) Blocks.GLASS_PANE, "glass", mcLoc("block/glass"), mcLoc("block/glass_pane_top"));

        doorBlock((DoorBlock) Blocks.ACACIA_DOOR, "acacia", mcLoc("block/acacia_door_bottom"), mcLoc("block/acacia_door_top"));
        trapdoorBlock((TrapDoorBlock) Blocks.ACACIA_TRAPDOOR, "acacia", mcLoc("block/acacia_trapdoor"), true);
        trapdoorBlock((TrapDoorBlock) Blocks.OAK_TRAPDOOR, "oak", mcLoc("block/oak_trapdoor"), false); // Test a non-orientable trapdoor

        simpleBlock(Blocks.TORCH, models().torch("torch", mcLoc("block/torch")));
        horizontalBlock(Blocks.WALL_TORCH, models().torchWall("wall_torch", mcLoc("block/torch")), 90);
    }


    private void dummyBlock(Block block) {
        ModelFile model = models()//
                .withExistingParent("dummy", "block")//
                .texture("particle", "minecraft:block/glass");
        simpleBlock(block, model);
    }

    public void directionalFromNorth(Block block, ModelFile model) {
        directionalFromNorth(block, model, 180);
    }

    public void directionalFromNorth(Block block, ModelFile model, int angleOffset) {
        directionalFromNorth(block, $ -> model, angleOffset);
    }

    public void directionalFromNorth(Block block, Function<BlockState, ModelFile> modelFunc) {
        directionalFromNorth(block, modelFunc, 180);
    }

    public void directionalFromNorth(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == DOWN ? 90 : dir == UP ? -90 : 0)
                            .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
                            .build();
                });
    }

    public void multiLayerBlock(Block block, ResourceLocation solid, ResourceLocation overlay) {
        simpleBlock(block,
                models().getBuilder(block.getRegistryName().getPath())
                        .parent(models().getExistingFile(mcLoc("block/block")))
                        .customLoader(MultiLayerModelBuilder::begin)
                        .submodel(RenderType.solid(), models().nested().parent(models().getExistingFile(mcLoc("block/cube_all"))).texture("all", solid))
                        .submodel(RenderType.cutoutMipped(), models().nested().parent(models().getExistingFile(mcLoc("block/cube_all"))).texture("all", overlay))
                        .end());
    }

    @Override
    public String getName() {
        return "Draconic Evolution Blockstates";
    }
}
