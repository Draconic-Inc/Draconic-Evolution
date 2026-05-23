---
navigation:
  title: Fusion Crafting
  icon: draconicevolution:crafting_core
  position: 40
item_ids:
- draconicevolution:crafting_core
- draconicevolution:basic_crafting_injector
- draconicevolution:wyvern_crafting_injector
- draconicevolution:awakened_crafting_injector
- draconicevolution:chaotic_crafting_injector
---

# Fusion Crafting

<GameScene zoom="2" interactive={true}>
    <ImportStructure src="./fusion_crafting.nbt" />
    <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Fusion Crafting is an important mechanic in Draconic Evolution, granting access to [modular tools](./equipment/modular_tools.md), <ItemLink id="draconicevolution:awakened_draconium_block" />, and more.

Injectors are placed facing the <ItemLink id="draconicevolution:crafting_core" />, up to 16 blocks away, and up to 1 block off-axis. **<KeyBind id="key.sneak"/> + <KeyBind id="key.use"/>** an injector to switch between storing a stack of items and a single item.

**Injectors need line-of-sight in order to be recognized by a <ItemLink id="draconicevolution:crafting_core" />!**

# Crafting

Items can be inserted or removed from injectors pressing **<KeyBind id="key.use"/>** with an item in-hand.
Fusion Crafting recipes require OP to craft, which can be inserted into injectors.
Each Fusion Crafting recipe requires a certain injector tier. If you do not use injectors that are at least the tier required, your craft will not begin.

Once a valid recipe is found, start a craft by clicking on the button in the GUI.


# Automation

Items can be inserted and extracted from injectors and the <ItemLink id="draconicevolution:crafting_core" />, such as with a <ItemLink id="minecraft:hopper" />.

<ItemLink id="draconicevolution:crafting_core" />*s* can also be activated with a redstone signal.

A <ItemLink id="minecraft:comparator" /> attached to the <ItemLink id="draconicevolution:crafting_core" /> will output the following values:
- 0: No valid recipe
- 1: Valid recipe
- 2-14: Crafting in progress
- 15: Item in output slot

# Recipes

<RecipeFor id="draconicevolution:crafting_core" />
<RecipeFor id="draconicevolution:basic_crafting_injector" />
<RecipeFor id="draconicevolution:wyvern_crafting_injector" />
<RecipeFor id="draconicevolution:awakened_crafting_injector" />
<RecipeFor id="draconicevolution:chaotic_crafting_injector" />