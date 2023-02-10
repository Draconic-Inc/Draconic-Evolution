package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.blocks.tileentity.chest.SmeltingLogic.FeedMode;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEModules;

import net.minecraft.ChatFormatting;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 21/5/20.
 */
//@formatter:off
public class LangGenerator extends LanguageProvider {
    public LangGenerator(DataGenerator gen) {
        super(gen, MODID, "en_us");
    }

    private void blocks(PrefixHelper helper) {
        helper.add(DEContent.block_draconium,                                   "Draconium Block");
        helper.add(DEContent.block_draconium_awakened,                          "Awakened Draconium Block");
        helper.add(DEContent.chaos_crystal,                                     "Chaos Crystal");
        helper.add(DEContent.crafting_injector_basic,                           "Draconium Fusion Crafting Injector");
        helper.add(DEContent.crafting_injector_wyvern,                          "Wyvern Fusion Crafting Injector");
        helper.add(DEContent.crafting_injector_awakened,                        "Draconic Fusion Crafting Injector");
        helper.add(DEContent.crafting_injector_chaotic,                         "Chaotic Fusion Crafting Injector");
        helper.add(DEContent.crafting_core,                                     "Fusion Crafting Core");
        helper.add(DEContent.crystal_relay_basic,                               "Basic Energy Relay Crystal");
        helper.add(DEContent.crystal_relay_wyvern,                              "Wyvern Energy Relay Crystal");
        helper.add(DEContent.crystal_relay_draconic,                            "Draconic Energy Relay Crystal");
//        helper.add(DEContent.crystal_relay_chaotic,                             "Chaotic Energy Relay Crystal");
        helper.add(DEContent.crystal_io_basic,                                  "Basic Energy I/O Crystal");
        helper.add(DEContent.crystal_io_wyvern,                                 "Wyvern Energy I/O Crystal");
        helper.add(DEContent.crystal_io_draconic,                               "Draconic Energy I/O Crystal");
//        helper.add(DEContent.crystal_io_chaotic,                                "Chaotic Energy I/O Crystal");
        helper.add(DEContent.crystal_wireless_basic,                            "Basic Wireless Energy Crystal");
        helper.add(DEContent.crystal_wireless_wyvern,                           "Wyvern Wireless Energy Crystal");
        helper.add(DEContent.crystal_wireless_draconic,                         "Draconic Wireless Energy Crystal");
//        helper.add(DEContent.crystal_wireless_chaotic,                          "Chaotic Wireless Energy Crystal");
        helper.add(DEContent.creative_op_capacitor,                             "Creative Power Source");
        helper.add(DEContent.celestial_manipulator,                             "Celestial Manipulator");
        helper.add(DEContent.disenchanter,                                      "Disenchanter");
        helper.add(DEContent.dislocation_inhibitor,                             "Dislocation Normalization Field Projector");
        add("tile." + MODID + ".dislocation_inhibitor.info",                    "Prevents items dropped within 5 blocks from being collected by Item Dislocators.");
        helper.add(DEContent.dislocator_pedestal,                               "Dislocator Pedestal");
        helper.add(DEContent.dislocator_receptacle,                             "Dislocator Receptacle");
        helper.add(DEContent.draconium_chest,                                   "Draconium Chest");
        helper.add(DEContent.energy_transfuser,                                 "Energy Transfuser");
        helper.add(DEContent.entity_detector,                                   "Entity Detector");
        helper.add(DEContent.entity_detector_advanced,                          "Advanced Entity Detector");
        helper.add(DEContent.energy_core,                                       "Energy Core");
        helper.add(DEContent.energy_core_stabilizer,                            "Energy Core Stabilizer");
        helper.add(DEContent.energy_pylon,                                      "Energy Pylon");
        helper.add(DEContent.structure_block,                                   "");
        helper.add(DEContent.flux_gate,                                         "Flux Gate");
        helper.add(DEContent.fluid_gate,                                        "Fluid Gate");
        helper.add(DEContent.generator,                                         "Generator");
        helper.add(DEContent.grinder,                                           "Mob Grinder");
        helper.add(DEContent.infused_obsidian,                                  "Draconium Infused Obsidian");
        helper.add(DEContent.ore_draconium_overworld,                           "Draconium Ore");
        helper.add(DEContent.ore_draconium_deepslate,                           "Deepslate Draconium Ore");
        helper.add(DEContent.ore_draconium_nether,                              "Nether Draconium Ore");
        helper.add(DEContent.ore_draconium_end,                                 "Ender Draconium Ore");
        helper.add(DEContent.potentiometer,                                     "Potentiometer");
        helper.add(DEContent.particle_generator,                                "Particle Generator");
        helper.add(DEContent.placed_item,                                       "Placed Item");
        helper.add(DEContent.portal,                                            "Portal");
        helper.add(DEContent.reactor_core,                                      "Draconic Reactor Core");
        helper.add(DEContent.reactor_stabilizer,                                "Reactor Stabilizer");
        helper.add(DEContent.reactor_injector,                                  "Reactor Energy Injector");
        helper.add(DEContent.rain_sensor,                                       "Rain Sensor");
        helper.add(DEContent.stabilized_spawner,                                "Stabilized Mob Spawner");
    }

    private void items(PrefixHelper helper) {
        //Components
        helper.add(DEContent.dust_draconium,                                    "Draconium Dust");
        helper.add(DEContent.dust_draconium_awakened,                           "Awakened Draconium Dust");
        helper.add(DEContent.ingot_draconium,                                   "Draconium Ingot");
        helper.add(DEContent.ingot_draconium_awakened,                          "Awakened Draconium Ingot");
        helper.add(DEContent.nugget_draconium,                                  "Draconium Nugget");
        helper.add(DEContent.nugget_draconium_awakened,                         "Awakened Draconium Nugget");
        helper.add(DEContent.core_draconium,                                    "Draconium Core");
        helper.add(DEContent.core_wyvern,                                       "Wyvern Core");
        helper.add(DEContent.core_awakened,                                     "Draconic Core");
        helper.add(DEContent.core_chaotic,                                      "Chaotic Core");
        helper.add(DEContent.energy_core_wyvern,                                "Wyvern Energy Controller");
        helper.add(DEContent.energy_core_draconic,                              "Draconic Energy Controller");
        helper.add(DEContent.energy_core_chaotic,                               "Chaotic Energy Controller");
        helper.add(DEContent.dragon_heart,                                      "Dragon Heart");
        helper.add(DEContent.chaos_shard,                                       "Chaos Shard");
        helper.add(DEContent.chaos_frag_large,                                  "Large Chaos Fragment");
        helper.add(DEContent.chaos_frag_medium,                                 "Small Chaos Fragment");
        helper.add(DEContent.chaos_frag_small,                                  "Tiny Chaos Fragment");
        helper.add(DEContent.module_core,                                       "Module Core");
        helper.add(DEContent.reactor_prt_stab_frame,                            "Reactor Stabilizer Frame");
        helper.add(DEContent.reactor_prt_in_rotor,                              "Reactor Stabilizer Inner Rotor");
        helper.add(DEContent.reactor_prt_out_rotor,                             "Reactor Stabilizer Outer Rotor");
        helper.add(DEContent.reactor_prt_rotor_full,                            "Reactor Stabilizer Rotor Assembly");
        helper.add(DEContent.reactor_prt_focus_ring,                            "Reactor Stabilizer Focus Ring");
        //Misc Tools
        helper.add(DEContent.magnet,                                            "Item Dislocator");
        helper.add(DEContent.magnet_advanced,                                   "Awakened Item Dislocator");
        helper.add(DEContent.dislocator,                                        "Dislocator");
        helper.add(DEContent.dislocator_advanced,                               "Advanced Dislocator");
        helper.add(DEContent.dislocator_p2p,                                    "Bound Dislocator (Point-to-Point)");
        helper.add(DEContent.dislocator_p2p_unbound,                            "Un-Bound Dislocator (Point-to-Point)");
        helper.add(DEContent.dislocator_player,                                 "Bound Dislocator (Player)");
        helper.add(DEContent.dislocator_player_unbound,                         "Un-Bound Dislocator (Player)");
        helper.add(DEContent.crystal_binder,                                    "Crystal Binder");
        helper.add(DEContent.info_tablet,                                       "Information Tablet");
        helper.add(DEContent.ender_energy_manipulator,                          "Ender Energy Manipulator");
//        helper.add(DEContent.creative_exchanger,                                "Creative Exchanger");
        helper.add(DEContent.mob_soul,                                          "Soul");
        //Tools
        helper.add(DEContent.capacitor_wyvern,                                  "Wyvern Capacitor");
        helper.add(DEContent.capacitor_draconic,                                "Draconic Capacitor");
        helper.add(DEContent.capacitor_chaotic,                                 "Chaotic Capacitor");
        helper.add(DEContent.capacitor_creative,                                "Creative Capacitor");
        helper.add(DEContent.shovel_wyvern,                                     "Wyvern Shovel");
        helper.add(DEContent.shovel_draconic,                                   "Draconic Shovel");
        helper.add(DEContent.shovel_chaotic,                                    "Chaotic Shovel");
        helper.add(DEContent.hoe_wyvern,                                        "Wyvern Hoe");
        helper.add(DEContent.hoe_draconic,                                      "Draconic Hoe");
        helper.add(DEContent.hoe_chaotic,                                       "Chaotic Hoe");
        helper.add(DEContent.pickaxe_wyvern,                                    "Wyvern Pickaxe");
        helper.add(DEContent.pickaxe_draconic,                                  "Draconic Pickaxe");
        helper.add(DEContent.pickaxe_chaotic,                                   "Chaotic Pickaxe");
        helper.add(DEContent.axe_wyvern,                                        "Wyvern Axe");
        helper.add(DEContent.axe_draconic,                                      "Draconic Axe");
        helper.add(DEContent.axe_chaotic,                                       "Chaotic Axe");
        helper.add(DEContent.bow_wyvern,                                        "Wyvern Bow");
        helper.add(DEContent.bow_draconic,                                      "Draconic Bow");
        helper.add(DEContent.bow_chaotic,                                       "Chaotic Bow");
        helper.add(DEContent.sword_wyvern,                                      "Wyvern Sword");
        helper.add(DEContent.sword_draconic,                                    "Draconic Sword");
        helper.add(DEContent.sword_chaotic,                                     "Chaotic Sword");
        helper.add(DEContent.staff_draconic,                                    "Draconic Staff of Power");
        helper.add(DEContent.staff_chaotic,                                     "Chaotic Staff of Power");
        //Armor
        helper.add(DEContent.chestpiece_wyvern,                                 "Wyvern Chestpiece");
        helper.add(DEContent.chestpiece_draconic,                               "Draconic Chestpiece");
        helper.add(DEContent.chestpiece_chaotic,                                "Chaotic Chestpiece");
    }

    private void itemProps(PrefixHelper helper) {
        helper.setPrefix("item_prop." + MODID);
        helper.add("attack_aoe",                                                "Attack Radius");
        helper.add("attack_aoe.info",                                           "Allows you to adjust the area covered by this weapon's swing.\nThis covers a 100 degree arc in the direction you are looking.");
        helper.add("mining_aoe",                                                "Mining AOE");
        helper.add("mining_aoe.info",                                           "Allows you to adjust this tool's mining area.");
        helper.add("aoe_safe",                                                  "AOE Safe Mode");
        helper.add("aoe_safe.info",                                             "When enabled, this tool will not break anything if it detects a tile entity within its AOE range. This can help prevent you from accidentally breaking half of your base with a single misclick.");
        helper.add("aoe_safe.blocked",                                          ChatFormatting.BLUE + "(" + ChatFormatting.GREEN + "AOE Safe Mode is enabled" + ChatFormatting.BLUE + ")" + ChatFormatting.RED + "Operation cancelled due to TileEntity within AOE range.");

        helper.add("mining_speed",                                              "Dig Speed Multiplier");
        helper.add("mining_speed.info",                                         "Allows you to adjust how fast this tool breaks blocks. Useful if you need precision over speed.");
        helper.add("walk_speed",                                                "Walk Speed");
        helper.add("walk_speed.info",                                           "Allows you to adjust the speed boost that's applied when you walk.");
        helper.add("run_speed",                                                 "Sprint Speed");
        helper.add("run_speed.info",                                            "Allows you to adjust the speed boost that's applied when you sprint.");
        helper.add("jump_boost",                                                "Jump Boost");
        helper.add("jump_boost.info",                                           "Adjusts jump boost.");
        helper.add("jump_boost_run",                                            "Jump Boost - Running");
        helper.add("jump_boost_run.info",                                       "Adjusts jump boost while sprinting.");
        helper.add("night_vision.enabled",                                      "Night Vision Toggle");
        helper.add("night_vision.enabled.info",                                 "Allows you to toggle your night vision on and off. While enabled, any other source of night vision is overridden.");
        helper.add("night_vision.light_level",                                  "Night Vision Light Level");
        helper.add("night_vision.light_level.info",                             "Sets the light level at which the night vision will become active.  While active, it drains a small amount of OP every tick.");
        
        helper.add("shield_mod.enabled",                                        "Shield Toggle");
        helper.add("shield_mod.enabled.info",                                   "Allows you to disable your shield. This prevents your shield from using power but leaves you vulnerable to damage.");
        helper.add("shield_mod.always_visible",                                 "Shield Always Visible");
        helper.add("shield_mod.always_visible.info",                            "Purely cosmetic. If false, your shield will only be visible when it absorbs damage.");

        helper.add("feed_mod.consume_food",                                     "Consume Food");
        helper.add("feed_mod.consume_food.info",                                "When enabled, this module will automatically consume food from your inventory to fill its internal storage.");

        helper.add("junk_filter_mod.enabled",                                   "Incinerate");
        helper.add("ender_collection_mod.enabled",                              "Ender Collection");

        helper.add("flight_mod.elytra",                                         "Elytra Flight");
        helper.add("flight_mod.elytra.info",                                    "Allows you to enable / disable elytra flight.");
        helper.add("flight_mod.creative",                                       "Creative Flight");
        helper.add("flight_mod.creative.info",                                  "Allows you to enable / disable creative flight.");
        helper.add("flight_mod.elytra_boost",                                   "Elytra Boost");
        helper.add("flight_mod.elytra_boost.info",                              "Adjusts the power of Elytra speed boost.\nSpeed boost can be activated by pressing Forward + Sprint while flying.");

        helper.add("auto_fire_mod.enable",                                      "Auto Fire");
        helper.add("auto_fire_mod.enable.info",                                 "Automatically fire bow when fully drawn.");

        helper.add("charge_held_item",                                          "Charge Held Items");
        helper.add("charge_armor",                                              "Charge Armor");
        helper.add("charge_hot_bar",                                            "Charge Hotbar");
        helper.add("charge_main",                                               "Charge Main Inventory");
        helper.add("charge_curios",                                             "Charge Curios");

        helper.add("tree_harvest_mod.leaves",                                   "Harvest Leaves");
        helper.add("tree_harvest_mod.range",                                    "Harvest Radius");
    }

    private void modules(PrefixHelper helper) {
        helper.setPrefix("module." + MODID);
        helper.add(ModuleTypes.ENERGY_STORAGE,                                  "Energy Capacity");

        helper.add("energy.stored_energy",                                      "Stored Energy");
        helper.add("energy.capacity",                                           "Energy Capacity");
        helper.add("energy.capacity.value",                                     "+%s OP");
        helper.add("energy.transfer",                                           "Energy Transfer");
        helper.add("energy.transfer.value",                                     "+%s OP/t");

        helper.add(ModuleTypes.ENERGY_SHARE,                                    "Energy Share");
        helper.add(ModuleTypes.ENERGY_LINK,                                     "Energy Link");
        helper.add(ModuleTypes.SHIELD_CONTROLLER,                               "Shield Controller");
        helper.add(ModuleTypes.SHIELD_BOOST,                                    "Shield Boost");
        helper.add("shield_capacity.name",                                      "Shield Capacity");
        helper.add("shield_capacity.value",                                     "%s points");
        helper.add("shield_recharge.name",                                      "Shield Recharge");
        helper.add("shield_recharge.value",                                     "%s pps (%s seconds)\n@ %s OP/t");
        helper.add("shield_passive.name",                                       "Shield Operating Cost");
        helper.add("shield_passive.value",                                      "%s OP/t");
        helper.add("shield_control.name",                                       "Shield Cooldown");
        helper.add("shield_control.value",                                      "%s Seconds");

        helper.add(ModuleTypes.FLIGHT,                                          "Flight");
        helper.add("flight.name",                                               "Flight");
        helper.add("flight.true.false",                                         "Elytra");
        helper.add("flight.false.true",                                         "Creative");
        helper.add("flight.true.true",                                          "Creative & Elytra");
        helper.add("flight.boost.name",                                         "Elytra Boost");

        helper.add(ModuleTypes.UNDYING,                                         "Undying");
        helper.add("undying.health.name",                                       "Health Boost");
        helper.add("undying.health.value",                                      "%s Health points");
        helper.add("undying.shield.name",                                       "Shield Boost");
        helper.add("undying.shield.value",                                      "%s for %s seconds");
        helper.add("undying.charge.name",                                       "Charge Time");
        helper.add("undying.charge.value",                                      "%s Seconds");
        helper.add("undying.energy.name",                                       "Charge Energy");
        helper.add("undying.energy.value",                                      "%sOP @%s OP/t");
        helper.add("undying.invuln.name",                                       "Invulnerable Time");
        helper.add("undying.invuln.value",                                      "%s Seconds");
        helper.add("undying.invuln.active",                                     "Invulnerable for %s Seconds");

        helper.add(ModuleTypes.AUTO_FEED,                                       "Auto Feed");
        helper.add("auto_feed.name",                                            "Food Storage");
        helper.add("auto_feed.value",                                           "%s Hunger Points");
        helper.add("auto_feed.stored",                                          "Food Stored:");
        helper.add("auto_feed.stored.value",                                    "%s Hunger Points");

        helper.add(ModuleTypes.NIGHT_VISION,                                    "Night Vision");

        helper.add(ModuleTypes.JUMP_BOOST,                                      "Jump Boost");
        helper.add("jump.name",                                                 "Jump Boost");
        helper.add("jump.value",                                                "+%s%%");
//        add(ModuleTypes.FALL_PROTECT,                                           "Fall Protection");

        helper.add(ModuleTypes.AQUA_ADAPT,                                      "Aqua Adaptation");

        helper.add(ModuleTypes.MINING_STABILITY,                                "Mining Stabilizer");

        helper.add(ModuleTypes.AOE,                                             "AOE");
        helper.add("aoe.name",                                                  "AOE");
        helper.add("aoe.value",                                                 "%sx%s");

        helper.add(ModuleTypes.DAMAGE,                                          "Damage");
        helper.add("damage.name",                                               "Damage");
        helper.add("damage.attack",                                             "+%s Attack Damage");

        helper.add(ModuleTypes.SPEED,                                           "Speed");
        helper.add("speed.name",                                                "Speed");
        helper.add("speed.value",                                               "+%s%%");

        helper.add(ModuleTypes.HILL_STEP,                                       "Step Assist");

        helper.add(ModuleTypes.JUNK_FILTER,                                     "Junk Filter");

        helper.add("filtered_module.filter_slot",                               "Filter Slot");
        helper.add("filtered_module.filter_item",                               "Filter Item");
        helper.add("filtered_module.filter_tag",                                "Filter Tag");
        helper.add("filtered_module.set_item_filter",                           "Click with item to set item filter");
        helper.add("filtered_module.configure_slot",                            "Right click to set tag filter");
        helper.add("filtered_module.clear_slot",                                "Shift+Right click to clear");
        helper.add("filtered_module.matching",                                  "Matching Item(s)");

        helper.add("filtered_module.filter_by_tag",                             "Filter by item tag");
        helper.add("filtered_module.filter_example",                            "e.g. forge:stone");
        helper.add("filtered_module.select_or_enter",                           "Select tag");
        helper.add("filtered_module.select_from_item",                          "Select tag from item");

        helper.add(ModuleTypes.ENDER_COLLECTION,                                "Ender Collection");
        helper.add("ender_storage.about",                                       "Transfers collected items to your personal ender chest.");
        helper.add("ender_storage.about_compat",                                "Can be bound to an Ender Storage ender chest.");
        helper.add("ender_storage.about_compat2",                               "(Shift+Right Click ender chest with module)");
        helper.add("ender_storage.frequency",                                   "Frequency");
        helper.add("ender_storage.owner",                                       "Owner");
        helper.add("ender_storage.how_to_clear",                                "Shift right click module to clear");

        helper.add(ModuleTypes.TREE_HARVEST,                                    "Tree Harvest");
        helper.add("tree_harvest_range.name",                                   "Tree Harvest Range");
        helper.add("tree_harvest_range.value",                                  "%s Blocks");
        helper.add("tree_harvest_speed.name",                                   "Tree Harvest Speed");
        helper.add("tree_harvest_speed.value",                                  "%s Blocks/s");
        helper.add("tree_harvest.single",                                       "Single: Hold right click on tree.");
        helper.add("tree_harvest.area",                                         "Area: Hold right click in centre of target area.");

        helper.add(ModuleTypes.AUTO_FIRE,                                       "Auto Fire");

        helper.add(ModuleTypes.PROJ_ANTI_IMMUNE,                                "Projectile Immunity Cancellation");

        helper.add(ModuleTypes.PROJ_MODIFIER,                                   "Projectile Modifier");
//        helper.add(ModuleTypes.PROJ_VELOCITY,                                   "Projectile Velocity");
        helper.add("proj_velocity.name",                                        "Velocity");
        helper.add("proj_velocity.value",                                       "%s%% (~%s m/s)");

//        helper.add(ModuleTypes.PROJ_ACCURACY,                                   "Projectile Accuracy");
        helper.add("proj_accuracy.name",                                        "Inaccuracy");
        helper.add("proj_accuracy.value",                                       "%s%%");

//        helper.add(ModuleTypes.PROJ_GRAV_COMP,                                  "Projectile Gravity Compensation");
        helper.add("proj_grav_comp.name",                                       "Gravity Cancellation");
        helper.add("proj_grav_comp.value",                                      "%s%%");

//        helper.add(ModuleTypes.PROJ_PENETRATION,                                "Projectile Penetration");
        helper.add("proj_penetration.name",                                     "Penetration Chance");
        helper.add("proj_penetration.value",                                    "%s%%");
        helper.add("proj_penetration.info",                                     "Increases the chance a projectile will pass through a target and potentially hit additional targets.");
        helper.add("proj_penetration.info2",                                    "Decreases by 25%% for every subsequent target.");

        helper.add("proj_damage.name",                                          "Base Damage");
        helper.add("proj_damage.value",                                         "%s%%");

        helper.add(DEModules.draconiumEnergy,                                   "Energy Module");
        helper.add(DEModules.wyvernEnergy,                                      "Wyvern Energy Module");
        helper.add(DEModules.draconicEnergy,                                    "Draconic Energy Module");
        helper.add(DEModules.chaoticEnergy,                                     "Chaotic Energy Module");

        helper.add(DEModules.draconiumSpeed,                                    "Speed Module");
        helper.add(DEModules.wyvernSpeed,                                       "Wyvern Speed Module");
        helper.add(DEModules.draconicSpeed,                                     "Draconic Speed Module");
        helper.add(DEModules.chaoticSpeed,                                      "Chaotic Speed Module");

        helper.add(DEModules.draconiumDamage,                                   "Damage Module");
        helper.add(DEModules.wyvernDamage,                                      "Wyvern Damage Module");
        helper.add(DEModules.draconicDamage,                                    "Draconic Damage Module");
        helper.add(DEModules.chaoticDamage,                                     "Chaotic Damage Module");

        helper.add(DEModules.draconiumAOE,                                      "AOE Module");
        helper.add(DEModules.wyvernAOE,                                         "Wyvern AOE Module");
        helper.add(DEModules.draconicAOE,                                       "Draconic AOE Module");
        helper.add(DEModules.chaoticAOE,                                        "Chaotic AOE Module");

        helper.add(DEModules.wyvernJunkFilter,                                  "Selective Incineration Module");

        helper.add(DEModules.wyvernEnderCollection,                             "Ender Collection Module");
        helper.add(DEModules.draconicEnderCollection,                           "Filterable Ender Collection Module");

        helper.add(DEModules.wyvernTreeHarvest,                                 "Wyvern Tree Harvester");
        helper.add(DEModules.draconicTreeHarvest,                               "Draconic Forest Reduction Assistant");
        helper.add(DEModules.chaoticTreeHarvest,                                "Chaotic Deforestation Module");

        helper.add(DEModules.wyvernAutoFire,                                    "Auto Fire Module");

        helper.add(DEModules.draconicProjAntiImmune,                            "Projectile Immunity Cancellation Module");

        helper.add(DEModules.wyvernProjVelocity,                                "Wyvern Projectile Velocity Module");
        helper.add(DEModules.draconicProjVelocity,                              "Draconic Projectile Velocity Module");
        helper.add(DEModules.chaoticProjVelocity,                               "Chaotic Projectile Velocity Module");

        helper.add(DEModules.wyvernProjAccuracy,                                "Wyvern Projectile Accuracy Module");
        helper.add(DEModules.draconicProjAccuracy,                              "Draconic Projectile Accuracy Module");
        helper.add(DEModules.chaoticProjAccuracy,                               "Chaotic Projectile Accuracy Module");

        helper.add(DEModules.wyvernProjGravComp,                                "Wyvern Projectile Gravity Compensation Module");
        helper.add(DEModules.draconicProjGravComp,                              "Draconic Projectile Gravity Compensation Module");
        helper.add(DEModules.chaoticProjGravComp,                               "Chaotic Projectile Gravity Compensation Module");

        helper.add(DEModules.wyvernProjPenetration,                             "Wyvern Projectile Penetration Module");
        helper.add(DEModules.draconicProjPenetration,                           "Draconic Projectile Penetration Module");
        helper.add(DEModules.chaoticProjPenetration,                            "Chaotic Projectile Penetration Module");

        helper.add(DEModules.wyvernProjDamage,                                  "Wyvern Projectile Damage Module");
        helper.add(DEModules.draconicProjDamage,                                "Draconic Projectile Damage Module");
        helper.add(DEModules.chaoticProjDamage,                                 "Chaotic Projectile Damage Module");

        helper.add(DEModules.wyvernShieldControl,                               "Wyvern Shield Control Module");
        helper.add(DEModules.draconicShieldControl,                             "Draconic Shield Control Module");
        helper.add(DEModules.chaoticShieldControl,                              "Chaotic Shield Control Module");

        helper.add(DEModules.wyvernShieldCapacity,                              "Wyvern Shield Capacity Module");
        helper.add(DEModules.draconicShieldCapacity,                            "Draconic Shield Capacity Module");
        helper.add(DEModules.chaoticShieldCapacity,                             "Chaotic Shield Capacity Module");

        helper.add(DEModules.wyvernLargeShieldCapacity,                         "Wyvern Large Shield Capacity Module");
        helper.add(DEModules.draconicLargeShieldCapacity,                       "Draconic Large Shield Capacity Module");
        helper.add(DEModules.chaoticLargeShieldCapacity,                        "Chaotic Large Shield Capacity Module");

        helper.add(DEModules.wyvernShieldRecovery,                              "Wyvern Shield Recovery Module");
        helper.add(DEModules.draconicShieldRecovery,                            "Draconic Shield Recovery Module");
        helper.add(DEModules.chaoticShieldRecovery,                             "Chaotic Shield Recovery Module");

        helper.add(DEModules.wyvernFlight,                                      "Wyvern Flight Module");
        helper.add(DEModules.draconicFlight,                                    "Draconic Flight Module");
        helper.add(DEModules.chaoticFlight,                                     "Chaotic Flight Module");

        helper.add(DEModules.wyvernUndying,                                     "Wyvern Undying Module");
        helper.add(DEModules.draconicUndying,                                   "Draconic Undying Module");
        helper.add(DEModules.chaoticUndying,                                    "Chaotic Undying Module");

        helper.add(DEModules.draconiumAutoFeed,                                 "Auto Feed Module");
        helper.add(DEModules.wyvernAutoFeed,                                    "Wyvern Auto Feed Module");
        helper.add(DEModules.draconicAutoFeed,                                  "Draconic Auto Feed Module");

        helper.add(DEModules.wyvernNightVision,                                 "Night Vision Module");

        helper.add(DEModules.draconiumJump,                                     "Jump Module");
        helper.add(DEModules.wyvernJump,                                        "Wyvern Jump Module");
        helper.add(DEModules.draconicJump,                                      "Draconic Jump Module");
        helper.add(DEModules.chaoticJump,                                       "Chaotic Jump Module");

//        helper.add(DEModules.wyvernAquaAdapt,                                   "Aqua Adapt Module");

        helper.add(DEModules.wyvernHillStep,                                    "Hill Step Module");

        helper.add("module_type",                                               "Module Type");
        helper.add("grid_size",                                                 "Module Size");
        helper.add("max_installable",                                           "Max Installable");
    }

    private void guis(PrefixHelper helper) {
        //Item Config
        helper.setPrefix("gui." + MODID + ".item_config");
        helper.add("name",                                                      "Configure Equipment");
        helper.add("configure",                                                 "Configure");
        helper.add("toggle_hidden.info",                                        "Show / Hide Inventory");
        helper.add("toggle_advanced.info",                                      "Toggle advanced config mode");
        helper.add("delete_zone.info",                                          "Drop a property or group here to delete it.");
        helper.add("add_group",                                                 "Add Group");
        helper.add("add_group.info",                                            "Add a New Property Group");
        helper.add("click_and_drag_to_place",                                   "Click and Drag");
        helper.add("edit_preset.info",                                          "Edit Preset Properties");
        helper.add("expand_group.info",                                         "Expand Group");
        helper.add("collapse_group.info",                                       "Collapse Group");
        helper.add("move_group.info",                                           "Click and drag to move this group.");
        helper.add("delete_group.info",                                         "Delete");
        helper.add("copy_group.info",                                           "Copy Group");
        helper.add("toggle_preset.info",                                        "Toggle preset mode\nConverts this property group into a property preset.");
        helper.add("apply_preset",                                              "Apply Preset");
        helper.add("move_prop.info",                                            "Click and drag to move property.");
        helper.add("drop_create_group.info",                                    "Drop to Create Group");
        helper.add("add_to_group.info",                                         "Add to Group");
        helper.add("drop_to_delete.info",                                       "Drop to delete.");
        helper.add("drop_prop_here",                                            "Drop Property Here");
        helper.add("global.info",                                               "Toggle global mode\nWhen in global mode, a property will be applied to all equipment of the same type.\nPlease note that when in global mode, the displayed value may not match the item's actual value.");
        helper.add("provider_unavailable",                                      "Item not available.");
        helper.add("select_item_to_get_started",                                "Select a configurable item to get started.");
        helper.add("options",                                                   "Options");
        helper.add("hide_unavailable",                                          "Hide Unavailable");
        helper.add("hide_unavailable.info",                                     "Hide properties if the associated item is not available.");
        helper.add("show_unavailable",                                          "Show Unavailable");
        helper.add("show_unavailable.info",                                     "Show properties if the associated item is not available.");
        helper.add("disable_snapping",                                          "Disable Snapping");
        helper.add("disable_snapping.info",                                     "Toggle Property/Group Position Snapping");
        helper.add("enable_snapping",                                           "Enable Snapping");
        helper.add("disable_visualization",                                     "Disable Visualization");
        helper.add("disable_visualization.info",                                "Toggle the highlight/animation that occurs over a properties associated item when hovering over or editing a property.");
        helper.add("enable_visualization",                                      "Enable Visualization");
        helper.add("hide_group_button",                                         "Hide \"New Group\" Button");
        helper.add("show_group_button",                                         "Show \"New Group\" Button");
        helper.add("hide_delete_zone",                                          "Hide \"Delete Zone\"");
        helper.add("show_delete_zone",                                          "Show \"Delete Zone\"");
        helper.add("disable_adv_xover",                                         "Disable Advanced Crossover");
        helper.add("disable_adv_xover.info",                                    "Hide configured properties, property groups and presets when in simple configuration mode.");
        helper.add("enable_adv_xover",                                          "Enable Advanced Crossover");
        helper.add("enable_adv_xover.info",                                     "Show configured properties, property groups and presets when in simple configuration mode.");
        helper.add("not_bound",                                                 "Not Bound");
        helper.add("toggle_global_binding.info",                                "Global Binding\nThis setting will allow you to use this keybind when this GUI is closed.\nThis will work with any key even if it conflicts with another keybinding.");
        helper.add("set_key_bind.info",                                         "Click to set a keybind for this preset.");
        helper.add("open_modules.info",                                         "Open Module Configuration GUI");
        helper.add("no_configurable_items",                                     "You don't have any configurable items in your inventory!");

        helper.setPrefix("gui." + MODID + ".boolean_property");
        helper.add("true",                                                      "True");
        helper.add("false",                                                     "False");
        helper.add("enabled",                                                   "Enabled");
        helper.add("disabled",                                                  "Disabled");
        helper.add("active",                                                    "Active");
        helper.add("inactive",                                                  "Inactive");
        helper.add("yes",                                                       "Yes");
        helper.add("no",                                                        "No");

        //Module GUI
        helper.setPrefix("gui." + MODID + ".modular_item");
        helper.add("name",                                                      "Configure Modules");
        helper.add("modules",                                                   "Modules");
        helper.add("open_item_config.info",                                     "Open Item Configuration GUI");
        helper.add("no_module_hosts",                                           "You don't have any modular items in your inventory!");
        helper.add("module_grid",                                               "Module Grid");

        //SupportedModulesIcon
        add("gui." + MODID + ".modular_item.supported_modules",                 "Supported Modules");

        //Generator
        helper.setPrefix("gui." + MODID + ".generator");
        helper.add("fuel_efficiency",                                           "Fuel Efficiency:");
        helper.add("output_power",                                              "Output Power:");
        helper.add("current_fuel_value",                                        "Current Fuel Value:");
        helper.add("mode_eco_plus",                                             "Eco Plus");
        helper.add("mode_eco_plus.info",                                        "Eco Plus\nSignificantly increased fuel efficiency.\nSignificantly decreased output power.");
        helper.add("mode_eco",                                                  "Eco");
        helper.add("mode_eco.info",                                             "Eco Mode\nIncreased fuel efficiency.\nDecreased output power.");
        helper.add("mode_normal",                                               "Normal");
        helper.add("mode_normal.info",                                          "Normal Mode\nStandard output power and efficiency.\nSimilar to other basic generators.");
        helper.add("mode_performance",                                          "Performance");
        helper.add("mode_performance.info",                                     "Performance Mode\nProvides a worthwhile increase in output power\nfor a small fuel efficiency penalty.");
        helper.add("mode_performance_plus",                                     "Overdrive");
        helper.add("mode_performance_plus.info",                                "Overdrive Mode\nNeed all the power you can get?\nHave plenty of fuel to burn?\nThis is the mode for you!");

        //Grinder
        helper.setPrefix("gui." + MODID + ".grinder");
        helper.add("aoe",                                                       "AOE:");
        helper.add("aoe.info",                                                  "Changes the grinder's Area Of Effect.\n(The area in which it will kill mobs.)");
        helper.add("show_aoe",                                                  "Show AOE");
        helper.add("collect.items",                                             "Collect Items");
        helper.add("collect.items.info",                                        "If enabled, the Grinder will collect items within the kill area and insert them into an adjacent inventory.");
        helper.add("collect.xp",                                                "Collect XP");
        helper.add("collect.xp.info",                                           "If enabled, the Grinder will collect XP dropped within the kill area and store it internally.\nThis XP can then be claimed by a player or piped out if there is a mod installed that adds liquid XP.");
        helper.add("claim.xp",                                                  "Claim XP");
        helper.add("claim.xp.info",                                             "Claim all stored XP");
        helper.add("claim.xp.level.info",                                       "Claim 1 experience level");
        helper.add("claim.xp.levels.info",                                      "Claim %s experience levels");
        helper.add("stored_xp",                                                 "Stored Experience:");
        helper.add("stored_xp.raw",                                             "(Raw XP)");
        helper.add("weapon_slot",                                               "Optional weapon to use when attacking.");

        //Disenchanter
        helper.setPrefix("gui." + MODID + ".disenchanter");
        helper.add("level",                                                     "Level of Enchant: %s");
        helper.add("cost",                                                      "XP Cost in Levels: %s");

        //Celestial Manipulator
        helper.setPrefix("gui." + MODID + ".celestial_manipulator");
        helper.add("weather",                                                   "Weather");
        helper.add("time",                                                      "Time");
        helper.add("stopRain",                                                  "Clear Skies");
        helper.add("startRain",                                                 "Create Rain");
        helper.add("startStorm",                                                "Create Storm");
        helper.add("skipTo",                                                    "Skip To");
        helper.add("sunRise",                                                   "Sunrise");
        helper.add("midDay",                                                    "Noon");
        helper.add("sunSet",                                                    "Sunset");
        helper.add("moonRise",                                                  "Moonrise");
        helper.add("midnight",                                                  "Midnight");
        helper.add("moonSet",                                                   "Moonset");
        helper.add("skip24",                                                    "Skip 24 Hours");
        helper.add("stop",                                                      "Stop Skipping");
        helper.add("rs.0",                                                      "Clear Skies");
        helper.add("rs.1",                                                      "Create Rain");
        helper.add("rs.2",                                                      "Create Storm");
        helper.add("rs.3",                                                      "Sunrise");
        helper.add("rs.4",                                                      "Noon");
        helper.add("rs.5",                                                      "Sunset");
        helper.add("rs.6",                                                      "Moonrise");
        helper.add("rs.7",                                                      "Midnight");
        helper.add("rs.8",                                                      "Moonset");

        helper.setPrefix("msg." + MODID + ".celestial_manipulator");
        helper.add("alreadyRunning",                                            "Celestial Manipulator is currently running...  Please wait!");
        helper.add("notRaining",                                                "You can't stop rain that isn't there!");
        helper.add("insufficientPower",                                         "Not enough energy.");
        helper.add("alreadyRaining",                                            "It's already raining!");
        helper.add("alreadyStorming",                                           "It's already storming!");

        //Entity Detector
        helper.setPrefix("gui." + MODID + ".entity_detector");
        helper.add("range",                                                     "Range");
        helper.add("rsmin",                                                     "Min Str.");
        helper.add("rsmax",                                                     "Max Str.");
        helper.add("pulse_rate",                                                "Rate");
        helper.add("pulse_mode",                                                "Mode");
        helper.add("pulse_mode.on",                                             "Pulse");
        helper.add("pulse_mode.off",                                            "Constant");

        //Draconium Chest
        helper.setPrefix("gui." + MODID + ".draconium_chest");
        helper.add("feed." + FeedMode.OFF.localKey() + ".info",                 "Auto-Smelt: Off");
        helper.add("feed." + FeedMode.ALL.localKey() + ".info",                 "Auto-Smelt: All\nFeed all smeltable items into the furnace.");
        helper.add("feed." + FeedMode.FILTER.localKey() + ".info",              "Auto-Smelt: Filtered\nFeed any items that are already being smelted into the furnace.");
        helper.add("feed." + FeedMode.STiCKY.localKey() + ".info",              "Auto-Smelt: Filtered (Sticky)\nSame as Filtered, but will keep one item so the filter persists.");
        helper.add("color_picker",                                              "Change Chest Color");

        //Reactor
        helper.setPrefix("gui." + MODID + ".reactor");
        helper.add("title",                                                     "Draconic Reactor");
        helper.add("core_volume",                                               "Core Volume");
        helper.add("core_volume.info",                                          "This shows the total volume of matter within the reactor in cubic meters (Draconium + Chaos). This value will only change when you add or remove fuel.");
        helper.add("gen_rate",                                                  "Generation Rate");
        helper.add("gen_rate.info",                                             "This is the current OP/t being generated by the reactor.");
        helper.add("field_rate",                                                "Field Input Rate");
        helper.add("field_rate.info",                                           "This is the exact OP/t of input required to maintain the current field strength. As field strength increases, this will increase exponentially.");
        helper.add("convert_rate",                                              "Fuel Conversion Rate");
        helper.add("convert_rate.info",                                         "This is how fast the reactor is currently using fuel. As the reactor saturation increases, this will go down.");
        helper.add("go_boom_now",                                               "Emergency shield reserve is now active, but it won't last long! There is no way to stop the overload; the stabilizers are fried. I suggest you run!");
        helper.add("fuel_in",                                                   "Fuel (In)");
        helper.add("chaos_out",                                                 "Chaos (Out)");
        helper.add("status",                                                    "Status");

        helper.add("charge",                                                    "Charge");
        helper.add("activate",                                                  "Activate");
        helper.add("shutdown",                                                  "Shutdown");
        helper.add("rs_mode",                                                   "Redstone\nMode");
        helper.add("rs_mode.info",                                              "Configure the comparator output for this reactor component.");
        helper.add("sas",                                                       "SAS");
        helper.add("sas.info",                                                  "Semi-Automated Shutdown. When enabled, the reactor will automatically initiate shutdown when the temperature drops below 2500C and saturation reaches 99%. This can be used to automatically shutdown your reactor in the event of a malfunction or just when it needs to be refueled.");

        helper.add("rs_mode_temp",                                              "Temp");
        helper.add("rs_mode_temp.info",                                         "Output a signal from 0 to 15 as temperature rises up to 10000C.");
        helper.add("rs_mode_temp_inv",                                          "-Temp");
        helper.add("rs_mode_temp_inv.info",                                     "Same as Temp, but the signal is inverted.");
        helper.add("rs_mode_field",                                             "Shield");
        helper.add("rs_mode_field.info",                                        "Output a signal from 0 to 15 as the shield strength fluctuates between 0 and 100%; Signal of 1 = > 10% shield and Signal of 15 = >= 90% shield power.");
        helper.add("rs_mode_field_inv",                                         "-Shield");
        helper.add("rs_mode_field_inv.info",                                    "Same as Shield, but the signal is inverted.");
        helper.add("rs_mode_sat",                                               "Saturation");
        helper.add("rs_mode_sat.info",                                          "Output a signal from 0 to 15 as the saturation level fluctuates between 0 and 100%.");
        helper.add("rs_mode_sat_inv",                                           "-Saturation");
        helper.add("rs_mode_sat_inv.info",                                      "Same as Saturation, but the signal is inverted.");
        helper.add("rs_mode_fuel",                                              "Conversion");
        helper.add("rs_mode_fuel.info",                                         "Output a signal from 0 to 15 as the fuel conversion level increases from 0 to 100%, Signal of 15 = >= 90% Conversion.");
        helper.add("rs_mode_fuel_inv",                                          "-Conversion");
        helper.add("rs_mode_fuel_inv.info",                                     "Same as Conversion, but the signal is inverted.");

        helper.add("reaction_temp",                                             "Core Temperature");
        helper.add("field_strength",                                            "Containment Field Strength");
        helper.add("energy_saturation",                                         "Energy Saturation");
        helper.add("fuel_conversion",                                           "Fuel Conversion Level");

        helper.setPrefix("gui.reactor.status");
        helper.add("invalid.info",                                              "Invalid Setup");
        helper.add("cold.info",                                                 "Offline");
        helper.add("warming_up.info",                                           "Warming Up");
        helper.add("running.info",                                              "Online");
        helper.add("stopping.info",                                             "Stopping");
        helper.add("cooling.info",                                              "Cooling Down");
        helper.add("beyond_hope.info",                                          "Explosion Imminent!!!");

        //Flow Gate
        helper.setPrefix("gui." + MODID + ".flow_gate");
        helper.add("overridden",                                                "Overridden");
        helper.add("overridden.info",                                           "Default controls have been disabled by a computer.");
        helper.add("redstone_high",                                             "Redstone Signal High");
        helper.add("redstone_high.info",                                        "The flow that will be allowed through when receiving a redstone signal of 15.");
        helper.add("apply",                                                     "Apply");
        helper.add("redstone_low",                                              "Redstone Signal Low");
        helper.add("redstone_low.info",                                         "The flow that will be allowed through when receiving a redstone signal of 0.");
        helper.add("flow",                                                      "Flow");
        helper.add("flow.info",                                                 "The actual flow will vary between the two given values depending on the strength of the redstone signal being supplied to the block.");

        //Energy Transfuser
        helper.setPrefix("gui." + MODID + ".transfuser");
        helper.add("mode_charge",                                               "Charge\n - Accepts power from external sources\n - Accepts power from buffer slots\n - Can be extracted when fully charged");
        helper.add("mode_discharge",                                            "Discharge\n - Discharges to external consumers\n - Discharges to buffer slots\n - Can be extracted when fully discharged");
        helper.add("mode_buffer",                                               "Buffer\n - Accepts power from external sources\n - Accepts power from discharge slots\n - Discharges to external consumers\n - Discharges to charge slots");
        helper.add("mode_disabled",                                             "Disabled\n - Slot is disabled");
        helper.add("sequential_charge",                                         "Sequential Input Priority\nThe left most item will receive charging priority.\nOnce that item is full the next will receive priority.");
        helper.add("balanced_charge",                                           "Balanced Input Priority\nPower input will be balanced between all 4 slots,\nbut each slot will be limited to 1/4 of the total input rate.");

        //Dislocator
        helper.setPrefix("gui." + MODID + ".dislocator");
        helper.add("add",                                                       "Add New");
        helper.add("add.info",                                                  "Add current position\n- insert below selected");
        helper.add("add_top.info",                                              "Add current position\n- add to top of list");
        helper.add("add_bottom.info",                                           "Add current position\n- add to bottom of list");
        helper.add("fuel",                                                      "Fuel:");
        helper.add("fuel.info",                                                 "The total number of teleports remaining.");
        helper.add("update",                                                    "Set Here");
        helper.add("update.info",                                               "Update the selected location to your current coordinates.");
        helper.add("fuel_add_1.info",                                           "Add 1 Ender Pearl from your inventory.\nEnder pearls are used as teleport fuel.");
        helper.add("fuel_add_16.info",                                          "Add 16 Ender Pearls from your inventory.\nEnder pearls are used as teleport fuel.");
        helper.add("fuel_add_all.info",                                         "Add all Ender Pearls from your inventory.\nEnder pearls are used as teleport fuel.");
        helper.add("right_click_tp",                                            "Right click to teleport.");
        helper.add("double_click_name",                                         "Double left click to rename.");
        helper.add("must_unlock",                                               "(must be unlocked)");
        helper.add("drag_to_move",                                              "Left click and drag to move.");
        helper.add("delete.info",                                               "Delete");
        helper.add("edit_lock.info",                                            "Toggle edit lock");
        helper.add("right_click_mode.info",                                     "Switch between \"Teleport to Selected\" and \"Blink\" on right click.\nYou can also configure keybindings.");
        helper.add("mode_blink",                                                "Use Mode:\nBlink");
        helper.add("mode_tp",                                                   "Use Mode:\nTeleport");
        helper.add("add_1",                                                     "+1");
        helper.add("add_16",                                                    "+16");
        helper.add("add_all",                                                   "All");

        //Fusion Crafting Core
        helper.setPrefix("gui." + MODID + ".fusion_craft");
        helper.add("craft",                                                     "Craft");

        //JEI
        helper.add("tier.draconium",                                            "Tier: Draconium");
        helper.add("tier.wyvern",                                               "Tier: Wyvern");
        helper.add("tier.draconic",                                             "Tier: Draconic");
        helper.add("tier.chaotic",                                              "Tier: Chaotic");
        helper.add("energy_cost",                                               "Energy Cost");
        helper.add("ne_tier_injectors",                                         "Not enough %s tier injectors.");

        //Energy Core
        helper.setPrefix("gui." + MODID + ".energy_core");
        helper.add("title",                                                     "Tier %s Energy Core");
        helper.add("activate",                                                  "Activate");
        helper.add("deactivate",                                                "Deactivate");
        helper.add("tier_down",                                                 "Tier Down");
        helper.add("tier_up",                                                   "Tier Up");
        helper.add("build_guide",                                               "Toggle Build Guide");
        helper.add("assemble",                                                  "Assemble Core");
        helper.add("energy_target",                                             "Energy Target");
        helper.add("energy_target_info",                                        "Used to define an 'energy target'.\nThis is for display purposes only.\nYou can use basic numbers or\nscientific notation in the following format,\n'9.223E18' which would translate to\n9223000000000000000");
        helper.add("legacy_false",                                              "Enable legacy renderer");
        helper.add("legacy_true",                                               "Disable legacy renderer");
        helper.add("custom_colour_false",                                       "Enable colour customisation");
        helper.add("custom_colour_true",                                        "Disable colour customisation");
        helper.add("config_colour",                                             "Configure Colour");
        helper.add("reset",                                                     "Reset");
        helper.add("core_invalid",                                              "Core structure invalid.");
        helper.add("stabilizers_invalid",                                       "Stabilizer configuration invalid." );
        helper.add("stabilizers_advanced",                                      "(Advanced stabilizers required)");

        add("msg." + MODID + ".energy_core.already_building", "Assembly already in progress!");
        add("msg." + MODID + ".energy_core.core_not_found",   "404 Core Not Found!!!");

        add("generic.configureRedstone",                                        "Configure Redstone");
    }

    private void hudAndMessages(PrefixHelper helper) {
        //Energy Network
        helper.setPrefix("gui." + MODID + ".energy_net");
        helper.add("pos_saved_to_tool",                                         "Block position saved to tool. (Sneak + Right click air to clear)");
        helper.add("pos_cleared",                                               "Position Cleared");
        helper.add("tool_not_bound",                                            "Tool not bound! Sneak + Right click to bind.");
        helper.add("bound_to_invalid",                                          "The tool is bound to an invalid block!");
        helper.add("link_broken",                                               "Link broken.");
        helper.add("devices_linked",                                            "Devices linked.");
        helper.add("link_limit_reached_this",                                   "This device has reached its connection limit!");
        helper.add("link_limit_reached_target",                                 "The target device has reached its connection limit!");
        helper.add("this_range_limit",                                          "The target device is too far away!");
        helper.add("target_range_limit",                                        "This device is too far from the target device!");
        helper.add("device_invalid",                                            "That is not a valid device!");
        helper.add("link_failed_unknown",                                       "Link Failed! [Reason Unknown...]");
        helper.add("link_to_self",                                              "You can not link a device to itself...");
        helper.add("hud_charge",                                                "Charge");
        helper.add("hud_links",                                                 "Links");
        helper.add("hud_wireless_links",                                        "Wireless Links");
        helper.add("io_output_true",                                            "I/O Mode:%s Output to Block");
        helper.add("io_output_false",                                           "I/O Mode:%s Input from Block");
        helper.add("side_can_not_receive",                                      "That block can not receive energy on that side!");
        helper.add("side_can_not_extract",                                      "That block can not supply energy on that side!");
        helper.add("max_receivers",                                             "Receiver Limit Reached!");
        helper.add("output",                                                    "Output");
        helper.add("input",                                                     "Input");
        helper.add("identify",                                                  "Identify");
        helper.add("unlink",                                                    "Unlink");
        helper.add("clear_links",                                               "Clear Links");
        helper.add("clear_receivers",                                           "Clear Receivers");

        //Modular Item
        helper.setPrefix("modular_item." + MODID);
        helper.add("requires_energy",                                           "This item requires energy modules to function.");
        helper.add("requires_energy_press",                                     "Press %s to open module config.");
        helper.add("error.no_modular_items",                                    "You don't have any modular items in your inventory!");
        helper.add("error.module_install_limit",                                "Install limit reached for this module.");
        helper.add("error.only_one_use_override_module",                        "There is already a module that overrides right click functionality installed. Only one such module can be installed at a time!");
        helper.add("error.not_compatible_with",                                 "Not compatible with");

        helper.add("cant_install.level_high",                                   "The module tier is too high for this modular item.");
        helper.add("cant_install.not_supported",                                "This module is not supported by this modular item.");
        helper.add("cant_install.wont_fit",                                     "The module won't fit in that position.");

        //Dislocator
        helper.setPrefix("dislocate." + MODID);
        helper.add("not_set",                                                   "You must first set the destination before you can teleport (Sneak + Right click).");
        helper.add("player_need_advanced",                                      "You need a more powerful dislocator to do that.");
        helper.add("entity_sent_to",                                            "The entity has been sent to");
        helper.add("already_bound",                                             "ERROR: This charm can only be bound to one location.");
        helper.add("un_set_info1",                                              "Unbound");
        helper.add("un_set_info2",                                              "Sneak + Right click to bind your current");
        helper.add("un_set_info3",                                              "X, Y, and Z coordinates,");
        helper.add("un_set_info4",                                              "as well as the direction you are facing");
        helper.add("un_set_info5",                                              "and the dimension you are currently in.");
        helper.add("bound_to",                                                  "Bound to");
        helper.add("uses_remain",                                               "%s Uses Remaining");

        helper.add("no_fuel",                                                   "Out of Fuel!");
        helper.add("player_allow",                                              "The player must give their consent by sneaking.");
        helper.add("fuel",                                                      "Fuel:");
        helper.add("teleport_fuel",                                             "Teleport Fuel:");
        helper.add("to_open_gui",                                               "Sneak + Right click to open GUI.");

        helper.add("bound.click_to_link",                                       "Right click to create bound pair.");
        helper.add("bound.click_to_link_self",                                  "Right click to bind to yourself.");
        helper.add("bound.player_link",                                         "Bound to player");
        helper.add("bound.key",                                                 "Unique Key");
        helper.add("bound.bound_to",                                            "Bound To");
        helper.add("bound.in_player_inventory",                                 "The bound dislocator is in a players inventory. Will teleport to the player.");
        helper.add("bound.unknown_link",                                        "Unknown Location");
        helper.add("bound.cant_find_player",                                    "The bound player is not online!");
        helper.add("bound.cant_find_target",                                    "Could not find the bound dislocator!");
        helper.add("bound.link_id",                                             "Link ID");


        //Item Dislocator
        helper.setPrefix("item_dislocate." + MODID);
        helper.add("activate",                                                  "Item Dislocator Activated");
        helper.add("deactivate",                                                "Item Dislocator Deactivated");

        //Fusion Crafting
        helper.setPrefix("fusion_inj." + MODID);
        helper.add("single_item",                                               "Single item mode");
        helper.add("multi_item",                                                "Stack mode");

        helper.setPrefix("fusion_status." + MODID);
        helper.add("charging",                                                  "Charging: %s%%");
        helper.add("crafting",                                                  "Crafting: %s%%");
        helper.add("output_obstructed",                                         "Output Obstructed");
        helper.add("tier_low",                                                  "Injector tier too low");
        helper.add("no_recipe",                                                 "No Valid Recipe");
        helper.add("canceled",                                                  "Craft Canceled");
        helper.add("ready",                                                     "Ready to craft");
        helper.add("injector_close",                                            "One or more injectors are too close!");

        //Armor Hud
        add("hud." + MODID + ".shield_hud.name",              "Shield HUD");
        add("hud." + MODID + ".shield_hud.info",              "This HUD displays the current status of your Draconic Shield as well as your energy reserves and Undying modules.");
        helper.setPrefix("hud_armor." + MODID);
        helper.add("no_shield",                                                 "No Shield Installed");
        helper.add("shield_disabled",                                           "Shield Disabled");
        helper.add("numeric.true",                                              "Numeric Energy: Enabled");
        helper.add("numeric.false",                                             "Numeric Energy: Disabled");
        helper.add("numeric.info",                                              "Enable / Disable numeric energy value display.");
        helper.add("undying.true",                                              "Undying: Enabled");
        helper.add("undying.false",                                             "Undying: Disabled");
        helper.add("undying.info",                                              "Enable / Disable undying module display.");
        helper.add("energy.0",                                                  "Energy Display: Armor");
        helper.add("energy.1",                                                  "Energy Display: Capacitors");
        helper.add("energy.2",                                                  "Energy Display: Combined");
        helper.add("energy.info",                                               "Cycle between displaying energy stored in Armor, Capacitors, or both combined.");
        helper.add("scale",                                                     "Scale");
        helper.add("scale.info",                                                "Change HUD element scale.");

        add("hud." + MODID + ".open_hud_config",                                "Open HUD configuration GUI.");

        add("disenchanter." + MODID + ".not_enough_levels",                     "You require %s levels to extract that enchantment!");

    }

    private void toolTips(PrefixHelper helper) {
        helper.setPrefix("tooltip." + MODID);
        //Bows
        helper.add("bow.damage",                                                "%s Max Attack Damage");
        helper.add("bow.energy_per_shot",                                       "%s OP/shot");
    }

    private void misc(PrefixHelper helper) {
        add("itemGroup." + MODID + ".blocks",                 "Draconic Evolution Blocks");
        add("itemGroup." + MODID + ".items",                  "Draconic Evolution Items");
        add("itemGroup." + MODID + ".modules",                "Draconic Evolution Modules");
        add("tech_level." + MODID + ".draconium",             "Draconium");
        add("tech_level." + MODID + ".wyvern",                "Wyvern");
        add("tech_level." + MODID + ".draconic",              "Draconic");
        add("tech_level." + MODID + ".chaotic",               "Chaotic");

        //Entities
        add("entity." + MODID + ".draconic_guardian",         "Chaos Guardian");
        add("entity." + MODID + ".guardian_wither",           "Guardian Wither");
        add("entity." + MODID + ".guardian_crystal",          "Guardian Crystal");
        add("entity." + MODID + ".guardian_projectile",       "Guardian Projectile");
        add("entity." + MODID + ".persistent_item",           "Persistent Item");
        add("entity." + MODID + ".draconic_arrow",            "Draconic Arrow");

        //Death messages
        add("death.attack." + MODID + ".draconic_guardian",   "%1$s was torn apart by %2$s");
        add("death.attack." + MODID + ".guardian_projectile", "%1$s was obliterated by %2$s");
        add("death.attack." + MODID + ".guardian_laser",      "%1$s was vaporized by %2$s using a frickin laser beam");
        add("death.attack.administrative.kill",                                 "%1$s deserved to die by the powers that be");

        add("key." + MODID + ".place_item",                   "Place Item");
        add("key." + MODID + ".tool_config",                  "Tool Config");
        add("key." + MODID + ".toggle_flight",                "Toggle Flight");
        add("key." + MODID + ".tool_modules",                 "Tool Modules");
        add("key." + MODID + ".toggle_magnet",                "Toggle Item Dislocator");
        add("key." + MODID + ".dislocator_teleport",          "Advanced Dislocator Teleport");
        add("key." + MODID + ".dislocator_blink",             "Advanced Dislocator Blink");
        add("key." + MODID + ".dislocator_gui",               "Advanced Dislocator GUI");
        add("key." + MODID + ".dislocator_up",                "Advanced Dislocator Select Up");
        add("key." + MODID + ".dislocator_down",              "Advanced Dislocator Select Down");

        add("enchantment." + MODID + ".reaper_enchantment",   "Reaper");


        add("numprefix." + MODID + ".10-3",                   " Kilo ");
        add("numprefix." + MODID + ".10-6",                   " Mega ");
        add("numprefix." + MODID + ".10-9",                   " Giga ");
        add("numprefix." + MODID + ".10-12",                  " Tera ");
        add("numprefix." + MODID + ".10-15",                  " Peta ");
        add("numprefix." + MODID + ".10-18",                  " Exa ");
        add("numprefix." + MODID + ".10-21",                  " Zetta ");
        add("numprefix." + MODID + ".10-24",                  " Yotta ");
        add("numprefix." + MODID + ".10-27",                  " Octillion ");
        add("numprefix." + MODID + ".10-30",                  " Nonillion ");
        add("numprefix." + MODID + ".10-33",                  " Decillion ");
        add("numprefix." + MODID + ".10-36",                  " Undecillion ");
        add("numprefix." + MODID + ".10-39",                  " Duodecillion ");
        add("numprefix." + MODID + ".10-42",                  " Tredecillion ");
        add("numprefix." + MODID + ".10-45",                  " Quattuordecillion ");
        add("numprefix." + MODID + ".10-48",                  " Quindecillion ");
        add("numprefix." + MODID + ".10-51",                  " Sexdecillion ");
        add("numprefix." + MODID + ".10-54",                  " Septendecillion ");
        add("numprefix." + MODID + ".10-57",                  " Octodecillion ");
        add("numprefix." + MODID + ".10-60",                  " Novemdecillion ");
        add("numprefix." + MODID + ".10-63",                  " Vigintillion ");
    }

    @Override
    protected void addTranslations() {
        PrefixHelper helper = new PrefixHelper(this);
        blocks(helper);
        items(helper);
        itemProps(helper);
        guis(helper);
        modules(helper);
        toolTips(helper);
        misc(helper);
        hudAndMessages(helper);
    }

    //region Helpers

    @Override
    public void add(Block key, String name) {
        if (key != null) super.add(key, name);
    }

    @Override
    public void add(Item key, String name) {
        if (key != null) super.add(key, name);
    }

    public void add(ModuleType<?> key, String name) {
        super.add("module_type." + MODID + "." + key.getName() + ".name", name);
    }

    public void add(Module<?> key, String name) {
        super.add(key.getItem(), name);
    }

    public static class PrefixHelper {
        private final LangGenerator generator;
        private String prefix;

        public PrefixHelper(LangGenerator generator) {
            this.generator = generator;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix + ".";
        }

        public void add(String translationKey, String translation) {
            generator.add(prefix + translationKey, translation);
        }

        public void add(Block key, String name) {
            if (key != null) generator.add(key, name);
        }

        public void add(Item key, String name) {
            if (key != null) generator.add(key, name);
        }

        public void add(ModuleType<?> key, String name) {
            generator.add("module_type." + MODID + "." + key.getName() + ".name", name);
        }

        public void add(Module<?> key, String name) {
            generator.add(key.getItem(), name);
        }
    }
    //endregion
}
