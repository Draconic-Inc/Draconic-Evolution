package com.brandon3055.draconicevolution.integration.computers;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluxGate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ComputerCraftCompatEventHandler {
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
    	if (event.getObject() instanceof TileBCore) {
    		TileBCore tile = (TileBCore)event.getObject();
	    	if (tile instanceof TileReactorComponent) {
	    		PeripheralReactorComponent peripheral = new PeripheralReactorComponent((TileReactorComponent)tile);
	            event.addCapability(new ResourceLocation(DraconicEvolution.MODID, peripheral.getType()), peripheral);
	            event.addListener(peripheral::invalidate);
	        }
	    	else if (tile instanceof TileEnergyPylon) {
	    		TileEnergyPylon tileEntity = (TileEnergyPylon)tile;
	    		PeripheralEnergyPylon peripheral = new PeripheralEnergyPylon((TileEnergyPylon)tile);
	            event.addCapability(new ResourceLocation(DraconicEvolution.MODID, peripheral.getType()), peripheral);
	            event.addListener(peripheral::invalidate);
	        }
	    	else if (tile instanceof TileFlowGate) {
	    		TileFlowGate tileEntity = (TileFlowGate)tile;
	    		PeripheralFlowGate peripheral = new PeripheralFlowGate((TileFlowGate)tile);
	            event.addCapability(new ResourceLocation(DraconicEvolution.MODID, peripheral.getType()), peripheral);
	            event.addListener(peripheral::invalidate);
	        }
	    	else if (tile instanceof TileFluidGate) {
	    		TileFluidGate tileEntity = (TileFluidGate)tile;
	    		PeripheralFluidGate peripheral = new PeripheralFluidGate((TileFluidGate)tile);
	            event.addCapability(new ResourceLocation(DraconicEvolution.MODID, peripheral.getType()), peripheral);
	            event.addListener(peripheral::invalidate);
	        }
	    	else if (tile instanceof TileFluxGate) {
	    		TileFluxGate tileEntity = (TileFluxGate)tile;
	    		PeripheralFluxGate peripheral = new PeripheralFluxGate((TileFluxGate)tile);
	            event.addCapability(new ResourceLocation(DraconicEvolution.MODID, peripheral.getType()), peripheral);
	            event.addListener(peripheral::invalidate);
	        }
	    }
    }
}
