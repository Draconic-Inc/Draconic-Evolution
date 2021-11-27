package com.brandon3055.draconicevolution.integration.crafttweaker.expands;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import com.brandon3055.brandonscore.api.TechLevel;

@ZenRegister
@NativeTypeRegistration(value = TechLevel.class, zenCodeName = "mods.draconicevolution.TechLevel")
public class ExpandTechLevel {
    // CraftTweaker registers all the enum constants for us
}
