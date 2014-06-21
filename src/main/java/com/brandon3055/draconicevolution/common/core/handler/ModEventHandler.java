package draconicevolution.common.core.handler;


public class ModEventHandler {

	/*
	//Allow Flight when holding item
	@SubscribeEvent
	public void onLivingUpdateEvent(LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			ItemStack heldItem = player.getHeldItem();
			if (heldItem != null && heldItem.isItemEqual(new ItemStack(Items.arrow))) {
				System.out.println("True");
				player.capabilities.allowFlying = true;
			} else {
				player.capabilities.allowFlying = player.capabilities.isCreativeMode ? true : false;
			}
		}
	}*/ 
	
	
	
	/*
	@SubscribeEvent
	public void removedeathemesage(ClientChatReceivedEvent event)
	{
		String player = event.message.getUnformattedText().substring(0, event.message.getUnformattedText().indexOf(" ") + 1);
		System.out.println(player);
		
		if(event.message.getUnformattedText().contains(player + " fell out of the world"))
		{
			event.setCanceled(true);
		}
		if(event.message.getUnformattedText().contains(player + " was slain by"))
		{
			event.setCanceled(true);
		}
		if(event.message.getUnformattedText().contains(player + "was pricked to death"))
		{
			event.setCanceled(true);
		}
		if(event.message.getUnformattedText().contains(player + "drowned"))
		{
			event.setCanceled(true);
		}
		if(event.message.getUnformattedText().contains(player + "blew up"))
		{
			event.setCanceled(true);
		}
		if(event.message.getUnformattedText().contains(player + "was blown up"))
		{
			event.setCanceled(true);
		}
		if(event.message.getUnformattedText().contains(player + "was doomed to fall"))
		{
			event.setCanceled(true);
		}
		if(event.message.getUnformattedText().contains(player + "was shot"))
		{
			event.setCanceled(true);
		}
		//Etc
	}*/
	
	
	
}
