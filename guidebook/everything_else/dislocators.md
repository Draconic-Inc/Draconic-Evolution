---
navigation:
  parent: everything_else/everything_else.md
  title: Dislocators
  icon: draconicevolution:dislocator
item_ids:
  - draconicevolution:dislocator
  - draconicevolution:advanced_dislocator
  - draconicevolution:p2p_dislocator
  - draconicevolution:p2p_dislocator_unbound
  - draconicevolution:player_dislocator
  - draconicevolution:player_dislocator_unbound
  - draconicevolution:dislocator_receptacle
  - draconicevolution:infused_obsidian
  - draconicevolution:dislocator_pedestal
---
# Dislocators

Dislocators teleport you (between dimensions!) to its destination. **<KeyBind id="key.sneak" /> + <KeyBind id="key.use" />** with a Dislocator to set its destination, then **<KeyBind id="key.use" />** to teleport. 

## <ItemImage id="draconicevolution:dislocator" scale = "0.5"/> <ItemLink id="draconicevolution:dislocator" />
Teleports you to a position. Can only be used 32 times before breaking.

## <ItemImage id="draconicevolution:player_dislocator_unbound" scale = "0.5"/> <ItemLink id="draconicevolution:player_dislocator_unbound" />
Teleports you to a player. Can only be used if that player is online.

## <ItemImage id="draconicevolution:p2p_dislocator_unbound" scale = "0.5"/> <ItemLink id="draconicevolution:p2p_dislocator_unbound" />
Teleports you to the other Dislocator. Only works if the other Dislocator is in a <ItemLink id="draconicevolution:dislocator_pedestal" /> or <ItemLink id="draconicevolution:dislocator_receptacle" />.

## <ItemImage id="draconicevolution:advanced_dislocator" scale = "0.5"/> <ItemLink id="draconicevolution:advanced_dislocator" />
Teleports you to a position. Can store up to 100 locations, but uses <ItemLink id="minecraft:ender_pearl" />*s* as fuel.

In the GUI, you can set new positions, update positions to your current location, switch between Teleport and Blink mode, and refuel with <ItemLink id="minecraft:ender_pearl" />*s* in your inventory. **<KeyBind id="key.sneak" /> + Scroll Wheel** to change the active destination.

# Pedestals
<BlockImage id="draconicevolution:dislocator_pedestal" scale="2"/>
Stores Dislocators for later access. **<KeyBind id="key.use" />** with a Dislocator to insert it, and **<KeyBind id="key.sneak" /> + <KeyBind id="key.use" />** to extract it. **<KeyBind id="key.use" />** to activte the Dislocator. Named Dislocators will show their name above the Pedestal.

> For the <ItemLink id="draconicevolution:advanced_dislocator" />, the active destination will be used, and will instead show the name of their active destination.

# Portals

<GameScene zoom="2">
    <ImportStructure src="./portals.nbt"/>
</GameScene>

Portals are made of two components: A <ItemLink id="draconicevolution:dislocator_receptacle" /> to hold the Dislocator, and a frame of <ItemLink id="draconicevolution:infused_obsidian" /> to house the portal. **<KeyBind id="key.use" />** to insert or extract a Dislocator. The <ItemLink id="draconicevolution:dislocator_receptacle" /> can be automated, such as with a <ItemLink id="minecraft:hopper" />.

# Recipes
<RecipeFor id="draconicevolution:dislocator" />
<RecipeFor id="draconicevolution:advanced_dislocator" />
<RecipeFor id="draconicevolution:p2p_dislocator_unbound" />
<RecipeFor id="draconicevolution:player_dislocator_unbound" />
<RecipeFor id="draconicevolution:dislocator_receptacle" />
<RecipeFor id="draconicevolution:infused_obsidian" />
<RecipeFor id="draconicevolution:dislocator_pedestal" />