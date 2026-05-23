---
navigation:
  parent: everything_else/everything_else.md
  title: Gates
  icon: draconicevolution:flux_gate
item_ids:
  - draconicevolution:fluid_gate
  - draconicevolution:flux_gate
---
<Column alignItems="center" fullWidth={true}>
    <Row>
        <BlockImage id="draconicevolution:fluid_gate" scale = "4"/>
        <BlockImage id="draconicevolution:flux_gate" scale = "4"/>
    </Row>
</Column>
# Gates

Gates are used to control the flow of power or fluid through a system. Simply attach a power or fluid source to the input side and the gate will allow you to control how quickly it flows through to the output side.

When you open the GUI, you will see two settings: "Redstone Signal High" and "Redstone Signal Low". these allow you to set the flow rate based on the given redstone signal.

The actual flow rate (seen in the bottom of the GUI) is determined by the strength of the redstone signal, interpolating between the two set values. A <ItemLink id="draconicevolution:potentiometer" /> could be quite useful.

# Computer Control
Flow gates can also be controlled using [CC: Tweaked](https://www.curseforge.com/minecraft/mc-mods/cc-tweaked). The avalible peripheral methods are:
- `getFlow`: Returns the current flow rate.
- `setOverrideEnabled`: Enables computer override mode.
- `getOverrideEnabled`: Returns true if override is enabled, disabling all manual control via the normal GUI.
- `setFlowOverride`: Sets the override value.
- `getSignalHighFlow`: Gets the RS High flow rate.
- `getSignalLowFlow`: Gets the RS Low flow rate. 
- `setSignalHighFlow`: Sets the RS High flow rate.
- `setSignalLowFlow`: Sets the RS Low flow rate.

# Recipes
<RecipeFor id="draconicevolution:fluid_gate" />
<RecipeFor id="draconicevolution:flux_gate" />