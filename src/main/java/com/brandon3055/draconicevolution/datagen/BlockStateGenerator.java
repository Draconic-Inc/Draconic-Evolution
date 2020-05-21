package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.machines.EnergyPylon;
import com.brandon3055.draconicevolution.blocks.machines.Generator;
import com.brandon3055.draconicevolution.blocks.machines.Grinder;
import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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
        simpleBlock(DEContent.energy_core);
        simpleBlock(DEContent.energy_core_stabilizer, models().getExistingFile(modLoc("block/energy_core_stabilizer")));
        simpleBlock(DEContent.creative_op_capacitor);
        simpleBlock(DEContent.stabilized_spawner, models().getExistingFile(modLoc("block/stabilized_spawner")));

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
        for (Direction dir : FenceGateBlock.HORIZONTAL_FACING.getAllowedValues()) {
            int angle = (int) dir.getOpposite().getHorizontalAngle();
            grinderBuilder.part().modelFile(modelGrinder).rotationY(angle).addModel().condition(Grinder.FACING, dir).end()
                    .part().modelFile(modelGrinderActive).rotationY(angle).addModel().condition(Grinder.FACING, dir).condition(Grinder.ACTIVE, true).end();
        }





//            getVariantBuilder(DEContent.grinder)
//                    .forAllStates(state -> ConfiguredModel.builder()
//                            .modelFile(modelGrinder)
//                            .rotationY((int) state.get(Generator.FACING).getOpposite().getHorizontalAngle())
//                            .build());


//            ModelFile modelGenerator = getExistingFile(modLoc("block/generator/generator"));
//            ModelFile modelGeneratorFlame = getExistingFile(modLoc("block/generator/generator_flame"));
//            MultiPartBlockStateBuilder generatorBuilder = getMultipartBuilder(DEContent.generator);
//            for (Direction dir : FenceGateBlock.HORIZONTAL_FACING.getAllowedValues()) {
//                int angle = (int) dir.getOpposite().getHorizontalAngle();
//                generatorBuilder.part().modelFile(modelGenerator).rotationY(angle).addModel().condition(Generator.FACING, dir).end()
//                        .part().modelFile(modelGeneratorFlame).rotationY(angle).addModel().condition(Generator.FACING, dir).condition(Generator.ACTIVE, true).end();
//            }

//            VariantBlockStateBuilder

//            getVariantBuilder(DEContent.generator)
//                    .forAllStates(state -> {
//                        Builder<?> bdr = ConfiguredModel.builder()
//                                .modelFile(modelGenerator)
//                                .rotationY((int) state.get(Generator.FACING).getOpposite().getHorizontalAngle());
//                        if (state.get(Generator.ACTIVE)) {
//                            bdr = bdr.nextModel().modelFile(modelGeneratorFlame)
//                                    .rotationY((int) state.get(Generator.FACING).getOpposite().getHorizontalAngle());
//                        }
//                        return bdr.build();
//                    });


//            getVariantBuilder(Blocks.FURNACE)
//                    .forAllStates(state -> ConfiguredModel.builder()
//                            .modelFile(state.get(FurnaceBlock.LIT) ? furnaceLit : furnace)
//                            .rotationY((int) state.get(FurnaceBlock.FACING).getOpposite().getHorizontalAngle())
//                            .build()
//                    );


//            simpleBlock(DEContent.generator, );


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
                .allFaces((dir, face) -> face.texture("#" + dir.getName()).cullface(dir));

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

        logBlock((LogBlock) Blocks.ACACIA_LOG);

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

    // Testing the outputs

//        private static final Set<Block> IGNORED_BLOCKS = ImmutableSet.of(Blocks.BIRCH_FENCE_GATE, Blocks.STONE);
//        private static final Set<ResourceLocation> IGNORED_MODELS = ImmutableSet.of();

    private List<String> errors = new ArrayList<>();

//        @Override
//        public void act(DirectoryCache cache) throws IOException {
//            super.act(cache);
////            this.errors.addAll(testModelResults(this.generatedModels, existingFileHelper, IGNORED_MODELS));
//            this.registeredBlocks.forEach((block, state) -> {
////                if (IGNORED_BLOCKS.contains(block)) return;
//                JsonObject generated = state.toJson();
//                try {
//                    IResource vanillaResource = existingFileHelper.getResource(block.getRegistryName(), ResourcePackType.CLIENT_RESOURCES, ".json", "blockstates");
//                    JsonObject existing = GSON.fromJson(new InputStreamReader(vanillaResource.getInputStream()), JsonObject.class);
//                    if (state instanceof VariantBlockStateBuilder) {
//                        compareVariantBlockstates(block, generated, existing);
//                    } else if (state instanceof MultiPartBlockStateBuilder) {
//                        compareMultipartBlockstates(block, generated, existing);
//                    } else {
//                        throw new IllegalStateException("Unknown blockstate type: " + state.getClass());
//                    }
//                }
//                catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            if (!errors.isEmpty()) {
//                LOGGER.error("Found {} discrepancies between generated and vanilla models/blockstates: ", errors.size());
//                for (String s : errors) {
//                    LOGGER.error("    {}", s);
//                }
//                throw new AssertionError("Generated blockstates/models differed from vanilla equivalents, check above errors.");
//            }
//        }
//
//        private void compareVariantBlockstates(Block block, JsonObject generated, JsonObject vanilla) {
//            JsonObject generatedVariants = generated.getAsJsonObject("variants");
//            JsonObject vanillaVariants = vanilla.getAsJsonObject("variants");
//            Stream.concat(generatedVariants.entrySet().stream(), vanillaVariants.entrySet().stream())
//                    .map(e -> e.getKey())
//                    .distinct()
//                    .forEach(key -> {
//                        JsonElement generatedVariant = generatedVariants.get(key);
//                        JsonElement vanillaVariant = vanillaVariants.get(key);
//                        if (generatedVariant.isJsonArray()) {
//                            compareArrays(block, "key " + key, "random variants", generatedVariant, vanillaVariant);
//                            for (int i = 0; i < generatedVariant.getAsJsonArray().size(); i++) {
//                                compareVariant(block, key + "[" + i + "]", generatedVariant.getAsJsonArray().get(i).getAsJsonObject(), vanillaVariant.getAsJsonArray().get(i).getAsJsonObject());
//                            }
//                        }
//                        if (generatedVariant.isJsonObject()) {
//                            if (!vanillaVariant.isJsonObject()) {
//                                blockstateError(block, "incorrectly does not have an array of variants for key %s", key);
//                                return;
//                            }
//                            compareVariant(block, key, generatedVariant.getAsJsonObject(), vanillaVariant.getAsJsonObject());
//                        }
//                    });
//        }

//        private void compareVariant(Block block, String key, JsonObject generatedVariant, JsonObject vanillaVariant) {
//            if (generatedVariant == null) {
//                blockstateError(block, "missing variant for %s", key);
//                return;
//            }
//            if (vanillaVariant == null) {
//                blockstateError(block, "has extra variant %s", key);
//                return;
//            }
//            String generatedModel = toVanillaModel(generatedVariant.get("model").getAsString());
//            String vanillaModel = vanillaVariant.get("model").getAsString();
//            if (!generatedModel.equals(vanillaModel)) {
//                blockstateError(block, "has incorrect model \"%s\" for variant %s. Expecting: %s", generatedModel, key, vanillaModel);
//                return;
//            }
//            generatedVariant.addProperty("model", generatedModel);
//            // Parse variants to objects to handle default values in vanilla jsons
//            Variant parsedGeneratedVariant = GSON.fromJson(generatedVariant, Variant.class);
//            Variant parsedVanillaVariant = GSON.fromJson(vanillaVariant, Variant.class);
//            if (!parsedGeneratedVariant.equals(parsedVanillaVariant)) {
//                blockstateError(block, "has incorrect variant %s. Expecting: %s, Found: %s", key, vanillaVariant, generatedVariant);
//                return;
//            }
//        }
//
//        private void compareMultipartBlockstates(Block block, JsonObject generated, JsonObject vanilla) {
//            JsonElement generatedPartsElement = generated.get("multipart");
//            JsonElement vanillaPartsElement = vanilla.getAsJsonArray("multipart");
//            compareArrays(block, "parts", "multipart", generatedPartsElement, vanillaPartsElement);
//            // String instead of JSON types due to inconsistent hashing
//            Multimap<String, String> generatedPartsByCondition = HashMultimap.create();
//            Multimap<String, String> vanillaPartsByCondition = HashMultimap.create();
//
//            JsonArray generatedParts = generatedPartsElement.getAsJsonArray();
//            JsonArray vanillaParts = vanillaPartsElement.getAsJsonArray();
//            for (int i = 0; i < generatedParts.size(); i++) {
//                JsonObject generatedPart = generatedParts.get(i).getAsJsonObject();
//                String generatedCondition = toEquivalentString(generatedPart.get("when"));
//                JsonElement generatedVariants = generatedPart.get("apply");
//                if (generatedVariants.isJsonObject()) {
//                    correctVariant(generatedVariants.getAsJsonObject());
//                } else if (generatedVariants.isJsonArray()) {
//                    for (int j = 0; j < generatedVariants.getAsJsonArray().size(); j++) {
//                        correctVariant(generatedVariants.getAsJsonArray().get(i).getAsJsonObject());
//                    }
//                }
//                generatedPartsByCondition.put(generatedCondition, toEquivalentString(generatedVariants));
//
//                JsonObject vanillaPart = vanillaParts.get(i).getAsJsonObject();
//                String vanillaCondition = toEquivalentString(vanillaPart.get("when"));
//                String vanillaVariants = toEquivalentString(vanillaPart.get("apply"));
//
//                vanillaPartsByCondition.put(vanillaCondition, vanillaVariants);
//            }
//
//            Stream.concat(generatedPartsByCondition.keySet().stream(), vanillaPartsByCondition.keySet().stream())
//                    .distinct()
//                    .forEach(cond -> {
//                        Collection<String> generatedVariants = generatedPartsByCondition.get(cond);
//                        Collection<String> vanillaVariants = vanillaPartsByCondition.get(cond);
//                        if (generatedVariants.size() != vanillaVariants.size()) {
//                            if (vanillaVariants.isEmpty()) {
//                                blockstateError(block, " has extra condition %s", cond);
//                            } else if (generatedVariants.isEmpty()) {
//                                blockstateError(block, " is missing condition %s", cond);
//                            } else {
//                                blockstateError(block, " has differing amounts of variant lists matching condition %s. Expected: %d, Found: %d", cond, vanillaVariants.size(), generatedVariants.size());
//                            }
//                            return;
//                        }
//
//                        if (!vanillaVariants.containsAll(generatedVariants) || !generatedVariants.containsAll(vanillaVariants)) {
//                            List<String> extra = new ArrayList<>(generatedVariants);
//                            extra.removeAll(vanillaVariants);
//                            List<String> missing = new ArrayList<>(vanillaVariants);
//                            missing.removeAll(generatedVariants);
//                            if (!extra.isEmpty()) {
//                                blockstateError(block, " has extra variants for condition %s: %s", cond, extra);
//                            }
//                            if (!missing.isEmpty()) {
//                                blockstateError(block, " has missing variants for condition %s: %s", cond, missing);
//                            }
//                        }
//                    });
//        }

//        // Eliminate some formatting differences that are not meaningful
//        private String toEquivalentString(JsonElement element) {
//            return Objects.toString(element)
//                    .replaceAll("\"(true|false)\"", "$1") // Unwrap booleans in strings
//                    .replaceAll("\"(-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)\"", "$1"); // Unwrap numbers in strings, regex from https://stackoverflow.com/questions/13340717/json-numbers-regular-expression
//        }
//
//        private void correctVariant(JsonObject variant) {
//            variant.addProperty("model", toVanillaModel(variant.get("model").getAsString()));
//        }
//
//        private boolean compareArrays(Block block, String key, String name, JsonElement generated, JsonElement vanilla) {
//            if (!vanilla.isJsonArray()) {
//                blockstateError(block, "incorrectly has an array of %s for %s", name, key);
//                return false;
//            }
//            JsonArray generatedArray = generated.getAsJsonArray();
//            JsonArray vanillaArray = vanilla.getAsJsonArray();
//            if (generatedArray.size() != vanillaArray.size()) {
//                blockstateError(block, "has incorrect number of %s for %s. Expecting: %s, Found: %s", name, key, vanillaArray.size(), generatedArray.size());
//                return false;
//            }
//            return true;
//        }

//        private void blockstateError(Block block, String fmt, Object... args) {
//            errors.add("Generated blockstate for block " + block + " " + String.format(fmt, args));
//        }

    @Override
    public String getName() {
        return "Draconic Evolution Blockstates";
    }
}
