package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.DislocatorReceptacle;
import com.brandon3055.draconicevolution.blocks.Portal;
import com.brandon3055.draconicevolution.blocks.RainSensor;
import com.brandon3055.draconicevolution.blocks.machines.EnergyPylon;
import com.brandon3055.draconicevolution.blocks.machines.Generator;
import com.brandon3055.draconicevolution.blocks.machines.Grinder;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.UP;

/**
 * Created by brandon3055 on 28/2/20.
 */
public class BlockStateGenerator extends BlockStateProvider {
    private static final Logger LOGGER = LogManager.getLogger();

    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen.getPackOutput(), DraconicEvolution.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //Simple Blocks
        simpleBlock(DEContent.DRACONIUM_BLOCK);
        simpleBlock(DEContent.AWAKENED_DRACONIUM_BLOCK, models().cubeBottomTop("awakened_draconium_block", modLoc("block/awakened_draconium_block_side"), modLoc("block/awakened_draconium_block"), modLoc("block/awakened_draconium_block")));
        simpleBlock(DEContent.INFUSED_OBSIDIAN);
        simpleBlock(DEContent.ENERGY_CORE);
        simpleBlock(DEContent.ENERGY_CORE_STABILIZER, models().getExistingFile(modLoc("block/energy_core_stabilizer")));
        simpleBlock(DEContent.CREATIVE_OP_CAPACITOR);
        simpleBlock(DEContent.STABILIZED_SPAWNER, models().getExistingFile(modLoc("block/stabilized_spawner")));
        simpleBlock(DEContent.PARTICLE_GENERATOR, models().getExistingFile(modLoc("block/particle_generator")));
        simpleBlock(DEContent.CRAFTING_CORE, models().getExistingFile(modLoc("block/crafting/fusion_crafting_core")));
        simpleBlock(DEContent.DISLOCATION_INHIBITOR, models().cubeBottomTop("dislocation_inhibitor", modLoc("block/dislocation_inhibitor"), modLoc("block/parts/machine_top"), modLoc("block/parts/machine_top")));

        //TODO
//        multiLayerBlock(DEContent.OVERWORLD_DRACONIUM_ORE, mcLoc("block/stone"), modLoc("block/draconium_ore_overlay"));
//        multiLayerBlock(DEContent.NETHER_DRACONIUM_ORE, mcLoc("block/netherrack"), modLoc("block/draconium_ore_overlay"));
//        multiLayerBlock(DEContent.END_DRACONIUM_ORE, mcLoc("block/end_stone"), modLoc("block/draconium_ore_overlay"));
//        multiLayerBlock(DEContent.DEEPSLATE_DRACONIUM_ORE, mcLoc("block/deepslate"), modLoc("block/draconium_ore_overlay"));

        simpleBlock(DEContent.OVERWORLD_DRACONIUM_ORE);
        simpleBlock(DEContent.NETHER_DRACONIUM_ORE);
        simpleBlock(DEContent.END_DRACONIUM_ORE);
        simpleBlock(DEContent.DEEPSLATE_DRACONIUM_ORE);

        directionalBlock(DEContent.BASIC_CRAFTING_INJECTOR, models().getExistingFile(modLoc("block/crafting/crafting_injector_draconium")));
        directionalBlock(DEContent.WYVERN_CRAFTING_INJECTOR, models().getExistingFile(modLoc("block/crafting/crafting_injector_wyvern")));
        directionalBlock(DEContent.AWAKENED_CRAFTING_INJECTOR, models().getExistingFile(modLoc("block/crafting/crafting_injector_draconic")));
        directionalBlock(DEContent.CHAOTIC_CRAFTING_INJECTOR, models().getExistingFile(modLoc("block/crafting/crafting_injector_chaotic")));

        directionalFromNorth(DEContent.FLUID_GATE, models().getExistingFile(modLoc("block/fluid_gate")));
        directionalFromNorth(DEContent.FLUX_GATE, models().getExistingFile(modLoc("block/flux_gate")));

        getVariantBuilder(DEContent.RAIN_SENSOR).forAllStates(state -> ConfiguredModel.builder().modelFile(models().getExistingFile(state.getValue(RainSensor.ACTIVE) ? modLoc("block/rain_sensor_active") : modLoc("block/rain_sensor"))).build());

        directionalBlock(DEContent.POTENTIOMETER, models().getExistingFile(modLoc("block/potentiometer")));

        simpleBlock(DEContent.ENERGY_TRANSFUSER, models().getExistingFile(modLoc("block/energy_transfuser")));

        simpleBlock(DEContent.DISENCHANTER, models().getExistingFile(modLoc("block/disenchanter")));
        simpleBlock(DEContent.CELESTIAL_MANIPULATOR, models().getExistingFile(modLoc("block/celestial_manipulator")));
        simpleBlock(DEContent.ENTITY_DETECTOR, models().getExistingFile(modLoc("block/entity_detector")));
        simpleBlock(DEContent.ENTITY_DETECTOR_ADVANCED, models().getExistingFile(modLoc("block/entity_detector_advanced")));

        dummyBlock(DEContent.BASIC_IO_CRYSTAL);
        dummyBlock(DEContent.WYVERN_IO_CRYSTAL);
        dummyBlock(DEContent.DRACONIC_IO_CRYSTAL);
//        dummyBlock(DEContent.  CRYSTAL_IO_CHAOTIC);
        dummyBlock(DEContent.BASIC_RELAY_CRYSTAL);
        dummyBlock(DEContent.WYVERN_RELAY_CRYSTAL);
        dummyBlock(DEContent.DRACONIC_RELAY_CRYSTAL);
//        dummyBlock(DEContent.  CRYSTAL_RELAY_CHAOTIC);
        dummyBlock(DEContent.BASIC_WIRELESS_CRYSTAL);
        dummyBlock(DEContent.WYVERN_WIRELESS_CRYSTAL);
        dummyBlock(DEContent.DRACONIC_WIRELESS_CRYSTAL);
//        dummyBlock(DEContent.  CRYSTAL_WIRELESS_CHAOTIC);
        dummyBlock(DEContent.STRUCTURE_BLOCK);
        dummyBlock(DEContent.CHAOS_CRYSTAL);
        dummyBlock(DEContent.PLACED_ITEM);
        dummyBlock(DEContent.CHAOS_CRYSTAL_PART);
        dummyBlock(DEContent.COMET_SPAWNER);

        dummyBlock(DEContent.DRACONIUM_CHEST);
        dummyBlock(DEContent.REACTOR_CORE);
        dummyBlock(DEContent.REACTOR_STABILIZER);
        dummyBlock(DEContent.REACTOR_INJECTOR);

        VariantBlockStateBuilder pylonBuilder = getVariantBuilder(DEContent.ENERGY_PYLON);
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

        simpleBlock(DEContent.DISLOCATOR_PEDESTAL, models().getExistingFile(modLoc("block/dislocator_pedestal")));

        VariantBlockStateBuilder receptacleBuilder = getVariantBuilder(DEContent.DISLOCATOR_RECEPTACLE);
        receptacleBuilder.addModels(receptacleBuilder.partialState().with(DislocatorReceptacle.CAMO, true), ConfiguredModel.builder().modelFile(models().cubeAll("infused_obsidian", modLoc("block/infused_obsidian"))).build());
        receptacleBuilder.addModels(receptacleBuilder.partialState().with(DislocatorReceptacle.ACTIVE, false).with(DislocatorReceptacle.CAMO, false), ConfiguredModel.builder().modelFile(models().cubeAll("dislocator_receptacle_inactive", modLoc("block/dislocator_receptacle_inactive"))).build());
        receptacleBuilder.addModels(receptacleBuilder.partialState().with(DislocatorReceptacle.ACTIVE, true).with(DislocatorReceptacle.CAMO, false), ConfiguredModel.builder().modelFile(models().cubeAll("dislocator_receptacle_active", modLoc("block/dislocator_receptacle_active"))).build());

        //Generate portal block state
        ModelFile portalModel = models().getExistingFile(modLoc("block/portal/portal"));
        ModelFile portalWallX = models().getExistingFile(modLoc("block/portal/portal_wall_x"));
        ModelFile portalWallY = models().getExistingFile(modLoc("block/portal/portal_wall_y"));
        ModelFile portalWallZ = models().getExistingFile(modLoc("block/portal/portal_wall_z"));
        MultiPartBlockStateBuilder portalBuilder = getMultipartBuilder(DEContent.PORTAL);
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
        MultiPartBlockStateBuilder generatorBuilder = getMultipartBuilder(DEContent.GENERATOR);
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
        MultiPartBlockStateBuilder grinderBuilder = getMultipartBuilder(DEContent.GRINDER);

         Direction[] BY_2D_DATA = Arrays.stream(Direction.values())
                .filter(p_235685_ -> p_235685_.getAxis().isHorizontal())
                .sorted(Comparator.comparingInt(Direction::get2DDataValue))
                .toArray(Direction[]::new);

        for (Direction dir : BY_2D_DATA) {
            int angle = (int) dir.getOpposite().toYRot();
            grinderBuilder.part().modelFile(modelGrinder).rotationY(angle).addModel().condition(Grinder.FACING, dir).end()
                    .part().modelFile(modelGrinderActive).rotationY(angle).addModel().condition(Grinder.FACING, dir).condition(Grinder.ACTIVE, true).end();
        }
    }


    private void dummyBlock(Supplier<? extends Block> block) {
        ModelFile model = models()//
                .withExistingParent("dummy", "block")//
                .texture("particle", "minecraft:block/glass");
        simpleBlock(block.get(), model);
    }

    public void directionalFromNorth(Supplier<? extends Block> block, ModelFile model) {
        directionalFromNorth(block, model, 180);
    }

    public void directionalFromNorth(Supplier<? extends Block> block, ModelFile model, int angleOffset) {
        directionalFromNorth(block, $ -> model, angleOffset);
    }

    public void directionalFromNorth(Supplier<? extends Block> block, Function<BlockState, ModelFile> modelFunc) {
        directionalFromNorth(block, modelFunc, 180);
    }

    public void directionalFromNorth(Supplier<? extends Block> block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        getVariantBuilder(block.get())
                .forAllStates(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == DOWN ? 90 : dir == UP ? -90 : 0)
                            .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
                            .build();
                });
    }

    public void multiLayerBlock(Supplier<? extends Block> block, ResourceLocation solid, ResourceLocation overlay) {

        simpleBlock(block,
                models().getBuilder(BuiltInRegistries.BLOCK.getKey(block.get()).getPath())
                        .parent(models().getExistingFile(mcLoc("block/block"))).texture("particle", solid)
                        .customLoader(CompositeModelBuilder::begin)
                        .child("base", models().nested().parent(models().getExistingFile(mcLoc("block/cube_all"))).renderType("solid").texture("all", solid))
                        .child("overlay", models().nested().parent(models().getExistingFile(mcLoc("block/cube_all"))).renderType("cutout_mipped").texture("all", overlay))
                        .end());
    }

    public MultiPartBlockStateBuilder getMultipartBuilder(Supplier<? extends Block> b) {
        return super.getMultipartBuilder(b.get());
    }

    public void simpleBlock(Supplier<? extends Block> block, ModelFile model) {
        super.simpleBlock(block.get(), model);
    }

    public VariantBlockStateBuilder getVariantBuilder(Supplier<? extends Block> b) {
        return super.getVariantBuilder(b.get());
    }

    public void directionalBlock(Supplier<? extends Block> block, ModelFile model) {
        super.directionalBlock(block.get(), model);
    }

    public void simpleBlock(Supplier<? extends Block> block) {
        super.simpleBlock(block.get());
    }

    @Override
    public String getName() {
        return "Draconic Evolution Blockstates";
    }
}
