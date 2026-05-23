---
navigation:
  parent: everything_else/everything_else.md
  title: Entity Detectors
  icon: draconicevolution:entity_detector
item_ids:
  - draconicevolution:entity_detector
  - draconicevolution:entity_detector_advanced
---
<Column alignItems="center" fullWidth={true}>
    <Row>
        <BlockImage id="draconicevolution:entity_detector" scale = "4"/>
        <BlockImage id="draconicevolution:entity_detector_advanced" scale = "4"/>
    </Row>
</Column>
# Entity Detectors

These detectors are capable of searching for specific entity types and emitting a adjustable redstone signal based on what they find. They send out scanning pulses at a configurable frequency (max 1200 ticks, or 1 minute), and then update their redstone output. Their minimum and maximum entity count can also be configured, with a minimum of 0 entities, and a maximum of 127.

The <ItemLink id="draconicevolution:entity_detector" /> has a max range of 16 blocks, and a minimum frequency of 30 ticks, or 1.5 seconds. It can only detect players, hostile mobs, and non-hostile mobs.
The <ItemLink id="draconicevolution:entity_detector_advanced" /> has a max range of 64 blocks, and a minimum frequency of 5 ticks, or 0.25 seconds. It can detect specific players, hostile mobs, non-hostile mobs, tamed mobs, by entity type, dropped items, and subgroups.

# Recipes
<RecipeFor id="draconicevolution:entity_detector" />
<RecipeFor id="draconicevolution:entity_detector_advanced" />