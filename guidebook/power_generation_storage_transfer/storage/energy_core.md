---
navigation:
  title: Energy Core
  parent: power_generation_storage_transfer/power_generation_storage_transfer.md
  icon: draconicevolution:energy_core
item_ids:
- draconicevolution:energy_core
- draconicevolution:energy_pylon
- draconicevolution:energy_core_stabilizer
---

# Energy Core

<GameScene zoom="1" interactive={true}>
    <ImportStructure src="./energy_core.nbt" />
    <IsometricCamera yaw="195" pitch="30" />

    <BoxAnnotation min="9 9 0" max="12 12 1">
An Advanced Energy Core Stabilizer, made with nine <ItemLink id="draconicevolution:energy_core_stabilizer" />*s*.
<BlockImage id="draconicevolution:energy_core_stabilizer" scale="2"/>
    </BoxAnnotation>
    <BoxAnnotation min="0 9 9" max="1 12 12">
An Advanced Energy Core Stabilizer, made with nine <ItemLink id="draconicevolution:energy_core_stabilizer" />*s*.
<BlockImage id="draconicevolution:energy_core_stabilizer" scale="2"/>
    </BoxAnnotation>
    <BoxAnnotation min="9 9 20" max="12 12 21">
An Advanced Energy Core Stabilizer, made with nine <ItemLink id="draconicevolution:energy_core_stabilizer" />*s*.
<BlockImage id="draconicevolution:energy_core_stabilizer" scale="2"/>
    </BoxAnnotation>
    <BoxAnnotation min="20 9 9" max="21 12 12">
An Advanced Energy Core Stabilizer, made with nine <ItemLink id="draconicevolution:energy_core_stabilizer" />*s*.
<BlockImage id="draconicevolution:energy_core_stabilizer" scale="2"/>
    </BoxAnnotation>

    <BoxAnnotation min="4 4 4" max="17 17 17">
An assortment of <ItemLink id="minecraft:redstone_block"/>, <ItemLink id="draconicevolution:draconium_block"/>, and/or <ItemLink id="draconicevolution:awakened_draconium_block"/>.
    <Row><BlockImage id="minecraft:redstone_block" scale="2"/><BlockImage id="draconicevolution:draconium_block" scale="2"/><BlockImage id="draconicevolution:awakened_draconium_block" scale="2"/></Row>
    </BoxAnnotation>

    <BlockAnnotation x="10" y="1" z="10">
<ItemLink id="minecraft:glass"/> placed on a <ItemLink id="draconicevolution:energy_pylon"/>. **<KeyBind id="key.use"/>** to toggle between inserting and extracting OP via the associated <ItemLink id="draconicevolution:energy_pylon"/>.
<BlockImage id="minecraft:glass" scale="2"/>
    </BlockAnnotation>
    <BlockAnnotation x="10" y="19" z="10">
<ItemLink id="minecraft:glass"/> placed on a <ItemLink id="draconicevolution:energy_pylon"/>. **<KeyBind id="key.use"/>** to toggle between inserting and extracting OP via the associated <ItemLink id="draconicevolution:energy_pylon"/>.
<BlockImage id="minecraft:glass" scale="2"/>
    </BlockAnnotation>

    <DiamondAnnotation pos="10.5 10.5 10.5" color="#ff00ff">
An <ItemLink id="draconicevolution:energy_core" />, the center of your Energy Core.
<BlockImage id="draconicevolution:energy_core" scale="2"/>
    </DiamondAnnotation>
</GameScene>

# Energy Cores

The ultimate energy storage device. Energy Cores come in multiple tiers, each with their own storage capacity (and price tag):

| Tier | Storage (OP)| Cost |
|---|---|---|
| 1 | 45.5 Million | 1x <ItemLink id="draconicevolution:energy_core" />, 4x <ItemLink id="draconicevolution:energy_core_stabilizer" />
| 2 | 273 Million | 1x <ItemLink id="draconicevolution:energy_core" />, 4x <ItemLink id="draconicevolution:energy_core_stabilizer" />, 6x <ItemLink id="draconicevolution:draconium_block" />
| 3 | 1.64 Billion | 1x <ItemLink id="draconicevolution:energy_core" />, 4x <ItemLink id="draconicevolution:energy_core_stabilizer" />, 26x <ItemLink id="draconicevolution:draconium_block" />
| 4 | 9,88 Billion | 1x <ItemLink id="draconicevolution:energy_core" />, 4x <ItemLink id="draconicevolution:energy_core_stabilizer" />, 54x <ItemLink id="draconicevolution:draconium_block" />, 26x <ItemLink id="minecraft:redstone_block" />
| 5 | 59.3 Billion | 1x <ItemLink id="draconicevolution:energy_core" />, 36x <ItemLink id="draconicevolution:energy_core_stabilizer" />, 90x <ItemLink id="draconicevolution:draconium_block" />, 80x <ItemLink id="minecraft:redstone_block" />
| 6 | 356 Billion | 1x <ItemLink id="draconicevolution:energy_core" />, 36x <ItemLink id="draconicevolution:energy_core_stabilizer" />, 150x <ItemLink id="draconicevolution:draconium_block" />, 178x <ItemLink id="minecraft:redstone_block" />
| 7 | 2.14 Tillion | 1x <ItemLink id="draconicevolution:energy_core" />, 36x <ItemLink id="draconicevolution:energy_core_stabilizer" />, 210x <ItemLink id="draconicevolution:draconium_block" />, 328x <ItemLink id="minecraft:redstone_block" />
| 8 | [Infinity](https://en.wikipedia.org/wiki/9223372036854775807) | 1x <ItemLink id="draconicevolution:energy_core" />, 36x <ItemLink id="draconicevolution:energy_core_stabilizer" />, 786x <ItemLink id="draconicevolution:draconium_block" />, 378x <ItemLink id="draconicevolution:awakened_draconium_block" />

# Energy Core Stabilizers

Energy Cores need four <ItemLink id="draconicevolution:energy_core_stabilizer" />*s*, which both must be within 16 blocks of the <ItemLink id="draconicevolution:energy_core" />, and aligned to the <ItemLink id="draconicevolution:energy_core" />. Energy Cores above Tier 4 need Advanced Energy Core Stabilizers, made out of nine <ItemLink id="draconicevolution:energy_core_stabilizer" />*s* each.

<GameScene zoom="2">
    <ImportStructure src="./advanced_stabilizer.nbt"/>
    <Block y="2" id="draconicevolution:energy_core_stabilizer" />
    <BlockAnnotation x="1" y="1">
The center <ItemLink id="draconicevolution:energy_core_stabilizer" /> needs to be aligned with the <ItemLink id="draconicevolution:energy_core" />.
    </BlockAnnotation>
</GameScene>

Advanced Energy Core Stabilizers can be placed in any orientation.

# Energy Pylons

<ItemLink id="draconicevolution:energy_pylon" />*s* are used to insert and extract energy from a core.
To set one up, simply place it within 16 blocks of a <ItemLink id="draconicevolution:energy_core" />, and place <ItemLink id="minecraft:glass" /> facing the <ItemLink id="draconicevolution:energy_core" />.

<GameScene zoom="2">
    <ImportStructure src="./energy_pylon.nbt"/>
    <Block y="1" id="minecraft:glass" />
</GameScene>

To check if the <ItemLink id="draconicevolution:energy_pylon" /> is connected to the <ItemLink id="draconicevolution:energy_core" />, simply **<KeyBind id="key.sneak"/> + <KeyBind id="key.use"/>** the <ItemLink id="draconicevolution:energy_pylon" />. It will create a line of particles that indicates which core it is connected to. If there is more than one <ItemLink id="draconicevolution:energy_core" /> nearby, this will make the <ItemLink id="draconicevolution:energy_pylon" /> cycle between the <ItemLink id="draconicevolution:energy_core" />*s* in range.

To toggle the <ItemLink id="draconicevolution:energy_pylon" /> between input and output mode, **<KeyBind id="key.use"/>** the <ItemLink id="minecraft:glass" />. 

# Recipes

<RecipeFor id="draconicevolution:energy_core" />
<RecipeFor id="draconicevolution:energy_core_stabilizer" />
<RecipeFor id="draconicevolution:energy_pylon" />