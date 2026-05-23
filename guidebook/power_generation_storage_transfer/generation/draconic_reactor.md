---
navigation:
  parent: power_generation_storage_transfer/power_generation_storage_transfer.md
  title: Draconic Reactor
  icon: draconicevolution:reactor_core
item_ids:
  - draconicevolution:reactor_core
  - draconicevolution:reactor_stabilizer
  - draconicevolution:reactor_injector
  - draconicevolution:reactor_prt_focus_ring
  - draconicevolution:reactor_prt_in_rotor
  - draconicevolution:reactor_prt_out_rotor
  - draconicevolution:reactor_prt_rotor_full
  - draconicevolution:reactor_prt_stab_frame
---

<GameScene zoom="2" interactive={true}>
    <ImportStructure src="./reactor.nbt"/>
    <Entity x="5.5" y="5.5" z="5.5" id="minecraft:item_display" data="{item:{id:'draconicevolution:reactor_core'},item_display:'head'}"/>
</GameScene>


# Draconic Reactor

The Draconic Reactor is one of the most powerful reactors there are! Sure, it may also be one of the most *destructive* reactors if you mess up, but at least it won't spew radiation everywhere if it blows up.

## If you haven't already, go read [On the Subject of Draconic Reactors](./reactor_how_to.md)!

Setting up a Draconic Reactor is a lot like setting up a [Energy Core](../storage/energy_core.md), substituting the <ItemLink id="draconicevolution:energy_core" /> for a <ItemLink id="draconicevolution:reactor_core" />, the <ItemLink id="draconicevolution:energy_pylon" /> and <ItemLink id="minecraft:glass" /> for a <ItemLink id="draconicevolution:reactor_injector" />, and the <ItemLink id="draconicevolution:energy_core_stabilizer" />*s* for <ItemLink id="draconicevolution:reactor_stabilizer" />*s*.

These blocks should be placed at least four blocks away from the <ItemLink id="draconicevolution:reactor_core" />, as it expands as it heats.

> <Color id="red">WARNING: An expanding <ItemLink id="draconicevolution:reactor_core" /> can (and will!) destroy any poor <ItemLink id="draconicevolution:reactor_injector" /> in its way, leading to a catastrophic failure.</Color>

To run the reactor safely you will need a large energy buffer to store all the power that the reactor will generate. You will also need a substantial amount of energy to activate the reactor.

<ItemLink id="draconicevolution:flux_gate" />s are encouraged for limiting the amount of OP to and from the reactor. A <ItemLink id="draconicevolution:wyvern_io_crystal" /> is also recommended for transferring all the generated power.

# Recipes
<RecipeFor id="draconicevolution:reactor_core" />
<RecipeFor id="draconicevolution:reactor_prt_focus_ring" />
<RecipeFor id="draconicevolution:reactor_prt_in_rotor" />
<RecipeFor id="draconicevolution:reactor_prt_out_rotor" />
<RecipeFor id="draconicevolution:reactor_prt_rotor_full" />
<RecipeFor id="draconicevolution:reactor_prt_stab_frame" />
<RecipeFor id="reactor_injector" />
<RecipeFor id="reactor_stabilizer" />