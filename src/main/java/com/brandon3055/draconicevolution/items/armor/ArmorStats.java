package com.brandon3055.draconicevolution.items.armor;


import com.brandon3055.brandonscore.registry.ModConfigContainer;
import com.brandon3055.brandonscore.registry.ModConfigProperty;
import com.brandon3055.draconicevolution.DraconicEvolution;

/**
 * Created by FoxMcloud5655 on 11/02/2019.
 * This class holds all of the base stats for the armor sets.
 */
@ModConfigContainer(modid = DraconicEvolution.MODID)
public class ArmorStats {
	
	//Wyvern Armor
    
    @ModConfigProperty(category = "Armor Stat Tweaks", name = "wyvernBaseShieldCapacity", comment = "Allows you to adjust the total shield capacity of a full set of Wyvern Armor.", autoSync = true)
    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int wyvernBaseShieldCapacity = 256;
	
    @ModConfigProperty(category = "Armor Stat Tweaks", name = "wyvernShieldRechargeCost", comment = "Allows you to adjust the amount of RF that Wyvern Armor requires to recharge 1 shield point.", autoSync = true)
    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int wyvernShieldRechargeCost = 1000;
    
    @ModConfigProperty(category = "Armor Stat Tweaks", name = "wyvernShieldRecovery", comment = "Allows you to adjust how fast Wyvern Armor is able to recover entropy.  Value is {this number}% every 5 seconds.", autoSync = true)
    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static double wyvernShieldRecovery = 2D;
    
    @ModConfigProperty(category = "Armor Stat Tweaks", name = "wyvernMaxRecieve", comment = "Allows you to adjust how fast Wyvern Armor is able to recieve RF/tick.", autoSync = true)
    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int wyvernMaxRecieve = 512000;
    
    //Draconic Armor
    
    @ModConfigProperty(category = "Armor Stat Tweaks", name = "draconicBaseShieldCapacity", comment = "Allows you to adjust the total shield capacity of a full set of Draconic Armor.", autoSync = true)
    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int draconicBaseShieldCapacity = 512;
    
    @ModConfigProperty(category = "Armor Stat Tweaks", name = "draconicShieldRechargeCost", comment = "Allows you to adjust the amount of RF that Draconic Armor requires to recharge 1 shield point.", autoSync = true)
    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int draconicShieldRechargeCost = 1000;
    
    @ModConfigProperty(category = "Armor Stat Tweaks", name = "draconicShieldRecovery", comment = "Allows you to adjust how fast Draconic Armor is able to recover entropy.  Value is {this number}% every 5 seconds.", autoSync = true)
    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static double draconicShieldRecovery = 4D;
    
    @ModConfigProperty(category = "Armor Stat Tweaks", name = "draconicMaxRecieve", comment = "Allows you to adjust how fast Draconic Armor is able to recieve RF/tick.", autoSync = true)
    @ModConfigProperty.MinMax(min = "0", max = "2147483647")
    public static int draconicMaxRecieve = 1000000;
}
