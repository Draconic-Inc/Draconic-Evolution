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
        PrefixHelper helper = new PrefixHelper(this);

        //@formatter:off
        //region # Blocks
        helper.add(DEContent.block_draconium                   ,"Draconium Block");
        helper.add(DEContent.block_draconium_awakened          ,"Awakened Draconium Block");
        helper.add(DEContent.chaos_crystal                     ,"Chaos Crystal");
        helper.add(DEContent.crafting_injector_basic           ,"Basic Fusion Crafting Injector");
        helper.add(DEContent.crafting_injector_wyvern          ,"Wyvern Fusion Crafting Injector");
        helper.add(DEContent.crafting_injector_awakened        ,"Draconic Fusion Crafting Injector");
        helper.add(DEContent.crafting_injector_chaotic         ,"Chaotic Fusion Crafting Injector");
        helper.add(DEContent.crafting_core                     ,"Fusion Crafting Core");
        helper.add(DEContent.crystal_relay_basic               ,"Basic Energy Relay Crystal");
        helper.add(DEContent.crystal_relay_wyvern              ,"Wyvern Energy Relay Crystal");
        helper.add(DEContent.crystal_relay_draconic            ,"Draconic Energy Relay Crystal");
//      helper.  add(DEContent.crystal_relay_chaotic           ,"Chaotic Energy Relay Crystal");
        helper.add(DEContent.crystal_io_basic                  ,"Basic Energy I/O Crystal");
        helper.add(DEContent.crystal_io_wyvern                 ,"Wyvern Energy I/O Crystal");
        helper.add(DEContent.crystal_io_draconic               ,"Draconic Energy I/O Crystal");
//      helper.  add(DEContent.crystal_io_chaotic              ,"Chaotic Energy I/O Crystal");
        helper.add(DEContent.crystal_wireless_basic            ,"Basic Wireless Energy Crystal");
        helper.add(DEContent.crystal_wireless_wyvern           ,"Wyvern Wireless Energy Crystal");
        helper.add(DEContent.crystal_wireless_draconic         ,"Draconic Wireless Energy Crystal");
//      helper.  add(DEContent.crystal_wireless_chaotic        ,"Chaotic Wireless Energy Crystal");
        helper.add(DEContent.creative_op_capacitor             ,"Creative Power Source");
        helper.add(DEContent.celestial_manipulator             ,"Celestial Manipulator");
        helper.add(DEContent.disenchanter                      ,"Disenchanter");
        helper.add(DEContent.dislocation_inhibitor             ,"Dislocation Normalization Field Projector");
        helper.add(DEContent.dislocator_pedestal               ,"Dislocator Pedestal");
        helper.add(DEContent.dislocator_receptacle             ,"Dislocator Receptacle");
        helper.add(DEContent.draconium_chest                   ,"Draconium Chest");
        helper.add(DEContent.energy_infuser                    ,"Energy Infuser");
        helper.add(DEContent.entity_detector                   ,"Entity Detector");
        helper.add(DEContent.entity_detector_advanced          ,"Advanced Entity Detector");
        helper.add(DEContent.energy_core                       ,"Energy Core");
        helper.add(DEContent.energy_core_stabilizer            ,"Energy Core Stabilizer");
        helper.add(DEContent.energy_pylon                      ,"Energy Pylon");
        helper.add(DEContent.energy_core_structure             ,"");
        helper.add(DEContent.flux_gate                         ,"Flux Gate");
        helper.add(DEContent.fluid_gate                        ,"Fluid Gate");
        helper.add(DEContent.generator                         ,"Generator");
        helper.add(DEContent.grinder                           ,"Mob Grinder");
        helper.add(DEContent.infused_obsidian                  ,"Draconium Infused Obsidian");
        helper.add(DEContent.ore_draconium_overworld           ,"Draconium Ore");
        helper.add(DEContent.ore_draconium_nether              ,"Nether Draconium Ore");
        helper.add(DEContent.ore_draconium_end                 ,"Ender Draconium Ore");
        helper.add(DEContent.potentiometer                     ,"Potentiometer");
        helper.add(DEContent.particle_generator                ,"Particle Generator");
        helper.add(DEContent.placed_item                       ,"");
        helper.add(DEContent.portal                            ,"Portal");
        helper.add(DEContent.reactor_core                      ,"Draconic Reactor Core");
        helper.add(DEContent.reactor_stabilizer                ,"Reactor Stabilizer");
        helper.add(DEContent.reactor_injector                  ,"Reactor Energy Injector");
        helper.add(DEContent.rain_sensor                       ,"Rain Sensor");
        helper.add(DEContent.stabilized_spawner                ,"Stabilized Mob Spawner");
        //endregion

        //region # Items
        //Components
        helper.add(DEContent.dust_draconium                    ,"Draconium Dust");
        helper.add(DEContent.dust_draconium_awakened           ,"Awakened Draconium Dust");
        helper.add(DEContent.ingot_draconium                   ,"Draconium Ingot");
        helper.add(DEContent.ingot_draconium_awakened          ,"Awakened Draconium Ingot");
        helper.add(DEContent.nugget_draconium                  ,"Draconium Nugget");
        helper.add(DEContent.nugget_draconium_awakened         ,"Awakened Draconium Nugget");
        helper.add(DEContent.core_draconium                    ,"Draconium Core");
        helper.add(DEContent.core_wyvern                       ,"Wyvern Core");
        helper.add(DEContent.core_awakened                     ,"Draconic Core");
        helper.add(DEContent.core_chaotic                      ,"Chaotic Core");
        helper.add(DEContent.energy_core_wyvern                ,"Wyvern Energy Core");
        helper.add(DEContent.energy_core_draconic              ,"Draconic Energy Core");
        helper.add(DEContent.dragon_heart                      ,"Dragon Heart");
        helper.add(DEContent.chaos_shard                       ,"Chaos Shard");
        helper.add(DEContent.chaos_frag_large                  ,"Large Chaos Fragment");
        helper.add(DEContent.chaos_frag_medium                 ,"Small Chaos Fragment");
        helper.add(DEContent.chaos_frag_small                  ,"Tiny Chaos Fragment");
        //Misc Tools
        helper.add(DEContent.magnet                            ,"Item Dislocator");
        helper.add(DEContent.magnet_advanced                   ,"Awakened Item Dislocator");
        helper.add(DEContent.dislocator                        ,"Dislocator");
        helper.add(DEContent.dislocator_advanced               ,"Advanced Dislocator");
        helper.add(DEContent.dislocator_p2p                    ,"Bound Dislocator (Player)");
        helper.add(DEContent.dislocator_player                 ,"Bound Dislocator (Point to Point)");
        helper.add(DEContent.crystal_binder                    ,"Crystal Binder");
        helper.add(DEContent.info_tablet                       ,"Information Tablet");
        helper.add(DEContent.ender_energy_manipulator          ,"Ender energy manipulator");
        helper.add(DEContent.creative_exchanger                ,"Creative Exchanger");
        helper.add(DEContent.mob_soul                          ,"Mob soul");
        //Tools
        helper.add(DEContent.capacitor_wyvern                  ,"Wyvern Capacitor");
        helper.add(DEContent.capacitor_draconic                ,"Draconic Capacitor");
        helper.add(DEContent.capacitor_chaotic                 ,"Chaotic Capacitor");
        helper.add(DEContent.capacitor_creative                ,"Creative Capacitor");
        helper.add(DEContent.shovel_wyvern                     ,"Wyvern Shovel");
        helper.add(DEContent.shovel_draconic                   ,"Draconic Shovel");
        helper.add(DEContent.shovel_chaotic                    ,"Cheotic Shovel");
        helper.add(DEContent.hoe_wyvern                        ,"Wyvern Hoe");
        helper.add(DEContent.hoe_draconic                      ,"Draconic Hoe");
        helper.add(DEContent.hoe_chaotic                       ,"Chaotic Hoe");
        helper.add(DEContent.pickaxe_wyvern                    ,"Wyvern Pickaxe");
        helper.add(DEContent.pickaxe_draconic                  ,"Draconic Pickaxe");
        helper.add(DEContent.pickaxe_chaotic                   ,"Chaotic Pickaxe");
        helper.add(DEContent.axe_wyvern                        ,"Wyvern Axe");
        helper.add(DEContent.axe_draconic                      ,"Draconic Axe");
        helper.add(DEContent.axe_chaotic                       ,"Cheotic Axe");
        helper.add(DEContent.bow_wyvern                        ,"Wyvern Bow");
        helper.add(DEContent.bow_draconic                      ,"Draconic Bow");
        helper.add(DEContent.bow_chaotic                       ,"Chaotic Bow");
        helper.add(DEContent.sword_wyvern                      ,"Wyvern Sword");
        helper.add(DEContent.sword_draconic                    ,"Draconic Sword");
        helper.add(DEContent.sword_chaotic                     ,"Chaotic Sword");
        helper.add(DEContent.staff_draconic                    ,"Draconic Staff of Power");
        helper.add(DEContent.staff_chaotic                     ,"Chaotic Staff of Power");
        //Armor
        helper.add(DEContent.chestpiece_wyvern                 ,"Wyvern Chest Piece");
        helper.add(DEContent.chestpiece_draconic               ,"Draconic Chest Piece");
        helper.add(DEContent.chestpiece_chaotic                ,"Chaotic Chest Piece");
        //endregion



        //region # Gui's and related translations
        //Item Config
        helper.setPrefix("gui.draconicevolution.item_config");
        helper.add("name"                                                                   ,"Configure Equipment");
        helper.add("configure"                                                              ,"Configure");
        helper.add("toggle_hidden.info"                                                     ,"Show / Hide Inventory");
        helper.add("toggle_advanced.info"                                                   ,"Toggle advanced config mode");
        helper.add("delete_zone.info"                                                       ,"Drop a property or group here to delete it");
        helper.add("add_group"                                                              ,"Add Group");
        helper.add("add_group.info"                                                         ,"Add a new property group");
        helper.add("click_and_drag_to_place"                                                ,"Click and drag");
        helper.add("edit_preset.info"                                                       ,"Edit preset properties");
        helper.add("expand_group.info"                                                      ,"Expand Group");
        helper.add("collapse_group.info"                                                    ,"Collapse Group");
        helper.add("move_group.info"                                                        ,"Click and drag to move this group");
        helper.add("delete_group.info"                                                      ,"Delete");
        helper.add("copy_group.info"                                                        ,"Copy Group");
        helper.add("toggle_preset.info"                                                     ,"Toggle preset mode\nConverts this property group into a property preset");
        helper.add("apply_preset"                                                           ,"Apply Preset");
        helper.add("move_prop.info"                                                         ,"Click and drag to move property");
        helper.add("drop_create_group.info"                                                 ,"Drop to create group");
        helper.add("add_to_group.info"                                                      ,"Add to group");
        helper.add("drop_to_delete.info"                                                    ,"Drop to delete");
        helper.add("drop_prop_here"                                                         ,"Drop Property Here");
        helper.add("global.info"                                                            ,"Toggle global mode\nWhen in global mode a property will be apply to all equipment of the same type\nAlso note when in global mode the displayed value may not match the items actual value");
        helper.add("provider_unavailable"                                                   ,"Item not available");
        helper.add("select_item_to_get_started"                                             ,"Select a configurable item to get started");
        helper.add("options"                                                                ,"Options");
        helper.add("hide_unavailable"                                                       ,"Hide unavailable");
        helper.add("hide_unavailable.info"                                                  ,"Hide properties if the associated item is not available");
        helper.add("show_unavailable"                                                       ,"Show unavailable");
        helper.add("show_unavailable.info"                                                  ,"Show properties if the associated item is not available");
        helper.add("disable_snapping"                                                       ,"Disable snapping");
        helper.add("disable_snapping.info"                                                  ,"Toggle property / group position snapping");
        helper.add("enable_snapping"                                                        ,"Enable snapping");
        helper.add("disable_visualization"                                                  ,"Disable visualization");
        helper.add("disable_visualization.info"                                             ,"Toggle the highlight / animation that occurs over a properties associated item when hovering over or editing a property");
        helper.add("enable_visualization"                                                   ,"Enable visualization");
        helper.add("hide_group_button"                                                      ,"Hide \"New Group\" button");
        helper.add("show_group_button"                                                      ,"Show \"New Group\" button");
        helper.add("hide_delete_zone"                                                       ,"Hide \"Delete Zone\"");
        helper.add("show_delete_zone"                                                       ,"Show \"Delete Zone\"");
        helper.add("disable_adv_xover"                                                      ,"Disable advanced crossover");
        helper.add("disable_adv_xover.info"                                                 ,"Hide configured properties, property groups and presets when in the simple configuration mode");
        helper.add("enable_adv_xover"                                                       ,"Enable advanced crossover");
        helper.add("enable_adv_xover.info"                                                  ,"Show configured properties, property groups and presets when in the simple configuration mode");
        helper.add("not_bound"                                                              ,"Not bound");
        helper.add("toggle_global_binding.info"                                             ,"Global Binding\nWill allow you to use this key bind when this gui is closed.\nThis will work with any key regardless of weather or not it conflicts with another keybinding.");
        helper.add("set_key_bind.info"                                                      ,"Click to set a key bind for this preset");
        helper.add("open_modules.info"                                                      ,"Open module configuration GUI");

        helper.setPrefix("gui.draconicevolution.boolean_property");
        helper.add("true"                                                                   ,"True");
        helper.add("false"                                                                  ,"False");
        helper.add("enabled"                                                                ,"Enabled");
        helper.add("disabled"                                                               ,"Disabled");
        helper.add("active"                                                                 ,"Active");
        helper.add("inactive"                                                               ,"Inactive");
        helper.add("yes"                                                                    ,"Yes");
        helper.add("no"                                                                     ,"No");

        //Module GUI
        helper.setPrefix("gui.draconicevolution.modular_item");
        helper.add("name"                                                                   ,"Configure Modules");
        helper.add("modules"                                                                ,"Modules");
        helper.add("open_item_config.info"                                                  ,"Open item configuration GUI");
        helper.add("no_module_hosts"                                                        ,"You do not have any modular items in your inventory!");
        helper.add("module_grid"                                                            ,"Module Grid");

        //Generator
        helper.setPrefix("gui.draconicevolution.generator");
        helper.add("fuel_efficiency"                                                        ,"Fuel efficiency:");
        helper.add("output_power"                                                           ,"Output power:");
        helper.add("current_fuel_value"                                                     ,"Current fuel value:");
        helper.add("mode_eco_plus"                                                          ,"Eco Plus");
        helper.add("mode_eco_plus,info"                                                     ,"Eco Plus\nSignificantly increased fuel efficiency.\nSignificantly decreased output power.");
        helper.add("mode_eco"                                                               ,"Eco");
        helper.add("mode_eco,info"                                                          ,"Eco Mode\nIncreased fuel efficiency\nat the cost of output power.");
        helper.add("mode_normal"                                                            ,"Normal");
        helper.add("mode_normal,info"                                                       ,"Normal Mode\nStandard output and efficiency.\nSimilar to other basic generators.");
        helper.add("mode_performance"                                                       ,"Performance");
        helper.add("mode_performance,info"                                                  ,"Performance Mode\nProvides a worthwhile increase in output power\nfor a small fuel efficiency penalty.");
        helper.add("mode_performance_plus"                                                  ,"Overdrive");
        helper.add("mode_performance_plus,info"                                             ,"Overdrive Mode\nNeed all the power you can get\nHave plenty of fuel to burn?\nThis is the mode for you!");

        //Grinder
        helper.setPrefix("gui.draconicevolution.grinder");
        helper.add("aoe"                                                                    ,"AOE:");
        helper.add("aoe.info"                                                               ,"Increment's the grinder's Area Of Effect.\n(The area in which it will kill mobs)");
        helper.add("show_aoe"                                                               ,"Show AOE");
        helper.add("collect.items"                                                          ,"Collect Items");
        helper.add("collect.items.info"                                                     ,"If enabled will collect items within the kill area and insert them into an adjacent inventory.");
        helper.add("collect.xp"                                                             ,"Collect XP");
        helper.add("collect.xp.info"                                                        ,"If enabled will collect experiance dropped within the kill area and store itinternally.\nThis XP can then be claimed by a player or piped out if there is a mod installed that adds liquid XP.");
        helper.add("claim.xp"                                                               ,"Claim XP");
        helper.add("claim.xp.info"                                                          ,"Claim all stored XP");
        helper.add("claim.xp.level.info"                                                    ,"Claim 1 expireance level");
        helper.add("claim.xp.levels.info"                                                   ,"Claim %s expireance levels");
        helper.add("stored_xp"                                                              ,"Stored Expireance:");
        helper.add("stored_xp.raw"                                                          ,"(Raw XP)");

        //Reactor
        helper.setPrefix("gui.draconicevolution.reactor");
        helper.add("title"                                                                  ,"Draconic Reactor");
        helper.add("core_volume"                                                            ,"Core Volume");
        helper.add("core_volume.info"                                                       ,"This shows the total volume of matter within the reactor in cubic meters (Draconium + Chaos). This value will only change when you add or remove fuel.");
        helper.add("gen_rate"                                                               ,"Generation Rate");
        helper.add("gen_rate.info"                                                          ,"This is the current RF/t being generated by the reactor.");
        helper.add("field_rate"                                                             ,"Field Input Rate");
        helper.add("field_rate.info"                                                        ,"This is the exact RF/t input required to maintain the current field strength. As field strength increases, this will increase exponentially.");
        helper.add("convert_rate"                                                           ,"Fuel Conversion Rate");
        helper.add("convert_rate.info"                                                      ,"This is how fast the reactor is currently using fuel. As the reactor saturation increases, this will go down.");
        helper.add("go_boom_now"                                                            ,"Emergency shield reserve is now active but it wont last long! There is no way to stop the overload the stabilizers are fried. I suggest you run!");
        helper.add("fuel_in"                                                                ,"Fuel (in)");
        helper.add("chaos_out"                                                              ,"Chaos (out)");
        helper.add("status"                                                                 ,"Status");

        helper.add("charge"                                                                 ,"Charge");
        helper.add("activate"                                                               ,"Activate");
        helper.add("shutdown"                                                               ,"Shutdown");
        helper.add("rs_mode"                                                                ,"Redstone\nMode");
        helper.add("rs_mode.info"                                                           ,"Configure the comparator output for this reactor component.");
        helper.add("sas"                                                                    ,"SAS");
        helper.add("sas.info"                                                               ,"Semi-Automated Shutdown. When enabled the reactor will automatically initiate shutdown when the Temperature drops bellow 2500C and Saturation reaches 99%% This can be used to automatically shutdown your reactor in the event of a malfunction or just when it needs to be refueled.");

        helper.add("rs_mode_temp"                                                           ,"Temp");
        helper.add("rs_mode_temp.info"                                                      ,"Will output a signal from 0 to 15 as temperature rises up to 10000.");
        helper.add("rs_mode_temp_inv"                                                       ,"-Temp");
        helper.add("rs_mode_temp_inv.info"                                                  ,"This is the same as Temperature except the signal is inverted.");
        helper.add("rs_mode_field"                                                          ,"Shield");
        helper.add("rs_mode_field.info"                                                     ,"Will output a signal from 0 to 15 as the shield strength fluctuates between 0 and 100%, Signal of 1 = > 10% shield and Signal of 15 = >= 90% shield power.");
        helper.add("rs_mode_field_inv"                                                      ,"-Shield");
        helper.add("rs_mode_field_inv.info"                                                 ,"-This is the same as Shield except the signal is inverted.");
        helper.add("rs_mode_sat"                                                            ,"Saturation");
        helper.add("rs_mode_sat.info"                                                       ,"Will output a signal from 0 to 15 as the saturation level fluctuates between 0 and 100%.");
        helper.add("rs_mode_sat_inv"                                                        ,"-Saturation");
        helper.add("rs_mode_sat_inv.info"                                                   ,"This is the same as Saturation except the signal is inverted.");
        helper.add("rs_mode_fuel"                                                           ,"Conversion");
        helper.add("rs_mode_fuel.info"                                                      ,"Will output a signal from 0 to 15 as the fuel conversion level increases from 0 to 100%, Signal of 15 = >= 90% Conversion.");
        helper.add("rs_mode_fuel_inv"                                                       ,"-Conversion");
        helper.add("rs_mode_fuel_inv.info"                                                  ,"This is the same as Conversion except the signal is inverted.");

        helper.add("reaction_temp"                                                          ,"Core Temperature");
        helper.add("field_strength"                                                         ,"Containment Field Strength");
        helper.add("energy_saturation"                                                      ,"Energy Saturation");
        helper.add("fuel_conversion"                                                        ,"Fuel Conversion Level");

        helper.setPrefix("gui.reactor.status");
        helper.add("invalid.info"                                                           ,"Invalid Setup");
        helper.add("cold.info"                                                              ,"Offline");
        helper.add("warming_up.info"                                                        ,"Warming Up");
        helper.add("running.info"                                                           ,"Online");
        helper.add("stopping.info"                                                          ,"Stopping");
        helper.add("cooling.info"                                                           ,"Cooling Down");
        helper.add("beyond_hope.info"                                                       ,"Explosion Imminent!!!");

        //Flow Gate
        helper.setPrefix("gui.draconicevolution.flow_gate");
        helper.add("overridden"                                                             ,"Overridden");
        helper.add("overridden.info"                                                        ,"Default controls have been disabled by a computer");
        helper.add("redstone_high"                                                          ,"Redstone Signal High");
        helper.add("redstone_high.info"                                                     ,"The flow that will be allowed when to pass when receiving a redstone signal of 15");
        helper.add("apply"                                                                  ,"Apply");
        helper.add("redstone_low"                                                           ,"Redstone Signal Low");
        helper.add("redstone_low.info"                                                      ,"The flow that will be allowed when to pass when receiving a redstone signal of 0");
        helper.add("flow"                                                                   ,"Flow");
        helper.add("flow.info"                                                              ,"The actual flow will vary between the two given values depending on the strength of the redstone signal being supplied to the block.");

        //endregion





        //region # Modules
        helper.setPrefix("module.draconicevolution");
        helper.add(ModuleTypes.ENERGY_STORAGE                                               , "Energy Capacity");
        helper.add("energy.capacity"                                                        , "Energy Capacity");
        helper.add("energy.capacity.value"                                                  , "+%s OP");
        helper.add("energy.transfer"                                                        , "Energy Transfer");
        helper.add("energy.transfer.value"                                                  , "+%s OP/t");

        helper.add(ModuleTypes.ENERGY_SHARE                                                 , "Energy Share");
        helper.add(ModuleTypes.ENERGY_LINK                                                  , "Energy Link");
        helper.add(ModuleTypes.SHIELD_CONTROLLER                                            , "Shield Controller");
        helper.add(ModuleTypes.SHIELD_BOOST                                                 , "Shield Boost");
        helper.add("shield_capacity.name"                                                   , "Shield Capacity");
        helper.add("shield_capacity.value"                                                  , "%s points");
        helper.add("shield_recharge.name"                                                   , "Shield Recharge");
        helper.add("shield_recharge.value"                                                  , "%s pps (%s seconds)\n@ %s OP/t");
        helper.add("shield_passive.name"                                                    , "Shield Operating Cost");
        helper.add("shield_passive.value"                                                   , "%s OP/t");
        helper.add(ModuleTypes.FLIGHT                                                       , "Flight");
        helper.add("flight.name"                                                            , "Flight");
        helper.add("flight.true.false"                                                      , "Elytra");
        helper.add("flight.false.true"                                                      , "Creative");
        helper.add("flight.true.true"                                                       , "Creative & Elytra");
        helper.add("flight.boost.name"                                                      , "Elytra Boost");
//        add("module.draconicevolution.flight.boost.value"                                   , "%s%");

        helper.add(ModuleTypes.LAST_STAND                                                   , "Last Stand");
        helper.add("last_stand.health.name"                                                 , "Health Boost");
        helper.add("last_stand.health.value"                                                , "%s Health points");
        helper.add("last_stand.shield.name"                                                 , "Shield Boost");
        helper.add("last_stand.shield.value"                                                , "%s for %s seconds");
        helper.add("last_stand.charge.name"                                                 , "Charge Time");
        helper.add("last_stand.charge.value"                                                , "%s Seconds");
        helper.add("last_stand.energy.name"                                                 , "Charge Energy");
        helper.add("last_stand.energy.value"                                                , "%sOP @%s OP/t");
        helper.add(ModuleTypes.AUTO_FEED                                                    , "Auto Feed");
        helper.add("auto_feed.name"                                                         , "Food Storage");
        helper.add("auto_feed.value"                                                        , "%s Hunger points");
        helper.add("auto_feed.stored"                                                       , "Food Stored:");
        helper.add("auto_feed.stored.value"                                                 , "%s Hunger Points");
        helper.add(ModuleTypes.NIGHT_VISION                                                 , "Night Vision");
        helper.add(ModuleTypes.JUMP_BOOST                                                   , "Jump Boost");
        helper.add("jump.name"                                                              , "Jump Boost");
        helper.add("jump.value"                                                             , "+%s%%");
//        add(ModuleTypes.FALL_PROTECT                                                        , "Fall Protection");
        helper.add(ModuleTypes.AQUA_ADAPT                                                   , "Aqua Adaptation");
        helper.add(ModuleTypes.MINING_STABILITY                                             , "Mining Stabilizer");
        helper.add(ModuleTypes.AOE                                                          , "AOE");
        helper.add("aoe.name"                                                               , "AOE");
        helper.add("aoe.value"                                                              , "%sx%s");
        helper.add(ModuleTypes.DAMAGE                                                       , "Damage");
        helper.add("damage.name"                                                            , "Damage");
        helper.add("damage.attack"                                                          , "+%s Attack Damage");
        helper.add(ModuleTypes.SPEED                                                        , "Speed");
        helper.add("speed.name"                                                             , "Speed");
        helper.add("speed.value"                                                            , "+%s%%");
        helper.add(ModuleTypes.HILL_STEP                                                    , "Step Assist");
        helper.add(ModuleTypes.JUNK_FILTER                                                  , "Junk Filter");

        helper.add(DEModules.draconiumEnergy                                                , "Energy Module");
        helper.add(DEModules.wyvernEnergy                                                   , "Wyvern Energy Module");
        helper.add(DEModules.draconicEnergy                                                 , "Draconic Energy Module");
        helper.add(DEModules.chaoticEnergy                                                  , "Chaotic Energy Module");

        helper.add(DEModules.draconiumSpeed                                                 , "Speed Module");
        helper.add(DEModules.wyvernSpeed                                                    , "Wyvern Speed Module");
        helper.add(DEModules.draconicSpeed                                                  , "Draconic Speed Module");
        helper.add(DEModules.chaoticSpeed                                                   , "Chaotic Speed Module");

        helper.add(DEModules.draconiumDamage                                                , "Damage Module");
        helper.add(DEModules.wyvernDamage                                                   , "Wyvern Damage Module");
        helper.add(DEModules.draconicDamage                                                 , "Draconic Damage Module");
        helper.add(DEModules.chaoticDamage                                                  , "Chaotic Damage Module");

        helper.add(DEModules.draconiumAOE                                                   , "AOE Module");
        helper.add(DEModules.wyvernAOE                                                      , "Wyvern AOE Module");
        helper.add(DEModules.draconicAOE                                                    , "Draconic AOE Module");
        helper.add(DEModules.chaoticAOE                                                     , "Chaotic AOE Module");

        helper.add(DEModules.wyvernMiningStability                                          , "Mining Stability Module");

        helper.add(DEModules.wyvernJunkFilter                                               , "Junk Filter Module");

        helper.add(DEModules.wyvernShieldControl                                            , "Wyvern Shield Control Module");
        helper.add(DEModules.draconicShieldControl                                          , "Draconic Shield Control Module");
        helper.add(DEModules.chaoticShieldControl                                           , "Chaotic Shield Control Module");

        helper.add(DEModules.wyvernShieldCapacity                                           , "Wyvern Shield Capacity Module");
        helper.add(DEModules.draconicShieldCapacity                                         , "Draconic Shield Capacity Module");
        helper.add(DEModules.chaoticShieldCapacity                                          , "Chaotic Shield Capacity Module");

        helper.add(DEModules.wyvernLargeShieldCapacity                                      , "Wyvern Large Shield Capacity Module");
        helper.add(DEModules.draconicLargeShieldCapacity                                    , "Draconic Large Shield Capacity Module");
        helper.add(DEModules.chaoticLargeShieldCapacity                                     , "Chaotic Large Shield Capacity Module");

        helper.add(DEModules.wyvernShieldRecovery                                           , "Wyvern Shield Recovery Module");
        helper.add(DEModules.draconicShieldRecovery                                         , "Draconic Shield Recovery Module");
        helper.add(DEModules.chaoticShieldRecovery                                          , "Chaotic Shield Recovery Module");

        helper.add(DEModules.wyvernFlight                                                   , "Wyvern Flight Module");
        helper.add(DEModules.draconicFlight                                                 , "Draconic Flight Module");
        helper.add(DEModules.chaoticFlight                                                  , "Chaotic Flight Module");

        helper.add(DEModules.wyvernLastStand                                                , "Wyvern Last Stand Module");
        helper.add(DEModules.draconicLastStand                                              , "Draconic Last Stand Module");
        helper.add(DEModules.chaoticLastStand                                               , "Chaotic Last Stand Module");

        helper.add(DEModules.draconiumAutoFeed                                              , "Auto Feed Module");
        helper.add(DEModules.wyvernAutoFeed                                                 , "Wyvern Auto Feed Module");
        helper.add(DEModules.draconicAutoFeed                                               , "Draconic Auto Feed Module");

        helper.add(DEModules.wyvernNightVision                                              , "Night Vision Module");

        helper.add(DEModules.draconiumJump                                                  , "Jump Module");
        helper.add(DEModules.wyvernJump                                                     , "Wyvern Jump Module");
        helper.add(DEModules.draconicJump                                                   , "Draconic Jump Module");
        helper.add(DEModules.chaoticJump                                                    , "Chaotic Jump Module");

        helper.add(DEModules.wyvernAquaAdapt                                                , "Aqua Adapt Module");

        helper.add(DEModules.wyvernHillStep                                                 , "Hill Step Module");




        helper.add("energy.stored_energy"                                                   ,"Stored Energy");
        helper.add("module_type"                                                            ,"Module Type");
        helper.add("grid_size"                                                              ,"Module Size");
        helper.add("max_installable"                                                        ,"Max Installable");

        //endregion

        //region Item Properties

        helper.setPrefix("item_prop.draconicevolution");
        helper.add("attack_aoe"                                                             , "Attack Radius");
        helper.add("attack_aoe.info"                                                        , "Allows you to adjust the area covered by this weapon's swing.\nThis covers a 100 degree arc in the direction you are looking");
        helper.add("mining_aoe"                                                             , "Mining AOE");
        helper.add("mining_aoe.info"                                                        , "Allows you to adjust this tools mining area of effect.");
        helper.add("aoe_safe"                                                               , "AOE Safe Mode");
        helper.add("aoe_safe.info"                                                          , "When enabled this tool will not break anything if it detects a tile entity within its AOE range. This can help prevent you from accidentally breaking half your base with a single miss click");
        helper.add("aoe_safe.blocked"                                                       , "§9(§aAOE §aSafe §aMode §ais §aenabled§9) §cOperation §cCanceled §cbecause §ca §ctile §centity §cwas §cdetected");

        helper.add("mining_speed"                                                           , "Dig Speed Multiplier");
        helper.add("mining_speed.info"                                                      , "Allows you to adjust how fast this tool breaks blocks. Useful if you need precision over speed.");
        helper.add("walk_speed"                                                             , "Walk Speed");
        helper.add("walk_speed.info"                                                        , "Allows you to adjust the speed boost that is applied while you are walking.");
        helper.add("run_speed"                                                              , "Sprint Speed");
        helper.add("run_speed.info"                                                         , "Allows you to adjust the speed boost that is applied while you are sprinting.");
        helper.add("de.module.jump_boost.prop"                                              , "Jump Boost");
        helper.add("de.module.jump_boost.prop.info"                                         , "Adjust jump boost.");
        helper.add("de.module.jump_boost_run.prop"                                          , "Run Jump Boost");
        helper.add("de.module.jump_boost_run.prop.info"                                     , "Adjust running jump boost.");

        helper.add("shield_mod.enabled"                                                     , "Shield Toggle");
        helper.add("shield_mod.enabled.info"                                                , "Allows you to disable your shield. This prevents your shield from using power but leaves you vulnerable to damage.");
        helper.add("shield_mod.always_visible"                                              , "Shield Always Visible");
        helper.add("shield_mod.always_visible.info"                                         , "Purely cosmetic. If false your shield will only be visible when it absorbs damage.");

        helper.add("feed_mod.consume_food"                                                  , "Consume Food.");
        helper.add("feed_mod.consume_food.info"                                             , "When enabled this module will automatically consume food from your inventory to fill its internal storage.");

        helper.add("flight_mod.elytra"                                                      , "Elytra Flight");
        helper.add("flight_mod.elytra.info"                                                 , "Enables you to enable / disable elytra flight.");
        helper.add("flight_mod.creative"                                                    , "Creative Flight");
        helper.add("flight_mod.creative.info"                                               , "Enables you to enable / disable creative flight.");
        helper.add("flight_mod.elytra_boost"                                                , "Elytra Boost");
        helper.add("flight_mod.elytra_boost.info"                                           , "Allows you to adjust the power of Elytra speed boost.\nSpeed boost can be activated by pressing forward + sprint key while flying");

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

    public static class PrefixHelper {
        private LangGenerator generator;
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
}
