---
navigation:
  parent: power_generation_storage_transfer/power_generation_storage_transfer.md
  title: Energy Transfuser
  icon: draconicevolution:energy_transfuser
item_ids:
  - draconicevolution:energy_transfuser
---

<BlockImage id="draconicevolution:energy_transfuser" scale = "4"/>
# Disenchanter

This device is capable of charging four items at up to 1 million OP/tick. It can be toggled between charging the first item, and all items equally.

> Note: While the <ItemLink id="draconicevolution:energy_transfuser"/> can charge up to 1 million OP/tick, the effective charging rate is limited by the transfer rate of each item.

Each slot can switch between four modes:
- Charge: Charges the item, taking energy from surrounding blocks and Discharge/Buffer slots. Items can be extracted from these slots when fully charged, such as with a <ItemLink id="minecraft:hopper"/>.
- Discharge: Discharges the item, sending the energy to surrounding blocks and Charge/Buffer slots. Items can be extracted from these slots when fully discharged.
- Buffer: Allows both sending and taking energy from surounding blocks and slots. Items cannot be extracted from these slots.
- Disabled: Prevents charging, discharging, and item extraction.

# Recipes
<RecipeFor id="draconicevolution:energy_transfuser" />