---
navigation:
  parent: power_generation_storage_transfer/power_generation_storage_transfer.md
  title: Energy Network
  icon: draconicevolution:draconic_relay_crystal
item_ids:
  - draconicevolution:basic_io_crystal
  - draconicevolution:basic_relay_crystal
  - draconicevolution:basic_wireless_crystal
  - draconicevolution:wyvern_io_crystal
  - draconicevolution:wyvern_relay_crystal
  - draconicevolution:wyvern_wireless_crystal
  - draconicevolution:draconic_io_crystal
  - draconicevolution:draconic_relay_crystal
  - draconicevolution:draconic_wireless_crystal
  - draconicevolution:crystal_binder
---

<GameScene zoom="2" interactive={true}>
    <ImportStructure src="./network.nbt"/>
    <IsometricCamera yaw="-45" pitch="30" />
</GameScene>

# Energy Network

Crystals are your go-to way for transferring Operation Potential, Forge Energy, or whatever sort of flux you may have on hand. To link Crystals together, **<KeyBind id="key.sneak" /> + <KeyBind id="key.use" />** with a <ItemLink id="draconicevolution:crystal_binder" /> to target a Crystal, then **<KeyBind id="key.use" />** another Crystal to bind the two together. While holding a <ItemLink id="draconicevolution:crystal_binder" />, you will see links between crystals shown in blue, and wireless links in red.

To clear the target crystal in a <ItemLink id="draconicevolution:crystal_binder" />, **<KeyBind id="key.sneak" /> + <KeyBind id="key.use" />** while looking at nothing.

Each energy crystal has an internal energy buffer. When two or more crystals are linked, they will attempt to balance their stored energy so they are all at the same charge level. Each crystal can only support a certain number of connections to other crystals, determined by the type and tier of the crystal:
|Tier|Range (blocks)|Capacity (OP)|I/O|Relay|Wireless
|---|---|---|---|---|---|
|§1Basic|32|4 Million|2|8|4 Links, 16 Wireless Links
|§5Wyvern|64|16 Million|3|16|8 Links, 32 Wireless Links
|§6Draconic|127|64 Million|4|32|16 Links, 64 Wireless Links

# Crystal Types

## <ItemImage id="draconicevolution:draconic_io_crystal" scale = "0.5"/> I/O Crystals
I/O Crystals are your primary form of transferring OP between the network and other blocks. **<KeyBind id="key.sneak" /> + <KeyBind id="key.use" />** to switch between inserting and extracting OP from the connected block.

## <ItemImage id="draconicevolution:draconic_relay_crystal" scale = "0.5"/> Relay Crystals
Relay Crystals cannot interact with other blocks. Instead, their high link count makes them useful as a point for other Crystals to bind to.

## <ItemImage id="draconicevolution:draconic_wireless_crystal" scale = "0.5"/> Wireless Crystals
Wireless Crystals have the unique ability to link to multiple blocks at range. **<KeyBind id="key.sneak" /> + <KeyBind id="key.use" />** to switch between inserting and extracting OP from the connected blocks.

> Note: Wireless Crystals bind to block *faces*, not just the blocks themselves!

# Recipes
<RecipeFor id="draconicevolution:crystal_binder" />
<Recipe id="draconicevolution:machines/basic_relay_crystal" />
<RecipeFor id="draconicevolution:basic_io_crystal" />
<RecipeFor id="draconicevolution:basic_wireless_crystal" />