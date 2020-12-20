package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.machines.EnergyPylon;
import com.brandon3055.draconicevolution.blocks.machines.Generator;
import com.brandon3055.draconicevolution.blocks.machines.Grinder;
import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 28/2/20.
 */
public class BlockStateGenerator extends BlockStateProvider {
    private static final Logger LOGGER = LogManager.getLogger();

    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        //Simple Blocks
        simpleBlock(DEContent.block_draconium);
        simpleBlock(DEContent.block_draconium_awakened, models().cubeBottomTop("awakened_draconium_block", modLoc("block/awakened_draconium_block_side"), modLoc("block/awakened_draconium_block"), modLoc("block/awakened_draconium_block")));
        simpleBlock(DEContent.ore_draconium_end);
        simpleBlock(DEContent.ore_draconium_nether);
        simpleBlock(DEContent.ore_draconium_overworld);
        simpleBlock(DEContent.infused_obsidian);
        simpleBlock(DEContent.energy_core);
        simpleBlock(DEContent.energy_core_stabilizer, models().getExistingFile(modLoc("block/energy_core_stabilizer")));
        simpleBlock(DEContent.creative_op_capacitor);
        simpleBlock(DEContent.stabilized_spawner, models().getExistingFile(modLoc("block/stabilized_spawner")));

        simpleBlock(DEContent.particle_generator, models().getExistingFile(modLoc("block/particle_generator")));

        simpleBlock(DEContent.crafting_core, models().getExistingFile(modLoc("block/crafting/fusion_crafting_core")));
        directionalBlock(DEContent.crafting_injector_basic, models().getExistingFile(modLoc("block/crafting/crafting_injector_draconium")));
        directionalBlock(DEContent.crafting_injector_wyvern, models().getExistingFile(modLoc("block/crafting/crafting_injector_wyvern")));
        directionalBlock(DEContent.crafting_injector_awakened, models().getExistingFile(modLoc("block/crafting/crafting_injector_draconic")));
        directionalBlock(DEContent.crafting_injector_chaotic, models().getExistingFile(modLoc("block/crafting/crafting_injector_chaotic")));

        directionalFromNorth(DEContent.fluid_gate, models().getExistingFile(modLoc("block/fluid_gate")));
        directionalFromNorth(DEContent.flux_gate, models().getExistingFile(modLoc("block/flux_gate")));

        directionalBlock(DEContent.potentiometer, models().getExistingFile(modLoc("block/potentiometer")));

        simpleBlock(DEContent.energy_transfuser, models().getExistingFile(modLoc("block/energy_transfuser")));

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
        dummyBlock(DEContent.energy_core_structure);
        dummyBlock(DEContent.chaos_crystal);
        dummyBlock(DEContent.chaos_crystal_part);


        getVariantBuilder(DEContent.energy_pylon).forAllStates(state -> ConfiguredModel.builder().modelFile(models().cubeBottomTop(state.get(EnergyPylon.OUTPUT) ? "energy_pylon_output" : "energy_pylon_input", modLoc("block/energy_pylon/energy_pylon_" + (state.get(EnergyPylon.OUTPUT) ? "output" : "input")), modLoc("block/energy_pylon/energy_pylon_active_face"), modLoc("block/energy_pylon/energy_pylon_active_face"))).build());


        //Generator
        ModelFile modelGenerator = models().getExistingFile(modLoc("block/generator/generator"));
        ModelFile modelGeneratorFlame = models().getExistingFile(modLoc("block/generator/generator_flame"));
        MultiPartBlockStateBuilder generatorBuilder = getMultipartBuilder(DEContent.generator);
        for (Direction dir : FenceGateBlock.HORIZONTAL_FACING.getAllowedValues()) {
            int angle = (int) dir.getOpposite().getHorizontalAngle();
            generatorBuilder.part().modelFile(modelGenerator).rotationY(angle).addModel().condition(Generator.FACING, dir).end()
                    .part().modelFile(modelGeneratorFlame).rotationY(angle).addModel().condition(Generator.FACING, dir).condition(Generator.ACTIVE, true).end();
        }

        //Grinder
        ModelFile modelGrinder = models().getExistingFile(modLoc("block/grinder/grinder"));
        ModelFile modelGrinderActive = models().getExistingFile(modLoc("block/grinder/grinder_eyes"));
        MultiPartBlockStateBuilder grinderBuilder = getMultipartBuilder(DEContent.grinder);
        for (Direction dir : Direction.BY_HORIZONTAL_INDEX) {
            int angle = (int) dir.getOpposite().getHorizontalAngle();
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
        for (Direction dir : FenceGateBlock.HORIZONTAL_FACING.getAllowedValues()) {
            int angle = (int) dir.getHorizontalAngle();
            builder
                    .partialState()
                    .with(FenceGateBlock.HORIZONTAL_FACING, dir)
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
                    .with(FenceGateBlock.HORIZONTAL_FACING, dir)
                    .with(FenceGateBlock.IN_WALL, false)
                    .with(FenceGateBlock.OPEN, true)
                    .modelForState()
                    .modelFile(birchFenceGateOpen)
                    .rotationY(angle)
                    .uvLock(true)
                    .addModel()
                    .partialState()
                    .with(FenceGateBlock.HORIZONTAL_FACING, dir)
                    .with(FenceGateBlock.IN_WALL, true)
                    .with(FenceGateBlock.OPEN, false)
                    .modelForState()
                    .modelFile(birchFenceGateWall)
                    .rotationY(angle)
                    .uvLock(true)
                    .addModel()
                    .partialState()
                    .with(FenceGateBlock.HORIZONTAL_FACING, dir)
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
        ModelFile block = models().getBuilder("block").transforms()
                .transform(ModelBuilder.Perspective.GUI)
                .rotation(30, 225, 0)
                .scale(0.625f)
                .end()
                .transform(ModelBuilder.Perspective.GROUND)
                .translation(0, 3, 0)
                .scale(0.25f)
                .end()
                .transform(ModelBuilder.Perspective.FIXED)
                .scale(0.5f)
                .end()
                .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
                .rotation(75, 45, 0)
                .translation(0, 2.5f, 0)
                .scale(0.375f)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
                .rotation(0, 45, 0)
                .scale(0.4f)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
                .rotation(0, 225, 0)
                .scale(0.4f)
                .end()
                .end();

        models().getBuilder("cube")
                .parent(block)
                .element()
                .allFaces((dir, face) -> face.texture("#" + dir.getString()).cullface(dir));

        ModelFile furnace = models().orientable("furnace", mcLoc("block/furnace_side"), mcLoc("block/furnace_front"), mcLoc("block/furnace_top"));
        ModelFile furnaceLit = models().orientable("furnace_on", mcLoc("block/furnace_side"), mcLoc("block/furnace_front_on"), mcLoc("block/furnace_top"));

        getVariantBuilder(Blocks.FURNACE)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.get(FurnaceBlock.LIT) ? furnaceLit : furnace)
                        .rotationY((int) state.get(FurnaceBlock.FACING).getOpposite().getHorizontalAngle())
                        .build()
                );

        ModelFile barrel = models().cubeBottomTop("barrel", mcLoc("block/barrel_side"), mcLoc("block/barrel_bottom"), mcLoc("block/barrel_top"));
        ModelFile barrelOpen = models().cubeBottomTop("barrel_open", mcLoc("block/barrel_side"), mcLoc("block/barrel_bottom"), mcLoc("block/barrel_top_open"));
        directionalBlock(Blocks.BARREL, state -> state.get(BarrelBlock.PROPERTY_OPEN) ? barrelOpen : barrel); // Testing custom state interpreter

//        logBlock((LogBlock) Blocks.ACACIA_LOG);

        stairsBlock((StairsBlock) Blocks.ACACIA_STAIRS, "acacia", mcLoc("block/acacia_planks"));
        slabBlock((SlabBlock) Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS.getRegistryName(), mcLoc("block/acacia_planks"));

        fenceBlock((FenceBlock) Blocks.ACACIA_FENCE, "acacia", mcLoc("block/acacia_planks"));
        fenceGateBlock((FenceGateBlock) Blocks.ACACIA_FENCE_GATE, "acacia", mcLoc("block/acacia_planks"));

        wallBlock((WallBlock) Blocks.COBBLESTONE_WALL, "cobblestone", mcLoc("block/cobblestone"));

        paneBlock((PaneBlock) Blocks.GLASS_PANE, "glass", mcLoc("block/glass"), mcLoc("block/glass_pane_top"));

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
                    Direction dir = state.get(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                            .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.getHorizontalAngle()) + angleOffset) % 360)
                            .build();
                });
    }





















    @Override
    public String getName() {
        return "Draconic Evolution Blockstates";
    }
}
