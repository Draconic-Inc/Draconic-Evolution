package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DEModules;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistryEntry;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class LangGenerator extends LanguageProvider {
    public LangGenerator(DataGenerator gen) {
        super(gen, MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        //@formatter:off
        //region # Blocks
        add(DEContent.block_draconium                   ,"Draconium Block");
        add(DEContent.block_draconium_awakened          ,"Awakened Draconium Block");
        add(DEContent.chaos_crystal                     ,"Chaos Crystal");
        add(DEContent.crafting_injector_basic           ,"Basic Fusion Crafting Injector");
        add(DEContent.crafting_injector_wyvern          ,"Wyvern Fusion Crafting Injector");
        add(DEContent.crafting_injector_awakened        ,"Draconic Fusion Crafting Injector");
        add(DEContent.crafting_injector_chaotic         ,"Chaotic Fusion Crafting Injector");
        add(DEContent.crafting_core                     ,"Fusion Crafting Core");
        add(DEContent.crystal_relay_basic               ,"Basic Energy Relay Crystal");
        add(DEContent.crystal_relay_wyvern              ,"Wyvern Energy Relay Crystal");
        add(DEContent.crystal_relay_draconic            ,"Draconic Energy Relay Crystal");
//        add(DEContent.crystal_relay_chaotic           ,"Chaotic Energy Relay Crystal");
        add(DEContent.crystal_io_basic                  ,"Basic Energy I/O Crystal");
        add(DEContent.crystal_io_wyvern                 ,"Wyvern Energy I/O Crystal");
        add(DEContent.crystal_io_draconic               ,"Draconic Energy I/O Crystal");
//        add(DEContent.crystal_io_chaotic              ,"Chaotic Energy I/O Crystal");
        add(DEContent.crystal_wireless_basic            ,"Basic Wireless Energy Crystal");
        add(DEContent.crystal_wireless_wyvern           ,"Wyvern Wireless Energy Crystal");
        add(DEContent.crystal_wireless_draconic         ,"Draconic Wireless Energy Crystal");
//        add(DEContent.crystal_wireless_chaotic        ,"Chaotic Wireless Energy Crystal");
        add(DEContent.creative_op_capacitor             ,"Creative Power Source");
        add(DEContent.celestial_manipulator             ,"Celestial Manipulator");
        add(DEContent.disenchanter                      ,"Disenchanter");
        add(DEContent.dislocation_inhibitor             ,"Dislocation Normalization Field Projector");
        add(DEContent.dislocator_pedestal               ,"Dislocator Pedestal");
        add(DEContent.dislocator_receptacle             ,"Dislocator Receptacle");
        add(DEContent.draconium_chest                   ,"Draconium Chest");
        add(DEContent.energy_infuser                    ,"Energy Infuser");
        add(DEContent.entity_detector                   ,"Entity Detector");
        add(DEContent.entity_detector_advanced          ,"Advanced Entity Detector");
        add(DEContent.energy_core                       ,"Energy Core");
        add(DEContent.energy_core_stabilizer            ,"Energy Core Stabilizer");
        add(DEContent.energy_pylon                      ,"Energy Pylon");
        add(DEContent.energy_core_structure             ,"");
//        add(DEContent.flux_gate                         ,"Flux Gate");
//        add(DEContent.fluid_gate                        ,"Fluid Gate");
        add(DEContent.generator                         ,"Generator");
        add(DEContent.grinder                           ,"Mob Grinder");
        add(DEContent.infused_obsidian                  ,"Draconium Infused Obsidian");
        add(DEContent.ore_draconium_overworld           ,"Draconium Ore");
        add(DEContent.ore_draconium_nether              ,"Nether Draconium Ore");
        add(DEContent.ore_draconium_end                 ,"Ender Draconium Ore");
        add(DEContent.potentiometer                     ,"Potentiometer");
        add(DEContent.particle_generator                ,"Particle Generator");
        add(DEContent.placed_item                       ,"");
        add(DEContent.portal                            ,"Portal");
        add(DEContent.reactor_core                      ,"Draconic Reactor Core");
        add(DEContent.reactor_stabilizer                ,"Reactor Stabilizer");
        add(DEContent.reactor_injector                  ,"Reactor Energy Injector");
        add(DEContent.rain_sensor                       ,"Rain Sensor");
        add(DEContent.stabilized_spawner                ,"Stabilized Mob Spawner");
        //endregion

        //region # Items
        //Components
        add(DEContent.dust_draconium                    ,"Draconium Dust");
        add(DEContent.dust_draconium_awakened           ,"Awakened Draconium Dust");
        add(DEContent.ingot_draconium                   ,"Draconium Ingot");
        add(DEContent.ingot_draconium_awakened          ,"Awakened Draconium Ingot");
        add(DEContent.nugget_draconium                  ,"Draconium Nugget");
        add(DEContent.nugget_draconium_awakened         ,"Awakened Draconium Nugget");
        add(DEContent.core_draconium                    ,"Draconium Core");
        add(DEContent.core_wyvern                       ,"Wyvern Core");
        add(DEContent.core_awakened                     ,"Draconic Core");
        add(DEContent.core_chaotic                      ,"Chaotic Core");
        add(DEContent.energy_core_wyvern                ,"Wyvern Energy Core");
        add(DEContent.energy_core_draconic              ,"Draconic Energy Core");
        add(DEContent.dragon_heart                      ,"Dragon Heart");
        add(DEContent.chaos_shard                       ,"Chaos Shard");
        add(DEContent.chaos_frag_large                  ,"Large Chaos Fragment");
        add(DEContent.chaos_frag_medium                 ,"Small Chaos Fragment");
        add(DEContent.chaos_frag_small                  ,"Tiny Chaos Fragment");
        //Misc Tools
        add(DEContent.magnet                            ,"Item Dislocator");
        add(DEContent.magnet_advanced                   ,"Awakened Item Dislocator");
        add(DEContent.dislocator                        ,"Dislocator");
        add(DEContent.dislocator_advanced               ,"Advanced Dislocator");
        add(DEContent.dislocator_p2p                    ,"Bound Dislocator (Player)");
        add(DEContent.dislocator_player                 ,"Bound Dislocator (Point to Point)");
        add(DEContent.crystal_binder                    ,"Crystal Binder");
        add(DEContent.info_tablet                       ,"Information Tablet");
        add(DEContent.ender_energy_manipulator          ,"Ender energy manipulator");
        add(DEContent.creative_exchanger                ,"Creative Exchanger");
        add(DEContent.mob_soul                          ,"Mob soul");
        //Tools
        add(DEContent.capacitor_wyvern                  ,"Wyvern Capacitor");
        add(DEContent.capacitor_draconic                ,"Draconic Capacitor");
        add(DEContent.capacitor_chaotic                 ,"Chaotic Capacitor");
        add(DEContent.capacitor_creative                ,"Creative Capacitor");
        add(DEContent.shovel_wyvern                     ,"Wyvern Shovel");
        add(DEContent.shovel_draconic                   ,"Draconic Shovel");
        add(DEContent.shovel_chaotic                    ,"Cheotic Shovel");
        add(DEContent.hoe_wyvern                        ,"Wyvern Hoe");
        add(DEContent.hoe_draconic                      ,"Draconic Hoe");
        add(DEContent.hoe_chaotic                       ,"Chaotic Hoe");
        add(DEContent.pickaxe_wyvern                    ,"Wyvern Pickaxe");
        add(DEContent.pickaxe_draconic                  ,"Draconic Pickaxe");
        add(DEContent.pickaxe_chaotic                   ,"Chaotic Pickaxe");
        add(DEContent.axe_wyvern                        ,"Wyvern Axe");
        add(DEContent.axe_draconic                      ,"Draconic Axe");
        add(DEContent.axe_chaotic                       ,"Cheotic Axe");
        add(DEContent.bow_wyvern                        ,"Wyvern Bow");
        add(DEContent.bow_draconic                      ,"Draconic Bow");
        add(DEContent.bow_chaotic                       ,"Chaotic Bow");
        add(DEContent.sword_wyvern                      ,"Wyvern Sword");
        add(DEContent.sword_draconic                    ,"Draconic Sword");
        add(DEContent.sword_chaotic                     ,"Chaotic Sword");
        add(DEContent.staff_draconic                    ,"Draconic Staff of Power");
        add(DEContent.staff_chaotic                     ,"Chaotic Staff of Power");
        //Armor
        add(DEContent.chestpiece_wyvern                      ,"Wyvern Chest Piece");
        add(DEContent.chestpiece_draconic                    ,"Draconic Chest Piece");
        add(DEContent.chestpiece_chaotic                     ,"Chaotic Chest Piece");
        //endregion

        //region # Gui's and related translations
        //Item Config
        add("gui.draconicevolution.item_config.name"                                        ,"Configure Equipment");
        add("gui.draconicevolution.item_config.configure"                                   ,"Configure");
        add("gui.draconicevolution.item_config.toggle_hidden.info"                          ,"Show / Hide Inventory");
        add("gui.draconicevolution.item_config.toggle_advanced.info"                        ,"Toggle advanced config mode");
        add("gui.draconicevolution.item_config.delete_zone.info"                            ,"Drop a property or group here to delete it");
        add("gui.draconicevolution.item_config.add_group"                                   ,"Add Group");
        add("gui.draconicevolution.item_config.add_group.info"                              ,"Add a new property group");
        add("gui.draconicevolution.item_config.click_and_drag_to_place"                     ,"Click and drag");
        add("gui.draconicevolution.item_config.edit_preset.info"                            ,"Edit preset properties");
        add("gui.draconicevolution.item_config.expand_group.info"                           ,"Expand Group");
        add("gui.draconicevolution.item_config.collapse_group.info"                         ,"Collapse Group");
        add("gui.draconicevolution.item_config.move_group.info"                             ,"Click and drag to move this group");
        add("gui.draconicevolution.item_config.copy_group.info"                             ,"Copy Group");
        add("gui.draconicevolution.item_config.toggle_preset.info"                          ,"Toggle preset mode\nConverts this property group into a property preset");
        add("gui.draconicevolution.item_config.apply_preset"                                ,"Apply Preset");
        add("gui.draconicevolution.item_config.move_prop.info"                              ,"Click and drag to move property");
        add("gui.draconicevolution.item_config.move_prop_in_group.info"                     ,"Click and drag to move property");
        add("gui.draconicevolution.item_config.drop_create_group.info"                      ,"Drop to create group");
        add("gui.draconicevolution.item_config.add_to_group.info"                           ,"Add to group");
        add("gui.draconicevolution.item_config.drop_to_delete.info"                         ,"Drop to delete");
        add("gui.draconicevolution.item_config.drop_prop_here"                              ,"Drop Property Here");
        add("gui.draconicevolution.item_config.global.info"                                 ,"Toggle global mode\nWhen in global mode a property will be apply to all equipment of the same type\nAlso note when in global mode the displayed value may not match the items actual value");
        add("gui.draconicevolution.item_config.provider_unavailable"                        ,"Item not available");
        add("gui.draconicevolution.item_config.select_item_to_get_started"                  ,"Select a configurable item to get started");
        add("gui.draconicevolution.item_config.options"                                     ,"Options");
        add("gui.draconicevolution.item_config.hide_unavailable"                            ,"Hide unavailable");
        add("gui.draconicevolution.item_config.hide_unavailable.info"                       ,"Hide properties if the associated item is not available");
        add("gui.draconicevolution.item_config.show_unavailable"                            ,"Show unavailable");
        add("gui.draconicevolution.item_config.show_unavailable.info"                       ,"Show properties if the associated item is not available");
        add("gui.draconicevolution.item_config.disable_snapping"                            ,"Disable snapping");
        add("gui.draconicevolution.item_config.disable_snapping.info"                       ,"Toggle property / group position snapping");
        add("gui.draconicevolution.item_config.enable_snapping"                             ,"Enable snapping");
        add("gui.draconicevolution.item_config.disable_visualization"                       ,"Disable visualization");
        add("gui.draconicevolution.item_config.disable_visualization.info"                  ,"Toggle the highlight / animation that occurs over a properties associated item when hovering over or editing a property");
        add("gui.draconicevolution.item_config.enable_visualization"                        ,"Enable visualization");
        add("gui.draconicevolution.item_config.hide_group_button"                           ,"Hide \"New Group\" button");
        add("gui.draconicevolution.item_config.show_group_button"                           ,"Show \"New Group\" button");
        add("gui.draconicevolution.item_config.hide_delete_zone"                            ,"Hide \"Delete Zone\"");
        add("gui.draconicevolution.item_config.show_delete_zone"                            ,"Show \"Delete Zone\"");
        add("gui.draconicevolution.item_config.disable_adv_xover"                           ,"Disable advanced crossover");
        add("gui.draconicevolution.item_config.disable_adv_xover.info"                      ,"Hide configured properties, property groups and presets when in the simple configuration mode");
        add("gui.draconicevolution.item_config.enable_adv_xover"                            ,"Enable advanced crossover");
        add("gui.draconicevolution.item_config.enable_adv_xover.info"                       ,"Show configured properties, property groups and presets when in the simple configuration mode");
        add("gui.draconicevolution.item_config.not_bound"                                   ,"Not bound");
        add("gui.draconicevolution.item_config.toggle_global_binding.info"                  ,"Global Binding\nWill allow you to use this key bind when this gui is closed.\nThis will work with any key regardless of weather or not it conflicts with another keybinding.");
        add("gui.draconicevolution.item_config.set_key_bind.info"                           ,"Click to set a key bind for this preset");
        add("gui.draconicevolution.item_config.open_modules.info"                           ,"Open module configuration GUI");

        add("gui.draconicevolution.boolean_property.true"                                   ,"True");
        add("gui.draconicevolution.boolean_property.false"                                  ,"False");
        add("gui.draconicevolution.boolean_property.enabled"                                ,"Enabled");
        add("gui.draconicevolution.boolean_property.disabled"                               ,"Disabled");
        add("gui.draconicevolution.boolean_property.active"                                 ,"Active");
        add("gui.draconicevolution.boolean_property.inactive"                               ,"Inactive");
        add("gui.draconicevolution.boolean_property.yes"                                    ,"Yes");
        add("gui.draconicevolution.boolean_property.no"                                     ,"No");

        //Module GUI
        add("gui.draconicevolution.modular_item.name"                                       ,"Configure Modules");
        add("gui.draconicevolution.modular_item.modules"                                    ,"Modules");
        add("gui.draconicevolution.modular_item.open_item_config.info"                      ,"Open item configuration GUI");
        add("msg.draconicevolution.modular_item.no_module_hosts"                            ,"You do not have any modular items in your inventory!");
        add("gui.draconicevolution.modular_item.module_grid"                                ,"Module Grid");

        //Generator
        add("gui.draconicevolution.generator.fuel_efficiency"                               ,"Fuel efficiency:");
        add("gui.draconicevolution.generator.output_power"                                  ,"Output power:");
        add("gui.draconicevolution.generator.current_fuel_value"                            ,"Current fuel value:");
        add("gui.draconicevolution.generator.mode_eco_plus"                                 ,"Eco Plus");
        add("gui.draconicevolution.generator.mode_eco_plus,info"                            ,"Eco Plus\nSignificantly increased fuel efficiency.\nSignificantly decreased output power.");
        add("gui.draconicevolution.generator.mode_eco"                                      ,"Eco");
        add("gui.draconicevolution.generator.mode_eco,info"                                 ,"Eco Mode\nIncreased fuel efficiency\nat the cost of output power.");
        add("gui.draconicevolution.generator.mode_normal"                                   ,"Normal");
        add("gui.draconicevolution.generator.mode_normal,info"                              ,"Normal Mode\nStandard output and efficiency.\nSimilar to other basic generators.");
        add("gui.draconicevolution.generator.mode_performance"                              ,"Performance");
        add("gui.draconicevolution.generator.mode_performance,info"                         ,"Performance Mode\nProvides a worthwhile increase in output power\nfor a small fuel efficiency penalty.");
        add("gui.draconicevolution.generator.mode_performance_plus"                         ,"Overdrive");
        add("gui.draconicevolution.generator.mode_performance_plus,info"                    ,"Overdrive Mode\nNeed all the power you can get\nHave plenty of fuel to burn?\nThis is the mode for you!");


        //Grinder
        add("gui.draconicevolution.grinder.aoe"                                           ,"AOE:");
        add("gui.draconicevolution.grinder.aoe.info"                                      ,"Increment's the grinder's Area Of Effect.\n(The area in which it will kill mobs)");
        add("gui.draconicevolution.grinder.show_aoe"                                      ,"Show AOE");
        add("gui.draconicevolution.grinder.collect.items"                                 ,"Collect Items");
        add("gui.draconicevolution.grinder.collect.items.info"                            ,"If enabled will collect items within the kill area and insert them into an adjacent inventory.");
        add("gui.draconicevolution.grinder.collect.xp"                                    ,"Collect XP");
        add("gui.draconicevolution.grinder.collect.xp.info"                               ,"If enabled will collect experiance dropped within the kill area and store itinternally.\nThis XP can then be claimed by a player or piped out if there is a mod installed that adds liquid XP.");
        add("gui.draconicevolution.grinder.claim.xp"                                      ,"Claim XP");
        add("gui.draconicevolution.grinder.claim.xp.info"                                 ,"Claim all stored XP");
        add("gui.draconicevolution.grinder.claim.xp.level.info"                           ,"Claim 1 expireance level");
        add("gui.draconicevolution.grinder.claim.xp.levels.info"                          ,"Claim %s expireance levels");
        add("gui.draconicevolution.generator.stored_xp"                                   ,"Stored Expireance:");
        add("gui.draconicevolution.generator.stored_xp.raw"                               ,"(Raw XP)");

        //endregion








        //region # Modules
        add(ModuleTypes.ENERGY_STORAGE                                                      , "Energy Capacity");
        add("module.draconicevolution.energy.capacity"                                      , "Energy Capacity");
        add("module.draconicevolution.energy.capacity.value"                                , "+%s OP");
        add("module.draconicevolution.energy.transfer"                                      , "Energy Transfer");
        add("module.draconicevolution.energy.transfer.value"                                , "+%s OP/t");

        add(ModuleTypes.ENERGY_SHARE                                                        , "Energy Share");
        add(ModuleTypes.ENERGY_LINK                                                         , "Energy Link");
        add(ModuleTypes.SHIELD_CONTROLLER                                                   , "Shield Controller");
        add(ModuleTypes.SHIELD_BOOST                                                        , "Shield Boost");
        add("module.draconicevolution.shield_capacity.name"                                 , "Shield Capacity");
        add("module.draconicevolution.shield_capacity.value"                                , "%s points");
        add("module.draconicevolution.shield_recharge.name"                                 , "Shield Recharge");
        add("module.draconicevolution.shield_recharge.value"                                , "%s pps (%s seconds)\n@ %s OP/t");
        add("module.draconicevolution.shield_passive.name"                                  , "Shield Operating Cost");
        add("module.draconicevolution.shield_passive.value"                                 , "%s OP/t");
        add(ModuleTypes.FLIGHT                                                              , "Flight");
        add("module.draconicevolution.flight.name"                                          , "Flight");
        add("module.draconicevolution.flight.true.false"                                    , "Elytra");
        add("module.draconicevolution.flight.false.true"                                    , "Creative");
        add("module.draconicevolution.flight.true.true"                                     , "Creative & Elytra");
        add("module.draconicevolution.flight.boost.name"                                    , "Elytra Boost");
//        add("module.draconicevolution.flight.boost.value"                                   , "%s%");

        add(ModuleTypes.LAST_STAND                                                          , "Last Stand");
        add("module.draconicevolution.last_stand.health.name"                               , "Health Boost");
        add("module.draconicevolution.last_stand.health.value"                              , "%s Health points");
        add("module.draconicevolution.last_stand.shield.name"                               , "Shield Boost");
        add("module.draconicevolution.last_stand.shield.value"                              , "%s for %s seconds");
        add("module.draconicevolution.last_stand.charge.name"                               , "Charge Time");
        add("module.draconicevolution.last_stand.charge.value"                              , "%s Seconds");
        add("module.draconicevolution.last_stand.energy.name"                               , "Charge Energy");
        add("module.draconicevolution.last_stand.energy.value"                              , "%sOP @%s OP/t");
        add(ModuleTypes.AUTO_FEED                                                           , "Auto Feed");
        add("module.draconicevolution.auto_feed.name"                                       , "Food Storage");
        add("module.draconicevolution.auto_feed.value"                                      , "%s Hunger points");
        add("module.draconicevolution.auto_feed.stored"                                     , "Food Stored:");
        add("module.draconicevolution.auto_feed.stored.value"                               , "%s Hunger Points");
        add(ModuleTypes.NIGHT_VISION                                                        , "Night Vision");
        add(ModuleTypes.JUMP_BOOST                                                          , "Jump Boost");
        add("module.draconicevolution.jump.name"                                            , "Jump Boost");
        add("module.draconicevolution.jump.value"                                           , "+%s%%");
//        add(ModuleTypes.FALL_PROTECT                                                        , "Fall Protection");
        add(ModuleTypes.AQUA_ADAPT                                                          , "Aqua Adaptation");
        add(ModuleTypes.MINING_STABILITY                                                    , "Mining Stabilizer");
        add(ModuleTypes.AOE                                                                 , "AOE");
        add("module.draconicevolution.aoe.name"                                             , "AOE");
        add("module.draconicevolution.aoe.value"                                            , "%sx%s");
        add(ModuleTypes.DAMAGE                                                              , "Damage");
        add("module.draconicevolution.damage.name"                                          , "Damage");
        add("module.draconicevolution.damage.attack"                                        , "+%s Attack Damage");
        add(ModuleTypes.SPEED                                                               , "Speed");
        add("module.draconicevolution.speed.name"                                           , "Speed");
        add("module.draconicevolution.speed.value"                                          , "+%s%%");
        add(ModuleTypes.HILL_STEP                                                           , "Step Assist");
        add(ModuleTypes.JUNK_FILTER                                                         , "Junk Filter");

        add(DEModules.draconiumEnergy                                                       , "Energy Module");
        add(DEModules.wyvernEnergy                                                          , "Wyvern Energy Module");
        add(DEModules.draconicEnergy                                                        , "Draconic Energy Module");
        add(DEModules.chaoticEnergy                                                         , "Chaotic Energy Module");

        add(DEModules.draconiumSpeed                                                        , "Speed Module");
        add(DEModules.wyvernSpeed                                                           , "Wyvern Speed Module");
        add(DEModules.draconicSpeed                                                         , "Draconic Speed Module");
        add(DEModules.chaoticSpeed                                                          , "Chaotic Speed Module");

        add(DEModules.draconiumDamage                                                       , "Damage Module");
        add(DEModules.wyvernDamage                                                          , "Wyvern Damage Module");
        add(DEModules.draconicDamage                                                        , "Draconic Damage Module");
        add(DEModules.chaoticDamage                                                         , "Chaotic Damage Module");

        add(DEModules.draconiumAOE                                                          , "AOE Module");
        add(DEModules.wyvernAOE                                                             , "Wyvern AOE Module");
        add(DEModules.draconicAOE                                                           , "Draconic AOE Module");
        add(DEModules.chaoticAOE                                                            , "Chaotic AOE Module");

        add(DEModules.wyvernMiningStability                                                 , "Mining Stability Module");

        add(DEModules.wyvernJunkFilter                                                      , "Junk Filter Module");

        add(DEModules.wyvernShieldControl                                                   , "Wyvern Shield Control Module");
        add(DEModules.draconicShieldControl                                                 , "Draconic Shield Control Module");
        add(DEModules.chaoticShieldControl                                                  , "Chaotic Shield Control Module");

        add(DEModules.wyvernShieldCapacity                                                  , "Wyvern Shield Capacity Module");
        add(DEModules.draconicShieldCapacity                                                , "Draconic Shield Capacity Module");
        add(DEModules.chaoticShieldCapacity                                                 , "Chaotic Shield Capacity Module");

        add(DEModules.wyvernLargeShieldCapacity                                             , "Wyvern Large Shield Capacity Module");
        add(DEModules.draconicLargeShieldCapacity                                           , "Draconic Large Shield Capacity Module");
        add(DEModules.chaoticLargeShieldCapacity                                            , "Chaotic Large Shield Capacity Module");

        add(DEModules.wyvernShieldRecovery                                                  , "Wyvern Shield Recovery Module");
        add(DEModules.draconicShieldRecovery                                                , "Draconic Shield Recovery Module");
        add(DEModules.chaoticShieldRecovery                                                 , "Chaotic Shield Recovery Module");

        add(DEModules.wyvernFlight                                                          , "Wyvern Flight Module");
        add(DEModules.draconicFlight                                                        , "Draconic Flight Module");
        add(DEModules.chaoticFlight                                                         , "Chaotic Flight Module");

        add(DEModules.wyvernLastStand                                                       , "Wyvern Last Stand Module");
        add(DEModules.draconicLastStand                                                     , "Draconic Last Stand Module");
        add(DEModules.chaoticLastStand                                                      , "Chaotic Last Stand Module");

        add(DEModules.draconiumAutoFeed                                                     , "Auto Feed Module");
        add(DEModules.wyvernAutoFeed                                                        , "Wyvern Auto Feed Module");
        add(DEModules.draconicAutoFeed                                                      , "Draconic Auto Feed Module");

        add(DEModules.wyvernNightVision                                                     , "Night Vision Module");

        add(DEModules.draconiumJump                                                         , "Jump Module");
        add(DEModules.wyvernJump                                                            , "Wyvern Jump Module");
        add(DEModules.draconicJump                                                          , "Draconic Jump Module");
        add(DEModules.chaoticJump                                                           , "Chaotic Jump Module");

        add(DEModules.wyvernAquaAdapt                                                       , "Aqua Adapt Module");

        add(DEModules.wyvernHillStep                                                        , "Hill Step Module");




        add("module.draconicevolution.energy.stored_energy"                                 ,"Stored Energy");
        add("module.draconicevolution.module_type"                                          ,"Module Type");
        add("module.draconicevolution.grid_size"                                            ,"Module Size");
        add("module.draconicevolution.max_installable"                                      ,"Max Installable");

        //endregion

        //region Item Properties

        add("item_prop.draconicevolution.attack_aoe"                                        , "Attack Radius");
        add("item_prop.draconicevolution.attack_aoe.info"                                   , "Allows you to adjust the area covered by this weapon's swing.\nThis covers a 100 degree arc in the direction you are looking");
        add("item_prop.draconicevolution.mining_aoe"                                        , "Mining AOE");
        add("item_prop.draconicevolution.mining_aoe.info"                                   , "Allows you to adjust this tools mining area of effect.");
        add("item_prop.draconicevolution.aoe_safe"                                          , "AOE Safe Mode");
        add("item_prop.draconicevolution.aoe_safe.info"                                     , "When enabled this tool will not break anything if it detects a tile entity within its AOE range. This can help prevent you from accidentally breaking half your base with a single miss click");
        add("item_prop.draconicevolution.aoe_safe.blocked"                                  , "§9(§aAOE §aSafe §aMode §ais §aenabled§9) §cOperation §cCanceled §cbecause §ca §ctile §centity §cwas §cdetected");

        add("item_prop.draconicevolution.mining_speed"                                      , "Dig Speed Multiplier");
        add("item_prop.draconicevolution.mining_speed.info"                                 , "Allows you to adjust how fast this tool breaks blocks. Useful if you need precision over speed.");
        add("item_prop.draconicevolution.walk_speed"                                        , "Walk Speed");
        add("item_prop.draconicevolution.walk_speed.info"                                   , "Allows you to adjust the speed boost that is applied while you are walking.");
        add("item_prop.draconicevolution.run_speed"                                         , "Sprint Speed");
        add("item_prop.draconicevolution.run_speed.info"                                    , "Allows you to adjust the speed boost that is applied while you are sprinting.");
        add("item_prop.draconicevolution.de.module.jump_boost.prop"                         , "Jump Boost");
        add("item_prop.draconicevolution.de.module.jump_boost.prop.info"                    , "Adjust jump boost.");
        add("item_prop.draconicevolution.de.module.jump_boost_run.prop"                     , "Run Jump Boost");
        add("item_prop.draconicevolution.de.module.jump_boost_run.prop.info"                , "Adjust running jump boost.");

        add("item_prop.draconicevolution.shield_mod.enabled"                                , "Shield Toggle");
        add("item_prop.draconicevolution.shield_mod.enabled.info"                           , "Allows you to disable your shield. This prevents your shield from using power but leaves you vulnerable to damage.");
        add("item_prop.draconicevolution.shield_mod.always_visible"                         , "Shield Always Visible");
        add("item_prop.draconicevolution.shield_mod.always_visible.info"                    , "Purely cosmetic. If false your shield will only be visible when it absorbs damage.");

        add("item_prop.draconicevolution.feed_mod.consume_food"                             , "Consume Food.");
        add("item_prop.draconicevolution.feed_mod.consume_food.info"                        , "When enabled this module will automatically consume food from your inventory to fill its internal storage.");

        add("item_prop.draconicevolution.flight_mod.elytra"                                 , "Elytra Flight");
        add("item_prop.draconicevolution.flight_mod.elytra.info"                            , "Enables you to enable / disable elytra flight.");
        add("item_prop.draconicevolution.flight_mod.creative"                               , "Creative Flight");
        add("item_prop.draconicevolution.flight_mod.creative.info"                          , "Enables you to enable / disable creative flight.");

        //emdregion

        //region # Misc
        add("itemGroup.draconicevolution.blocks"                                            ,"Draconic Evolution Blocks");
        add("itemGroup.draconicevolution.items"                                             ,"Draconic Evolution Items");
        add("itemGroup.draconicevolution.modules"                                           ,"Draconic Evolution Modules");
        add("tech_level.draconicevolution.draconium"                                        ,"Draconium");
        add("tech_level.draconicevolution.wyvern"                                           ,"Wyvern");
        add("tech_level.draconicevolution.draconic"                                         ,"Draconic");
        add("tech_level.draconicevolution.chaotic"                                          ,"Chaotic");

        //@formatter:on
    }







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
}
