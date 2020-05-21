package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class LangGenerator extends LanguageProvider {
    public LangGenerator(DataGenerator gen) {
        super(gen, MODID, "en_us");
    }

    @Override
    public void add(Block key, String name) {
        if (key != null)super.add(key, name);
    }

    @Override
    public void add(Item key, String name) {
        if (key != null)super.add(key, name);
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
        add(DEContent.armor_wyvern                      ,"Wyvern Chest Piece");
        add(DEContent.armor_draconic                    ,"Draconic Chest Piece");
        add(DEContent.armor_chaotic                     ,"Chaotic Chest Piece");
        //endregion

        //region # Gui's and related translations
        //Item Config
        add("gui.draconicevolution.item_config.name"                                        ,"Configure Equipment");
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
        add("msg.draconicevolution.modular_item.no_module_hosts"                            ,"You do not have any modular items in your inventory!");
        add("gui.draconicevolution.modular_item.open_item_config.info"                      ,"Open item configuration GUI");


        //endregion






        //region # Misc
        add("itemGroup.draconicevolution.blocks"                                            ,"Draconic Evolution Blocks");
        add("itemGroup.draconicevolution.items"                                             ,"Draconic Evolution Items");
        add("itemGroup.draconicevolution.modules"                                           ,"Draconic Evolution Modules");





        //temp
        add("item_prop.draconicevolution.test_boolean1.name"                                ,"Test Boolean 1");
        add("item_prop.draconicevolution.test_integer1.name"                                ,"Test Integer 1");
        add("item_prop.draconicevolution.test_decimal1.name"                                ,"Test Decimal 1");
        add("item_prop.draconicevolution.test_enum.name"                                    ,"Test Enum");
        add("item_prop.draconicevolution.test_enum2.name"                                   ,"Test Enum 2");
        add("item_prop.draconicevolution.test_boolean2.name"                                ,"Test Boolean 2");
        add("item_prop.draconicevolution.test_boolean3.name"                                ,"Test Boolean 3");
        add("item_prop.draconicevolution.test_integer2.name"                                ,"Test Integer 2");
        add("item_prop.draconicevolution.test_integer3.name"                                ,"Test Integer 3");
        add("item_prop.draconicevolution.test_decimal2.name"                                ,"Test Decimal 2");
        add("item_prop.draconicevolution.test_decimal3.name"                                ,"Test Decimal 3");

        //@formatter:on
    }
}
