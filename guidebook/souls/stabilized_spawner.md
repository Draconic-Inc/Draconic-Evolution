---
navigation:
  parent: souls/souls.md
  title: Stabilized Spawner
  icon: draconicevolution:stabilized_spawner
item_ids:
  - draconicevolution:stabilized_spawner
  - draconicevolution:draconium_core
  - draconicevolution:wyvern_core
  - draconicevolution:awakened_core
  - draconicevolution:chaotic_core
---
<BlockImage id="draconicevolution:stabilized_spawner" scale = "4"/>
# Stabilized Spawner

A <ItemLink id="draconicevolution:stabilized_spawner" /> by pressing **<KeyBind id="key.use" />** a <ItemLink id="minecraft:spawner" /> (or a Broken Spawner, if EnderIO is installed) while holding a core. By default, it will retain the soul that the original <ItemLink id="minecraft:spawner" /> had, but it can be changed with a [Mob Soul](./mob_soul.md).

> WARNING: Breaking a <ItemLink id="draconicevolution:stabilized_spawner" /> will cause it to lose its soul, requiring a new one when placed!

The core of a <ItemLink id="draconicevolution:stabilized_spawner" /> can be exchanged by pressing **<KeyBind id="key.use" />** while holding a core. Mobs are spawned in a four-block radius, and spawning can be disabled with a redstone signal.

The efficiency of a <ItemLink id="draconicevolution:stabilized_spawner" /> is determined by its core:

| Tier | Spawn Count | Minimum Delay | Maximum Delay | Player Nearby? | Spawn Requirements?
|---|---|---|---|---|---
|§1Basic|4|10|40|Yes|Yes
|§5Wyvern|6|5|20|No|Yes
|§6Draconic|8|2.5|10|No|No
|§4Chaotic|12|1.25|5|No|No