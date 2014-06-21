package com.brandon3055.draconicevolution.client.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.core.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.core.helper.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.core.network.TeleporterPacket;
import com.brandon3055.draconicevolution.common.core.network.TeleporterStringPacket;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;

@SideOnly(Side.CLIENT)
public class GUITeleporter extends GuiScreen
{
	private final int xSize = 160;
	private final int ySize = 138;
	private ResourceLocation guiTexture = new ResourceLocation(References.MODID.toLowerCase(), "textures/gui/TeleporterMKII.png");
	private ItemStack teleporterItem;
	private double[] dPosX = { 0, 0, 0, 0, 0 };
	private double[] dPosY = { 0, 0, 0, 0, 0 };
	private double[] dPosZ = { 0, 0, 0, 0, 0 };
	private int[] dPosD = { 0, 0, 0, 0, 0 };
	private float[] dYaw = { 0, 0, 0, 0, 0 };
	private float[] dPitch = { 0, 0, 0, 0, 0 };
	private boolean[] dSet = { false, false, false, false, false };
	private int selected = 0;
	private int fuel = 0;
	private boolean editing = false;

	private EntityPlayer player;

	public GUITeleporter(EntityPlayer player) {
		super();
		this.player = player;
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().isItemEqual(new ItemStack(ModItems.teleporterMKII)))
		{
			this.teleporterItem = player.getCurrentEquippedItem();
			updatePositions(teleporterItem);
			updateNames(teleporterItem);
		}
	}

	String[] destText = { "", "", "", "", "" };
	String[] destValue = { "[Unset]", "[Unset]", "[Unset]", "[Unset]", "[Unset]" };

	@Override
	public void drawScreen(int x, int y, float f)
	{
		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(guiTexture);
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;
		drawTexturedModalRect(posX, posY, 0, 0, xSize, ySize);
		drawTexturedModalRect(posX + 3, posY + 3 + selected * 23, 0, ySize, 67, 10);
		
		//Light Cursor Position
		for (int i = 0; i < 5; i++)
		{
			if ((x > posX + 2 && x < posX + 70) && (y > posY + 2 + i * 23 && y < posY + 14 + i * 23))
			{
				if (!editing)
					drawTexturedModalRect(posX + 3, posY + 3 + i * 23, 0, ySize, 67, 10);
				
				if (i == selected && !editing)
				{
					drawTexturedModalRect(posX, posY - 10, 0, ySize + 10, 30, 10);
					drawTexturedModalRect(posX + 15, posY - 10, 40, ySize + 10, 30, 10);
					fontRendererObj.drawString("Rename", posX + 3, posY - 9, 0xffffff, false);
				}else if (!editing
						)
				{
					drawTexturedModalRect(posX, posY - 10, 0, ySize + 10, 30, 10);
					drawTexturedModalRect(posX + 10, posY - 10, 40, ySize + 10, 30, 10);
					fontRendererObj.drawString("Select", posX + 3, posY - 9, 0xffffff, false);
				}
					
	
			}
		}
		
		if (editing)
		{
			drawTexturedModalRect(posX, posY - 10, 0, ySize + 10, 60, 10);
			drawTexturedModalRect(posX + 60, posY - 10, 18, ySize + 10, 60, 10);
			fontRendererObj.drawString("Press Enter to save", posX + 3, posY - 9, 0xffffff, false);
		}

		//Draw Destination data
		for (int i = 0; i < 5; i++)
		{
			if (dSet[i])
			{
				destValue[i] = "X:" + (int) dPosX[0] + " Y:" + (int) dPosY[0] + " Z:" + (int) dPosZ[0];// + " Dimension:" + world.provider.getProviderForDimension(dPosD[i]).getDimensionName() + "";
			}

			if (editing && i == selected)
				fontRendererObj.drawString(EnumChatFormatting.UNDERLINE + destText[i], posX + 5, posY + 5 + (i * 23), 0xff0000);
			else
				fontRendererObj.drawString(destText[i], posX + 5, posY + 5 + (i * 23), 0x000000);
			fontRendererObj.drawString((dSet[i] ? (EnumChatFormatting.DARK_BLUE + WorldProvider.getProviderForDimension(dPosD[i]).getDimensionName()) : EnumChatFormatting.DARK_RED + "[Unset]"), posX + 73, posY + 5 + (i * 23), 0x000000);
			fontRendererObj.drawString("X:" + String.valueOf((int) dPosX[i]), posX + 4, posY + 16 + (i * 23), 0x00b000);
			fontRendererObj.drawString("Y:" + String.valueOf((int) dPosY[i]), posX + 50, posY + 16 + (i * 23), 0x00b000);
			fontRendererObj.drawString("Z:" + String.valueOf((int) dPosZ[i]), posX + 96, posY + 16 + (i * 23), 0x00b000);
		}

		fontRendererObj.drawString("Charges: " + fuel, posX + 5, posY + 120, 0x000000);
		
		super.drawScreen(x, y, f);
		//initGui();
		if (player.getCurrentEquippedItem() == null || !player.getCurrentEquippedItem().isItemEqual(new ItemStack(ModItems.teleporterMKII)) || player.isDead)
		{
			this.mc.displayGuiScreen(null);
			this.mc.setIngameFocus();
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;

		for (int i = 0; i < 5; i++)
		{
			if ((x > posX + 2 && x < posX + 70) && (y > posY + 2 + i * 23 && y < posY + 14 + i * 23))
			{
				if (selected != i && !editing)
				{
					selected = i;
					if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().isItemEqual(new ItemStack(ModItems.teleporterMKII)))
						ItemNBTHelper.setShort(this.player.getCurrentEquippedItem(), "Selection", (short) selected);
					DraconicEvolution.channelHandler.sendToServer(new TeleporterPacket((byte) 0, selected));
					initGui();
				}else if (!editing)
				{
					editing = true;
				}
			}
		}

		super.mouseClicked(x, y, button);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		buttonList.clear();
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;

		updateTeleporter();

		buttonList.add(new GuiButton(selected, posX - 25, posY + 3 + selected * 23, 25, 20, "Set"));
		buttonList.add(new GuiButton(5, posX + 109, posY + 117, 50, 20, "Add Fuel"));
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		super.actionPerformed(button);
		
		for (int i = 0; i < 5; i++)
		{
			if (i == button.id)
			{
				double playerX = player.posX;
				double playerY = player.posY - 1.61;
				double playerZ = player.posZ;
				float yaw = player.rotationYaw;
				float pitch = player.rotationPitch;
				int dim = player.dimension;
				
				dPosX[i] = playerX;
				dPosY[i] = playerY;
				dPosZ[i] = playerZ;
				dPosD[i] = dim;
				dSet[i] = true;
				
				DraconicEvolution.channelHandler.sendToServer(new TeleporterPacket((byte) 1, i, dim, playerX, playerY, playerZ, yaw, pitch));
				
				
			}
		}

		if (button.id == 5)
		{
			if (player.inventory.hasItem(Items.ender_pearl))
			{
				if ((!Keyboard.isKeyDown(42)) && (!Keyboard.isKeyDown(54)))
				{
					DraconicEvolution.channelHandler.sendToServer(new TeleporterPacket((byte) 6, 1));
					this.fuel += ConfigHandler.teleporterUsesPerPearl;
				}else if (hasPearls(16))
				{
					DraconicEvolution.channelHandler.sendToServer(new TeleporterPacket((byte) 6, 16));
					this.fuel += ConfigHandler.teleporterUsesPerPearl * 16;
				}else
					player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfPearls.txt"));
			}
			else
				player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfPearls.txt"));
		}
	}
	
	public boolean hasPearls(int number)
	{
		int found = 0;
		ItemStack stack;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			stack = player.inventory.getStackInSlot(i);
			if (stack != null && stack.isItemEqual(new ItemStack(Items.ender_pearl)))
				found += stack.stackSize;
			
			if (found >= number)
				return true;
		}
		return false;
	}

	@Override
	public void keyTyped(char key, int keyN)
	{
		if ((key == 'e' && !editing) || key == '')
		{
			this.mc.displayGuiScreen(null);
			this.mc.setIngameFocus();
		}
		
		if (editing)
		{
			String text = destText[selected];
			if (keyN == 14)
				if (text.length() > 0)
					destText[selected] = text.substring(0, text.length() - 1);
				else
					return;
			else if (keyN == 28)
			{
				editing = false;
				DraconicEvolution.channelHandler.sendToServer(new TeleporterStringPacket(selected, destText[selected]));
			}
			else if (keyN == 42)
				return;
			else if (destText[selected].length() < 13)
				destText[selected] += key;
			
			initGui();
		}
		
		
	}

	@Override
	public void updateScreen()
	{
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	private void updatePositions(ItemStack teleporter)
	{
		for (int i = 0; i < 5; i++)
		{
			this.dPosX[i] = ItemNBTHelper.getDouble(teleporter, "X_" + i, -1);
		}
		for (int i = 0; i < 5; i++)
		{
			this.dPosY[i] = ItemNBTHelper.getDouble(teleporter, "Y_" + i, -1);
		}
		for (int i = 0; i < 5; i++)
		{
			this.dPosZ[i] = ItemNBTHelper.getDouble(teleporter, "Z_" + i, -1);
		}
		for (int i = 0; i < 5; i++)
		{
			this.dPosD[i] = ItemNBTHelper.getIntager(teleporter, "Dimension_" + i, 0);
		}
		for (int i = 0; i < 5; i++)
		{
			this.dYaw[i] = ItemNBTHelper.getFloat(teleporter, "Yaw_" + i, 0);
		}
		for (int i = 0; i < 5; i++)
		{
			this.dPitch[i] = ItemNBTHelper.getFloat(teleporter, "Pitch_" + i, 0);
		}
		for (int i = 0; i < 5; i++)
		{
			this.dSet[i] = ItemNBTHelper.getBoolean(teleporter, "IsSet_" + i, false);
		}
		this.selected = ItemNBTHelper.getShort(teleporter, "Selection", (short) 0);
		this.fuel = ItemNBTHelper.getIntager(teleporter, "Fuel", 0);
	}
	
	public void updateNames(ItemStack teleporter)
	{
		for (int i = 0; i < 5; i++)
		{
			this.destText[i] = ItemNBTHelper.getString(teleporter, "Dest_" + i, "Destination " + i);
		}
	}
	
	public void updateTeleporter()
	{
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().isItemEqual(new ItemStack(ModItems.teleporterMKII)))
		{
			this.teleporterItem = player.getCurrentEquippedItem();
			updatePositions(teleporterItem);
		}
	}

}
