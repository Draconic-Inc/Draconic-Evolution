package com.brandon3055.draconicevolution.client.interfaces;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.interfaces.manual.GuiButtonAHeight;
import com.brandon3055.draconicevolution.common.core.Teleporter.Teleporter.TeleportLocation;
import com.brandon3055.draconicevolution.common.core.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.core.network.TeleporterPacket;
import com.brandon3055.draconicevolution.common.core.utills.GuiHelper;
import com.brandon3055.draconicevolution.common.core.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GUITeleporter extends GuiScreen
{
	private final int xSize = 182;
	private final int ySize = 141;
	private ResourceLocation guiTexture = new ResourceLocation(References.MODID.toLowerCase(), "textures/gui/TeleporterMKII.png");
	private ItemStack teleporterItem;
	protected List<TeleportLocation> locations = new ArrayList<TeleportLocation>(0);

	private int selected = 0;
	private int selrctionOffset = 0;
	private int maxOffset = 0;
	private int fuel = 0;
	private boolean editingExisting = false;
	private boolean editingNew = false;
	private boolean showFuelLight = true;
	private int tick = 0;
	private GuiTextField textBeingEdited;

	private EntityPlayer player;

	public GUITeleporter(EntityPlayer player) {
		super();
		this.player = player;
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().isItemEqual(new ItemStack(ModItems.teleporterMKII)))
		{
			this.teleporterItem = player.getCurrentEquippedItem();
			readDataFromItem(teleporterItem);
		}
	}

	@Override
	public void drawScreen(int x, int y, float f){
		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(guiTexture);
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;
		drawTexturedModalRect(posX, posY, 0, 0, xSize, ySize);

		if (fuel <= 5) drawTexturedModalRect(posX+169, posY+86, 40, 150, 7, 7);
		if (fuel <= 5 && showFuelLight || (fuel > 5 && fuel < 10)) drawTexturedModalRect(posX+169, posY+86, 40, 143, 7, 7);

		drawArrows(x-posX, y-posY);

		drawLocations(x-posX, y-posY);

		drawSelectionInfo();

		textBeingEdited.drawTextBox();

		String colour = EnumChatFormatting.GREEN + "";
		if (fuel < 10) colour = EnumChatFormatting.YELLOW + "";
		if (fuel == 0) colour = EnumChatFormatting.DARK_RED + "";
		fontRendererObj.drawString(colour+"Fuel: " + fuel, posX + 115, posY + 87, 0x000000);

		super.drawScreen(x, y, f);
	}

	private void drawLocations(int x, int y){
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;

		for (int i = 0; i < Math.min(12, locations.size()); i++){
			if (GuiHelper.isInRect(17, 6+i*11, 80, 10, x, y)) drawTexturedModalRect(posX+19, posY+5+i*11, 0, 188, 80, 10);

			if (locations.get(i+selrctionOffset).getWriteProtected()){
				if (GuiHelper.isInRect(102, 7+i*11, 6, 6, x, y)) drawTexturedModalRect(posX+102, posY+7+i*11, 26, 149, 6, 6);
				else drawTexturedModalRect(posX+102, posY+7+i*11, 26, 143, 6, 6);
			}else{
				if (GuiHelper.isInRect(101, 7+i*11, 8, 7, x, y)) drawTexturedModalRect(posX+101, posY+7+i*11, 32, 150, 8, 7);
				else drawTexturedModalRect(posX+101, posY+7+i*11, 32, 143, 8, 7);
			}
		}

		drawTexturedModalRect(posX+19, posY+5+selected*11, 0, 188, 80, 10);

		int yl = 0;
		for (int i = selrctionOffset; i < locations.size() && i < selrctionOffset + 12; i++){
			String s = locations.get(i).getName();
			if (fontRendererObj.getStringWidth(s) > 80) {
				int safety = 0;
				while (fontRendererObj.getStringWidth(s) > 70){
					s = s.substring(0, s.length()-1);
					safety++;
					if (safety > 200) break;
				}
				s = s+"...";
			}
			fontRendererObj.drawString(s, posX + 21, posY + 7 + (yl * 11), 0x000000);
			yl++;
		}
	}

	private void drawArrows(int x, int y){
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;

		if (selrctionOffset > 0){
			boolean highLighted = GuiHelper.isInRect(4, 4, 13, 15, x, y);
			if (highLighted) drawTexturedModalRect(posX+4, posY+4, 0, 158, 13, 15);
			else drawTexturedModalRect(posX+4, posY+4, 0, 143, 13, 15);
		}else drawTexturedModalRect(posX+4, posY+4, 0, 173, 13, 15);
		if (selrctionOffset < maxOffset){
			boolean highLighted = GuiHelper.isInRect(4, 122, 13, 15, x, y);
			if (highLighted) drawTexturedModalRect(posX+4, posY+122, 13, 158, 13, 15);
			else drawTexturedModalRect(posX+4, posY+122, 13, 143, 13, 15);
		} else drawTexturedModalRect(posX+4, posY+122, 13, 173, 13, 15);

		float percent = locations.size() <= 12 ? 1f : 12F / (float)locations.size();
		int drawSize = (int)(percent * 99F);
		int space = 99 - drawSize;
		float location = (float)selrctionOffset / (float)(locations.size()-12);
		int yOffset = (int) (location * (float)space);
		drawTexturedModalRect(posX+5, posY+21+yOffset, 182, 0, 11, drawSize);
		drawTexturedModalRect(posX+5, posY+21+drawSize-1+yOffset, 182, 98, 11, 1);
	}

	private void drawSelectionInfo(){
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;
		if (locations.size() <= 0) return;
		fontRendererObj.drawString(EnumChatFormatting.GOLD+"X: "+(int)locations.get(selected+selrctionOffset).getXCoord(), posX+114, posY+7, 0x000000);
		fontRendererObj.drawString(EnumChatFormatting.GOLD + "Y: " + (int) locations.get(selected + selrctionOffset).getYCoord(), posX + 114, posY + 16, 0x000000);
		fontRendererObj.drawString(EnumChatFormatting.GOLD + "Z: " + (int) locations.get(selected + selrctionOffset).getZCoord(), posX + 114, posY + 25, 0x000000);
		fontRendererObj.drawString(EnumChatFormatting.GOLD+""+WorldProvider.getProviderForDimension(locations.get(selected + selrctionOffset).getDimension()).getDimensionName(), posX+114, posY+34, 0x000000);
	}

	@Override
	protected void mouseClicked(int x, int y, int button){
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;

		boolean offsetChanged = false;
		boolean selectionChanged = false;

		if (textBeingEdited.getVisible()) textBeingEdited.mouseClicked(x, y, button);

		//Check for arrow click
		if (selrctionOffset > 0 && GuiHelper.isInRect(3, 5, 13, 15, x-posX, y-posY)){
			selrctionOffset--;
			offsetChanged = true;
			if (selected < 11) {
				selected++;
				selectionChanged = true;
			}
		}
		if (selrctionOffset < maxOffset && GuiHelper.isInRect(3, 123, 13, 15, x-posX, y-posY)){
			selrctionOffset++;
			offsetChanged = true;
			if (selected > 0) {
				selected--;
				selectionChanged = true;
			}
		}

		//Check for location or lock clicked
		for (int i = 0; i < Math.min(12, locations.size()); i++){
			if (GuiHelper.isInRect(17, 6+i*11, 80, 10, x-posX, y-posY)) {
				if (!(locations.get(i+selrctionOffset).getWriteProtected() && editingExisting)) {
					selected = i;
					selectionChanged = true;
				}
			}

			if (GuiHelper.isInRect(99, 8+i*11, 8, 7, x-posX, y-posY)) {
				locations.get(i+selrctionOffset).setWriteProtected(!locations.get(i+selrctionOffset).getWriteProtected());
				DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.UPDATELOCK, i+selrctionOffset, locations.get(i+selrctionOffset).getWriteProtected()));
			}
		}


		if (selectionChanged) DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.CHANGESELECTION, selected, false));
		if (offsetChanged) DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.UPDATEOFFSET, selrctionOffset, false));

		updateButtons();
		super.mouseClicked(x, y, button);
	}

	@Override
	protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_) {
		super.mouseMovedOrUp(p_146286_1_, p_146286_2_, p_146286_3_);
	}

	@Override
	public void handleMouseInput() {
		int i = org.lwjgl.input.Mouse.getEventDWheel();
		boolean offsetChanged = false;
		boolean selectionChanged = false;
		if (i < 0 && selrctionOffset < maxOffset) {
			selrctionOffset++;
			offsetChanged = true;
			if (selected > 0) {
				selected--;
				selectionChanged = true;
			}
			updateButtons();
		}
		if (i > 0 && selrctionOffset > 0) {
			selrctionOffset--;
			offsetChanged = true;
			if (selected < 11) {
				selected++;
				selectionChanged = true;
			}
			updateButtons();
		}

		if (selectionChanged) DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.CHANGESELECTION, selected, false));
		if (offsetChanged) DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.UPDATEOFFSET, selrctionOffset, false));

		super.handleMouseInput();
	}

	private  void updateButtons(){
		if (locations.size() > 12) maxOffset = locations.size() - 12;
		else maxOffset = 0;
		if (selrctionOffset > maxOffset) selrctionOffset = maxOffset;
		if (selected > locations.size() || selected < 0) selected = Math.max(locations.size() - 1, 0);
		if (locations.size() == 0 || locations.get(selected + selrctionOffset).getWriteProtected()) {
			((GuiButton) buttonList.get(0)).enabled = false;
			((GuiButton) buttonList.get(1)).enabled = false;
			((GuiButton) buttonList.get(2)).enabled = false;
		} else {
			((GuiButton) buttonList.get(0)).enabled = true;
			((GuiButton) buttonList.get(1)).enabled = true;
			((GuiButton) buttonList.get(2)).enabled = true;
		}
		if (editingNew){
			((GuiButton) buttonList.get(4)).enabled = !textBeingEdited.getText().isEmpty();
			((GuiButton) buttonList.get(4)).displayString = "Commit";
		}
		if (editingExisting){
			((GuiButton) buttonList.get(0)).enabled = !textBeingEdited.getText().isEmpty();
			((GuiButton) buttonList.get(0)).displayString = "Commit";
		}
		if (locations.size() >= 100) ((GuiButton) buttonList.get(4)).enabled = false;
		else if (!editingNew) ((GuiButton) buttonList.get(4)).enabled = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui(){
		buttonList.clear();
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;

		//updateTeleporter();

		buttonList.add(new GuiButtonAHeight(0, posX + 112, posY + 45, 66, 12, "Rename"));
		buttonList.add(new GuiButtonAHeight(1, posX + 112, posY + 58, 66, 12, "Set Here"));
		buttonList.add(new GuiButtonAHeight(2, posX + 112, posY + 71, 66, 12, "Remove"));
		buttonList.add(new GuiButtonAHeight(3, posX + 112, posY + 99, 66, 12, "Teleport"));
		buttonList.add(new GuiButtonAHeight(4, posX + 112, posY + 112, 66, 12, "Add New"));
		buttonList.add(new GuiButtonAHeight(5, posX + 112, posY + 125, 66, 12, "Add Fuel"));
		buttonList.add(new GuiButtonAHeight(6, posX+xSize-63, posY-15, 60, 15, "Cancel"));
		((GuiButton) buttonList.get(6)).visible = false;

		textBeingEdited = new GuiTextField(fontRendererObj, posX+3, posY-14, xSize-67, 12);
		textBeingEdited.setTextColor(-1);
		textBeingEdited.setDisabledTextColour(-1);
		textBeingEdited.setEnableBackgroundDrawing(true);
		textBeingEdited.setMaxStringLength(40);
		textBeingEdited.setVisible(false);

		updateButtons();
	}

	@Override
	protected void actionPerformed(GuiButton button){
		if (button.id == 0 || button.id == 6 && !editingNew){
			if(button.id == 6){
				editingExisting = false;
				((GuiButton) buttonList.get(0)).displayString = "Rename";
				((GuiButton) buttonList.get(6)).visible = false;
				textBeingEdited.setVisible(false);
				((GuiButton) buttonList.get(0)).enabled = true;
				return;
			}
			if (!editingExisting){
				editingExisting = true;
				textBeingEdited.setVisible(true);
				textBeingEdited.setText(locations.get(selected+selrctionOffset).getName());
				textBeingEdited.setSelectionPos(0);
				textBeingEdited.setFocused(true);
				((GuiButton) buttonList.get(6)).visible = true;
			}else{
				if (!textBeingEdited.getText().isEmpty()){
					locations.get(selected+selrctionOffset).setName(textBeingEdited.getText());
					TeleportLocation location = new TeleportLocation();
					location.setName(textBeingEdited.getText());
					DraconicEvolution.network.sendToServer(new TeleporterPacket(location, TeleporterPacket.UPDATENAME, selected+selrctionOffset));
					((GuiButton) buttonList.get(0)).displayString = "Rename";
					editingExisting = false;
					textBeingEdited.setVisible(false);
					((GuiButton) buttonList.get(6)).visible = false;
				}
			}
		}

		if (button.id == 1){
			TeleportLocation location = new TeleportLocation(player.posX, player.posY, player.posZ, player.dimension, player.cameraPitch, player.cameraYaw, locations.get(selected+selrctionOffset).getName());
			DraconicEvolution.network.sendToServer(new TeleporterPacket(location, TeleporterPacket.UPDATEDESTINATION, selected+selrctionOffset));
			locations.set(selected + selrctionOffset, location);
		}

		if (button.id == 2) {
			DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.REMOVEDESTINATION, selected+selrctionOffset, false));
			locations.remove(selected+selrctionOffset);
			if (selrctionOffset > 0) selrctionOffset--;
			if (selected >= locations.size()) selected--;
		}

		if (button.id == 3){
			if (locations.isEmpty()) return;
			if (player.isRiding())
			{
				return;
			}

			if (!player.capabilities.isCreativeMode && fuel <= 0)
			{
				player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfFuel.txt"));
			}

			if (player.capabilities.isCreativeMode || fuel > 0) {
				fuel--;
				DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.TELEPORT, selected+selrctionOffset, false));
			}
		}

		if (button.id == 4 || button.id == 6 && !editingExisting){
			if(button.id == 6){
				editingNew = false;
				((GuiButton) buttonList.get(4)).displayString = "Add New";
				((GuiButton) buttonList.get(6)).visible = false;
				((GuiButton) buttonList.get(4)).enabled = true;
				textBeingEdited.setVisible(false);
				return;
			}
			if (!editingNew){
				editingNew = true;
				textBeingEdited.setVisible(true);
				textBeingEdited.setText(""+(int)player.posX+" "+(int)player.posY+" "+(int)player.posZ);
				textBeingEdited.setSelectionPos(0);
				textBeingEdited.setFocused(true);
				((GuiButton) buttonList.get(6)).visible = true;
			}else{
				if (!textBeingEdited.getText().isEmpty()){
					addCurrentLocationToList(textBeingEdited.getText());
					((GuiButton) buttonList.get(4)).displayString = "Add New";
					editingNew = false;
					textBeingEdited.setVisible(false);
					((GuiButton) buttonList.get(6)).visible = false;
				}
			}
		}

		if (button.id == 5)
		{
			if (player.inventory.hasItem(Items.ender_pearl))
			{
				if ((!Keyboard.isKeyDown(42)) && (!Keyboard.isKeyDown(54)))
				{
					DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.ADDFUEL, 1, false));
					this.fuel += ConfigHandler.teleporterUsesPerPearl;
				}else if (hasPearls(16))
				{
					DraconicEvolution.network.sendToServer(new TeleporterPacket(TeleporterPacket.ADDFUEL, 16, false));
					this.fuel += ConfigHandler.teleporterUsesPerPearl * 16;
				}else
					player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfPearls.txt"));
			}
			else
				player.addChatMessage(new ChatComponentTranslation("msg.teleporterOutOfPearls.txt"));
		}
		updateButtons();
	}
	
	public boolean hasPearls(int number){
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
	public void keyTyped(char key, int keyN){
		if(this.textBeingEdited.textboxKeyTyped(key, keyN)) {
			if (editingNew){
				((GuiButton) buttonList.get(4)).enabled = !textBeingEdited.getText().isEmpty();
				((GuiButton) buttonList.get(4)).displayString = "Commit";
			}
			if (editingExisting){
				((GuiButton) buttonList.get(0)).enabled = !textBeingEdited.getText().isEmpty();
				((GuiButton) buttonList.get(0)).displayString = "Commit";
			}
			return;
		}

		if (keyN == 28 && editingNew) actionPerformed((GuiButton)buttonList.get(4));
		if (keyN == 28 && editingExisting) actionPerformed((GuiButton)buttonList.get(0));

		if ((key == 'e' && (!editingExisting || !editingNew)) || key == '')
		{
			this.mc.displayGuiScreen(null);
			this.mc.setIngameFocus();
		}
	}

	@Override
	public void updateScreen(){
		if (player.getCurrentEquippedItem() == null || !player.getCurrentEquippedItem().isItemEqual(new ItemStack(ModItems.teleporterMKII)) || player.isDead)
		{
			this.mc.displayGuiScreen(null);
			this.mc.setIngameFocus();
		}


		tick++;
		if (tick >= 10){
			tick = 0;
			showFuelLight = !showFuelLight;
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	private void readDataFromItem(ItemStack teleporter){
		this.selected = ItemNBTHelper.getShort(teleporter, "Selection", (short) 0);
		this.selrctionOffset = ItemNBTHelper.getIntager(teleporter, "SelectionOffset", 0);
		this.fuel = ItemNBTHelper.getIntager(teleporter, "Fuel", 0);

		locations.clear();

		NBTTagCompound compound = teleporter.getTagCompound();
		if(compound == null || compound.getTagList("Locations", 0) == null) return;
		NBTTagList list = (NBTTagList)compound.getTag("Locations");
		if (list == null) list = new NBTTagList();

		for (int i = 0; i < list.tagCount(); i++){
			NBTTagCompound tagLocation = list.getCompoundTagAt(i);
			TeleportLocation location = new TeleportLocation(tagLocation.getDouble("X"), tagLocation.getDouble("Y"), tagLocation.getDouble("Z"), tagLocation.getInteger("Dimension"), tagLocation.getFloat("Pitch"), tagLocation.getFloat("Yaw"), tagLocation.getString("Name"));
			location.setWriteProtected(tagLocation.getBoolean("WP"));
			locations.add(location);
		}
	}

	private void addCurrentLocationToList(String name){
		TeleportLocation currentLocation = new TeleportLocation(player.posX, player.posY, player.posZ, player.dimension, player.rotationPitch, player.rotationYaw, name);
		DraconicEvolution.network.sendToServer(new TeleporterPacket(currentLocation, 0));
		locations.add(currentLocation);
	}

}
